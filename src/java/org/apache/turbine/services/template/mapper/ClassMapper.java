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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.modules.Assembler;
import org.apache.turbine.modules.Loader;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.util.TurbineException;

/**
 * This mapper tries to map Template names to class names. If no direct match
 * is found, it tries matches "upwards" in the package hierarchy until either
 * a match is found or the root is hit. Then it returns the name of the
 * default class from the TemplateEngineService.
 *
 * 1. about.directions.Driving     &lt;- direct matching the template to the class name
 * 2. about.directions.Default     &lt;- matching the package, class name is Default
 * 3. about.Default                &lt;- stepping up in the package hierarchy, looking for Default
 * 4. Default                      &lt;- Class called "Default" without package
 * 5. VelocityScreen               &lt;- The class configured by the Service (VelocityService) to
 *
 * Please note, that no actual packages are searched. This is the scope of the
 * TemplateEngine Loader which is passed at construction time.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class ClassMapper
    extends BaseMapper
    implements Mapper
{
    /** The loader for actually trying out the package names */
    private Loader<? extends Assembler> loader = null;

    /** Logging */
    private static Log log = LogFactory.getLog(ClassMapper.class);

    /**
     * Default C'tor. If you use this C'tor, you must use
     * the bean setter to set the various properties needed for
     * this mapper before first usage.
     */
    public ClassMapper()
    {
    	// empty
    }

    /**
     * Get the Loader value.
     * @return the Loader value.
     */
    public Loader<? extends Assembler> getLoader()
    {
        return loader;
    }

    /**
     * Set the Loader value.
     * @param loader The new Loader value.
     */
    public void setLoader(Loader<? extends Assembler> loader)
    {
        this.loader = loader;
        log.debug("Loader is " + this.loader);
    }

    /**
     * Strip off a possible extension, replace all "," with "."
     * Look through the given package path until a match is found.
     *
     * @param template The template name.
     * @return A class name for the given template.
     */
    @Override
	public String doMapping(String template)
    {
        log.debug("doMapping(" + template + ")");

        // Copy our elements into an array
        List<String> components
            = new ArrayList<String>(Arrays.asList(StringUtils.split(
                                              template,
                                              String.valueOf(TemplateService.TEMPLATE_PARTS_SEPARATOR))));
        int componentSize = components.size() - 1 ;

        // This method never gets an empty string passed.
        // So this is never < 0
        String className = components.get(componentSize);
        components.remove(componentSize--);

        log.debug("className is " + className);

        // Strip off a possible Extension
        int dotIndex = className.lastIndexOf(TemplateService.EXTENSION_SEPARATOR);
        className = (dotIndex < 0) ? className : className.substring(0, dotIndex);

        // This is an optimization. If the name we're looking for is
        // already the default name for the template, don't do a "first run"
        // which looks for an exact match.
        boolean firstRun = !className.equals(TemplateService.DEFAULT_NAME);

        for(;;)
        {
            String pkg = StringUtils.join(components.iterator(), String.valueOf(separator));
            StringBuilder testName = new StringBuilder();

            log.debug("classPackage is now: " + pkg);

            if (!components.isEmpty())
            {
                testName.append(pkg);
                testName.append(separator);
            }

            testName.append((firstRun)
                ? className
                : TemplateService.DEFAULT_NAME);

            log.debug("Looking for " + testName);
            try
            {
                loader.getAssembler(testName.toString());
                log.debug("Found it, returning " + testName);
                return testName.toString();
            }
            catch (TurbineException e)
            {
                log.error("Turbine Exception Class mapping  ",e);
            }
            catch (Exception e)
            {
                // Not found. Go on.
            }

            if (firstRun)
            {
                firstRun = false;
            }
            else
            {
                if (components.isEmpty())
                {
                    break; // for(;;)
                }
                components.remove(componentSize--);
            }
        }

        log.debug("Returning default");
        return getDefaultName(template);
    }
}




