package org.apache.turbine.modules.navigations;


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


import org.apache.ecs.ConcreteElement;

import org.apache.turbine.modules.Navigation;
import org.apache.turbine.pipeline.PipelineData;

import org.apache.turbine.util.RunData;

/**
 * Base Template Navigation.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class TemplateNavigation
        extends Navigation
{
    /**
     * WebMacro Navigations extending this class should overide this
     * method to perform any particular business logic and add
     * information to the context.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @throws Exception a generic exception.
     */
    protected abstract void doBuildTemplate(RunData data)
            throws Exception;
    
    /**
     * WebMacro Navigations extending this class should overide this
     * method to perform any particular business logic and add
     * information to the context.
     *
     * @param data Turbine information.
     * @throws Exception a generic exception.
     */
    protected void doBuildTemplate(PipelineData pipelineData)
            throws Exception
    {
	    RunData data = (RunData) getRunData(pipelineData);
	    doBuildTemplate(data);
    }


    /**
     * This Builds the WebMacro/FreeMarker/etc template.
     * @deprecated Use PipelineData version
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @throws Exception a generic exception.
     */
    public abstract ConcreteElement buildTemplate(RunData data)
            throws Exception;

    /**
     * This Builds the WebMacro/FreeMarker/etc template.
     * Should revert to abstract when RunData goes. 
     * @param pipelineData Turbine information.
     * @return A ConcreteElement.
     * @throws Exception a generic exception.
     */
    public ConcreteElement buildTemplate(PipelineData pipelineData)
    throws Exception
	{
	    RunData data = (RunData) getRunData(pipelineData);
	    return buildTemplate(data);
	}    

    /**
     * Calls doBuildTemplate() and then buildTemplate().
     *
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @throws Exception a generic exception.
     */
    protected ConcreteElement doBuild(RunData data)
            throws Exception
    {
        doBuildTemplate(data);
        return buildTemplate(data);
    }

    /**
     * Calls doBuildTemplate() and then buildTemplate().
     *
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @throws Exception a generic exception.
     */
    protected ConcreteElement doBuild(PipelineData pipelineData)
            throws Exception
    {
	    RunData data = (RunData) getRunData(pipelineData);
        return doBuild(data);
    }
    
}
