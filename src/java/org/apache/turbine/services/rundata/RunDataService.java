package org.apache.turbine.services.rundata;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.turbine.services.Service;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * The RunData Service provides the implementations for RunData and
 * related interfaces required by request processing. It supports
 * different configurations of implementations, which can be selected
 * by specifying a configuration key. It may use pooling, in which case
 * the implementations should implement the Recyclable interface.
 *
 * @version $Id$
 */
public interface RunDataService
    extends Service
{
    /** The key under which this service is stored in TurbineServices. */
    String SERVICE_NAME = "RunDataService";

    /** The default parser configuration key. */
    String DEFAULT_CONFIG = "default";

    /** The property for the implemention of the RunData object */
    String RUN_DATA_KEY = "run.data";

    /** The property for the implemention of the ParameterParser. */
    String PARAMETER_PARSER_KEY = "parameter.parser";

    /** The property for the implemention of the CookieParser. */
    String COOKIE_PARSER_KEY = "cookie.parser";

    /**
     * Gets a default RunData object.
     *
     * @param req a servlet request.
     * @param res a servlet response.
     * @param config a servlet config.
     * @return a new or recycled RunData object.
     * @throws TurbineException if the operation fails.
     */
    RunData getRunData(HttpServletRequest req,
                       HttpServletResponse res,
                       ServletConfig config)
            throws TurbineException;

    /**
     * Gets a RunData object from a specific configuration.
     *
     * @param key a configuration key.
     * @param req a servlet request.
     * @param res a servlet response.
     * @param config a servlet config.
     * @return a new or recycled RunData object.
     * @throws TurbineException if the operation fails.
     */
    RunData getRunData(String key,
                       HttpServletRequest req,
                       HttpServletResponse res,
                       ServletConfig config)
            throws TurbineException;

    /**
     * Puts the used RunData object back to the factory for recycling.
     *
     * @param data the used RunData object.
     * @return true, if pooling is supported and the object was accepted.
     */
    boolean putRunData(RunData data);
}
