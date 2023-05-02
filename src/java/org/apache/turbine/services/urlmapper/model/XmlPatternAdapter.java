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

import java.util.regex.Pattern;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Creates Regex Pattern objects.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class XmlPatternAdapter extends XmlAdapter<String, Pattern>
{
    /**
     * @see jakarta.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Pattern unmarshal(String urlPattern) throws Exception
    {
        return Pattern.compile(urlPattern);
    }

    /**
     * @see jakarta.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public String marshal(Pattern pattern) throws Exception
    {
        return pattern.pattern();
    }
}
