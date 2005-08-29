package org.apache.turbine.services.velocity;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletConfig;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.log.SimpleLog4JLogSystem;

import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.pull.PullService;
import org.apache.turbine.services.pull.TurbinePull;
import org.apache.turbine.services.template.BaseTemplateEngineService;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * This is a Service that can process Velocity templates from within a
 * Turbine Screen. It is used in conjunction with the templating service
 * as a Templating Engine for templates ending in "vm". It registers
 * itself as translation engine with the template service and gets
 * accessed from there. After configuring it in your properties, it
 * should never be necessary to call methods from this service directly.
 *
 * Here's an example of how you might use it from a
 * screen:<br>
 *
 * <code>
 * Context context = TurbineVelocity.getContext(data);<br>
 * context.put("message", "Hello from Turbine!");<br>
 * String results = TurbineVelocity.handleRequest(context,"helloWorld.vm");<br>
 * data.getPage().getBody().addElement(results);<br>
 * </code>
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:sean@informage.ent">Sean Legassick</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TurbineVelocityService
        extends BaseTemplateEngineService
        implements VelocityService,
                   MethodExceptionEventHandler
{
    /** The generic resource loader path property in velocity.*/
    private static final String RESOURCE_LOADER_PATH = ".resource.loader.path";

    /** Default character set to use if not specified in the RunData object. */
    private static final String DEFAULT_CHAR_SET = "ISO-8859-1";

    /** The prefix used for URIs which are of type <code>jar</code>. */
    private static final String JAR_PREFIX = "jar:";

    /** The prefix used for URIs which are of type <code>absolute</code>. */
    private static final String ABSOLUTE_PREFIX = "file://";

    /** Logging */
    private static Log log = LogFactory.getLog(TurbineVelocityService.class);

    /** Is the pullModelActive? */
    private boolean pullModelActive = false;

    /** Shall we catch Velocity Errors and report them in the log file? */
    private boolean catchErrors = true;

    /** Internal Reference to the pull Service */
    private PullService pullService = null;


    /**
     * Load all configured components and initialize them. This is
     * a zero parameter variant which queries the Turbine Servlet
     * for its config.
     *
     * @throws InitializationException Something went wrong in the init
     *         stage
     */
    public void init()
            throws InitializationException
    {
        try
        {
            initVelocity();

            // We can only load the Pull Model ToolBox
            // if the Pull service has been listed in the TR.props
            // and the service has successfully been initialized.
            if (TurbinePull.isRegistered())
            {
                pullModelActive = true;

                pullService = TurbinePull.getService();

                log.debug("Activated Pull Tools");
            }

            // Register with the template service.
            registerConfiguration(VelocityService.VELOCITY_EXTENSION);

            setInit(true);
        }
        catch (Exception e)
        {
            throw new InitializationException(
                "Failed to initialize TurbineVelocityService", e);
        }
    }


    /**
     * Inits the service using servlet parameters to obtain path to the
     * configuration file.
     *
     * @param config The ServletConfiguration from Turbine
     *
     * @throws InitializationException Something went wrong when starting up.
     * @deprecated use init() instead.
     */
    public void init(ServletConfig config)
            throws InitializationException
    {
        init();
    }


    /**
     * Create a Context object that also contains the globalContext.
     *
     * @return A Context object.
     */
    public Context getContext()
    {
        Context globalContext =
                pullModelActive ? pullService.getGlobalContext() : null;

        Context ctx = new VelocityContext(globalContext);
        return ctx;
    }

    /**
     * This method returns a new, empty Context object.
     *
     * @return A Context Object.
     */
    public Context getNewContext()
    {
        Context ctx = new VelocityContext();

        // Attach an Event Cartridge to it, so we get exceptions
        // while invoking methods from the Velocity Screens
        EventCartridge ec = new EventCartridge();
        ec.addEventHandler(this);
        ec.attachToContext(ctx);
        return ctx;
    }

    /**
     * MethodException Event Cartridge handler
     * for Velocity.
     *
     * It logs an execption thrown by the velocity processing
     * on error level into the log file
     *
     * @param clazz The class that threw the exception
     * @param method The Method name that threw the exception
     * @param e The exception that would've been thrown
     * @return A valid value to be used as Return value
     * @throws Exception We threw the exception further up
     */
    public Object methodException(Class clazz, String method, Exception e)
            throws Exception
    {
        log.error("Class " + clazz.getName() + "." + method + " threw Exception", e);

        if (!catchErrors)
        {
            throw e;
        }

        return "[Turbine caught an Error here. Look into the turbine.log for further information]";
    }

    /**
     * Create a Context from the RunData object.  Adds a pointer to
     * the RunData object to the VelocityContext so that RunData
     * is available in the templates.
     *
     * @param data The Turbine RunData object.
     * @return A clone of the WebContext needed by Velocity.
     */
    public Context getContext(RunData data)
    {
        // Attempt to get it from the data first.  If it doesn't
        // exist, create it and then stuff it into the data.
        Context context = (Context)
            data.getTemplateInfo().getTemplateContext(VelocityService.CONTEXT);

        if (context == null)
        {
            context = getContext();
            context.put(VelocityService.RUNDATA_KEY, data);

            if (pullModelActive)
            {
                // Populate the toolbox with request scope, session scope
                // and persistent scope tools (global tools are already in
                // the toolBoxContent which has been wrapped to construct
                // this request-specific context).
                pullService.populateContext(context, data);
            }

            data.getTemplateInfo().setTemplateContext(
                VelocityService.CONTEXT, context);
        }
        return context;
    }

    /**
     * Process the request and fill in the template with the values
     * you set in the Context.
     *
     * @param context  The populated context.
     * @param filename The file name of the template.
     * @return The process template as a String.
     *
     * @throws TurbineException Any exception trown while processing will be
     *         wrapped into a TurbineException and rethrown.
     */
    public String handleRequest(Context context, String filename)
        throws TurbineException
    {
        String results = null;
        ByteArrayOutputStream bytes = null;
        OutputStreamWriter writer = null;
        String charset = getCharSet(context);

        try
        {
            bytes = new ByteArrayOutputStream();

            writer = new OutputStreamWriter(bytes, charset);

            executeRequest(context, filename, writer);
            writer.flush();
            results = bytes.toString(charset);
        }
        catch (Exception e)
        {
            renderingError(filename, e);
        }
        finally
        {
            try
            {
                if (bytes != null)
                {
                    bytes.close();
                }
            }
            catch (IOException ignored)
            {
                // do nothing.
            }
        }
        return results;
    }

    /**
     * Process the request and fill in the template with the values
     * you set in the Context.
     *
     * @param context A Context.
     * @param filename A String with the filename of the template.
     * @param output A OutputStream where we will write the process template as
     * a String.
     *
     * @throws TurbineException Any exception trown while processing will be
     *         wrapped into a TurbineException and rethrown.
     */
    public void handleRequest(Context context, String filename,
                              OutputStream output)
            throws TurbineException
    {
        String charset  = getCharSet(context);
        OutputStreamWriter writer = null;

        try
        {
            writer = new OutputStreamWriter(output, charset);
            executeRequest(context, filename, writer);
        }
        catch (Exception e)
        {
            renderingError(filename, e);
        }
        finally
        {
            try
            {
                if (writer != null)
                {
                    writer.flush();
                }
            }
            catch (Exception ignored)
            {
                // do nothing.
            }
        }
    }


    /**
     * Process the request and fill in the template with the values
     * you set in the Context.
     *
     * @param context A Context.
     * @param filename A String with the filename of the template.
     * @param writer A Writer where we will write the process template as
     * a String.
     *
     * @throws TurbineException Any exception trown while processing will be
     *         wrapped into a TurbineException and rethrown.
     */
    public void handleRequest(Context context, String filename, Writer writer)
            throws TurbineException
    {
        try
        {
            executeRequest(context, filename, writer);
        }
        catch (Exception e)
        {
            renderingError(filename, e);
        }
        finally
        {
            try
            {
                if (writer != null)
                {
                    writer.flush();
                }
            }
            catch (Exception ignored)
            {
                // do nothing.
            }
        }
    }


    /**
     * Process the request and fill in the template with the values
     * you set in the Context. Apply the character and template
     * encodings from RunData to the result.
     *
     * @param context A Context.
     * @param filename A String with the filename of the template.
     * @param writer A OutputStream where we will write the process template as
     * a String.
     *
     * @throws Exception A problem occured.
     */
    private void executeRequest(Context context, String filename,
                                Writer writer)
            throws Exception
    {
        String encoding = getEncoding(context);

        if (encoding != null)
        {
            Velocity.mergeTemplate(filename, encoding, context, writer);
        }
        else
        {
            Velocity.mergeTemplate(filename, context, writer);
        }
    }

    /**
     * Retrieve the required charset from the Turbine RunData in the context
     *
     * @param context A Context.
     * @return The character set applied to the resulting String.
     */
    private String getCharSet(Context context)
    {
        String charset = null;

        Object data = context.get(VelocityService.RUNDATA_KEY);
        if ((data != null) && (data instanceof RunData))
        {
            charset = ((RunData) data).getCharSet();
        }

        return (StringUtils.isEmpty(charset)) ? DEFAULT_CHAR_SET : charset;
    }

    /**
     * Retrieve the required encoding from the Turbine RunData in the context
     *
     * @param context A Context.
     * @return The encoding applied to the resulting String.
     */
    private String getEncoding(Context context)
    {
        String encoding = null;

        Object data = context.get(VelocityService.RUNDATA_KEY);
        if ((data != null) && (data instanceof RunData))
        {
            encoding = ((RunData) data).getTemplateEncoding();
        }

        return encoding;
    }

    /**
     * Macro to handle rendering errors.
     *
     * @param filename The file name of the unrenderable template.
     * @param e        The error.
     *
     * @exception TurbineException Thrown every time.  Adds additional
     *                             information to <code>e</code>.
     */
    private static final void renderingError(String filename, Exception e)
            throws TurbineException
    {
        String err = "Error rendering Velocity template: " + filename;
        log.error(err, e);
        throw new TurbineException(err, e);
    }

    /**
     * Setup the velocity runtime by using a subset of the
     * Turbine configuration which relates to velocity.
     *
     * @exception Exception An Error occured.
     */
    private synchronized void initVelocity()
        throws Exception
    {
        // Get the configuration for this service.
        Configuration conf = getConfiguration();

        catchErrors = conf.getBoolean(CATCH_ERRORS_KEY, CATCH_ERRORS_DEFAULT);

        conf.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS,
                SimpleLog4JLogSystem.class.getName());
        conf.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM
                + ".log4j.category", "velocity");

        Velocity.setExtendedProperties(createVelocityProperties(conf));
        Velocity.init();
    }


    /**
     * This method generates the Extended Properties object necessary
     * for the initialization of Velocity. It also converts the various
     * resource loader pathes into webapp relative pathes. It also
     *
     * @param conf The Velocity Service configuration
     *
     * @return An ExtendedProperties Object for Velocity
     *
     * @throws Exception If a problem occured while converting the properties.
     */

    public ExtendedProperties createVelocityProperties(Configuration conf)
            throws Exception
    {
        // This bugger is public, because we want to run some Unit tests
        // on it.

        ExtendedProperties veloConfig = new ExtendedProperties();

        // Fix up all the template resource loader pathes to be
        // webapp relative. Copy all other keys verbatim into the
        // veloConfiguration.

        for (Iterator i = conf.getKeys(); i.hasNext();)
        {
            String key = (String) i.next();
            if (!key.endsWith(RESOURCE_LOADER_PATH))
            {
                Object value = conf.getProperty(key);

                // Since 1.0-pre-something, Commons Collections suddently
                // no longer returns a vector for multiple value-keys but a
                // List object. Velocity will choke if we add this object because
                // org.apache.commons.collections.ExtendedProperties expect a
                // Vector object. Ah, the joys of incompatible class changes,
                // unwritten assumptions and general Java JAR Hell... =8-O
                if (value instanceof List)
                {
                    List srcValue = (List) value;
                    Vector targetValue = new Vector(srcValue.size());

                    for (Iterator it = srcValue.iterator(); it.hasNext(); )
                    {
                        targetValue.add(it.next());
                    }

                    veloConfig.addProperty(key, targetValue);
                }
                else
                {
                    veloConfig.addProperty(key, value);
                }

                continue; // for()
            }

            List paths = conf.getList(key, null);
            if (paths == null)
            {
                // We don't copy this into VeloProperties, because
                // null value is unhealthy for the ExtendedProperties object...
                continue; // for()
            }

            Velocity.clearProperty(key);

            // Translate the supplied pathes given here.
            // the following three different kinds of
            // pathes must be translated to be webapp-relative
            //
            // jar:file://path-component!/entry-component
            // file://path-component
            // path/component

            for (Iterator j = paths.iterator(); j.hasNext();)
            {
                String path = (String) j.next();

                log.debug("Translating " + path);

                if (path.startsWith(JAR_PREFIX))
                {
                    // skip jar: -> 4 chars
                    if (path.substring(4).startsWith(ABSOLUTE_PREFIX))
                    {
                        // We must convert up to the jar path separator
                        int jarSepIndex = path.indexOf("!/");

                        // jar:file:// -> skip 11 chars
                        path = (jarSepIndex < 0)
                            ? Turbine.getRealPath(path.substring(11))
                        // Add the path after the jar path separator again to the new url.
                            : (Turbine.getRealPath(path.substring(11, jarSepIndex)) + path.substring(jarSepIndex));

                        log.debug("Result (absolute jar path): " + path);
                    }
                }
                else if(path.startsWith(ABSOLUTE_PREFIX))
                {
                    // skip file:// -> 7 chars
                    path = Turbine.getRealPath(path.substring(7));

                    log.debug("Result (absolute URL Path): " + path);
                }
                // Test if this might be some sort of URL that we haven't encountered yet.
                else if(path.indexOf("://") < 0)
                {
                    path = Turbine.getRealPath(path);

                    log.debug("Result (normal fs reference): " + path);
                }

                log.debug("Adding " + key + " -> " + path);
                // Re-Add this property to the configuration object
                veloConfig.addProperty(key, path);
            }
        }
        return veloConfig;
    }

    /**
     * Find out if a given template exists. Velocity
     * will do its own searching to determine whether
     * a template exists or not.
     *
     * @param template String template to search for
     * @return True if the template can be loaded by Velocity
     */
    public boolean templateExists(String template)
    {
        return Velocity.templateExists(template);
    }

    /**
     * Performs post-request actions (releases context
     * tools back to the object pool).
     *
     * @param context a Velocity Context
     */
    public void requestFinished(Context context)
    {
        if (pullModelActive)
        {
            pullService.releaseTools(context);
        }
    }
}
