package org.apache.turbine.services.pull;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.turbine.Turbine;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.pool.PoolService;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.ServletUtils;

/**
 * <p>
 * This is the concrete implementation of the Turbine
 * Pull Service.
 * </p>
 * <p>
 * These are tools that are placed in the context by the service
 * These tools will be made available to all your
 * templates. You list the tools in the following way:
 * </p>
 * <pre>
 * tool.<scope>.<id> = <classname>
 *
 * <scope>      is the tool scope: global, request, session
 *             or persistent (see below for more details)
 * <id>         is the name of the tool in the context
 *
 * You can configure the tools in this way:
 * tool.<id>.<parameter> = <value>
 *
 * So if you find "global", "request", "session" or "persistent" as second
 * part, it is a configuration to put a tool into the toolbox, else it is a
 * tool specific configuration.
 *
 * For example:
 *
 * tool.global.ui    = org.apache.turbine.util.pull.UIManager
 * tool.global.mm    = org.apache.turbine.util.pull.MessageManager
 * tool.request.link = org.apache.turbine.util.template.TemplateLink
 * tool.request.page = org.apache.turbine.util.template.TemplatePageAttributes
 *
 * Then:
 *
 * tool.ui.skin = default
 *
 * configures the value of "skin" for the "ui" tool.
 *
 * Tools are accessible in all templates by the <id> given
 * to the tool. So for the above listings the UIManager would
 * be available as $ui, the MessageManager as $mm, the TemplateLink
 * as $link and the TemplatePageAttributes as $page.
 *
 * You should avoid using tool names called "global", "request",
 * "session" or "persistent" because of clashes with the possible Scopes.
 *
 * Scopes:
 *
 *  global:     tool is instantiated once and that instance is available
 *              to all templates for all requests. Tool must be threadsafe.
 *
 *  request:    tool is instantiated once for each request (although the
 *              PoolService is used to recycle instances). Tool need not
 *              be threadsafe.
 *
 *  session:    tool is instantiated once for each user session, and is
 *              stored in the user's temporary hashtable. Tool should be 
 *              threadsafe.
 *
 *  persistent: tool is instantitated once for each use session, and
 *              is stored in the user's permanent hashtable. This means
 *              for a logged in user the tool will be persisted in the
 *              user's objectdata. Tool should be threadsafe and 
 *              Serializable.
 *
 * Defaults: none
 * </pre>
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @version $Id$
 */
public class TurbinePullService extends TurbineBaseService 
    implements PullService
{
    /**
     * This is the container for the global web application 
     * tools that are used in conjunction with the
     * Turbine Pull Model. All the global tools will be placed 
     * in this Context and be made accessible inside
     * templates via the tool name specified in the TR.props
     * file.
     */
    private Context globalContext;

    /**
     * This inner class is used in the lists below to store the
     * tool name and class for each of request, session and persistent
     * tools
     */
    private static class ToolData
    {
        String toolName;
        String toolClassName;
        Class  toolClass;

        public ToolData(String toolName, String toolClassName, Class toolClass)
        {
            this.toolName      = toolName;
            this.toolClassName = toolClassName;
            this.toolClass     = toolClass;
        }
    }

    /**
     * The lists that store tool data (name and class) for each
     * of the different type of tool. The Lists contain ToolData
     * objects.
     */
    private List globalTools;  
    private List requestTools;
    private List sessionTools;
    private List persistentTools;

    /**
     * The property tags that are used in conjunction with
     * TurbineResources.getOrderedValues(String) to get
     * our list of tools to instantiate (one tag for each
     * type of tool).
     */
    private static final String GLOBAL_TOOL = "tool.global";
    private static final String REQUEST_TOOL = "tool.request";
    private static final String SESSION_TOOL = "tool.session";
    private static final String PERSISTENT_TOOL = "tool.persistent";

    /**
     * Directory where application tool resources are stored.
     */
    private static String resourcesDirectory;

    /**
     * The absolute path the to resources directory used
     * by the application tools.
     */
    private static String absolutePathToResourcesDirectory;
    
    /**
     * Property tag for application tool resources directory
     */
    private static final String TOOL_RESOURCES_DIR
        = "tools.resources.dir";
    
    /**
     * Default value for the application tool resources
     * directory. The location for the resources directory
     * is typically WEBAPP/resources.
     */
    private static final String TOOL_RESOURCES_DIR_DEFAULT 
        = "/resources";

    /**
     * Property tag for per request tool refreshing
     * (for obvious reasons has no effect for per-request tools)
     */
    private static final String TOOLS_PER_REQUEST_REFRESH =
        "tools.per.request.refresh";         

    /**
     * Should we refresh the application tools on
     * a per request basis.
     */
    private static boolean refreshToolsPerRequest;

    /**
     * Called the first time the Service is used.
     */
    public void init() throws InitializationException
    {
        try
        {
           /*
            * Make sure to setInit(true) *inside* initPull() 
            * because Tools may make calls back to the TurbinePull 
            * static methods which may cause a recursive init 
            * thing to happen.
            */
            initPull();
        }
        catch (Exception e)
        {
            throw new InitializationException(
                "TurbinePullService failed to initialize", e);
        }
    }

    /**
     * Initialize the pull system
     *
     * @exception Exception, a generic exception.
     */
    private void initPull() throws Exception
    {
        Properties props = getProperties();

        /*
         * Get the resources directory that is specificed
         * in the TR.props or default to "/resources".
         */
        resourcesDirectory = TurbineResources.getString(
            TOOL_RESOURCES_DIR,
                TOOL_RESOURCES_DIR_DEFAULT);

        /*
         * Get absolute path to the resources directory.
         * 
         * This should be done before the tools initialized
         * because a tool might need to know this value
         * for it to initialize correctly.
         */
         absolutePathToResourcesDirectory = 
            Turbine.getRealPath(resourcesDirectory);
    
        /*
         * Should we refresh the tool box on a per
         * request basis.
         */
        refreshToolsPerRequest = 
            new Boolean(properties.getProperty(
                TOOLS_PER_REQUEST_REFRESH)).booleanValue();
        
        /*
         * Log the fact that the application tool box will
         * be refreshed on a per request basis.
         */
        if (refreshToolsPerRequest)
            Log.info("Pull Model tools will "
                    + "be refreshed on a per request basis.");

        /*
         * Make sure to set init true because Tools may make
         * calls back to the TurbinePull static methods which 
         * may cause a recursive init thing to happen.
         */
        setInit(true);

        /*
         * Grab each list of tools that are to be used (for global scope,
         * request scope, session scope and persistent scope tools).
         * They are specified respectively in the TR.props like this:
         *
         * tool.global.ui = org.apache.turbine.util.pull.UIManager
         * tool.global.mm = org.apache.turbine.util.pull.MessageManager
         *
         * tool.request.link = org.apache.turbine.util.template.TemplateLink;
         *
         * tool.session.basket = org.sample.util.ShoppingBasket;
         *
         * tool.persistent.ui = org.apache.turbine.services.pull.util.PersistentUIManager
         */
        globalTools     = getTools(GLOBAL_TOOL);
        requestTools    = getTools(REQUEST_TOOL);
        sessionTools    = getTools(SESSION_TOOL);
        persistentTools = getTools(PERSISTENT_TOOL);

        /*
         * Create and populate the global context right now
         */
        globalContext = new VelocityContext();
        populateWithGlobalTools(globalContext);
    }

    /**
     * Retrieve the tool names and classes for the tools definied
     * in the properties file with the prefix given.
     *
     * @param keyPrefix a String giving the property name prefix to look for
     */
    private List getTools(String keyPrefix)
    {
        List classes = new ArrayList();

        ResourceService toolResources = 
            TurbineResources.getResources(keyPrefix);

        /*
         * There might not be any tools for this prefix
         * so return an empty list.
         */
        if (toolResources == null)
        {
            return classes;
        }            

        Iterator it = toolResources.getKeys();
        while (it.hasNext())
        {
            String toolName = (String) it.next();
            String toolClassName = toolResources.getString(toolName);
            
            try
            {
                /* 
                 * Create an instance of the tool class.
                 */
                Class toolClass = Class.forName(toolClassName);

                /*
                 * Add the tool to the list being built.
                 */
                classes.add(new ToolData(toolName, toolClassName, toolClass));
                
                Log.info("Instantiated tool class " + toolClassName
                        + " to add to the context as '$"  + toolName + "'"); 
            }
            catch (Exception e)
            {
                Log.error("Cannot find tool class " + toolClassName
                        + ", please check the name of the class.");
            }
        }

        return classes;
    }
    
    /**
     * Return the Context which contains all global tools that
     * are to be used in conjunction with the Turbine
     * Pull Model.
     */
    public Context getGlobalContext()
    {
        return globalContext;
    }

    /**
     * Populate the given context with all request, session
     * and persistent scope tools (it is assumed that the context
     * already wraps the global context, and thus already contains
     * the global tools).
     *
     * @param context a Velocity Context to populate
     * @param data a RunData object for request specific data
     */
    public void populateContext(Context context, RunData data)
    {
        populateWithRequestTools(context, data);

        // session tools (whether session-only or persistent are
        // very similar, so the same method is used - the
        // boolean parameter indicates whether get/setPerm is to be used
        // rather than get/setTemp)
        User user = data.getUser();
        if (user != null)
        {
            populateWithSessionTools(sessionTools,    context, user, false);

            if (user.hasLoggedIn())
            {
                populateWithSessionTools(persistentTools, context, user, true);
            }
        }
    }

    /**
     * Populate the given context with the global tools
     *
     * @param context a Velocity Context to populate
     */
    private void populateWithGlobalTools(Context context)
    {
        Iterator it = globalTools.iterator();
        while (it.hasNext())
        {
            ToolData toolData = (ToolData)it.next();
            try
            {
                Object tool = toolData.toolClass.newInstance();
                if (tool instanceof ApplicationTool)
                {
                    // global tools are init'd with a null data parameter
                    ((ApplicationTool)tool).init(null);
                }
                // put the tool in the context
                context.put(toolData.toolName, tool);
            }
            catch (Exception e)
            {
                Log.error(
                        "Could not instantiate tool " + toolData.toolClassName
                        + " to add to the context");
            }
        }
    }

    /**
     * Populate the given context with the request-scope tools
     *
     * @param context a Velocity Context to populate
     * @param data a RunData instance
     */
    private void populateWithRequestTools(Context context, RunData data)
    {
        // Get the PoolService to fetch object instances from
        PoolService pool = (PoolService)
            TurbineServices.getInstance().getService(PoolService.SERVICE_NAME);

        // Iterate the tools
        Iterator it = requestTools.iterator();
        while (it.hasNext())
        {
            ToolData toolData = (ToolData)it.next();
            try
            {
                Object tool = pool.getInstance(toolData.toolClass);
                if (tool instanceof ApplicationTool)
                {
                    // request tools are init'd with a RunData object
                    ((ApplicationTool)tool).init(data);
                }
                // put the tool in the context
                context.put(toolData.toolName, tool);
            }
            catch (Exception e)
            {
                Log.error(
                        "Could not instantiate tool " + toolData.toolClassName
                        + " to add to the context",e);
            }
        }
    }

    /**
     * Populate the given context with the session-scoped tools.
     *
     * @param tools The list of tools with which to populate the
     * session.
     * @param context The context to populate.
     * @param user The <code>User</code> object whose storage to
     * retrieve the tool from.
     * @param userPerm Whether to retrieve the tools from the
     * permanent storage (as opposed to the temporary storage).
     */
    private void populateWithSessionTools(List tools, Context context,
                                          User user, boolean usePerm)
    {
        // Get the PoolService to fetch object instances from
        PoolService pool = (PoolService)
            TurbineServices.getInstance().getService(PoolService.SERVICE_NAME);

        // Iterate the tools
        Iterator it = tools.iterator();
        while (it.hasNext())
        {
            ToolData toolData = (ToolData)it.next();
            try
            {
                // ensure that tool is created only once for a user
                // by synchronizing against the user object
                synchronized (user)
                {
                    // first try and fetch the tool from the user's 
                    // hashtable
                    Object tool = usePerm
                        ? user.getPerm(toolData.toolClassName)
                        : user.getTemp(toolData.toolClassName);
                    
                    if (tool == null)
                    {
                        // if not there, an instance must be fetched from
                        // the pool
                        tool = pool.getInstance(toolData.toolClass);
                        if (tool instanceof ApplicationTool)
                        {
                            // session tools are init'd with the User object
                            ((ApplicationTool)tool).init(user);
                        }
                        // store the newly created tool in the user's hashtable
                        if (usePerm)
                        {
                            user.setPerm(toolData.toolClassName, tool);
                        }
                        else
                        {
                            user.setTemp(toolData.toolClassName, tool);
                        }
                    }
                    else if (refreshToolsPerRequest
                            && tool instanceof ApplicationTool)
                    {
                        ((ApplicationTool)tool).refresh();
                    }
                    // put the tool in the context
                    context.put(toolData.toolName, tool);
                }
            }
            catch (Exception e)
            {
                Log.error(
                        "Could not instantiate tool " + toolData.toolClassName
                        + " to add to the context");
            }
        }
    }
      
    /**
     * Return the absolute path to the resources directory 
     * used by the application tools.
     */
    public String getAbsolutePathToResourcesDirectory()
    {
        return absolutePathToResourcesDirectory;
    }
    
    /**
     * Return the resources directory. This is
     * relative to the web context.
     */
    public String getResourcesDirectory()
    {
        return resourcesDirectory;
    }        

    /**
     * Refresh the global tools. We can
     * only refresh those tools that adhere to
     * ApplicationTool interface because we
     * know those types of tools have a refresh
     * method.
     */
    public void refreshGlobalTools()
    {
        Iterator i = globalTools.iterator();
        while (i.hasNext())
        {
            ToolData toolData = (ToolData)i.next();
            Object tool = globalContext.get(toolData.toolName);
            if (tool instanceof ApplicationTool)
                ((ApplicationTool)tool).refresh();
        }
    }

    /**
     * Should we refresh the ToolBox on
     * a per request basis.
     */
    public boolean refreshToolsPerRequest()
    {
        return refreshToolsPerRequest;
    }

    /**
     * Release the request-scope tool instances in the
     * given Context back to the pool
     *
     * @param context the Velocity Context to release tools from
     */
    public void releaseTools(Context context)
    {
        // Get the PoolService to release object instances to
        PoolService pool = (PoolService)
            TurbineServices.getInstance().getService(PoolService.SERVICE_NAME);
       
        // only the request tools can be released - other scoped 
        // tools will have continuing references to them
        releaseTools(context, pool, requestTools);
    }

    /**
     * Release the given list of tools from the context back
     * to the pool
     *
     * @param context the Context containing the tools
     * @param pool an instance of the PoolService
     * @param tools a List of ToolData objects
     */
    private void releaseTools(Context context, PoolService pool, List tools)
    {
        Iterator it = tools.iterator();
        while (it.hasNext())
        {
            ToolData toolData = (ToolData)it.next();
            Object tool = context.remove(toolData.toolName);
            
            if (tool != null)
            {
                pool.putInstance(tool);
            }                
        }
    }
}
