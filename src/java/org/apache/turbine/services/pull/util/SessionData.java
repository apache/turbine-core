package org.apache.turbine.services.pull.util;


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


import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.turbine.services.pull.ApplicationTool;

/**
 * Pull tool designed to be used in the session scope for storage of
 * temporary data.  This tool should eliminate the need for the
 * {@link org.apache.turbine.om.security.User#setTemp} and
 * {@link org.apache.turbine.om.security.User#getTemp} methods.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class SessionData implements ApplicationTool
{
    /** Storage of user defined data */
    private Map<String, Object> dataStorage;

    /**
     * Initialize the application tool.
     *
     * @param data initialization data
     */
    public void init(Object data)
    {
        dataStorage = new HashMap<String, Object>();
    }

    /**
     * Refresh the application tool.
     */
    public void refresh()
    {
        // do nothing
    }

    /**
     * Gets the data stored under the key.  Null will be returned if the
     * key does not exist or if null was stored under the key.
     * <p>
     * To check for a key with a null value use {@link #containsKey}.
     *
     * @param key key under which the data is stored.
     * @return <code>Object</code> stored under the key.
     */
    public Object get(String key)
    {
        return dataStorage.get(key);
    }

    /**
     * Determines is a given key is stored.
     *
     * @param key  the key to check for
     * @return true if the key was found
     */
    public boolean containsKey(String key)
    {
        return dataStorage.containsKey(key);
    }

    /**
     * Stores the data.  If the key already exists, the value will be
     * overwritten.
     *
     * @param key   key under which the data will be stored.
     * @param value data to store under the key.  Null values are allowed.
     */
    public void put(String key, Object value)
    {
        dataStorage.put(key, value);
    }

    /**
     * Clears all data
     */
    public void clear()
    {
        dataStorage.clear();
    }

    /**
     * Gets a iterator for the keys.
     *
     * @return <code>Iterator</code> for the keys
     */
    public Iterator<String> iterator()
    {
        return dataStorage.keySet().iterator();
    }
}
