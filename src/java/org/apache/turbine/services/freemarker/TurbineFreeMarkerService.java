package org.apache.turbine.services.freemarker;

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

// Java Stuff
import java.util.*;
import java.io.*;

// Java Servlet Classes
import javax.servlet.*;

// Turbine Stuff
import org.apache.turbine.util.*;
import org.apache.turbine.util.security.*;
import org.apache.turbine.om.security.*;
import org.apache.turbine.services.*;
import org.apache.turbine.services.resources.*;
import org.apache.turbine.services.servlet.TurbineServlet;

import org.apache.commons.configuration.Configuration;

import org.apache.ecs.StringElement;

// FreeMarker Stuff
import freemarker.template.*;


/*
 * This is a Service that can process FreeMarker templates from within
 * a Turbine Screen.  Here's an example of how you might use it from a
 * screen:
 *
 * <br>
 *
 * FreeMarkerService fm = (FreeMarkerService)TurbineServices.getInstance()
 * .getService(FreeMarkerService.SERVICE_NAME);
 * SimpleHash context = fm.getContext(data);
 * context.put("message", "Hello from Turbine!");
 * String results = fm.handleRequest(context,"helloWorld.wm");
 * data.getPage().getBody().addElement(results);
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id$
 * @deprecated
 */
public class TurbineFreeMarkerService
    extends TurbineBaseService
    implements FreeMarkerService, CacheListener
{
    /** A cache to store parsed templates. */
    private FileTemplateCache templateCache;

    /** The base path prepended to filenames given in arguments. */
    private String path;

    /**
     * Constructor.
     */
    public TurbineFreeMarkerService()
    {
    }

    /**
     * Called during Turbine.init()
     *
     * @param config A ServletConfig.
     */
    public void init() throws InitializationException
    {
        try
        {
            initFreeMarker();
            setInit(true);
        }
        catch (Exception e)
        {
            throw new InitializationException(
                "TurbineFreeMarkerService failed to initialize", e);
        }
   }

    /**
     * Create a context needed by the FreeMarker template.  This
     * method just returns an SimpleHash with the request parameters
     * copied into a model called request.
     *
     * @return SimpleHash which can be used as the model for a
     * template.
     */
    public SimpleHash getContext()
    {
        return new SimpleHash();
    }

    /**
     * Create a context needed by the FreeMarker template.  This
     * method just returns an SimpleHash with the request parameters
     * copied into a model called request.
     *
     * @param req A ServletRequest.
     * @return SimpleHash which can be used as the model for a
     * template.
     */
    public SimpleHash getContext(ServletRequest req)
    {
        return (SimpleHash)TemplateServletUtils.copyRequest(req);
    }

    /**
     * Create a context from the RunData object.  Values found in
     * RunData are copied into the modelRoot under similar names as
     * they can be found in RunData. e.g. data.serverName,
     * data.parameters.form_field_name
     * data.acl.permissions.can_write_file.  Some default links are
     * also made available under links.
     *
     * @param data The Turbine RunData object.
     * @return a SimpleHash populated with RunData data.
     */
    public SimpleHash getContext(RunData data)
    {
        SimpleHash modelRoot = new SimpleHash();
        SimpleHash modelLinks = new SimpleHash();
        modelRoot.put("link", new DynamicURIModel(data));
        modelRoot.put("links", modelLinks);
        // modelLinks.put("default", new DynamicURI(data, "FreeMarkerScreen").toString());
        // modelLinks.put("no_cache", new DynamicURI(data, "FreeMarkerNoCacheScreen").toString());
        // modelLinks.put("simple", new DynamicURI(data).toString());
        if (data.getUser() != null && data.getUser().hasLoggedIn())
        {
            modelRoot.put("loginout_link", new DynamicURI(data, "Login", "LogoutUser").toString());
            modelRoot.put("loginout_text", "Logout");
        }
        else
        {
            modelRoot.put("loginout_link", new DynamicURI(data, "Login").toString());
            modelRoot.put("loginout_text", "Login");
        }

        // Add default.
        SimpleHash modelData = new SimpleHash();
        SimpleHash modelParameters = new SimpleHash();
        SimpleHash modelACL = new SimpleHash();
        SimpleHash modelRoles = new SimpleHash();
        SimpleHash modelPermissions = new SimpleHash();
        modelRoot.put("data", modelData);
        modelData.put("parameters", modelParameters);
        modelData.put("acl", modelACL);
        modelACL.put("roles", modelRoles);
        modelACL.put("permissions", modelPermissions);

        modelData.put("serverName", data.getServerName() );
        modelData.put("serverPort", String.valueOf(data.getServerPort()) );
        modelData.put("serverScheme", data.getServerScheme() );
        modelData.put("scriptName", data.getScriptName() );
        modelData.put("screen", data.getScreen() );
        modelData.put("action", data.getAction() );
        modelData.put("title", data.getTitle() );
        modelData.put("message", data.getMessage() );

        ParameterParser params = data.getParameters();
        Enumeration e = params.keys();
        while (e.hasMoreElements() )
        {
            String key = (String)e.nextElement();
            String[] values = params.getStrings(key);
            if (values.length==1)
            {
                modelParameters.put(key, values[0]);
            }
            else
            {
                SimpleList listModel = new SimpleList();
                modelParameters.put(key, listModel);
                for (int i=0; i<values.length; i++)
                {
                    listModel.add(values[i]);
                }
            }
        }

        if (data.getACL() != null)
        {
            Iterator roles = data.getACL().getRoles().elements();
            while (roles.hasNext() )
            {
                String key = ((Role)roles.next()).getName();
                modelRoles.put(key, true);
            }
            Iterator permissions = data.getACL().getPermissions().elements();
            while (permissions.hasNext() )
            {
                String key = ((Permission)permissions.next()).getName();
                modelPermissions.put(key, true);
            }
        }

        modelRoot.put("setTitle", new SetTitleModel(data));
        modelRoot.put("addToHead", new AddToHeadModel(data));
        modelRoot.put("addTemplatesToHead", new AddTemplatesToHeadModel(data));
        modelRoot.put("setBodyAttributes", new SetBodyAttributesModel(data));

        String templatePath = params.getString("template", null);
        if (templatePath != null)
        {
            StringTokenizer st = new StringTokenizer(templatePath, "/");
            int max = st.countTokens() - 1;
            for (int i=0; i<max; i++)
            {
                modelRoot.put("template_path_" + (i+1), st.nextToken());
            }
        }

        return modelRoot;
    }

    /**
     * Process the request and fill in the template with the values
     * you set in the WebContext.
     *
     * @param context A SimpleHash with the context.
     * @param templateName A String with the filename of the template.
     * @param cache True if the parsed template should be cached.
     * @return The processed template as a String.
     * @throws TurbineException Any exception trown while processing will be
     *         wrapped into a TurbineException and rethrown.
     */
    public String handleRequest(SimpleHash context,
                                String filename,
                                boolean cache)
        throws TurbineException
    {
        String results = null;
        StringWriter sw = null;
        PrintWriter out = null;
        try
        {
            sw = new StringWriter();
            out = new PrintWriter(sw);
            Template template = null;
            if (cache)
            {
                template = getCachedTemplate(filename);
            }
            else
            {
                template = getNonCachedTemplate(filename);
            }

            template.process(context, out);
            results = sw.toString();
        }
        catch(Exception e)
        {
            throw new TurbineException(
                "Error encountered processing a template: " + filename, e);
        }
        finally
        {
            try
            {
                if (out != null) out.close();
                if (sw != null) sw.close();
            }
            catch (Exception e) {}
        }
        return results;
    }

    /**
     * Gets the base path for the FreeMarker templates.
     *
     * @return The base path for the FreeMarker templates.
     */
    public String getBasePath()
    {
        return path;
    }

    /**
     * Return a FreeMarker template from the cache.  If the template
     * has not been cached yet, it will be added to the cache.
     *
     * @param templateName A String with the name of the template.
     * @return A Template.
     */
    public Template getCachedTemplate(String templateName)
    {
        Template template = templateCache.getTemplate(templateName);
        // if (template == null)
        // {
        //     Exception e = new Exception("Template " + templateName +
        //                                 " is not available.");
        //     Log.error ( e );
        //     throw e;
        // }
        return template;
    }

    /**
     * Return a FreeMarker template. It will not be added to the
     * cache.
     *
     * @param templateName A String with the name of the template.
     * @return A Template.
     * @exception IOException, if there was an I/O problem.
     */
    public Template getNonCachedTemplate(String templateName)
        throws IOException
    {
        templateName = templateName.replace('/', File.separatorChar);
        File file = new File(path, templateName);
        if (file.canRead())
        {
            return new Template(file);
        }
        // else
        // {
        //     Exception e = new Exception("Template " + templateName +
        //                                 " could not be read.");
        //     Log.error ( e );
        //     throw e;
        // }
        return null;
    }

    /**
     * This method sets up the FreeMarker template cache.
     *
     * @param config A ServletConfig.
     * @exception Exception, a generic exception.
     */
    private void initFreeMarker() throws Exception
    {
        Configuration config = getConfiguration();

        path = config.getString("templates", "/templates");

        // If possible, transform paths to be webapp root relative.
        path = TurbineServlet.getRealPath(path);

        // Store the converted path in service properties for Turbine
        // based providers.
        config.setProperty("templates", path);

        templateCache = new FileTemplateCache(path);
        templateCache.addCacheListener(this);
        templateCache.startAutoUpdate();
    }

    /**
     * Method called by templateCache.
     *
     * @param e A CacheEvent.
     */
    public void cacheUnavailable(CacheEvent e)
    {
        Log.error("Cache unavailable: " + e.getException().toString());
    }

    /**
     * Method called by templateCache.
     *
     * @param e A CacheEvent.
     */
    public void elementUpdated(CacheEvent e)
    {
        Log.info("Template updated: " + e.getElementName());
    }

    /**
     * Method called by templateCache.
     *
     * @param e A CacheEvent.
     */
    public void elementUpdateFailed(CacheEvent e)
    {
        Log.error("Update of template " + e.getElementName() +
                  " failed: " + e.getException().toString());
    }

    /**
     * Method called by templateCache.
     *
     * @param e A CacheEvent.
     */
    public void elementRemoved(CacheEvent e)
    {
        Log.warn("Template removed: " + e.getElementName());
    }
}
