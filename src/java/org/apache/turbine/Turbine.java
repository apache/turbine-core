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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
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
import org.apache.log4j.PropertyConfigurator;
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
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.ServerData;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.uri.URIConstants;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

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
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id$
 */
public class Turbine
        extends HttpServlet
{
    /** Serialversion */
    private static final long serialVersionUID = -6317118078613623990L;

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

    /** Default Input encoding if the servlet container does not report an encoding */
    private String inputEncoding = null;

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

                TemplateService templateService = TurbineTemplate.getService();
                if (templateService == null)
                {
                    throw new TurbineException(
                            "No Template Service configured!");
                }

                if (getRunDataService() == null)
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

        // Set the application root. This defaults to the webapp
        // context if not otherwise set. This is to allow 2.1 apps
        // to be developed from CVS. This feature will carry over
        // into 3.0.
        applicationRoot = findInitParameter(context, config,
                TurbineConstants.APPLICATION_ROOT_KEY,
                TurbineConstants.APPLICATION_ROOT_DEFAULT);

        webappRoot = config.getServletContext().getRealPath("/");
        // log.info("Web Application root is " + webappRoot);
        // log.info("Application root is "     + applicationRoot);

        if (applicationRoot == null || applicationRoot.equals(TurbineConstants.WEB_CONTEXT))
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
            configuration = new PropertiesConfiguration(confPath);
            confStyle = "Properties";
        }


        //
        // Set up logging as soon as possible
        //
        String log4jFile = configuration.getString(TurbineConstants.LOG4J_CONFIG_FILE,
                                                   TurbineConstants.LOG4J_CONFIG_FILE_DEFAULT);

        if (StringUtils.isNotEmpty(log4jFile) &&
                !log4jFile.equalsIgnoreCase("none"))
        {
            log4jFile = getRealPath(log4jFile);
    
            //
            // Load the config file above into a Properties object and
            // fix up the Application root
            //
            Properties p = new Properties();
            try
            {
                p.load(new FileInputStream(log4jFile));
                p.setProperty(TurbineConstants.APPLICATION_ROOT_KEY, getApplicationRoot());
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
        configuration.setProperty(TurbineConstants.APPLICATION_ROOT_KEY, applicationRoot);
        configuration.setProperty(TurbineConstants.WEBAPP_ROOT_KEY, webappRoot);

        // Get the default input encoding
        inputEncoding = configuration.getString(
                TurbineConstants.PARAMETER_ENCODING_KEY,
                TurbineConstants.PARAMETER_ENCODING_DEFAULT);

        if (log.isDebugEnabled())
        {
            log.debug("Input Encoding has been set to " + inputEncoding);
        }        
        
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

        descriptorPath = getRealPath(descriptorPath);

  		log.debug("Using descriptor path: " + descriptorPath);
        Reader reader = new BufferedReader(new FileReader(descriptorPath));
        XStream pipelineMapper = new XStream(new DomDriver()); // does not require XPP3 library
        pipeline = (Pipeline) pipelineMapper.fromXML(reader);

	  	log.debug("Initializing pipeline");

	  	pipeline.initialize();
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
                                        TurbineConstants.LOGGING_ROOT_KEY,
                                        TurbineConstants.LOGGING_ROOT_DEFAULT);

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
    public final void init(PipelineData data)
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
                TurbineServices services = (TurbineServices)TurbineServices.getInstance();
                
                for (Iterator i = services.getServiceNames(); i.hasNext();)
                {
                	String serviceName = (String)i.next();
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
        PipelineData pipelineData = null;

        try
        {
            // Check to make sure that we started up properly.
            if (initFailure != null)
            {
                throw initFailure;
            }

            //
            // If the servlet container gives us no clear indication about the
            // Encoding of the contents, set it to our default value.
            if (req.getCharacterEncoding() == null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Changing Input Encoding to " + inputEncoding);
                }

                try
                {
                    req.setCharacterEncoding(inputEncoding);
                }
                catch (UnsupportedEncodingException uee)
                {
                    log.warn("Could not change request encoding to " + inputEncoding, uee);
                }
            }
            
            // Get general RunData here...
            // Perform turbine specific initialization below.
            pipelineData = getRunDataService().getRunData(req, res, getServletConfig());
           // Map runDataMap = new HashMap();
            //runDataMap.put(RunData.class, data);
            // put the data into the pipeline
           // pipelineData.put(RunData.class, runDataMap);

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
        catch (Exception e)
        {
            handleException(pipelineData, res, e);
        }
        catch (Throwable t)
        {
            handleException(pipelineData, res, t);
        }
        finally
        {
            // Return the used RunData to the factory for recycling.
            getRunDataService().putRunData((RunData)pipelineData);
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
     * @param data A Turbine PipelineData object.
     * @param res Servlet response.
     * @param t The exception to report.
     */
    private final void handleException(PipelineData pipelineData, HttpServletResponse res,
                                       Throwable t)
    {
        RunData data = getRunData(pipelineData);
        // make sure that the stack trace makes it the log
        log.error("Turbine.handleException: ", t);

        String mimeType = "text/plain";
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
                // ignore
            }

            try
            {
				data.getResponse().getWriter().print("java.lang.NoSuchFieldError: "
                        + "Please recompile all of your source code.");
            }
            catch (IOException ignored)
            {
                // ignore
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
                // ignore
            }

            log.error(reallyScrewedNow.getMessage(), reallyScrewedNow);
        }
    }

    /**
     * Save some information about this servlet so that
     * it can be utilized by object instances that do not
     * have direct access to RunData.
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
        ServerData requestServerData = (ServerData) data.get(Turbine.class, ServerData.class);
        serverData = (ServerData) requestServerData.clone();
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

    /**
     * Get a RunData from the pipelineData. Once RunData is fully replaced
     * by PipelineData this should not be required.
     * @param pipelineData
     * @return
     */
    private RunData getRunData(PipelineData pipelineData)
    {
        RunData data = null;
        data = (RunData)pipelineData;
        return data;
    }


    /**
     * Returns the default input encoding for the servlet.
     * 
     * @return the default input encoding.
     */
    public String getDefaultInputEncoding() {
        return inputEncoding;
    }
    
    /**
     * Static Helper method for looking up the RunDataService
     * @return A RunDataService
     */
    private static RunDataService getRunDataService()
    {
        return (RunDataService) TurbineServices
        .getInstance().getService(RunDataService.SERVICE_NAME);
    }
}
