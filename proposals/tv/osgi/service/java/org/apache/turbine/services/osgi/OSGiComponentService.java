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

import org.apache.turbine.services.Service;
import org.apache.turbine.services.TurbineServiceProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * This service allows access to OSGi components.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: OSGiComponentService.java 615328 2008-01-25 20:25:05Z tv $
 */
public interface OSGiComponentService
        extends Service, TurbineServiceProvider
{
    /** The publically visible name of the service */
    String SERVICE_NAME = "OSGiComponentService";

    /** Where we write the OSGi Logger messages */
    String OSGI_LOG_CATEGORY = "osgi";

    /** Key used to define the root directory for the OSGi bundle cache */
    String CONFIG_KEY_CACHE_DIRECTORY = "cacheDirectory";

    /** Key used to install bundles by default */
    String CONFIG_KEY_INSTALL_BUNDLE = "installBundle";
    
    /**
     * Install a Bundle
     * 
     * @param bundleUrl the url pointing to the bundle
     * 
     * @return the Bundle object installed and activated
     * @throws BundleException if the installation fails
     */
    Bundle installBundle(String bundleUrl) throws BundleException;
    
    /**
     * Get all bundles
     * 
     * @return an array of bundles
     * @throws BundleException if the operation fails
     */
    Bundle[] getBundles() throws BundleException;
}
