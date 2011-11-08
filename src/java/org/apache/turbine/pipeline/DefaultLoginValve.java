package org.apache.turbine.pipeline;


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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.Action;
import org.apache.turbine.modules.ActionLoader;
import org.apache.turbine.services.assemblerbroker.TurbineAssemblerBroker;
import org.apache.turbine.services.velocity.VelocityService;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.template.TemplateInfo;

/**
 * Handles the Login and Logout actions in the request process
 * cycle.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:dlr@apache.org">Daniel Rall</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class DefaultLoginValve
    extends AbstractValve
{
    private ActionLoader actionLoader;

    /**
     * Here we can setup objects that are thread safe and can be
     * reused. We setup the session validator and the access
     * controller.
     */
    public DefaultLoginValve()
        throws Exception
    {
        // empty constructor
    }

    /**
     * Initialize this valve for use in a pipeline.
     *
     * @see org.apache.turbine.pipeline.AbstractValve#initialize()
     */
    @Override
    public void initialize() throws Exception
    {
        super.initialize();

        this.actionLoader = (ActionLoader)TurbineAssemblerBroker.getLoader(Action.class);
    }

    /**
     * @see org.apache.turbine.Valve#invoke(RunData, ValveContext)
     */
    @Override
    public void invoke(PipelineData pipelineData, ValveContext context)
        throws IOException, TurbineException
    {
        try
        {
            process(pipelineData);
        }
        catch (Exception e)
        {
            throw new TurbineException(e);
        }

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(pipelineData);
    }

    /**
     * Handles user sessions, parsing of the action from the query
     * string, and access control.
     *
     * @param data The run-time data.
     */
    protected void process(PipelineData pipelineData)
        throws Exception
    {
        RunData data = getRunData(pipelineData);
        // Special case for login and logout, this must happen before the
        // session validator is executed in order either to allow a user to
        // even login, or to ensure that the session validator gets to
        // mandate its page selection policy for non-logged in users
        // after the logout has taken place.
        String actionName = data.getAction();
        if (data.hasAction() &&
            actionName.equalsIgnoreCase
            (Turbine.getConfiguration().getString(TurbineConstants.ACTION_LOGIN_KEY)) ||
            actionName.equalsIgnoreCase
            (Turbine.getConfiguration().getString(TurbineConstants.ACTION_LOGOUT_KEY)))
        {
            // If a User is logging in, we should refresh the
            // session here.  Invalidating session and starting a
            // new session would seem to be a good method, but I
            // (JDM) could not get this to work well (it always
            // required the user to login twice).  Maybe related
            // to JServ?  If we do not clear out the session, it
            // is possible a new User may accidently (if they
            // login incorrectly) continue on with information
            // associated with the previous User.  Currently the
            // only keys stored in the session are "turbine.user"
            // and "turbine.acl".
            if (actionName.equalsIgnoreCase
                (Turbine.getConfiguration().getString(TurbineConstants.ACTION_LOGIN_KEY)))
            {
                @SuppressWarnings("unchecked")
                Enumeration<String> names = data.getSession().getAttributeNames();
                if (names != null)
                {
                    // copy keys into a new list, so we can clear the session
                    // and not get ConcurrentModificationException
                    List<String> nameList = new ArrayList<String>();
                    while (names.hasMoreElements())
                    {
                        nameList.add(names.nextElement());
                    }

                    HttpSession session = data.getSession();
                    for (String name : nameList)
                    {
                        try
                        {
                            session.removeAttribute(name);
                        }
                        catch (IllegalStateException invalidatedSession)
                        {
                            break;
                        }
                    }
                }
            }

            actionLoader.exec(pipelineData, data.getAction());
            cleanupTemplateContext(data);
            data.setAction(null);
        }
    }
    /**
     * cleans the Velocity Context if available.
     *
     * @param data A RunData Object
     *
     * @throws Exception A problem while cleaning out the Template Context occured.
     */
    private void cleanupTemplateContext(RunData data)
    throws Exception
    {
        // This is Velocity specific and shouldn't be done here.
        // But this is a band aid until we get real listeners
        // here.
        TemplateInfo ti = data.getTemplateInfo();
        if (ti != null)
        {
            ti.removeTemp(VelocityService.CONTEXT);
        }
    }
}
