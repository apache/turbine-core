package org.apache.turbine.services.uniqueid;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.java.lang.Bytes;
import org.apache.java.security.MD5;

import org.apache.turbine.Turbine;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.util.GenerateUniqueId;
import org.apache.turbine.util.RunData;

/**
 * <p> This is an implementation of {@link UniqueIdService}.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
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
     * @deprecated Use init() instead
     */
    public void init(RunData data)
    {
        init();
    }

    /**
     * <p> Initializes the service upon first Turbine.doGet()
     * invocation.
     */
    public void init()
    {
        ServerData = Turbine.getDefaultServerData();


        MD5 md5 = new MD5();
        turbineId = Bytes.toString(md5.digest(url.toString().getBytes()));

        log.info("This is Turbine instance running at: " + url);
        log.info("The instance id is #" + turbineId);
        setInit(true);
    }

    /**
     * <p> Writes a message to the log upon system shutdown.
     */
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
