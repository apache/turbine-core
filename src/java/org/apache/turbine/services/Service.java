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

import java.util.Properties;

import org.apache.commons.configuration.Configuration;

/**
 * <code>Services</code> are <code>Initables</code> that have a name,
 * and a set of properties.
 *
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:burton@apache.org">Kevin Burton</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @version $Id$
 */
public interface Service
        extends Initable
{
    /** The name of this service. */
    String SERVICE_NAME = "Service";

    /**
     * Provides a Service with a reference to the ServiceBroker that
     * instantiated this object, so that it can ask for its properties
     * and access other Services.
     *
     * @param broker The ServiceBroker that instantiated this object.
     */
    void setServiceBroker(ServiceBroker broker);

    /**
     * ServiceBroker uses this method to pass a Service its name.
     * Service uses its name to ask the broker for an apropriate set
     * of Properties.
     *
     * @param name The name of this Service.
     */
    void setName(String name);

    /**
     * Returns the name of this Service.
     *
     * @return The name of this Service.
     */
    String getName();

    /**
     * Returns the Properties of this Service.  Every Service has at
     * least one property, which is "classname", containing the name
     * of the class implementing this service.  Note that the service
     * may chose to alter its properties, therefore they may be
     * different from those returned by ServiceBroker.
     *
     * @return The properties of this Service.
     */
    Properties getProperties();

    /**
     * Returns the Configuration of this Service.  Every Service has at
     * least one property, which is "classname", containing the name
     * of the class implementing this service.  Note that the service
     * may chose to alter its configuration, therefore they may be
     * different from those returned by ServiceBroker.
     *
     * @return The Configuration of this Service.
     */
    Configuration getConfiguration();
}
