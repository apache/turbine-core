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

import javax.servlet.ServletConfig;

import org.apache.avalon.excalibur.component.DefaultRoleManager;
import org.apache.avalon.excalibur.component.ExcaliburComponentManager;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.Turbine;

/**
 * An implementation of AvalonComponentService which loads all the
 * components given in the TurbineResources.properties File
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
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class TurbineAvalonComponentService
        extends TurbineBaseService
        implements AvalonComponentService, Initializable, Disposable
{
    /** Logging */
    private static Log log = LogFactory.getLog(TurbineAvalonComponentService.class);

    /** Component manager */
    private ExcaliburComponentManager manager = new ExcaliburComponentManager();

    // -------------------------------------------------------------
    // Service initilization
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
            log.error("Component Service failed: ", e);
            throw new InitializationException("ComponentService failed: ", e);
        }
    }

    /**
     * Inits the service using servlet parameters to obtain path to the
     * configuration file. Change relatives paths.
     *
     * @param config The ServletConfiguration from Turbine
     *
     * @throws InitializationException Something went wrong when starting up.
     * @deprecated use init() instead.
     */
    public void init(ServletConfig config)
            throws InitializationException
    {
        init();
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
        // get the filenames and expand them relative to webapp root
        String sysConfigFilename = Turbine.getRealPath(
                getConfiguration().getString(COMPONENT_CONFIG));
        String roleConfigFilename = Turbine.getRealPath(
                getConfiguration().getString(COMPONENT_ROLE));

        // process configuration files
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration sysConfig = builder.buildFromFile(sysConfigFilename);
        Configuration roleConfig = builder.buildFromFile(roleConfigFilename);

        // Create the LoggerManager for Log4J
        LoggerManager lm =
                new org.apache.avalon.excalibur.logger.Log4JLoggerManager();

        // Setup the RoleManager
        DefaultRoleManager roles = new DefaultRoleManager();
        roles.enableLogging(
                lm.getLoggerForCategory("org.apache.turbine.services"));
        roles.configure(roleConfig);

        // Setup ECM
        this.manager.setLoggerManager(lm);
        this.manager.enableLogging(
                lm.getLoggerForCategory("org.apache.turbine.services"));
        DefaultContext context = new DefaultContext();
        context.put(AvalonComponentService.COMPONENT_APP_ROOT, Turbine.getRealPath("/"));
        System.setProperty("applicationRoot", Turbine.getRealPath("/"));
        this.manager.contextualize(context);
        this.manager.setRoleManager(roles);
        this.manager.configure(sysConfig);

        // Init ECM!!!!
        this.manager.initialize();
    }

    /**
     * Disposes of the container and releases resources
     */
    public void dispose()
    {
        this.manager.dispose();
    }

    /**
     * Returns an instance of the named component
     *
     * @param roleName Name of the role the component fills.
     * @throws ComponentException generic exception
     */
    public Component lookup(String roleName)
            throws ComponentException
    {
        return this.manager.lookup(roleName);
    }

    /**
     * Releases the component
     *
     * @param component
     */
    public void release(Component component)
    {
        this.manager.release(component);
    }

}
