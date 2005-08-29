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

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;

import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.TurbineXmlConfig;

/**
 * Tests that the ConfigurationFactory and regular old properties methods both work.
 * Verify the overriding of properties.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class ConfigurationTest extends BaseTestCase
{
    public static final String SERVICE_PREFIX = "services.";

    /**
     * A <code>Service</code> property determining its implementing
     * class name .
     */
    public static final String CLASSNAME_SUFFIX = ".classname";

    private static TurbineConfig tc = null;
    private static TurbineXmlConfig txc = null;

    public ConfigurationTest(String name) throws Exception
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(ConfigurationTest.class);
    }

    public void testCreateTurbineWithConfigurationXML() throws Exception
    {
        txc = new TurbineXmlConfig(".", "/conf/test/TurbineConfiguration.xml");

        try
        {
            txc.initialize();

            Configuration configuration = Turbine.getConfiguration();
            assertNotNull("No Configuration Object found!", configuration);
            assertFalse("Make sure we have values", configuration.isEmpty());

            // overridden value
            String key = "module.cache";

            assertEquals("Read a config value " + key + ", received:" + configuration.getString(key), "true", configuration.getString(key));

            // non overridden value
            key = "scheduledjob.cache.size";
            assertEquals("Read a config value " + key + ", received:" + configuration.getString(key), "10", configuration.getString(key));
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            txc.dispose();
        }
    }

    public void testCreateTurbineWithConfiguration() throws Exception
    {
        tc = new TurbineConfig(".", "/conf/test/TemplateService.properties");

        try
        {
            tc.initialize();

            Configuration configuration = Turbine.getConfiguration();
            assertNotNull("No Configuration Object found!", configuration);
            assertFalse("Make sure we have values", configuration.isEmpty());

            String key = "scheduledjob.cache.size";
            assertEquals("Read a config value " + key + ", received:" + configuration.getString(key), "10", configuration.getString(key));
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            tc.dispose();
        }
    }

}
