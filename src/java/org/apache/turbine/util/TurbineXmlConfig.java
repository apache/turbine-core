package org.apache.turbine.util;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import java.util.HashMap;
import java.util.Map;

/**
 * A class used for initialization of Turbine without a servlet container.
 * <p>
 * If you need to use Turbine outside of a servlet container, you can
 * use this class for initialization of the Turbine servlet.
 * <p>
 * <blockquote><code><pre>
 * TurbineXmlConfig config = new TurbineXmlConfig(".", "conf/TurbineResources.properties");
 * </pre></code></blockquote>
 * <p>
 * All paths referenced in TurbineResources.properties and the path to
 * the properties file itself (the second argument) will be resolved
 * relative to the directory given as the first argument of the constructor,
 * here - the directory where application was started. Don't worry about
 * discarding the references to objects created above. They are not needed,
 * once everything is initialized.
 * <p>
 * In order to initialize the Services Framework outside of the Turbine Servlet,
 * you need to call the <code>init()</code> method. By default, this will
 * initialize the Resource and Logging Services and any other services you
 * have defined in your TurbineResources.properties file.
 *
 * @todo Make this class enforce the lifecycle contracts
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class TurbineXmlConfig
        extends TurbineConfig
{
    /**
     * Constructs a new TurbineXmlConfig.
     *
     * This is the general form of the constructor. You can provide
     * a path to search for files, and a name-value map of init
     * parameters.
     *
     * <p> For the list of recognized init parameters, see
     * {@link org.apache.turbine.Turbine} class.
     *
     * @param path The web application root (i.e. the path for file lookup).
     * @param attributes Servlet container (or emulator) attributes.
     * @param initParams initialization parameters.
     */
    public TurbineXmlConfig(String path, Map attributes, Map initParams)
    {
        super(path, attributes, initParams);
    }

    /**
     * @see #TurbineXmlConfig(String path, Map attributes, Map initParams)
     */
    public TurbineXmlConfig(String path, Map initParams)
    {
        this(path, new HashMap(0), initParams);
    }

    /**
     * Constructs a TurbineXmlConfig.
     *
     * This is a specialized constructor that allows to configure
     * Turbine easiliy in the common setups.
     *
     * @param path The web application root (i.e. the path for file lookup).
     * @param configuration the relative path to TurbineResources.xml file
     */
    public TurbineXmlConfig(String path, String config)
    {
        this(path, new HashMap(1));
        initParams.put(CONFIGURATION_PATH_KEY, config);
    }
}
