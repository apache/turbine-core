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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSessionBindingEvent;

import org.apache.fulcrum.security.model.turbine.entity.TurbineUser;
import org.apache.fulcrum.security.model.turbine.entity.TurbineUserGroupRole;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.security.SecurityService;
import org.apache.turbine.util.ObjectUtils;

/**
 * This is the Default user implementation. It is a wrapper around
 * a TurbineUser object
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: TorqueUser.java 1199856 2011-11-09 17:06:04Z tv $
 */

public class DefaultUserImpl implements User, TurbineUserDelegate
{
    /** Serial version */
    private static final long serialVersionUID = -1866504873085624111L;

    /** The date on which the user last accessed the application. */
    private Date lastAccessDate = null;

    /** This is data that will survive a servlet engine restart. */
    private Map<String, Object> permStorage = null;

    /** This is data that will not survive a servlet engine restart. */
    private Map<String, Object> tempStorage = null;

    /** The Fulcrum user instance to delegate to */
    private TurbineUser userDelegate = null;

    /**
     * Constructor
     *
     * @param user the user object to wrap
     */
    public DefaultUserImpl(TurbineUser user)
    {
        super();
        this.userDelegate = user;
        setCreateDate(new Date());
        tempStorage = new HashMap<String, Object>(10);
        setHasLoggedIn(Boolean.FALSE);
    }

    /**
     * Implement this method if you wish to be notified when the User
     * has been Bound to the session.
     *
     * @param hsbe Indication of value/session binding.
     */
    @Override
    public void valueBound(HttpSessionBindingEvent hsbe)
    {
        // Currently we have no need for this method.
    }

    /**
     * Implement this method if you wish to be notified when the User
     * has been Unbound from the session.
     *
     * @param hsbe Indication of value/session unbinding.
     */
    @Override
    public void valueUnbound(HttpSessionBindingEvent hsbe)
    {
        try
        {
            if (hasLoggedIn())
            {
                SecurityService securityService = (SecurityService)TurbineServices.getInstance().getService(SecurityService.SERVICE_NAME);
                securityService.saveOnSessionUnbind(this);
            }
        }
        catch (Exception e)
        {
            //Log.error("TorqueUser.valueUnbound(): " + e.getMessage(), e);

            // To prevent messages being lost in case the logging system
            // goes away before sessions get unbound on servlet container
            // shutdown, print the stacktrace to the container's console.
            ByteArrayOutputStream ostr = new ByteArrayOutputStream();
            e.printStackTrace(new PrintWriter(ostr, true));
            String stackTrace = ostr.toString();
            System.out.println(stackTrace);
        }
    }

    /**
     * Get the Name of the SecurityEntity.
     *
     * @return The Name of the SecurityEntity.
     */
    @Override
    public String getName()
    {
        return userDelegate.getName();
    }

    /**
     * Sets the Name of the SecurityEntity.
     *
     * @param name
     *            Name of the SecurityEntity.
     */
    @Override
    public void setName(String name)
    {
        userDelegate.setName(name);
    }

    /**
     * Get the Id of the SecurityEntity.
     *
     * @return The Id of the SecurityEntity.
     */
    @Override
    public Object getId()
    {
        return userDelegate.getId();
    }

    /**
     * Sets the Id of the SecurityEntity.
     *
     * @param id
     *            The new Id of the SecurityEntity
     */
    @Override
    public void setId(Object id)
    {
        userDelegate.setId(id);
    }

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
    @Override
    public String getPassword()
    {
        return userDelegate.getPassword();
    }

    /**
     * Set password. Application should not use this method
     * directly, see {@link #getPassword()}.
     * See also {@link org.apache.turbine.services.security.UserManager#changePassword(User,String,String)}.
     *
     * @param password The new password.
     */
    @Override
    public void setPassword(String password)
    {
        userDelegate.setPassword(password);
    }

    /**
     * Returns the first name for this user.
     *
     * @return A String with the user's first name.
     */

    @Override
    public String getFirstName()
    {
        return userDelegate.getFirstName();
    }

    /**
     * Sets the first name for this user.
     *
     * @param firstName User's first name.
     */
    @Override
    public void setFirstName(String firstName)
    {
        userDelegate.setFirstName(firstName);
    }

    /**
     * Returns the last name for this user.
     *
     * @return A String with the user's last name.
     */
    @Override
    public String getLastName()
    {
        return userDelegate.getLastName();
    }

    /**
     * Sets the last name for this user.
     *
     * @param lastName User's last name.
     */
    @Override
    public void setLastName(String lastName)
    {
        userDelegate.setLastName(lastName);
    }

    /**
     * Returns the email address for this user.
     *
     * @return A String with the user's email address.
     */
    @Override
    public String getEmail()
    {
        return userDelegate.getEmail();
    }

    /**
     * Sets the email address.
     *
     * @param address The email address.
     */
    @Override
    public void setEmail(String address)
    {
        userDelegate.setEmail(address);
    }

    /**
     * Returns the value of the objectdata for this user.
     * Objectdata is a storage area used
     * to store the permanent storage table from the User
     * object.
     *
     * @return The bytes in the objectdata for this user
     */
    @Override
    public byte[] getObjectdata()
    {
        return userDelegate.getObjectdata();
    }

    /**
     * Sets the value of the objectdata for the user
     *
     * @param objectdata The new permanent storage for the user
     */
    @Override
    public void setObjectdata(byte[] objectdata)
    {
        userDelegate.setObjectdata(objectdata);
    }

    /**
     * Get the User/Group/Role set associated with this entity
     *
     * @return a set of User/Group/Role relations
     */
    @Override
    public <T extends TurbineUserGroupRole> Set<T> getUserGroupRoleSet()
    {
        return userDelegate.getUserGroupRoleSet();
    }

    /**
     * Set the User/Group/Role set associated with this entity
     *
     * @param userGroupRoleSet
     *            a set of User/Group/Role relations
     */
    @Override
    public <T extends TurbineUserGroupRole> void setUserGroupRoleSet(Set<T> userGroupRoleSet)
    {
        userDelegate.setUserGroupRoleSet(userGroupRoleSet);
    }

    /**
     * Add a User/Group/Role relation to this entity
     *
     * @param userGroupRole
     *            a User/Group/Role relation to add
     */
    @Override
    public void addUserGroupRole(TurbineUserGroupRole userGroupRole)
    {
        userDelegate.addUserGroupRole(userGroupRole);
    }

    /**
     * Remove a User/Group/Role relation from this entity
     *
     * @param userGroupRole
     *            a User/Group/Role relation to remove
     */
    @Override
    public void removeUserGroupRole(TurbineUserGroupRole userGroupRole)
    {
        userDelegate.removeUserGroupRole(userGroupRole);
    }

    /**
     * Gets the access counter for a user from perm storage.
     *
     * @return The access counter for the user.
     */
    @Override
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
     * Gets the access counter for a user during a session.
     *
     * @return The access counter for the user for the session.
     */
    @Override
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
     * Increments the permanent hit counter for the user.
     */
    @Override
    public void incrementAccessCounter()
    {
        // Ugh. Race city, here I come...
        setAccessCounter(getAccessCounter() + 1);
    }

    /**
     * Increments the session hit counter for the user.
     */
    @Override
    public void incrementAccessCounterForSession()
    {
        setAccessCounterForSession(getAccessCounterForSession() + 1);
    }

    /**
     * Sets the access counter for a user, saved in perm storage.
     *
     * @param cnt The new count.
     */
    @Override
    public void setAccessCounter(int cnt)
    {
        setPerm(User.ACCESS_COUNTER, Integer.valueOf(cnt));
    }

    /**
     * Sets the session access counter for a user, saved in temp
     * storage.
     *
     * @param cnt The new count.
     */
    @Override
    public void setAccessCounterForSession(int cnt)
    {
        setTemp(User.SESSION_ACCESS_COUNTER, Integer.valueOf(cnt));
    }

    /**
     * Gets the last access date for this User.  This is the last time
     * that the user object was referenced.
     *
     * @return A Java Date with the last access date for the user.
     */
    @Override
    public java.util.Date getLastAccessDate()
    {
        if (lastAccessDate == null)
        {
            setLastAccessDate();
        }
        return lastAccessDate;
    }

    /**
     * Sets the last access date for this User. This is the last time
     * that the user object was referenced.
     */
    @Override
    public void setLastAccessDate()
    {
        lastAccessDate = new java.util.Date();
    }

    /**
     * Returns the permanent storage. This is implemented
     * as a Map
     *
     * @return A Map.
     */
    @Override
    public Map<String, Object> getPermStorage()
    {
        if (permStorage == null)
        {
            byte [] objectdata = getObjectdata();

            if (objectdata != null)
            {
                permStorage = ObjectUtils.deserialize(objectdata);
            }

            if (permStorage == null)
            {
                permStorage = new HashMap<String, Object>();
            }
        }

        return permStorage;
    }

    /**
     * This should only be used in the case where we want to make the
     * data persistent.
     *
     * @param permStorage A Map.
     */
    @Override
    public void setPermStorage(Map<String, Object> permStorage)
    {
        if (permStorage != null)
        {
            this.permStorage = permStorage;
        }
    }

    /**
     * Returns the temporary storage. This is implemented
     * as a Map
     *
     * @return A Map.
     */
    @Override
    public Map<String, Object> getTempStorage()
    {
        if (tempStorage == null)
        {
            tempStorage = new HashMap<String, Object>();
        }
        return tempStorage;
    }

    /**
     * This should only be used in the case where we want to save the
     * data to the database.
     *
     * @param tempStorage A Map.
     */
    @Override
    public void setTempStorage(Map<String, Object> tempStorage)
    {
        if (tempStorage != null)
        {
            this.tempStorage = tempStorage;
        }
    }

    /**
     * Get an object from permanent storage.
     *
     * @param name The object's name.
     * @return An Object with the given name.
     */
    @Override
    public Object getPerm(String name)
    {
        return getPermStorage().get(name);
    }

    /**
     * Get an object from permanent storage; return default if value
     * is null.
     *
     * @param name The object's name.
     * @param def A default value to return.
     * @return An Object with the given name.
     */
    @Override
    public Object getPerm(String name, Object def)
    {
        try
        {
            Object val = getPermStorage().get(name);
            return (val == null ? def : val);
        }
        catch (Exception e)
        {
            return def;
        }
    }

    /**
     * Put an object into permanent storage.
     *
     * @param name The object's name.
     * @param value The object.
     */
    @Override
    public void setPerm(String name, Object value)
    {
        getPermStorage().put(name, value);
    }

    /**
     * Get an object from temporary storage.
     *
     * @param name The object's name.
     * @return An Object with the given name.
     */
    @Override
    public Object getTemp(String name)
    {
        return getTempStorage().get(name);
    }

    /**
     * Get an object from temporary storage; return default if value
     * is null.
     *
     * @param name The object's name.
     * @param def A default value to return.
     * @return An Object with the given name.
     */
    @Override
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
     * Put an object into temporary storage.
     *
     * @param name The object's name.
     * @param value The object.
     */
    @Override
    public void setTemp(String name, Object value)
    {
        getTempStorage().put(name, value);
    }

    /**
     * Remove an object from temporary storage and return the object.
     *
     * @param name The name of the object to remove.
     * @return An Object.
     */
    @Override
    public Object removeTemp(String name)
    {
        return getTempStorage().remove(name);
    }

    /**
     * Returns the confirm value of the user
     *
     * @return The confirm value of the user
     */
    @Override
    public String getConfirmed()
    {
        return (String) getPerm(User.CONFIRM_VALUE);
    }

    /**
     * Sets the new confirm value of the user
     *
     * @param confirm The new confirm value of the user
     */
    @Override
    public void setConfirmed(String confirm)
    {
        setPerm(User.CONFIRM_VALUE, confirm);
    }

    /**
     * Returns the creation date of the user
     *
     * @return The creation date of the user
     */
    @Override
    public java.util.Date getCreateDate()
    {
        return (java.util.Date)getPerm(CREATE_DATE, new java.util.Date());
    }

    /**
     * Sets the new creation date of the user
     *
     * @param createDate The new creation date of the user
     */
    @Override
    public void setCreateDate(java.util.Date createDate)
    {
        setPerm(CREATE_DATE, createDate);
    }

    /**
     * Returns the date of the last login of the user
     *
     * @return The date of the last login of the user
     */
    @Override
    public java.util.Date getLastLogin()
    {
        return (java.util.Date) getPerm(User.LAST_LOGIN);
    }

    /**
     * Sets the new date of the last login of the user
     *
     * @param lastLogin The new the date of the last login of the user
     */
    @Override
    public void setLastLogin(java.util.Date lastLogin)
    {
        setPerm(User.LAST_LOGIN, lastLogin);
    }

    /**
     * The user is considered logged in if they have not timed out.
     *
     * @return Whether the user has logged in.
     */
    @Override
    public boolean hasLoggedIn()
    {
        Boolean loggedIn = (Boolean) getTemp(User.HAS_LOGGED_IN);
        return (loggedIn != null && loggedIn.booleanValue());
    }

    /**
     * This sets whether or not someone has logged in.  hasLoggedIn()
     * returns this value.
     *
     * @param value Whether someone has logged in or not.
     */
    @Override
    public void setHasLoggedIn(Boolean value)
    {
        setTemp(User.HAS_LOGGED_IN, value);
    }

    /**
     * This method reports whether or not the user has been confirmed
     * in the system by checking the User.CONFIRM_VALUE
     * column in the users record to see if it is equal to
     * User.CONFIRM_DATA.
     *
     * @return True if the user has been confirmed.
     */
    @Override
    public boolean isConfirmed()
    {
        String value = getConfirmed();
        return (value != null && value.equals(User.CONFIRM_DATA));
    }

    /**
     * Updates the last login date in the database.
     *
     * @throws Exception A generic exception.
     */
    @Override
    public void updateLastLogin()
        throws Exception
    {
        setLastLogin(new java.util.Date());
    }

    /* (non-Javadoc)
	 * @see org.apache.turbine.om.security.UserDelegate#getUserDelegate()
	 */
    @Override
	public TurbineUser getUserDelegate()
    {
        return userDelegate;
    }

    /* (non-Javadoc)
	 * @see org.apache.turbine.om.security.UserDelegate#setUserDelegate(org.apache.fulcrum.security.model.turbine.entity.TurbineUser)
	 */
    @Override
	public void setUserDelegate(TurbineUser userDelegate)
    {
        this.userDelegate = userDelegate;
    }
}
