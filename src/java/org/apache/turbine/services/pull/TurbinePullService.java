package org.apache.turbine.services.pull;


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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.fulcrum.pool.PoolService;
import org.apache.turbine.services.avaloncomponent.AvalonComponentService;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.services.velocity.VelocityService;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.util.RunData;

import org.apache.velocity.context.Context;

/**
 * This is the concrete implementation of the Turbine
 * Pull Service.
 * <p>
 * These are tools that are placed in the context by the service
 * These tools will be made available to all your
 * templates. You list the tools in the following way:
 * <p>
 * <pre>
 * tool.&lt;scope&gt;.&lt;id&gt; = &lt;classname&gt;
 *
 * &lt;scope&gt;      is the tool scope: global, request, session,
 *              authorized or persistent (see below for more details)
 * &lt;id&gt;         is the name of the tool in the context
 *
 * You can configure the tools in this way:
 * tool.&lt;id&gt;.&lt;parameter&gt; = &lt;value&gt;
 *
 * So if you find "global", "request", "session" or "persistent" as second
 * part, it is a configuration to put a tool into the toolbox, else it is a
 * tool specific configuration.
 *
 * For example:
 *
 * tool.global.ui    = org.apache.turbine.util.pull.UIManager
 * tool.global.mm    = org.apache.turbine.util.pull.MessageManager
 * tool.request.link = org.apache.turbine.services.pull.tools.TemplateLink
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
 *              stored in the session.  These tools do not need to be
 *              threadsafe.
 *
 *  authorized: tool is instantiated once for each user session once the
 *              user logs in. After this, it is a normal session tool.
 *
 *  persistent: tool is instantitated once for each user session once
 *              the user logs in and is is stored in the user's permanent
 *              hashtable.
 *              This means for a logged in user the tool will be persisted
 *              in the user's objectdata. Tool should be Serializable.  These
 *              tools do not need to be threadsafe.
 *              <b>persistent scope tools are deprecated in 2.3</b>
 *
 * Defaults: none
 * </pre>
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class TurbinePullService
        extends TurbineBaseService
        implements PullService
{
    /** Logging */
    private static Log log = LogFactory.getLog(TurbinePullService.class);

    /** Reference to the pool service */
    private PoolService pool = null;

    /** Reference to the templating (nee Velocity) service */
    private VelocityService velocity = null;

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
        Class toolClass;

        public ToolData(String toolName, String toolClassName, Class toolClass)
        {
            this.toolName = toolName;
            this.toolClassName = toolClassName;
            this.toolClass = toolClass;
        }
    }

    /** Internal list of global tools */
    private List globalTools;

    /** Internal list of request tools */
    private List requestTools;

    /** Internal list of session tools */
    private List sessionTools;

    /** Internal list of authorized tools */
    private List authorizedTools;

    /** Internal list of persistent tools */
    private List persistentTools;

    /** Directory where application tool resources are stored.*/
    private String resourcesDirectory;

    /** Should we refresh the application tools on a per request basis? */
    private boolean refreshToolsPerRequest = false;

    /**
     * Called the first time the Service is used.
     */
    public void init()
        throws InitializationException
    {
        try
        {
        	   AvalonComponentService acs = (AvalonComponentService) TurbineServices.getInstance().getService(AvalonComponentService.SERVICE_NAME);
		   pool = (PoolService)acs.lookup(PoolService.ROLE);


            if (pool == null)
            {
                throw new InitializationException("Pull Service requires"
                    + " configured Pool Service!");
            }

            initPullService();
            // Make sure to setInit(true) because Tools may
            // make calls back to the TurbinePull static methods
            // which causes an init loop.
            setInit(true);

            // Do _NOT_ move this before the setInit(true)
            velocity = TurbineVelocity.getService();

            if (velocity != null)
            {
                initPullTools();
            }
            else
            {
                log.info("Velocity Service not configured, skipping pull tools!");
            }
        }
        catch (Exception e)
        {
            throw new InitializationException(
                "TurbinePullService failed to initialize", e);
        }
    }

    /**
     * Initialize the pull service
     *
     * @exception Exception A problem happened when starting up
     */
    private void initPullService()
        throws Exception
    {
        // This is the per-service configuration, prefixed with services.PullService
        Configuration conf = getConfiguration();

        // Get the resources directory that is specificed
        // in the TR.props or default to "resources", relative to the webapp.
        resourcesDirectory = conf.getString(
            TOOL_RESOURCES_DIR_KEY,
            TOOL_RESOURCES_DIR_DEFAULT);

        // Should we refresh the tool box on a per
        // request basis.
        refreshToolsPerRequest =
            conf.getBoolean(
                TOOLS_PER_REQUEST_REFRESH_KEY,
                TOOLS_PER_REQUEST_REFRESH_DEFAULT);

        // Log the fact that the application tool box will
        // be refreshed on a per request basis.
        if (refreshToolsPerRequest)
        {
            log.info("Pull Model tools will "
                + "be refreshed on a per request basis.");
        }
    }

    /**
     * Initialize the pull tools. At this point, the
     * service must be marked as initialized, because the
     * tools may call the methods of this service via the
     * static facade class TurbinePull.
     *
     * @exception Exception A problem happened when starting up
     */
    private void initPullTools()
        throws Exception
    {
        // And for reasons I never really fully understood,
        // the tools directive is toplevel without the service
        // prefix. This is brain-damaged but for legacy reasons we
        // keep this. So this is the global turbine configuration:
        Configuration conf = Turbine.getConfiguration();

        // Grab each list of tools that are to be used (for global scope,
        // request scope, authorized scope, session scope and persistent
        // scope tools). They are specified respectively in the TR.props
        // like this:
        //
        // tool.global.ui = org.apache.turbine.util.pull.UIManager
        // tool.global.mm = org.apache.turbine.util.pull.MessageManager
        //
        // tool.request.link = org.apache.turbine.services.pull.tools.TemplateLink
        //
        // tool.session.basket = org.sample.util.ShoppingBasket;
        //
        // tool.persistent.ui = org.apache.turbine.services.pull.util.PersistentUIManager

        log.debug("Global Tools:");
        globalTools     = getTools(conf.subset(GLOBAL_TOOL));
        log.debug("Request Tools:");
        requestTools    = getTools(conf.subset(REQUEST_TOOL));
        log.debug("Session Tools:");
        sessionTools    = getTools(conf.subset(SESSION_TOOL));
        log.debug("Authorized Tools:");
        authorizedTools = getTools(conf.subset(AUTHORIZED_TOOL));
        log.debug("Persistent Tools:");
        persistentTools = getTools(conf.subset(PERSISTENT_TOOL));

        // Create and populate the global context right now

        // This is unholy, because it entwines the VelocityService and 
        // the Pull Service even further. However, there isn't much we can
        // do for the 2.3 release. Expect this to go post-2.3
        globalContext = velocity.getNewContext();

        populateWithGlobalTools(globalContext);
    }

    /**
     * Retrieve the tool names and classes for the tools definied
     * in the configuration file with the prefix given.
     *
     * @param toolConfig The part of the configuration describing some tools
     */
    private List getTools(Configuration toolConfig)
    {
        List tools = new ArrayList();

        // There might not be any tools for this prefix
        // so return an empty list.
        if (toolConfig == null)
        {
            return tools;
        }

        for (Iterator it = toolConfig.getKeys(); it.hasNext();)
        {
            String toolName = (String) it.next();
            String toolClassName = toolConfig.getString(toolName);

            try
            {
                // Create an instance of the tool class.
                Class toolClass = Class.forName(toolClassName);

                // Add the tool to the list being built.
                tools.add(new ToolData(toolName, toolClassName, toolClass));

                log.info("Tool " + toolClassName
                    + " to add to the context as '$" + toolName + "'");
            }
            catch (Exception e)
            {
                log.error("Cannot instantiate tool class "
                    + toolClassName + ": ", e);
            }
        }

        return tools;
    }

    /**
     * Return the Context which contains all global tools that
     * are to be used in conjunction with the Turbine
     * Pull Model. The tools are refreshed every time the
     * global Context is pulled.
     */
    public Context getGlobalContext()
    {
        if (refreshToolsPerRequest)
        {
            refreshGlobalTools();
        }
        return globalContext;
    }

    /**
     * Populate the given context with all request, session, authorized
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

        //
        // Session Tool start right at the session once the user has been set
        // while persistent and authorized Tools are started when the user has
        // logged in
        //
        User user = data.getUser();

        // Note: Session tools are currently lost after the login action
        // because the anonymous user is replaced the the real user object.
        // We should either store the session pull tools in the session or
        // make Turbine.loginAction() copy the session pull tools into the
        // new user object.
        populateWithSessionTools(sessionTools, context, data, user);

        if (!TurbineSecurity.isAnonymousUser(user))
        {
            if (user.hasLoggedIn())
            {
                populateWithSessionTools(authorizedTools, context, data, user);
                populateWithPermTools(persistentTools, context, data, user);
            }
        }
    }

    /**
     * Populate the given context with all request, session, authorized
     * and persistent scope tools (it is assumed that the context
     * already wraps the global context, and thus already contains
     * the global tools).
     *
     * @param context a Velocity Context to populate
     * @param data a PipelineData object for request specific data
     */
    public void populateContext(Context context, PipelineData pipelineData)
    {
       // Map runDataMap = (Map) pipelineData.get(RunData.class);
       // RunData data = (RunData)runDataMap.get(RunData.class);
        RunData data = (RunData)pipelineData;

        populateWithRequestTools(context, pipelineData);
        // session tools (whether session-only or persistent are
        // very similar, so the same method is used - the
        // boolean parameter indicates whether get/setPerm is to be used
        // rather than get/setTemp)

        //
        // Session Tool start right at the session once the user has been set
        // while persistent and authorized Tools are started when the user has
        // logged in
        //
        User user = data.getUser();

        // Note: Session tools are currently lost after the login action
        // because the anonymous user is replaced the the real user object.
        // We should either store the session pull tools in the session or
        // make Turbine.loginAction() copy the session pull tools into the
        // new user object.
        populateWithSessionTools(sessionTools, context, pipelineData, user);

        if (!TurbineSecurity.isAnonymousUser(user))
        {
            if (user.hasLoggedIn())
            {
                populateWithSessionTools(authorizedTools, context, pipelineData, user);
                populateWithPermTools(persistentTools, context, pipelineData, user);
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
        for (Iterator it = globalTools.iterator(); it.hasNext();)
        {
            ToolData toolData = (ToolData) it.next();
            try
            {
                Object tool = toolData.toolClass.newInstance();

                // global tools are init'd with a null data parameter
                initTool(tool, null);

                // put the tool in the context
                context.put(toolData.toolName, tool);
            }
            catch (Exception e)
            {
                log.error("Could not instantiate global tool "
                    + toolData.toolName + " from a "
                    + toolData.toolClassName + " object", e);
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
        // Iterate the tools
        for (Iterator it = requestTools.iterator(); it.hasNext();)
        {
            ToolData toolData = (ToolData) it.next();
            try
            {
                // Fetch Object through the Pool.
                Object tool = pool.getInstance(toolData.toolClass);

                // request tools are init'd with a RunData object
                initTool(tool, data);

                // put the tool in the context
                context.put(toolData.toolName, tool);
            }
            catch (Exception e)
            {
                log.error("Could not instantiate request tool "
                    + toolData.toolName + " from a "
                    + toolData.toolClassName + " object", e);
            }
        }
    }

    
    /**
     * Populate the given context with the request-scope tools
     *
     * @param context a Velocity Context to populate
     * @param data a RunData instance
     */
    private void populateWithRequestTools(Context context, PipelineData pipelineData)
    {
        // Iterate the tools
        for (Iterator it = requestTools.iterator(); it.hasNext();)
        {
            ToolData toolData = (ToolData) it.next();
            try
            {
                // Fetch Object through the Pool.
                Object tool = pool.getInstance(toolData.toolClass);

                initTool(tool, pipelineData);

                // put the tool in the context
                context.put(toolData.toolName, tool);
            }
            catch (Exception e)
            {
                log.error("Could not instantiate request tool "
                    + toolData.toolName + " from a "
                    + toolData.toolClassName + " object", e);
            }
        }
    }

    /**
     * Populate the given context with the session-scoped tools.
     *
     * @param tools The list of tools with which to populate the
     * session.
     * @param context The context to populate.
     * @param data The current RunData object
     * @param user The <code>User</code> object whose storage to
     * retrieve the tool from.
     */
    private void populateWithSessionTools(List tools, Context context,
            PipelineData pipelineData, User user)
    {
        //Map runDataMap = (Map)pipelineData.get(RunData.class);
        //RunData data = (RunData) runDataMap.get(RunData.class);
        RunData runData = (RunData)pipelineData;
        // Iterate the tools
        for (Iterator it = tools.iterator(); it.hasNext();)
        {
            ToolData toolData = (ToolData) it.next();
            try
            {
                // ensure that tool is created only once for a user
                // by synchronizing against the user object
                synchronized (runData.getSession())
                {
                    // first try and fetch the tool from the user's
                    // hashtable
                    Object tool = runData.getSession().getAttribute(
                            SESSION_TOOLS_ATTRIBUTE_PREFIX
                            + toolData.toolClassName);

                    if (tool == null)
                    {
                        // if not there, an instance must be fetched from
                        // the pool
                        tool = pool.getInstance(toolData.toolClass);

                        // session tools are init'd with the User object
                        initTool(tool, user);

                        // store the newly created tool in the session
                        runData.getSession().setAttribute(
                                SESSION_TOOLS_ATTRIBUTE_PREFIX
                                + tool.getClass().getName(), tool);
                    }

                    // *NOT* else
                    if(tool != null)
                    {
                        // This is a semantics change. In the old
                        // Turbine, Session tools were initialized and
                        // then refreshed every time they were pulled
                        // into the context if "refreshToolsPerRequest"
                        // was wanted.
                        //
                        // RunDataApplicationTools now have a parameter
                        // for refresh. If it is not refreshed immediately
                        // after init(), the parameter value will be undefined
                        // until the 2nd run. So we refresh all the session
                        // tools on every run, even if we just init'ed it.
                        //

                        if (refreshToolsPerRequest)
                        {
                            refreshTool(tool, pipelineData);
                        }

                        // put the tool in the context
                        log.debug("Adding " + tool + " to ctx as "
                                + toolData.toolName);
                        context.put(toolData.toolName, tool);
                    }
                    else
                    {
                        log.info("Tool " + toolData.toolName
                                + " was null, skipping it.");
                    }
                }
            }
            catch (Exception e)
            {
                log.error("Could not instantiate session tool "
                    + toolData.toolName + " from a "
                    + toolData.toolClassName + " object", e);
            }
        }
    }

    /**
     * Populate the given context with the session-scoped tools.
     *
     * @param tools The list of tools with which to populate the
     * session.
     * @param context The context to populate.
     * @param data The current RunData object
     * @param user The <code>User</code> object whose storage to
     * retrieve the tool from.
     */
    private void populateWithSessionTools(List tools, Context context,
            RunData data, User user)
    {
        // Iterate the tools
        for (Iterator it = tools.iterator(); it.hasNext();)
        {
            ToolData toolData = (ToolData) it.next();
            try
            {
                // ensure that tool is created only once for a user
                // by synchronizing against the user object
                synchronized (data.getSession())
                {
                    // first try and fetch the tool from the user's
                    // hashtable
                    Object tool = data.getSession().getAttribute(
                            SESSION_TOOLS_ATTRIBUTE_PREFIX
                            + toolData.toolClassName);

                    if (tool == null)
                    {
                        // if not there, an instance must be fetched from
                        // the pool
                        tool = pool.getInstance(toolData.toolClass);

                        // session tools are init'd with the User object
                        initTool(tool, user);

                        // store the newly created tool in the session
                        data.getSession().setAttribute(
                                SESSION_TOOLS_ATTRIBUTE_PREFIX
                                + tool.getClass().getName(), tool);
                    }

                    // *NOT* else
                    if(tool != null)
                    {
                        // This is a semantics change. In the old
                        // Turbine, Session tools were initialized and
                        // then refreshed every time they were pulled
                        // into the context if "refreshToolsPerRequest"
                        // was wanted.
                        //
                        // RunDataApplicationTools now have a parameter
                        // for refresh. If it is not refreshed immediately
                        // after init(), the parameter value will be undefined
                        // until the 2nd run. So we refresh all the session
                        // tools on every run, even if we just init'ed it.
                        //

                        if (refreshToolsPerRequest)
                        {
                            refreshTool(tool, data);
                        }

                        // put the tool in the context
                        log.debug("Adding " + tool + " to ctx as "
                                + toolData.toolName);
                        context.put(toolData.toolName, tool);
                    }
                    else
                    {
                        log.info("Tool " + toolData.toolName
                                + " was null, skipping it.");
                    }
                }
            }
            catch (Exception e)
            {
                log.error("Could not instantiate session tool "
                    + toolData.toolName + " from a "
                    + toolData.toolClassName + " object", e);
            }
        }
    }

    
    
    /**
     * Populate the given context with the perm-scoped tools.
     *
     * @param tools The list of tools with which to populate the
     * session.
     * @param context The context to populate.
     * @param data The current RunData object
     * @param user The <code>User</code> object whose storage to
     * retrieve the tool from.
     */
    private void populateWithPermTools(List tools, Context context,
            PipelineData pipelineData, User user)
    {
        // Iterate the tools
        for (Iterator it = tools.iterator(); it.hasNext();)
        {
            ToolData toolData = (ToolData) it.next();
            try
            {
                // ensure that tool is created only once for a user
                // by synchronizing against the user object
                synchronized (user)
                {
                    // first try and fetch the tool from the user's
                    // hashtable
                    Object tool = user.getPerm(toolData.toolClassName);

                    if (tool == null)
                    {
                        // if not there, an instance must be fetched from
                        // the pool
                        tool = pool.getInstance(toolData.toolClass);

                        // session tools are init'd with the User object
                        initTool(tool, user);

                        // store the newly created tool in the user's hashtable
                        user.setPerm(toolData.toolClassName, tool);
                    }

                    // *NOT* else
                    if(tool != null)
                    {
                        // This is a semantics change. In the old
                        // Turbine, Session tools were initialized and
                        // then refreshed every time they were pulled
                        // into the context if "refreshToolsPerRequest"
                        // was wanted.
                        //
                        // RunDataApplicationTools now have a parameter
                        // for refresh. If it is not refreshed immediately
                        // after init(), the parameter value will be undefined
                        // until the 2nd run. So we refresh all the session
                        // tools on every run, even if we just init'ed it.
                        //

                        if (refreshToolsPerRequest)
                        {
                            refreshTool(tool, pipelineData);
                        }

                        // put the tool in the context
                        log.debug("Adding " + tool + " to ctx as "
                                + toolData.toolName);
                        log.warn("Persistent scope tools are deprecated.");
                        context.put(toolData.toolName, tool);
                    }
                    else
                    {
                        log.info("Tool " + toolData.toolName
                                + " was null, skipping it.");
                    }
                }
            }
            catch (Exception e)
            {
                log.error("Could not instantiate perm tool "
                    + toolData.toolName + " from a "
                    + toolData.toolClassName + " object", e);
            }
        }
    }

    /**
     * Populate the given context with the perm-scoped tools.
     *
     * @param tools The list of tools with which to populate the
     * session.
     * @param context The context to populate.
     * @param data The current RunData object
     * @param user The <code>User</code> object whose storage to
     * retrieve the tool from.
     */
    private void populateWithPermTools(List tools, Context context,
            RunData data, User user)
    {
        // Iterate the tools
        for (Iterator it = tools.iterator(); it.hasNext();)
        {
            ToolData toolData = (ToolData) it.next();
            try
            {
                // ensure that tool is created only once for a user
                // by synchronizing against the user object
                synchronized (user)
                {
                    // first try and fetch the tool from the user's
                    // hashtable
                    Object tool = user.getPerm(toolData.toolClassName);

                    if (tool == null)
                    {
                        // if not there, an instance must be fetched from
                        // the pool
                        tool = pool.getInstance(toolData.toolClass);

                        // session tools are init'd with the User object
                        initTool(tool, user);

                        // store the newly created tool in the user's hashtable
                        user.setPerm(toolData.toolClassName, tool);
                    }

                    // *NOT* else
                    if(tool != null)
                    {
                        // This is a semantics change. In the old
                        // Turbine, Session tools were initialized and
                        // then refreshed every time they were pulled
                        // into the context if "refreshToolsPerRequest"
                        // was wanted.
                        //
                        // RunDataApplicationTools now have a parameter
                        // for refresh. If it is not refreshed immediately
                        // after init(), the parameter value will be undefined
                        // until the 2nd run. So we refresh all the session
                        // tools on every run, even if we just init'ed it.
                        //

                        if (refreshToolsPerRequest)
                        {
                            refreshTool(tool, data);
                        }

                        // put the tool in the context
                        log.debug("Adding " + tool + " to ctx as "
                                + toolData.toolName);
                        log.warn("Persistent scope tools are deprecated.");
                        context.put(toolData.toolName, tool);
                    }
                    else
                    {
                        log.info("Tool " + toolData.toolName
                                + " was null, skipping it.");
                    }
                }
            }
            catch (Exception e)
            {
                log.error("Could not instantiate perm tool "
                    + toolData.toolName + " from a "
                    + toolData.toolClassName + " object", e);
            }
        }
    }

    
    
    /**
     * Return the absolute path to the resources directory
     * used by the application tools.
     *
     * @return the absolute path of the resources directory
     */
    public String getAbsolutePathToResourcesDirectory()
    {
        return Turbine.getRealPath(resourcesDirectory);
    }

    /**
     * Return the resources directory. This is
     * relative to the web context.
     *
     * @return the relative path of the resources directory
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
     * @deprecated Will be made private after 2.3
     */
    public void refreshGlobalTools()
    {
        for (Iterator it = globalTools.iterator(); it.hasNext();)
        {
            ToolData toolData = (ToolData) it.next();
            Object tool = globalContext.get(toolData.toolName);
            refreshTool(tool, null);
        }
    }

    /**
     * Should we refresh the ToolBox on
     * a per request basis.
     * @deprecated No longer needed as Pull and Velocity Service are now more separate.
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
        // only the request tools can be released - other scoped
        // tools will have continuing references to them
        releaseTools(context, requestTools);
    }

    /**
     * Release the given list of tools from the context back
     * to the pool
     *
     * @param context the Context containing the tools
     * @param tools a List of ToolData objects
     */
    private void releaseTools(Context context, List tools)
    {
        for (Iterator it = tools.iterator(); it.hasNext();)
        {
            ToolData toolData = (ToolData) it.next();
            Object tool = context.remove(toolData.toolName);

            if (tool != null)
            {
                pool.putInstance(tool);
            }
        }
    }

    /**
     * Initialized a given Tool with the passed init Object
     *
     * @param tool A Tool Object
     * @param param The Init Parameter
     *
     * @throws Exception If anything went wrong.
     */
    private void initTool(Object tool, Object param)
        throws Exception
    {
        if (param instanceof PipelineData)
        {
            if (tool instanceof PipelineDataApplicationTool)
            {
                ((PipelineDataApplicationTool) tool).init((PipelineData)param);
            }
            else if (tool instanceof RunDataApplicationTool)
            {
                RunData data = getRunData((PipelineData)param);
                ((RunDataApplicationTool) tool).init(data);
            }
            else if (tool instanceof ApplicationTool)
            {
                RunData data = getRunData((PipelineData)param);
                ((ApplicationTool) tool).init(data);
            }
        } 
        else
        {
            if (tool instanceof PipelineDataApplicationTool)
            {
                ((PipelineDataApplicationTool) tool).init(param);
            }
            else if (tool instanceof RunDataApplicationTool)
            {
                ((RunDataApplicationTool) tool).init(param);
            }
            else if (tool instanceof ApplicationTool)
            {
                ((ApplicationTool) tool).init(param);
            }
        }
    }

    /**
     * Refresh a given Tool.
     *
     * @param tool A Tool Object
     * @param data The current RunData Object
     */
    private void refreshTool(Object tool, Object dataObject)
    {
        RunData data = null;
        PipelineData pipelineData = null;
        if (dataObject instanceof PipelineData)
        {
            pipelineData = (PipelineData)dataObject;
            data = getRunData(pipelineData);
            if (tool instanceof PipelineDataApplicationTool)
            {
                ((PipelineDataApplicationTool) tool).refresh(pipelineData);
            }
        }
        if (tool instanceof ApplicationTool)
        {
            ((ApplicationTool) tool).refresh();
        }
        else if (tool instanceof RunDataApplicationTool)
        {
            ((RunDataApplicationTool) tool).refresh(data);
        }
    }
    
    private RunData getRunData(PipelineData pipelineData)
    {
        if(!(pipelineData instanceof RunData)){
            throw new RuntimeException("Can't cast to rundata from pipeline data.");
        }
        return (RunData)pipelineData;
    }
}
