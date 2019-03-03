package org.apache.turbine.om.security;

import java.util.Date;

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

import java.util.Map;

import javax.servlet.http.HttpSessionBindingListener;

import org.apache.fulcrum.security.model.turbine.entity.TurbineUser;

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
    extends HttpSessionBindingListener, TurbineUserDelegate, TurbineUser
{
    /** The 'perm storage' key name for the create_date field. */
    String CREATE_DATE = "CREATE_DATE";

    /** The 'perm storage' key name for the last_login field. */
    String LAST_LOGIN = "LAST_LOGIN";

    /** The 'perm storage' key for the confirm_value field. */
    String CONFIRM_VALUE = "CONFIRM_VALUE";

    /** This is the value that is stored in the database for confirmed users */
    String CONFIRM_DATA = "CONFIRMED";

    /** The 'perm storage' key name for the access counter. */
    String ACCESS_COUNTER = "_access_counter";

    /** The 'temp storage' key name for the session access counter */
    String SESSION_ACCESS_COUNTER = "_session_access_counter";

    /** The 'temp storage' key name for the 'has logged in' flag */
    String HAS_LOGGED_IN = "_has_logged_in";

    /** The session key for the User object. */
    String SESSION_KEY = "turbine.user";

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
    Date getLastAccessDate();

    /**
     * Gets the create date for this User.  This is the time at which
     * the user object was created.
     *
     * @return A Java Date with the date of creation for the user.
     */
    Date getCreateDate();

    /**
     * Returns the user's last login date.
     *
     * @return A Java Date with the last login date for the user.
     */
    Date getLastLogin();

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
     * @return A Map.
     */
    Map<String, Object> getPermStorage();

    /**
     * This should only be used in the case where we want to save the
     * data to the database.
     *
     * @return A Map.
     */
    Map<String, Object> getTempStorage();

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
    void setLastLogin(Date lastLogin);

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
     * @param storage A Map.
     */
    void setPermStorage(Map<String, Object> storage);

    /**
     * This should only be used in the case where we want to save the
     * data to the database.
     *
     * @param storage A Map.
     */
    void setTempStorage(Map<String, Object> storage);

    /**
     * Put an object into temporary storage.
     *
     * @param name The object's name.
     * @param value The object.
     */
    void setTemp(String name, Object value);

    /**
     * Sets the creation date for this user.
     *
     * @param date Creation date
     */
    void setCreateDate(Date date);

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
     * @throws Exception A generic exception.
     */

    void updateLastLogin()
        throws Exception;
}
