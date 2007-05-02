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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;

import org.apache.turbine.Turbine;

/**
 * Base functionality to be extended by all Apache Turbine test cases.  Test
 * case implementations are used to automate testing via JUnit.
 *
 * @author <a href="mailto:celkins@scardini.com">Christopher Elkins</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public abstract class BaseTestCase
        extends TestCase
{
    private static File log4jFile = new File("conf/test/Log4j.properties");

    public BaseTestCase(String name)
            throws Exception
    {
        super(name);

        try
        {
            Properties p = new Properties();
            p.load(new FileInputStream(log4jFile));
            p.setProperty(Turbine.APPLICATION_ROOT_KEY, new File(".").getAbsolutePath());
            PropertyConfigurator.configure(p);
        }
        catch (FileNotFoundException fnf)
        {
            System.err.println("Could not open Log4J configuration file "
                    + log4jFile);
        }
    }
}

