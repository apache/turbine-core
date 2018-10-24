package org.apache.turbine.modules.layouts;


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


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.annotation.TurbineLoader;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.modules.Layout;
import org.apache.turbine.modules.Screen;
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.velocity.VelocityService;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.template.TemplateNavigation;
import org.apache.velocity.context.Context;

/**
 * This Layout module allows Velocity templates to be used as layouts.
 * Since dynamic content is supposed to be primarily located in
 * screens and navigations there should be relatively few reasons to
 * subclass this Layout.
 *
 * To get the same functionality as with VelocityECSLayout, you can use two
 * supplied VelocityMacros, TurbineHtmlHead and TurbineHtmlBodyAttributes
 * in your templates. These are used to put HtmlPageAttributes into a page
 * before rendering.
 *
 * Use these macros should be used in the Layout template like this:
 *
 * ... set things like style sheets, scripts here.
 * &lt;html&gt;
 * #TurbineHtmlHead()
 * &lt;body #TurbineHtmlBodyAttributes() &gt;
 *  .... your body information
 * &lt;/body&gt;
 * &lt;/html&gt;
 *
 * As the layout template is rendered _after_ the screen template, you
 * can of course, add information to the $page tool in your screen template.
 * This will be added correctly to the &lt;head&gt;...&lt;/head&gt; and
 * &lt;body&gt; tags.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class VelocityOnlyLayout
    extends Layout
{
    /** Logging */
    private static Log log = LogFactory.getLog(VelocityOnlyLayout.class);

    /** The prefix for lookup up layout pages */
    private final String prefix = Layout.PREFIX + "/";

    /** Injected service instance */
    @TurbineService
    private VelocityService velocityService;

    /** Injected loader instance */
    @TurbineLoader( Screen.class )
    private ScreenLoader screenLoader;

    /**
     * Build the layout.  Also sets the ContentType and Locale headers
     * of the HttpServletResponse object.
     *
     *
     * @param pipelineData PipelineData
     * @throws Exception generic exception
     */
    @Override
    public void doBuild(PipelineData pipelineData)
        throws Exception
    {
        RunData data = getRunData(pipelineData);
        // Get the context needed by Velocity.
        Context context = velocityService.getContext(pipelineData);

        String screenName = data.getScreen();

        log.debug("Loading Screen " + screenName);

        // First, generate the screen and put it in the context so
        // we can grab it the layout template.
        String results = screenLoader.eval(pipelineData, screenName);
        String returnValue = StringUtils.defaultIfEmpty(results, StringUtils.EMPTY);

        // variable for the screen in the layout template
        context.put(TurbineConstants.SCREEN_PLACEHOLDER, returnValue);

        // variable to reference the navigation screen in the layout template
        context.put(TurbineConstants.NAVIGATION_PLACEHOLDER,
                    new TemplateNavigation(data));

        // Grab the layout template set in the VelocityPage.
        // If null, then use the default layout template
        // (done by the TemplateInfo object)
        String templateName = data.getTemplateInfo().getLayoutTemplate();

        // Set the locale and content type
        data.getResponse().setLocale(data.getLocale());
        data.getResponse().setContentType(data.getContentType());

        log.debug("Now trying to render layout " + templateName);

        // Finally, generate the layout template and send it to the browser
        velocityService.handleRequest(context,
                prefix + templateName, data.getResponse().getOutputStream());
    }
}
