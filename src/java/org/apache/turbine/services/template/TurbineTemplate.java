package org.apache.turbine.services.template;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import org.apache.turbine.services.TurbineServices;

import org.apache.turbine.util.RunData;

/**
 * This is a simple static accessor to common TemplateService tasks such as
 * getting a Screen that is associated with a screen template.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @version $Id$
 */
public abstract class TurbineTemplate
{
    /**
     * Utility method for accessing the service
     * implementation
     *
     * @return a TemplateService implementation instance
     */
    protected static TemplateService getService()
    {
        return (TemplateService) TurbineServices
            .getInstance().getService(TemplateService.SERVICE_NAME);
    }

    /**
     * Returns true if the Template Service has caching activated
     *
     * @return true if Caching is active.
     */
    public static final boolean isCaching()
    {
        return getService().isCaching();
    }

    /**
     * Get the default extension given in the properties file.
     *
     * @return A String with the extension.
     */
    public static final String getDefaultExtension()
    {
        return getService().getDefaultExtension();
    }

    /**
     * Return Extension for a supplied template
     *
     * @param template The template name
     *
     * @return extension The extension for the supplied template
     */
    public static final String getExtension(String template)
    {
        return getService().getExtension(template);
    }

    /**
     * Returns the Default Template Name with the Default Extension.
     * If the extension is unset, return only the template name
     *
     * @return The default template Name
     */
    public static final String getDefaultTemplate()
    {
        return getService().getDefaultTemplate();
    }

    /**
     * Get the default page module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default page module name.
     */
    public static final String getDefaultPage()
    {
        return getService().getDefaultPage();
    }

    /**
     * Get the Screen template given in the properties file.
     *
     * @return A String which is the value of the TemplateService
     * default.screen property.
     */
    public static final String getDefaultScreen()
    {
        return getService().getDefaultScreen();
    }

    /**
     * Get the default layout module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default layout module name.
     */
    public static final String getDefaultLayout()
    {
        return getService().getDefaultLayout();
    }

    /**
     * Get the default Navigation given in the properties file.
     *
     * @return A String which is the value of the TemplateService
     * default.navigation property.
     */
    public static final String getDefaultNavigation()
    {
        return getService().getDefaultNavigation();
    }

    /**
     * Get the default layout template given in the properties file.
     *
     * @return A String which is the value of the TemplateService
     * default.layout.template property.
     */
    public static final String getDefaultLayoutTemplate()
    {
        return getService().getDefaultLayoutTemplate();
    }

    /**
     * Get the default page module name of the template engine
     * service corresponding to the template name extension of
     * the named template.
     *
     * @param template The template name.
     * @return The default page module name.
     */
    public static final String getDefaultPageName(String template)
    {
        return getService().getDefaultPageName(template);
    }

    /**
     * Get the default screen module name of the template engine
     * service corresponding to the template name extension of
     * the named template.
     *
     * @param template The template name.
     * @return The default screen module name.
     */
    public static final String getDefaultScreenName(String template)
    {
        return getService().getDefaultScreenName(template);
    }

    /**
     * Get the default layout module name of the template engine
     * service corresponding to the template name extension of
     * the named template.
     *
     * @param template The template name.
     * @return The default layout module name.
     */
    public static final String getDefaultLayoutName(String template)
    {
        return getService().getDefaultLayoutName(template);
    }

    /**
     * Get the default navigation module name of the template engine
     * service corresponding to the template name extension of
     * the named template.
     *
     * @param template The template name.
     * @return The default navigation module name.
     */
    public static final String getDefaultNavigationName(String template)
    {
        return getService().getDefaultNavigationName(template);
    }

    /**
     * Get the default layout template name of the template engine
     * service corresponding to the template name extension of
     * the named template.
     *
     * @param template The template name.
     * @return The default layout template name.
     */
    public static final String getDefaultLayoutTemplateName(String template)
    {
        return getService().getDefaultLayoutTemplateName(template);
    }

    /**
     * Find the default page module name for the given request.
     *
     * @param data The encapsulation of the request to retrieve the
     *             default page for.
     * @return The default page module name.
     */
    public static final String getDefaultPageName(RunData data)
    {
        return getService().getDefaultPageName(data);
    }

    /**
     * Find the default layout module name for the given request.
     *
     * @param data The encapsulation of the request to retrieve the
     *             default layout for.
     * @return The default layout module name.
     */
    public static final String getDefaultLayoutName(RunData data)
    {
        return getService().getDefaultLayoutName(data);
    }

    /**
     * Locate and return the name of a Screen module.
     *
     * @param name A String with the name of the template.
     * @return A String with the name of the screen.
     * @exception Exception, a generic exception.
     */
    public static final String getScreenName(String name)
        throws Exception
    {
        return getService().getScreenName(name);
    }

    /**
     * Locate and return the name of the layout module to be used
     * with the named layout template.
     *
     * @param template The layout template name.
     * @return The found layout module name.
     * @exception Exception, a generic exception.
     */
    public static final String getLayoutName(String template)
        throws Exception
    {
        return getService().getLayoutName(template);
    }

    /**
     * Locate and return the name of the navigation module to be used
     * with the named navigation template.
     *
     * @param template The navigation template name.
     * @return The found navigation module name.
     * @exception Exception, a generic exception.
     */
    public static final String getNavigationName(String template)
        throws Exception
    {
        return getService().getNavigationName(template);
    }

    /**
     * Locate and return the name of a screen template.
     *
     * @param key A String which is the key to the template.
     * @return A String with the screen template path.
     * @exception Exception, a generic exception.
     */
    public static final String getScreenTemplateName(String key)
        throws Exception
    {
        return getService().getScreenTemplateName(key);
    }

    /**
     * Locate and return the name of a layout template.
     *
     * @param name A String with the name of the template.
     * @return A String with the layout template path.
     * @exception Exception, a generic exception.
     */
    public static final String getLayoutTemplateName(String name)
        throws Exception
    {
        return getService().getLayoutTemplateName(name);
    }

    /**
     * Translates the supplied template paths into their Turbine-canonical
     * equivalent (probably absolute paths).
     *
     * @param templatePaths An array of template paths.
     * @return An array of translated template paths.
     * @deprecated Each template engine service should know how to translate
     *             a request onto a file. 
     */
    public static final String[] translateTemplatePaths(String[] templatePaths)
    {
        return getService().translateTemplatePaths(templatePaths);
    }

    /**
     * Delegates to the appropriate {@link
     * org.apache.turbine.services.template.TemplateEngineService} to
     * check the existance of the specified template.
     *
     * @param template The template to check for the existance of.
     * @param templatePaths The paths to check for the template.
     * @deprecated Use templateExists from the various Templating Engines
     */
    public static final boolean templateExists(String template, String[] templatePaths)
    {
        return getService().templateExists(template, templatePaths);
    }

    /**
     * Registers the provided template engine for use by the
     * <code>TemplateService</code>.
     *
     * @param service The <code>TemplateEngineService</code> to register.
     */
    public static final void registerTemplateEngineService(TemplateEngineService service)
    {
        getService().registerTemplateEngineService(service);
    }

    /**
     * The {@link org.apache.turbine.services.template.TemplateEngineService}
     * associated with the specified template's file extension.
     *
     * @param template The template name.
     * @return The template engine service.
     */
    public static final TemplateEngineService getTemplateEngineService(String template)
    {
        return getService().getTemplateEngineService(template);
    }
}
