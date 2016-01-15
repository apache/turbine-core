package org.apache.turbine.annotation;

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

import org.apache.commons.configuration.Configuration;
import org.apache.fulcrum.factory.FactoryService;
import org.apache.turbine.modules.Screen;
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.services.assemblerbroker.AssemblerBrokerService;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.TurbineException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the various annotations
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class AnnotationProcessorTest
{
    private static TurbineConfig tc;

    @TurbineConfiguration
    private Configuration completeConfiguration = null;

    @TurbineConfiguration("serverdata.default")
    private Configuration serverdataDefaultConfiguration = null;

    @TurbineConfiguration("module.cache")
    private boolean moduleCache = true;

    @TurbineConfiguration("action.cache.size")
    private int actionCacheSize = 0;

    @TurbineConfiguration("template.homepage")
    private String templateHomepage;

    @TurbineConfiguration("does.not.exist")
    private long notModified = 1;

    @TurbineLoader(Screen.class)
    private ScreenLoader screenLoader;

    @TurbineService
    private AssemblerBrokerService asb;

    @TurbineService
    private FactoryService factory;

    @BeforeClass
    public static void init() throws Exception
    {
        tc = new TurbineConfig(".", "/conf/test/CompleteTurbineResources.properties");
        tc.initialize();
    }

    @AfterClass
    public static void destroy()
        throws Exception
    {
        tc.dispose();
    }

    @Test
    public void testProcess() throws TurbineException
    {
        AnnotationProcessor.process(this);

        assertNotNull(completeConfiguration);
        assertFalse(completeConfiguration.getBoolean("module.cache", true));

        assertNotNull(serverdataDefaultConfiguration);
        assertEquals(80, serverdataDefaultConfiguration.getInt("serverPort"));

        assertFalse(moduleCache);
        assertEquals(20, actionCacheSize);
        assertEquals("Index.vm", templateHomepage);
        assertEquals(1, notModified);

        assertNotNull(screenLoader);
        assertNotNull(asb);
        assertNotNull(factory);
    }

    @Test
    public void testProcessingPerformance() throws TurbineException
    {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++)
        {
            AnnotationProcessor.process(this);
        }

        System.out.println(System.currentTimeMillis() - startTime);
    }
}
