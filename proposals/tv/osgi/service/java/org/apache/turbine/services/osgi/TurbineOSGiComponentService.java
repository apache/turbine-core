package org.apache.turbine.services.osgi;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.framework.util.StringMap;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.InstantiationException;
import org.apache.turbine.services.TurbineBaseService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceException;

/**
 * An implementation of a Turbine service initializing the Apache Felix container
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class TurbineOSGiComponentService
        extends TurbineBaseService
        implements OSGiComponentService
{
    /** the logger to be used */
    private static Log log = LogFactory.getLog(OSGI_LOG_CATEGORY);

    /** OSGi container */
    private Felix m_felix = null;

    /** OSGi system bundle context accessor */
    private OSGiHostActivator m_activator = null;

    // -------------------------------------------------------------
    // Service initialization
    // -------------------------------------------------------------

    public TurbineOSGiComponentService()
    {
        // nothing to do
    }

    /**
     * Load all configured components and initialize them. This is a zero parameter variant which
     * queries the Turbine Servlet for its config.
     *
     * @throws InitializationException Something went wrong in the init stage
     */
    public void init() throws InitializationException
    {
        try
        {
            log.info("Initializing TurbineOSGiComponentService ...");
            initialize();
            setInit(true);
        }
        catch (Exception e)
        {
            log.error("Exception caught initializing service: ", e);
            throw new InitializationException("Initializing TurbineOSGiComponentService failed", e);
        }
    }

    /**
     * Shuts the Component Service down, calls dispose on the components that implement this
     * interface
     *
     */
    public void shutdown()
    {
        log.info("Disposing TurbineOSGiComponentService ...");
        dispose();
        setInit(false);
    }

    /**
     * Initializes the container
     *
     * @throws Exception generic exception
     */
    public void initialize() throws Exception
    {
        // get the configuration from the baseclass
        Configuration conf = this.getConfiguration();

        // Create a case-insensitive configuration property map.
        StringMap configMap = new StringMap(false);

        // Add core OSGi packages to be exported from the class path
        // via the system bundle.
        configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES,
            "org.osgi.framework; version=1.3.0," +
            "org.osgi.service.packageadmin; version=1.2.0," +
            "org.osgi.service.startlevel; version=1.0.0," +
            "org.osgi.service.url; version=1.0.0");

        // Provide a logger
        configMap.put(FelixConstants.LOG_LOGGER_PROP, new OSGiLogger(log));
        configMap.put(FelixConstants.LOG_LEVEL_PROP, String.valueOf(Logger.LOG_DEBUG));
        
        // determine the home directory
        String cacheDir = conf.getString(CONFIG_KEY_CACHE_DIRECTORY, Turbine.getRealPath("/"));
        File dir = new File(cacheDir);
        
        if (!dir.isAbsolute())
        {
            cacheDir = Turbine.getRealPath("/") + File.separator + cacheDir;
        }
        
        log.info("Using cache directory " + cacheDir);

        // Explicitly specify the directory to use for caching bundles.
        configMap.put("felix.cache.rootdir", cacheDir);

        // Create host activator;
        m_activator = new OSGiHostActivator();
        List<BundleActivator> list = new ArrayList<BundleActivator>();
        list.add(m_activator);
        configMap.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, list);
        
        // Now create an instance of the framework with
        // our configuration properties and activator.
        m_felix = new Felix(configMap);

        // Start Felix instance.
        m_felix.start();
        
        // Install default bundles from the configuration
        for (Iterator keys = conf.getKeys(); keys.hasNext();)
        {
            String key = (String) keys.next();
            String[] keyParts = StringUtils.split(key, ".");
            
            if (keyParts.length == 2 && CONFIG_KEY_INSTALL_BUNDLE.equals(keyParts[0]))
            {
                Bundle bundle = installBundle(conf.getString(key));
                log.info("Installed bundle " + bundle.getSymbolicName());
                
                if (bundle.getState() != Bundle.ACTIVE)
                {
                    bundle.start();
                }
            }
        }
    }

    /**
     * Disposes of the container and releases resources
     */
    public void dispose()
    {
        // Shut down the felix framework when stopping the
        // Turbine service.
        if (this.m_felix != null)
        {
            try
            {
                this.m_felix.stop();
                this.m_felix.waitForStop(30000);
            }
            catch (Exception e)
            {
                // Swallow
            }
            
            this.m_felix = null;
        }
    }

    /**
     * Returns an instance of the named component
     *
     * @param roleName Name of the role the component fills.
     * @return an instance of the named component
     * @throws Exception generic exception
     */
    public Object lookup(String roleName) throws ServiceException
    {
        return this.m_activator.getService(roleName);
    }

    /**
     * Releases the component.
     *
     * @param component the component to release
     */
    public void release(Object component)
    {
        // do nothing
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#hasService(java.lang.String)
     */
    public boolean hasService(String roleName)
    {
        return this.m_activator.getService(roleName) != null;
    }

    // -------------------------------------------------------------
    // TurbineServiceProvider
    // -------------------------------------------------------------

    /**
     * @see org.apache.turbine.services.TurbineServiceProvider#exists(java.lang.String)
     */
    public boolean exists(String roleName)
    {
        return this.hasService(roleName);
    }

    /**
     * @see org.apache.turbine.services.TurbineServiceProvider#get(java.lang.String)
     */
    public Object get(String roleName) throws InstantiationException
    {
        try
        {
            return this.lookup(roleName);
        }
        catch (ServiceException e)
        {
            String msg = "Unable to get the following service : " + roleName;
            log.error(msg);
            throw new InstantiationException(msg);
        }
        catch (Throwable t)
        {
            String msg = "Unable to get the following service : " + roleName;
            log.error(msg,t);
            throw new InstantiationException(msg,t);
        }
    }
    
    /**
     * Install a Bundle
     * 
     * @param bundleUrl the url pointing to the bundle
     * 
     * @return the Bundle object installed and activated
     * @throws BundleException if the installation fails
     */
    public Bundle installBundle(String bundleUrl) throws BundleException
    {
        return this.m_activator.installBundle(bundleUrl);
    }

    /**
     * Get all bundles
     * 
     * @return an array of bundles
     * @throws BundleException if the operation fails
     */
    public Bundle[] getBundles() throws BundleException
    {
        return this.m_activator.getBundles();
    }
}
