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

import java.io.File;
import org.apache.turbine.services.ServiceBroker;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.util.Log;
import org.apache.stratum.configuration.Configuration;
import org.webmacro.Broker;
import org.webmacro.NotFoundException;
import org.webmacro.Template;
import org.webmacro.broker.Config;
import org.webmacro.broker.CreateResourceEvent;
import org.webmacro.broker.RequestResourceEvent;
import org.webmacro.broker.ResourceBroker;
import org.webmacro.broker.ResourceEvent;
import org.webmacro.broker.ResourceInitException;
import org.webmacro.broker.ResourceProvider;
import org.webmacro.engine.FileTemplate;

/**
 * <p>This class provides the WebMacro engine with <code>Temlate</code>
 * instances.
 *
 * <p>This class was created as a replacement of
 * <code>org.webmacro.resource.TemplateProvider</code> that relies on
 * the properties of <code>WebMacroService</code> for locating the templates.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @version $Id$
 * @deprecated
 */
public class TurbineTemplateProvider
    implements ResourceProvider
{
    /** The single resource type we provide. */
    public static String TYPE = "template";

    /** An array of supported resource types. */
    private static String[] types =  { TYPE };

    /** Expiration time of the provided resources. */
    private int cacheTime;

    /** Stored paths that are searched by this provider. */
    private String[] paths;

    /** Resource broker that manages this provider. */
    private Broker broker;

    /**
     * Returns an array of supported resource types.
     *
     * @return A String[] with the supported resource types.
     */
    public String[] getTypes()
    {
        return types;
    }

    /**
     * Returns expiration time of the provided resources.
     *
     * @return An int with the expiration time of the provided
     * resources.
     */
    public int resourceExpireTime()
    {
        return cacheTime;
    }

    /**
     * Returns the number of additional worker threads to be used by
     * the provider
     *
     * @return The number of additional worker threads to be used by
     * the provider.
     */
    public int resourceThreads()
    {
        // Provider is I/O bound, therefore spawning an extra thread
        // may be beneficial.
        return 1;
    }

    /**
     * Initializes the provider.
     *
     * @param broker The <code>ResourceBroker</code> that manages this
     * provider.
     * @exception ResourceInitException The resource couldn't be initialized.
     */
    public void init(ResourceBroker broker)
        throws ResourceInitException
    {
        Log.info("TurbineTemplateProvider initializing");
        this.broker = broker;

        try
        {
            try
            {
                String tmp = (String)broker.getValue(Config.TYPE,
                                                     Config.TEMPLATE_CACHE);
                cacheTime = Integer.valueOf(tmp).intValue();
            }
            catch (Exception e)
            {
                // 10 minutes.
                cacheTime = 10 * 60 * 1000;
            }

            /*
             * We are specifically not getting the configuration
             * from the service because it hasn't finished
             * init'ing we're getting it from the service broker.
             */
            Configuration config = TurbineServices.getInstance()
                .getConfiguration(WebMacroService.SERVICE_NAME);

            paths = config.getStringArray("templates");
            paths = TurbineTemplate.translateTemplatePaths(paths);

            StringBuffer pathMsg = new StringBuffer("TurbineTemplateProvider path(s):");

            for (int i = 0; i < paths.length; i++)
            {
                pathMsg.append('\n').append(paths[i]);
            }
            Log.info(pathMsg.toString());
        }
        catch(Exception e)
        {
            String msg = "TurbineTemplateProvider failed to initialize";
            Log.error(msg, e);
            throw new ResourceInitException(msg);
        }
        Log.info("TurbineTemplateProvider initialized");
    }

    /**
     * Shuts down the provider.
     */
    public void destroy()
    {
        broker = null;
        paths = null;
    }

    /**
     * Retrieves a resource.
     *
     * @param request A <code>RequestResourceEvent</code>.
     * @exception NotFoundException.
     * @exception InterruptedException.
     */
    public void resourceRequest(RequestResourceEvent request)
        throws NotFoundException,
               InterruptedException
    {
        Template t = findTemplate(request.getName());
        if (t == null)
        {
            // Let other providers handle it.
            return;
        }
        try
        {
            // Announce that we have it.
            request.set(t);
        }
        catch (Exception e)
        {
            // Ignore.
        }
    }

    /**
     * Creates a new resource (not implemented).
     *
     * @param create A <code>CreateResourceEvent</code>.
     * @exception NotFoundException.
     * @exception InterruptedException.
     */
    public void resourceCreate(CreateResourceEvent create)
        throws NotFoundException,
               InterruptedException
    {
        // Unsupported.
    }

    /**
     * Deletes a resource (not implemented).
     *
     * @param delete A <code>ResourceEvent</code>.
     * @return Always false.
     */
    public boolean resourceDelete(ResourceEvent delete)
    {
        // Unsupported.
        return false;
    }

    /**
     * Saves a resource to permanet storage (not implemented).
     *
     * @param save A <code>ResourceEvent</code>.
     * @return Always false.
     */
    public boolean resourceSave(ResourceEvent save)
    {
        // Unsupported.
        return false;
    }

    /**
     * Locates a template file, and instantiates <code>Template</code>
     * object.
     *
     * @param name the name of the template to be located
     * @return Instantiated <code>Template</code> object.
     */
    private Template findTemplate( String name )
    {
        for (int i = 0; i < paths.length; i++)
        {
            Template template;
            File file = new File(paths[i], name);
            //Log.debug("Looking for WebMacro template: path=" + paths[i] +
            //          ", name=" + name);
            if (file.canRead())
            {
                try
                {
                    template = new FileTemplate
                        (broker, file,
                         TurbineWebMacroService.DEFAULT_ENCODING);
                    template.parse();
                    return template;
                }
                catch(Exception e)
                {
                    Log.error
                        ("TurbineTemplateProvider failed to load template " +
                         file.getPath(), e);
                }
            }
        }
        return null;
    }
}
