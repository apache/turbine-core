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

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationConverter;

/**
 * A base implementation of an {@link java.rmi.server.UnicastRemoteObject}
 * as a Turbine {@link org.apache.turbine.services.Service}.
 *
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 */
public class BaseUnicastRemoteService extends UnicastRemoteObject
        implements Service
{
    /**
     * Serial version.
     */
    private static final long serialVersionUID = -7775459623190960297L;

    protected Configuration configuration;
    private boolean isInitialized;
    private InitableBroker initableBroker;
    private String name;
    private ServiceBroker serviceBroker;

    /**
     * Default constructor
     * @throws RemoteException if the remote object cannot be created
     */
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
    @Override
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

    @Override
    public void setInitableBroker(InitableBroker broker)
    {
        this.initableBroker = broker;
    }

    /**
     * Get the {@link InitableBroker} instance
     * @return the broker instance
     */
    public InitableBroker getInitableBroker()
    {
        return initableBroker;
    }

    @Override
    public void init(Object data)
            throws InitializationException
    {
        init();
    }

    @Override
    public void init() throws InitializationException
    {
        setInit(true);
    }

    protected void setInit(boolean value)
    {
        isInitialized = value;
    }

    @Override
    public boolean getInit()
    {
        return isInitialized;
    }

    /**
     * Shuts down this service.
     */
    @Override
    public void shutdown()
    {
        setInit(false);
    }

    @Override
    public Properties getProperties()
    {
        return ConfigurationConverter.getProperties(getConfiguration());
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Get the {@link ServiceBroker} instance
     * @return the broker instance
     */
    public ServiceBroker getServiceBroker()
    {
        return serviceBroker;
    }

    @Override
    public void setServiceBroker(ServiceBroker broker)
    {
        this.serviceBroker = broker;
    }
}
