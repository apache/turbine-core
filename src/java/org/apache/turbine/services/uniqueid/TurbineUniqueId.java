package org.apache.turbine.services.uniqueid;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.apache.turbine.services.TurbineServices;

/**
 * This is a facade class for {@link UniqueIdService}.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public abstract class TurbineUniqueId
{
    /**
     * Utility method for accessing the service
     * implementation
     *
     * @return a UniqueIdService implementation instance
     */
    protected static UniqueIdService getService()
    {
        return (UniqueIdService) TurbineServices
                .getInstance().getService(UniqueIdService.SERVICE_NAME);
    }

    /**
     * <p> Returs an identifer of this Turbine instance that is unique
     * both on the server and worldwide.
     *
     * @return A String with the instance identifier.
     */
    public static String getInstanceId()
    {
        return getService().getInstanceId();
    }

    /**
     * <p> Returns an identifier that is unique within this turbine
     * instance, but does not have random-like apearance.
     *
     * @return A String with the non-random looking instance
     * identifier.
     */
    public static String getUniqueId()
    {
        return getService().getUniqueId();
    }

    /**
     * <p> Returns a unique identifier that looks like random data.
     *
     * @return A String with the random looking instance identifier.
     */
    public static String getPseudorandomId()
    {
        return getService().getPseudorandomId();
    }
}
