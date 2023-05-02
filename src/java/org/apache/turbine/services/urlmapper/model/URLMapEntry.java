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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The url map model class
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
@XmlType(name="map")
@XmlAccessorType(XmlAccessType.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class URLMapEntry
{
    private Pattern urlPattern;
    private Map<String, String> implicit = new LinkedHashMap<>();
    private Map<String, String> ignore = new LinkedHashMap<>();
    private Map<String, String> override = new LinkedHashMap<>();

    private Map<String, Integer> groupNamesMap;
    private Set<String> relevantKeys = null;

    /**
     * @return the urlPattern
     */
    @XmlElement(name="pattern")
    @XmlJavaTypeAdapter(XmlPatternAdapter.class)
    @JsonProperty("pattern")
    public Pattern getUrlPattern()
    {
        return urlPattern;
    }

    /**
     * @param urlPattern the urlPattern to set
     */
    protected void setUrlPattern(Pattern urlPattern)
    {
        this.urlPattern = urlPattern;
    }

    /**
     * @return the implicit parameters
     */
    @XmlElement(name="implicit-parameters")
    @XmlJavaTypeAdapter(XmlParameterAdapter.class)
    @JsonProperty("implicit-parameters")
    public Map<String, String> getImplicitParameters()
    {
        return implicit;
    }

    /**
     * @param implicit the implicit parameters to set
     */
    protected void setImplicitParameters(Map<String, String> implicit)
    {
        this.implicit = implicit;
    }

    /**
     * @return the ignored parameters
     */
    @XmlElement(name="ignore-parameters")
    @XmlJavaTypeAdapter(XmlParameterAdapter.class)
    @JsonProperty("ignore-parameters")
    public Map<String, String> getIgnoreParameters()
    {
        return ignore;
    }

    /**
     * @param ignore the ignored parameters to set
     */
    protected void setIgnoreParameters(Map<String, String> ignore)
    {
        this.ignore = ignore;
    }

    /**
     * @return the override parameters
     */
    @XmlElement(name="override-parameters")
    @XmlJavaTypeAdapter(XmlParameterAdapter.class)
    @JsonProperty("override-parameters")
    public Map<String, String> getOverrideParameters()
    {
        return override;
    }

    /**
     * @param override the override parameters to set
     */
    protected void setOverrideParameters(Map<String, String> override)
    {
        this.override = override;
    }

    /**
     * Get the map of group names to group indices for the stored Pattern
     *
     * @return the groupNamesMap
     */
    public Map<String, Integer> getGroupNamesMap()
    {
        return groupNamesMap;
    }

    /**
     * Set the map of group names to group indices for the stored Pattern
     *
     * @param groupNamesMap the groupNamesMap to set
     */
    public void setGroupNamesMap(Map<String, Integer> groupNamesMap)
    {
        this.groupNamesMap = groupNamesMap;
    }

    /**
     * Get the set of relevant keys for comparison (cached for performance)
     *
     * @return the relevantKeys
     */
    public Set<String> getRelevantKeys()
    {
        return relevantKeys;
    }

    /**
     * Set the set of relevant keys for comparison (cached for performance)
     *
     * @param relevantKeys the relevantKeys to set
     */
    public void setRelevantKeys(Set<String> relevantKeys)
    {
        this.relevantKeys = relevantKeys;
    }

    @Override
    public String toString()
    {
    	return "URLMapEntry: [ pattern: " + urlPattern + ", implicit-parameters: " + implicit + ", override-parameters: " + override + ", ignore-parameters:" + ignore + " ]";
    }
}
