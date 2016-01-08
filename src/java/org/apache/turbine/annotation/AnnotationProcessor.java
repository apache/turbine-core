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


import java.lang.reflect.Field;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    /**
     * Search for annotated fields of the object and provide them with the
     * appropriate TurbineService
     *
     * @param object the object
     * @throws TurbineException if the service could not be injected
     */
    public static void process(Object object) throws TurbineException
    {
        ServiceManager manager = TurbineServices.getInstance();
        AssemblerBrokerService assembler = (AssemblerBrokerService)manager.getService(AssemblerBrokerService.SERVICE_NAME);
        Class<?> clazz = object.getClass();

        while (clazz != null)
        {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields)
            {
                if (field.isAnnotationPresent(TurbineService.class))
                {
                    injectTurbineService(object, manager, field);
                }
                else if (field.isAnnotationPresent(TurbineConfiguration.class))
                {
                    injectTurbineConfiguration(object, field);
                }
                else if (field.isAnnotationPresent(TurbineLoader.class))
                {
                    injectTurbineLoader(object, assembler, field);
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
     *
     * @throws TurbineException if loader cannot be set
     */
    private static void injectTurbineLoader(Object object, AssemblerBrokerService assembler, Field field) throws TurbineException
    {
        TurbineLoader la = field.getAnnotation(TurbineLoader.class);
        Loader<?> loader = assembler.getLoader(la.value());
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
     * @param field the field
     *
     * @throws TurbineException if configuration cannot be set
     */
    private static void injectTurbineConfiguration(Object object, Field field) throws TurbineException
    {
        TurbineConfiguration ca = field.getAnnotation(TurbineConfiguration.class);
        Configuration conf = null;

        // Check for annotation value
        if (StringUtils.isNotEmpty(ca.value()))
        {
            conf = Turbine.getConfiguration().subset(ca.value());
        }
        else
        {
            conf = Turbine.getConfiguration();
        }

        field.setAccessible(true);

        try
        {
            if (log.isDebugEnabled())
            {
                log.debug("Injection of " + conf + " into object " + object);
            }

            field.set(object, conf);
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
     *
     * @throws TurbineException if service is not available
     */
    private static void injectTurbineService(Object object, ServiceManager manager, Field field) throws TurbineException
    {
        TurbineService sa = field.getAnnotation(TurbineService.class);
        String serviceName = null;
        // Check for annotation value
        if (StringUtils.isNotEmpty(sa.value()))
        {
            serviceName = sa.value();
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
