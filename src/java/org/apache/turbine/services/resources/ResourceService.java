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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import javax.servlet.ServletConfig;
import org.apache.turbine.services.Service;
import org.apache.stratum.configuration.Configuration;


/**
 * <p>This service define a resource interface for accessing the configuration
 * information of the application.</p>
 * <p>Since implementations of this service are used by Turbine itself and
 * the <code>TurbineServices</code> depends on their proper operation, they
 * must respect some specific implementation rules:</p>
 * <ul>
 *    <li>They can't use other services or classes depending on any services
 *        in their inititialization code. This would create circular services
 *        dependency
 *    </li>
 *    <li>They must provide an early initialization init method. The
 *      <code>ServletConfig</code> init parameters are the only way
 *      to retrieve parameters for this service. It's impossible to use
 *      TurbineServices utility methods for this purpose
 *    </li>
 * </ul>
 *
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @author <a href="mailto:luta.raphael@networks.vivendi.net">Raphaël Luta</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 */
public interface ResourceService extends Service
{

    public String SERVICE_NAME = "ResourceService";

    /**
     * Set a property in with a key=value pair.
     *
     * @param String key
     * @param String value
     */
    public void setProperty(String key, String value);

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a boolean value.
     *
     * @param name The resource name.
     * @return The value of the named resource as a boolean.
     */
    public boolean getBoolean(String name);

    /**
     * The purppose of this method is to get the configuration
     * resource with the given name as a boolean value, or a default
     * value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the named resource as a boolean.
     */
    public boolean getBoolean(String name,boolean def);

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a double.
     *
     * @param name The resoource name.
     * @return The value of the named resource as double.
     */
    public double getDouble(String name);

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a double, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the named resource as a double.
     */
    public double getDouble(String name,double def);

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a float.
     *
     * @param name The resource name.
     * @return The value of the resource as a float.
     */
    public float getFloat(String name);

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a float, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a float.
     */
    public float getFloat(String name,float def);

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as an integer.
     *
     * @param name The resource name.
     * @return The value of the resource as an integer.
     */
    public int getInt(String name);

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as an integer, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as an integer.
     */
    public int getInt(String name,int def);

    /**
     * Get the list of the keys contained in the configuration
     * repository.
     *
     * @return An Enumeration with all the keys.
     */
    public Iterator getKeys();

    /**
     * Get the list of the keys contained in the configuration
     * repository that match the specified prefix.
     *
     * @param prefix A String prefix to test against.
     * @return An Enumeration of keys that match the prefix.
     */
    public Iterator getKeys(String prefix);

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a long.
     *
     * @param name The resource name.
     * @return The value of the resource as a long.
     */
    public long getLong(String name);

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a long, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a long.
     */
    public long getLong(String name,long def);

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a string.
     *
     * @param name The resource name.
     * @return The value of the resource as a string.
     */
    public String getString(String name);

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a string, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a string.
     */
    public String getString(String name,String def);

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a string array.
     *
     * @param name The resource name.
     * @return The value of the resource as a string array.
     */
    public String[] getStringArray(String name);

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a vector.
     *
     * @param name The resource name.
     * @return The value of the resource as a vector.
     */
    public Vector getVector(String name);

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a vector, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a vector.
     */
    public Vector getVector(String name,Vector def);

    /**
     * The purpose of this method is to extract a subset of configuraton
     * resources sharing a common name prefix.
     *
     * @param prefix the common name prefix
     * @return A ResourceService providing the subset of configuration.
     */
    public ResourceService getResources(String prefix);

    /**
     * The purpose of this method is to extract a subset configuraton
     * sharing a common name prefix.
     *
     * @param prefix the common name prefix
     * @return A Configuration providing the subset of configuration.
     */
    public Configuration getConfiguration(String prefix);
}
