/*
 * Created on 15-Jul-2004
 *
 */
package org.apache.turbine.modules.pages;

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
        RunData data = (RunData) getRunData(pipelineData);
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
        RunData data = (RunData) getRunData(pipelineData);
        Assert.assertNotNull("RunData object is null.", data);
    }
    
    public void doPostBuild(PipelineData pipelineData) throws Exception
    {
        numberOfCalls++;
        RunData data = (RunData) getRunData(pipelineData);
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
        RunData data = (RunData) getRunData(pipelineData);
        Assert.assertNotNull("RunData object is null.", data);
    }
    
    
}
