package org.apache.turbine.services;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;

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
