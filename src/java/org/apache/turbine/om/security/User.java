package org.apache.turbine.om.security;

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

import java.util.Date;
import java.util.Hashtable;
import java.io.Serializable;
import javax.servlet.http.HttpSessionBindingListener;
import org.apache.turbine.util.RunData;

/**
 * This interface represents functionality that all users of the
 * Turbine system require.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:jon@collab.net">Jon S. Stevens</a>
 * @author <a href="mailto:cberry@gluecode.com">Craig D. Berry</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public interface User
    extends HttpSessionBindingListener, Serializable, SecurityEntity
{
    /** The 'perm storage' key name for the first name. */
    static final String FIRST_NAME = "FIRST_NAME";

    /** The 'perm storage' key name for the last name. */
    static final String LAST_NAME = "LAST_NAME";

    /** The 'perm storage' key name for the last_login field. */
    static final String LAST_LOGIN = "LAST_LOGIN";

    /** The 'perm storage' key name for the password field. */
    static final String PASSWORD = "PASSWORD_VALUE";

    /** The 'perm storage' key name for the username field. */
    static final String USERNAME = "LOGIN_NAME";

    /** The 'perm storage' key for the confirm_value field. */
    static final String CONFIRM_VALUE = "CONFIRM_VALUE";

    /** The 'perm storage' key for the email field. */
    static final String EMAIL = "EMAIL";

    /** This is the value that is stored in the database for confirmed users */
    static final String CONFIRM_DATA = "CONFIRMED";

    /** The 'perm storage' key name for the access counter. */
    static final String ACCESS_COUNTER = "_access_counter";

    /** The 'temp storage' key name for the session access counter */
    static final String SESSION_ACCESS_COUNTER = "_session_access_counter";

    /** The 'temp storage' key name for the 'has logged in' flag */
    static final String HAS_LOGGED_IN = "_has_logged_in";

    /** The session key for the User object. */
    static final String SESSION_KEY = "turbine.user";

    /**
     * Gets the access counter for a user from perm storage.
     *
     * @return The access counter for the user.
     */
    int getAccessCounter();

    /**
     * Gets the access counter for a user during a session.
     *
     * @return The access counter for the user for the session.
     */
    int getAccessCounterForSession();

    /**
     * Gets the last access date for this User. This is the last time
     * that the user object was referenced.
     *
     * @return A Java Date with the last access date for the user.
     */
    java.util.Date getLastAccessDate();

    /**
     * Gets the create date for this User.  This is the time at which
     * the user object was created.
     *
     * @return A Java Date with the date of creation for the user.
     */
    java.util.Date getCreateDate();

    /**
     * Returns the user's last login date.
     *
     * @return A Java Date with the last login date for the user.
     */
    java.util.Date getLastLogin();

    /**
     * Returns the user's password. This method should not be used by
     * the application directly, because it's meaning depends upon
     * the implementation of UserManager that manages this particular
     * user object. Some implementations will use this attribute for
     * storing a password encrypted in some way, other will not use
     * it at all, when user entered password is presented to some external
     * authority (like NT domain controller) to validate it.
     * See also {@link org.apache.turbine.services.security.UserManager#authenticate(User,String)}.
     *
     * @return A String with the password for the user.
     */
    String getPassword();

    /**
     * Get an object from permanent storage.
     *
     * @param name The object's name.
     * @return An Object with the given name.
     */
    Object getPerm(String name);

    /**
     * Get an object from permanent storage; return default if value
     * is null.
     *
     * @param name The object's name.
     * @param def A default value to return.
     * @return An Object with the given name.
     */
    Object getPerm(String name, Object def);

    /**
     * This should only be used in the case where we want to save the
     * data to the database.
     *
     * @return A Hashtable.
     */
    Hashtable getPermStorage();

    /**
     * This should only be used in the case where we want to save the
     * data to the database.
     *
     * @return A Hashtable.
     */
    Hashtable getTempStorage();

    /**
     * Get an object from temporary storage.
     *
     * @param name The object's name.
     * @return An Object with the given name.
     */
    Object getTemp(String name);

    /**
     * Get an object from temporary storage; return default if value
     * is null.
     *
     * @param name The object's name.
     * @param def A default value to return.
     * @return An Object with the given name.
     */
    Object getTemp(String name, Object def);

    /**
     * Returns the username for this user.
     *
     * @return A String with the username.
     */
    String getUserName();

    /**
     * Returns the first name for this user.
     *
     * @return A String with the user's first name.
     */

    String getFirstName();

    /**
     * Returns the last name for this user.
     *
     * @return A String with the user's last name.
     */
    String getLastName();

    /**
     * Returns the email address for this user.
     *
     * @return A String with the user's email address.
     */
    String getEmail();

    /**
     * This sets whether or not someone has logged in.  hasLoggedIn()
     * returns this value.
     *
     * @param value Whether someone has logged in or not.
     */
    void setHasLoggedIn(Boolean value);

    /**
     * The user is considered logged in if they have not timed out.
     *
     * @return True if the user has logged in.
     */
    boolean hasLoggedIn();

    /**
     * Increments the permanent hit counter for the user.
     */
    void incrementAccessCounter();

    /**
     * Increments the session hit counter for the user.
     */
    void incrementAccessCounterForSession();

    /**
     * Remove an object from temporary storage and return the object.
     *
     * @param name The name of the object to remove.
     * @return An Object.
     */
    Object removeTemp(String name);

    /**
     * Sets the access counter for a user, saved in perm storage.
     *
     * @param cnt The new count.
     */
    void setAccessCounter(int cnt);

    /**
     * Sets the session access counter for a user, saved in temp
     * storage.
     *
     * @param cnt The new count.
     */
    void setAccessCounterForSession(int cnt);

    /**
     * Sets the last access date for this User. This is the last time
     * that the user object was referenced.
     */
    void setLastAccessDate();

    /**
     * Set last login date/time.
     *
     * @param lastLogin The last login date.
     */
    void setLastLogin(java.util.Date lastLogin);

    /**
     * Set password. Application should not use this method
     * directly, see {@link #getPassword()}.
     * See also {@link org.apache.turbine.services.security.UserManager#changePassword(User,String,String)}.
     *
     * @param password The new password.
     */

    void setPassword(String password);

    /**
     * Put an object into permanent storage.
     *
     * @param name The object's name.
     * @param value The object.
     */
    void setPerm(String name,
                 Object value);

    /**
     * This should only be used in the case where we want to save the
     * data to the database.
     *
     * @param storage A Hashtable.
     */
    void setPermStorage(Hashtable storage);

    /**
     * This should only be used in the case where we want to save the
     * data to the database.
     *
     * @param storage A Hashtable.
     */
    void setTempStorage(Hashtable storage);

    /**
     * Put an object into temporary storage.
     *
     * @param name The object's name.
     * @param value The object.
     */
    void setTemp(String name, Object value);

    /**
     * Sets the username for this user.
     *
     * @param username The user's username.
     */
    void setUserName(String username);

    /**
     * Sets the first name for this user.
     *
     * @param firstName User's first name.
     */
    void setFirstName(String firstName);

    /**
     * Sets the last name for this user.
     *
     * @param lastName User's last name.
     */
    void setLastName(String lastName);

    /**
     * Sets the creation date for this user.
     *
     * @param date Creation date
     */
    void setCreateDate(java.util.Date date);

    /**
     * Sets the email address.
     *
     * @param address The email address.
     */
    void setEmail(String address);

    /**
     * This method reports whether or not the user has been confirmed
     * in the system by checking the TurbineUserPeer.CONFIRM_VALUE
     * column to see if it is equal to CONFIRM_DATA.
     *
     * @return True if the user has been confirmed.
     */
    boolean isConfirmed();

    /**
     * Sets the confirmation value.
     *
     * @param value The confirmation key value.
     */
    void setConfirmed(String value);

    /**
     * Gets the confirmation value.
     *
     * @return The confirmed value
     */
    String getConfirmed();

    /**
     * Updates the last login date in the database.
     *
     * @exception Exception A generic exception.
     */
    void updateLastLogin()
        throws Exception;
}
