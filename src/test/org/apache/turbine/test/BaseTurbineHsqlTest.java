package org.apache.turbine.test;

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

import org.apache.turbine.Turbine;

import org.apache.turbine.test.HsqlDB;

/**
 * A base class to implement tests that need a running
 * Turbine framework on it and an initialized Hsql Database
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public abstract class BaseTurbineHsqlTest
        extends BaseTurbineTest
{
    private static HsqlDB hsqlDB = null;

    public BaseTurbineHsqlTest(String name, String config)
            throws Exception
    {
        super(name, config);

        if (hsqlDB == null)
        {
            hsqlDB = new HsqlDB("jdbc:hsqldb:.", Turbine.getRealPath("conf/test/create-db.sql"));
        }
    }
}
