package org.apache.turbine.services.template;

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

import java.util.Hashtable;

import org.apache.commons.configuration.Configuration;

import org.apache.turbine.services.TurbineBaseService;

/**
 * The base implementation of Turbine {@link
 * org.apache.turbine.services.template.TemplateEngineService}.
 *
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
    private Hashtable configuration = new Hashtable();

    /**
     * @see org.apache.turbine.services.template.TemplateEngineService#registerConfiguration
     */
    public void registerConfiguration(String defaultExt)
    {
        initConfiguration(defaultExt);
        TurbineTemplate.registerTemplateEngineService(this);
    }

    /**
     * @see org.apache.turbine.services.template.TemplateEngineService#getTemplateEngineServiceConfiguration
     */
    public Hashtable getTemplateEngineServiceConfiguration()
    {
        return configuration;
    }

    /**
     * @see org.apache.turbine.services.template.TemplateEngineService#getAssociatedFileExtensions
     */
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

        for (int i = 0; i < copyParams.length; i++)
        {
            configuration.put(copyParams[i], config.getString(copyParams[i], ""));
        }
    }

    /**
     * @see org.apache.turbine.services.template.TemplateEngineService#templateExists
     */
    public abstract boolean templateExists(String template);
}
