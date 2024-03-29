package org.apache.turbine.pipeline;

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

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Creates Valve objects.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class XmlValveAdapter extends XmlAdapter<XmlValve, Valve>
{
    /**
     * @see jakarta.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Valve unmarshal(XmlValve xmlValve) throws Exception
    {
        Class<?> valveClass = Class.forName(xmlValve.getClazz());
        return (Valve) valveClass.getDeclaredConstructor().newInstance();
    }

    /**
     * @see jakarta.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public XmlValve marshal(Valve valve) throws Exception
    {
        XmlValve xmlValve = new XmlValve();
        xmlValve.setClazz(valve.getClass().getName());

        return xmlValve;
    }
}
