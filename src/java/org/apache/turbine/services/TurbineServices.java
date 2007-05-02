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

/**
 * This is a singleton utility class that acts as a Services broker.
 *
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:burton@apache.org">Kevin Burton</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TurbineServices
        extends BaseServiceBroker
        implements ServiceManager
{
    /** The single instance of this class. */
    private static ServiceManager instance = new TurbineServices();

    /**
     * This constructor is protected to force clients to use
     * getInstance() to access this class.
     */
    protected TurbineServices()
    {
        super();
    }

    /**
     * The method through which this class is accessed as a broker.
     *
     * @return The single instance of this class.
     */
    public static ServiceManager getInstance()
    {
        return instance;
    }

    /**
     * The method through which to change the default manager.
     * Note that services of the previous manager will be shutdown.
     * @param manager a new service manager.
     */
    public static synchronized void setManager(ServiceManager manager)
    {
        ServiceManager previous = instance;
        instance = manager;
        if (previous != null)
        {
            previous.shutdownServices();
        }
    }
}
