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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * A JAXB Class for holding a list of entries with key (in an attribute) and a value.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class XmlParameterList
{
    public static class XmlParameter
    {
        @XmlAttribute
        public String key;

        @XmlValue
        public String value;

        /**
         * Default Constructor
         */
        public XmlParameter()
        {
            // empty
        }

        /**
         * Constructor
         *
         * @param key the key
         * @param value the value
         */
        public XmlParameter(String key, String value)
        {
            this.key = key;
            this.value = value;
        }
    }

    private List<XmlParameter> xmlParameters;

    /**
     * Get the list of XmlParameters
     *
     * @return the xmlParameters
     */
    @XmlElement(name="parameter")
    public List<XmlParameter> getXmlParameters()
    {
        return xmlParameters;
    }

    /**
     * Set a list of XmlParameters
     *
     * @param xmlParameters the xmlParameters to set
     */
    public void setXmlParameters(List<XmlParameter> xmlParameters)
    {
        this.xmlParameters = xmlParameters;
    }
}
