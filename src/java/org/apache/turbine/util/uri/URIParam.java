package org.apache.turbine.util.uri;


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


import org.apache.commons.lang.StringUtils;

/**
 * Helper Class to keep a key and a value together in
 * one object. Used for URI Parameters
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class URIParam
{
    /** Key */
    private String key = null;

    /** Value */
    private Object value = null;

    /**
     * Creates a new Object from Key and Value
     *
     * @param key A String with the Param Name.
     * @param value An Object with the Value.
     *
     */
    public URIParam(String key, Object value)
    {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the key.
     *
     * @return The key value.
     *
     */
    public String getKey()
    {
        return (StringUtils.isNotEmpty(key)) ? key : "";
    }

    /**
     * Returns the value.
     *
     * @return The value of this object.
     *
     */
    public Object getValue()
    {
        return value;
    }
}
