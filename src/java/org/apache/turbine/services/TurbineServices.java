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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import javax.servlet.ServletConfig;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.turbine.services.logging.LoggingService;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.resources.TurbineResources;


/**
 * This is a singleton utility class that acts as a Services broker.
 *
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:burton@apache.org">Kevin Burton</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public class TurbineServices
    extends BaseServiceBroker
{
    /**
     * Servlet initialization parameter name for defining the resources
     * service implementation to use (<code>resources</code>).
     */
    public final static String RESOURCES_CLASS_KEY = "resources";

    /**
     * Default resources service implementation to use when none explicitly
     * specified (<code>
     * org.apache.turbine.services.resources.TurbineResourceService</code>)
     */
    public final static String RESOURCES_CLASS_DEFAULT =
        "org.apache.turbine.services.resources.TurbineResourceService";

    /** Default bootstrap logger implementation */
    public final static String LOGGING_CLASS_DEFAULT =
        "org.apache.turbine.services.logging.TurbineLoggingService";

    /**
     * Servlet initialization parameter name for defining the logging
     * service implementation to use.
     */
    public final static String LOGGING_CLASS_KEY="logging";

    /**
     * Servlet initialization parameter name for the path to
     * TurbineResources.properties file used by
     * {@link org.apache.turbine.services.resources.TurbineResourceService}
     * (<code>properties</code>).
     */
    public static final String PROPERTIES_PATH_KEY = "properties";

    /**
     * Default value of TurbineResources.properties file path
     * (<code>/WEB-INF/conf/TurbineResources.properties</code>).
     */
    public static final String PROPERTIES_PATH_DEFAULT =
        "/WEB-INF/conf/TurbineResources.properties";

    /**
     * A prefix for <code>Service</code> properties in
     * TurbineResource.properties.
     */
    public static final String SERVICE_PREFIX = "services.";

    /**
     * A <code>Service</code> property determining its implementing
     * class name .
     */
    public static final String CLASSNAME_SUFFIX = ".classname";

    /** The single instance of this class. */
    protected static ServiceBroker instance = new TurbineServices();

    /** True if logging should go throught LoggingService, false if not. */
    private boolean enabledLogging = false;

    /** caches log messages before logging is enabled */
    private Vector logCache = new Vector(5);

    /** the logger */
    private LoggingService logger;

    /**
     * This constructor is protected to force clients to use
     * getInstance() to access this class.
     */
    protected TurbineServices()
    {
        super();
    }

    /**
     * The method through which this class is accessed.
     *
     * @return The single instance of this class.
     */
    public static ServiceBroker getInstance()
    {
        return instance;
    }

    /**
     * Initialize the primary services (Logging and Resources).
     */
    public void initPrimaryServices(ServletConfig config)
        throws InstantiationException, InitializationException
    {
        // Resource service must start as the very first
        String resourcesClass = config.getInitParameter(RESOURCES_CLASS_KEY);

        try
        {
            if (resourcesClass == null)
            {
                resourcesClass = RESOURCES_CLASS_DEFAULT;
            }
            mapping.setProperty(ResourceService.SERVICE_NAME, resourcesClass);
            initService(ResourceService.SERVICE_NAME, config);

            // Now logging can be initialized
            String loggingClass = config.getInitParameter(LOGGING_CLASS_KEY);
            if (loggingClass == null)
            {
                loggingClass = LOGGING_CLASS_DEFAULT;
            }
            mapping.setProperty(LoggingService.SERVICE_NAME, loggingClass);
            try
            {
                initService(LoggingService.SERVICE_NAME, config);
                logger = getLogger();
            }
            catch (InitializationException e)
            {
                mapping.clearProperty(LoggingService.SERVICE_NAME);
                throw e;
            }
            catch (InstantiationException e)
            {
                mapping.clearProperty(LoggingService.SERVICE_NAME);
                throw e;
            }
        }
        finally
        {
            // All further messages will go through LoggingService
            // if logging service could not be initialized we still want
            // to enable logging for further messages to go to console
            enableLogging();
        }
        // Since we have ResourceService running, real mappings of services
        // may be loaded now
        initMapping();
    }

    /**
     * Creates mapping of Service names to class names.
     * BaseServiceBroker knows no mappings.
     */
    public void initMapping(Configuration mapping)
    {
        this.mapping = mapping;
    }

    /**
     * Creates a mapping between Service names and class names.
     *
     * The mapping is built according to settings present in
     * TurbineResources.properties.  The entries should have the
     * following form:
     *
     * <pre>
     * services.MyService.classname=com.mycompany.MyServiceImpl
     * services.MyOtherService.classname=com.mycompany.MyOtherServiceImpl
     * </pre>
     *
     * <br>
     *
     * Generic ServiceBroker provides no Services.
     */
    protected void initMapping()
    {
        int pref = SERVICE_PREFIX.length();
        int suff = CLASSNAME_SUFFIX.length();

        /*
         * These keys returned in an order that corresponds
         * to the order the services are listed in
         * the TR.props.
         *
         * When the mapping is created we use a Configuration
         * object to ensure that the we retain the order
         * in which the order the keys are returned.
         *
         * There's no point in retrieving an ordered set
         * of keys if they aren't kept in order :-)
         */
        Iterator keys = TurbineResources.getKeys();
        while(keys.hasNext())
        {
            String key = (String)keys.next();
            String [] keyParts = StringUtils.split(key, ".");
            if ((keyParts.length == 3) 
                && keyParts[0].equals(SERVICE_PREFIX) 
                && keyParts[2].equals(CLASSNAME_SUFFIX))
            {
                String serviceKey = keyParts[1];
                notice ("Added Mapping for Service: " + serviceKey);

                if (!mapping.containsKey(serviceKey))
                {
                    mapping.setProperty(serviceKey, TurbineResources.getString(key));
                }
            }
        }
    }

    /**
     * Returns the properites of a specific service.  Properties are
     * retrieved from TurbineResources.properties provided that you
     * have the following entries:
     *
     * <br>
     *
     * services.MyService.greeting=Hello\, I'm Jan B.
     *
     * <br>
     *
     * services.MyService.defaultAction=beep
     *
     * <br>
     *
     * Service "MyService" will get a set of two properites:
     * "greeting" = "Hello, I'm Jan B." and "defaultAction" = "beep".
     *
     * <p> Note that this way you will receive a 'collapsed' version
     * of your resources - multiple entries with the same key will
     * have only one value stored.  Use the {@link #getConfiguration}
     * or {@link #getResources} method to take advantage of the capabilities
     * of the {@link org.apache.turbine.services.resources.TurbineResources}
     * class.
     *
     * @param name The name of the service.
     * @return Properties of requested Service.
     */
    public Properties getProperties( String name )
    {
        Properties properties = new Properties();

        String servicePrefix = SERVICE_PREFIX + name + '.';
        Iterator keys = TurbineResources.getKeys(servicePrefix);

        while(keys.hasNext())
        {
            String key = (String)keys.next();
            String value;
            try
            {
                value = TurbineResources.getString(key);
            }
            catch (ClassCastException propIsArray)
            {
                String[] values = TurbineResources.getStringArray(key);
                value = values[values.length - 1];
            }
            properties.setProperty(key.substring(servicePrefix.length()),
                                   value);
        }

        return properties;
    }

    /**
     * Returns the Configuration for the specified service.
     *
     * @param name The name of the service.
     */
    public Configuration getConfiguration( String name )
    {
        return TurbineResources.getConfiguration(SERVICE_PREFIX + name);
    }

    /**
     * Returns the configuration resources of a specific service.
     *
     * This method extracts the configuration options of a service
     * from global Turbine configuration. The interface
     * {@link org.apache.turbine.services.resources.ResourceService}
     * offers significant advantages over plain java.util.Properties -
     * you can request for exaple you can retrieve <code>int</code>s
     * <code>boolean</code>s, and vectors of <code>String</code>s.
     *
     * <p> Note that the proces extracting the configuration might
     * be time consuming, it might be a good idea to store the
     * reference returned by this method in an instance variable
     * of the service.</p>
     *
     * @param name The name of the Service.
     * @return The configuration resources of the Service.
     */
    public ResourceService getResources( String name )
    {
        return TurbineResources.getResources(SERVICE_PREFIX + name);
    }

    /**
     * Output a diagnostic notice.
     *
     * This method is used by the service framework classes for producing
     * tracing mesages that might be useful for debugging.
     *
     * <p>Standard Turbine logging facilities are used.
     *
     * @param msg the message to print.
     */
    public void notice(String msg)
    {
        if (enabledLogging)
        {
            if (logger == null)
            {
                System.out.println("(!) NOTICE: " + msg);
            }
            else
            {
                logger.info(msg);
            }
        }
        else
        {
            // cache the message to log as soon as logging is on
            logCache.add(msg);
        }
    }

    /**
     * Output an error message.
     *
     * This method is used by the service framework classes for displaying
     * stacktraces of any exceptions that might be caught during processing.
     *
     * <p>Standard Turbine logging facilities are used.
     *
     * @param msg the message to print.
     */
    public void error(Throwable t)
    {
        if (enabledLogging)
        {
            if (logger == null)
            {
                System.out.println("(!) ERROR: " + t.getMessage());
            }
            else
            {
                logger.error("", t);
            }
        }
        else
        {
            // cache the message to log as soon as logging is on
            logCache.add("ERROR: " + t.getMessage());
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            logCache.add(sw.toString());
        }

    }

    /**
     * Allows logging using a logging service instead of console
     * This method should be called after initialization of the logging service
     */
    private void enableLogging()
    {
        enabledLogging = true;
        //log all cached log messages
        for (int i = 0; i < logCache.size(); i++)
        {
            String s = (String) logCache.elementAt(i);
            notice(s);
        }
        //dispose of the cache
        logCache = null;

        notice("ServiceBroker: LoggingService enabled.");

    }

    /**
     * Macro to reduce duplicated code and casting.
     */
    private final LoggingService getLogger()
    {
        return (LoggingService) getService(LoggingService.SERVICE_NAME);
    }
}
