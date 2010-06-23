package org.apache.turbine.services.security.ldap;

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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.servlet.http.HttpSessionBindingEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.security.TurbineSecurity;

/**
 * LDAPUser implements User and provides access to a user who accesses the
 * system via LDAP.
 *
 * @author <a href="mailto:cberry@gluecode.com">Craig D. Berry</a>
 * @author <a href="mailto:tadewunmi@gluecode.com">Tracy M. Adewunmi</a>
 * @author <a href="mailto:lflournoy@gluecode.com">Leonard J. Flournoy </a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:hhernandez@itweb.com.mx">Humberto Hernandez</a>
 */
public class LDAPUser implements User
{

    /** Logging */
    private static Log log = LogFactory.getLog(LDAPUser.class);

    /* A few attributes common to a User. */

    /** Date when the user was created */
    private java.util.Date createDate = null;

    /** Date when the user was last accessed */
    private java.util.Date lastAccessDate = null;

    /** timeout */
    private int timeout = 15;

    /** This is data that will survive a servlet engine restart. */
    private Hashtable permStorage = null;

    /** This is data that will not survive a servlet engine restart. */
    private Hashtable tempStorage = null;

    /**
     * Constructor.
     * Create a new User and set the createDate.
     */
    public LDAPUser()
    {
        createDate = new java.util.Date();
        tempStorage = new Hashtable(10);
        permStorage = new Hashtable(10);
        setHasLoggedIn(Boolean.FALSE);
    }

    /**
     * Populates the user with values obtained from the LDAP Service.
     * This method could be redefined in subclasses.
     * @param attribs The attributes obtained from LDAP.
     * @throws NamingException if there was an error with JNDI.
     */
    public void setLDAPAttributes(Attributes attribs)
            throws NamingException
    {

        Attribute attr;
        String attrName;

        // Set the User id.
        attrName = LDAPSecurityConstants.getUserIdAttribute();
        if (attrName != null)
        {
            attr = attribs.get(attrName);
            if (attr != null && attr.get() != null)
            {
                try
                {
                    //setPrimaryKey(attr.get().toString());
                }
                catch (Exception ex)
                {
                    log.error("Exception caught:", ex);
                }
            }
        }

        // Set the Username.
        attrName = LDAPSecurityConstants.getNameAttribute();
        if (attrName != null)
        {
            attr = attribs.get(attrName);
            if (attr != null && attr.get() != null)
            {
                setName(attr.get().toString());
            }
        }
        else
        {
            log.error("There is no LDAP attribute for the username.");
        }

        // Set the Firstname.
        attrName = LDAPSecurityConstants.getFirstNameAttribute();
        if (attrName != null)
        {
            attr = attribs.get(attrName);
            if (attr != null && attr.get() != null)
            {
                setFirstName(attr.get().toString());
            }
        }

        // Set the Lastname.
        attrName = LDAPSecurityConstants.getLastNameAttribute();
        if (attrName != null)
        {
            attr = attribs.get(attrName);
            if (attr != null && attr.get() != null)
            {
                setLastName(attr.get().toString());
            }
        }

        // Set the E-Mail
        attrName = LDAPSecurityConstants.getEmailAttribute();
        if (attrName != null)
        {
            attr = attribs.get(attrName);
            if (attr != null && attr.get() != null)
            {
                setEmail(attr.get().toString());
            }
        }
    }

    /**
     * Get the JNDI Attributes used to store the user in LDAP.
     * This method could be redefined in a subclass.
     *
     * @throws NamingException if there is a JNDI error.
     * @return The JNDI attributes of the user.
     */
    public Attributes getLDAPAttributes()
            throws NamingException
    {
        Attributes attribs = new BasicAttributes();
        String attrName;

        // Set the objectClass
        attrName = "objectClass";
        if (attrName != null)
        {
            Object value = "turbineUser";

            if (value != null)
            {
                Attribute attr = new BasicAttribute(attrName, value);

                attribs.put(attr);
            }
        }

        // Set the User id.
        attrName = LDAPSecurityConstants.getUserIdAttribute();
        if (attrName != null)
        {
            Object value = this.getIdAsObj();

            if (value != null)
            {
                Attribute attr = new BasicAttribute(attrName, value);

                attribs.put(attr);
            }
        }

        // Set the Username.
        attrName = LDAPSecurityConstants.getNameAttribute();
        if (attrName != null)
        {
            Object value = getName();

            if (value != null)
            {
                Attribute attr = new BasicAttribute(attrName, value);

                attribs.put(attr);
            }
        }

        // Set the Firstname.
        attrName = LDAPSecurityConstants.getFirstNameAttribute();
        if (attrName != null)
        {
            Object value = getFirstName();

            if (value != null)
            {
                Attribute attr = new BasicAttribute(attrName, value);

                attribs.put(attr);
            }
        }

        // Set the Lastname.
        attrName = LDAPSecurityConstants.getLastNameAttribute();
        if (attrName != null)
        {
            Object value = getLastName();

            if (value != null)
            {
                Attribute attr = new BasicAttribute(attrName, value);

                attribs.put(attr);
            }
        }

        // Set the E-Mail.
        attrName = LDAPSecurityConstants.getEmailAttribute();
        if (attrName != null)
        {
            Object value = getEmail();

            if (value != null)
            {
                Attribute attr = new BasicAttribute(attrName, value);

                attribs.put(attr);
            }
        }

        // Set the Password
        attrName = LDAPSecurityConstants.getPasswordAttribute();
        if (attrName != null)
        {
            Object value = getPassword();

            if (value != null)
            {
                Attribute attr = new BasicAttribute(attrName, value);

                attribs.put(attr);
            }
        }

        return attribs;
    }

    /**
     * Gets the distinguished name (DN) of the User.
     * This method could be redefined in a subclass.
     * @return The Distinguished Name of the user.
     */
    public String getDN()
    {
        String filterAttribute = LDAPSecurityConstants.getNameAttribute();
        String userBaseSearch = LDAPSecurityConstants.getBaseSearch();
        String userName = getName();

        String dn = filterAttribute + "=" + userName + "," + userBaseSearch;

        return dn;
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
     * Returns the value of Confirmed variable
     * @return the confirm value.
     */
    public String getConfirmed()
    {
        String tmp = null;

        tmp = (String) getPerm(User.CONFIRM_VALUE);
        if (tmp != null && tmp.length() == 0)
        {
            tmp = null;
        }
        return tmp;
    }

    /**
     * Returns the Email for this user.  If this is defined, then
     * the user is considered logged in.
     *
     * @return A String with the user's Email.
     */
    public String getEmail()
    {
        String tmp = null;

        tmp = (String) getPerm(User.EMAIL);
        if (tmp != null && tmp.length() == 0)
        {
            tmp = null;
        }
        return tmp;
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
     * @param name The object's name.
     * @return An Object with the given name.
     */
    public Object getPerm(String name)
    {
        return permStorage.get(name);
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
        try
        {
            Object val = permStorage.get(name);

            if (val == null)
            {
                return def;
            }
            return val;
        }
        catch (Exception e)
        {
            return def;
        }
    }

    /**
     * This should only be used in the case where we want to save the
     * data to the database.
     *
     * @return A Hashtable.
     */
    public Hashtable getPermStorage()
    {
        if (this.permStorage == null)
        {
            this.permStorage = new Hashtable();
        }
        return this.permStorage;
    }

    /**
     * Get an object from temporary storage.
     *
     * @param name The object's name.
     * @return An Object with the given name.
     */
    public Object getTemp(String name)
    {
        return tempStorage.get(name);
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
            val = tempStorage.get(name);
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
     * A User object can have a variable Timeout, which is defined in
     * minutes.  If the user has been timed out, then the
     * hasLoggedIn() value will return false.
     *
     * @return An int specifying the timeout.
     */
    public int getTimeout()
    {
        return this.timeout;
    }


    /**
     * Returns the first name for this user.  If this is defined, then
     * the user is considered logged in.
     *
     * @return A String with the user's first name.
     */
    public String getFirstName()
    {
        String tmp = null;

        tmp = (String) getPerm(User.FIRST_NAME);
        if (tmp != null && tmp.length() == 0)
        {
            tmp = null;
        }
        return tmp;
    }

    /**
     * Returns the last name for this user.  If this is defined, then
     * the user is considered logged in.
     *
     * @return A String with the user's last name.
     */
    public String getLastName()
    {
        String tmp = null;

        tmp = (String) getPerm(User.LAST_NAME);
        if (tmp != null && tmp.length() == 0)
        {
            tmp = null;
        }
        return tmp;
    }

    /**
     * The user is considered logged in if they have not timed out.
     *
     * @return True if the user has logged in.
     */
    public boolean hasLoggedIn()
    {
        Boolean tmp = getHasLoggedIn();

        if (tmp != null && tmp.booleanValue())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * This method reports whether or not the user has been confirmed
     * in the system by checking the <code>CONFIRM_VALUE</code>
     * column to see if it is equal to <code>CONFIRM_DATA</code>.
     *
     * @return True if the user has been confirmed.
     */
    public boolean isConfirmed()
    {
        return ((String) getTemp(CONFIRM_VALUE, "")).equals(CONFIRM_DATA);
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
        return tempStorage.remove(name);
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
     * Set the users confirmed variable
     *
     * @param confirm The new confim value.
     */
    public void setConfirmed(String confirm)
    {
        getPerm(User.CONFIRM_VALUE, confirm);
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
     * Set the users Email
     *
     * @param email The new email.
     */
    public void setEmail(String email)
    {
        setPerm(User.EMAIL, email);
    }

    /**
     * Set the users First Name
     *
     * @param fname The new firstname.
     */
    public void setFirstName(String fname)
    {
        setPerm(User.FIRST_NAME, fname);
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
     * Set the users Last Name
     * Sets the last name for this user.
     *
     * @param lname The new lastname.
     */
    public void setLastName(String lname)
    {
        setPerm(User.LAST_NAME, lname);
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
     * Put an object into permanent storage.
     *
     * @param name The object's name.
     * @param value The object.
     */
    public void setPerm(String name, Object value)
    {
        permStorage.put(name, value);
    }

    /**
     * This should only be used in the case where we want to save the
     * data to the database.
     *
     * @param stuff A Hashtable.
     */
    public void setPermStorage(Hashtable stuff)
    {
        this.permStorage = stuff;
    }

    /**
     * This should only be used in the case where we want to save the
     * data to the database.
     *
     * @return A Hashtable.
     */
    public Hashtable getTempStorage()
    {
        if (this.tempStorage == null)
        {
            this.tempStorage = new Hashtable();
        }
        return this.tempStorage;
    }

    /**
     * This should only be used in the case where we want to save the
     * data to the database.
     *
     * @param storage A Hashtable.
     */
    public void setTempStorage(Hashtable storage)
    {
        this.tempStorage = storage;
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
     * Put an object into temporary storage.
     *
     * @param name The object's name.
     * @param value The object.
     */
    public void setTemp(String name, Object value)
    {
        tempStorage.put(name, value);
    }

    /**
     * A User object can have a variable Timeout which is defined in
     * minutes.  If the user has been timed out, then the
     * hasLoggedIn() value will return false.
     *
     * @param time The user's timeout.
     */
    public void setTimeout(int time)
    {
        this.timeout = time;
    }

    /**
     * Updates the last login date in the database.
     *
     * @exception Exception a generic exception.
     */
    public void updateLastLogin() throws Exception
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
        // Do not currently need this method.
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
                TurbineSecurity.saveUser(this);
            }
        }
        catch (Exception e)
        {
            log.error("BaseUser.valueUnbobund(): "
                    + e.getMessage());
            log.error(e);

            // To prevent messages being lost in case the logging system
            // goes away before sessions get unbound on servlet container
            // shutdown, print the stcktrace to the container's console.
            ByteArrayOutputStream ostr = new ByteArrayOutputStream();

            e.printStackTrace(new PrintWriter(ostr, true));
            String stackTrace = ostr.toString();

            System.out.println(stackTrace);
        }
    }

    /**
     * Returns the username for this user.  If this is defined, then
     * the user is considered logged in.
     *
     * @return A String with the username.
     */
    public String getName()
    {
        String tmp = null;

        tmp = (String) getPerm(User.USERNAME);
        if (tmp != null && tmp.length() == 0)
        {
            tmp = null;
        }
        return tmp;
    }

    /**
     * Set the users name.
     * @param name the name of the User.
     */
    public void setName(String name)
    {
		setPerm(User.USERNAME, name);
    }

    /**
     * Not implemented.
     * @return 0
     */
    public int getId()
    {
        return 0;
    }

    /**
     * Not implemented.
     * @return null
     */
    public Integer getIdAsObj()
    {
        return new Integer(0);
    }

    /**
     * Not implemented.
     *
     * @param id The id of the User.
     */
    public void setId(int id)
    {
    }

    /**
     * Saves this object to the data store.
     * @throws Exception if it cannot be saved
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
     * @param conn the database connection
     * @throws Exception if there is an error
     */
    public void save(Connection conn) throws Exception
    {
        throw new Exception("not implemented");
    }

    /**
     * not implemented
     *
     * @param dbname the database name
     * @throws Exception if there is an error
     */
    public void save(String dbname) throws Exception
    {
        throw new Exception("not implemented");
    }

}
