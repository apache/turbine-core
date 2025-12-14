package org.apache.turbine.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

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
        assertEquals("""
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>\
            <pipeline><valves>\
            <valve>org.apache.turbine.pipeline.SimpleValve</valve>\
            <valve>org.apache.turbine.pipeline.DetermineActionValve</valve>\
            </valves></pipeline>""", writer.toString());
    }

    @Test
    public void testReadingPipeline() throws Exception
    {
        String xml = """
            <pipeline name="default"><valves>\
            <valve>org.apache.turbine.pipeline.SimpleValve</valve>\
            <valve>org.apache.turbine.pipeline.DetermineActionValve</valve>\
            </valves></pipeline>""";
        JAXBContext context = JAXBContext.newInstance(TurbinePipeline.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(xml);
        Pipeline pipeline = (Pipeline) unmarshaller.unmarshal(reader);
        assertEquals(2, pipeline.getValves().length);
        assertInstanceOf(SimpleValve.class, pipeline.getValves()[0]);
        assertInstanceOf(DetermineActionValve.class, pipeline.getValves()[1]);
    }

}
