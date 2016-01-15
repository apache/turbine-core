package org.apache.turbine.pipeline;


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

import java.io.StringWriter;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests TurbinePipeline.
 *
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
public class PipelineTest
{
    private final static int THREADS = 100;
    private final static int LOOPS = 10000;

    /**
     * Tests the Pipeline.
     */
    @Test public void testPipeline() throws Exception
    {
        // Make sure Valves are getting added properly to the
        // Pipeline.
        StringWriter writer = new StringWriter();
        Pipeline pipeline = new TurbinePipeline();

        SimpleValve valve = new SimpleValve();
        valve.setWriter(writer);
        valve.setValue("foo");
        pipeline.addValve(valve);
        valve = new SimpleValve();
        valve.setWriter(writer);
        valve.setValue("bar");
        pipeline.addValve(valve);

        pipeline.invoke(new DefaultPipelineData());

        assertEquals("foobar", writer.toString());
    }

    /**
     * Tests the Pipeline throughput.
     */
    @Ignore("For performance tests only") @Test public void testPipelinePerformance() throws Exception
    {
        StringWriter writer = new StringWriter();
        Pipeline pipeline = new TurbinePipeline();

        SimpleValve valve = new SimpleValve();
        valve.setWriter(writer);
        valve.setValue("foo");
        pipeline.addValve(valve);
        valve = new SimpleValve();
        valve.setWriter(writer);
        valve.setValue("bar");
        pipeline.addValve(valve);

        Worker[] worker = new Worker[THREADS];
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < THREADS; i++)
        {
            worker[i] = new Worker(pipeline);
            worker[i].start();
        }

        for (int i = 0; i < THREADS; i++)
        {
            worker[i].join();
        }

        System.out.println(System.currentTimeMillis() - startTime);
    }

    /**
     * Worker thread
     */
    protected class Worker extends Thread
    {
        Pipeline pipeline;

        /**
         * Constructor
         *
         * @param pipeline
         */
        public Worker(Pipeline pipeline)
        {
            super();
            this.pipeline = pipeline;
        }

        @Override
        public void run()
        {
            PipelineData pd = new DefaultPipelineData();

            for (int idx = 0; idx < LOOPS; idx++)
            {
                try
                {
                    pipeline.invoke(pd);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
