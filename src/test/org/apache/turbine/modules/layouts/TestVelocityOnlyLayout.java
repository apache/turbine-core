/*
 * Created on 15-Jul-2004
 *
 */
package org.apache.turbine.modules.layouts;

import junit.framework.Assert;

import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;


/**
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 */
public class TestVelocityOnlyLayout extends VelocityOnlyLayout
{
    public static int numberOfCalls = 0;
    
    public void doBuild(PipelineData pipelineData)
    {
        numberOfCalls++;
        RunData data = (RunData) getRunData(pipelineData);
        Assert.assertNotNull("RunData object is null.", data);
    }
    
    public void doBuild(RunData runData)
    {
        numberOfCalls++;
        Assert.assertNotNull("RunData object is null.", runData);
    }
    
    
}
