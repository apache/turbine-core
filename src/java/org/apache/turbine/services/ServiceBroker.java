package org.apache.turbine.services;


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


import org.apache.commons.configuration2.Configuration;

/**
 * Classes that implement this interface can act as a broker for
 * <code>Service</code> classes.
 *
 * Functionality that <code>ServiceBroker</code> provides in addition
 * to <code>InitableBroker</code> functionality includes:
 *
 * <ul>
 *
 * <li>Maintaining service name to class name mapping, allowing
 * plugable service implementations.</li>
 *
 * <li>Providing <code>Services</code> with <code>Properties</code>
 * based on a system wide configuration mechanism.</li>
 *
 * </ul>
 *
 * @author <a href="mailto:burton@apache.org">Kevin Burton</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public interface ServiceBroker
{
    /**
     * Determines whether a service is registered in the configured
     * <code>TurbineResources.properties</code>.
     *
     * @param serviceName The name of the service whose existance to check.
     * @return Registration predicate for the desired services.
     */
    boolean isRegistered(String serviceName);

    /**
     * Performs early initialization of the specified service.
     *
     * @param name The name of the service.
     * @throws InitializationException if the service is unknown
     * or can't be initialized.
     */
    void initService(String name) throws InitializationException;

    /**
     * Shutdowns a Service.
     *
     * This method is used to release resources allocated by a
     * Service, and return it to initial (uninitailized) state.
     *
     * @param name The name of the Service to be uninitialized.
     */
    void shutdownService(String name);

    /**
     * Shutdowns all Services.
     *
     * This method is used to release resources allocated by
     * Services, and return them to initial (uninitialized) state.
     */
    void shutdownServices();

    /**
     * Returns an instance of requested Service.
     *
     * @param name The name of the Service requested.
     * @return An instance of requested Service.
     * @throws InstantiationException if the service is unknown or
     * can't be initialized.
     */
    Object getService(String name) throws InstantiationException;

    /**
     * Returns the configuration of a specific service. Services
     * use this method to retrieve their configuration.
     *
     * @param name The name of the service.
     * @return Configuration of the requested service.
     */
    Configuration getConfiguration(String name);
}
