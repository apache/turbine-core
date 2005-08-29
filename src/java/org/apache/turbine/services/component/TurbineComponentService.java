package org.apache.turbine.services.component;

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
 * @deprecated torque is now loaded using the AvalonComponentService
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

        log.warn("The ComponentService is deprecated!");

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
