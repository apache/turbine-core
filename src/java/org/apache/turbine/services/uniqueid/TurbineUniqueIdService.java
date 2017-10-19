package org.apache.turbine.services.uniqueid;


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


import java.security.MessageDigest;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.util.GenerateUniqueId;

/**
 * <p> This is an implementation of {@link UniqueIdService}.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TurbineUniqueIdService
        extends TurbineBaseService
        implements UniqueIdService
{
    /** Logging */
    private static Log log = LogFactory.getLog(TurbineUniqueIdService.class);

    /** The identifier of this instance of turbine. */
    protected static String turbineId = "UNKNOWN";

    protected static String turbineURL = "UNKNOWN";

    protected static int counter;


    /**
     * <p> Initializes the service upon first Turbine.doGet()
     * invocation.
     */
    @Override
    public void init()
            throws InitializationException
    {
        try
        {
            // This might be a problem if the unique Id Service runs
            // before Turbine got its first request. In this case,
            // getDefaultServerData will return just a dummy value
            // which is the same for all instances of Turbine.
            //
            // TODO This needs definitely further working.
            String url = Turbine.getDefaultServerData().toString();

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte [] bytesId = md.digest(url.getBytes("UTF-8"));
            turbineId = new String(Base64.encodeBase64(bytesId),"UTF-8");

            log.info("This is Turbine instance running at: " + url);
            log.info("The instance id is #" + turbineId);
            setInit(true);
        }
        catch (Exception e)
        {
            throw new InitializationException(
                    "Could not initialize TurbineUniqueId Service", e);
        }
    }

    /**
     * <p> Writes a message to the log upon system shutdown.
     */
    @Override
    public void shutdown()
    {
        log.info("Turbine instance running at " + turbineURL + " shutting down.");
    }

    /**
     * <p> Returns an identifier of this Turbine instance that is unique
     * both on the server and worldwide.  This identifier is computed
     * as an MD5 sum of the URL (including schema, address, port if
     * different that 80/443 respecively, context and servlet name).
     * There is an overwhelming probalility that this id will be
     * different that all other Turbine instances online.
     *
     * @return A String with the instance identifier.
     */
    public String getInstanceId()
    {
        return turbineId;
    }

    /**
     * <p> Returns an identifier that is unique within this turbine
     * instance, but does not have random-like apearance.
     *
     * @return A String with the non-random looking instance
     * identifier.
     */
    public String getUniqueId()
    {
        int current;
        synchronized (TurbineUniqueIdService.class)
        {
            current = counter++;
        }
        String id = Integer.toString(current);

        // If you manage to get more than 100 million of ids, you'll
        // start getting ids longer than 8 characters.
        if (current < 100000000)
        {
            id = ("00000000" + id).substring(id.length());
        }
        return id;
    }

    /**
     * <p> Returns a unique identifier that looks like random data.
     *
     * @return A String with the random looking instance identifier.
     */
    public String getPseudorandomId()
    {
        return GenerateUniqueId.getIdentifier();
    }
}
