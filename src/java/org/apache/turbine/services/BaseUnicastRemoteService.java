package org.apache.turbine.services;

import org.apache.commons.configuration.Configuration;

import java.util.Properties;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.servlet.ServletConfig;


/**
 * A base implementation of an {@link java.rmi.server.UnicastRemoteObject}
 * as a Turbine {@link org.apache.turbine.services.Service}.
 *
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 */
public class BaseUnicastRemoteService extends UnicastRemoteObject
    implements Service
{
    protected Configuration configuration;
    private boolean isInitialized;
    private InitableBroker initableBroker;
    private String name;
    private Properties properties;
    private ServiceBroker serviceBroker;

    public BaseUnicastRemoteService()
        throws RemoteException
    {
        isInitialized = false;
        initableBroker = null;
        properties = null;
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
        if (name == null)
        {
            return null;
        }

        if (properties == null)
        {
            properties = getServiceBroker().getProperties(name);
        }
        return properties;
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
