package org.apache.turbine.services.session;

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

import java.io.Serializable;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionActivationListener;

/**
 * This class is a listener for both session creation and destruction,
 * and for session activation and passivation.  It must be configured
 * via your web application's <code>web.xml</code> deployment
 * descriptor as follows for the container to call it:
 *
 * <blockquote><code><pre>
 * <listener>
 *   <listener-class>
 *     org.apache.turbine.session.SessionListener
 *   </listener-class>
 * </listener>
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
     * Called by the servlet container when a new session is created
     *
     * @param event Session creation event.
     */
    public void sessionCreated(HttpSessionEvent event)
    {
        TurbineSession.addSession(event.getSession());
        event.getSession().setAttribute(getClass().getName(), this);
    }

    /**
     * Called by the servlet container when a session is destroyed
     *
     * @param event Session destruction event.
     */
    public void sessionDestroyed(HttpSessionEvent event)
    {
        TurbineSession.removeSession(event.getSession());
    }


    // ---- HttpSessionActivationListener implementation -------------------

    /**
     * Called by the servlet container when an existing session is
     * (re-)activated.
     *
     * @param event Session activation event.
     */
    public void sessionDidActivate(HttpSessionEvent event)
    {
        TurbineSession.addSession(event.getSession());
    }

    /**
     * Called by the servlet container when a an existing session is
     * passivated.
     *
     * @param event Session passivation event.
     */
    public void sessionWillPassivate(HttpSessionEvent event)
    {
        TurbineSession.removeSession(event.getSession());
    }
}
