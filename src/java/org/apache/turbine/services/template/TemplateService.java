package org.apache.turbine.services.template;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

import org.apache.turbine.util.RunData;

import org.apache.turbine.services.Service;

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
public interface TemplateService extends Service
{
    /**
     * The key under which this service is stored in TurbineServices.
     */
    public static final String SERVICE_NAME = "TemplateService";

    /**
     * Get the default template name extension specified 
     * in the template service properties.
     *
     * @return The default the extension.
     */
    public String getDefaultExtension();
    
    /**
     * Get the default page module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default page module name.
     */
    public String getDefaultPage();
    
    /**
     * Get the default screen module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default screen module name.
     */
    public String getDefaultScreen();
    
    /**
     * Get the default layout module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default layout module name.
     */
    public String getDefaultLayout();
    
    /**
     * Get the default navigation module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default navigation module name.
     */
    public String getDefaultNavigation();

    /**
     * Get the default layout template name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default layout template name.
     */
    public String getDefaultLayoutTemplate();

    /**
     * Get the default page module name of the template engine
     * service corresponding to the template name extension of 
     * the named template.
     *
     * @param template The template name.
     * @return The default page module name.
     */
    public String getDefaultPageName(String template);
    
    /**
     * Get the default screen module name of the template engine
     * service corresponding to the template name extension of 
     * the named template.
     *
     * @param template The template name.
     * @return The default screen module name.
     */
    public String getDefaultScreenName(String template);
    
    /**
     * Get the default layout module name of the template engine
     * service corresponding to the template name extension of 
     * the named template.
     *
     * @param template The template name.
     * @return The default layout module name.
     */
    public String getDefaultLayoutName(String template);
    
    /**
     * Get the default navigation module name of the template engine
     * service corresponding to the template name extension of 
     * the named template.
     *
     * @param template The template name.
     * @return The default navigation module name.
     */
    public String getDefaultNavigationName(String template);
    
    /**
     * Get the default layout template name of the template engine
     * service corresponding to the template name extension of 
     * the named template.
     *
     * @param template The template name.
     * @return The default layout template name.
     */
    public String getDefaultLayoutTemplateName(String template);
    
    /**
     * Find the default page module name for the given request.
     *
     * @param data The encapsulation of the request to retrieve the
     *             default page for.
     * @return The default page module name.
     */
    public String getDefaultPageName(RunData data);

    /**
     * Find the default layout module name for the given request.
     *
     * @param data The encapsulation of the request to retrieve the
     *             default layout for.
     * @return The default layout module name.
     */
    public String getDefaultLayoutName(RunData data);

    /**
     * Locate and return the name of the screen module to be used
     * with the named screen template.
     *
     * @param template The screen template name.
     * @return The found screen module name.
     * @exception Exception, a generic exception.
     */
    public String getScreenName(String template)
        throws Exception;

    /**
     * Locate and return the name of the layout module to be used
     * with the named layout template. 
     *
     * @param template The layout template name.
     * @return The found layout module name.
     * @exception Exception, a generic exception.
     */
    public String getLayoutName(String template)
        throws Exception;

    /**
     * Locate and return the name of the navigation module to be used
     * with the named navigation template. 
     *
     * @param template The navigation template name.
     * @return The found navigation module name.
     * @exception Exception, a generic exception.
     */
    public String getNavigationName(String name)
        throws Exception;

    /**
     * Locate and return the name of the screen template corresponding
     * to the given template name parameter.
     *
     * @param template The template name parameter.
     * @return The found screen template name.
     * @exception Exception, a generic exception.
     */
    public String getScreenTemplateName(String template)
        throws Exception;

    /**
     * Locate and return the name of the layout template corresponding
     * to the given screen template name parameter.
     *
     * @param template The template name parameter.
     * @return The found screen template name.
     * @exception Exception, a generic exception.
     */
    public String getLayoutTemplateName(String template)
        throws Exception;

    /**
     * Translates the supplied template paths into their Turbine-canonical
     * equivalent (probably absolute paths).
     *
     * @param templatePaths An array of template paths. 
     * @return An array of translated template paths.
     */
    public String[] translateTemplatePaths(String[] templatePaths);

    /**
     * Delegates to the appropriate {@link
     * org.apache.turbine.services.template.TemplateEngineService} to
     * check the existance of the specified template.
     *
     * @param template      The template to check for the existance of.
     * @param templatePaths The paths to check for the template.
     */
    public boolean templateExists(String template, 
                                  String[] templatePaths);

    /**
     * Registers the provided template engine for use by the
     * <code>TemplateService</code>.
     *
     * @param service The <code>TemplateEngineService</code> to register.
     */
    public void registerTemplateEngineService(TemplateEngineService service);
}
