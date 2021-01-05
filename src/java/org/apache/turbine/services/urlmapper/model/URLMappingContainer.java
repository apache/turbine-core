package org.apache.turbine.services.urlmapper.model;

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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * URL Map Container Model Class
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
@XmlRootElement(name="url-mapping")
@XmlAccessorType(XmlAccessType.NONE)
public class URLMappingContainer
{
    /**
     * Name of this map.
     */
    @XmlAttribute
    private String name;

    /**
     * The list of map entries
     */
    private CopyOnWriteArrayList<URLMapEntry> urlMapEntries = new CopyOnWriteArrayList<>();

    /**
     * Set the name of this map.
     *
     * @param name
     *            Name of this map.
     */
    protected void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the name of this map.
     *
     * @return String Name of this map.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the list of map entries
     */
    @XmlElementWrapper(name="maps")
    @XmlElement(name="map")
    public List<URLMapEntry> getMapEntries()
    {
        return urlMapEntries;
    }

    /**
     * Set new map entries during deserialization
     *
     * @param newURLMapEntries the newURLMapEntries to set
     */
    protected void setMapEntries(List<URLMapEntry> newURLMapEntries)
    {
        this.urlMapEntries = new CopyOnWriteArrayList<URLMapEntry>(newURLMapEntries);
    }
}
