package org.apache.turbine.modules.actions;


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


// Turbine Modules

import org.apache.turbine.modules.Action;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;

/**
 * This is a Default Action module that doesn't do much.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class DefaultAction extends Action
{
    /**
     * Execute the action.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @throws Exception a generic exception.
     */
    public void doPerform(RunData data)
            throws Exception
    {
        data.setMessage(data.getScreen() + " has been executed!");
    }

    /**
     * Execute the action.
     *
     * @param data Turbine information.
     * @throws Exception a generic exception.
     */
    public void doPerform(PipelineData pipelineData)
            throws Exception
    {
        RunData data = (RunData) getRunData(pipelineData);
        doPerform(data);
    }

}
