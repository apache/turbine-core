package org.apache.turbine.modules.navigations;


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


import org.apache.ecs.ConcreteElement;
import org.apache.turbine.modules.Navigation;
import org.apache.turbine.pipeline.PipelineData;

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
     * @param pipelineData Turbine information.
     * @throws Exception a generic exception.
     */
    protected abstract void doBuildTemplate(PipelineData pipelineData) throws Exception;

    /**
     * This Builds the WebMacro/FreeMarker/etc template.
     * @param pipelineData Turbine information.
     * @return A ConcreteElement.
     * @throws Exception a generic exception.
     */
    public abstract ConcreteElement buildTemplate(PipelineData pipelineData) throws Exception;

    /**
     * Calls doBuildTemplate() and then buildTemplate().
     *
     * @param pipelineData Turbine information.
     * @return A ConcreteElement.
     * @throws Exception a generic exception.
     */
    @Override
    protected ConcreteElement doBuild(PipelineData pipelineData)
            throws Exception
    {
        doBuildTemplate(pipelineData);
        return buildTemplate(pipelineData);
    }
}
