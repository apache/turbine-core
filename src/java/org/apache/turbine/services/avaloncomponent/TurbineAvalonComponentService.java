package org.apache.turbine.services.avaloncomponent;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import java.util.Iterator;
import java.util.Vector;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.Turbine;

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

        Vector lookupComponents = conf.getVector(COMPONENT_LOOKUP_KEY,
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
