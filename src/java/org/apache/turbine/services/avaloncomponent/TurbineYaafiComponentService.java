package org.apache.turbine.services.avaloncomponent;

/*
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Jdk14Logger;
import org.apache.avalon.framework.logger.Log4JLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.NullLogger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.yaafi.framework.container.ServiceContainer;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerConfiguration;
import org.apache.fulcrum.yaafi.framework.factory.ServiceContainerFactory;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.InstantiationException;
import org.apache.turbine.services.TurbineBaseService;

/**
 * An implementation of Turbine service initializing the YAAFI container
 *
 * @author <a href="mailto:siegfried.goescfl@it20one.at">Siegfried Goeschl</a>
 */
public class TurbineYaafiComponentService
        extends TurbineBaseService
        implements AvalonComponentService, Initializable, Disposable
{
    /** the logger to be used */
    private static Log log = LogFactory.getLog(AVALON_LOG_CATEGORY);

    /** property to lookup the container configuration file */
    public static final String CONTAINER_CONFIGURATION_KEY = "containerConfiguration";

    /** the default value for the container configuration file */
    public static final String CONTAINER_CONFIGURATION_VALUE = "/WEB-INF/conf/containerConfiguration.xml";

    /** property to lookup the properties file */
    public static final String COMPONENT_PARAMETERS_KEY = "parameters";

    /** the default value for the parameter file */
    public static final String COMPONENT_PARAMETERS_VALUE = "/WEB-INF/conf/parameters.properties";

    /** YAFFI container */
    private ServiceContainer container;
    
    // -------------------------------------------------------------
    // Service initialization
    // -------------------------------------------------------------

    public TurbineYaafiComponentService()
    {
        // nothing to do
    }

    /**
     * Load all configured components and initialize them. This is a zero parameter variant which
     * queries the Turbine Servlet for its config.
     *
     * @throws InitializationException Something went wrong in the init stage
     */
    public void init() throws InitializationException
    {
        try
        {
            log.info( "Initializing TurbineYaafiComponentService ..." );
            initialize();
            setInit(true);
        }
        catch (Exception e)
        {
            log.error("Exception caught initialising service: ", e);
            throw new InitializationException("Initializing TurbineYaafiComponentService failed", e);
        }
    }

    /**
     * Shuts the Component Service down, calls dispose on the components that implement this
     * interface
     *
     */
    public void shutdown()
    {
        log.info( "Disposing TurbineYaafiComponentService ..." );
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
        // get the configuration from the baseclass

        Configuration conf = this.getConfiguration();

        // determine the home directory

        String homePath = Turbine.getRealPath("/");
        log.info( "Using the following home : " + homePath );

        // create the configuration for YAAFI

        ServiceContainerConfiguration config = 
            this.createServiceContainerConfiguration(conf);

        config.setLogger( this.createAvalonLogger() );
        config.setApplicationRootDir( homePath );

        // initialize the container
        
        try
        {
            this.container = ServiceContainerFactory.create(
                config
                );
        }
        catch (Exception e)
        {
            String msg = "Initializing YAAFI failed";
            log.error(msg,e);
            throw e;
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
     * @param roleName Name of the role the component fills.
     * @return an instance of the named component
     * @throws Exception generic exception
     */
    public Object lookup(String roleName) throws ServiceException
    {
        return this.container.lookup(roleName);
    }

    /**
     * Releases the component.
     *
     * @param component the component to release
     */
    public void release(Object component)
    {
        this.container.release( component );
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#hasService(java.lang.String)
     */
    public boolean hasService(String roleName)
    {
        return this.container.hasService(roleName);
    }
    
    /**
     * Create a ServiceContainerConfiguration based on the Turbine configuration
     *
     * @param conf the Turbine configuration
     * @return the YAAFI configuration
     * @throws IOException creating the YAAFI configuration failed
     */
    protected ServiceContainerConfiguration createServiceContainerConfiguration( Configuration conf )
        throws IOException
    {
        ServiceContainerConfiguration result = new ServiceContainerConfiguration();

        // are we using a "containerConfiguration.xml" ?!

        if( conf.containsKey(CONTAINER_CONFIGURATION_KEY) )
        {
            // determine the container configuration file

            String containerConfiguration = conf.getString(
                CONTAINER_CONFIGURATION_KEY
                );

            result.loadContainerConfiguration(containerConfiguration);
        }
        else if( conf.containsKey(COMPONENT_ROLE_KEY) )
        {
            // determine the location of the role configuraton file

            String roleConfigurationFileName = conf.getString(
                COMPONENT_ROLE_KEY,
                COMPONENT_ROLE_VALUE
                );

            // determine the location of component configuration file

            String componentConfigurationFileName = conf.getString(
                COMPONENT_CONFIG_KEY,
                COMPONENT_CONFIG_VALUE
                );

            // determine the location of parameters file

            String parametersFileName = conf.getString(
                COMPONENT_PARAMETERS_KEY,
                COMPONENT_PARAMETERS_VALUE
                );

            result.setComponentRolesLocation( roleConfigurationFileName );
            result.setComponentConfigurationLocation( componentConfigurationFileName );
            result.setParametersLocation( parametersFileName );
        }
        else
        {
            // determine the container configuration file

            String containerConfiguration = conf.getString(
                CONTAINER_CONFIGURATION_KEY,
                CONTAINER_CONFIGURATION_VALUE
                );

            result.loadContainerConfiguration(containerConfiguration);
        }

        return result;
    }

    /**
     * Create the Avalon logger to be passed to YAAFI
     * @return an Avalon Logger
     */
    protected Logger createAvalonLogger()
    {
        Logger result = null;
        
        if( log instanceof org.apache.commons.logging.impl.Log4JLogger )
        {
            log.debug("Using an Avalon Log4JLogger");
            result = new Log4JLogger( 
                ((org.apache.commons.logging.impl.Log4JLogger) log).getLogger()
                );
        }
        else if( log instanceof org.apache.commons.logging.impl.Jdk14Logger )
        {
            log.debug("Using an Avalon Jdk14Logger");
            result = new Jdk14Logger( 
                ((org.apache.commons.logging.impl.Jdk14Logger) log).getLogger()
                );
        }
        else if( log instanceof org.apache.commons.logging.impl.NoOpLog )
        {
            log.debug("Using an Avalon NullLogger");
            result = new NullLogger();
        }
        else
        {
            log.debug("Using an Avalon ConsoleLogger");
            result = new ConsoleLogger();
        }
        
        return result;
    }
    
    // -------------------------------------------------------------
    // TurbineServiceProvider
    // -------------------------------------------------------------
    
    /**
     * @see org.apache.turbine.services.TurbineServiceProvider#exists(java.lang.String)
     */
    public boolean exists(String roleName)
    {
        return this.hasService(roleName);
    }
    
    /**
     * @see org.apache.turbine.services.TurbineServiceProvider#get(java.lang.String)
     */
    public Object get(String roleName) throws InstantiationException
    {
        try
        {
            return this.lookup(roleName);
        }
        catch (ServiceException e)
        {
            String msg = "Unable to get the following service : " + roleName;
            log.error(msg);
            throw new InstantiationException(msg);
        }
        catch (Throwable t)
        {
            String msg = "Unable to get the following service : " + roleName;
            log.error(msg,t);
            throw new InstantiationException(msg,t);
        }        
    }
}