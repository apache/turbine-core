package org.apache.turbine.services.component;

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

import java.util.Iterator;
import javax.servlet.ServletConfig;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.stratum.component.ComponentLoader;
import org.apache.stratum.lifecycle.Disposable;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;

/**
 * An implementation of ComponentService which loads all the
 * components given in the TurbineResources.properties File
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class TurbineComponentService
        extends TurbineBaseService
        implements ComponentService
{

    /** Logging */
    private static Log log = LogFactory.getLog(TurbineComponentService.class);

    /** Extension used for Configuration files. */
    private static String CONFIG = "config";

    /** Name tag used in Configurations */
    private static String NAME = "name";

    /** Prefix used by the Component Loader */
    private static String COMPONENT = "component";

    /** List of Components that was initialized */
    private Object[] components = null;

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
        Configuration loaderConf = new BaseConfiguration();

        String[] names = getConfiguration().getStringArray(NAME);

        for (int i = 0; i < names.length; i++)
        {
            String key = names[i];

            loaderConf.addProperty(COMPONENT + "." + NAME, key);

            String subProperty = COMPONENT + "." + key;
            Configuration subConf = getConfiguration().subset(key);

            for (Iterator it = subConf.getKeys(); it.hasNext();)
            {
                String subKey = (String) it.next();
                Object subVal = subConf.getProperty(subKey);

                if (subKey.equals(CONFIG))
                {
                    log.debug("Fixing up " + subVal);
                    String newPath =
                            config.getServletContext().getRealPath((String) subVal);

                    if (newPath == null)
                    {
                      throw new InitializationException("Could not translate path " + subVal);
                    }

                    subVal = newPath;
                    log.debug("Now: " + subVal);
                }

                loaderConf.addProperty(subProperty + "." + subKey,
                        subVal);
            }

            log.info("Added " + key + " as a component");
        }

        try
        {
            ComponentLoader cl = new ComponentLoader(loaderConf);
            components = cl.load();
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
        if (components != null)
        {
            for (int i = 0; i < components.length; i++)
            {
                if (components[i] instanceof Disposable)
                {
                    log.debug("Disposing a " + components[i].getClass().getName() + " object");
                    ((Disposable) components[i]).dispose();
                }
                else
                {
                    log.debug("Not disposing " + components[i].getClass().getName() + ", not a Disposable Object");
                }
            }
        }
        setInit(false);
    }
}
