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


import java.util.Properties;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationConverter;

/**
 * This class is a generic implementation of <code>Service</code>.
 *
 * @author <a href="mailto:burton@apache.org">Kevin Burton</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class BaseService
        extends BaseInitable
        implements Service
{
    /** A reference to the ServiceBroker that instantiated this object. */
    protected ServiceBroker serviceBroker;

    /** The configuration for this service */
    protected Configuration configuration;

    /** The name of this Service. */
    protected String name;

    /**
     * Saves a reference to the ServiceBroker that instantiated this
     * object, so that it can ask for its properties and access other
     * Services.
     *
     * @param broker The ServiceBroker that instantiated this object.
     */
    public void setServiceBroker(ServiceBroker broker)
    {
        this.serviceBroker = broker;
    }

    /**
     * ServiceBroker uses this method to pass a Service its name.
     *
     * @param name The name of this Service.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of this service.
     *
     * @return The name of this Service.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns a ServiceBroker reference.
     *
     * @return The ServiceBroker that instantiated this object.
     */
    public ServiceBroker getServiceBroker()
    {
        return serviceBroker;
    }

    /**
     * Returns the properties of this Service.
     *
     * @return The Properties of this Service.
     */
    public Properties getProperties()
    {
        return ConfigurationConverter.getProperties(getConfiguration());
    }

    /**
     * Returns the configuration of this Service.
     *
     * @return The Configuration of this Service.
     */
    public Configuration getConfiguration()
    {
        if (name == null)
        {
            return null;
        }

        if (configuration == null)
        {
            configuration = getServiceBroker().getConfiguration(name);
        }
        return configuration;
    }
}
