package org.apache.turbine.services.resources;

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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.security.SecurityService;

/**
 * This is a static class for defining the default Turbine configuration
 * keys used by core Turbine components.
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @author <a href="mailto:luta.raphael@networks.vivendi.net">Raphaël Luta</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 * @deprecated as of Turbine 2.2 use Turbine.getConfiguration()
 */
public abstract class TurbineResources
{
    /** @deprecated Use the corresponding constant from TurbineConstants */
    public static final String MAIL_SERVER_KEY = TurbineConstants.MAIL_SERVER_KEY;

    /** @deprecated Use the corresponding constant from TurbineConstants */
    public static final String MODULE_CACHE_KEY = TurbineConstants.MODULE_CACHE_KEY;

    /** @deprecated Use the corresponding constant from TurbineConstants */
    public static final String MODULE_PACKAGES_KEY = TurbineConstants.MODULE_PACKAGES;

    /** @deprecated Use the corresponding constant from TurbineConstants */
    public static final String ACTION_CACHE_SIZE_KEY = TurbineConstants.ACTION_CACHE_SIZE_KEY;

    /** @deprecated Use the corresponding constant from TurbineConstants */
    public static final String LAYOUT_CACHE_SIZE_KEY = TurbineConstants.LAYOUT_CACHE_SIZE_KEY;

    /** @deprecated Use the corresponding constant from TurbineConstants */
    public static final String NAVIGATION_CACHE_SIZE_KEY = TurbineConstants.NAVIGATION_CACHE_SIZE_KEY;

    /** @deprecated Use the corresponding constant from TurbineConstants */
    public static final String PAGE_CACHE_SIZE_KEY = TurbineConstants.PAGE_CACHE_SIZE_KEY;

    /** @deprecated Use the corresponding constant from TurbineConstants */
    public static final String SCREEN_CACHE_SIZE_KEY = TurbineConstants.SCREEN_CACHE_SIZE_KEY;

    /** @deprecated Use the corresponding constant from SecurityService */
    public static final String USER_CLASS_KEY = SecurityService.USER_CLASS_KEY;

    /** @deprecated No longer used */
    public static final String MAX_FILE_SIZE_KEY = "max.file.size.bytes";

    /** @deprecated No longer used */
    public static final String FILE_SERVER = "file.server";

    /** @deprecated Use the corresponding constant from TurbineConstants */
    public static final String LOGIN_MESSAGE = TurbineConstants.LOGIN_MESSAGE;

    /** @deprecated Use the corresponding constant from TurbineConstants */
    public static final String LOGIN_ERROR = TurbineConstants.LOGIN_ERROR;

    /** @deprecated Use the corresponding constant from TurbineConstants */
    public static final String LOGIN_MESSAGE_NOSCREEN = TurbineConstants.LOGIN_MESSAGE_NOSCREEN;

    /** @deprecated Use the corresponding constant from TurbineConstants */
    public static final String LOGOUT_MESSAGE = TurbineConstants.LOGOUT_MESSAGE;

    /**
     * Set a property in with a key=value pair.
     *
     * @param key
     * @param value
     */
    public static void setProperty(String key, String value)
    {
        Turbine.getConfiguration().setProperty(key, value);
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
        return Turbine.getConfiguration().getBoolean(name);
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
        return Turbine.getConfiguration().getBoolean(name, def);
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
        return Turbine.getConfiguration().getDouble(name);
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
        return Turbine.getConfiguration().getDouble(name, def);
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
        return Turbine.getConfiguration().getFloat(name);
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
        return Turbine.getConfiguration().getFloat(name, def);
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
        return Turbine.getConfiguration().getInt(name);
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
        return Turbine.getConfiguration().getInt(name, def);
    }

    /**
     * Get the list of the keys contained in the configuration
     * repository.
     *
     * @return An Enumeration with all the keys.
     */
    public static Iterator getKeys()
    {
        return Turbine.getConfiguration().getKeys();
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
        return Turbine.getConfiguration().getKeys(prefix);
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
        return Turbine.getConfiguration().getLong(name);
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
        return Turbine.getConfiguration().getLong(name, def);
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
        return Turbine.getConfiguration().getString(name);
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
        return Turbine.getConfiguration().getString(name, def);
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
        return Turbine.getConfiguration().getStringArray(name);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a vector.
     *
     * @param name The resource name.
     * @return The value of the resource as a vector.
     */
    public static List getList(String name)
    {
        return Turbine.getConfiguration().getList(name);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a vector, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a vector.
     */
    public static List getList(String name,
                                   List def)
    {
        return Turbine.getConfiguration().getList(name, def);
    }

    /**
     * Get the configuration.
     *
     * @return configuration.
     */
    public static Configuration getConfiguration()
    {
        return Turbine.getConfiguration();
    }

    /**
     * The purpose of this method is to extract a subset configuration
     * sharing a common name prefix.
     *
     * @param prefix the common name prefix
     * @return A Configuration providing the subset of configuration.
     */
    public static Configuration getConfiguration(String prefix)
    {
        return Turbine.getConfiguration().subset(prefix);
    }
}
