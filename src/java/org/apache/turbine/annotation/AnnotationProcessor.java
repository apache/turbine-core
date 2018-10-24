package org.apache.turbine.annotation;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.model.turbine.TurbineAccessControlList;
import org.apache.turbine.Turbine;
import org.apache.turbine.modules.Loader;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.assemblerbroker.AssemblerBrokerService;
import org.apache.turbine.util.TurbineException;

/**
 * AnnotationProcessor contains static helper methods that handle the
 * Turbine annotations for objects
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: TurbineAssemblerBrokerService.java 1521103 2013-09-09 13:38:07Z tv $
 */
public class AnnotationProcessor
{
    /** Logging */
    private static Log log = LogFactory.getLog(AnnotationProcessor.class);

    /** Annotation cache */
    private static ConcurrentMap<String, Annotation[]> annotationCache = new ConcurrentHashMap<String, Annotation[]>();

    /**
     * Get cached annotations for field, class or method
     *
     * @param object a field, class or method
     *
     * @return the declared annotations for the object
     */
    public static Annotation[] getAnnotations(AccessibleObject object)
    {
        String key = object.getClass() + object.toString();
        Annotation[] annotations = annotationCache.get(key);
        if (annotations == null)
        {
            Annotation[] newAnnotations = object.getDeclaredAnnotations();
            annotations = annotationCache.putIfAbsent(key, newAnnotations);
            if (annotations == null)
            {
                annotations = newAnnotations;
            }
        }
        return annotations;
    }
    
    public enum ConditionType
    {
        COMPOUND, ANY; 
    }
    
    /**
     * Check if the object given is authorized to be executed based on its annotations
     * 
     * The method will return false if one of the annotations denies execution
     * 
     * @see #isAuthorized(AccessibleObject, TurbineAccessControlList, ConditionType)
     * 
     * @param object
     * @param acl
     * @return true if the execution is allowed
     */
    public static <A extends TurbineAccessControlList> boolean isAuthorized(AccessibleObject object, A acl)
    {
        return isAuthorized( object, acl, ConditionType.COMPOUND );
    }

    /**
     * Check if the object given is authorized to be executed based on its annotations
     * The method's return value depends on the conditonType, cft. {@link RoleConditionType}.
     *
     * @param object the object
     * @param acl
     * @param conditonType either {@link RoleConditionType#COMPOUND}: The method will return false if one of the annotations denies execution
     *                     or {@link RoleConditionType#SINGLE} : The method will return true if one of the annotations allows execution
     * @return true if the execution is allowed
     */
    public static <A extends TurbineAccessControlList> boolean isAuthorized(AccessibleObject object, A acl, ConditionType conditonType)
    {
        Annotation[] annotations = getAnnotations(object);

        for (Annotation annotation : annotations)
        {
            if (annotation instanceof TurbineRequiredRole)
            {
                TurbineRequiredRole trr = (TurbineRequiredRole) annotation;
                String[] roleNames = trr.value();
                String group = trr.group();
                
                if (StringUtils.isEmpty(group)) // global group
                {
                    for (String roleName : roleNames)
                    {
                        switch ( conditonType ) {
                            case COMPOUND: default:
                                if (!acl.hasRole(roleName))
                                {
                                    return false;
                                }
                                break;
                            case ANY:
                                if (acl.hasRole(roleName))
                                {
                                    return true;
                                }
                                break;
                        }
                    }
                    if (conditonType == ConditionType.ANY) { // nothing matched
                        return false;
                    }
                }
                else
                {
                    for (String roleName : roleNames)
                    {
                        switch ( conditonType ) {
                            case COMPOUND: default:
                                if (!acl.hasRole(roleName, group))
                                {
                                    return false;
                                }
                                break;
                            case ANY:
                                if (acl.hasRole(roleName, group))
                                {
                                    return true;
                                }
                                break;
                        }
                    }
                }
            }
            else if (annotation instanceof TurbineRequiredPermission)
            {
                TurbineRequiredPermission trp = (TurbineRequiredPermission) annotation;
                String[] permissionNames = trp.value();
                String group = trp.group();
                
                if (StringUtils.isEmpty(group)) // global group
                {
                    for (String permissionName : permissionNames)
                    {
                        switch ( conditonType ) {
                            case COMPOUND: default:
                                if (!acl.hasPermission(permissionName))
                                {
                                    return false;
                                }
                                break;
                            case ANY:
                                if (acl.hasPermission(permissionName))
                                {
                                    return true;
                                }
                                break;
                        }
                    }
                }
                else
                {
                    for (String permissionName : permissionNames)
                    {
                        switch ( conditonType ) {
                            case COMPOUND: default:
                                if (!acl.hasPermission(permissionName, group))
                                {
                                    return false;
                                }
                                break;
                            case ANY:
                                if (acl.hasPermission(permissionName, group))
                                {
                                    return true;
                                }
                                break;
                        }

                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * Search for annotated fields of the object and inject the appropriate
     * objects
     *
     * @param object the object
     * @throws TurbineException if the objects could not be injected
     */
    public static void process(Object object) throws TurbineException
    {
        ServiceManager manager = null;
        Configuration config = null;
        AssemblerBrokerService assembler = null;
        Class<?> clazz = object.getClass();

        while (clazz != null)
        {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields)
            {
                Annotation[] annotations = getAnnotations(field);

                for (Annotation a : annotations)
                {
                    if (a instanceof TurbineService)
                    {
                        if (manager == null)
                        {
                            manager = TurbineServices.getInstance();
                        }
                        injectTurbineService(object, manager, field, (TurbineService) a);
                    }
                    else if (a instanceof TurbineConfiguration)
                    {
                        if (config == null)
                        {
                            config = Turbine.getConfiguration();
                        }
                        injectTurbineConfiguration(object, config, field, (TurbineConfiguration) a);
                    }
                    else if (a instanceof TurbineLoader)
                    {
                        if (assembler == null)
                        {
                            assembler = (AssemblerBrokerService) TurbineServices.getInstance().
                                getService(AssemblerBrokerService.SERVICE_NAME);
                        }
                        injectTurbineLoader(object, assembler, field, (TurbineLoader) a);
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Inject Turbine configuration into field of object
     *
     * @param object the object to process
     * @param assembler AssemblerBrokerService, provides the loader
     * @param field the field
     * @param annotation the value of the annotation
     *
     * @throws TurbineException if loader cannot be set
     */
    private static void injectTurbineLoader(Object object, AssemblerBrokerService assembler, Field field, TurbineLoader annotation) throws TurbineException
    {
        Loader<?> loader = assembler.getLoader(annotation.value());
        field.setAccessible(true);

        try
        {
            if (log.isDebugEnabled())
            {
                log.debug("Injection of " + loader + " into object " + object);
            }

            field.set(object, loader);
        }
        catch (IllegalArgumentException e)
        {
            throw new TurbineException("Could not inject loader "
                    + loader + " into object " + object, e);
        }
        catch (IllegalAccessException e)
        {
            throw new TurbineException("Could not inject loader "
                    + loader + " into object " + object, e);
        }
    }

    /**
     * Inject Turbine configuration into field of object
     *
     * @param object the object to process
     * @param conf the configuration to use
     * @param field the field
     * @param annotation the value of the annotation
     *
     * @throws TurbineException if configuration cannot be set
     */
    private static void injectTurbineConfiguration(Object object, Configuration conf, Field field, TurbineConfiguration annotation) throws TurbineException
    {
        Class<?> type = field.getType();
        String key = annotation.value();

        try
        {
            if (Configuration.class.isAssignableFrom(type))
            {
                // Check for annotation value
                if (StringUtils.isNotEmpty(key))
                {
                    conf = conf.subset(key);
                }

                if (log.isDebugEnabled())
                {
                    log.debug("Injection of " + conf + " into object " + object);
                }

                field.setAccessible(true);
                field.set(object, conf);
            }
            else if (conf.containsKey(key))
            {
                if ( String.class.isAssignableFrom( type ) )
                {
                    String value = conf.getString(key);
                    if (log.isDebugEnabled())
                    {
                        log.debug("Injection of " + value + " into object " + object);
                    }

                    field.setAccessible(true);
                    field.set(object, value);
                }
                else if ( Boolean.TYPE.isAssignableFrom( type ) )
                {
                    boolean value = conf.getBoolean(key);
                    if (log.isDebugEnabled())
                    {
                        log.debug("Injection of " + value + " into object " + object);
                    }

                    field.setAccessible(true);
                    field.setBoolean(object, value);
                }
                else if ( Integer.TYPE.isAssignableFrom( type ) )
                {
                    int value = conf.getInt(key);
                    if (log.isDebugEnabled())
                    {
                        log.debug("Injection of " + value + " into object " + object);
                    }

                    field.setAccessible(true);
                    field.setInt(object, value);
                }
                else if ( Long.TYPE.isAssignableFrom( type ) )
                {
                    long value = conf.getLong(key);
                    if (log.isDebugEnabled())
                    {
                        log.debug("Injection of " + value + " into object " + object);
                    }

                    field.setAccessible(true);
                    field.setLong(object, value);
                }
                else if ( Short.TYPE.isAssignableFrom( type ) )
                {
                    short value = conf.getShort(key);
                    if (log.isDebugEnabled())
                    {
                        log.debug("Injection of " + value + " into object " + object);
                    }

                    field.setAccessible(true);
                    field.setShort(object, value);
                }
                else if ( Long.TYPE.isAssignableFrom( type ) )
                {
                    long value = conf.getLong(key);
                    if (log.isDebugEnabled())
                    {
                        log.debug("Injection of " + value + " into object " + object);
                    }

                    field.setAccessible(true);
                    field.setLong(object, value);
                }
                else if ( Float.TYPE.isAssignableFrom( type ) )
                {
                    float value = conf.getFloat(key);
                    if (log.isDebugEnabled())
                    {
                        log.debug("Injection of " + value + " into object " + object);
                    }

                    field.setAccessible(true);
                    field.setFloat(object, value);
                }
                else if ( Double.TYPE.isAssignableFrom( type ) )
                {
                    double value = conf.getDouble(key);
                    if (log.isDebugEnabled())
                    {
                        log.debug("Injection of " + value + " into object " + object);
                    }

                    field.setAccessible(true);
                    field.setDouble(object, value);
                }
                else if ( Byte.TYPE.isAssignableFrom( type ) )
                {
                    byte value = conf.getByte(key);
                    if (log.isDebugEnabled())
                    {
                        log.debug("Injection of " + value + " into object " + object);
                    }

                    field.setAccessible(true);
                    field.setByte(object, value);
                }
                else if ( List.class.isAssignableFrom( type ) )
                {
                    List<Object> values = conf.getList(key);
                    if (log.isDebugEnabled())
                    {
                        log.debug("Injection of " + values + " into object " + object);
                    }

                    field.setAccessible(true);
                    field.set(object, values);
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            throw new TurbineException("Could not inject configuration "
                    + conf + " into object " + object, e);
        }
        catch (IllegalAccessException e)
        {
            throw new TurbineException("Could not inject configuration "
                    + conf + " into object " + object, e);
        }
    }

    /**
     * Inject Turbine service into field of object
     *
     * @param object the object to process
     * @param manager the service manager
     * @param field the field
     * @param annotation the value of the annotation
     *
     * @throws TurbineException if service is not available
     */
    private static void injectTurbineService(Object object, ServiceManager manager, Field field, TurbineService annotation) throws TurbineException
    {
        String serviceName = null;
        // Check for annotation value
        if (StringUtils.isNotEmpty(annotation.value()))
        {
            serviceName = annotation.value();
        }
        // Check for fields SERVICE_NAME and ROLE
        else
        {
            Field[] typeFields = field.getType().getFields();
            for (Field f : typeFields)
            {
                if (TurbineService.SERVICE_NAME.equals(f.getName()))
                {
                    try
                    {
                        serviceName = (String)f.get(null);
                    }
                    catch (Exception e)
                    {
                        continue;
                    }
                    break;
                }
                else if (TurbineService.ROLE.equals(f.getName()))
                {
                    try
                    {
                        serviceName = (String)f.get(null);
                    }
                    catch (Exception e)
                    {
                        continue;
                    }
                    break;
                }
            }
        }

        if (StringUtils.isEmpty(serviceName))
        {
            // Try interface class name
            serviceName = field.getType().getName();
        }

        if (log.isDebugEnabled())
        {
            log.debug("Looking up service for injection: " + serviceName + " for object " + object);
        }

        Object service = manager.getService(serviceName); // throws Exception on unknown service
        field.setAccessible(true);

        try
        {
            if (log.isDebugEnabled())
            {
                log.debug("Injection of " + serviceName + " into object " + object);
            }

            field.set(object, service);
        }
        catch (IllegalArgumentException e)
        {
            throw new TurbineException("Could not inject service "
                    + serviceName + " into object " + object, e);
        }
        catch (IllegalAccessException e)
        {
            throw new TurbineException("Could not inject service "
                    + serviceName + " into object " + object, e);
        }
    }
}
