package org.apache.turbine.services.template;


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


import java.util.Hashtable;

import org.apache.commons.configuration2.Configuration;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;

/**
 * The base implementation of Turbine {@link
 * org.apache.turbine.services.template.TemplateEngineService}.
 *
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class BaseTemplateEngineService
    extends TurbineBaseService
    implements TemplateEngineService
{
    /**
     * A Map containing the configuration for the template
     * engine service. The configuration contains:
     *
     * 1) template extensions
     * 2) default page
     * 3) default screen
     * 4) default layout
     * 5) default navigation
     * 6) default error screen
     */
    private final Hashtable<String, Object> configuration = new Hashtable<>();

    /**
     * @see org.apache.turbine.services.template.TemplateEngineService#registerConfiguration
     */
    @Override
    public void registerConfiguration(String defaultExt)
    {
        initConfiguration(defaultExt);
        TemplateService templateService = (TemplateService)TurbineServices.getInstance().getService(TemplateService.SERVICE_NAME);
        templateService.registerTemplateEngineService(this);
    }

    /**
     * @see org.apache.turbine.services.template.TemplateEngineService#getTemplateEngineServiceConfiguration
     */
    @Override
    public Hashtable<String, Object> getTemplateEngineServiceConfiguration()
    {
        return configuration;
    }

    /**
     * @see org.apache.turbine.services.template.TemplateEngineService#getAssociatedFileExtensions
     */
    @Override
    public String[] getAssociatedFileExtensions()
    {
        return (String[]) configuration.get(TEMPLATE_EXTENSIONS);
    }

    /**
     * Initialize the Template Engine Service.
     *
     * Note engine file extension associations.  First attempts to
     * pull a list of custom extensions from the property file value
     * keyed by <code>template.extension</code>.  If none are defined,
     * uses the value keyed by
     * <code>template.default.extension</code>, defaulting to the
     * emergency value supplied by <code>defaultExt</code>.
     *
     * @param defaultExt The default used when the default defined in the
     *                   properties file is missing or misconfigured.
     */
    protected void initConfiguration(String defaultExt)
    {
        Configuration config = getConfiguration();

        //
        // Should modify the configuration class to take defaults
        // here, should have to do this.
        //
        String[] fileExtensionAssociations =
                config.getStringArray(TEMPLATE_EXTENSIONS);

        if (fileExtensionAssociations == null ||
            fileExtensionAssociations.length == 0)
        {
            fileExtensionAssociations = new String[1];
            fileExtensionAssociations[0] = config.getString(
                    DEFAULT_TEMPLATE_EXTENSION, defaultExt);
        }

        configuration.put(TEMPLATE_EXTENSIONS, fileExtensionAssociations);

        /*
         * We need some better error checking here and should probably
         * throw an exception here if these things aren't set
         * up correctly.
         */

        String[] copyParams = {
            DEFAULT_PAGE,
            DEFAULT_SCREEN,
            DEFAULT_LAYOUT,
            DEFAULT_NAVIGATION,
            DEFAULT_ERROR_SCREEN,
            DEFAULT_LAYOUT_TEMPLATE,
            DEFAULT_SCREEN_TEMPLATE
        };

        for (String copyParam : copyParams)
        {
            configuration.put(copyParam, config.getString(copyParam, ""));
        }
    }

    /**
     * @see org.apache.turbine.services.template.TemplateEngineService#templateExists
     */
    @Override
    public abstract boolean templateExists(String template);
}
