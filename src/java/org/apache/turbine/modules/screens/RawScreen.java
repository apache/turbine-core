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

import org.apache.turbine.modules.Screen;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;

/**
 * Base class for writing Screens that output binary data.  This class
 * is provided as a helper class for those who want to write Screens
 * that output raw binary data.  For example, it may be extended into
 * a Screen that outputs a SVG file or a SWF (Flash Player format)
 * movie.  The only thing one has to do is to implement the two
 * methods <code>getContentType(PipelineData data)</code> and
 * <code>doOutput(PipelineData data)</code> (see below).
 *
 * <p> You might want to take a look at the ImageServer screen class
 * contained in the TDK.<br>
 *
 * @author <a href="mailto:rkoenig@chez.com">Regis Koenig</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class RawScreen implements Screen
{
    /**
     * Build the Screen.  This method actually makes a call to the
     * doOutput() method in order to generate the Screen content.
     *
     * @param pipelineData Turbine information.
     * @return A ConcreteElement.
     * @throws Exception a generic exception.
     */
    @Override
    public final String doBuild(PipelineData pipelineData)
            throws Exception
    {
        RunData data = getRunData(pipelineData);
        data.getResponse().setContentType(getContentType(pipelineData));
        data.declareDirectResponse();
        doOutput(pipelineData);
        return null;
    }

    /**
     * Set the content type.  This method should be overridden to
     * actually set the real content-type header of the output.
     *
     * @param pipelineData Turbine information.
     * @return A String with the content type.
     */
    protected abstract String getContentType(PipelineData pipelineData);

    /**
     * Actually output the dynamic content.  The OutputStream can be
     * accessed like this: <pre>OutputStream out =
     * data.getResponse().getOutputStream();</pre>.
     *
     * @param pipelineData Turbine information.
     * @throws Exception a generic exception.
     */
    protected abstract void doOutput(PipelineData pipelineData) throws Exception;

    /**
     * The layout must be set to null.
     *
     * @param pipelineData Turbine information.
     * @return A null String.
     */
    @Override
    public final String getLayout(PipelineData pipelineData)
    {
        return null;
    }
}
