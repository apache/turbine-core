package org.apache.turbine.pipeline;

import java.util.HashMap;
import java.util.Map;

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.util.RunData;


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
 * <p>A <b>PipelineData</b> is a holder for data being passed from one
 * Valve to the next.
 * The detailed contract for a Valve is included in the description of
 * the <code>invoke()</code> method below.</p>
 *
 * <b>HISTORICAL NOTE</b>:  The "PipelineData" name was assigned to this
 * holder as it functions similarly to the RunData object, but without
 * the additional methods
 *
 * @author <a href="mailto:epugh@opensourceconnections.com">Eric Pugh</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 */
public class DefaultPipelineData implements PipelineData
{
    private final Map<Class<?>, Map<Class<?>, ? super Object>> map =
        new HashMap<>();

    /**
     * Put a configured map of objects into the pipeline data object
     *
     * @param key the key class
     * @param value the value map
     */
    @Override
    public void put(Class<?> key, Map<Class<?>, ? super Object> value)
    {
        map.put(key, value);
    }

    /**
     * Get the configured map of objects for the given key
     *
     * @param key the key class
     * @return the value map or null if no such key exists
     */
    @Override
    public Map<Class<?>, ? super Object> get(Class<?> key)
    {
        return map.get(key);
    }

    /**
     * Get a value from the configured map of objects for the given keys
     *
     * @param key the key class
     * @param innerKey the key into the value map
     * @return the inner value or null if no such keys exist
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<?> key, Class<T> innerKey)
    {
        Map<Class<?>, ? super Object> innerMap = get(key);
        if (innerMap == null)
        {
            return null;
        }
        return (T) innerMap.get(innerKey);
    }

    /**
     * Put object back into RunDataService for recycling
     */
    @Override
    public void close() throws Exception
    {
        RunDataService rds = (RunDataService) TurbineServices.getInstance().getService(RunDataService.SERVICE_NAME);
        if (rds != null)
        {
            rds.putRunData((RunData) this);
        }
    }
}
