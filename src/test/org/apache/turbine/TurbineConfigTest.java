package org.apache.turbine;


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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.TurbineXmlConfig;

/**
 * This testcase verifies that TurbineConfig can be used to startup Turbine in a non
 * servlet environment properly.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class TurbineConfigTest
    extends BaseTestCase
{
    private static TurbineConfig tc = null;
    private static TurbineXmlConfig txc = null;

    public TurbineConfigTest(String name)
            throws Exception
    {
        super(name);
    }

    public void testTurbineConfigWithPropertiesFile() throws Exception
    {
        String value = new File("/conf/test/TemplateService.properties").getPath();
        tc = new TurbineConfig(".", value);
        Turbine turbine = new Turbine();

        ServletConfig config = tc;
        ServletContext context = config.getServletContext();

        String confFile= turbine.findInitParameter(context, config,
                TurbineConfig.PROPERTIES_PATH_KEY,
                null);
        assertEquals(value, confFile);
    }

    public void testTurbineXmlConfigWithConfigurationFile() throws Exception
    {
        String value = new File("/conf/test/TurbineConfiguration.xml").getPath();
            txc = new TurbineXmlConfig(".", value);
        Turbine turbine = new Turbine();

        ServletConfig config = txc;
        ServletContext context = config.getServletContext();

            String confFile= turbine.findInitParameter(context, config,
                    TurbineConfig.CONFIGURATION_PATH_KEY,
                    null);
        assertEquals(value, confFile);
        }
}
