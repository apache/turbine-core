package org.apache.turbine.services.systemproperties;


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
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;

/**
 * The SystemPropertiesService provides a convenient way of getting properties 
 * defined in TurbineResources.properties into System.properties.
 *
 * <p>Properties defined as:<br/ >
 *     <code>services.SystemPropertiesService.name = value</code><br/ >
 * will be added to System.properties as<br/ >
 *     <code>name=value</code><br/ >
 * Suggested use is to configure mail.host for JavaMail thus:<br/ >
 *     <code>services.SystemPropertiesService.mail.host = localhost</code>
 *
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id$
 */
public class TurbineSystemPropertiesService
        extends TurbineBaseService
        implements SystemPropertiesService
{
    private static Log log 
            = LogFactory.getLog(TurbineSystemPropertiesService.class);

    // ---- Service initilization ------------------------------------------

    /**
     * Initializes the service.
     */
    public void init() throws InitializationException
    {
        Configuration conf = getConfiguration();
        Properties systemProperties = System.getProperties();
        for (Iterator iter = conf.getKeys(); iter.hasNext();)
        {
            String key = (String) iter.next();
            if (key.equals("classname") || key.equals("earlyInit"))
            {
                continue;
            }
            if (log.isDebugEnabled())
            {
                log.debug("Setting System property \"" + key + "\" to \"" 
                        + conf.getString(key) + "\"");
            }
            systemProperties.setProperty(key, conf.getString(key));
        }
        
        setInit(true);
    }

}
