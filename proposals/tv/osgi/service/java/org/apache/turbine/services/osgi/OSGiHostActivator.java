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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This provides access to the system bundle context for OSGi components.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: OSGiComponentService.java 615328 2008-01-25 20:25:05Z tv $
 */
public class OSGiHostActivator
        implements BundleActivator
{
    /** a reference to the system bundle context */
    private BundleContext context = null;
    
    /** 
     * a map which stores the service names and their associated ServiceTracker 
     * to allow for proper ungetService()
     */
    private Map<String, ServiceTracker> serviceTrackerHash = null;

    /**
     * Start the bundle (OSGi lifecycle)
     */
    public void start(BundleContext context)
    {
        this.context = context;
        this.serviceTrackerHash = Collections.synchronizedMap(new HashMap<String, ServiceTracker>());
    }

    /**
     * Stop the bundle (OSGi lifecycle)
     */
    public void stop(BundleContext context)
    {
        this.context = null;
        
        for (ServiceTracker tracker : this.serviceTrackerHash.values())
        {
            tracker.close();
        }
        
        this.serviceTrackerHash.clear();
    }
    
    /**
     * Install a Bundle
     * 
     * @param bundleUrl the url pointing to the bundle
     * 
     * @return the Bundle object installed and activated
     * @throws BundleException if the installation fails
     */
    public Bundle installBundle(String bundle) throws BundleException
    {
        return this.context.installBundle(bundle);
    }

    /**
     * Get all bundles
     * 
     * @return an array of bundles
     * @throws BundleException if the operation fails
     */
    public Bundle[] getBundles() throws BundleException
    {
        return this.context.getBundles();
    }
    
    /**
     * Get a (possibly cached) instance of a ServiceTracker for a given service class
     * 
     * @param clazzName the class name of the registered service
     * 
     * @return a ServiceTracker instance
     */
    private ServiceTracker getServiceTracker(String clazzName)
    {
        ServiceTracker tracker = null;
        
        if (this.serviceTrackerHash.containsKey(clazzName))
        {
            tracker = this.serviceTrackerHash.get(clazzName);
        }
        else
        {
            tracker = new ServiceTracker(this.context, clazzName, null);
            tracker.open();
            
            this.serviceTrackerHash.put(clazzName, tracker);
        }
        
        return tracker;
    }

    /**
     * Get a service from the OSGi container
     * 
     * @param roleName the (interface-)name of the service.
     * 
     * @return the service object
     */
    public Object getService(String roleName)
    {
        ServiceTracker tracker = getServiceTracker(roleName);
        return tracker.getService();
    }
}
