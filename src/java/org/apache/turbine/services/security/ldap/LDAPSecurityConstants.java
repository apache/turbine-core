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

import java.util.Properties;
import org.apache.turbine.services.security.TurbineSecurity;


/**
 * <p>This is a static class for defining the default ldap confiquration
 * keys used by core Turbine components.</p>
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:hhernandez@itweb.com.mx">Humberto Hernandez</a>
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
    public static String getUserNameAttribute()
    {
        return getProperty(LDAP_USER_USERNAME_KEY, LDAP_USER_USERNAME_DEFAULT);
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
