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
 * This is a pretty simple mapper which returns template pathes for
 * a supplied template name. If the path does not exist, it looks for
 * a templated called "Default" in the same package.
 * This path can be used by the TemplateEngine to access
 * a certain resource to actually render the template.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class ScreenDefaultTemplateMapper
    extends BaseTemplateMapper
    implements Mapper
{
    /** Logging */
    private static final Logger log = LogManager.getLogger(ScreenDefaultTemplateMapper.class);

    /**
     * Default C'tor. If you use this C'tor, you must use
     * the bean setter to set the various properties needed for
     * this mapper before first usage.
     */
    public ScreenDefaultTemplateMapper()
    {
    	// empty
    }

    /**
     * Look for a given Template, then try the
     * default.
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

        String defaultName = "Default.vm";

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
                // We run this loop only two times. The
                // first time with the 'real' name and the
                // second time with "Default". The second time
                // we will end up here and break the for(;;) loop.
                break;
            }
        }

        log.debug("Returning default");
        return getDefaultName(template);
    }
}
