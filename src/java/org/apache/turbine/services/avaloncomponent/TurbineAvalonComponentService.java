package org.apache.turbine.services.avaloncomponent;

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

import javax.servlet.ServletConfig;

import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.avaloncomponent.AvalonComponentService;
import org.apache.turbine.util.ServletUtils;
import org.apache.turbine.Turbine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.avalon.excalibur.component.ExcaliburComponentManager;
import org.apache.avalon.excalibur.component.DefaultRoleManager;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.log.Hierarchy;

/**
 * An implementation of AvalonComponentService which loads all the
 * avalon components given in the TurbineResources.properties File
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */

public class TurbineAvalonComponentService
        extends TurbineBaseService
        implements AvalonComponentService
{

    private ExcaliburComponentManager manager;

    /** Logging */
    private static Log log = LogFactory.getLog(TurbineAvalonComponentService.class);

    /** Extension used for Configuration files. */
    private static String CONFIG = "config";

    /** Name tag used in Configurations */
    private static String NAME = "name";

    /** Prefix used by the Component Loader */
    private static String COMPONENT = "avaloncomponent";

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

        ServletConfig config = Turbine.getTurbineServletConfig();

        manager = new ExcaliburComponentManager();
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

        log.debug("Building configuration object");
        Configuration sysConfig = null;
        Configuration roleConfig = null;
        try
        {
            String sysConfigFile =
                    ServletUtils.expandRelative(config, "/WEB-INF/conf/AvalonComponentManager.xml");
            log.debug("Loading component manager config from "+sysConfigFile);
            sysConfig = builder.buildFromFile(sysConfigFile);

            String roleConfigFile =
                    ServletUtils.expandRelative(config, "/WEB-INF/conf/AvalonComponentRoles.xml");
            log.debug("Loading component role config from "+roleConfigFile);
            roleConfig = builder.buildFromFile(roleConfigFile);
        }
        catch(Exception e)
        {
            throw new InitializationException("Could parse configurations files for ExcaliburComponementManeger!", e);
        }

        DefaultRoleManager roles = new DefaultRoleManager();

        manager.setLogger( Hierarchy.getDefaultHierarchy().getLoggerFor("manager") );
        manager.contextualize(new DefaultContext());

        log.debug("Processing configuration");
        try
        {
            roles.setLogger( Hierarchy.getDefaultHierarchy().getLoggerFor("manager.roles") );
            roles.configure(roleConfig);
            manager.setRoleManager(roles);
            manager.configure(sysConfig);
        }
        catch(ConfigurationException e)
        {
            throw new InitializationException("Could not configure ExcaliburComponentManager", e);
        }

        log.debug("Initializing manager");
        try
        {
            manager.initialize();
        }
        catch(Exception e)
        {
            throw new InitializationException("Could not initialize ExcaliburComponentManager", e);
        }

        setInit(true);

    }

    public Component getComponent( String roleName )
            throws ComponentException
    {
        return manager.lookup(roleName);
    }

    public void releaseComponent( Component component )
    {
        manager.release( component );
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
        manager.dispose();
        manager = null;
        setInit(false);
    }
}
