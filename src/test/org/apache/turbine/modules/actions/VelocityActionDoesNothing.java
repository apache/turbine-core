package org.apache.turbine.modules.actions;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import junit.framework.Assert;

import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * This action is used in testing the ExecutePageValve by the ExecutePageValveTest.
 * 
 * @author     <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 */
public class VelocityActionDoesNothing extends VelocityAction
{
    private static Log log = LogFactory.getLog(VelocityActionDoesNothing.class);
    public static int numberOfCalls;
    public static int runDataCalls;
    public static int pipelineDataCalls;
    /**
     *  Default action is throw an exception.
     *
     * @param  data           Current RunData information
     * @param  context        Context to populate
     * @exception  Exception  Thrown on error
     */
    public void doPerform(RunData data, Context context) throws Exception
    {
        log.debug("Calling doPerform");
		VelocityActionDoesNothing.numberOfCalls++;
		VelocityActionDoesNothing.runDataCalls++;
    }
    
    /**
     *  Default action is throw an exception.
     *
     * @param  data           Current RunData information
     * @param  context        Context to populate
     * @exception  Exception  Thrown on error
     */
    public void doPerform(PipelineData pipelineData, Context context) throws Exception
    {
        log.debug("Calling doPerform(PipelineData)");
		VelocityActionDoesNothing.numberOfCalls++;
        RunData rd = (RunData)pipelineData;
		Assert.assertNotNull("RunData object was Null.", rd);
		VelocityActionDoesNothing.pipelineDataCalls++;
    }   

}
