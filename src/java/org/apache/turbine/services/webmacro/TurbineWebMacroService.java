package org.apache.turbine.services.webmacro;

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
import java.io.OutputStream;
import javax.servlet.ServletConfig;
import org.apache.commons.configuration.Configuration;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.services.template.BaseTemplateEngineService;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.util.ContentURI;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.template.TemplateLink;
import org.apache.turbine.util.template.TemplatePageAttributes;
import org.apache.turbine.util.webmacro.WebMacroFormatter;
import org.webmacro.Broker;
import org.webmacro.FastWriter;
import org.webmacro.InitException;
import org.webmacro.NotFoundException;
import org.webmacro.Template;
import org.webmacro.WM;
import org.webmacro.WebMacro;
import org.webmacro.broker.ResourceBroker;
import org.webmacro.broker.ResourceProvider;
import org.webmacro.servlet.WebContext;

/**
 * This is a Service that can process WebMacro templates from within a
 * Turbine Screen.  Here's an example of how you might use it from a
 * screen:
 * <br>
 * <code>
 * WebContext context = WebMacro.getContext(data);<br>
 * context.put("message", "Hello from Turbine!");<br>
 * String results = WebMacro.handleRequest(context,"helloWorld.wm");<br>
 * data.getPage().getBody().addElement(results);<br>
 * </code>
 * <p>
 * Multiple template paths are specified via the
 * <code>services.TurbineWebMacroService.templates</code> property in the
 * <code>TurbineResources.properties</code> file, specified as a single string
 * delimited by the value of the System property <code>path.separator</code>
 * (this is <code>:</code> on UNIX and <code>;</code> on Windows).
 * <p>
 * Turbine does not use the default <code>ResourceProvider</code> for WebMacro
 * ResourceEvents of type <code>template</code>.  Instead, Turbine uses its own
 * <code>TurbineTemplateProvider</code> implementation.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id$
 * @deprecated
 */
public class TurbineWebMacroService extends BaseTemplateEngineService
    implements WebMacroService
{
    /**
     * The default encoding used by the WebMacro <code>FastWriter</code>.
     * <p>
     * We should encode the ouput byte stream as <code>UTF-16</code> to
     * achieve the fastest conversion back to Java characters, but for some
     * reason this does not work (bug in FastWriter?).  <code>UTF-8</code>
     * seems to work and is used as a fallback option.
     */
    protected static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * The WebMacro engine.
     */
    private WebMacro wm = null;

    /**
     * The context used as a factory.
     */
    private WebContext wcPrototype = null;

    /**
     * The broker used by WebMacro to retrieve resources.
     */
    private Broker broker = null;

    /**
     * Paths where templates can be located.
     */
    private String[] templatePaths = null;

    /**
     * Webmacro properties file.
     */
    private String WMProperties = null;

    /**
     * WebMacro template provider.
     */
    private String WMTemplateProvider = null;

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
        ServletConfig conf = Turbine.getTurbineServletConfig();
        init(conf);
    }

    /**
     * Inits the service using servlet parameters to obtain path to the
     * configuration file.
     *
     * @param config The ServletConfiguration from Turbine
     *
     * @throws InitializationException Something went wrong when starting up.
     */
    public void init(ServletConfig unused) throws InitializationException
    {
        try
        {
            initWebMacro();
            initWebContext();
            registerConfiguration("wm");
            setInit(true);
        }
        catch (Exception e)
        {
            throw new InitializationException
                (WebMacroService.SERVICE_NAME + " failed to initialize", e);
        }
    }

    /**
     * Shuts down the service, including its
     * <a href="http://webmacro.org/">WebMacro</a> engine.
     */
    public void shutdown()
    {
        wm.destroy();
        /*
         * WebMacro leaves an active thread after shutdown (which is probably
         * a bug). We shut it down manually here.
         */
        org.webmacro.util.ThreadScheduler.stop();
    }

    /**
     * Create an empty WebContext object.
     *
     * @return A new, empty context.
     */
    public WebContext getContext()
    {
        return new WebContext(broker);
    }

    /**
     * Create a <code>WebContext</code> from the <code>RunData</code>
     * object.  Adds a pointer to the <code>RunData</code> object to
     * the WC so that RunData is available in the templates.
     *
     * @param data The Turbine RunData object.
     * @return A clone of the WebContext needed by WebMacro.
     */
    public WebContext getContext(RunData data)
    {
        WebContext newWC = wcPrototype.newInstance(data.getRequest(),
                                                   data.getResponse());
        newWC.put( "data", data );
        newWC.put( "link", new TemplateLink(data) );
        newWC.put( "page", new TemplatePageAttributes(data) );
        newWC.put( "formatter", new WebMacroFormatter(newWC) );
        newWC.put( "content", new ContentURI(data) );
        return newWC;
    }

    /**
     * Process the request and fill in the template with the values
     * you set in the WebContext.
     *
     * @param wc The populated context.
     * @param filename The file name of the template.
     * @return The process template as a String.
     * @throws TurbineException Any exception trown while processing will be
     *         wrapped into a TurbineException and rethrown.
     */
    public String handleRequest(WebContext wc,
                                String filename)
        throws TurbineException
    {
        String results = null;
        try
        {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            FastWriter fw = new FastWriter(bytes, DEFAULT_ENCODING);
            handleRequest(wc, filename, fw);
            fw.flush();
            results = bytes.toString(DEFAULT_ENCODING);
        }
        catch( Exception e )
        {
            throw new TurbineException
                ("An error occurred while rendering template: " + filename, e);
        }

        return results;
    }

    /**
     * Process the request and fill in the template with the values
     * you set in the WebContext.
     *
     * @param wc The populated context.
     * @param filename The file name of the template.
     * @param out A stream to write the processed template to.
     * @throws TurbineException Any exception trown while processing will be
     *         wrapped into a TurbineException and rethrown.
     */
    public void handleRequest(WebContext wc,
                              String filename,
                              OutputStream out)
        throws Exception
    {
        handleRequest(wc, filename, new FastWriter(out, DEFAULT_ENCODING));
    }

    /**
     * Process the request and fill in the template with the values
     * you set in the WebContext.
     *
     * @param wc The populated context.
     * @param filename The file name of the template.
     * @param writer A writer to write the processed template with.
     * @throws TurbineException Any exception trown while processing will be
     *         wrapped into a TurbineException and rethrown.
     */
    public void handleRequest(WebContext wc,
                              String filename,
                              FastWriter writer)
        throws Exception
    {
        Template template = getTemplate(filename);
        template.write(writer, wc);
    }

    /**
     * Return a template from WebMacro.
     *
     * @param filename A String with the name of the template.
     * @return A Template.
     * @exception NotFoundException The template could not be found.
     */
    public Template getTemplate(String filename)
        throws NotFoundException
    {
        return wm.getTemplate(filename);
    }

    /**
     * This method returns the WebMacro object which will be used to
     * load, access, and manage the Broker.  The WM first tries to
     * locate the WebMacro properties file in the location specified
     * by the 'services.WebmacroService.properties' key in the
     * TurbineResource file.  If the key is not set, it will search
     * the classpath looking for the file.
     *
     * @param config A ServletConfig.
     * @exception InitException Thrown by WebMacro.
     * @exception InitializationException Thrown by Turbine.
     */
    private void initWebMacro()
        throws InitException, InitializationException
    {
        Configuration config = getConfiguration();

        /*
         * We retrieve the template paths here and translate
         * them so that we have a functional templateExists(name)
         * method in this service. This happens again in
         * the provider so that we don't have to store the
         * converted configuration, and we don't want to
         * store them because the initialization of this
         * service is done in one pass. This means that
         * the provider trying to retrieve the converted
         * properties via this service we get a recursion
         * problem. What would be idea is if the service
         * broker could store the properties for each of
         * the services, even if they are converted. Just
         * trying to explain what's going on.
         */
        templatePaths = config.getStringArray("templates");
        templatePaths = TurbineTemplate.translateTemplatePaths(templatePaths);

        WMTemplateProvider = config.getString("templates.provider", null);
        WMProperties = config.getString("properties");

        if (WMProperties != null)
        {
            /*
             * If possible, transform paths to be webapp
             * root relative.
             */
            WMProperties = TurbineServlet.getRealPath(WMProperties);
            wm = new WM(WMProperties);
        }
        else
        {
            /*
             * Not specified: fallback to WM style properties search
             * through the classpath.
             */
            wm = new WM();
        }
        broker = wm.getBroker();

        try
        {
            ResourceBroker resourceBroker = (ResourceBroker)broker;

            /*
             * Replace WM's TemplateProvider with Turbine's.
             */
            try
            {
                /*
                 * Instantiate Turbine's TemplateProvider.
                 */
                Log.debug("Loading TurbineTemplateProvider");
                Class c = Class.forName(WMTemplateProvider);
                ResourceProvider provider = (ResourceProvider)c.newInstance();

                /*
                 * Register the provider with the broker.
                 */
                Log.debug("Registering TurbineTemplateProvider with WebMacro");
                resourceBroker.join(provider);
                // TODO: Find WM template provider instance and remove it.
                // ResourceProvider wmProvier = null;
                // resourceBroker.leave(wmProvider);
            }
            catch (Exception e)
            {
                throw new TurbineException
                    ("Unable to register TurbineTemplateProvider", e);
            }

            // Note that WebMacro.properties shouldn't contain
            // org.webmacro.resource.TemplateProvider in the Providers
            // entry, because then we would have two TemplateProviders
            // in the system, one of wich would be non functional and
            // would waste cycles during every request.
            //
            // It seems reasonable to me, that there should be a
            // possibility to ask the broker if it knows any providers
            // of a specified type of resource.  This would allow us
            // to notify the user that there is a non-functional
            // provider in the system, that should be removed from the
            // config file.  Better yet, if the browser supported
            // enumerating providers of a specified type of resource,
            // we could locate such provider, and ask it to terminate.
            //
            // The broker class in the WM snapshot I have doesn't
            // support either of these options, and I don't feel
            // competent enough to implement it.
            //
            // Rafal
        }
        catch (Exception e)
        {
            throw new InitializationException
                ("Failed to set up WebMacro templates", e);
        }
    }

    /**
     * This method must return a cloneable WebContext which can be
     * cloned for use in responding to individual requests.  Each
     * incoming request will receive a clone of the returned object as
     * its context.  The default implementation is to return a new
     * WebContext.
     *
     * @exception InitException.
     */
    private void initWebContext()
        throws InitException
    {
        wcPrototype = getContext();
        if (wcPrototype == null)
        {
            throw new InitException
                ("Unable to create WebContext prototype");
        }
    }

    /**
     * Determine whether a given template exists. This service
     * currently only supports file base template hierarchies
     * so we will use the utility methods provided by
     * the template service to do the searching.
     *
     * @param String template
     * @return boolean
     */
    public boolean templateExists(String template)
    {
        return TurbineTemplate.templateExists(template, templatePaths);
    }
}
