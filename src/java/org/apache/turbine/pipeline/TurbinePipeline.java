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

import org.apache.turbine.util.TurbineException;

/**
 * Flexible implementation of a {@link org.apache.turbine.Pipeline}.
 * Originally based on code from Catalina and ideas from Apache httpd.
 *
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:jvanzyl@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 */
public class TurbinePipeline
    implements Pipeline, ValveContext
{
    /**
     * The "Turbine Classic" pipeline.
     */
    public static String CLASSIC_PIPELINE =
        "WEB-INF/conf/turbine-classic-pipeline.xml";

    /**
     * Name of this pipeline.
     */
    protected String name;

    /**
     * The set of Valves associated with this Pipeline.
     */
    protected Valve[] valves = new Valve[0];

    /**
     * The per-thread execution state for processing through this
     * pipeline.  The actual value is a java.lang.Integer object
     * containing the subscript into the <code>values</code> array, or
     * a subscript equal to <code>values.length</code> if the basic
     * Valve is currently being processed.
     */
    protected ThreadLocal state= new ThreadLocal();

    /**
     * @see org.apache.turbine.Pipeline#initialize()
     */
    public void initialize()
        throws Exception
    {
        if (state==null){
            state = new ThreadLocal();
        }

        // Valve implementations are added to this Pipeline using the
        // Mapper.

        // Initialize the valves
        for (int i = 0; i < valves.length; i++)
        {
            valves[i].initialize();
        }
    }

    /**
     * Set the name of this pipeline.
     *
     * @param name Name of this pipeline.
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
     * @see org.apache.turbine.Pipeline#addValve(Valve)
     */
    public void addValve(Valve valve)
    {
        // Add this Valve to the set associated with this Pipeline
        synchronized (valves)
        {
            Valve[] results = new Valve[valves.length + 1];
            System.arraycopy(valves, 0, results, 0, valves.length);
            results[valves.length] = valve;
            valves = results;
        }
    }

    /**
     * @see org.apache.turbine.Pipeline#getValves()
     */
    public Valve[] getValves()
    {
        synchronized (valves)
        {
            Valve[] results = new Valve[valves.length];
            System.arraycopy(valves, 0, results, 0, valves.length);
            return results;
        }
    }

    /**
     * @see org.apache.turbine.Pipeline#removeValve(Valve)
     */
    public void removeValve(Valve valve)
    {
        synchronized (valves)
        {
            // Locate this Valve in our list
            int index = -1;
            for (int i = 0; i < valves.length; i++)
            {
                if (valve == valves[i])
                {
                    index = i;
                    break;
                }
            }
            if (index < 0)
            {
                return;
            }

            // Remove this valve from our list
            Valve[] results = new Valve[valves.length - 1];
            int n = 0;
            for (int i = 0; i < valves.length; i++)
            {
                if (i == index)
                {
                    continue;
                }
                results[n++] = valves[i];
            }
            valves = results;
        }
    }

    /**
     * @see org.apache.turbine.Pipeline#invoke(RunData)
     */
    public void invoke(PipelineData pipelineData)
        throws TurbineException, IOException
    {
        // Initialize the per-thread state for this thread
        state.set(new Integer(0));

        // Invoke the first Valve in this pipeline for this request
        invokeNext(pipelineData);
    }

    /**
     * @see org.apache.turbine.ValveContext#invokeNext(RunData)
     */
    public void invokeNext(PipelineData pipelineData)
        throws TurbineException, IOException
    {
        // Identify the current subscript for the current request thread
        Integer current = (Integer) state.get();
        int subscript = current.intValue();

        if (subscript < valves.length)
        {
            // Invoke the requested Valve for the current request
            // thread and increment its thread-local state.
            state.set(new Integer(subscript + 1));
            valves[subscript].invoke(pipelineData, this);
        }
    }
}
