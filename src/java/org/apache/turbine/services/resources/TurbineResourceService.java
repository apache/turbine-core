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

import java.io.IOException;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import javax.servlet.ServletConfig;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.BaseInitable;
import org.apache.turbine.services.BaseServiceBroker;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.ServiceBroker;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.ServletUtils;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.TurbineException;
import org.apache.velocity.runtime.configuration.Configuration;

/**
 * <p>This implementation of the <code>resourcesService</code> relies
 * on an external properties file for storing the configuration keys
 * and values.</p>
 *
 * <P>In order to be compatible with legacy applications, this implementation
 * kept a static method for initializing the service, so it's still possible
 * to write the following code:
 * <p><code>
 * TurbineResourceService.setPropertiesName("d:/conf/Turbine.properties");
 * Vector myVar = TurbineResources.getVector("myvar");
 * </code></p>
 *
 * <p>The new way to do things is to look at the org.apache.turbine.util.TurbineConfig
 *    class.</p>
 *
 * @author <a href="mailto:jm@mediaphil.de">Jonas Maurus</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:luta.raphael@networks.vivendi.net">Raphaël Luta</a>
 * @author <a href="mailto:jvanzyl@periapt.com@">Jason van Zyl</a>
 * @version $Id$
 */
public class TurbineResourceService 
    extends TurbineBaseService
    implements ResourceService
{
    /** The container for the generic resources. */
    //private GenericResources generic = null;
    private Configuration configuration = null;

    private static final String START_TOKEN="${";
    private static final String END_TOKEN="}";

    /**
     * Performs early initialization.  Overrides init() method in
     * BaseService to detect objects used in Turbine's Service
     * initialization and pass them to apropriate init() methods.
     *
     * @param data An Object to use for initialization activities.
     * @exception InitializationException, if initialization of this
     * class was not successful.
     */
    public void init( Object data )
        throws InitializationException
    {
        if (data instanceof ServletConfig)
        {
            init((ServletConfig)data);
        }
        else if (data instanceof Properties)
        {
            init((Properties)data);
        }
        else if (data instanceof Configuration)
        {
            init((Configuration)data);
        }
    }

    /**
     * This method is called when the Service is initialized
     *
     * @param config a ServletConfig object
     */
    public void init(ServletConfig config) 
        throws InitializationException
    {
        String props = config.getInitParameter(TurbineServices.PROPERTIES_PATH_KEY);
        
        if(props == null) 
        {
            props = TurbineServices.PROPERTIES_PATH_DEFAULT;
        }

        // This will attempt to find the location of the properties
        // file from the relative path to the WAR archive (ie:
        // docroot). Since JServ returns null for getRealPath()
        // because it was never implemented correctly, then we know we
        // will not have an issue with using it this way. I don't know
        // if this will break other servlet engines, but it probably
        // shouldn't since WAR files are the future anyways.
        //props = ServletUtils.expandRelative(config, props);
        props = Turbine.getRealPath(props);
        
        try
        {
            init(new Configuration(props));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new InitializationException("Can't load file " + props);
        }
    }

    /**
     * Init the service with the given properties filename
     *
     * @deprecated
     * @param propertiesFileName The file name.
     * @exception IOException, if there was an I/O problem.
     */
    public static void setPropertiesFileName(String propertiesFileName)
        throws TurbineException
    {
        Configuration mappings = new Configuration();
        
        mappings.setProperty(ResourceService.SERVICE_NAME,
            TurbineResourceService.class.getName());
        
        TurbineServices services = (TurbineServices) TurbineServices.getInstance();
        services.initMapping(mappings);
        services.initServices(new TurbineConfig(".", propertiesFileName), true);
    }

    /**
     * Init the service with the given properties object.  Called
     * from Cocoon to initialize Turbine.
     *
     * @param properties The java.util.Properties object sent from another process such as
     *                   Cocoon.  This Properties object contains all of the necessary properties
     *                   found in the TurbineResources.properties file.
     * @exception TurbineException, if there was an I/O problem.
     */
    public static void setProperties(Properties properties)
        throws TurbineException
    {
        Configuration mappings = new Configuration();
        
        mappings.setProperty(ResourceService.SERVICE_NAME,
            TurbineResourceService.class.getName());
        
        TurbineServices services = (TurbineServices) TurbineServices.getInstance();
        services.initMapping(mappings);
        services.initServices(properties, true);
    }

    /**
     * Set a property in with a key=value pair.
     *
     * @param String key
     * @param String value
     */
    public void setProperty(String key, String value)
    {
        configuration.setProperty(key,value);
    }

    protected String interpolate(String base)
    {
        if (base == null)
        {
            return null;
        }            
        
        int begin = -1;
        int end = -1;
        int prec = 0-END_TOKEN.length();
        String variable = null;
        StringBuffer result = new StringBuffer();
        
        // FIXME: we should probably allow the escaping of the start token
        while ( ((begin=base.indexOf(START_TOKEN,prec+END_TOKEN.length()))>-1)
                && ((end=base.indexOf(END_TOKEN,begin))>-1) ) 
        {
            result.append(base.substring(prec+END_TOKEN.length(),begin));
            variable = base.substring(begin+START_TOKEN.length(),end);
            if (configuration.get(variable)!=null) 
            {
                result.append(configuration.get(variable));
            }
            prec=end;
        }
        result.append(base.substring(prec+END_TOKEN.length(),base.length()));
        
        return result.toString();
    }

    /**
     * Wrapper around the configuration resources.
     *
     * @return A Configuration.
     */
    public Configuration getConfiguration()
    {
        return configuration;
    }

    /**
     * Initializer method that sets up the configuration resources.
     *
     * @param confs A Configurations object.
     */
    private void init(Configuration configuration)
    {
        this.configuration = configuration;
        setInit(true);
    }

    /**
     * The purpose of this method is to init the configuration
     * resource with a Properties object sent from a different system.
     * For example, a Properties sent over from Cocoon.  The reason
     * for this code is to provide a bridge between an
     * org.apache.turbine.util.Configurations class and an
     * org.apache.cocoon.framework.Configurations class.
     *
     * @param props A Properties object.
     */
    private void init(Properties props)
    {
        Configuration configuration = Configuration.convertProperties(props);
        init(configuration);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a boolean value.
     *
     * @param name The resource name.
     * @return The value of the named resource as a boolean.
     */
    public boolean getBoolean(String name)
    {
        return getConfiguration().getBoolean(name);
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
    public boolean getBoolean(String name,
                                     boolean def)
    {
        return getConfiguration().getBoolean(name, def);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a double.
     *
     * @param name The resoource name.
     * @return The value of the named resource as double.
     */
    public double getDouble(String name)
    {
        return getConfiguration().getDouble(name);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a double, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the named resource as a double.
     */
    public double getDouble(String name,
                                   double def)
    {
        return getConfiguration().getDouble(name, def);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a float.
     *
     * @param name The resource name.
     * @return The value of the resource as a float.
     */
    public float getFloat(String name)
    {
        return getConfiguration().getFloat(name);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a float, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a float.
     */
    public float getFloat(String name,
                                 float def)
    {
        return getConfiguration().getFloat(name, def);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as an Integer.
     *
     * @param name The resource name.
     * @return The value of the resource as an Integer.
     */
    
    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as an integer.
     *
     * @param name The resource name.
     * @return The value of the resource as an integer.
     */
    public int getInt(String name)
    {
        return getConfiguration().getInt(name);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as an integer, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as an integer.
     */
    public int getInt(String name,
                             int def)
    {
        return getConfiguration().getInt(name, def);
    }

    /**
     * Get the list of the keys contained in the configuration
     * repository.
     *
     * @return An Enumeration with all the keys.
     */
    //public Enumeration getKeys()
    public Iterator getKeys()
    {
        return getConfiguration().getKeys();
    }

    /**
     * Get the list of the keys contained in the configuration
     * repository that match the specified prefix.
     *
     * @param prefix A String prefix to test against.
     * @return An Enumeration of keys that match the prefix.
     */
    public Iterator getKeys(String prefix)
    {
        return getConfiguration().getKeys(prefix);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a long.
     *
     * @param name The resource name.
     * @return The value of the resource as a long.
     */
    public long getLong(String name)
    {
        return getConfiguration().getLong(name);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a long, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a long.
     */
    public long getLong(String name,
                               long def)
    {
        return getConfiguration().getLong(name, def);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a string.
     *
     * @param name The resource name.
     * @return The value of the resource as a string.
     */
    public String getString(String name)
    {
        return interpolate(getConfiguration().getString(name));
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a string, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a string.
     */
    public String getString(String name,
                            String def)
    {
        return interpolate(getConfiguration().getString(name, def));
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a string array.
     *
     * @param name The resource name.
     * @return The value of the resource as a string array.
     */
    public String[] getStringArray(String name)
    {
        String[] std = getConfiguration().getStringArray(name);

        if (std != null) 
        {
            for(int i=0; i<std.length; i++) 
            {
                std[i]=interpolate(std[i]);
            }
        }
        
        return std;
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a vector.
     *
     * @param name The resource name.
     * @return The value of the resource as a vector.
     */
    public Vector getVector(String name)
    {
        Vector std = getConfiguration().getVector(name);
        
        if (std != null) 
        {
            Vector newstd = new Vector();
            Enumeration en = std.elements();
            while (en.hasMoreElements()) 
            {
                newstd.addElement(interpolate((String)en.nextElement()));
            }
            std = newstd;
        }
        
        return std;
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a vector, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a vector.
     */
    public Vector getVector(String name,
                            Vector def)
    {
        Vector std = getVector(name); 
        if (std == null) 
        {
            if (def != null) 
            {
                std = new Vector();
                Enumeration en = def.elements();
                while (en.hasMoreElements()) 
                {
                    std.addElement(interpolate((String)en.nextElement()));
                }
            }
        }

        return std;
    }

    /**
     * The purpose of this method is to extract a subset of configuraton
     * resources sharing a common name prefix. The prefix is stripped
     * from the names of the resulting resources.
     *
     * @param prefix the common name prefix
     * @return A ResourceService providing the subset of configuration.
     */
    public ResourceService getResources(String prefix)
    {
        Configuration config = getConfiguration().subset(prefix);
        
        if (config == null)
        {
            return null;
        }
        
        TurbineResourceService res = new TurbineResourceService();
        res.init(config);
        return (ResourceService)res;
    }

    /**
     * The purpose of this method is to extract a subset of configuraton
     * resources sharing a common name prefix. The prefix is stripped
     * from the names of the resulting resources.
     *
     * @param prefix the common name prefix
     * @return A Configuration providing the subset of configuration.
     */
    public Configuration getConfiguration(String prefix)
    {
        Configuration config = getConfiguration().subset(prefix);
        
        if (config == null)
        {
            return null;
        }
        
        return config;
    }
}
