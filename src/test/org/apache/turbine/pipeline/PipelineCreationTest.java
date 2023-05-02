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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.io.StringReader;
import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

/**
 * Tests TurbinePipeline.
 *
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
public class PipelineCreationTest
{
    private Pipeline pipeline;

    @BeforeEach
    public void setUp()
    {
        pipeline = new TurbinePipeline();
        pipeline.addValve(new SimpleValve());
        pipeline.addValve(new DetermineActionValve());
    }

    @Test
    public void testSavingPipeline() throws Exception
    {
        JAXBContext context = JAXBContext.newInstance(TurbinePipeline.class);
        Marshaller marshaller = context.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(pipeline, writer);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<pipeline><valves>"
                + "<valve>org.apache.turbine.pipeline.SimpleValve</valve>"
                + "<valve>org.apache.turbine.pipeline.DetermineActionValve</valve>"
                + "</valves></pipeline>", writer.toString());
    }

    @Test
    public void testReadingPipeline() throws Exception
    {
        String xml = "<pipeline name=\"default\"><valves>"
                + "<valve>org.apache.turbine.pipeline.SimpleValve</valve>"
                + "<valve>org.apache.turbine.pipeline.DetermineActionValve</valve>"
                + "</valves></pipeline>";
        JAXBContext context = JAXBContext.newInstance(TurbinePipeline.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(xml);
        Pipeline pipeline = (Pipeline) unmarshaller.unmarshal(reader);
        assertEquals(2, pipeline.getValves().length);
        assertTrue(pipeline.getValves()[0] instanceof SimpleValve);
        assertTrue(pipeline.getValves()[1] instanceof DetermineActionValve);
    }

}
