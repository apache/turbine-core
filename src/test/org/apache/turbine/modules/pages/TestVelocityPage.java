package org.apache.turbine.modules.pages;

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

import junit.framework.Assert;

import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;


/**
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 */
public class TestVelocityPage extends VelocityPage{

    public static int numberOfCalls = 0;

    public void doBuild(PipelineData pipelineData) throws Exception
    {
        numberOfCalls++;
        super.doBuild(pipelineData);
        RunData data = getRunData(pipelineData);
        Assert.assertNotNull("RunData object is null.", data);
    }

    public void doBuild(RunData runData) throws Exception
    {
        numberOfCalls++;
        super.doBuild(runData);
        Assert.assertNotNull("RunData object is null.", runData);
    }

    public void doBuildBeforeAction(PipelineData pipelineData) throws Exception
    {
        numberOfCalls++;
        RunData data = getRunData(pipelineData);
        Assert.assertNotNull("RunData object is null.", data);
    }

    public void doPostBuild(PipelineData pipelineData) throws Exception
    {
        numberOfCalls++;
        RunData data = getRunData(pipelineData);
        Assert.assertNotNull("RunData object is null.", data);
    }

    public void doBuildBeforeAction(RunData data) throws Exception
    {
        numberOfCalls++;
        Assert.assertNotNull("RunData object is null.", data);
    }

    public void doPostBuild(RunData data) throws Exception
    {
        numberOfCalls++;
        Assert.assertNotNull("RunData object is null.", data);
    }

    public void doBuildAfterAction(RunData data) throws Exception
    {
        numberOfCalls++;
        Assert.assertNotNull("RunData object is null.", data);
    }

    public void doBuildAfterAction(PipelineData pipelineData) throws Exception
    {
        numberOfCalls++;
        RunData data = getRunData(pipelineData);
        Assert.assertNotNull("RunData object is null.", data);
    }
}
