package org.apache.turbine.services.security.ldap;

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

import java.util.Properties;

import org.apache.turbine.services.security.TurbineSecurity;

/**
 * <p>This is a static class for defining the default ldap confiquration
 * keys used by core Turbine components.</p>
 *
 *
 */
public class LDAPSecurityConstants
{
    /** Property key */
    static final String LDAP_ADMIN_USERNAME_KEY = "ldap.admin.username";

    /** Property key */
    static final String LDAP_ADMIN_PASSWORD_KEY = "ldap.admin.password";

    /** Property key */
    static final String LDAP_HOST_KEY = "ldap.host";

    /** Property default value */
    static final String LDAP_HOST_DEFAULT = "localhost";

    /** Property key */
    static final String LDAP_PORT_KEY = "ldap.port";

    /** Property default value */
    static final String LDAP_PORT_DEFAULT = "389";

    /** Property key */
    static final String LDAP_PROVIDER_KEY = "ldap.provider";

    /** Property default value */
    static final String LDAP_PROVIDER_DEFAULT =
            "com.sun.jndi.ldap.LdapCtxFactory";

    /** Property key */
    static final String LDAP_BASE_SEARCH_KEY = "ldap.basesearch";

    /** Property key */
    static final String LDAP_AUTH_KEY = "ldap.security.authentication";

    /** Property default value */
    static final String LDAP_AUTH_DEFAULT = "simple";

    /** Property key */
    static final String LDAP_USER_USERID_KEY = "ldap.user.userid";

    /** Property key */
    static final String LDAP_USER_USERNAME_KEY = "ldap.user.username";

    /** Property default value */
    static final String LDAP_USER_USERNAME_DEFAULT = "turbineUserUniqueId";

    /** Property key */
    static final String LDAP_USER_FIRSTNAME_KEY = "ldap.user.firstname";

    /** Property default value */
    static final String LDAP_USER_FIRSTNAME_DEFAULT = "turbineUserFirstName";

    /** Property key */
    static final String LDAP_USER_LASTNAME_KEY = "ldap.user.lastname";

    /** Property default value */
    static final String LDAP_USER_LASTNAME_DEFAULT = "turbineUserLastName";

    /** Property key */
    static final String LDAP_USER_EMAIL_KEY = "ldap.user.email";

    /** Property default value */
    static final String LDAP_USER_EMAIL_DEFAULT = "turbineUserMailAddress";

    /** Property key */
    static final String LDAP_USER_PASSWORD_KEY = "ldap.user.password";

    /** Property default value */
    static final String LDAP_USER_PASSWORD_DEFAULT = "userPassword";

    /**
     * Get all the properties for the security service.
     * @return all the properties of the security service.
     */
    public static Properties getProperties()
    {
        return TurbineSecurity.getService().getProperties();
    }

    /**
     * Get a property from the LDAP security service.
     * @param key The key to access the value of the property.
     * @return The value of the property.
     */
    public static String getProperty(String key)
    {
        return getProperties().getProperty(key);
    }

    /**
     * Get a property from the LDAP security service.
     * @param key The key to access the value of the property.
     * @param defaultValue The value that the property takes
     *        when it doesn't exist.
     * @return The value of the property.
     */
    public static String getProperty(String key, String defaultValue)
    {
        return getProperties().getProperty(key, defaultValue);
    }

    /**
     * Get the value of the property for the administration username.
     * @return the value of the property.
     */
    public static String getAdminUsername()
    {
        String str = getProperty(LDAP_ADMIN_USERNAME_KEY);

        /*
         * The adminUsername string contains some
         * characters that need to be transformed.
         */
        str = str.replace('/', '=');
        str = str.replace('%', ',');
        return str;
    }

    /**
     * Get the value of the property for the administration password.
     * @return the value of the property.
     */
    public static String getAdminPassword()
    {
        return getProperty(LDAP_ADMIN_PASSWORD_KEY);
    }

    /**
     * Get the value of the property for the LDAP Host.
     * @return the value of the property.
     */
    public static String getLDAPHost()
    {
        return getProperty(LDAP_HOST_KEY, LDAP_HOST_DEFAULT);
    }

    /**
     * Get the value of the property for the LDAP Port.
     * @return the value of the property.
     */
    public static String getLDAPPort()
    {
        return getProperty(LDAP_PORT_KEY, LDAP_PORT_DEFAULT);
    }

    /**
     * Get the value of the property for the  LDAP Provider.
     * @return the value of the property.
     */
    public static String getLDAPProvider()
    {
        return getProperty(LDAP_PROVIDER_KEY, LDAP_PROVIDER_DEFAULT);
    }

    /**
     * Get value of the property for the Base Search.
     * @return the value of the property.
     */
    public static String getBaseSearch()
    {
        String str = getProperty(LDAP_BASE_SEARCH_KEY);

        /*
         * The userBaseSearch string contains some
         * characters that need to be transformed.
         */
        str = str.replace('/', '=');
        str = str.replace('%', ',');
        return str;
    }

    /**
     * Get the value of the property for the Authentication
     * mechanism. Valid values are: none, simple,
     * @return the value of the property.
     */
    public static String getLDAPAuthentication()
    {
        return getProperty(LDAP_AUTH_KEY, LDAP_AUTH_DEFAULT);
    }

    /**
     * Get the value of the User id Attribute.
     * @return the value of the property.
     */
    public static String getUserIdAttribute()
    {
        return getProperty(LDAP_USER_USERID_KEY);
    }

    /**
     * Get the value of the Username Attribute.
     * @return the value of the property.
     */
    public static String getNameAttribute()
    {
        return getProperty(LDAP_USER_USERNAME_KEY, LDAP_USER_USERNAME_DEFAULT);
    }

    /**
     * Get the value of the Username Attribute.
     * @return the value of the property.
     * @deprecated Use getNameAttribute()
     */
    public static String getUserNameAttribute()
    {
        return getNameAttribute();
    }

    /**
     * Get the value of the Firstname Attribute.
     * @return the value of the property.
     */
    public static String getFirstNameAttribute()
    {
        return getProperty(LDAP_USER_FIRSTNAME_KEY,
                LDAP_USER_FIRSTNAME_DEFAULT);
    }

    /**
     * Get the value of the Lastname Attribute.
     * @return the value of the property.
     */
    public static String getLastNameAttribute()
    {
        return getProperty(LDAP_USER_LASTNAME_KEY, LDAP_USER_LASTNAME_DEFAULT);
    }

    /**
     * Get the value of the Password Attribute.
     * @return the value of the property.
     */
    public static String getPasswordAttribute()
    {
        return getProperty(LDAP_USER_PASSWORD_KEY, LDAP_USER_PASSWORD_DEFAULT);
    }

    /**
     * Get the value of the E-Mail Attribute.
     * @return the value of the property.
     */
    public static String getEmailAttribute()
    {
        return getProperty(LDAP_USER_EMAIL_KEY, LDAP_USER_EMAIL_DEFAULT);
    }

}
