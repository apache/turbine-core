package org.apache.turbine.services.session;


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


import java.io.Serializable;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.turbine.services.TurbineServices;

/**
 * This class is a listener for both session creation and destruction,
 * and for session activation and passivation.  It must be configured
 * via your web application's <code>web.xml</code> deployment
 * descriptor as follows for the container to call it:
 *
 * <blockquote><code><pre>
 * &lt;listener&gt;
 *   &lt;listener-class&gt;
 *     org.apache.turbine.session.SessionListener
 *   &lt;/listener-class&gt;
 * &lt;/listener&gt;
 * </pre></code></blockquote>
 *
 * <code>&lt;listener&gt;</code> elemements can occur between
 * <code>&lt;context-param&gt;</code> and <code>&lt;servlet&gt;</code>
 * elements in your deployment descriptor.
 *
 * The {@link #sessionCreated(HttpSessionEvent)} callback will
 * automatically add an instance of this listener to any newly created
 * <code>HttpSession</code> for detection of session passivation and
 * re-activation.
 *
 * @since 2.3
 * @version $Id$
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:dlr@apache.org">Daniel Rall</a>
 * @see javax.servlet.http.HttpSessionListener
 */
public class SessionListener
        implements HttpSessionListener, HttpSessionActivationListener, Serializable
{
    // ---- HttpSessionListener implementation -----------------------------

    /**
     * Serial version.
     */
    private static final long serialVersionUID = -8083730704842809870L;

    /**
     * The session service.
     */
    private SessionService sessionService;

    /**
     * Lazy initialization
     *
     * @return the sessionService
     */
    private SessionService getSessionService()
    {
        // don't care about synchronization, lookup is cheap
        if (sessionService == null)
        {
            sessionService = (SessionService)TurbineServices.getInstance().getService(SessionService.SERVICE_NAME);
        }

        return sessionService;
    }

    /**
     * Called by the servlet container when a new session is created
     *
     * @param event Session creation event.
     */
    @Override
    public void sessionCreated(HttpSessionEvent event)
    {
        getSessionService().addSession(event.getSession());
        event.getSession().setAttribute(getClass().getName(), this);
    }

    /**
     * Called by the servlet container when a session is destroyed
     *
     * @param event Session destruction event.
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent event)
    {
        getSessionService().removeSession(event.getSession());
    }


    // ---- HttpSessionActivationListener implementation -------------------

    /**
     * Called by the servlet container when an existing session is
     * (re-)activated.
     *
     * @param event Session activation event.
     */
    @Override
    public void sessionDidActivate(HttpSessionEvent event)
    {
        getSessionService().addSession(event.getSession());
    }

    /**
     * Called by the servlet container when a an existing session is
     * passivated.
     *
     * @param event Session passivation event.
     */
    @Override
    public void sessionWillPassivate(HttpSessionEvent event)
    {
        getSessionService().removeSession(event.getSession());
    }
}
