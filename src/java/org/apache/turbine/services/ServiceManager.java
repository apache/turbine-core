package org.apache.turbine.services;


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


import org.apache.commons.configuration.Configuration;

/**
 * Classes that implement this interface can act as a manager for
 * <code>Service</code> classes.
 *
 * Functionality that <code>ServiceManager</code> provides in addition
 * to <code>ServiceBroker</code> functionality includes configuration
 * of the manager.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public interface ServiceManager extends ServiceBroker
{
    /**
     * Initialize this service manager.
     */
    void init() throws InitializationException;

    /**
     * Get the configuration for this service manager.
     *
     * @return Manager configuration.
     */
    Configuration getConfiguration();

    /**
     * Set the configuration object for the services broker.
     * This is the configuration that contains information
     * about all services in the care of this service
     * manager.
     *
     * @param configuration Manager configuration.
     */
    void setConfiguration(Configuration configuration);

    /**
     * Set the application root.
     *
     * @param applicationRoot application root
     */
    void setApplicationRoot(String applicationRoot);
}
