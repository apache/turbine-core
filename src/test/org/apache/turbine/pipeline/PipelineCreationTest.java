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



import junit.framework.TestCase;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Tests TurbinePipeline.
 *
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
public class PipelineCreationTest extends TestCase
{
    private Pipeline pipeline;
    /**
     * Constructor
     */
    public PipelineCreationTest(String testName)
    {
        super(testName);
    }

    public void setUp(){
        pipeline = new TurbinePipeline();
        pipeline.addValve(new SimpleValve());
        pipeline.addValve(new DetermineActionValve());
    }


    public void testSavingPipelineWXstream() throws Exception
    {
        XStream xstream = new XStream(new DomDriver()); // does not require XPP3 library

        String xml = xstream.toXML(pipeline);
        //System.out.println(xml);
        //Pipeline pipeline = (Pipeline)xstream.fromXML(xml);

    }

    public void testReadingPipelineWXstream() throws Exception{
        String xml="<org.apache.turbine.pipeline.TurbinePipeline>  <valves>    <org.apache.turbine.pipeline.SimpleValve/>    <org.apache.turbine.pipeline.DetermineActionValve/>  </valves></org.apache.turbine.pipeline.TurbinePipeline>";
        XStream xstream = new XStream(new DomDriver()); // does not require XPP3 library
        Object o = xstream.fromXML(xml);
        Pipeline pipeline = (Pipeline)o;
        assertEquals(pipeline.getValves().length,2);
        assertTrue(pipeline.getValves()[0] instanceof SimpleValve);
        assertTrue(pipeline.getValves()[1] instanceof DetermineActionValve);
    }

}
