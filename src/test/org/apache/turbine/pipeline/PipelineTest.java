package org.apache.turbine.pipeline;


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


import java.io.StringWriter;

import junit.framework.TestCase;

/**
 * Tests TurbinePipeline.
 *
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
public class PipelineTest extends TestCase
{
    /**
     * Constructor
     */
    public PipelineTest(String testName)
    {
        super(testName);
    }

    /**
     * Tests the Pipeline.
     */
    public void testPipeline() throws Exception
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
}
