package org.apache.turbine.services.velocity;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Vector;
import javax.servlet.ServletConfig;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.pull.TurbinePull;
import org.apache.turbine.services.template.BaseTemplateEngineService;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.log.SimpleLog4JLogSystem;

/**
 * This is a Service that can process Velocity templates from within a
 * Turbine Screen.  Here's an example of how you might use it from a
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
        implements VelocityService
{
    /**
     * The generic resource loader path property in velocity.
     */
    private static final String RESOURCE_LOADER_PATH =
            ".resource.loader.path";

    /**
     * Default character set to use if not specified in the RunData object.
     */
    private static final String DEFAULT_CHAR_SET = "ISO-8859-1";

    /**
     * The prefix used for URIs which are of type <code>jar</code>.
     */
    private static final String JAR_PREFIX = "jar:";

    /**
     * The prefix used for URIs which are of type <code>absolute</code>.
     */
    private static final String ABSOLUTE_PREFIX = "file://";

    /** Logging */
    private static Log log = LogFactory.getLog(TurbineVelocityService.class);

    /**
     * The context used to the store the context
     * containing the global application tools.
     */
    private Context globalContext = null;

    /**
     * Is the pullModelActive.
     */
    private boolean pullModelActive = false;

    /**
     * Should we refresh the tools on a per
     * request basis. This is used for development.
     */
    private boolean refreshToolsPerRequest = false;

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

            globalContext = null;

            /*
             * We can only load the Pull Model ToolBox
             * if the Pull service has been listed in the TR.props
             * and the service has successfully been initialized.
             */
            if (TurbinePull.isRegistered())
            {
                globalContext = TurbinePull.getGlobalContext();

                pullModelActive = true;

                refreshToolsPerRequest = TurbinePull.refreshToolsPerRequest();
            }

            /*
             * If the Pull service is inactive then we create
             * an empty toolBoxContext
             */
            if (globalContext == null)
            {
                globalContext = new VelocityContext();
            }

            /*
             * Register with the template service.
             */
            registerConfiguration("vm");
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
        return new VelocityContext(globalContext);
    }

    /**
     * Create a Context from the RunData object.  Adds a pointer to
     * the RunData object to the WC so that RunData is available in
     * the templates.
     *
     * @param data The Turbine RunData object.
     * @return A clone of the WebContext needed by Velocity.
     */
    public Context getContext(RunData data)
    {
        /*
         * Attempt to get it from the data first.  If it doesn't
         * exist, create it and then stuff it into the data.
         */
        Context context = (Context)
                data.getTemplateInfo().getTemplateContext(VelocityService.CONTEXT);

        if (context == null)
        {
            context = getContext();
            context.put("data", data);

            if (pullModelActive)
            {
                /*
                 * Populate the toolbox with request scope, session scope
                 * and persistent scope tools (global tools are already in
                 * the toolBoxContent which has been wrapped to construct
                 * this request-specific context).
                 */
                TurbinePull.populateContext(context, data);
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

        try
        {
            bytes = new ByteArrayOutputStream();
            String charset = decodeRequest(context, filename, bytes);
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
                if (bytes != null) bytes.close();
            }
            catch (IOException ignored)
            {
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
    public void handleRequest(Context context,
                              String filename,
                              OutputStream output)
            throws TurbineException
    {
        decodeRequest(context, filename, output);
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
    public void handleRequest(Context context,
                              String filename,
                              Writer writer)
            throws TurbineException
    {
        String encoding = getEncoding(context);
        decodeRequest(context, filename, encoding, writer);
    }

    /**
     * Process the request and fill in the template with the values
     * you set in the Context. Apply the character and template
     * encodings from RunData to the result.
     *
     * @param context A Context.
     * @param filename A String with the filename of the template.
     * @param output A OutputStream where we will write the process template as
     * a String.
     * @return The character encoding applied to the resulting String.
     *
     * @throws TurbineException Any exception trown while processing will be
     *         wrapped into a TurbineException and rethrown.
     */
    private String decodeRequest(Context context,
                                 String filename,
                                 OutputStream output)
            throws TurbineException
    {
        /*
         * This is for development.
         */
        if (pullModelActive && refreshToolsPerRequest)
        {
            TurbinePull.refreshGlobalTools();
        }

        /*
         * Get the character and template encodings from the RunData object.
         */
        String charset = getCharSet(context);
        String encoding = getEncoding(context);

        OutputStreamWriter writer = null;

        try
        {
            writer = new OutputStreamWriter(output, charset);
            if (encoding != null)
            {
                Velocity.mergeTemplate(filename, encoding, context, writer);
            }
            else
            {
                Velocity.mergeTemplate(filename, context, writer);
            }
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
                /*
                 * do nothing.
                 */
            }
        }

        return charset;
    }

    /**
     * Retrieve the required charset from the Turbine RunData in the context
     *
     * @param context A Context.
     * @return The character set applied to the resulting String.
     */
    private String getCharSet(Context context)
    {
        String charset;
        Object data = context.get("data");
        if ((data != null) && (data instanceof RunData))
        {
            charset = ((RunData) data).getCharSet();
            if (charset == null)
            {
                charset = DEFAULT_CHAR_SET;
            }
        }
        else
        {
            charset = DEFAULT_CHAR_SET;
        }

        return charset;
    }

    /**
     * Retrieve the required encoding from the Turbine RunData in the context
     *
     * @param context A Context.
     * @return The encoding applied to the resulting String.
     */
    private String getEncoding(Context context)
    {
        String encoding;
        Object data = context.get("data");
        if ((data != null) && (data instanceof RunData))
        {
            encoding = ((RunData) data).getTemplateEncoding();
        }
        else
        {
            encoding = null;
        }
        return encoding;
    }

    /**
     * Process the request and fill in the template with the values
     * you set in the Context.
     *
     * @param context A Context.
     * @param filename A String with the filename of the template.
     * @param encoding The encoding to use with the template
     * @param writer A Writer where we will write the process template as
     * a String. This writer charset should be compatible with the selected
     * encoding
     *
     * @throws TurbineException Any exception trown while processing will be
     *         wrapped into a TurbineException and rethrown.
     */
    private void decodeRequest(Context context,
                               String filename,
                               String encoding,
                               Writer writer)
            throws TurbineException
    {
        /*
         * This is for development.
         */
        if (pullModelActive && refreshToolsPerRequest)
        {
            TurbinePull.refreshGlobalTools();
        }

        try
        {
            if (encoding != null)
            {
                Velocity.mergeTemplate(filename, encoding, context, writer);
            }
            else
            {
                Velocity.mergeTemplate(filename, context, writer);
            }
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
                /*
                 * do nothing.
                 */
            }
        }
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
        log.error(err + ": " + e.getMessage());
        throw new TurbineException(err, e);
    }

    /**
     * Setup the velocity runtime by using a subset of the
     * Turbine configuration which relates to velocity.
     *
     * @exception InitializationException For any errors during initialization.
     */
    private void initVelocity()
            throws InitializationException
    {
        /*
         * Get the configuration for this service.
         */
        Configuration configuration = getConfiguration();

        configuration.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS,
                SimpleLog4JLogSystem.class.getName());
        configuration.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM
                + ".log4j.category", "velocity");

        /*
         * Get all the template paths where the velocity runtime should search
         * for templates and collect them into a separate vector to avoid
         * concurrent modification exceptions.
         */
        String key;
        Vector keys = new Vector();
        for (Iterator i = configuration.getKeys(); i.hasNext();)
        {
            key = (String) i.next();
            if (key.endsWith(RESOURCE_LOADER_PATH))
            {
                keys.add(key);
            }
        }

        /*
         * Loop through all template paths, clear the corresponding
         * velocity properties and translate them all to the webapp space.
         */

        int ind;
        Vector paths;
        String entry;
        for (Iterator i = keys.iterator(); i.hasNext();)
        {
            key = (String) i.next();
            paths = configuration.getVector(key, null);
            if (paths != null)
            {
                Velocity.clearProperty(key);
                configuration.clearProperty(key);

                for (Iterator j = paths.iterator(); j.hasNext();)
                {
                    String path = (String) j.next();
                    if (path.startsWith(JAR_PREFIX + "file"))
                    {
                        /*
                         * A local jar resource URL path is a bit more
                         * complicated, but we can translate it as well.
                         */
                        ind = path.indexOf("!/");
                        if (ind >= 0)
                        {
                            entry = path.substring(ind);
                            path = path.substring(9, ind);
                        }
                        else
                        {
                            entry = "!/";
                            path = path.substring(9);
                        }
                        path = JAR_PREFIX + "file:"
                                + Turbine.getRealPath(path) + entry;
                    }
                    else if (path.startsWith(ABSOLUTE_PREFIX))
                    {
                        path = path.substring(ABSOLUTE_PREFIX.length(),
                                path.length());
                    }
                    else if (!path.startsWith(JAR_PREFIX))
                    {
                        // But we don't translate remote jar URLs.
                        path = Turbine.getRealPath(path);
                    }
                    // Put the translated paths back to the configuration.
                    configuration.addProperty(key, path);
                }
            }
        }
        try
        {
            Velocity.setExtendedProperties(ConfigurationConverter
                    .getExtendedProperties(configuration));
            Velocity.init();
        }
        catch (Exception e)
        {
            /*
             * This will be caught and rethrown by the init() method.
             * Oh well, that will protect us from RuntimeException folk showing
             * up somewhere above this try/catch
             */
            throw new InitializationException(
                    "Failed to set up TurbineVelocityService", e);
        }
    }

    /**
     * Find out if a given template exists. Velocity
     * will do its own searching to determine whether
     * a template exists or not.
     *
     * @param template String template to search for
     * @return boolean
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
            TurbinePull.releaseTools(context);
        }
    }
}
