package org.apache.turbine.services.factory;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and 
 *    "Apache Turbine" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. For 
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without 
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import org.apache.velocity.runtime.configuration.Configuration;

import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.pool.ObjectInputStreamForContext;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.resources.TurbineResources;

/**
 * The Factory Service instantiates objects using specified
 * class loaders. If none is specified, the default one
 * will be used.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @version $Id$
 */
public class TurbineFactoryService 
    extends TurbineBaseService
    implements FactoryService
{
    /**
     * The property specifying a set of additional class loaders.
     */
    public static final String CLASS_LOADERS = "class.loaders";

    /**
     * The property prefix specifying additional object factories.
     */
    public static final String OBJECT_FACTORY = "factory.";

    /**
     * Primitive classes for reflection of constructors.
     */
    private static HashMap primitiveClasses;

    {
        primitiveClasses = new HashMap(8);
        primitiveClasses.put(Boolean.TYPE.toString(),Boolean.TYPE);
        primitiveClasses.put(Character.TYPE.toString(),Character.TYPE);
        primitiveClasses.put(Byte.TYPE.toString(),Byte.TYPE);
        primitiveClasses.put(Short.TYPE.toString(),Short.TYPE);
        primitiveClasses.put(Integer.TYPE.toString(),Integer.TYPE);
        primitiveClasses.put(Long.TYPE.toString(),Long.TYPE);
        primitiveClasses.put(Float.TYPE.toString(),Float.TYPE);
        primitiveClasses.put(Double.TYPE.toString(),Double.TYPE);
    }

    /**
     * Additional class loaders.
     */
    private ArrayList classLoaders = new ArrayList();
    
    /**
     * Customized object factories.
     */
    private HashMap objectFactories = new HashMap();

    /**
     * Gets the class of a primitive type.
     *
     * @param type a primitive type.
     * @return the corresponding class, or null.
     */
    protected static Class getPrimitiveClass(String type)
    {
        return (Class) primitiveClasses.get(type);
    }

    /**
     * Constructs a Factory Service.
     */
    public TurbineFactoryService()
    {
    }

    /**
     * Initializes the service by loading default class loaders
     * and customized object factories.
     *
     * @throws InitializationException if initialization fails.
     */
    public void init() throws InitializationException
    {
        Configuration conf = getConfiguration();
        if (conf != null)
        {
            Vector loaders = conf.getVector(CLASS_LOADERS);
            if (loaders != null)
            {
                for (int i = 0; i < loaders.size(); i++)
                {
                    try
                    {
                        classLoaders.add(
                            loadClass((String) loaders.get(i)).newInstance());
                    }
                    catch (Exception x)
                    {
                        throw new InitializationException(
                            "No such class loader '" + 
                                (String) loaders.get(i) + 
                                    "' for TurbinbeFactoryService",x);
                    }
                }
            }
            
            String key,factory;
            for (Iterator i = conf.getKeys(OBJECT_FACTORY); i.hasNext();)
            {
                key = (String) i.next();
                factory = conf.getString(key);
                
                /*
                 * Store the factory to the table as a string and
                 * instantiate it by using the service when needed.
                 */
                objectFactories.put(
                    key.substring(OBJECT_FACTORY.length()),factory);
            }
        }
        setInit(true);
    }

    /**
     * Gets an instance of a named class.
     *
     * @param className the name of the class.
     * @return the instance.
     * @throws TurbineException if instantiation fails.
     */
    public Object getInstance(String className)
        throws TurbineException
    {
        if (className == null)
        {
            throw new TurbineException(
                new NullPointerException("String className"));
        }

        Factory factory = getFactory(className);
        if (factory == null)
        {
            Class clazz;
            try
            {
                clazz = loadClass(className);
            }
            catch (ClassNotFoundException x)
            {
                throw new TurbineException(
                    "Instantiation failed for class " + className,x);
            }
            return getInstance(clazz);
        }
        else
        {
            return factory.getInstance();
        }
    }

    /**
     * Gets an instance of a named class using a specified class loader.
     * 
     * <p>Class loaders are supported only if the isLoaderSupported
     * method returns true. Otherwise the loader parameter is ignored. 
     * 
     * @param className the name of the class.
     * @param loader the class loader.
     * @return the instance.
     * @throws TurbineException if instantiation fails.
     */
    public Object getInstance(String className,
                              ClassLoader loader)
        throws TurbineException
    {
        if (className == null)
        {
            throw new TurbineException(
                new NullPointerException("String className"));
        }

        Factory factory = getFactory(className);
        if (factory == null)
        {
            if (loader != null)
            {
                Class clazz;
                try
                {
                    clazz = loadClass(className,loader);
                }
                catch (ClassNotFoundException x)
                {
                    throw new TurbineException(
                        "Instantiation failed for class " + className,x);
                }
                return getInstance(clazz);
            }
            else
            {
                return getInstance(className);
            }
        }
        else
        {
            return factory.getInstance(loader);
        }
    }

    /**
     * Gets an instance of a named class.
     * Parameters for its constructor are given as an array of objects,
     * primitive types must be wrapped with a corresponding class.
     *
     * @param className the name of the class.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws TurbineException if instantiation fails.
     */
    public Object getInstance(String className,
                              Object[] params,
                              String[] signature)
        throws TurbineException
    {
        if (className == null)
        {
            throw new TurbineException(
                new NullPointerException("String className"));
        }

        Factory factory = getFactory(className);
        if (factory == null)
        {
            Class clazz;
            try
            {
                clazz = loadClass(className);
            }
            catch (ClassNotFoundException x)
            {
                throw new TurbineException(
                    "Instantiation failed for class " + className,x);
            }
            return getInstance(clazz,params,signature);
        }
        else
        {
            return factory.getInstance(params,signature);
        }
    }

    /**
     * Gets an instance of a named class using a specified class loader.
     * Parameters for its constructor are given as an array of objects,
     * primitive types must be wrapped with a corresponding class.
     *
     * <p>Class loaders are supported only if the isLoaderSupported
     * method returns true. Otherwise the loader parameter is ignored. 
     * 
     * @param className the name of the class.
     * @param loader the class loader.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws TurbineException if instantiation fails.
     */
    public Object getInstance(String className,
                              ClassLoader loader,
                              Object[] params,
                              String[] signature)
        throws TurbineException
    {
        if (className == null)
        {
            throw new TurbineException(
                new NullPointerException("String className"));
        }

        Factory factory = getFactory(className);
        if (factory == null)
        {
            if (loader != null)
            {
                Class clazz;
                try
                {
                    clazz = loadClass(className,loader);
                }
                catch (ClassNotFoundException x)
                {
                    throw new TurbineException(
                        "Instantiation failed for class " + className,x);
                }
                return getInstance(clazz,params,signature);
            }
            else
            {
                return getInstance(className,params,signature);
            }
        }
        else
        {
            return factory.getInstance(loader,params,signature);
        }
    }

    /**
     * Tests if specified class loaders are supported for a named class.
     * 
     * @param className the name of the class.
     * @return true if class loaders are supported, false otherwise. 
     * @throws TurbineException if test fails.
     */
    public boolean isLoaderSupported(String className)
        throws TurbineException
    {
        Factory factory = getFactory(className);
        return factory != null ? 
            factory.isLoaderSupported() : true;
    }

    /**
     * Gets an instance of a specified class.
     *
     * @param clazz the class.
     * @return the instance.
     * @throws TurbineException if instantiation fails.
     */
    protected Object getInstance(Class clazz)
        throws TurbineException
    {
        try
        {
            return clazz.newInstance();
        }
        catch (Exception x)
        {
            throw new TurbineException(
                "Instantiation failed for " + clazz.getName(),x);
        }
    }

    /**
     * Gets an instance of a specified class.
     * Parameters for its constructor are given as an array of objects,
     * primitive types must be wrapped with a corresponding class.
     *
     * @param clazz the class.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws TurbineException if instantiation fails.
     */
    protected Object getInstance(Class clazz,
                                 Object params[],
                                 String signature[])
        throws TurbineException
    {
        /* Try to construct. */
        try
        {
            Class[] sign = getSignature(clazz,params,signature);
            return clazz.getConstructor(sign).newInstance(params);
        }
        catch (Exception x)
        {
            throw new TurbineException(
                "Instantiation failed for " + clazz.getName(),x);
        }
    }

    /**
     * Gets the signature classes for parameters of a method of a class.
     *
     * @param clazz the class.
     * @param params an array containing the parameters of the method.
     * @param signature an array containing the signature of the method.
     * @return an array of signature classes. Note that in some cases
     * objects in the parameter array can be switched to the context
     * of a different class loader.
     * @throws ClassNotFoundException if any of the classes is not found.
     */
    protected Class[] getSignature(Class clazz,
                                   Object params[],
                                   String signature[])
        throws ClassNotFoundException
    {
        if (signature != null)
        {
            /* We have parameters. */
            ClassLoader tempLoader;
            ClassLoader loader = clazz.getClassLoader();
            Class[] sign = new Class[signature.length];
            for (int i= 0; i < signature.length; i++)
            {
                /* Check primitive types. */
                sign[i] = getPrimitiveClass(signature[i]);
                if (sign[i] == null)
                {
                    /* Not a primitive one, continue building. */
                    if (loader != null)
                    {
                        /* Use the class loader of the target object. */
                        sign[i] = loader.loadClass(signature[i]);
                        tempLoader = sign[i].getClassLoader();
                        if ((params[i] != null) &&
                            (tempLoader != null) &&
                            !tempLoader.equals(params[i].getClass().getClassLoader()))
                        {
                            /*
                             * The class uses a different class loader, 
                             * switch the parameter.
                             */
                            params[i] = switchObjectContext(params[i],loader);
                        }
                    }
                    else
                    {
                        /* Use the default class loader. */
                        sign[i] = loadClass(signature[i]);
                    }
                }
            }
            return sign;
        }
        else
        {
            return null;
        }
    }

    /**
     * Switches an object into the context of a different class loader.
     *
     * @param object an object to switch.
     * @param loader the loader of the new context.
     */
    protected Object switchObjectContext(Object object,
                                         ClassLoader loader)
    {
        ByteArrayOutputStream bout =
            new ByteArrayOutputStream();
        try
        {
            ObjectOutputStream out =
                new ObjectOutputStream(bout);
            out.writeObject(object);
            out.flush();
        }
        catch (Exception x)
        {
            return object;
        }

        try
        {
            ByteArrayInputStream bin =
                new ByteArrayInputStream(bout.toByteArray());
            ObjectInputStreamForContext in =
                new ObjectInputStreamForContext(bin,loader);

            return in.readObject();
        }
        catch (Exception x)
        {
            return object;
        }
    }

    /**
     * Loads the named class using the default class loader.
     *
     * @param className the name of the class to load.
     * @return the loaded class.
     * @throws ClassNotFoundException if the class was not found.
     */
    protected Class loadClass(String className)
        throws ClassNotFoundException
    {
        ClassLoader loader = this.getClass().getClassLoader();
        try
        {
            return loader != null ?
                loader.loadClass(className) : Class.forName(className);
        }
        catch (ClassNotFoundException x)
        {
            /* Go through additional loaders. */
            for (Iterator i = classLoaders.iterator(); i.hasNext();)
            {
                  try
                  {
                      return ((ClassLoader) i.next()).loadClass(className);
                  }
                  catch (ClassNotFoundException xx) { }
            }

            /* Give up. */
            throw x;
        }
    }

    /**
     * Loads the named class using a specified class loader.
     *
     * @param className the name of the class to load.
     * @param loader the loader to use.
     * @return the loaded class.
     * @throws ClassNotFoundException if the class was not found.
     */
    protected Class loadClass(String className,
                              ClassLoader loader)
        throws ClassNotFoundException
    {
        return loader != null ?
            loader.loadClass(className) : loadClass(className);
    }
    
    /**
     * Gets a customized factory for a named class.
     * 
     * @param className the name of the class to load.
     * @return the factory or null if not specified.
     * @throws TurbineException if instantiation of the factory fails.
     */
    protected Factory getFactory(String className)
        throws TurbineException
    {
        HashMap factories = objectFactories;
        Object factory = factories.get(className);
        if (factory != null)
        {
            if (factory instanceof String)
            {
                /* Not yet instantiated... */
                try
                {
                    factory = (Factory) getInstance((String) factory);
                    ((Factory) factory).init(className);
                }
                catch (TurbineException x)
                {
                    throw x;
                }
                catch (ClassCastException x)
                {
                    throw new TurbineException(
                        "Incorrect factory " + (String) factory + 
                            " for class " + className,x);
                }
                factories = (HashMap) factories.clone();
                factories.put(className,factory);
                objectFactories = factories;
            }
            return (Factory) factory;
        }
        else
        {
            return null;
        }
    }
}
