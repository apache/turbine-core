package org.apache.turbine;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.combined.CombinedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.HomeDirectoryLocationStrategy;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.turbine.modules.PageLoader;
import org.apache.turbine.pipeline.Pipeline;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.pipeline.TurbinePipeline;
import org.apache.turbine.services.Initable;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.util.LocaleUtils;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.ServerData;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.uri.URIConstants;

/**
 * <p>
 * Turbine is the main servlet for the entire system. If you
 * need to perform initialization of a service, then you should implement the
 * Services API and let your code be initialized by it.
 * </p>
 * 
 * <p>
 * Turbine servlet recognizes the following initialization parameters.
 * </p>
 * 
 * <ul>
 * <li><code>properties</code> the path to TurbineResources.properties file
 * used to configure Turbine, relative to the application root.</li>
 * <li><code>configuration</code> the path to TurbineConfiguration.xml file
 * used to configure Turbine from various sources, relative
 * to the application root.</li>
 * <li><code>applicationRoot</code> this parameter defaults to the web context
 * of the servlet container. You can use this parameter to specify the directory
 * within the server's filesystem, that is the base of your web application.</li>
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
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id$
 */
@WebServlet(
    name = "Turbine",
    urlPatterns = {"/app"},
    loadOnStartup = 1,
    initParams={ @WebInitParam(name = TurbineConstants.APPLICATION_ROOT_KEY,
                    value = TurbineConstants.APPLICATION_ROOT_DEFAULT),
                 @WebInitParam(name = TurbineConfig.PROPERTIES_PATH_KEY,
                    value = TurbineConfig.PROPERTIES_PATH_DEFAULT) } )
@MultipartConfig
public class Turbine extends HttpServlet
{
    /** Serial version */
    private static final long serialVersionUID = -6317118078613623990L;

    /**
     * Name of path info parameter used to indicate the redirected stage of
     * a given user's initial Turbine request
     * @deprecated
     */
    @Deprecated // not used
    public static final String REDIRECTED_PATHINFO_NAME = "redirected";

    /** The base directory key @deprecated 
     * */
    @Deprecated // not used
    public static final String BASEDIR_KEY = "basedir";

    /**
     * In certain situations the init() method is called more than once,
     * sometimes even concurrently. This causes bad things to happen,
     * so we use this flag to prevent it.
     */
    private static boolean firstInit = true;

    /**
     * The pipeline to use when processing requests.
     */
    private static Pipeline pipeline = null;

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
    private static volatile ServerData serverData = null;

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

    /** Which configuration method is being used */
    private enum ConfigurationStyle
    {
        XML,
        PROPERTIES,
        JSON,
        YAML,
        UNSET
    }

    /** Logging class from commons.logging - this may still work, due to jcl-over-slf4j */
    //private static Log log = LogFactory.getLog(Turbine.class);
    public static Logger log = LogManager.getLogger();

    /**
     * This init method will load the default resources from a
     * properties file.
     *
     * This method is called by init(ServletConfig config)
     *
     * @throws ServletException a servlet exception.
     */
    @Override
    public void init() throws ServletException
    {
        synchronized (Turbine.class)
        {
            super.init();

            if (!firstInit)
            {
                log.info("Double initialization of Turbine was attempted!");
                return;
            }
            // executing init will trigger some static initializers, so we have
            // only one chance.
            firstInit = false;
            ServletConfig config = getServletConfig();

            try
            {
                ServletContext context = config.getServletContext();

                configure(config, context);

                TemplateService templateService =
                    (TemplateService)getServiceManager().getService(TemplateService.SERVICE_NAME);
                if (templateService == null)
                {
                    throw new TurbineException("No Template Service configured!");
                }

                if (getRunDataService() == null)
                {
                    throw new TurbineException("No RunData Service configured!");
                }
            }
            catch (Throwable e)
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
     * @throws Exception A problem occurred while reading the configuration or performing early startup
     */

    protected void configure(ServletConfig config, ServletContext context)
            throws Exception
    {

        // Set the application root. This defaults to the webapp
        // context if not otherwise set.
        applicationRoot = findInitParameter(context, config,
                TurbineConstants.APPLICATION_ROOT_KEY,
                TurbineConstants.APPLICATION_ROOT_DEFAULT);
        
        webappRoot = context.getRealPath("/");
        // log.info("Web Application root is " + webappRoot);
        // log.info("Application root is "     + applicationRoot);

        if (applicationRoot == null || applicationRoot.equals(TurbineConstants.WEB_CONTEXT))
        {
            applicationRoot = webappRoot;
            // log.info("got empty or 'webContext' Application root. Application root now: " + applicationRoot);
        }

        // Set the applicationRoot for this webapp.
        setApplicationRoot(applicationRoot);

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
        

        Path confPath =  configureApplication( config, context );

        configureLogging(confPath);
        
        //
        // Logging with log4j 2 is done via convention, finding in path
   
        setTurbineServletConfig(config);
        setTurbineServletContext(context);

        getServiceManager().setApplicationRoot(applicationRoot);

        // We want to set a few values in the configuration so
        // that ${variable} interpolation will work for
        //
        // ${applicationRoot}
        // ${webappRoot}
        configuration.setProperty(TurbineConstants.APPLICATION_ROOT_KEY, applicationRoot);
        configuration.setProperty(TurbineConstants.WEBAPP_ROOT_KEY, webappRoot);

        getServiceManager().setConfiguration(configuration);

        // Initialize the service manager. Services
        // that have its 'earlyInit' property set to
        // a value of 'true' will be started when
        // the service manager is initialized.
        getServiceManager().init();

        // Retrieve the pipeline class and then initialize it.  The pipeline
        // handles the processing of a webrequest/response cycle.
        String descriptorPath =
            configuration.getString(
              "pipeline.default.descriptor",
                      TurbinePipeline.CLASSIC_PIPELINE);

        if (log.isDebugEnabled())
        {
            log.debug("Using descriptor path: " + descriptorPath);
        }

        // context resource path has to begin with slash, cft. context.getResource
        if (!descriptorPath.startsWith( "/" ))
        {
            descriptorPath  = "/" + descriptorPath;
        }

        try (InputStream reader = context.getResourceAsStream(descriptorPath))
        {
            JAXBContext jaxb = JAXBContext.newInstance(TurbinePipeline.class);
            Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            pipeline = (Pipeline) unmarshaller.unmarshal(reader);
        }

        log.debug("Initializing pipeline");

        pipeline.initialize();
    }

    /**
     * Checks configuraton style, resolves the location of the configuration and loads it to 
     * internal {@link Configuration} object ({@link #configuration}).
     *  
     * @param config the Servlet Configuration
     * @param context Servlet Context
     * @return The resolved Configuration Path 
     * @throws IOException if configuration path not found
     * @throws ConfigurationException if failed to configure
     */
    protected Path configureApplication( ServletConfig config, ServletContext context )
        throws IOException, ConfigurationException
    {
        ConfigurationStyle confStyle = ConfigurationStyle.UNSET;
        // first test
        String confFile= findInitParameter(context, config,
                TurbineConfig.CONFIGURATION_PATH_KEY,
                null);
        if (StringUtils.isNotEmpty(confFile))
        {
            confStyle = ConfigurationStyle.XML;
        }
        else // second test
        {
            confFile = findInitParameter(context, config,
                    TurbineConfig.PROPERTIES_PATH_KEY,
                                         null);
            if (StringUtils.isNotEmpty((confFile)) )
            {
                confStyle = ConfigurationStyle.PROPERTIES;
            }
        }
        // more tests ..
        // last test
        if (confStyle == ConfigurationStyle.UNSET)
        {  // last resort
             confFile = findInitParameter(context, config,
                    TurbineConfig.PROPERTIES_PATH_KEY,
                    TurbineConfig.PROPERTIES_PATH_DEFAULT);
             confStyle = ConfigurationStyle.PROPERTIES;
        }
        
        // First report
        log.debug("Loading configuration (" + confStyle + ") from " + confFile);

        // now begin loading
        Parameters params = new Parameters();
        File confPath = getApplicationRootAsFile(); 

        if (confFile.startsWith( "/" ))
        {
            confFile = confFile.substring( 1 ); // cft. RFC2396 should not start with a slash, if not absolute path
        }

        Path confFileRelativePath =  Paths.get( confFile );// relative to later join
        Path targetPath = Paths.get( confPath.toURI() );
        targetPath = targetPath.resolve( confFileRelativePath );

        // Get the target path directory
        Path targetPathDirectory = targetPath.getParent();
        if ( targetPathDirectory != null )
        {
            // set the configuration path
            confPath = targetPathDirectory.normalize().toFile();

            Path targetFilePath = targetPath.getFileName();
            if ( targetFilePath != null )
            {
                // set the configuration file name
                confFile = targetFilePath.toString();
            }
            
        }

        switch (confStyle)
        {
            case XML:
                // relative base path used for this and child configuration files
                CombinedConfigurationBuilder combinedBuilder = new CombinedConfigurationBuilder()
                    .configure(params.fileBased()
                        .setFileName(confFile)
                        .setListDelimiterHandler(new DefaultListDelimiterHandler(','))
                        .setLocationStrategy(new HomeDirectoryLocationStrategy(confPath.getCanonicalPath(), false)));
                configuration = combinedBuilder.getConfiguration();
                break;

            case PROPERTIES:
                FileBasedConfigurationBuilder<PropertiesConfiguration> propertiesBuilder =
                                new FileBasedConfigurationBuilder<>(
                                                PropertiesConfiguration.class)
                    .configure(params.properties()
                        .setFileName(confFile)
                        .setListDelimiterHandler(new DefaultListDelimiterHandler(','))
                        .setLocationStrategy(new HomeDirectoryLocationStrategy(confPath.getCanonicalPath(), false)));
                configuration = propertiesBuilder.getConfiguration();
                break;
            case JSON: case YAML:
                throw new NotImplementedException("JSON or XAML configuration style not yet implemented!");

            default:
                break;
        }
        // Now report our successful configuration to the world
        log.info("Loaded configuration (" + confStyle + ") from " + confFile + " style: " + configuration.toString());

        return targetPath;
    }

    /**
     * Finds the specified servlet configuration/initialization
     * parameter, looking first for a servlet-specific parameter, then
     * for a global parameter, and using the provided default if not
     * found.
     * 
     * @param context the servlet context
     * @param config configuration object
     * @param name name of parameter
     * @param defaultValue of the parameter
     * @return String value of the parameter
     */
    protected String findInitParameter(ServletContext context,
            ServletConfig config, String name, String defaultValue)
    {
        String path = null;

        // Try the name as provided first.
        boolean usingNamespace = name.startsWith(TurbineConstants.CONFIG_NAMESPACE);
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
                        name = TurbineConstants.CONFIG_NAMESPACE + '.' + name;
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
     * Initializes the services which need <code>PipelineData</code> to
     * initialize themselves (post startup).
     *
     * @param data The first <code>GET</code> request.
     */
    public void init(PipelineData data)
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

                // Initialize services with the PipelineData instance
                TurbineServices services = (TurbineServices)getServiceManager();

                for (Iterator<String> i = services.getServiceNames(); i.hasNext();)
                {
                    String serviceName = i.next();
                    Object service = services.getService(serviceName);

                    if (service instanceof Initable)
                    {
                        try
                        {
                            ((Initable)service).init(data);
                        }
                        catch (InitializationException e)
                        {
                            log.warn("Could not initialize Initable " + serviceName + " with PipelineData", e);
                        }
                    }
                }

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
     * if you're running in a clustered structure. You can provide default
     * values in your configuration for cases where access is requied before
     * your application is first accessed by a user.  This might be used
     * if you need a DataURI and have no RunData object handy.
     *
     * @return An initialized ServerData object
     */
    public static ServerData getDefaultServerData()
    {
        if (serverData == null)
        {
            String serverName
                    = configuration.getString(TurbineConstants.DEFAULT_SERVER_NAME_KEY);
            if (serverName == null)
            {
                log.error("ServerData Information requested from Turbine before first request!");
            }
            else
            {
                log.info("ServerData Information retrieved from configuration.");
            }
            // Will be overwritten once the first request is run;
            serverData = new ServerData(serverName,
                    configuration.getInt(TurbineConstants.DEFAULT_SERVER_PORT_KEY,
                            URIConstants.HTTP_PORT),
                    configuration.getString(TurbineConstants.DEFAULT_SERVER_SCHEME_KEY,
                            URIConstants.HTTP),
                    configuration.getString(TurbineConstants.DEFAULT_SCRIPT_NAME_KEY),
                    configuration.getString(TurbineConstants.DEFAULT_CONTEXT_PATH_KEY));
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
    @Override
    public void destroy()
    {
        // Shut down all Turbine Services.
        getServiceManager().shutdownServices();

        firstInit = true;
        firstDoGet = true;
        log.info("Turbine: Done shutting down!");
    }

    /**
     * The primary method invoked when the Turbine servlet is executed.
     *
     * @param req Servlet request.
     * @param res Servlet response.
     * @throws IOException a servlet exception.
     * @throws ServletException a servlet exception.
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException
    {
        // Check to make sure that we started up properly.
        if (initFailure != null)
        {
            handleHorribleException(res, initFailure);
            return;
        }

        // Get general PipelineData here...
        try (PipelineData pipelineData = getRunDataService().getRunData(req, res, getServletConfig()))
        {
            try
            {
                // Perform turbine specific initialization below.
                Map<Class<?>, Object> runDataMap = new HashMap<Class<?>, Object>();
                runDataMap.put(RunData.class, pipelineData);
                // put the data into the pipeline
                pipelineData.put(RunData.class, runDataMap);

                // If this is the first invocation, perform some
                // initialization.  Certain services need RunData to initialize
                // themselves.
                if (firstDoGet)
                {
                    init(pipelineData);
                }

                // Stages of Pipeline implementation execution
                // configurable via attached Valve implementations in a
                // XML properties file.
                pipeline.invoke(pipelineData);
            }
            catch (Throwable t)
            {
                handleException(pipelineData, res, t);
            }
        }
        catch (Throwable t)
        {
            handleHorribleException(res, t);
        }
    }

    /**
     * In this application doGet and doPost are the same thing.
     *
     * @param req Servlet request.
     * @param res Servlet response.
     * @throws IOException a servlet exception.
     * @throws ServletException a servlet exception.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException
    {
        doGet(req, res);
    }

    /**
     * Return the servlet info.
     *
     * @return a string with the servlet information.
     */
    @Override
    public String getServletInfo()
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
     * @param pipelineData A Turbine PipelineData object.
     * @param res Servlet response.
     * @param t The exception to report.
     */
    protected void handleException(PipelineData pipelineData, HttpServletResponse res,
                                       Throwable t)
    {
        RunData data = (RunData) pipelineData;
        // make sure that the stack trace makes it the log
        log.error("Turbine.handleException: ", t);

        try
        {
            // This is where we capture all exceptions and show the
            // Error Screen.
            data.setStackTrace(ExceptionUtils.getStackTrace(t), t);

            // setup the screen
            data.setScreen(configuration.getString(
                    TurbineConstants.SCREEN_ERROR_KEY,
                    TurbineConstants.SCREEN_ERROR_DEFAULT));

            // do more screen setup for template execution if needed
            if (data.getTemplateInfo() != null)
            {
                data.getTemplateInfo()
                    .setScreenTemplate(configuration.getString(
                            TurbineConstants.TEMPLATE_ERROR_KEY,
                            TurbineConstants.TEMPLATE_ERROR_VM));
            }

            // Make sure to not execute an action.
            data.setAction("");

            PageLoader.getInstance().exec(pipelineData,
                    configuration.getString(TurbineConstants.PAGE_DEFAULT_KEY,
                            TurbineConstants.PAGE_DEFAULT_DEFAULT));

            data.getResponse().setContentType(data.getContentType());
            data.getResponse().setStatus(data.getStatusCode());
        }
        // Attempt to do *something* at this point...
        catch (Throwable reallyScrewedNow)
        {
            handleHorribleException(res, reallyScrewedNow);
        }
    }

    /**
     * This method handles exception cases where no PipelineData object exists
     *
     * @param res Servlet response.
     * @param t The exception to report.
     */
    protected void handleHorribleException(HttpServletResponse res, Throwable t)
    {
        try
        {
            res.setContentType( TurbineConstants.DEFAULT_TEXT_CONTENT_TYPE );
            res.setStatus(200);
            PrintWriter writer = res.getWriter();
            writer.println("Horrible Exception: ");
            t.printStackTrace(writer);
        }
        catch (Exception ignored)
        {
            // ignore
        }

        log.error(t.getMessage(), t);
    }

    /**
     * Save some information about this servlet so that
     * it can be utilized by object instances that do not
     * have direct access to PipelineData.
     *
     * @param data Turbine request data
     */
    public static synchronized void saveServletInfo(PipelineData data)
    {
        // Store the context path for tools like ContentURI and
        // the UIManager that use webapp context path information
        // for constructing URLs.

        //
        // Bundle all the information above up into a convenient structure
        //
        ServerData requestServerData = data.get(Turbine.class, ServerData.class);
        serverData = (ServerData) requestServerData.clone();
    }

    /**
     * Checks Log4j 2 Context, loads log4File, if configured and configuration is not already located.
     * @param logConf Configuration file path
     * @throws IOException if path not found
     */
    protected void configureLogging(Path logConf) throws IOException
    {   
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        
        if (context.getConfiguration().getConfigurationSource().getLocation() == null) {
            Path log4jFile = resolveLog4j2( logConf.getParent() );
            // configured + no other log4j configuration already found
            if (log4jFile != null) {
                LogManager.getContext(null, false, log4jFile.toUri());
            }
        }
        log.info( "resolved log4j2 location: {}", context.getConfiguration().getConfigurationSource().getLocation() );
    }
    
    /**
     * Check {@value TurbineConstants#LOG4J2_CONFIG_FILE} in Turbine configuration.
     * 
     * @param logConfPath configuration directory
     * @return Resolved log4j2 {@link Path} or null, if not found or configured "none".
     */
    protected Path resolveLog4j2( Path logConfPath )
    {
        String log4jFile = configuration.getString(TurbineConstants.LOG4J2_CONFIG_FILE,
                                                   TurbineConstants.LOG4J2_CONFIG_FILE_DEFAULT);
                        
        if (log4jFile.startsWith( "/" ))
        {
            log4jFile = log4jFile.substring( 1 );
        }
        Path log4jTarget = null;
        if (StringUtils.isNotEmpty(log4jFile) && !log4jFile.equalsIgnoreCase("none")) {
            // log4j must either share path with configuration path or resolved relatively
            
            if ( logConfPath != null )
            {
                Path log4jFilePath = Paths.get(log4jFile);
                Path logFilePath = logConfPath.resolve( log4jFilePath );
                if ( logFilePath != null && logFilePath.toFile().exists() )
                {
                    log4jTarget = logFilePath.normalize();
                } else {
                    // fall back just using the filename, if path match
                    if (log4jFilePath != null && log4jFilePath.getParent() != null && logConfPath.endsWith(log4jFilePath.getParent() )) {
                        logFilePath = logConfPath.resolve( log4jFilePath.getFileName());
                        if ( logFilePath != null && logFilePath.toFile().exists() ) {
                            log4jTarget = logFilePath.normalize();
                        }
                    }
                }
            }
        }
        return log4jTarget;
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
     * Get the application root for this Turbine webapp.
     *
     * @return String applicationRoot
     */
    public static String getApplicationRoot()
    {
        return applicationRoot;
    }

    /**
     * Get the application root for this Turbine webapp as a
     * file object.
     *
     * @return File applicationRootFile
     */
    public static File getApplicationRootAsFile()
    {
        return new File(applicationRoot);
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
            return new File(getApplicationRootAsFile(), path.substring(1)).getAbsolutePath();
        }

        return new File(getApplicationRootAsFile(), path).getAbsolutePath();
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

    /**
     * Returns the default input encoding for the servlet.
     *
     * @return the default input encoding.
     *
     * @deprecated Use {@link org.apache.turbine.pipeline.DefaultSetEncodingValve} to set default encoding
     */
    @Deprecated
    public static String getDefaultInputEncoding()
    {
        return LocaleUtils.getDefaultInputEncoding();
    }

    /**
     * Static Helper method for looking up the RunDataService
     * @return A RunDataService
     */
    private RunDataService getRunDataService()
    {
        return (RunDataService) getServiceManager().getService(RunDataService.SERVICE_NAME);
    }
}
