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
import java.util.stream.Collectors;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.turbine.services.urlmapper.model.XmlParameterList.XmlParameter;

/**
 * Creates Map objects from XmlParameterList objects and vice-versa.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class XmlParameterAdapter extends XmlAdapter<XmlParameterList, Map<String, String>>
{
    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Map<String, String> unmarshal(XmlParameterList xmlList) throws Exception
    {
        // Make sure that order is kept
        return xmlList.getXmlParameters().stream()
                .collect(Collectors.toMap(xml -> xml.key, xml -> xml.value,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public XmlParameterList marshal(Map<String, String> map) throws Exception
    {
        XmlParameterList xmlList = new XmlParameterList();
        xmlList.setXmlParameters(map.entrySet().stream()
                .map(entry -> new XmlParameter(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));

        return xmlList;
    }
}
