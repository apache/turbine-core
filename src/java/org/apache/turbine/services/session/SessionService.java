package org.apache.turbine.services.session;

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

import java.util.Collection;
import javax.servlet.http.HttpSession;

import org.apache.turbine.om.security.User;
import org.apache.turbine.services.Service;

/**
 * The SessionService allows access to the current sessions of the current context.
 * The session objects that are cached by this service are obtained through
 * a listener.  The listener must be configured in your web.xml file.
 *
 * @since 2.3
 * @see org.apache.turbine.services.session.SessionListener
 * @version $Id$
 */
public interface SessionService extends Service
{

    /**
     * The key under which this service is stored in TurbineServices.
     */
    static final String SERVICE_NAME = "SessionService";

    /**
     * Gets all active sessions
     *
     * @return Collection of HttpSession objects
     */
    Collection getActiveSessions();

    /**
     * Adds a session to the current list.  This method should only be
     * called by the listener.
     *
     * @param session Session to add
     */
    void addSession(HttpSession session);

    /**
     * Removes a session from the current list.  This method should only be
     * called by the listener.
     *
     * @param session Session to remove
     */
    void removeSession(HttpSession session);

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
    boolean isUserLoggedIn(User user);

    /**
     * Gets a collection of all user objects representing the users currently
     * logged in.  This will exclude any instances of anonymous user that
     * Turbine will use before the user actually logs on.
     *
     * @return collection of org.apache.turbine.om.security.User objects
     */
    Collection getActiveUsers();

    /**
     * Gets the User object of the the specified HttpSession.
     *
     * @param session The session from which to extract a user.
     * @return The Turbine User object.
     */
    User getUserFromSession(HttpSession session);

    /**
     * Gets the HttpSession by the session identifier
     *
     * @param sessionId The unique session identifier.
     * @return The session keyed by the specified identifier.
     */
    HttpSession getSession(String sessionId);

    /**
     * Get a collection of all session on which the given user
     * is logged in.
     *
     * @param user the user
     * @return Collection of HtttSession objects
     */
    Collection getSessionsForUser(User user);

}
