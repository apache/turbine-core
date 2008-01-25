package org.apache.turbine.modules.screens;


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


// Turbine stuff.

import org.apache.ecs.ConcreteElement;
import org.apache.turbine.modules.Screen;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;

/**
 * Base class for writing Screens that output binary data.  This class
 * is provided as a helper class for those who want to write Screens
 * that output raw binary data.  For example, it may be extended into
 * a Screen that outputs a SVG file or a SWF (Flash Player format)
 * movie.  The only thing one has to do is to implement the two
 * methods <code>getContentType(RunData data)</code> and
 * <code>doOutput(RunData data)</code> (see below).
 *
 * <p> You migth want to take a look at the ImageServer screen class
 * contained in the TDK.<br>
 *
 * @author <a href="mailto:rkoenig@chez.com">Regis Koenig</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class RawScreen extends Screen
{
    /**
     * Build the Screen.  This method actually makes a call to the
     * doOutput() method in order to generate the Screen content.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @exception Exception, a generic exception.
     */
    protected final ConcreteElement doBuild(RunData data)
            throws Exception
    {
        data.getResponse().setContentType(getContentType(data));
        data.declareDirectResponse();
        doOutput(data);
        return null;
    }

    /**
     * Build the Screen.  This method actually makes a call to the
     * doOutput() method in order to generate the Screen content.
     *
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @exception Exception, a generic exception.
     */
    protected final ConcreteElement doBuild(PipelineData pipelineData)
            throws Exception
    {
        RunData data = (RunData) getRunData(pipelineData);
        return doBuild(data);
    }


    /**
     * Set the content type.  This method should be overidden to
     * actually set the real content-type header of the output.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @return A String with the content type.
     */
    protected abstract String getContentType(RunData data);

    /**
     * Set the content type.  This method should be overidden to
     * actually set the real content-type header of the output.
     *
     * @param data Turbine information.
     * @return A String with the content type.
     */
    protected String getContentType(PipelineData pipelineData)
    {
        RunData data = (RunData) getRunData(pipelineData);
        return getContentType(data);
    }


    /**
     * Actually output the dynamic content.  The OutputStream can be
     * accessed like this: <pre>OutputStream out =
     * data.getResponse().getOutputStream();</pre>.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected abstract void doOutput(RunData data)
            throws Exception;

    /**
     * Actually output the dynamic content.  The OutputStream can be
     * accessed like this: <pre>OutputStream out =
     * data.getResponse().getOutputStream();</pre>.
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected void doOutput(PipelineData pipelineData)
            throws Exception
    {
        RunData data = (RunData) getRunData(pipelineData);
        doOutput(data);
    }


    /**
     * The layout must be set to null.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @return A null String.
     */
    public final String getLayout(RunData data)
    {
        return null;
    }

    /**
     * The layout must be set to null.
     *
     * @param data Turbine information.
     * @return A null String.
     */
    public final String getLayout(PipelineData pipelineData)
    {
        return null;
    }



}
