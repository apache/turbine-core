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


import org.apache.turbine.services.Service;

/**
 * <p> This service provides unique identifiers for the instance of
 * Turbine, and for objects it creates.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public interface UniqueIdService
        extends Service
{
    /** The service name */
    String SERVICE_NAME = "UniqueIdService";

    /**
     * <p> Returns an identifier of this Turbine instance that is unique
     * both on the server and worldwide.
     *
     * @return A String with the instance identifier.
     */
    String getInstanceId();

    /**
     * <p> Returns an identifier that is unique within this Turbine
     * instance, but does not have random-like appearance.
     *
     * <p> This method is intended to work fast; it can be used for
     * creating names of temporary files.
     *
     * @return A String with the non-random looking instance
     * identifier.
     * */
    String getUniqueId();

    /**
     * <p> Returns a unique identifier that looks like random data.
     *
     * <p> This method provides identifiers in a way that makes it
     * hard to guess or count, but still ensures their uniqueness
     * within this instance of Turbine.  It can be used for generating
     * cookies or other data that travels back and forth between
     * server and browser, and is potentially security sensitive.
     *
     * @return A String with the random looking instance identifier.
     */
    String getPseudorandomId();
}
