package org.apache.turbine.services.session;

import java.util.Collection;

import org.apache.turbine.services.pull.ApplicationTool;

import javax.servlet.http.HttpSession;
import org.apache.turbine.om.security.User;

/**
 */
public class SessionTool
        implements ApplicationTool
{
    public void init(Object o)
    {
    }

    public void refresh()
    {
    }

    /**
     * Gets a list of the active sessions
     *
     * @return List of HttpSession objects
     */
    public Collection getActiveSessions()
    {
        return TurbineSession.getActiveSessions();
    }

    /**
     * Adds a session to the current list.  This method should only be
     * called by the listener.
     *
     * @param session Session to add
     */
    public void addSession(HttpSession session)
    {
        TurbineSession.addSession(session);
    }

    /**
     * Removes a session from the current list.  This method should only be
     * called by the listener.
     *
     * @param session Session to remove
     */
    public void removeSession(HttpSession session)
    {
        TurbineSession.removeSession(session);
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
    public boolean isUserLoggedIn(User user)
    {
        return TurbineSession.isUserLoggedIn(user);
    }

    /**
     * Gets a collection of all user objects representing the users currently
     * logged in.  This will exclude any instances of anonymous user that
     * Turbine will use before the user actually logs on.
     *
     * @return collection of org.apache.turbine.om.security.User objects
     */
    public Collection getActiveUsers()
    {
        return TurbineSession.getActiveUsers();
    }

    /**
     * Gets the User object of the the specified HttpSession.
     *
     * @param session
     * @return
     */
    public User getUserFromSession(HttpSession session)
    {
        return TurbineSession.getUserFromSession(session);
    }

}
