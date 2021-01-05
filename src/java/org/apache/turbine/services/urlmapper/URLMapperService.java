package org.apache.turbine.services.urlmapper;

import org.apache.fulcrum.parser.ParameterParser;
import org.apache.turbine.services.Service;
import org.apache.turbine.util.uri.TurbineURI;

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
 * The URL mapper service provides methods to map a set of parameters to a
 * simplified URL and vice-versa. This service was inspired by the
 * Liferay Friendly URL Mapper.
 *
 * A mapper valve and a link pull tool are provided for easy application.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public interface URLMapperService extends Service
{
    /**
     * The service identifier.
     */
    String SERVICE_NAME = "URLMapperService";

    /**
     * Map a set of parameters (contained in TurbineURI PathInfo and QueryData)
     * to a TurbineURI
     *
     * @param uri the URI to be modified (with setScriptName())
     */
    void mapToURL(TurbineURI uri);

    /**
     * Map a simplified URL to a set of parameters
     *
     * @param url the URL
     * @param pp a ParameterParser to use for parameter mangling
     */
    void mapFromURL(String url, ParameterParser pp);
}
