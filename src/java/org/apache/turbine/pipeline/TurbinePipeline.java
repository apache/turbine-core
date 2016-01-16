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

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.turbine.annotation.AnnotationProcessor;
import org.apache.turbine.util.TurbineException;

/**
 * Flexible implementation of a {@link org.apache.turbine.pipeline.Pipeline}.
 * Originally based on code from Catalina and ideas from Apache httpd.
 *
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:jvanzyl@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 */
@XmlRootElement(name="pipeline")
@XmlAccessorType(XmlAccessType.NONE)
public class TurbinePipeline
        implements Pipeline, ValveContext
{
    /**
     * The "Turbine Classic" pipeline.
     */
    public static final String CLASSIC_PIPELINE =
            "WEB-INF/conf/turbine-classic-pipeline.xml";

    /**
     * Name of this pipeline.
     */
    @XmlAttribute
    private String name;

    /**
     * The set of Valves associated with this Pipeline.
     */
    private CopyOnWriteArrayList<Valve> valves = new CopyOnWriteArrayList<Valve>();

    /**
     * The per-thread execution state for processing through this pipeline.
     */
    private ThreadLocal<Iterator<Valve>> state = new ThreadLocal<Iterator<Valve>>();

    /**
     * @see org.apache.turbine.pipeline.Pipeline#initialize()
     */
    @Override
    public void initialize()
            throws Exception
    {
        // Valve implementations are added to this Pipeline using the
        // Mapper.

        // Initialize the valves
        for (Valve v : valves)
        {
            AnnotationProcessor.process(v);
            v.initialize();
        }
    }

    /**
     * Set the name of this pipeline.
     *
     * @param name
     *            Name of this pipeline.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the name of this pipeline.
     *
     * @return String Name of this pipeline.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @see org.apache.turbine.pipeline.Pipeline#addValve(Valve)
     */
    @Override
    public void addValve(Valve valve)
    {
        // Add this Valve to the end of the set associated with this Pipeline
        valves.add(valve);
    }

    /**
     * @see org.apache.turbine.pipeline.Pipeline#getValves()
     */
    @Override
    @XmlElementWrapper(name="valves")
    @XmlElement(name="valve")
    @XmlJavaTypeAdapter(XmlValveAdapter.class)
    public Valve[] getValves()
    {
        return valves.toArray(new Valve[0]);
    }

    /**
     * Set new valves during deserialization
     *
     * @param valves the valves to set
     */
    protected void setValves(Valve[] valves)
    {
        this.valves = new CopyOnWriteArrayList<Valve>(valves);
    }

    /**
     * @see org.apache.turbine.pipeline.Pipeline#removeValve(Valve)
     */
    @Override
    public void removeValve(Valve valve)
    {
        valves.remove(valve);
    }

    /**
     * @see org.apache.turbine.pipeline.Pipeline#invoke(PipelineData)
     */
    @Override
    public void invoke(PipelineData pipelineData)
            throws TurbineException, IOException
    {
        // Initialize the per-thread state for this thread
        state.set(valves.iterator());

        // Invoke the first Valve in this pipeline for this request
        invokeNext(pipelineData);
    }

    /**
     * @see org.apache.turbine.pipeline.ValveContext#invokeNext(PipelineData)
     */
    @Override
    public void invokeNext(PipelineData pipelineData)
            throws TurbineException, IOException
    {
        // Identify the current valve for the current request thread
        Iterator<Valve> current = state.get();

        if (current.hasNext())
        {
            // Invoke the requested Valve for the current request
            // thread and increment its thread-local state.
            current.next().invoke(pipelineData, this);
        }
    }
}
