package org.apache.turbine.services.security.ldap;

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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Hashtable;
import javax.servlet.http.HttpSessionBindingEvent;
import org.apache.turbine.om.BaseObject;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.Log;

/**
 * LDAPUser implements User and provides access to a user who accesses the
 * system via LDAP.
 *
 * @author <a href="mailto:cberry@gluecode.com">Craig D. Berry</a>
 * @author <a href="mailto:tadewunmi@gluecode.com">Tracy M. Adewunmi</a>
 * @author <a href="mailto:lflournoy@gluecode.com">Leonard J. Flournoy </a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 */
public class LDAPUser extends BaseObject implements User
{
    /* A few attributes common to a User. */
    private java.util.Date createDate = null;
    private java.util.Date lastAccessDate = null;
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
        setHasLoggedIn(new Boolean(false));
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
            return ( (Integer) getTemp(User.SESSION_ACCESS_COUNTER)).
                    intValue();
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
      *
      */
    public String getConfirmed()
    {
        String tmp = null;
        try
        {
            tmp = (String) getPerm (User.CONFIRM_VALUE);
            if (tmp.length() == 0)
                tmp = null;
        }
        catch (Exception e)
        {
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
        try
        {
            tmp = (String) getPerm (User.EMAIL);
            if (tmp.length() == 0)
                tmp = null;
        }
        catch (Exception e)
        {
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
            setLastAccessDate();
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
    public Object getPerm (String name)
    {
        return permStorage.get (name);
    }

    /**
      * Get an object from permanent storage; return default if value
      * is null.
      *
      * @param name The object's name.
      * @param def A default value to return.
      * @return An Object with the given name.
      */
    public Object getPerm (String name, Object def)
    {
        try
        {
            Object val = permStorage.get (name);
            if (val == null)
                return def;
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
    public Object getTemp (String name)
    {
        return tempStorage.get (name);
    }

    /**
      * Get an object from temporary storage; return default if value
      * is null.
      *
      * @param name The object's name.
      * @param def A default value to return.
      * @return An Object with the given name.
      */
    public Object getTemp (String name, Object def)
    {
        Object val;
        try
        {
            val = tempStorage.get (name);
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
      * Returns the username for this user.  If this is defined, then
      * the user is considered logged in.
      *
      * @return A String with the username.
      */
    public String getUserName()
    {
        String tmp = null;
        try
        {
            tmp = (String) getPerm (User.USERNAME);
            if (tmp.length() == 0)
                tmp = null;
        }
        catch (Exception e)
        {
        }
        return tmp;
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
        try
        {
            tmp = (String) getPerm (User.FIRST_NAME);
            if (tmp.length() == 0)
                tmp = null;
        }
        catch (Exception e)
        {
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
        try
        {
            tmp = (String) getPerm (User.LAST_NAME);
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
      * @return True if the user has logged in.
      */
    public boolean hasLoggedIn()
    {
        Boolean tmp = getHasLoggedIn();
        if (tmp != null && tmp.booleanValue())
            return true;
        else
            return false;
    }

    /**
      * This method reports whether or not the user has been confirmed
      * in the system by checking the <code>CONFIRM_VALUE</code>
      * column to see if it is equal to <code>CONFIRM_DATA</code>.
      *
      * @param user The User object.
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
    public Object removeTemp (String name)
    {
        return tempStorage.remove (name);
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
     */
    public void setConfirmed(String confirm)
    {
        getPerm (User.CONFIRM_VALUE, confirm);
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
     */
    public void setEmail(String email)
    {
        getPerm (User.EMAIL, email);
    }

    /**
      * Set the users First Name
      *
      */
    public void setFirstName(String fname)
    {
        setPerm (User.FIRST_NAME, fname);
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
      *
      */
    public void setLastName(String lname)
    {
        setPerm (User.LAST_NAME, lname);
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
    public void setPerm (String name, Object value)
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
            this.tempStorage = new Hashtable();
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
        return (Boolean) getTemp (User.HAS_LOGGED_IN);
    }

    /**
      * This sets whether or not someone has logged in.  hasLoggedIn()
      * returns this value.
      *
      * @param value Whether someone has logged in or not.
      */
    public void setHasLoggedIn (Boolean value)
    {
        setTemp (User.HAS_LOGGED_IN, value);
    }

    /**
      * Put an object into temporary storage.
      *
      * @param name The object's name.
      * @param value The object.
      */
    public void setTemp (String name, Object value)
    {
        tempStorage.put (name, value);
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
      * Sets the username for this user.
      *
      * @param username The user's username.
      */
    public void setUserName(String username)
    {
        setPerm (User.USERNAME, username);
    }

    /**
      * Updates the last login date in the database.
      *
      * @exception Exception, a generic exception.
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
            org.apache.turbine.util.Log.error("BaseUser.valueUnbobund(): "+
                    e.getMessage());
            org.apache.turbine.util.Log.error(e);

            // To prevent messages being lost in case the logging system
            // goes away before sessions get unbound on servlet container
            // shutdown, print the stcktrace to the container's console.
            ByteArrayOutputStream ostr = new ByteArrayOutputStream();
            e.printStackTrace(new PrintWriter(ostr, true));
            String stackTrace = ostr.toString();
            System.out.println(stackTrace);
        }
    }

    public String getName()
    {
        return null;
    }
    
    public void setName(String name)
    {
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
}
