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

import org.apache.turbine.services.TurbineServices;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * Static wrapper for the RunData service. The name is completely
 * out of line of the other Turbine Services. So what? All the good
 * ones were taken.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public abstract class TurbineRunDataFacade
{
    /**
     * Utility method for accessing the service
     * implementation
     *
     * @return a RunDataService implementation instance
     */
    public static RunDataService getService()
    {
        return (RunDataService) TurbineServices
            .getInstance().getService(RunDataService.SERVICE_NAME);
    }

    /**
     * Gets a default RunData object.
     *
     * @param req a servlet request.
     * @param res a servlet response.
     * @param config a servlet config.
     * @return a new or recycled RunData object.
     * @throws TurbineException if the operation fails.
     */
    public static RunData getRunData(HttpServletRequest req,
                                     HttpServletResponse res,
                                     ServletConfig config)
        throws TurbineException
    {
        return getService().getRunData(req, res, config);
    }

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
    public static RunData getRunData(String key,
                                     HttpServletRequest req,
                                     HttpServletResponse res,
                                     ServletConfig config)
        throws TurbineException
    {
        return getService().getRunData(key, req, res, config);
    }

    /**
     * Puts the used RunData object back to the factory for recycling.
     *
     * @param data the used RunData object.
     * @return true, if pooling is supported and the object was accepted.
     */
    public static boolean putRunData(RunData data)
    {
        return getService().putRunData(data);
    }
}
