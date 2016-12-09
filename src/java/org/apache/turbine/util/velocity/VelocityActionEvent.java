package org.apache.turbine.util.velocity;


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


import org.apache.fulcrum.parser.ParameterParser;
import org.apache.turbine.Turbine;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.modules.ActionEvent;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.velocity.VelocityService;
import org.apache.velocity.context.Context;

/**
 * If you are using VelocitySite stuff, then your Action's should
 * extend this class instead of extending the ActionEvent class.  The
 * difference between this class and the ActionEvent class is that
 * this class will first attempt to execute one of your doMethod's
 * with a constructor like this:
 *
 * <p><code>doEvent(RunData data, Context context)</code></p>
 *
 * <p>It gets the context from the TemplateInfo.getTemplateContext()
 * method. If it can't find a method like that, then it will try to
 * execute the method without the Context in it.</p>
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class VelocityActionEvent extends ActionEvent
{
    /** Injected velocity service */
    @TurbineService
    protected VelocityService velocity;

    /** Indicates whether or not this module has been initialized. */
    protected boolean initialized = false;

    /**
     * Provides a means of initializing the module.
     *
     * @throws Exception a generic exception.
     */
    protected abstract void initialize()
        throws Exception;

    /**
     * This overrides the default Action.doPerform() to execute the
     * doEvent() method.  If that fails, then it will execute the
     * doPerform() method instead.
     *
     * @param pipelineData A Turbine RunData object.
     * @throws Exception a generic exception.
     */
    @Override
    public void doPerform(PipelineData pipelineData)
            throws Exception
    {
        if (!initialized)
        {
            initialize();
        }

        ParameterParser pp = pipelineData.get(Turbine.class, ParameterParser.class);
        Context context = velocity.getContext(pipelineData);
        executeEvents(pp, new Class<?>[]{ PipelineData.class, Context.class },
                new Object[]{ pipelineData, context });
    }
}
