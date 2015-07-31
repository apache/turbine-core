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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.FileSystem;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.TurbineXmlConfig;
import org.junit.Test;

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

    private TurbineConfig tc = null;
    private TurbineXmlConfig txc = null;

    @Test
    public void testCreateTurbineWithConfigurationXML() throws Exception
    {
        txc = new TurbineXmlConfig(".", "conf/test/TurbineConfiguration.xml");

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

    @Test
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
    
    @Test
    public void testCreateTurbineWithIncludedConfiguration() throws Exception
    {
        String confPath = Turbine.getRealPath( "/conf/test/usersettings.properties" );
        try
        {
            Configuration configuration = new PropertiesConfiguration(confPath);;
            assertNotNull("No Configuration Object found!", configuration);
            assertFalse("Make sure we have values", configuration.isEmpty());

            String key = "scheduledjob.cache.size";
            assertEquals("Read a config value " + key + ", received:" + configuration.getString(key), "100", configuration.getString(key));
            String key2 ="module.cache";
            assertEquals("Read a config value " + key2 + ", received:" + configuration.getString(key2), "false", configuration.getString(key2));
        }
        catch (Exception e)
        {
            throw e;
        }
    }
    
    @Test
    public void testCreateTurbineWithXMLBuilderConfiguration() throws Exception
    {
        String configurationRessourcePath ="conf/test/ConfigurationBuilder.xml"; 
        tc = new TurbineXmlConfig(".",configurationRessourcePath );

        try
        {
            tc.initialize();

            Configuration configuration = Turbine.getConfiguration();
            assertNotNull("No Configuration Object found!", configuration);
            assertFalse("Make sure we have values", configuration.isEmpty());
            
            //assertTrue("Test  combined configuration is"+ configuration, configuration instanceof CombinedConfiguration);

            // overridden value
            String key = "scheduledjob.cache.size";
            assertEquals("Read a config value " + key + ", received:" + configuration.getInt(key), 100, configuration.getInt(key));
            
            // double overridden value
            key = "module.cache";
            assertEquals("Read a config value " + key + ", received:" + configuration.getBoolean(key), false, configuration.getBoolean(key));
            // new property
            key = "tests.test";
            configuration.addProperty( key, 123 );
            assertEquals("Read a config value " + key + ", received:" + configuration.getInt(key), 123, configuration.getInt(key));
            // not set
            key="test.nulltest3";
            assertEquals("Read a included config value " + key + ", received:" + configuration.getString(key), null, configuration.getString(key));
            // overridden value
            key="services.PullService.earlyInit";
            assertEquals("Read a config value " + key + ", received:" + configuration.getBoolean(key), true, configuration.getBoolean(key));
            configuration.setProperty( key, false );
            assertEquals("Read a config value " + key + ", received:" + configuration.getBoolean(key), false, configuration.getBoolean(key));
            
            // converts to URL, cft. RFC2396
            URL testURL = FileSystem.getDefaultFileSystem().locateFromURL(new File( Turbine.getApplicationRoot()).toURI().toString() , configurationRessourcePath);
            assertNotNull( "Should be a valid URL",testURL);
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
