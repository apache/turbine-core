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

import java.io.File;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.LayoutLoader;
import org.apache.turbine.modules.Loader;
import org.apache.turbine.modules.NavigationLoader;
import org.apache.turbine.modules.PageLoader;
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.factory.TurbineFactory;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.services.template.mapper.BaseTemplateMapper;
import org.apache.turbine.services.template.mapper.ClassMapper;
import org.apache.turbine.services.template.mapper.DirectMapper;
import org.apache.turbine.services.template.mapper.DirectTemplateMapper;
import org.apache.turbine.services.template.mapper.LayoutTemplateMapper;
import org.apache.turbine.services.template.mapper.Mapper;
import org.apache.turbine.services.template.mapper.ScreenTemplateMapper;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.uri.URIConstants;

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
    /** Logging */
    private static Log log = LogFactory.getLog(TurbineTemplateService.class);

    /** Represents Page Objects */
    public static final int PAGE_KEY = 0;

    /** Represents Page Objects */
    public static final String PAGE_NAME = "page";

    /** Represents Screen Objects */
    public static final int SCREEN_KEY = 1;

    /** Represents Screen Objects */
    public static final String SCREEN_NAME = "screen";

    /** Represents Layout Objects */
    public static final int LAYOUT_KEY = 2;

    /** Represents Layout Objects */
    public static final String LAYOUT_NAME = "layout";

    /** Represents Navigation Objects */
    public static final int NAVIGATION_KEY = 3;

    /** Represents Navigation Objects */
    public static final String NAVIGATION_NAME = "navigation";

    /** Represents Layout Template Objects */
    public static final int LAYOUT_TEMPLATE_KEY = 4;

    /** Represents Layout Template Objects */
    public static final String LAYOUT_TEMPLATE_NAME = "layout.template";

    /** Represents Screen Template Objects */
    public static final int SCREEN_TEMPLATE_KEY = 5;

    /** Represents Screen Template Objects */
    public static final String SCREEN_TEMPLATE_NAME = "screen.template";

    /** Represents Navigation Template Objects */
    public static final int NAVIGATION_TEMPLATE_KEY = 6;

    /** Represents Navigation Template Objects */
    public static final String NAVIGATION_TEMPLATE_NAME = "navigation.template";

    /** Number of different Template Types that we know of */
    public static final int TEMPLATE_TYPES = 7;

    /** Here we register the mapper objects for our various object types */
    private Mapper [] mapperRegistry = null;

    /**
     * The default file extension used as a registry key when a
     * template's file extension cannot be determined.
     *
     * @deprecated. Use TemplateService.DEFAULT_EXTENSION_VALUE.
     */
    protected static final String NO_FILE_EXT = TemplateService.DEFAULT_EXTENSION_VALUE;


    /** Flag set if cache is to be used. */
    private boolean useCache = false;

    /** Default extension for templates. */
    private String defaultExtension;

    /** Default template without the default extension. */
    private String defaultTemplate;

    /**
     * The mappings of template file extensions to {@link
     * org.apache.turbine.services.template.TemplateEngineService}
     * implementations. Implementing template engines can locate
     * templates within the capability of any resource loaders they
     * may possess, and other template engines are stuck with file
     * based template hierarchy only.
     */
    private Map templateEngineRegistry = null;

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
        // Get the configuration for the template service.
        Configuration config = getConfiguration();

        // Get the default extension to use if nothing else is applicable.
        defaultExtension = config.getString(TemplateService.DEFAULT_EXTENSION_KEY,
            TemplateService.DEFAULT_EXTENSION_VALUE);

        defaultTemplate =  config.getString(TemplateService.DEFAULT_TEMPLATE_KEY,
            TemplateService.DEFAULT_TEMPLATE_VALUE);

        // Check to see if we are going to be caching modules.
        // Aaargh, who moved this _out_ of the TemplateService package?
        useCache = Turbine.getConfiguration().getBoolean(TurbineConstants.MODULE_CACHE_KEY,
            TurbineConstants.MODULE_CACHE_DEFAULT);

        log.debug("Default Extension: " + defaultExtension);
        log.debug("Default Template:  " + defaultTemplate);
        log.debug("Use Caching:       " + useCache);

        templateEngineRegistry = Collections.synchronizedMap(new HashMap());

        initMapper(config);
        setInit(true);
    }

    /**
     * Returns true if the Template Service has caching activated
     *
     * @return true if Caching is active.
     */
    public boolean isCaching()
    {
        return useCache;
    }

    /**
     * Get the default template name extension specified
     * in the template service properties. If no extension
     * is defined, return the empty string.
     *
     * @return The default extension.
     */
    public String getDefaultExtension()
    {
        return StringUtils.isNotEmpty(defaultExtension) ? defaultExtension : "";
    }

    /**
     * Return Extension for a supplied template
     *
     * @param template The template name
     *
     * @return extension The extension for the supplied template
     */
    public String getExtension(String template)
    {
        if (StringUtils.isEmpty(template))
        {
            return getDefaultExtension();
        }

        int dotIndex = template.indexOf(EXTENSION_SEPARATOR);

        return (dotIndex < 0) ? getDefaultExtension() : template.substring(dotIndex + 1);
    }


    /**
     * Returns the Default Template Name with the Default Extension.
     * If the extension is unset, return only the template name
     *
     * @return The default template Name
     */
    public String getDefaultTemplate()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(defaultTemplate);
        if (StringUtils.isNotEmpty(defaultExtension))
        {
            sb.append(EXTENSION_SEPARATOR);
            sb.append(getDefaultExtension());
        }
        return sb.toString();
    }

    /**
     * Get the default page module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default page module name.
     */
    public String getDefaultPage()
    {
        return getDefaultPageName(getDefaultTemplate());
    }

    /**
     * Get the default screen module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default screen module name.
     */
    public String getDefaultScreen()
    {
        return getDefaultScreenName(getDefaultTemplate());
    }

    /**
     * Get the default layout module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default layout module name.
     */
    public String getDefaultLayout()
    {
        return getDefaultLayoutName(getDefaultTemplate());
    }

    /**
     * Get the default navigation module name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default navigation module name.
     */
    public String getDefaultNavigation()
    {
        return getDefaultNavigationName(getDefaultTemplate());
    }

    /**
     * Get the default layout template name of the template engine
     * service corresponding to the default template name extension.
     *
     * @return The default layout template name.
     */
    public String getDefaultLayoutTemplate()
    {
        return getDefaultLayoutTemplateName(getDefaultTemplate());
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
        return ((Mapper) mapperRegistry[PAGE_KEY]).getDefaultName(template);
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
        return ((Mapper) mapperRegistry[SCREEN_KEY]).getDefaultName(template);
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
        return ((Mapper) mapperRegistry[LAYOUT_KEY]).getDefaultName(template);
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
        return ((Mapper) mapperRegistry[NAVIGATION_KEY]).getDefaultName(template);
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
        return ((Mapper) mapperRegistry[LAYOUT_TEMPLATE_KEY]).getDefaultName(template);
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
        String template = data.getParameters().get(URIConstants.CGI_TEMPLATE_PARAM);
        return (template != null) ?
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
        String template = data.getParameters().get(URIConstants.CGI_TEMPLATE_PARAM);
        return (template != null) ?
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
        return ((Mapper) mapperRegistry[SCREEN_KEY]).getMappedName(template);
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
        return ((Mapper) mapperRegistry[LAYOUT_KEY]).getMappedName(template);
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
        return ((Mapper) mapperRegistry[NAVIGATION_KEY]).getMappedName(template);
    }

    /**
     * Locate and return the name of the screen template corresponding
     * to the given template name parameter. This might return null if
     * the screen is not found!
     *
     * @param template The template name parameter.
     * @return The found screen template name.
     * @exception Exception, a generic exception.
     */
    public String getScreenTemplateName(String template)
        throws Exception
    {
        return ((Mapper) mapperRegistry[SCREEN_TEMPLATE_KEY]).getMappedName(template);
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
        return ((Mapper) mapperRegistry[LAYOUT_TEMPLATE_KEY]).getMappedName(template);
    }

    /**
     * Locate and return the name of the navigation template corresponding
     * to the given template name parameter. This might return null if
     * the navigation is not found!
     *
     * @param template The template name parameter.
     * @return The found navigation template name.
     * @exception Exception, a generic exception.
     */
    public String getNavigationTemplateName(String template)
        throws Exception
    {
        return ((Mapper) mapperRegistry[NAVIGATION_TEMPLATE_KEY]).getMappedName(template);
    }

    /**
     * Translates the supplied template paths into their Turbine-canonical
     * equivalent (probably absolute paths). This is used if the templating
     * engine (e.g. JSP) does not provide any means to load a page but 
     * the page path is passed to the servlet container.
     *
     * @param templatePaths An array of template paths.
     * @return An array of translated template paths.
     * @deprecated Each template engine service should know how to translate
     *             a request onto a file. 
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
     * @deprecated Use templateExists from the various Templating Engines
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
        String[] exts = service.getAssociatedFileExtensions();

        for (int i = 0; i < exts.length; i++)
        {
            templateEngineRegistry.put(exts[i], service);
        }
    }

    /**
     * The {@link org.apache.turbine.services.template.TemplateEngineService}
     * associated with the specified template's file extension.
     *
     * @param template The template name.
     * @return The template engine service.
     */
    public TemplateEngineService getTemplateEngineService(String template)
    {
        return (TemplateEngineService) templateEngineRegistry.get(getExtension(template));
    }

    /**
     * Register a template Mapper to the service. This Mapper
     * performs the template mapping and searching for a specific
     * object type which is managed by the TemplateService.
     *
     * @param templateKey  One of the _KEY constants for the Template object types.
     * @param mapper  An object which implements the Mapper interface.
     */
    private void registerMapper(int templateKey, Mapper mapper)
    {
        mapper.init();
        mapperRegistry[templateKey] = mapper;
    }

    /**
     * Load and configure the Template mappers for
     * the Template Service.
     *
     * @param conf The current configuration object.
     * @throws InitializationException A problem occured trying to set up the mappers.
     */
    private void initMapper(Configuration conf)
            throws InitializationException
    {
        // Create a registry with the number of Template Types managed by this service.
        // We could use a List object here and extend the number of managed objects
        // dynamically. However, by using an Object Array, we get much more performance
        // out of the Template Service.
        mapperRegistry = new Mapper [TEMPLATE_TYPES];

        String [] mapperNames = new String [] {
            PAGE_NAME,SCREEN_NAME, LAYOUT_NAME,
            NAVIGATION_NAME, LAYOUT_TEMPLATE_NAME, SCREEN_TEMPLATE_NAME, NAVIGATION_TEMPLATE_NAME
        };

        String [] mapperClasses = new String [] {
            DirectMapper.class.getName(),
            ClassMapper.class.getName(),
            ClassMapper.class.getName(),
            ClassMapper.class.getName(),
            LayoutTemplateMapper.class.getName(),
            ScreenTemplateMapper.class.getName(),
            DirectTemplateMapper.class.getName()
        };

        int [] mapperCacheSize = new int [] {
            0,
            conf.getInt(
                    TurbineConstants.SCREEN_CACHE_SIZE_KEY,
                    TurbineConstants.SCREEN_CACHE_SIZE_DEFAULT),
            conf.getInt(
                    TurbineConstants.LAYOUT_CACHE_SIZE_KEY,
                    TurbineConstants.LAYOUT_CACHE_SIZE_DEFAULT),
            conf.getInt(
                    TurbineConstants.NAVIGATION_CACHE_SIZE_KEY,
                    TurbineConstants.NAVIGATION_CACHE_SIZE_DEFAULT),
            conf.getInt(
                    TurbineConstants.LAYOUT_CACHE_SIZE_KEY,
                    TurbineConstants.LAYOUT_CACHE_SIZE_DEFAULT),
            conf.getInt(
                    TurbineConstants.SCREEN_CACHE_SIZE_KEY,
                    TurbineConstants.SCREEN_CACHE_SIZE_DEFAULT),
            conf.getInt(
                    TurbineConstants.NAVIGATION_CACHE_SIZE_KEY,
                    TurbineConstants.NAVIGATION_CACHE_SIZE_DEFAULT)
        };

        String [] mapperDefaultProperty = new String [] {
            TemplateEngineService.DEFAULT_PAGE,
            TemplateEngineService.DEFAULT_SCREEN,
            TemplateEngineService.DEFAULT_LAYOUT,
            TemplateEngineService.DEFAULT_NAVIGATION,
            TemplateEngineService.DEFAULT_LAYOUT_TEMPLATE,
            TemplateEngineService.DEFAULT_SCREEN_TEMPLATE,
            TemplateEngineService.DEFAULT_NAVIGATION_TEMPLATE
        };

        char [] mapperSeparator = new char [] { '.', '.', '.', '.', '/', '/', '/' };

        Loader [] mapperLoader = new Loader [] { 
            PageLoader.getInstance(),
            ScreenLoader.getInstance(),
            LayoutLoader.getInstance(),
            NavigationLoader.getInstance(),
            null, null, null};

        String [] mapperPrefix = new String [] { 
            null, null, null, null,
            TurbineConstants.LAYOUT_PREFIX,
            TurbineConstants.SCREEN_PREFIX,
            TurbineConstants.NAVIGATION_PREFIX  };

        for (int i = 0; i < TEMPLATE_TYPES; i++)
        {
            StringBuffer mapperProperty = new StringBuffer();
            mapperProperty.append("mapper.");
            mapperProperty.append(mapperNames[i]);
            mapperProperty.append(".class");

            String mapperClass = 
                    conf.getString(mapperProperty.toString(), mapperClasses[i]);

            log.info("Using " + mapperClass + " to map " + mapperNames[i] + " elements");

            Mapper tm = null;

            try
            {
                tm = (Mapper) TurbineFactory.getInstance(mapperClass);
            }
            catch (TurbineException te)
            {
                throw new InitializationException("", te);
            }

            tm.setUseCache(useCache);
            tm.setCacheSize(mapperCacheSize[i]);
            tm.setDefaultProperty(mapperDefaultProperty[i]);
            tm.setSeparator(mapperSeparator[i]);

            if ((mapperLoader[i] != null) && (tm instanceof ClassMapper))
            {
                ((ClassMapper) tm).setLoader(mapperLoader[i]);
            }

            if ((mapperPrefix[i] != null) && (tm instanceof BaseTemplateMapper))
            {
                ((BaseTemplateMapper) tm).setPrefix(mapperPrefix[i]);
            }

            registerMapper(i, tm);
        }
    }
}
