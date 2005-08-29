package org.apache.turbine.services.template;

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

import org.apache.turbine.services.Service;

import org.apache.turbine.util.RunData;

/**
 * This service provides a method for mapping templates to their
 * appropriate Screens or Navigations.  It also allows templates to
 * define a layout/navigations/screen modularization within the
 * template structure.  It also performs caching if turned on in the
 * properties file.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @version $Id$
 */
public interface TemplateService
    extends Service
{
    /**
     * The key under which this service is stored in TurbineServices.
     */
    static final String SERVICE_NAME = "TemplateService";

    /** Default Template Name. */
    String DEFAULT_TEMPLATE_KEY = "default.template";

    /** Default value for the Template Name */
    String DEFAULT_TEMPLATE_VALUE = "Default";

    /** Default Extension for the template names. */
    String DEFAULT_EXTENSION_KEY = "default.extension";

    /** Default value of the Turbine Module Caching */
    String DEFAULT_EXTENSION_VALUE = "";

    /** Character that separates a Template Name from the Extension */
    char EXTENSION_SEPARATOR = '.';

    /** Character that separates the various Template Parts */
    char TEMPLATE_PARTS_SEPARATOR = ',';

    /** "Default" name for Classes and Templates */
    String DEFAULT_NAME = "Default";

    /**
     * Returns true if the Template Service has caching activated
     *
     * @return true if Caching is active.
     */
    boolean isCaching();

    /**
     * Get the default template name extension specified
     * in the template service properties.
     *
     * @return The default the extension.
     */
    String getDefaultExtension();

    /**
     * Return Extension for a supplied template
     *
     * @param template The template name
     *
     * @return extension The extension for the supplied template
     */
    String getExtension(String template);

    /**
     * Returns the Default Template Name with the Default Extension.
     * If the extension is unset, return only the template name
     *
     * @return The default template Name
     */
    String getDefaultTemplate();

    /**
     * Get the default page module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default page module name.
     */
    String getDefaultPage();

    /**
     * Get the default screen module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default screen module name.
     */
    String getDefaultScreen();

    /**
     * Get the default layout module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default layout module name.
     */
    String getDefaultLayout();

    /**
     * Get the default navigation module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default navigation module name.
     */
    String getDefaultNavigation();

    /**
     * Get the default layout template name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default layout template name.
     */
    String getDefaultLayoutTemplate();

    /**
     * Get the default page module name of the template engine
     * service corresponding to the template name extension of
     * the named template.
     *
     * @param template The template name.
     * @return The default page module name.
     */
    String getDefaultPageName(String template);

    /**
     * Get the default screen module name of the template engine
     * service corresponding to the template name extension of
     * the named template.
     *
     * @param template The template name.
     * @return The default screen module name.
     */
    String getDefaultScreenName(String template);

    /**
     * Get the default layout module name of the template engine
     * service corresponding to the template name extension of
     * the named template.
     *
     * @param template The template name.
     * @return The default layout module name.
     */
    String getDefaultLayoutName(String template);

    /**
     * Get the default navigation module name of the template engine
     * service corresponding to the template name extension of
     * the named template.
     *
     * @param template The template name.
     * @return The default navigation module name.
     */
    String getDefaultNavigationName(String template);

    /**
     * Get the default layout template name of the template engine
     * service corresponding to the template name extension of
     * the named template.
     *
     * @param template The template name.
     * @return The default layout template name.
     */
    String getDefaultLayoutTemplateName(String template);

    /**
     * Find the default page module name for the given request.
     *
     * @param data The encapsulation of the request to retrieve the
     *             default page for.
     * @return The default page module name.
     */
    String getDefaultPageName(RunData data);

    /**
     * Find the default layout module name for the given request.
     *
     * @param data The encapsulation of the request to retrieve the
     *             default layout for.
     * @return The default layout module name.
     */
    String getDefaultLayoutName(RunData data);

    /**
     * Locate and return the name of the screen module to be used
     * with the named screen template.
     *
     * @param template The screen template name.
     * @return The found screen module name.
     * @exception Exception, a generic exception.
     */
    String getScreenName(String template)
            throws Exception;

    /**
     * Locate and return the name of the layout module to be used
     * with the named layout template.
     *
     * @param template The layout template name.
     * @return The found layout module name.
     * @exception Exception, a generic exception.
     */
    String getLayoutName(String template)
            throws Exception;

    /**
     * Locate and return the name of the navigation module to be used
     * with the named navigation template.
     *
     * @param template The navigation template name.
     * @return The found navigation module name.
     * @exception Exception, a generic exception.
     */
    String getNavigationName(String name)
            throws Exception;

    /**
     * Locate and return the name of the screen template corresponding
     * to the given template name parameter.
     *
     * @param template The template name parameter.
     * @return The found screen template name.
     * @exception Exception, a generic exception.
     */
    String getScreenTemplateName(String template)
            throws Exception;

    /**
     * Locate and return the name of the layout template corresponding
     * to the given screen template name parameter.
     *
     * @param template The template name parameter.
     * @return The found screen template name.
     * @exception Exception, a generic exception.
     */
    String getLayoutTemplateName(String template)
            throws Exception;

    /**
     * Locate and return the name of the navigation template corresponding
     * to the given template name parameter.
     *
     * @param template The template name parameter.
     * @return The found navigation template name.
     * @exception Exception, a generic exception.
     */
    String getNavigationTemplateName(String template)
            throws Exception;

    /**
     * Translates the supplied template paths into their Turbine-canonical
     * equivalent (probably absolute paths).
     *
     * @param templatePaths An array of template paths.
     * @return An array of translated template paths.
     * @deprecated Each template engine service should know how to translate
     *             a request onto a file.
     */
    String[] translateTemplatePaths(String[] templatePaths);

    /**
     * Delegates to the appropriate {@link
     * org.apache.turbine.services.template.TemplateEngineService} to
     * check the existance of the specified template.
     *
     * @param template      The template to check for the existance of.
     * @param templatePaths The paths to check for the template.
     * @deprecated Use templateExists from the various Templating Engines
     */
    boolean templateExists(String template,
                           String[] templatePaths);


    /**
     * The {@link org.apache.turbine.services.template.TemplateEngineService}
     * associated with the specified template's file extension.
     *
     * @param template The template name.
     * @return The template engine service.
     */
    TemplateEngineService getTemplateEngineService(String template);

    /**
     * Registers the provided template engine for use by the
     * <code>TemplateService</code>.
     *
     * @param service The <code>TemplateEngineService</code> to register.
     */
    void registerTemplateEngineService(TemplateEngineService service);
}
