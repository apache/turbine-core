package org.apache.turbine.services.avaloncomponent;

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

import java.io.File;
import java.io.IOException;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.logger.Log4JLogger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.commons.configuration.Configuration;
import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerConfiguration;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerFactory;
import org.apache.log4j.Logger;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.servlet.TurbineServlet;

/**
 * An implementation of Turbine service initializing the YAAFI container
 *
 * @author <a href="mailto:siegfried.goescfl@it20one.at">Siegfried Goeschl</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id$
 */
public class ACSYaafiComponentService extends TurbineBaseService implements
        AvalonComponentService, Initializable, Disposable
{
    /** The publically visible name of the service */
    public static final String SERVICE_NAME = "YaafiComponentService";

    /** property to lookup the container configuration file */
    public static final String CONTAINER_CONFIGURATION_KEY
            = "containerConfiguration";

    /** the default value for the container configuration file */
    public static final String CONTAINER_CONFIGURATION_VALUE
            = "/WEB-INF/conf/containerConfiguration.xml";

    /** property to lookup the properties file */
    public static final String COMPONENT_PARAMETERS_KEY = "parameters";

    /** the default value for the parameter file */
    public static final String COMPONENT_PARAMETERS_VALUE
            = "/WEB-INF/conf/parameters.properties";

    /** YAFFI container */
    private ServiceContainer container;

    /** our Log4J logger */
    private Logger logger;

    // -------------------------------------------------------------
    // Service initialization
    // -------------------------------------------------------------

    public ACSYaafiComponentService()
    {
        this.logger = Logger.getLogger(ACSYaafiComponentService.class);
    }

    /**
     * Load all configured components and initialize them. This is a zero
     * parameter variant which queries the Turbine Servlet for its config.
     *
     * @throws InitializationException Something went wrong in the init stage
     */
    public void init() throws InitializationException
    {
        try
        {
            this.logger.info("Initializing ACSYaafiComponentService ...");
            initialize();
            setInit(true);
        }
        catch (Exception e)
        {
            this.logger.error("Exception caught initializing service: ", e);
            throw new InitializationException(
                    "Initializing ACSYaafiComponentService failed", e);
        }
    }

    /**
     * Shuts the Component Service down, calls dispose on the components that
     * implement this interface
     */
    public void shutdown()
    {
        this.logger.info("Disposing ACSYaafiComponentService ...");
        dispose();
        setInit(false);
    }

    // -------------------------------------------------------------
    // Avalon lifecycle interfaces
    // -------------------------------------------------------------

    /**
     * Initializes the container
     *
     * @throws Exception generic exception
     */
    public void initialize() throws Exception
    {
        // get the configuration from the base class
        Configuration conf = this.getConfiguration();

        // determine the home directory
        String homePath = Turbine.getRealPath("/");
        if (homePath == null)
        {
            homePath = Turbine.getApplicationRoot();
        }
        File home = new File(homePath);

        this.logger.info("Using the following home : "
                + home.getAbsolutePath());

        // create the configuration for YAAFI
        ServiceContainerConfiguration config = this
                .createServiceContainerConfiguration(conf,
                        home.getAbsolutePath());

        try
        {
            this.container = ServiceContainerFactory.create(config);
        }
        catch (Throwable t)
        {
            this.logger.error("Initializing YAAFI failed", t);
        }
    }

    /**
     * Disposes of the container and releases resources
     */
    public void dispose()
    {
        if (this.container != null)
        {
            this.container.dispose();
            this.container = null;
        }
    }

    /**
     * Returns an instance of the named component
     *
     * @param path Name of the role the component fills.
     * @return an instance of the named component
     * @throws Exception generic exception
     */
    public Component lookup(String path) throws ComponentException
    {
        try
        {
            return (Component) this.container.lookup(path);
        }
        catch (ServiceException e)
        {
            throw new ComponentException(e.getKey(), e.getMessage(), e);
        }
    }

    /**
     * Releases the component
     *
     * @param component the component to release
     */
    public void release(Component component)
    {
        this.container.release(component);
    }

    /**
     * Create a ServiceContainerConfiguration based on the Turbine configuration
     * 
     * @param conf the Turbine configuration
     * @param appRoot the absolute path to the application root directory
     * @return the YAAFI configuration
     * @throws IOException creating the YAAFI configuration failed
     */
    protected ServiceContainerConfiguration createServiceContainerConfiguration(
            Configuration conf, String appRoot) throws IOException
    {
        ServiceContainerConfiguration result
                = new ServiceContainerConfiguration();

        result.setLogger(this.createAvalonLogger(AVALON_LOG_CATEGORY));
        result.setApplicationRootDir(appRoot);

        // are we using a "containerConfiguration.xml" ?!

        if (conf.containsKey(CONTAINER_CONFIGURATION_KEY))
        {
            // determine the container configuration file

            String containerConfiguration
                    = conf.getString(CONTAINER_CONFIGURATION_KEY);

            result.loadContainerConfiguration(containerConfiguration);
        }
        else if (conf.containsKey(COMPONENT_ROLE_KEY))
        {
            // determine the location of the role configuration file

            String roleConfigurationFileName
                    = conf.getString(COMPONENT_ROLE_KEY, COMPONENT_ROLE_VALUE);

            // determine the location of component configuration file

            String componentConfigurationFileName = conf.getString(
                    COMPONENT_CONFIG_KEY, COMPONENT_CONFIG_VALUE);

            // determine the location of parameters file

            String parametersFileName = conf.getString(
                    COMPONENT_PARAMETERS_KEY, COMPONENT_PARAMETERS_VALUE);

            result.setComponentRolesLocation(roleConfigurationFileName);
            result.setComponentConfigurationLocation(
                    componentConfigurationFileName);
            result.setParametersLocation(parametersFileName);
        }
        else
        {
            // determine the container configuration file

            String containerConfiguration = conf.getString(
                    CONTAINER_CONFIGURATION_KEY, CONTAINER_CONFIGURATION_VALUE);

            result.loadContainerConfiguration(containerConfiguration);
        }

        return result;
    }

    /**
     * Create the Avalon logger to be passed to YAAFI
     *
     * @param name the name of the logger
     * @return an Avalon Logger
     */
    protected org.apache.avalon.framework.logger.Logger createAvalonLogger(
            String name)
    {
        return new Log4JLogger(Logger.getLogger(name));
    }
}
