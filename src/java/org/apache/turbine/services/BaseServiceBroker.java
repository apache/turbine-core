package org.apache.turbine.services;

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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.turbine.util.StringUtils;


/**
 * A generic implementation of a <code>ServiceBroker</code>.
 *
 * Functionality that <code>ServiceBroker</code> provides in addition
 * to <code>InitableBroker</code> functionality includes:
 *
 * <ul>
 *
 * <li>Maintaining service name to class name mapping, allowing
 * plugable service implementations.</li>
 *
 * <li>Providing <code>Services</code> with <code>Properties</code>
 * based on system wide configuration mechanism.</li>
 *
 * </ul>
 *
 * @author <a href="mailto:burton@apache.org">Kevin Burton</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
public abstract class BaseServiceBroker
    extends BaseInitableBroker
    implements ServiceBroker
{
    /** Mapping of Service names to class names. */
    //protected Hashtable mapping = new Hashtable();
    protected Configuration mapping = (Configuration) new BaseConfiguration();

    /** A repository of Service instances. */
    protected Hashtable services = new Hashtable();

    /**
     * Default constructor of InitableBroker.
     *
     * This constructor does nothing.
     */
    protected BaseServiceBroker()
    {
    }

    /**
     * Determines whether a service is registered in the configured
     * <code>TurbineResources.properties</code>.
     *
     * @param serviceName The name of the service whose existance to check.
     * @return Registration predicate for the desired services.
     */
    public boolean isRegistered(String serviceName)
    {
        return (services.get(serviceName) != null);
    }

    /**
     * Performs early initialization of specified service.
     *
     * @param name The name of the service (generally the
     * <code>SERVICE_NAME</code> constant of the service's interface
     * definition).
     * @param data An object to use for initialization activities.
     * @exception InitializationException Initialization of this
     * service was not successful.
     */
    public void initService( String name, Object data )
        throws InitializationException
    {
        String className = mapping.getString(name);
        if (StringUtils.isEmpty(className))
        {
            throw new InitializationException(
                "ServiceBroker: initialization of unknown service " +
                name + " requested.");
        }
        initClass(className, data);
    }

    /**
     * Performs early initialization of all services.  Failed early
     * initialization of a Service may be non-fatal to the system,
     * thuss the exceptions are logged and then discarded.
     *
     * @param data An Object to use for initialization activities.
     */
    public void initServices( Object data )
    {
        try
        {
            initServices(data, false);
        }
        catch(InstantiationException notThrown)
        {
        }
        catch(InitializationException notThrown)
        {
        }
    }

    /**
     * Performs early initiailzation of all services. You can decide
     * to handle failed initizalizations if you wish, but then
     * after one service fails, the other will not have the chance
     * to initialize.
     *
     * @param data An Object to use for initialization activities.
     * @param report <code>true</code> if you want exceptions thrown.
     */
    public void initServices( Object data, boolean report )
        throws InstantiationException, InitializationException
    {
        notice("Initializing all services using: " +
                data.getClass().getName());
        Iterator names = mapping.getKeys();
        // throw exceptions
        if(report)
        {
            while(names.hasNext())
            {
                doInitService(data, (String)names.next());
            }
        }
        // eat exceptions
        else
        {
            while(names.hasNext())
            {
                try
                {
                    doInitService(data, (String)names.next());
                }
                // In case of an exception, file an error message; the
                // system may be still functional, though.
                catch(InstantiationException e)
                {
                    error(e);
                }
                catch(InitializationException e)
                {
                    error(e);
                }
            }
        }
        notice("Finished initializing all services!");
    }

    /**
     * Internal utility method for use in initServices()
     * to prevent duplication of code.
     */
    private void doInitService(Object data, String name)
        throws InstantiationException, InitializationException
    {
        notice("Start Initializing service (early): " + name);

        // Make sure the service has it's name and broker
        // reference set before initialization.
        getServiceInstance(name);

        // Perform early initialization.
        initClass(mapping.getString(name), data);

        notice("Finish Initializing service (early): " + name);
    }

    /**
     * Shuts down a <code>Service</code>.
     *
     * This method is used to release resources allocated by a
     * Service, and return it to its initial (uninitialized) state.
     *
     * @param name The name of the <code>Service</code> to be uninitialized.
     */
    public void shutdownService( String name )
    {
        String className = mapping.getString(name);
        if (className != null)
        {
            shutdownClass(className);
        }
    }

    /**
     * Shuts down all Turbine services, releasing allocated resources and
     * returning them to their initial (uninitialized) state.
     */
    public void shutdownServices( )
    {
        notice("Shutting down all services!");

        Iterator serviceNames = mapping.getKeys();
        String serviceName = null;

        /*
         * Now we want to reverse the order of
         * this list. This functionality should be added to
         * the ExtendedProperties in the commons but
         * this will fix the problem for now.
         */

        ArrayList reverseServicesList = new ArrayList();

        while (serviceNames.hasNext())
        {
            serviceName = (String)serviceNames.next();
            reverseServicesList.add(0, serviceName);
        }

        serviceNames = reverseServicesList.iterator();

        while (serviceNames.hasNext())
        {
            serviceName = (String)serviceNames.next();
            notice("Shutting down service: " + serviceName);
            shutdownService(serviceName);
        }
    }

    /**
     * Returns an instance of requested Service.
     *
     * @param name The name of the Service requested.
     * @return An instance of requested Service.
     * @exception InstantiationException, if the service is unknown or
     * can't be initialized.
     */
    public Service getService( String name )
        throws InstantiationException
    {
        Service service;
        try
        {
            service = getServiceInstance(name);
            if(!service.getInit())
            {
                synchronized(service.getClass())
                {
                    if(!service.getInit())
                    {
                        notice("Start Initializing service (late): " + name);
                        service.init();
                        notice("Finish Initializing service (late): " + name);
                    }
                }
            }
            if(!service.getInit())
            {
                // this exception will be caught & rethrown by this very method.
                // getInit() returning false indicates some initialization issue,
                // which in turn prevents the InitableBroker from passing a
                // reference to a working instance of the initable to the client.
                throw new InitializationException(
                    "init() failed to initialize service " + name);
            }
            return service;
        }
        catch( InitializationException e )
        {
            throw new InstantiationException("Service " + name +
                " failed to initialize", e);
        }
    }

    /**
     * Retrieves an instance of a Service without triggering late
     * initialization.
     *
     * Early initialization of a Service can require access to Service
     * properties.  The Service must have its name and serviceBroker
     * set by then.  Therefore, before calling
     * Initable.initClass(Object), the class must be instantiated with
     * InitableBroker.getInitableInstance(), and
     * Service.setServiceBroker() and Service.setName() must be
     * called.  This calls for two - level accessing the Services
     * instances.
     *
     * @param name The name of the service requested.
     * @exception InstantiationException, if the service is unknown or
     * can't be initialized.
     */
    protected Service getServiceInstance( String name )
        throws InstantiationException
    {
        Service service = (Service)services.get(name);

        if(service == null)
        {
            String className = mapping.getString(name);
            if(className == null)
            {
                throw new InstantiationException(
                    "ServiceBroker: unknown service " + name + " requested");
            }
            try
            {
                service = (Service)getInitableInstance(className);
            }
            catch(ClassCastException e)
            {
                throw new InstantiationException(
                    "ServiceBroker: class " + className +
                    " does not implement Service interface.", e);
            }
            catch(InstantiationException e)
            {
                throw new InstantiationException(
                    "Failed to instantiate service " + name, e);
            }
            service.setServiceBroker(this);
            service.setName(name);
            services.put(name, service);
        }

        return service;
    }

    /**
     * Returns the properites of a specific service.
     *
     * Generic ServiceBroker returns empty set of Properties.
     *
     * @param name The name of the service.
     * @return Properties of requested Service.
     */
    public Properties getProperties( String name )
    {
        return new Properties();
    }

    /**
     * Returns the Configuration of a specific service.
     *
     * Generic ServiceBroker returns empty Configuration
     *
     * @param name The name of the service.
     * @return Properties of requested Service.
     */
    public Configuration getConfiguration( String name )
    {
        return (Configuration) new BaseConfiguration();
    }
}
