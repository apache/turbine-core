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

import java.io.File;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;

import org.apache.turbine.modules.LayoutLoader;
import org.apache.turbine.modules.NavigationLoader;
import org.apache.turbine.modules.ScreenLoader;

import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;

import org.apache.turbine.services.servlet.TurbineServlet;

import org.apache.turbine.util.RunData;

/**
 * This service provides a method for mapping templates to their
 * appropriate Screens or Navigations.  It also allows templates to
 * define a layout/navigations/screen modularization within the
 * template structure.  It also performs caching if turned on in the
 * properties file.
 *
 * This service is not bound to a specific templating engine but we
 * will use the Velocity templating engine for the examples. It is
 * available by using the VelocityService.
 *
 * This assumes the following properties in the Turbine configuration:
 *
 * <pre>
 * # Register the VelocityService for the "vm" extension.
 * services.VelocityService.template.extension=vm
 * 
 * # Default Java class for rendering a Page in this service
 * # (must be found on the class path (org.apache.turbine.modules.page.VelocityPage))
 * services.VelocityService.default.page = VelocityPage
 * 
 * # Default Java class for rendering a Screen in this service
 * # (must be found on the class path (org.apache.turbine.modules.screen.VelocityScreen))
 * services.VelocityService.default.screen=VelocityScreen
 * 
 * # Default Java class for rendering a Layout in this service
 * # (must be found on the class path (org.apache.turbine.modules.layout.VelocityOnlyLayout))
 * services.VelocityService.default.layout = VelocityOnlyLayout
 * 
 * # Default Java class for rendering a Navigation in this service
 * # (must be found on the class path (org.apache.turbine.modules.navigation.VelocityNavigation))
 * services.VelocityService.default.navigation=VelocityNavigation
 *
 * # Default Template Name to be used as Layout. If nothing else is
 * # found, return this as the default name for a layout
 * services.VelocityService.default.layout.template = Default.vm
 * </pre>
 * If you want to render a template, a search path is used to find 
 * a Java class which might provide information for the context of
 * this template.
 *
 * If you request e.g. the template screen
 * 
 * about,directions,Driving.vm 
 * 
 * then the following class names are searched (on the module search
 * path):
 *
 * 1. about.directions.Driving     &lt;- direct matching the template to the class name
 * 2. about.directions.Default     &lt;- matching the package, class name is Default
 * 3. about.Default                &lt;- stepping up in the package hierarchy, looking for Default
 * 4. Default                      &lt;- Class called "Default" without package
 * 5. VelocityScreen               &lt;- The class configured by the Service (VelocityService) to
 *
 * And if you have the following module packages configured:
 *
 * module.packages = org.apache.turbine.modules, com.mycorp.modules
 *
 * then the class loader will look for
 *
 * org.apache.turbine.modules.screens.about.directions.Driving
 * com.mycorp.modules.screens.about.directions.Driving
 * org.apache.turbine.modules.screens.about.directions.Default
 * com.mycorp.modules.screens.about.directions.Default
 * org.apache.turbine.modules.screens.about.Default
 * com.mycorp.modules.screens.about.Default
 * org.apache.turbine.modules.screens.Default
 * com.mycorp.modules.screens.Default
 * org.apache.turbine.modules.screens.VelocityScreen
 * com.mycorp.modules.screens.VelocityScreen
 *
 * Most of the times, you don't have any backing Java class for a
 * template screen, so the first match will be
 * org.apache.turbine.modules.screens.VelocityScreen
 * which then renders your screen.
 *
 * Please note, that your Screen Template (Driving.vm) must exist!
 * If it does not exist, the Template Service will report an error.
 *
 * Once the screen is found, the template service will look for
 * the Layout and Navigation templates of your Screen. Here, the
 * template service looks for matching template names! 
 *
 * Consider our example:  about,directions,Driving.vm (Screen Name)
 *
 * Now the template service will look for the following Navigation
 * and Layout templates:
 *
 * 1. about,directions,Driving.vm      &lt;- exact match
 * 2. about,directions,Default.vm      &lt;- package match, Default name
 * 3. about,Default.vm                 &lt;- stepping up in the hierarchy
 * 4. Default.vm                       &lt;- The name configured as default.layout.template
 *                                        in the Velocity service.
 *
 * And now Hennings' two golden rules for using templates:
 *
 * Many examples and docs from older Turbine code show template pathes
 * with a slashes. Repeat after me: "TEMPLATE NAMES NEVER CONTAIN SLASHES!"
 *
 * Many examples and docs from older Turbine code show templates that start
 * with "/". This is not only a violation of the rule above but actively breaks
 * things like loading templates from a jar with the velocity jar loader. Repeat
 * after me: "TEMPLATE NAMES ARE NOT PATHES. THEY'RE NOT ABSOLUTE AND HAVE NO 
 * LEADING /".
 *
 * If you now wonder how a template name is mapped to a file name: This is
 * scope of the templating engine. Velocity e.g. has this wonderful option to
 * load templates from jar archives. There is no single file but you tell
 * velocity "get about,directions,Driving.vm" and it returns the rendered
 * template. This is not the job of the Templating Service but of the Template
 * rendering services like VelocityService.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TurbineTemplateService
    extends TurbineBaseService
    implements TemplateService
{
    /**
     * The default file extension used as a registry key when a
     * template's file extension cannot be determined.
     */
    protected static final String NO_FILE_EXT = "";

    /** The keys of template objects provided by the service: Page objects */
    protected static final int PAGE_KEY = 0;

    /** The keys of template objects provided by the service: Screen objects */
    protected static final int SCREEN_KEY = 1;

    /** The keys of template objects provided by the service: Layout objects */
    protected static final int LAYOUT_KEY = 2;

    /** The keys of template objects provided by the service: Navigation objects */
    protected static final int NAVIGATION_KEY = 3;

    /** The keys of template objects provided by the service: Layout Template objects */
    protected static final int LAYOUT_TEMPLATE_KEY = 4;

    /** The keys of template objects provided by the service: Screen Template objects */
    protected static final int SCREEN_TEMPLATE_KEY = 5;

    /** The properties for default template object names. */
    private String[] defaultNameProperties = new String[]
    {
        TemplateEngineService.DEFAULT_PAGE,
        TemplateEngineService.DEFAULT_SCREEN,
        TemplateEngineService.DEFAULT_LAYOUT,
        TemplateEngineService.DEFAULT_NAVIGATION,
        TemplateEngineService.DEFAULT_LAYOUT_TEMPLATE
        // Missing 'default screen template'
    };

    /** The hashtables used to cache template object names. */
    private Map[] templateNameCache = new HashMap[6];

    /** Flag set if cache is to be used. */
    private boolean useCache = false;

    /** Default extension for templates. */
    private String defaultExtension;

    /** Default template with the default extension. */
    private String defaultTemplate;

    /**
     * The mappings of template file extensions to {@link
     * org.apache.turbine.services.template.TemplateEngineService}
     * implementations.  Implementing template engines can locate
     * templates within the capability of any resource loaders they
     * may possess, and other template engines are stuck with file
     * based template hierarchy only.
     */
    private HashMap templateEngineRegistry = null; // Do not change. We need .clone() !

    /** Logging */
    private static Log log = LogFactory.getLog(TurbineTemplateService.class);

    /**
     * C'tor
     */
    public TurbineTemplateService()
    {
    }

    /**
     * Called the first time the Service is used.
     *
     * @exception InitializationException Something went wrong when 
     *                                     setting up the Template Service.
     */
    public void init()
        throws InitializationException
    {
        initTemplate();
        setInit(true);
    }

    /**
     * Get the default template name extension specified
     * in the template service properties.
     *
     * @return The default extension.
     */
    public String getDefaultExtension()
    {
        return defaultExtension;
    }

    /**
     * Get the default page module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default page module name.
     */
    public String getDefaultPage()
    {
        return getDefaultPageName(defaultTemplate);
    }

    /**
     * Get the default screen module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default screen module name.
     */
    public String getDefaultScreen()
    {
        return getDefaultScreenName(defaultTemplate);
    }

    /**
     * Get the default layout module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default layout module name.
     */
    public String getDefaultLayout()
    {
        return getDefaultLayoutName(defaultTemplate);
    }

    /**
     * Get the default navigation module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default navigation module name.
     */
    public String getDefaultNavigation()
    {
        return getDefaultNavigationName(defaultTemplate);
    }

    /**
     * Get the default layout template name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default layout template name.
     */
    public String getDefaultLayoutTemplate()
    {
        return getDefaultLayoutTemplateName(defaultTemplate);
    }

    /**
     * Get the default page module name of the template engine
     * service corresponding to the template name extension of
     * the named template.
     *
     * @param template The template name.
     * @return The default page module name.
     */
    public String getDefaultPageName(String template)
    {
        return getDefaultModuleName(template, PAGE_KEY);
    }

    /**
     * Get the default screen module name of the template engine
     * service corresponding to the template name extension of
     * the named template.
     *
     * @param template The template name.
     * @return The default screen module name.
     */
    public String getDefaultScreenName(String template)
    {
        return getDefaultModuleName(template, SCREEN_KEY);
    }

    /**
     * Get the default layout module name of the template engine
     * service corresponding to the template name extension of
     * the named template.
     *
     * @param template The template name.
     * @return The default layout module name.
     */
    public String getDefaultLayoutName(String template)
    {
        return getDefaultModuleName(template, LAYOUT_KEY);
    }

    /**
     * Get the default navigation module name of the template engine
     * service corresponding to the template name extension of
     * the named template.
     *
     * @param template The template name.
     * @return The default navigation module name.
     */
    public String getDefaultNavigationName(String template)
    {
        return getDefaultModuleName(template, NAVIGATION_KEY);
    }

    /**
     * Get the default layout template name of the template engine
     * service corresponding to the template name extension of
     * the named template.
     *
     * @param template The template name.
     * @return The default layout template name.
     */
    public String getDefaultLayoutTemplateName(String template)
    {
        String layoutTemplate =
                getDefaultModuleName(template, LAYOUT_TEMPLATE_KEY);

        if (StringUtils.isNotEmpty(layoutTemplate)
            && layoutTemplate.indexOf('.') < 0)
        {
            int dotIndex = template.lastIndexOf('.');
            layoutTemplate += dotIndex >= 0 ?
                    template.substring(dotIndex) : '.' + defaultExtension;
        }
        return layoutTemplate;
    }

    /**
     * Find the default page module name for the given request.
     *
     * @param data The encapsulation of the request to retrieve the
     *             default page for.
     * @return The default page module name.
     */
    public String getDefaultPageName(RunData data)
    {
        String template = data.getParameters().get("template");
        return template != null ?
                getDefaultPageName(template) : getDefaultPage();
    }

    /**
     * Find the default layout module name for the given request.
     *
     * @param data The encapsulation of the request to retrieve the
     *             default layout for.
     * @return The default layout module name.
     */
    public String getDefaultLayoutName(RunData data)
    {
        String template = data.getParameters().get("template");
        return template != null ?
                getDefaultLayoutName(template) : getDefaultLayout();
    }

    /**
     * Locate and return the name of the screen module to be used
     * with the named screen template.
     *
     * @param template The screen template name.
     * @return The found screen module name.
     * @exception Exception, a generic exception.
     */
    public String getScreenName(String template)
            throws Exception
    {
        return getCachedName(template, SCREEN_KEY);
    }

    /**
     * Locate and return the name of the layout module to be used
     * with the named layout template.
     *
     * @param template The layout template name.
     * @return The found layout module name.
     * @exception Exception, a generic exception.
     */
    public String getLayoutName(String template)
            throws Exception
    {
        return getCachedName(template, LAYOUT_KEY);
    }

    /**
     * Locate and return the name of the navigation module to be used
     * with the named navigation template.
     *
     * @param template The navigation template name.
     * @return The found navigation module name.
     * @exception Exception, a generic exception.
     */
    public String getNavigationName(String template)
            throws Exception
    {
        return getCachedName(template, NAVIGATION_KEY);
    }

    /**
     * Locate and return the name of the screen template corresponding
     * to the given template name parameter.
     *
     * @param template The template name parameter.
     * @return The found screen template name.
     * @exception Exception, a generic exception.
     */
    public String getScreenTemplateName(String template)
            throws Exception
    {
        return getCachedName(template, SCREEN_TEMPLATE_KEY);
    }

    /**
     * Locate and return the name of the layout template corresponding
     * to the given screen template name parameter.
     *
     * @param template The template name parameter.
     * @return The found screen template name.
     * @exception Exception, a generic exception.
     */
    public String getLayoutTemplateName(String template)
            throws Exception
    {
        return getCachedName(template, LAYOUT_TEMPLATE_KEY);
    }

    /**
     * Translates the supplied template paths into their Turbine-canonical
     * equivalent (probably absolute paths).
     *
     * @param templatePaths An array of template paths.
     * @return An array of translated template paths.
     */
    public String[] translateTemplatePaths(String[] templatePaths)
    {
        for (int i = 0; i < templatePaths.length; i++)
        {
            templatePaths[i] = TurbineServlet.getRealPath(templatePaths[i]);
        }
        return templatePaths;
    }

    /**
     * Delegates to the appropriate {@link
     * org.apache.turbine.services.template.TemplateEngineService} to
     * check the existance of the specified template.
     *
     * @param template The template to check for the existance of.
     * @param templatePaths The paths to check for the template.
     */
    public boolean templateExists(String template,
                                  String[] templatePaths)
    {
        for (int i = 0; i < templatePaths.length; i++)
        {
            if (new File(templatePaths[i], template).exists())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Registers the provided template engine for use by the
     * <code>TemplateService</code>.
     *
     * @param service The <code>TemplateEngineService</code> to register.
     */
    public synchronized void registerTemplateEngineService(TemplateEngineService service)
    {
        // Clone the registry to write to non-sync'd
        // Map implementations.
        HashMap registry = templateEngineRegistry != null ?
                (HashMap) templateEngineRegistry.clone() : new HashMap();

        String[] exts = service.getAssociatedFileExtensions();

        for (int i = 0; i < exts.length; i++)
        {
            registry.put(exts[i], service);
        }
        templateEngineRegistry = registry;
    }

    /**
     * The {@link org.apache.turbine.services.template.TemplateEngineService}
     * associated with the specified template's file extension.
     *
     * @param template The template name.
     * @return The template engine service.
     */
    protected TemplateEngineService getTemplateEngineService(String template)
    {
        Map registry = templateEngineRegistry;
        if (registry != null && template != null)
        {
            int dotIndex = template.lastIndexOf('.');
            String ext = dotIndex == -1 ?
                    defaultExtension : template.substring(dotIndex + 1);
            return (TemplateEngineService) registry.get(ext);
        }
        else
        {
            return null;
        }
    }

    /**
     * Get the default module name of the template engine
     * service corresponding to the template name extension of
     * the named template.
     *
     * @param template The template name.
     * @param key The module type key.
     * @return The default page module name.
     */
    protected String getDefaultModuleName(String template,
                                          int key)
    {
        TemplateEngineService tes = getTemplateEngineService(template);
        return tes != null ?
                (String) tes.getTemplateEngineServiceConfiguration().
                get(defaultNameProperties[key]) : null;
    }

    /**
     * Get the cached template object name.
     * If caching is not in use or the name is not in the cache,
     * try to find the name from the template directories.
     *
     * @param template The template name.
     * @param key The object type key.
     * @return The cached name.
     * @exception Exception, a generic exception.
     */
    protected String getCachedName(String template,
                                   int key)
            throws Exception
    {
        if (StringUtils.isEmpty(template))
        {
            return null;
        }

        // Add a default extension if missing.
        if (template.indexOf('.') < 0)
        {
            template += '.' + defaultExtension;
        }

        // Check the cache first.
        String found;
        if (useCache)
        {
            found = (String) templateNameCache[key].get(template);
            if (found != null)
            {
                return found;
            }
        }

        // Not in the cache, try to find it.
        switch (key)
        {
            case SCREEN_TEMPLATE_KEY:
                found = getParsedScreenTemplateName(template);
                break;

            case LAYOUT_TEMPLATE_KEY:
                found = getParsedLayoutTemplateName(template);
                break;

            default:
                found = getParsedModuleName(template, key);
        }

        // Put the found name to the cache.
        if (useCache)
        {
            templateNameCache[key].put(template, found);
        }

        return found;
    }

    /**
     * Get the parsed module name for the specified template.
     *
     * @param template The template name.
     * @param key The module type key.
     * @return The parsed module name.
     * @exception Exception, a generaic exception.
     */
    protected String getParsedModuleName(String template,
                                         int key)
            throws Exception
    {
        // Parse the template name and change it into a package.
        StringBuffer pckage = new StringBuffer();
        int i = parseTemplatePath(template, pckage);
        if (pckage.charAt(0) == '/')
        {
            pckage.deleteCharAt(0);
            i--;
        }
        if (i >= 0)
        {
            for (int j = 0; j <= i; j++)
            {
                if (pckage.charAt(j) == '/')
                {
                    pckage.setCharAt(j, '.');
                }
            }
        }

        // Remove a possible file extension.
        for (int j = i + 1; j < pckage.length(); j++)
        {
            if (pckage.charAt(j) == '.')
            {
                pckage.delete(j, pckage.length());
                break;
            }
        }

        // Try first an exact match for a module having the same
        // name as the input template, traverse then upper level
        // packages to find a default module named Default.
        int j = 9999;
        String module;
        while (j-- > 0)
        {
            module = pckage.toString();
            try
            {
                switch (key)
                {
                    case SCREEN_KEY:
                        ScreenLoader.getInstance().getInstance(module);
                        return module;

                    case LAYOUT_KEY:
                        LayoutLoader.getInstance().getInstance(module);
                        return module;

                    case NAVIGATION_KEY:
                        NavigationLoader.getInstance().getInstance(module);
                        return module;
                }
            }
            catch (Exception x)
            {
            }

            pckage.setLength(i + 1);
            if (i > 0)
            {
                // We have still packages to traverse.
                for (i = pckage.length() - 2; i >= 0; i--)
                {
                    if (pckage.charAt(i) == '.')
                    {
                        break;
                    }
                }
            }
            else if (j > 0)
            {
                // Only the main level left.
                j = 1;
            }
            pckage.append("Default");
        }

        // Not found, return the default module name.
        return getDefaultModuleName(template, key);
    }

    /**
     * Get the parsed screen template name for the specified template.
     *
     * @param template The template name.
     * @return The parsed screen template name.
     * @exception Exception, a generic exception.
     */
    protected String getParsedScreenTemplateName(String template)
            throws Exception
    {
        // Parse the template name.
        StringBuffer path = new StringBuffer();
        parseTemplatePath(template, path);
        if (path.charAt(0) != '/')
        {
            path.insert(0, '/');
        }
        path.insert(0, "screens");

        // Let the template engine service to check its existance.
        TemplateEngineService tes = getTemplateEngineService(template);
        if ((tes == null) || !tes.templateExists(path.toString()))
        {
            throw new Exception(
                    "Screen template '" + template + "' not found");
        }
        return path.substring(7);
    }

    /**
     * Get the parsed layout template name for the specified template.
     *
     * @param template The template name.
     * @return The parsed layout template name.
     * @exception Exception, a generic exception.
     */
    protected String getParsedLayoutTemplateName(String template)
            throws Exception
    {
        // Parse the template name.
        StringBuffer path = new StringBuffer();
        int i = parseTemplatePath(template, path);
        if (path.charAt(0) != '/')
        {
            path.insert(0, '/');
            i++;
        }
        path.insert(0, "layouts");
        i += 7;

        // Try first an exact match for a layout template having the same
        // name as the input template, traverse then upper level
        // directories to find the default layout template.
        TemplateEngineService tes = getTemplateEngineService(template);
        if (tes == null)
        {
            throw new Exception(
                    "Layout template '" + template + "' not found");
        }

        String defaultLayout = getDefaultLayoutTemplateName(template);
        if (defaultLayout != null)
        {
            if (defaultLayout.length() == 0)
            {
                defaultLayout = null;
            }
            else if (defaultLayout.charAt(0) != '/')
            {
                defaultLayout = '/' + defaultLayout;
            }
        }

        int j = 9999;
        String layoutTemplate;
        while (j-- > 0)
        {
            layoutTemplate = path.toString();

            if (tes.templateExists(layoutTemplate))
            {
                return layoutTemplate.substring(7);
            }

            if (defaultLayout == null)
            {
                throw new Exception(
                        "Layout template '" + template + "' not found");
            }

            path.setLength(i);
            if (i > 8)
            {
                // We have still directories to traverse.
                for (i = path.length() - 2; i >= 8; i--)
                {
                    if (path.charAt(i) == '/')
                    {
                        break;
                    }
                }
            }
            else if (j > 0)
            {
                // Only the main level left.
                j = 1;
            }
            path.append(defaultLayout);
        }

        // Not found, return the default layout
        // template as such.
        return defaultLayout;
    }

    /**
     * Parse the template name collected from URL parameters or
     * template context to a path name. Double slashes are changed
     * into single ones and commas used as path delemiters in
     * URL parameters are changed into slashes. Empty names or
     * names without a file part are not accepted.
     *
     * @param template The template name.
     * @param buffer A buffer for the result.
     * @return The index of the separator between the path and the name.
     * @exception Exception Malformed template name.
     */
    private int parseTemplatePath(String template,
                                  StringBuffer buffer)
            throws Exception
    {
        char c;
        int j = 0;
        int ind = -1;
        buffer.setLength(0);
        buffer.append(template);
        int len = buffer.length();
        while (j < len)
        {
            c = buffer.charAt(j);
            if (c == ',')
            {
                c = '/';
                buffer.setCharAt(j, c);
            }
            if (c == '/')
            {
                ind = j;
                if (j < (len - 1))
                {
                    c = buffer.charAt(j + 1);
                    if ((c == '/') ||
                            (c == ','))
                    {
                        buffer.deleteCharAt(j);
                        len--;
                        continue;
                    }
                }
            }
            j++;
        }
        if ((len == 0) ||
                (ind >= (len - 1)))
        {
            throw new Exception(
                "Syntax error in template name '" + template + '\'');
        }
        return ind;
    }

    /**
     * Initialize the template service.
     */
    private void initTemplate()
    {
        // Get the configuration for the template service.
        Configuration config = getConfiguration();

        // Get the default extension to use if nothing else is applicable.
        defaultExtension = config.getString("default.extension", NO_FILE_EXT);
        defaultTemplate = "Default." + defaultExtension;

        // Check to see if we are going to be caching modules.
        useCache = Turbine.getConfiguration().getBoolean(TurbineConstants.MODULE_CACHE_KEY,
                                                         TurbineConstants.MODULE_CACHE_DEFAULT);

        log.debug("useCache: " + useCache);

        if (useCache)
        {
            int screenSize     = config.getInt(TurbineConstants.SCREEN_CACHE_SIZE_KEY,
                                               TurbineConstants.SCREEN_CACHE_SIZE_DEFAULT);
            int layoutSize     = config.getInt(TurbineConstants.LAYOUT_CACHE_SIZE_KEY,
                                               TurbineConstants.LAYOUT_CACHE_SIZE_DEFAULT);
            int navigationSize = config.getInt(TurbineConstants.NAVIGATION_CACHE_SIZE_KEY,
                                               TurbineConstants.NAVIGATION_CACHE_SIZE_DEFAULT);

            log.debug("screenSize: "     + screenSize);
            log.debug("layoutSize: "     + layoutSize);
            log.debug("navigationSize: " + navigationSize);

            // Create hashtables for each object type,
            // the first one for pages is not used.
            templateNameCache[PAGE_KEY] = null;

            templateNameCache[SCREEN_KEY] =
                new HashMap((int) (1.25 * screenSize) + 1);
            templateNameCache[SCREEN_TEMPLATE_KEY] =
                new HashMap((int) (1.25 * screenSize) + 1);

            templateNameCache[LAYOUT_KEY] =
                new HashMap((int) (1.25 * layoutSize) + 1);
            templateNameCache[LAYOUT_TEMPLATE_KEY] =
                new HashMap((int) (1.25 * layoutSize) + 1);

            templateNameCache[NAVIGATION_KEY] =
                new HashMap((int) (1.25 * navigationSize) + 1);
        }
    }
}
