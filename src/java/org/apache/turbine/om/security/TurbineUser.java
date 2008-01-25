package org.apache.turbine.om.security;


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


import java.sql.Connection;

import java.util.Date;
import java.util.Hashtable;

import javax.servlet.http.HttpSessionBindingEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.services.security.TurbineSecurity;

/**
 * A generic implementation of User interface.
 *
 * This basic implementation contains the functionality that is
 * expected to be common among all User implementations.
 * You are welcome to extend this class if you wish to have
 * custom functionality in your user objects (like accessor methods
 * for custom attributes). <b>Note</b> that implementing a different scheme
 * of user data storage normally involves writing an implementation of
 * {@link org.apache.turbine.services.security.UserManager} interface.
 *
 * @author <a href="mailto:josh@stonecottage.com">Josh Lucas</a>
 * @author <a href="mailto:jon@collab.net">Jon S. Stevens</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:cberry@gluecode.com">Craig D. Berry</a>
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TurbineUser extends SecurityObject implements User
{
    /** Logging */
    private static Log log = LogFactory.getLog(TurbineUser.class);

    /** The date on which the user account was created. */
    private Date createDate = null;

    /** The date on which the user last accessed the application. */
    private Date lastAccessDate = null;

    /** This is data that will survive a servlet engine restart. */
    private Hashtable permStorage = null;

    /** This is data that will not survive a servlet engine restart. */
    private Hashtable tempStorage = null;

    /**
     * Constructor.
     *
     * Create a new User and set the createDate.
     */
    public TurbineUser()
    {
        super();
        createDate = new Date();
        setHasLoggedIn(Boolean.FALSE);
    }

    /**
     * Gets the access counter for a user during a session.
     *
     * @return The access counter for the user for the session.
     */
    public int getAccessCounterForSession()
    {
        try
        {
            return ((Integer) getTemp(User.SESSION_ACCESS_COUNTER)).intValue();
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    /**
     * Gets the access counter for a user from perm storage.
     *
     * @return The access counter for the user.
     */
    public int getAccessCounter()
    {
        try
        {
            return ((Integer) getPerm(User.ACCESS_COUNTER)).intValue();
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    /**
     * Gets the create date for this User.  This is the time at which
     * the user object was created.
     *
     * @return A Java Date with the date of creation for the user.
     */
    public java.util.Date getCreateDate()
    {
        return createDate;
    }

    /**
     * Gets the last access date for this User.  This is the last time
     * that the user object was referenced.
     *
     * @return A Java Date with the last access date for the user.
     */
    public java.util.Date getLastAccessDate()
    {
        if (lastAccessDate == null)
        {
            setLastAccessDate();
        }
        return lastAccessDate;
    }

    /**
     * Get last login date/time for this user.
     *
     * @return A Java Date with the last login date for the user.
     */
    public java.util.Date getLastLogin()
    {
        return (java.util.Date) getPerm(User.LAST_LOGIN);
    }

    /**
     * Get password for this user.
     *
     * @return A String with the password for the user.
     */
    public String getPassword()
    {
        return (String) getPerm(User.PASSWORD);
    }

    /**
     * Get an object from permanent storage.
     *
     * @param name The object's name.
     * @return An Object with the given name, or null if not found.
     */
    public Object getPerm(String name)
    {
        return getPerm(name,null);
    }

    /**
     * Get an object from permanent storage; return default if value
     * is null.
     *
     * @param name The object's name.
     * @param def A default value to return.
     * @return An Object with the given name.
     */
    public Object getPerm(String name, Object def)
    {
        Object val;
        try
        {
            val = getPermStorage().get(name);
            if (val == null)
            {
                val = def;
            }
        }
        catch (Exception e)
        {
            val = def;
        }
        return val;
    }

    /**
     * This should only be used in the case where we want to save the
     * data to the database.
     *
     * @return A Hashtable.
     */
    public Hashtable getPermStorage()
    {
        if (permStorage == null)
        {
            permStorage = new Hashtable(10);
        }
        return permStorage;
    }

    /**
     * Get an object from temporary storage; return null if the
     * object can't be found.
     *
     * @param name The object's name.
     * @return An Object with the given name.
     */
    public Object getTemp(String name)
    {
        return getTemp(name, null);
    }

    /**
     * Get an object from temporary storage; return default if value
     * is null.
     *
     * @param name The object's name.
     * @param def A default value to return.
     * @return An Object with the given name.
     */
    public Object getTemp(String name, Object def)
    {
        Object val;
        try
        {
            val = getTempStorage().get(name);
            if (val == null)
            {
                val = def;
            }

        }
        catch (Exception e)
        {
            val = def;
        }
        return val;
    }


    /**
     * Returns the first name for this user.
     *
     * @return A String with the user's first name.
     */
    public String getFirstName()
    {
        String tmp = null;
        try
        {
            tmp = (String) getPerm(User.FIRST_NAME);
            if(tmp.length() == 0)
            {
                tmp = null;
            }
        }
        catch (Exception e)
        {
        }
        return tmp;
    }

    /**
     * Returns the last name for this user.
     *
     * @return A String with the user's last name.
     */
    public String getLastName()
    {
        String tmp = null;
        try
        {
            tmp = (String) getPerm(User.LAST_NAME);
            if (tmp.length() == 0)
                tmp = null;
        }
        catch (Exception e)
        {
        }
        return tmp;
    }

    /**
     * The user is considered logged in if they have not timed out.
     *
     * @return Whether the user has logged in.
     */
    public boolean hasLoggedIn()
    {
        Boolean loggedIn = getHasLoggedIn();
        return (loggedIn != null && loggedIn.booleanValue());
    }

    /**
     * Returns the email address for this user.
     *
     * @return A String with the user's email address.
     */
    public String getEmail()
    {
        return (String) getPerm(User.EMAIL);
    }

    /**
     * Increments the permanent hit counter for the user.
     */
    public void incrementAccessCounter()
    {
        setAccessCounter(getAccessCounter() + 1);
    }

    /**
     * Increments the session hit counter for the user.
     */
    public void incrementAccessCounterForSession()
    {
        setAccessCounterForSession(getAccessCounterForSession() + 1);
    }

    /**
     * Remove an object from temporary storage and return the object.
     *
     * @param name The name of the object to remove.
     * @return An Object.
     */
    public Object removeTemp(String name)
    {
        return getTempStorage().remove(name);
    }

    /**
     * Sets the access counter for a user, saved in perm storage.
     *
     * @param cnt The new count.
     */
    public void setAccessCounter(int cnt)
    {
        setPerm(User.ACCESS_COUNTER, new Integer(cnt));
    }

    /**
     * Sets the session access counter for a user, saved in temp
     * storage.
     *
     * @param cnt The new count.
     */
    public void setAccessCounterForSession(int cnt)
    {
        setTemp(User.SESSION_ACCESS_COUNTER, new Integer(cnt));
    }

    /**
     * Sets the last access date for this User. This is the last time
     * that the user object was referenced.
     */
    public void setLastAccessDate()
    {
        lastAccessDate = new java.util.Date();
    }

    /**
     * Sets the create date for this User. This is the time at which
     * the user object was created.
     *
     * @param date The create date.
     */
    public void setCreateDate(java.util.Date date)
    {
        createDate = date;
    }

    /**
     * Set last login date/time.
     *
     * @param date The last login date.
     */
    public void setLastLogin(java.util.Date date)
    {
        setPerm(User.LAST_LOGIN, date);
    }

    /**
     * Set password.
     *
     * @param password The new password.
     */
    public void setPassword(String password)
    {
        setPerm(User.PASSWORD, password);
    }

    /**
     * Put an object into permanent storage. If the value is null,
     * it will convert that to a "" because the underlying storage
     * mechanism within TurbineUser is currently a Hashtable and
     * null is not a valid value.
     *
     * @param name The object's name.
     * @param value The object.
     */
    public void setPerm(String name, Object value)
    {
        getPermStorage().put(name, (value == null) ? "" : value);
    }

    /**
     * This should only be used in the case where we want to save the
     * data to the database.
     *
     * @param permStorage A Hashtable.
     */
    public void setPermStorage(Hashtable permStorage)
    {
        this.permStorage = permStorage;
    }

    /**
     * This should only be used in the case where we want to save the
     * data to the database.
     *
     * @return A Hashtable.
     */
    public Hashtable getTempStorage()
    {
        if (tempStorage == null)
        {
            tempStorage = new Hashtable(10);
        }
        return tempStorage;
    }

    /**
     * This should only be used in the case where we want to save the
     * data to the database.
     *
     * @param storage A Hashtable.
     */
    public void setTempStorage(Hashtable tempStorage)
    {
        this.tempStorage = tempStorage;
    }

    /**
     * This gets whether or not someone has logged in.  hasLoggedIn()
     * returns this value as a boolean.  This is private because you
     * should use hasLoggedIn() instead.
     *
     * @return True if someone has logged in.
     */
    private Boolean getHasLoggedIn()
    {
        return (Boolean) getTemp(User.HAS_LOGGED_IN);
    }

    /**
     * This sets whether or not someone has logged in.  hasLoggedIn()
     * returns this value.
     *
     * @param value Whether someone has logged in or not.
     */
    public void setHasLoggedIn(Boolean value)
    {
        setTemp(User.HAS_LOGGED_IN, value);
    }

    /**
     * Put an object into temporary storage. If the value is null,
     * it will convert that to a "" because the underlying storage
     * mechanism within TurbineUser is currently a Hashtable and
     * null is not a valid value.
     *
     * @param name The object's name.
     * @param value The object.
     */
    public void setTemp(String name, Object value)
    {
        getTempStorage().put(name, (value == null) ? "" : value);
    }


    /**
     * Sets the first name for this user.
     *
     * @param firstName User's first name.
     */
    public void setFirstName(String firstName)
    {
        setPerm(User.FIRST_NAME, firstName);
    }

    /**
     * Sets the last name for this user.
     *
     * @param lastName User's last name.
     */
    public void setLastName(String lastName)
    {
        setPerm(User.LAST_NAME, lastName);
    }

    /**
     * Sets the email address.
     *
     * @param address The email address.
     */
    public void setEmail(String address)
    {
        setPerm(User.EMAIL, address);
    }

    /**
     * This method reports whether or not the user has been confirmed
     * in the system by checking the User.CONFIRM_VALUE
     * column in the users record to see if it is equal to
     * User.CONFIRM_DATA.
     *
     * @return True if the user has been confirmed.
     */
    public boolean isConfirmed()
    {
        String value = getConfirmed();
        return (value != null && value.equals(User.CONFIRM_DATA));
    }

    /**
     * Sets the confirmation value. The value should
     * be either a random string or User.CONFIRM_DATA
     *
     * @param value The confirmation key value.
     */
    public void setConfirmed(String value)
    {
        String val = "";
        if (value != null)
        {
            val = value;
        }
        setPerm(User.CONFIRM_VALUE, val);
    }

    /**
     * Gets the confirmation value.
     *
     * @return status The confirmation value for this User
     */
    public String getConfirmed()
    {
        return (String) getPerm(User.CONFIRM_VALUE);
    }

    /**
     * Updates the last login date in the database.
     *
     * @exception Exception a generic exception.
     */
    public void updateLastLogin()
            throws Exception
    {
        setPerm(User.LAST_LOGIN, new java.util.Date());
    }

    /**
     * Implement this method if you wish to be notified when the User
     * has been Bound to the session.
     *
     * @param hsbe The HttpSessionBindingEvent.
     */
    public void valueBound(HttpSessionBindingEvent hsbe)
    {
        // Currently we have no need for this method.
    }

    /**
     * Implement this method if you wish to be notified when the User
     * has been Unbound from the session.
     *
     * @param hsbe The HttpSessionBindingEvent.
     */
    public void valueUnbound(HttpSessionBindingEvent hsbe)
    {
        try
        {
            if (hasLoggedIn())
            {
                TurbineSecurity.saveOnSessionUnbind(this);
            }
        }
        catch (Exception e)
        {
            log.error("TurbineUser.valueUnbound(): " + e.getMessage(), e);
        }
    }

    /**
     * Saves this object to the data store.
     */
    public void save()
            throws Exception
    {
        if (TurbineSecurity.accountExists(this))
        {
            TurbineSecurity.saveUser(this);
        }
        else
        {
            TurbineSecurity.addUser(this, getPassword());
        }
    }

    /**
     * not implemented
     *
     * @param conn
     * @throws Exception
     */
    public void save(Connection conn) throws Exception
    {
        throw new Exception("not implemented");
    }

    /**
     * not implemented
     *
     * @param dbname
     * @throws Exception
     */
    public void save(String dbname) throws Exception
    {
        throw new Exception("not implemented");
    }

    /**
     * Returns the name of this user.  This will be the user name/
     * login name.
     *
     * @return The name of the user.
     */
    public String getName()
    {
        return (String) getPerm(User.USERNAME);
    }

    /**
     * Sets the name of this user.  This will be the user name/
     * login name.
     *
     * @param name The name of the object.
     */
    public void setName(String name)
    {
        setPerm(User.USERNAME, name);
    }
}
