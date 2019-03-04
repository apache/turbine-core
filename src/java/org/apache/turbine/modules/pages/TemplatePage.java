package org.apache.turbine.modules.pages;


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


import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * When building sites using templates, Screens need only be defined
 * for templates which require dynamic (database or object) data.
 *
 * <p>
 *
 * This page can be used on sites where the number of Screens can be
 * much less than the number of templates.  The templates can be
 * grouped in directories with common layouts.  Screen modules are
 * then expected to be placed in packages corresponding with the
 * templates' directories and follow a specific naming scheme.
 *
 * <p>
 *
 * The template parameter is parsed and and a Screen whose package
 * matches the templates path and shares the same name minus any
 * extension and beginning with a capital letter is searched for.  If
 * not found, a Screen in a package matching the template's path with
 * name Default is searched for.  If still not found, a Screen with
 * name Default is looked for in packages corresponding to parent
 * directories in the template's path until a match is found.
 *
 * <p>
 *
 * For example if data.getParameters().getString("template") returns
 * /about_us/directions/driving.wm, the search follows
 * about_us.directions.Driving, about_us.directions.Default,
 * about_us.Default, Default, WebMacroSiteScreen (i.e. the default
 * screen set in TurbineResources).
 *
 * <p>
 *
 * Only one Layout module is used, since it is expected that any
 * dynamic content will be placed in navigations and screens.  The
 * layout template to be used is found in a similar way to the Screen.
 * For example the following paths will be searched in the layouts
 * subdirectory: /about_us/directions/driving.wm,
 * /about_us/directions/default.wm, /about_us/default.wm, /default.wm,
 * where wm is the value of the template.default.extension property.
 *
 * <p>
 *
 * This approach allows a site with largely static content to be
 * updated and added to regularly by those with little Java
 * experience.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class TemplatePage
    extends DefaultPage
{
    /** Injected service instance */
    @TurbineService
    private TemplateService templateService;

    /**
     * Works with TemplateService to set up default templates and
     * corresponding class modules.
     *
     * @param pipelineData Turbine information.
     * @throws Exception a generic exception.
     */
    @Override
    protected void doBuildAfterAction(PipelineData pipelineData)
        throws Exception
    {
        RunData data = pipelineData.getRunData();
        // The Template Service at this point must fetch the Screen class
        // to match a given template. If the Screen class has already been
        // set by an action, skip this, because the user has the already
        // specified the Screen class he wants to use.
        if (!data.hasScreen())
        {
            // This is effectively getting the "template" parameter
            // from the parameter parser in rundata. This is coming
            // from the request for a template.
            String template = data.getTemplateInfo().getScreenTemplate();

            // Get the layout template and the correct Screen.
            String layoutTemplate =
                    templateService.getLayoutTemplateName(template);
            data.getTemplateInfo().setLayoutTemplate(layoutTemplate);

            String screen = templateService.getScreenName(template);

            if (screen == null)
            {
                String errMsg = "Couldn't map Template "
                    + template + " to any Screen class!";
                log.error(errMsg);
                throw new TurbineException(errMsg);
            }
            data.setScreen(screen);
        }
    }
}
