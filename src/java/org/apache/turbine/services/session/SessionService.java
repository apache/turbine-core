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

import java.util.Collection;
import javax.servlet.http.HttpSession;

import org.apache.turbine.om.security.User;
import org.apache.turbine.services.Service;

/**
 * The SessionService allows access to the current sessions of the current context.
 * The session objects that are cached by this service are obtained through
 * a listener.  The listener must be configured in your web.xml file.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 * @see org.apache.turbine.services.session.SessionListener
 */
public interface SessionService extends Service
{

    /**
     * The key under which this service is stored in TurbineServices.
     */
    public static final String SERVICE_NAME = "SessionService";

    /**
     * Gets all active sessions
     *
     * @return Collection of HttpSession objects
     */
    public Collection getActiveSessions();

    /**
     * Adds a session to the current list.  This method should only be
     * called by the listener.
     *
     * @param session Session to add
     */
    public void addSession(HttpSession session);

    /**
     * Removes a session from the current list.  This method should only be
     * called by the listener.
     *
     * @param session Session to remove
     */
    public void removeSession(HttpSession session);

    /**
     * Determines if a given user is currently logged in.  The actual
     * implementation of the User object must implement the equals()
     * method.  By default, Torque based objects (liek TurbineUser)
     * have an implementation of equals() that will compare the
     * result of getPrimaryKey().
     *
     * @param user User to check for
     * @return true if the user is logged in on one of the
     * active sessions.
     */
    public boolean isUserLoggedIn(User user);

    /**
     * Gets a collection of all user objects representing the users currently
     * logged in.  This will exclude any instances of anonymous user that
     * Turbine will use before the user actually logs on.
     *
     * @return collection of org.apache.turbine.om.security.User objects
     */
    public Collection getActiveUsers();

}
