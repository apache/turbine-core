package org.apache.turbine.services.velocity;


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


import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.Turbine;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.pull.PullService;
import org.apache.turbine.services.template.BaseTemplateEngineService;
import org.apache.turbine.util.LocaleUtils;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.util.introspection.Info;

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
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class TurbineVelocityService
        extends BaseTemplateEngineService
        implements VelocityService,
                   MethodExceptionEventHandler
{
    /** The generic resource loader path property in velocity.*/
    private static final String RESOURCE_LOADER_PATH = ".resource.loader.path";

    /** The prefix used for URIs which are of type <code>jar</code>. */
    private static final String JAR_PREFIX = "jar:";

    /** The prefix used for URIs which are of type <code>absolute</code>. */
    private static final String ABSOLUTE_PREFIX = "file://";

    /** Logging */
    private static final Logger log = LogManager.getLogger(TurbineVelocityService.class);

    /** Encoding used when reading the templates. */
    private Charset defaultInputEncoding;

    /** Encoding used by the outputstream when handling the requests. */
    private Charset defaultOutputEncoding;

    /** Is the pullModelActive? */
    private boolean pullModelActive = false;

    /** Shall we catch Velocity Errors and report them in the log file? */
    private boolean catchErrors = true;

    /** Velocity runtime instance */
    private VelocityEngine velocity = null;

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
    @Override
    public void init()
            throws InitializationException
    {
        try
        {
            velocity = getInitializedVelocityEngine();

            // We can only load the Pull Model ToolBox
            // if the Pull service has been listed in the TR.props
            // and the service has successfully been initialized.
            if (TurbineServices.getInstance().isRegistered(PullService.SERVICE_NAME))
            {
                pullModelActive = true;
                pullService = (PullService)TurbineServices.getInstance().getService(PullService.SERVICE_NAME);

                log.debug("Activated Pull Tools");
            }

            // Register with the template service.
            registerConfiguration(VelocityService.VELOCITY_EXTENSION);

            String inputEncoding = getConfiguration().getString("input.encoding", LocaleUtils.getDefaultInputEncoding());
            defaultInputEncoding = Charset.forName(inputEncoding);

            String outputEncodingString = getConfiguration().getString("output.encoding");
            if (outputEncodingString == null)
            {
                Charset outputEncoding = LocaleUtils.getOverrideCharset();
                if (outputEncoding == null)
                {
                    outputEncoding = defaultInputEncoding;
                }
                
                defaultOutputEncoding = outputEncoding;
            }
            else
            {
                defaultOutputEncoding = Charset.forName(outputEncodingString);
            }

            setInit(true);
        }
        catch (Exception e)
        {
            throw new InitializationException(
                "Failed to initialize TurbineVelocityService", e);
        }
    }

    /**
     * Create a Context object that also contains the globalContext.
     *
     * @return A Context object.
     */
    @Override
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
    @Override
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
     * It logs an exception thrown by the velocity processing
     * on error level into the log file
     *
     * @param context The current context
     * @param clazz The class that threw the exception
     * @param method The Method name that threw the exception
     * @param e The exception that would've been thrown
     * @param info Information about the template, line and column the exception occurred
     * @return A valid value to be used as Return value
     */
    @Override
	public Object methodException(Context context, @SuppressWarnings("rawtypes") Class clazz, String method, Exception e, Info info)
    {
        log.error("Class {}.{} threw Exception", clazz.getName(), method, e);

        if (!catchErrors)
        {
            throw new RuntimeException(e);
        }

        return "[Turbine caught an Error in template " + info.getTemplateName()
            + ", l:" + info.getLine()
            + ", c:" + info.getColumn()
            + ". Look into the turbine.log for further information]";
    }

    /**
     * Create a Context from the PipelineData object.  Adds a pointer to
     * the PipelineData object to the VelocityContext so that PipelineData
     * is available in the templates.
     *
     * @param pipelineData The Turbine PipelineData object.
     * @return A clone of the WebContext needed by Velocity.
     */
    @Override
    public Context getContext(PipelineData pipelineData)
    {
        //Map runDataMap = (Map)pipelineData.get(RunData.class);
        RunData data = (RunData)pipelineData;
        // Attempt to get it from the data first.  If it doesn't
        // exist, create it and then stuff it into the data.
        Context context = (Context)
            data.getTemplateInfo().getTemplateContext(VelocityService.CONTEXT);

        if (context == null)
        {
            context = getContext();
            context.put(VelocityService.RUNDATA_KEY, data);
            // we will add both data and pipelineData to the context.
            context.put(VelocityService.PIPELINEDATA_KEY, pipelineData);

            if (pullModelActive)
            {
                // Populate the toolbox with request scope, session scope
                // and persistent scope tools (global tools are already in
                // the toolBoxContent which has been wrapped to construct
                // this request-specific context).
                pullService.populateContext(context, pipelineData);
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
     * @throws TurbineException Any exception thrown while processing will be
     *         wrapped into a TurbineException and rethrown.
     */
    @Override
    public String handleRequest(Context context, String filename)
        throws TurbineException
    {
        String results = null;
        OutputStreamWriter writer = null;
        Charset charset = getOutputCharSet(context);

        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream())
        {
            writer = new OutputStreamWriter(bytes, charset);

            executeRequest(context, filename, writer);
            writer.flush();
            results = bytes.toString(charset.name());
        }
        catch (Exception e)
        {
            renderingError(filename, e);
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
     * @throws TurbineException Any exception thrown while processing will be
     *         wrapped into a TurbineException and rethrown.
     */
    @Override
    public void handleRequest(Context context, String filename,
                              OutputStream output)
            throws TurbineException
    {
        Charset charset  = getOutputCharSet(context);

        try (OutputStreamWriter writer = new OutputStreamWriter(output, charset))
        {
            executeRequest(context, filename, writer);
        }
        catch (Exception e)
        {
            renderingError(filename, e);
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
     * @throws TurbineException Any exception thrown while processing will be
     *         wrapped into a TurbineException and rethrown.
     */
    @Override
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
     * @throws Exception A problem occurred.
     */
    private void executeRequest(Context context, String filename,
                                Writer writer)
            throws Exception
    {
        Charset encoding = getTemplateEncoding(context);

        if (encoding == null)
        {
          encoding = defaultOutputEncoding;
        }

		velocity.mergeTemplate(filename, encoding.name(), context, writer);
    }

    /**
     * Retrieve the required charset from the Turbine RunData in the context
     *
     * @param context A Context.
     * @return The character set applied to the resulting String.
     */
    private Charset getOutputCharSet(Context context)
    {
        Charset charset = null;

        Object data = context.get(VelocityService.RUNDATA_KEY);
        if ((data != null) && (data instanceof RunData))
        {
            charset = ((RunData) data).getCharset();
        }

        return charset == null ? defaultOutputEncoding : charset;
    }

    /**
     * Retrieve the required encoding from the Turbine RunData in the context
     *
     * @param context A Context.
     * @return The encoding applied to the resulting String.
     */
    private Charset getTemplateEncoding(Context context)
    {
        Charset encoding = null;

        Object data = context.get(VelocityService.RUNDATA_KEY);
        if ((data != null) && (data instanceof RunData) && (((RunData) data).getTemplateEncoding() != null) )
        {
            encoding = Charset.forName(((RunData) data).getTemplateEncoding());
        }

        return encoding != null ? encoding : defaultInputEncoding;
    }

    /**
     * Macro to handle rendering errors.
     *
     * @param filename The file name of the unrenderable template.
     * @param e        The error.
     *
     * @throws TurbineException Thrown every time.  Adds additional
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
     * @throws Exception An Error occurred.
     * @return an initialized VelocityEngine instance
     */
    private VelocityEngine getInitializedVelocityEngine()
        throws Exception
    {
        // Get the configuration for this service.
        Configuration conf = getConfiguration();

        catchErrors = conf.getBoolean(CATCH_ERRORS_KEY, CATCH_ERRORS_DEFAULT);

        // backward compatibility, can be overridden in the configuration
        conf.setProperty(RuntimeConstants.RUNTIME_LOG_NAME, "velocity");

        VelocityEngine velocity = new VelocityEngine();
        setVelocityProperties(velocity, conf);
        velocity.init();

        return velocity;
    }


    /**
     * This method generates the Properties object necessary
     * for the initialization of Velocity. It also converts the various
     * resource loader pathes into webapp relative pathes. It also
     *
     * @param velocity The Velocity engine
     * @param conf The Velocity Service configuration
     *
     * @throws Exception If a problem occurred while converting the properties.
     */

    protected void setVelocityProperties(VelocityEngine velocity, Configuration conf)
            throws Exception
    {
        // Fix up all the template resource loader pathes to be
        // webapp relative. Copy all other keys verbatim into the
        // veloConfiguration.

        for (Iterator<String> i = conf.getKeys(); i.hasNext();)
        {
            String key = i.next();
            if (!key.endsWith(RESOURCE_LOADER_PATH))
            {
                Object value = conf.getProperty(key);
                if (value instanceof List<?>)
                {
                    for (Iterator<?> itr = ((List<?>)value).iterator(); itr.hasNext();)
                    {
                        velocity.addProperty(key, itr.next());
                    }
                }
                else
                {
                    velocity.addProperty(key, value);
                }
                log.debug("Adding {} -> {}", key, value);
                continue; // for()
            }

            List<Object> paths = conf.getList(key, null);
            if (paths == null)
            {
                // We don't copy this into VeloProperties, because
                // null value is unhealthy for the ExtendedProperties object...
                continue; // for()
            }

            // Translate the supplied pathes given here.
            // the following three different kinds of
            // pathes must be translated to be webapp-relative
            //
            // jar:file://path-component!/entry-component
            // file://path-component
            // path/component
            for (Object p : paths)
            {
            	String path = (String)p;
                log.debug("Translating {}", path);

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

                        log.debug("Result (absolute jar path): {}", path);
                    }
                }
                else if(path.startsWith(ABSOLUTE_PREFIX))
                {
                    // skip file:// -> 7 chars
                    path = Turbine.getRealPath(path.substring(7));

                    log.debug("Result (absolute URL Path): {}", path);
                }
                // Test if this might be some sort of URL that we haven't encountered yet.
                else if(path.indexOf("://") < 0)
                {
                    path = Turbine.getRealPath(path);

                    log.debug("Result (normal fs reference): {}", path);
                }

                log.debug("Adding {} -> {}", key, path);
                // Re-Add this property to the configuration object
                velocity.addProperty(key, path);
            }
        }
    }

    /**
     * Find out if a given template exists. Velocity
     * will do its own searching to determine whether
     * a template exists or not.
     *
     * @param template String template to search for
     * @return True if the template can be loaded by Velocity
     */
    @Override
    public boolean templateExists(String template)
    {
        return velocity.resourceExists(template);
    }

    /**
     * Performs post-request actions (releases context
     * tools back to the object pool).
     *
     * @param context a Velocity Context
     */
    @Override
    public void requestFinished(Context context)
    {
        if (pullModelActive)
        {
            pullService.releaseTools(context);
        }
    }
}
