package org.apache.turbine.services.resources;

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

import java.util.Iterator;
import java.util.Vector;
import javax.servlet.ServletConfig;
import org.apache.turbine.services.TurbineServices;
import org.apache.commons.configuration.Configuration;

/**
 * <p>This is a static class for defining the default Turbine configuration
 * keys used by core Turbine components.</p>
 * <p>It also extends the basic static accessor class to the
 * <code>ResourcesService</code> so that you directly access the
 * properties through this class</p>
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @author <a href="mailto:luta.raphael@networks.vivendi.net">Raphaël Luta</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class TurbineResources
{
    public static final String MAIL_SERVER_KEY = "mail.server";
    public static final String MODULE_CACHE_KEY = "module.cache";
    public static final String MODULE_PACKAGES_KEY = "module.packages";
    public static final String ACTION_CACHE_SIZE_KEY = "action.cache.size";
    public static final String LAYOUT_CACHE_SIZE_KEY = "layout.cache.size";
    public static final String NAVIGATION_CACHE_SIZE_KEY = "navigation.cache.size";
    public static final String PAGE_CACHE_SIZE_KEY = "page.cache.size";
    public static final String SCREEN_CACHE_SIZE_KEY = "screen.cache.size";
    public static final String USER_CLASS_KEY = "user.class";
    public static final String MAX_FILE_SIZE_KEY = "max.file.size.bytes";
    public static final String DB_DRIVER_KEY = "database.default.driver";
    public static final String DB_URL_KEY = "database.default.url";
    public static final String DB_USERNAME_KEY = "database.default.username";
    public static final String DB_PASSWORD_KEY = "database.default.password";
    public static final String DB_MAXCONNECTIONS_KEY = "database.maxConnections";
    public static final String DB_EXPIRYTIME_KEY = "database.expiryTime";
    public static final String FILE_SERVER = "file.server";
    public static final String LOGIN_MESSAGE = "login.message";
    public static final String LOGIN_ERROR = "login.error";
    public static final String LOGIN_MESSAGE_NOSCREEN = "login.message.noscreen";
    public static final String LOGOUT_MESSAGE = "logout.message";
    public static final String DB_IDBROKER_CLEVERQUANTITY = "database.idbroker.cleverquantity";
    public static final String DB_IDBROKER_PREFETCH = "database.idbroker.prefetch";

    /**
     * Utility method for accessing the service
     * implementation
     *
     * @return a ResourcesService implementation instance
     */
    protected static ResourceService getService()
    {
        return (ResourceService)TurbineServices
                    .getInstance()
                    .getService(ResourceService.SERVICE_NAME);
    }

    /**
     * Set a property in with a key=value pair.
     *
     * @param String key
     * @param String value
     */
    public static void setProperty(String key, String value)
    {
        getService().setProperty(key,value);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a boolean value.
     *
     * @param name The resource name.
     * @return The value of the named resource as a boolean.
     */
    public static boolean getBoolean(String name)
    {
        return getService().getBoolean (name);
    }

    /**
     * The purppose of this method is to get the configuration
     * resource with the given name as a boolean value, or a default
     * value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the named resource as a boolean.
     */
    public static boolean getBoolean(String name,
                                     boolean def)
    {
        return getService().getBoolean(name, def);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a double.
     *
     * @param name The resoource name.
     * @return The value of the named resource as double.
     */
    public static double getDouble(String name)
    {
        return getService().getDouble(name);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a double, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the named resource as a double.
     */
    public static double getDouble(String name,
                                   double def)
    {
        return getService().getDouble(name, def);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a float.
     *
     * @param name The resource name.
     * @return The value of the resource as a float.
     */
    public static float getFloat(String name)
    {
        return getService().getFloat(name);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a float, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a float.
     */
    public static float getFloat(String name,
                                 float def)
    {
        return getService().getFloat(name, def);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as an integer.
     *
     * @param name The resource name.
     * @return The value of the resource as an integer.
     */
    public static int getInt(String name)
    {
        return getService().getInt(name);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as an integer, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as an integer.
     */
    public static int getInt(String name,
                             int def)
    {
        return getService().getInt(name, def);
    }

    /**
     * Get the list of the keys contained in the configuration
     * repository.
     *
     * @return An Enumeration with all the keys.
     */
    public static Iterator getKeys()
    {
        return getService().getKeys();
    }

    /**
     * Get the list of the keys contained in the configuration
     * repository that match the specified prefix.
     *
     * @param prefix A String prefix to test against.
     * @return An Enumeration of keys that match the prefix.
     */
    public static Iterator getKeys(String prefix)
    {
        return getService().getKeys(prefix);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a long.
     *
     * @param name The resource name.
     * @return The value of the resource as a long.
     */
    public static long getLong(String name)
    {
        return getService().getLong(name);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a long, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a long.
     */
    public static long getLong(String name,
                               long def)
    {
        return getService().getLong(name, def);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a string.
     *
     * @param name The resource name.
     * @return The value of the resource as a string.
     */
    public static String getString(String name)
    {
        return getService().getString(name);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a string, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a string.
     */
    public static String getString(String name,
                                   String def)
    {
        return getService().getString(name, def);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a string array.
     *
     * @param name The resource name.
     * @return The value of the resource as a string array.
     */
    public static String[] getStringArray(String name)
    {
        return getService().getStringArray(name);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a vector.
     *
     * @param name The resource name.
     * @return The value of the resource as a vector.
     */
    public static Vector getVector(String name)
    {
        return getService().getVector(name);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a vector, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a vector.
     */
    public static Vector getVector(String name,
                                   Vector def)
    {
        return getService().getVector(name,def);
    }

    /**
     * The purpose of this method is to extract a subset of configuraton
     * resources sharing a common name prefix.
     *
     * @param prefix the common name prefix
     * @return A ResourceService providing the subset of configuration.
     */
    public static ResourceService getResources(String prefix)
    {
        return getService().getResources(prefix);
    }

    /**
     * Get the configuraton.
     *
     * @return configuration.
     */
    public static Configuration getConfiguration()
    {
        return getService().getConfiguration();
    }

    /**
     * The purpose of this method is to extract a subset configuraton
     * sharing a common name prefix.
     *
     * @param prefix the common name prefix
     * @return A Configuration providing the subset of configuration.
     */
    public static Configuration getConfiguration(String prefix)
    {
        return getService().getConfiguration(prefix);
    }
}
