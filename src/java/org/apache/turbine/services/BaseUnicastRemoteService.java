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

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import javax.servlet.ServletConfig;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;

/**
 * A base implementation of an {@link java.rmi.server.UnicastRemoteObject}
 * as a Turbine {@link org.apache.turbine.services.Service}.
 *
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @version $Id$
 */
public class BaseUnicastRemoteService extends UnicastRemoteObject
        implements Service
{
    /** Serial Version UID */
    private static final long serialVersionUID = -7775459623190960297L;

    protected Configuration configuration;
    private boolean isInitialized;
    private InitableBroker initableBroker;
    private String name;
    private ServiceBroker serviceBroker;

    public BaseUnicastRemoteService()
            throws RemoteException
    {
        isInitialized = false;
        initableBroker = null;
        name = null;
        serviceBroker = null;
    }

    /**
     * Returns the configuration of this service.
     *
     * @return The configuration of this service.
     */
    public Configuration getConfiguration()
    {
        if (name == null)
        {
            return null;
        }
        else
        {
            if (configuration == null)
            {
                configuration = getServiceBroker().getConfiguration(name);
            }
            return configuration;
        }
    }

    public void init(ServletConfig config)
            throws InitializationException
    {
        setInit(true);
    }

    public void setInitableBroker(InitableBroker broker)
    {
        this.initableBroker = broker;
    }

    public InitableBroker getInitableBroker()
    {
        return initableBroker;
    }

    public void init(Object data)
            throws InitializationException
    {
        init((ServletConfig) data);
    }

    public void init() throws InitializationException
    {
        setInit(true);
    }

    protected void setInit(boolean value)
    {
        isInitialized = value;
    }

    public boolean getInit()
    {
        return isInitialized;
    }

    /**
     * Shuts down this service.
     */
    public void shutdown()
    {
        setInit(false);
    }

    public Properties getProperties()
    {
        return ConfigurationConverter.getProperties(getConfiguration());
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public ServiceBroker getServiceBroker()
    {
        return serviceBroker;
    }

    public void setServiceBroker(ServiceBroker broker)
    {
        this.serviceBroker = broker;
    }
}
