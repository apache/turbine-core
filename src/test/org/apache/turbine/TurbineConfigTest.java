package org.apache.turbine;

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

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.turbine.Turbine;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.TurbineXmlConfig;

/**
 * This testcase verifies that TurbineConfig can be used to startup Turbine in a non
 * servlet environment properly.
 *
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

    public static Test suite()
    {
        return new TestSuite(TurbineConfigTest.class);
    }

    public void testTurbineConfigWithPropertiesFile() throws Exception
    {
        String value = new File("/conf/test/TemplateService.properties").getPath();
        tc = new TurbineConfig(".", value);

        ServletConfig config = (ServletConfig) tc;
        ServletContext context = config.getServletContext();

        String confFile= Turbine.findInitParameter(context, config, 
                TurbineConfig.PROPERTIES_PATH_KEY, 
                null);
        assertEquals(value, confFile);
    }
    
    public void testTurbineXmlConfigWithConfigurationFile() throws Exception
    {
        String value = new File("/conf/test/TurbineConfiguration.xml").getPath();
            txc = new TurbineXmlConfig(".", value);
            
        ServletConfig config = (ServletConfig) txc;
        ServletContext context = config.getServletContext();
            
            String confFile= Turbine.findInitParameter(context, config, 
                    TurbineConfig.CONFIGURATION_PATH_KEY, 
                    null);
        assertEquals(value, confFile);
        }
}
