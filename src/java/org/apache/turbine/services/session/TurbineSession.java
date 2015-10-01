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


import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.apache.turbine.om.security.User;
import org.apache.turbine.services.TurbineServices;

/**
 * This is a conveience class provided to allow access to the SessionService
 * through static methods.  The SessionService should ALWAYS be accessed
 * through this class.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 * @see org.apache.turbine.services.session.SessionService
 */
public abstract class TurbineSession
{
    /**
     * Gets a list of the active sessions
     *
     * @return List of HttpSession objects
     */
    public static Collection<HttpSession> getActiveSessions()
    {
        return getService().getActiveSessions();
    }

    /**
     * Adds a session to the current list.  This method should only be
     * called by the listener.
     *
     * @param session Session to add
     */
    public static void addSession(HttpSession session)
    {
        getService().addSession(session);
    }

    /**
     * Removes a session from the current list.  This method should only be
     * called by the listener.
     *
     * @param session Session to remove
     */
    public static void removeSession(HttpSession session)
    {
        getService().removeSession(session);
    }

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
    public static boolean isUserLoggedIn(User user)
    {
        return getService().isUserLoggedIn(user);
    }

    /**
     * Gets a collection of all user objects representing the users currently
     * logged in.  This will exclude any instances of anonymous user that
     * Turbine will use before the user actually logs on.
     *
     * @return collection of org.apache.turbine.om.security.User objects
     */
    public static Collection<User> getActiveUsers()
    {
        return getService().getActiveUsers();
    }

    /**
     * Utility method for accessing the service
     * implementation
     *
     * @return a IntakeService implementation instance
     */
    private static SessionService getService()
    {
        return (SessionService) TurbineServices
                .getInstance().getService(SessionService.SERVICE_NAME);
    }

    /**
     * Gets the User object of the the specified HttpSession.
     *
     * @param session
     * @return the user from the session
     */
    public static User getUserFromSession(HttpSession session)
    {
        return getService().getUserFromSession(session);
    }

    /**
     * Gets the HttpSession by the session identifier
     *
     * @param sessionId the id of the session
     * @return the session for the given id
     */
    public static HttpSession getSession(String sessionId)
    {
        return getService().getSession(sessionId);
    }

    /**
     * Get a collection of all session on which the given user
     * is logged in.
     *
     * @param user the user
     * @return Collection of HtttSession objects
     */
    public static Collection<HttpSession> getSessionsForUser(User user)
    {
        return getService().getSessionsForUser(user);
    }
}
