package org.apache.turbine;

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationFactory;
import org.apache.commons.configuration.PropertiesConfiguration;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.lang.exception.ExceptionUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4jFactory;

import org.apache.log4j.PropertyConfigurator;

import org.apache.turbine.modules.ActionLoader;
import org.apache.turbine.modules.PageLoader;

import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.avaloncomponent.AvalonComponentService;
import org.apache.turbine.services.component.ComponentService;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.services.rundata.TurbineRunDataFacade;
import org.apache.turbine.services.velocity.VelocityService;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.ServerData;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.util.template.TemplateInfo;
import org.apache.turbine.util.uri.URIConstants;

/**
 * Turbine is the main servlet for the entire system. It is <code>final</code>
 * because you should <i>not</i> ever need to subclass this servlet.  If you
 * need to perform initialization of a service, then you should implement the
 * Services API and let your code be initialized by it.
 * If you need to override something in the <code>doGet()</code> or
 * <code>doPost()</code> methods, edit the TurbineResources.properties file and
 * specify your own classes there.
 * <p>
 * Turbine servlet recognizes the following initialization parameters.
 * <ul>
 * <li><code>properties</code> the path to TurbineResources.properties file
 * used by the default implementation of <code>ResourceService</code>, relative
 * to the application root.</li>
 * <li><code>basedir</code> this parameter is used <strong>only</strong> if your
 * application server does not support web applications, or the or does not
 * support <code>ServletContext.getRealPath(String)</code> method correctly.
 * You can use this parameter to specify the directory within the server's
 * filesystem, that is the base of your web application.</li>
 * </ul>
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class Turbine
        extends HttpServlet
        implements TurbineConstants
{
    /**
     * Name of path info parameter used to indicate the redirected stage of
     * a given user's initial Turbine request
     */
    public static final String REDIRECTED_PATHINFO_NAME = "redirected";

    /** The base directory key */
    public static final String BASEDIR_KEY = "basedir";

    /**
     * In certain situations the init() method is called more than once,
     * somtimes even concurrently. This causes bad things to happen,
     * so we use this flag to prevent it.
     */
    private static boolean firstInit = true;

    /** Whether init succeeded or not. */
    private static Throwable initFailure = null;

    /**
     * Should initialization activities be performed during doGet() execution?
     */
    private static boolean firstDoGet = true;

    /**
     * Keep all the properties of the web server in a convenient data
     * structure
     */
    private static ServerData serverData = null;

    /** The base from which the Turbine application will operate. */
    private static String applicationRoot;

    /** Servlet config for this Turbine webapp. */
    private static ServletConfig servletConfig;

    /** Servlet context for this Turbine webapp. */
    private static ServletContext servletContext;

    /**
     * The webapp root where the Turbine application
     * is running in the servlet container.
     * This might differ from the application root.
     */
    private static String webappRoot;

    /** Our internal configuration object */
    private static Configuration configuration = null;

    /** A reference to the Template Service */
    private TemplateService templateService = null;

    /** A reference to the RunData Service */
    private RunDataService rundataService = null;

    /** Logging class from commons.logging */
    private static Log log = LogFactory.getLog(Turbine.class);

    /**
     * This init method will load the default resources from a
     * properties file.
     *
     * This method is called by init(ServletConfig config)
     *
     * @exception ServletException a servlet exception.
     */
    public final void init() throws ServletException
    {
        synchronized (this.getClass())
        {
            super.init();
            ServletConfig config = getServletConfig();

            if (!firstInit)
            {
                log.info("Double initialization of Turbine was attempted!");
                return;
            }
            // executing init will trigger some static initializers, so we have
            // only one chance.
            firstInit = false;

            try
            {
                ServletContext context = config.getServletContext();

                configure(config, context);

                templateService = TurbineTemplate.getService();
                rundataService = TurbineRunDataFacade.getService();

                if (rundataService == null)
                {
                    throw new TurbineException(
                            "No RunData Service configured!");
                }

            }
            catch (Exception e)
            {
                // save the exception to complain loudly later :-)
                initFailure = e;
                log.fatal("Turbine: init() failed: ", e);
                throw new ServletException("Turbine: init() failed", e);
            }
            log.info("Turbine: init() Ready to Rumble!");
        }
    }

    /**
     * Read the master configuration file in, configure logging
     * and start up any early services.
     *
     * @param config The Servlet Configuration supplied by the container
     * @param context The Servlet Context supplied by the container
     *
     * @throws Exception A problem occured while reading the configuration or performing early startup
     */

    private void configure(ServletConfig config, ServletContext context)
            throws Exception
    {
        //
        // Set up Commons Logging to use the Log4J Logging
        //
        System.getProperties().setProperty(LogFactory.class.getName(),
                                           Log4jFactory.class.getName());

        // Set the application root. This defaults to the webapp
        // context if not otherwise set. This is to allow 2.1 apps
        // to be developed from CVS. This feature will carry over
        // into 3.0.
        applicationRoot = findInitParameter(context, config,
                APPLICATION_ROOT_KEY,
                APPLICATION_ROOT_DEFAULT);

        webappRoot = config.getServletContext().getRealPath("/");
        // log.info("Web Application root is " + webappRoot);
        // log.info("Application root is "     + applicationRoot);

        if (applicationRoot == null || applicationRoot.equals(WEB_CONTEXT))
        {
            applicationRoot = webappRoot;
            // log.info("got empty or 'webContext' Application root. Application root now: " + applicationRoot);
        }

        // Set the applicationRoot for this webapp.
        setApplicationRoot(applicationRoot);

        // Create any directories that need to be setup for
        // a running Turbine application.
        createRuntimeDirectories(context, config);

        //
        // Now we run the Turbine configuration code. There are two ways
        // to configure Turbine:
        //
        // a) By supplying an web.xml init parameter called "configuration"
        //
        // <init-param>
        //   <param-name>configuration</param-name>
        //   <param-value>/WEB-INF/conf/turbine.xml</param-value>
        // </init-param>
        //
        // This loads an XML based configuration file.
        //
        // b) By supplying an web.xml init parameter called "properties"
        //
        // <init-param>
        //   <param-name>properties</param-name>
        //   <param-value>/WEB-INF/conf/TurbineResources.properties</param-value>
        // </init-param>
        //
        // This loads a Properties based configuration file. Actually, these are
        // extended properties as provided by commons-configuration
        //
        // If neither a) nor b) is supplied, Turbine will fall back to the
        // known behaviour of loading a properties file called
        // /WEB-INF/conf/TurbineResources.properties relative to the
        // web application root.

        String confFile= findInitParameter(context, config, 
                TurbineConfig.CONFIGURATION_PATH_KEY, 
                null);

        String confPath;
        String confStyle = "unset";

        if (StringUtils.isNotEmpty(confFile))
        {
            confPath = getRealPath(confFile);
            ConfigurationFactory configurationFactory = new ConfigurationFactory(confPath);
            configurationFactory.setBasePath(getApplicationRoot());
            configuration = configurationFactory.getConfiguration();
            confStyle = "XML";
        }
        else
        {
            confFile = findInitParameter(context, config,
                    TurbineConfig.PROPERTIES_PATH_KEY,
                    TurbineConfig.PROPERTIES_PATH_DEFAULT);

            confPath = getRealPath(confFile);

            // This should eventually be a Configuration
            // interface so that service and app configuration
            // can be stored anywhere.
            configuration = (Configuration) new PropertiesConfiguration(confPath);
            confStyle = "Properties";
        }


        //
        // Set up logging as soon as possible
        //
        String log4jFile = configuration.getString(LOG4J_CONFIG_FILE,
                                                   LOG4J_CONFIG_FILE_DEFAULT);

        log4jFile = getRealPath(log4jFile);

        //
        // Load the config file above into a Properties object and
        // fix up the Application root
        //
        Properties p = new Properties();
        try
        {
            p.load(new FileInputStream(log4jFile));
            p.setProperty(APPLICATION_ROOT_KEY, getApplicationRoot());
            PropertyConfigurator.configure(p);

            //
            // Rebuild our log object with a configured commons-logging
            log = LogFactory.getLog(this.getClass());

            log.info("Configured log4j from " + log4jFile);
        }
        catch (FileNotFoundException fnf)
        {
            System.err.println("Could not open Log4J configuration file "
                               + log4jFile + ": ");
            fnf.printStackTrace();
        }

        // Now report our successful configuration to the world
        log.info("Loaded configuration  (" + confStyle + ") from " + confFile + " (" + confPath + ")");

        
        setTurbineServletConfig(config);
        setTurbineServletContext(context);

        getServiceManager().setApplicationRoot(applicationRoot);

        // We want to set a few values in the configuration so
        // that ${variable} interpolation will work for
        //
        // ${applicationRoot}
        // ${webappRoot}
        configuration.setProperty(APPLICATION_ROOT_KEY, applicationRoot);
        configuration.setProperty(WEBAPP_ROOT_KEY, webappRoot);


        //
        // Be sure, that our essential services get run early
        //
        configuration.setProperty(TurbineServices.SERVICE_PREFIX +
                                  ComponentService.SERVICE_NAME + ".earlyInit",
                                  Boolean.TRUE);

        configuration.setProperty(TurbineServices.SERVICE_PREFIX +
                                  AvalonComponentService.SERVICE_NAME + ".earlyInit",
                                  Boolean.TRUE);

        getServiceManager().setConfiguration(configuration);

        // Initialize the service manager. Services
        // that have its 'earlyInit' property set to
        // a value of 'true' will be started when
        // the service manager is initialized.
        getServiceManager().init();
    }

    /**
     * Create any directories that might be needed during
     * runtime. Right now this includes:
     *
     * <ul>
     *
     * <li>The directory to write the log files to (relative to the
     * web application root), or <code>null</code> for the default of
     * <code>/logs</code>.  The directory is specified via the {@link
     * TurbineConstants#LOGGING_ROOT} parameter.</li>
     *
     * </ul>
     *
     * @param context Global initialization parameters.
     * @param config Initialization parameters specific to the Turbine
     * servlet.
     */
    private static void createRuntimeDirectories(ServletContext context,
                                                 ServletConfig config)
    {
        String path = findInitParameter(context, config,
                                        LOGGING_ROOT_KEY,
                                        LOGGING_ROOT_DEFAULT);

        File logDir = new File(getRealPath(path));
        if (!logDir.exists())
        {
            // Create the logging directory
            if (!logDir.mkdirs())
            {
                System.err.println("Cannot create directory for logs!");
            }
        }
    }

    /**
     * Finds the specified servlet configuration/initialization
     * parameter, looking first for a servlet-specific parameter, then
     * for a global parameter, and using the provided default if not
     * found.
     */
    protected static final String findInitParameter(ServletContext context,
            ServletConfig config, String name, String defaultValue)
    {
        String path = null;

        // Try the name as provided first.
        boolean usingNamespace = name.startsWith(CONFIG_NAMESPACE);
        while (true)
        {
            path = config.getInitParameter(name);
            if (StringUtils.isEmpty(path))
            {
                path = context.getInitParameter(name);
                if (StringUtils.isEmpty(path))
                {
                    // The named parameter didn't yield a value.
                    if (usingNamespace)
                    {
                        path = defaultValue;
                    }
                    else
                    {
                        // Try again using Turbine's namespace.
                        name = CONFIG_NAMESPACE + '.' + name;
                        usingNamespace = true;
                        continue;
                    }
                }
            }
            break;
        }

        return path;
    }

    /**
     * Initializes the services which need <code>RunData</code> to
     * initialize themselves (post startup).
     *
     * @param data The first <code>GET</code> request.
     */
    public final void init(RunData data)
    {
        synchronized (Turbine.class)
        {
            if (firstDoGet)
            {
                // All we want to do here is save some servlet
                // information so that services and processes
                // that don't have direct access to a RunData
                // object can still know something about
                // the servlet environment.
                saveServletInfo(data);

                // Mark that we're done.
                firstDoGet = false;
                log.info("Turbine: first Request successful");
            }
        }
    }

    /**
     * Return the current configuration with all keys included
     *
     * @return a Configuration Object
     */
    public static Configuration getConfiguration()
    {
        return configuration;
    }

    /**
     * Return the server name.
     *
     * @return String server name
     */
    public static String getServerName()
    {
        return getDefaultServerData().getServerName();
    }

    /**
     * Return the server scheme.
     *
     * @return String server scheme
     */
    public static String getServerScheme()
    {
        return getDefaultServerData().getServerScheme();
    }

    /**
     * Return the server port.
     *
     * @return String server port
     */
    public static String getServerPort()
    {
        return Integer.toString(getDefaultServerData().getServerPort());
    }

    /**
     * Get the script name. This is the initial script name.
     * Actually this is probably not needed any more. I'll
     * check. jvz.
     *
     * @return String initial script name.
     */
    public static String getScriptName()
    {
        return getDefaultServerData().getScriptName();
    }

    /**
     * Return the context path.
     *
     * @return String context path
     */
    public static String getContextPath()
    {
        return getDefaultServerData().getContextPath();
    }

    /**
     * Return all the Turbine Servlet information (Server Name, Port,
     * Scheme in a ServerData structure. This is generated from the
     * values set when initializing the Turbine and may not be correct
     * if you're running in a clustered structure. This might be used
     * if you need a DataURI and have no RunData object handy-
     *
     * @return An initialized ServerData object
     */
    public static ServerData getDefaultServerData()
    {
        if(serverData == null)
        {
            log.error("ServerData Information requested from Turbine before first request!");
            // Will be overwritten once the first request is run;
            serverData = new ServerData(null, URIConstants.HTTP_PORT,
                    URIConstants.HTTP, null, null);
        }
        return serverData;
    }

    /**
     * Set the servlet config for this turbine webapp.
     *
     * @param config New servlet config
     */
    public static void setTurbineServletConfig(ServletConfig config)
    {
        servletConfig = config;
    }

    /**
     * Get the servlet config for this turbine webapp.
     *
     * @return ServletConfig
     */
    public static ServletConfig getTurbineServletConfig()
    {
        return servletConfig;
    }

    /**
     * Set the servlet context for this turbine webapp.
     *
     * @param context New servlet context.
     */
    public static void setTurbineServletContext(ServletContext context)
    {
        servletContext = context;
    }

    /**
     * Get the servlet context for this turbine webapp.
     *
     * @return ServletContext
     */
    public static ServletContext getTurbineServletContext()
    {
        return servletContext;
    }

    /**
     * The <code>Servlet</code> destroy method.  Invokes
     * <code>ServiceBroker</code> tear down method.
     */
    public final void destroy()
    {
        // Shut down all Turbine Services.
        getServiceManager().shutdownServices();
        System.gc();

        firstInit = true;
        firstDoGet = true;
        log.info("Turbine: Done shutting down!");
    }

    /**
     * The primary method invoked when the Turbine servlet is executed.
     *
     * @param req Servlet request.
     * @param res Servlet response.
     * @exception IOException a servlet exception.
     * @exception ServletException a servlet exception.
     */
    public final void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException
    {
        // set to true if the request is to be redirected by the page
        boolean requestRedirected = false;

        // Placeholder for the RunData object.
        RunData data = null;
        try
        {
            // Check to make sure that we started up properly.
            if (initFailure != null)
            {
                throw initFailure;
            }

            // Get general RunData here...
            // Perform turbine specific initialization below.
            data = rundataService.getRunData(req, res, getServletConfig());

            // If this is the first invocation, perform some
            // initialization.  Certain services need RunData to initialize
            // themselves.
            if (firstDoGet)
            {
                init(data);
            }

            // set the session timeout if specified in turbine's properties
            // file if this is a new session
            if (data.getSession().isNew())
            {
                int timeout = configuration.getInt(SESSION_TIMEOUT_KEY,
                                                   SESSION_TIMEOUT_DEFAULT);

                if (timeout != SESSION_TIMEOUT_DEFAULT)
                {
                    data.getSession().setMaxInactiveInterval(timeout);
                }
            }

            // Fill in the screen and action variables.
            data.setScreen(data.getParameters().getString(URIConstants.CGI_SCREEN_PARAM));
            data.setAction(data.getParameters().getString(URIConstants.CGI_ACTION_PARAM));

            // Special case for login and logout, this must happen before the
            // session validator is executed in order either to allow a user to
            // even login, or to ensure that the session validator gets to
            // mandate its page selection policy for non-logged in users
            // after the logout has taken place.
            if (data.hasAction())
            {
                String action = data.getAction();
                log.debug("action = " + action);

                if (action.equalsIgnoreCase(
                        configuration.getString(ACTION_LOGIN_KEY,
                                                ACTION_LOGIN_DEFAULT)))
                {
                    loginAction(data);
                }
                else if (action.equalsIgnoreCase(
                        configuration.getString(ACTION_LOGOUT_KEY,
                                                ACTION_LOGOUT_DEFAULT)))
                {
                   logoutAction(data);
                }
            }

            // This is where the validation of the Session information
            // is performed if the user has not logged in yet, then
            // the screen is set to be Login. This also handles the
            // case of not having a screen defined by also setting the
            // screen to Login. If you want people to go to another
            // screen other than Login, you need to change that within
            // TurbineResources.properties...screen.homepage; or, you
            // can specify your own SessionValidator action.
            ActionLoader.getInstance().exec(
                    data, configuration.getString(ACTION_SESSION_VALIDATOR_KEY,
                        ACTION_SESSION_VALIDATOR_DEFAULT));

            // Put the Access Control List into the RunData object, so
            // it is easily available to modules.  It is also placed
            // into the session for serialization.  Modules can null
            // out the ACL to force it to be rebuilt based on more
            // information.
            ActionLoader.getInstance().exec(
                    data, configuration.getString(ACTION_ACCESS_CONTROLLER_KEY,
                        ACTION_ACCESS_CONTROLLER_DEFAULT));

            // Start the execution phase. DefaultPage will execute the
            // appropriate action as well as get the Layout from the
            // Screen and then execute that. The Layout is then
            // responsible for executing the Navigation and Screen
            // modules.
            //
            // Note that by default, this cannot be overridden from
            // parameters passed in via post/query data. This is for
            // security purposes.  You should really never need more
            // than just the default page.  If you do, add logic to
            // DefaultPage to do what you want.

            String defaultPage = (templateService == null)
                    ? null :templateService.getDefaultPageName(data);

            if (defaultPage == null)
            {
                /*
                 * In this case none of the template services are running.
                 * The application may be using ECS for views, or a
                 * decendent of RawScreen is trying to produce output.
                 * If there is a 'page.default' property in the TR.props
                 * then use that, otherwise return DefaultPage which will
                 * handle ECS view scenerios and RawScreen scenerios. The
                 * app developer can still specify the 'page.default'
                 * if they wish but the DefaultPage should work in
                 * most cases.
                 */
                defaultPage = configuration.getString(PAGE_DEFAULT_KEY,
                        PAGE_DEFAULT_DEFAULT);
            }

            PageLoader.getInstance().exec(data, defaultPage);

            // If a module has set data.acl = null, remove acl from
            // the session.
            if (data.getACL() == null)
            {
                try
                {
                    data.getSession().removeAttribute(
                            AccessControlList.SESSION_KEY);
                }
                catch (IllegalStateException ignored)
                {
                }
            }

            // handle a redirect request
            requestRedirected = ((data.getRedirectURI() != null)
                                 && (data.getRedirectURI().length() > 0));
            if (requestRedirected)
            {
                if (data.getResponse().isCommitted())
                {
                    requestRedirected = false;
                    log.warn("redirect requested, response already committed: " +
                             data.getRedirectURI());
                }
                else
                {
                    data.getResponse().sendRedirect(data.getRedirectURI());
                }
            }

            if (!requestRedirected)
            {
                try
                {
                    if (data.isPageSet() == false && data.isOutSet() == false)
                    {
                        throw new Exception("Nothing to output");
                    }

                    // We are all done! if isPageSet() output that way
                    // otherwise, data.getOut() has already been written
                    // to the data.getOut().close() happens below in the
                    // finally.
                    if (data.isPageSet() && data.isOutSet() == false)
                    {
                        // Modules can override these.
                        data.getResponse().setLocale(data.getLocale());
                        data.getResponse().setContentType(
                                data.getContentType());

                        // Set the status code.
                        data.getResponse().setStatus(data.getStatusCode());
                        // Output the Page.
                        data.getPage().output(data.getOut());
                    }
                }
                catch (Exception e)
                {
                    // The output stream was probably closed by the client
                    // end of things ie: the client clicked the Stop
                    // button on the browser, so ignore any errors that
                    // result.
                    log.debug("Output stream closed? ", e);
                }
            }
        }
        catch (Exception e)
        {
            handleException(data, res, e);
        }
        catch (Throwable t)
        {
            handleException(data, res, t);
        }
        finally
        {
            // Return the used RunData to the factory for recycling.
            rundataService.putRunData(data);
        }
    }

    /**
     * In this application doGet and doPost are the same thing.
     *
     * @param req Servlet request.
     * @param res Servlet response.
     * @exception IOException a servlet exception.
     * @exception ServletException a servlet exception.
     */
    public final void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException
    {
        doGet(req, res);
    }

    /**
     * This method is executed if the configured Login action should be
     * executed by Turbine.
     * <p>
     * This Action must be performed before the Session validation or we
     * get sent in an endless loop back to the Login screen before
     * the action can be performed
     *
     * @param data a RunData object
     *
     * @throws Exception A problem while logging in occured.
     */
    private void loginAction(RunData data)
            throws Exception
    {
        ActionLoader.getInstance().exec(data, data.getAction());
        cleanupTemplateContext(data);
        data.setAction(null);
    }

    /**
     * This method is executed if the configured Logout action should be
     * executed by Turbine.
     * <p>
     * This Action must be performed before the Session validation for the
     * session validator to send us back to the Login screen.
     * <p>
     * The existing session is invalidated before the logout action is
     * executed.
     *
     * @param data a RunData object
     *
     * @throws Exception A problem while logging out occured.
     */
    private void logoutAction(RunData data)
            throws Exception
    {
        ActionLoader.getInstance().exec(data, data.getAction());
        cleanupTemplateContext(data);
        data.setAction(null);
        data.getSession().invalidate();
    }

    /**
     * cleans the Velocity Context if available.
     *
     * @param data A RunData Object
     *
     * @throws Exception A problem while cleaning out the Template Context occured.
     */
    private void cleanupTemplateContext(RunData data)
            throws Exception
    {
        // This is Velocity specific and shouldn't be done here.
        // But this is a band aid until we get real listeners
        // here.
        TemplateInfo ti = data.getTemplateInfo();
        if (ti != null)
        {
            ti.removeTemp(VelocityService.CONTEXT);
        }
    }

    /**
     * Return the servlet info.
     *
     * @return a string with the servlet information.
     */
    public final String getServletInfo()
    {
        return "Turbine Servlet";
    }

    /**
     * This method is about making sure that we catch and display
     * errors to the screen in one fashion or another. What happens is
     * that it will attempt to show the error using your user defined
     * Error Screen. If that fails, then it will resort to just
     * displaying the error and logging it all over the place
     * including the servlet engine log file, the Turbine log file and
     * on the screen.
     *
     * @param data A Turbine RunData object.
     * @param res Servlet response.
     * @param t The exception to report.
     */
    private final void handleException(RunData data, HttpServletResponse res,
                                       Throwable t)
    {
        // make sure that the stack trace makes it the log
        log.error("Turbine.handleException: ", t);

        String mimeType = "text/plain";
        try
        {
            // This is where we capture all exceptions and show the
            // Error Screen.
            data.setStackTrace(ExceptionUtils.getStackTrace(t), t);

            // setup the screen
            data.setScreen(configuration.getString(SCREEN_ERROR_KEY,
                    SCREEN_ERROR_DEFAULT));

            // do more screen setup for template execution if needed
            if (data.getTemplateInfo() != null)
            {
                data.getTemplateInfo()
                    .setScreenTemplate(configuration.getString(
                            TEMPLATE_ERROR_KEY, TEMPLATE_ERROR_VM));
            }

            // Make sure to not execute an action.
            data.setAction("");

            PageLoader.getInstance().exec(data,
                    configuration.getString(PAGE_DEFAULT_KEY,
                            PAGE_DEFAULT_DEFAULT));

            data.getResponse().setContentType(data.getContentType());
            data.getResponse().setStatus(data.getStatusCode());
            if (data.isPageSet())
            {
                data.getOut().print(data.getPage().toString());
            }
        }
        // Catch this one because it occurs if some code hasn't been
        // completely re-compiled after a change..
        catch (java.lang.NoSuchFieldError e)
        {
            try
            {
                data.getResponse().setContentType(mimeType);
                data.getResponse().setStatus(200);
            }
            catch (Exception ignored)
            {
            }

            try
            {
                data.getOut().print("java.lang.NoSuchFieldError: "
                        + "Please recompile all of your source code.");
            }
            catch (IOException ignored)
            {
            }

            log.error(data.getStackTrace(), e);
        }
        // Attempt to do *something* at this point...
        catch (Throwable reallyScrewedNow)
        {
            StringBuffer msg = new StringBuffer();
            msg.append("Horrible Exception: ");
            if (data != null)
            {
                msg.append(data.getStackTrace());
            }
            else
            {
                msg.append(t);
            }
            try
            {
                res.setContentType(mimeType);
                res.setStatus(200);
                res.getWriter().print(msg.toString());
            }
            catch (Exception ignored)
            {
            }

            log.error(reallyScrewedNow.getMessage(), reallyScrewedNow);
        }
    }

    /**
     * Save some information about this servlet so that
     * it can be utilized by object instances that do not
     * have direct access to RunData.
     *
     * @param data
     */
    public static synchronized void saveServletInfo(RunData data)
    {
        // Store the context path for tools like ContentURI and
        // the UIManager that use webapp context path information
        // for constructing URLs.

        //
        // Bundle all the information above up into a convenient structure
        //
        serverData = (ServerData) data.getServerData().clone();
    }

    /**
     * Set the application root for the webapp.
     *
     * @param val New app root.
     */
    public static void setApplicationRoot(String val)
    {
        applicationRoot = val;
    }

    /**
     * Get the application root for this Turbine webapp. This
     * concept was started in 3.0 and will allow an app to be
     * developed from a standard CVS layout. With a simple
     * switch the app will work fully within the servlet
     * container for deployment.
     *
     * @return String applicationRoot
     */
    public static String getApplicationRoot()
    {
        return applicationRoot;
    }

    /**
     * Used to get the real path of configuration and resource
     * information. This can be used by an app being
     * developed in a standard CVS layout.
     *
     * @param path path translated to the application root
     * @return the real path
     */
    public static String getRealPath(String path)
    {
        if (path.startsWith("/"))
        {
            path = path.substring(1);
        }

        return new File(getApplicationRoot(), path).getAbsolutePath();
    }

    /**
     * Return an instance of the currently configured Service Manager
     *
     * @return A service Manager instance
     */
    private ServiceManager getServiceManager()
    {
        return TurbineServices.getInstance();
    }
}
