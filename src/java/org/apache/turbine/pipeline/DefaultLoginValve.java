package org.apache.turbine.pipeline;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.ActionLoader;
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
 * @version $Id$
 */
public class DefaultLoginValve
    extends AbstractValve
    implements TurbineConstants
{
    /**
     * Here we can setup objects that are thread safe and can be
     * reused. We setup the session validator and the access
     * controller.
     */
    public DefaultLoginValve()
        throws Exception
    {
    }

    /**
     * @see org.apache.turbine.Valve#invoke(RunData, ValveContext)
     */
    public void invoke(RunData data, ValveContext context)
        throws IOException, TurbineException
    {
        try
        { 
            process(data);
        }
        catch (Exception e)
        {
            throw new TurbineException(e);
        }

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(data);
    }

    /**
     * Handles user sessions, parsing of the action from the query
     * string, and access control.
     *
     * @param data The run-time data.
     */
    protected void process(RunData data)
        throws Exception
    {
        // Special case for login and logout, this must happen before the
        // session validator is executed in order either to allow a user to
        // even login, or to ensure that the session validator gets to
        // mandate its page selection policy for non-logged in users
        // after the logout has taken place.
        String actionName = data.getAction();
        if (data.hasAction() &&
            actionName.equalsIgnoreCase
            (Turbine.getConfiguration().getString(ACTION_LOGIN_KEY)) ||
            actionName.equalsIgnoreCase
            (Turbine.getConfiguration().getString(ACTION_LOGOUT_KEY)))
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
                (Turbine.getConfiguration().getString(ACTION_LOGIN_KEY)))
            {
                Enumeration names = data.getSession().getAttributeNames();
                if (names != null)
                {
                    // copy keys into a new list, so we can clear the session
                    // and not get ConcurrentModificationException
                    List nameList = new ArrayList();
                    while (names.hasMoreElements())
                    {
                        nameList.add(names.nextElement());
                    }

                    HttpSession session = data.getSession();
                    Iterator nameIter = nameList.iterator();
                    while (nameIter.hasNext())
                    {
                        try
                        {
                            session.removeAttribute((String)nameIter.next());
                        }
                        catch (IllegalStateException invalidatedSession)
                        {
                            break;
                        }
                    }
                }
            }

            
            ActionLoader.getInstance().exec(data, data.getAction());
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
