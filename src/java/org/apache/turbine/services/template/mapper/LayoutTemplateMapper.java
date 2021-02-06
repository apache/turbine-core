package org.apache.turbine.services.template.mapper;


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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.template.TemplateEngineService;
import org.apache.turbine.services.template.TemplateService;

/**
 * This mapper is responsible for the lookup of templates for the Layout
 * It tries to look in various packages for a match:
 *
 * 1. about,directions,Driving.vm      &lt;- exact match
 * 2. about,directions,Default.vm      &lt;- package match, Default name
 * 3. about,Default.vm                 &lt;- stepping up in the hierarchy
 * 4. Default.vm                       &lt;- The name configured as default.layout.template
 *                                        in the corresponding Templating Engine

 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class LayoutTemplateMapper
    extends BaseTemplateMapper
    implements Mapper
{
    /** Logging */
    private static final Logger log = LogManager.getLogger(LayoutTemplateMapper.class);

    /**
     * Default C'tor. If you use this C'tor, you must use
     * the bean setter to set the various properties needed for
     * this mapper before first usage.
     */
    public LayoutTemplateMapper()
    {
    	// empty
    }

    /**
     * Look for a given Template, then try the
     * defaults until we hit the root.
     *
     * @param template The template name.
     * @return The parsed module name.
     */
    @Override
    public String doMapping(String template)
    {
        log.debug("doMapping({})", template);
        // Copy our elements into an array
        List<String> components
            = new ArrayList<>(Arrays.asList(StringUtils.split(
                                              template,
                                              String.valueOf(TemplateService.TEMPLATE_PARTS_SEPARATOR))));
        int componentSize = components.size() - 1 ;

        // This method never gets an empty string passed.
        // So this is never < 0
        String templateName = components.get(componentSize);
        components.remove(componentSize--);

        log.debug("templateName is {}", templateName);

        // Last element decides, which template Service to use...
        TemplateService templateService = (TemplateService)TurbineServices.getInstance().getService(TemplateService.SERVICE_NAME);
        TemplateEngineService tes = templateService.getTemplateEngineService(templateName);

        if (tes == null)
        {
            return null;
        }

        // We're, after all, a Layout Template Mapper...
        String defaultName = templateService.getDefaultLayoutTemplateName(templateName);

        // This is an optimization. If the name we're looking for is
        // already the default name for the template, don't do a "first run"
        // which looks for an exact match.
        boolean firstRun = !templateName.equals(defaultName);

        for(;;)
        {
            String templatePackage = StringUtils.join(components.iterator(), String.valueOf(separator));

            log.debug("templatePackage is now: {}", templatePackage);

            StringBuilder testName = new StringBuilder();

            if (!components.isEmpty())
            {
                testName.append(templatePackage);
                testName.append(separator);
            }

            testName.append((firstRun)
                ? templateName
                : defaultName);

            // But the Templating service must look for the name with prefix
            StringBuilder templatePath = new StringBuilder();
            if (StringUtils.isNotEmpty(prefix))
            {
                templatePath.append(prefix);
                templatePath.append(separator);
            }
            templatePath.append(testName);

            log.debug("Looking for {}", templatePath);

            if (tes.templateExists(templatePath.toString()))
            {
                log.debug("Found it, returning {}", testName);
                return testName.toString();
            }

            if (firstRun)
            {
                firstRun = false;
            }
            else
            {
                // We're no longer on the first Run (so we
                // already tested the "Default" template)
                // and the package is empty (we've hit the
                // root. So we now break the endless loop.
                if (components.isEmpty())
                {
                    break; // for(;;)
                }
                // We still have components. Remove the
                // last one and go through the loop again.
                components.remove(componentSize--);
            }
        }

        log.debug("Returning default");
        return getDefaultName(template);
    }
}
