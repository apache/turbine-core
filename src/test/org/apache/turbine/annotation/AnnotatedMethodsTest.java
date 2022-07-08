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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.turbine.services.assemblerbroker.AssemblerBrokerService;
import org.apache.turbine.services.pull.PullService;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.TurbineException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test class for AnnotatedMethodsTest to test method fields annotation
 *
 */
public class AnnotatedMethodsTest {

    private static AssemblerBrokerService asb;
    private static TurbineConfig tc = null;
    private static PullService pullService;

    @BeforeAll
    public static void setup()
    {
        // required to initialize defaults
        tc = new TurbineConfig(
                        ".",
                        "/conf/test/CompleteTurbineResources.properties");
        tc.initialize();
    }

    @AfterAll
    public static void tearDown()
    {
        tc.dispose();
    }

    @TurbineService
    public void setAssemblerBrokerService(AssemblerBrokerService df)
    {
        AnnotatedMethodsTest.asb = df;
    }

    @TurbineService
    public static void setPullService(PullService pullService) {
        AnnotatedMethodsTest.pullService = pullService;
    }

    /*
     * Class under test for String format(Date, String)
     */
    @Test
    void testTool() throws TurbineException
    {
        AnnotationProcessor.process(this, true);
        assertNotNull(pullService);
        assertNotNull(asb);
    }

    @Tag("performance") // ignore in surefire, activating seems to be still buggy ?
    @Test
    public void testProcessingPerformance() throws TurbineException
    {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++)
        {
            AnnotationProcessor.process(this, true);
        }

        System.out.println(System.currentTimeMillis() - startTime);
    }

}
