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

/**
 * This is the interface that all template engine services must adhere
 * to. This includes the Velocity, WebMacro, FreeMarker, and JSP
 * services.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$ */
public interface TemplateEngineService
{
    static final String TEMPLATE_EXTENSIONS = "template.extension";
    static final String DEFAULT_TEMPLATE_EXTENSION = "template.default.extension";
    static final String DEFAULT_PAGE = "default.page";
    static final String DEFAULT_SCREEN = "default.screen";
    static final String DEFAULT_LAYOUT = "default.layout";
    static final String DEFAULT_NAVIGATION = "default.navigation";
    static final String DEFAULT_ERROR_SCREEN = "default.error.screen";
    static final String DEFAULT_LAYOUT_TEMPLATE = "default.layout.template";
    static final String DEFAULT_SCREEN_TEMPLATE = "default.screen.template";
    static final String DEFAULT_NAVIGATION_TEMPLATE = "default.navigation.template";

    /**
     * Return the configuration of the template engine in
     * the form of a Hashtable.
     */
    Hashtable getTemplateEngineServiceConfiguration();

    /**
     * Initializes file extension associations and registers with the
     * template service.
     *
     * @param defaultExt The default file extension association to use
     *                   in case of properties file misconfiguration.
     */
    void registerConfiguration(String defaultExt);

    /**
     * Supplies the file extension to key this engine in {@link
     * org.apache.turbine.services.template.TemplateService}'s
     * registry with.
     */
    String[] getAssociatedFileExtensions();

    /**
     * Use the specific template engine to determine whether
     * a given template exists. This allows Turbine the TemplateService
     * to delegate the search for a template to the template
     * engine being used for the view. This gives us the
     * advantage of fully utilizing the capabilities of
     * template engine with respect to retrieving templates
     * from arbitrary sources.
     *
     * @param template The name of the template to check the existance of.
     * @return         Whether the specified template exists.
     */
    boolean templateExists(String template);
}
