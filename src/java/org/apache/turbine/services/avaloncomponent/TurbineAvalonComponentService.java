package org.apache.turbine.services.avaloncomponent;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.avalon.excalibur.component.DefaultRoleManager;
import org.apache.avalon.excalibur.component.ExcaliburComponentManager;
import org.apache.avalon.excalibur.logger.Log4JLoggerManager;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.Logger;

import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;

/**
 * An implementation of AvalonComponentService which loads all the
 * components given in the TurbineResources.properties File.
 * <p>
 * For component which require the location of the application or
 * context root, there are two ways to get it.
 * <ol>
 * <li>
 *   Implement the Contextualizable interface.  The full path to the
 *   correct OS directory can be found under the ComponentAppRoot key.
 * </li>
 * <li>
 *   The system property "applicationRoot" is also set to the full path
 *   of the correct OS directory.
 * </li>
 * </ol>
 * If you want to initialize Torque by using the AvalonComponentService, you
 * must activate Torque at initialization time by specifying
 *
 * services.AvalonComponentService.lookup = org.apache.torque.Torque 
 *
 * in your TurbineResources.properties.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TurbineAvalonComponentService
        extends TurbineBaseService
        implements AvalonComponentService, Initializable, Disposable
{
    /** Logging */
    private static Log log = LogFactory.getLog(
            TurbineAvalonComponentService.class);

    /** Component manager */
    private ExcaliburComponentManager manager = null;

    // -------------------------------------------------------------
    // Service initialization
    // -------------------------------------------------------------

    /**
     * Load all configured components and initialize them. This is
     * a zero parameter variant which queries the Turbine Servlet
     * for its config.
     *
     * @throws InitializationException Something went wrong in the init
     *         stage
     */
    public void init()
            throws InitializationException
    {
        try
        {
            initialize();

            setInit(true);
        }
        catch (Exception e)
        {
            throw new InitializationException("init failed", e);
        }
    }

    /**
     * Shuts the Component Service down, calls dispose on the components that
     * implement this interface
     *
     */
    public void shutdown()
    {
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
        org.apache.commons.configuration.Configuration conf 
                = getConfiguration();

        // get the filenames and expand them relative to webapp root
        String sysConfigFilename = Turbine.getRealPath(
                conf.getString(COMPONENT_CONFIG_KEY, COMPONENT_CONFIG_VALUE));
        String roleConfigFilename = Turbine.getRealPath(
                conf.getString(COMPONENT_ROLE_KEY, COMPONENT_ROLE_VALUE));

        log.debug("Config File: " + sysConfigFilename);
        log.debug("Role File:   " + roleConfigFilename);

        // process configuration files

        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration sysConfig  = builder.buildFromFile(sysConfigFilename);
        Configuration roleConfig = builder.buildFromFile(roleConfigFilename);

        // Create the LoggerManager for Log4J
        LoggerManager lm = new Log4JLoggerManager();

        // Setup the RoleManager
        DefaultRoleManager roles = new DefaultRoleManager();

        Logger logger = lm.getLoggerForCategory(AVALON_LOG_CATEGORY);

        roles.enableLogging(logger);
        roles.configure(roleConfig);

        // Setup ECM
        manager = new ExcaliburComponentManager();

        manager.setLoggerManager(lm);
        manager.enableLogging(logger);

        DefaultContext context = new DefaultContext();
        String realPath = Turbine.getRealPath("/");

        context.put(AvalonComponentService.COMPONENT_APP_ROOT, realPath);
        System.setProperty("applicationRoot", realPath);

        log.debug("Application Root is " + realPath);

        manager.contextualize(context);
        manager.setRoleManager(roles);
        manager.configure(sysConfig);

        // Init ECM!!!!
        manager.initialize();

        List lookupComponents = conf.getList(COMPONENT_LOOKUP_KEY,
                new Vector());
        
        for (Iterator it = lookupComponents.iterator(); it.hasNext();)
        {
            String component = (String) it.next();
            try
            {
                Component c = manager.lookup(component);
                log.info("Lookup for Component " + component + " successful");
                manager.release(c);
            }
            catch (Exception e)
            {
                log.error("Lookup for Component " + component + " failed!");
            }
        }
    }

    /**
     * Disposes of the container and releases resources
     */
    public void dispose()
    {
        manager.dispose();
    }

    /**
     * Returns an instance of the named component
     *
     * @param roleName Name of the role the component fills.
     * @return an instance of the named component
     * @throws ComponentException generic exception
     */
    public Component lookup(String roleName)
            throws ComponentException
    {
        return manager.lookup(roleName);
    }

    /**
     * Releases the component
     *
     * @param component the component to release
     */
    public void release(Component component)
    {
        manager.release(component);
    }

}
