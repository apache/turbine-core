package org.apache.turbine.om;

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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.fulcrum.pool.Recyclable;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.pull.ApplicationTool;

/**
 * A Pull tool to make om objects available to a template
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 *
 * @deprecated This class is probably not used anymore, it may have been intended for cacheable Torque OM or might be used with Fulcrum Security Torque.
 */
@Deprecated
public class OMTool implements ApplicationTool, Recyclable
{
    protected ConcurrentMap<String, Object> omMap;

    // note the following could be a static attribute to reduce memory
    // footprint. Might require a service to front load the
    // PullHelpers to avoid MT issues. A multiple write is not so bad
    // though

    /** The cache of PullHelpers. **/
    private ConcurrentMap<String, OMTool.PullHelper> pullMap =
            new ConcurrentHashMap<String, OMTool.PullHelper>();

    /**
     *  The Factory responsible for retrieving the
     *  objects from storage
     */
    protected RetrieverFactory omFactory;

    /**
     * Default constructor
     * @throws Exception if creating the factory fails
     */
    public OMTool() throws Exception
    {
        omMap = new ConcurrentHashMap<String, Object>();
        String className = Turbine.getConfiguration().getString("tool.om.factory");
        this.omFactory = (RetrieverFactory)Class.forName(className).newInstance();
    }

    /**
     * Prepares tool for a single request
     *
     * @param data the initialization data
     */
    @Override
    public void init(Object data)
    {
        // data = (RunData)data;
    }

    /**
     * Implementation of ApplicationTool interface is not needed for this
     * method as the tool is request scoped
     */
    @Override
    public void refresh()
    {
        // empty
    }

    /**
     * Inner class to present a nice interface to the template designer
     */
    protected class PullHelper
    {
        String omName;

        protected PullHelper(String omName)
        {
            this.omName = omName;
        }

        public Object setKey(String key)
            throws Exception
        {
            Object om = null;
            String inputKey = omName + key;

            if (omMap.containsKey(inputKey))
            {
                om = omMap.get(inputKey);
            }
            else
            {
                om = omFactory.getInstance(omName).retrieve(key);
                omMap.put(inputKey, om);
            }

            return om;
        }
    }

    /**
     * Get the {@link PullHelper} object with the given name
     * @param omName the object name
     * @return the PullHelper
     * @throws Exception if retrieving the object fails
     */
    public PullHelper get(String omName) throws Exception
    {
        PullHelper ph = pullMap.putIfAbsent(omName, new OMTool.PullHelper(omName));
        if (ph == null)
        {
            return pullMap.get(omName);
        }

        return ph;
    }

    /**
     * Get the object with the given name and key
     * @param omName the object name
     * @param key the object key
     * @return the object
     * @throws Exception if retrieving the object fails
     */
    public Object get(String omName, String key) throws Exception
    {
        return get(omName).setKey(key);
    }

    // ****************** Recyclable implementation ************************

    private boolean disposed;

    /**
     * Recycles the object for a new client. Recycle methods with
     * parameters must be added to implementing object and they will be
     * automatically called by pool implementations when the object is
     * taken from the pool for a new client. The parameters must
     * correspond to the parameters of the constructors of the object.
     * For new objects, constructors can call their corresponding recycle
     * methods whenever applicable.
     * The recycle methods must call their super.
     */
    @Override
    public void recycle()
    {
        disposed = false;
    }

    /**
     * Disposes the object after use. The method is called
     * when the object is returned to its pool.
     * The dispose method must call its super.
     */
    @Override
    public void dispose()
    {
        omMap.clear();
        disposed = true;
    }

    /**
     * Checks whether the recyclable has been disposed.
     * @return true, if the recyclable is disposed.
     */
    @Override
    public boolean isDisposed()
    {
        return disposed;
    }
}
