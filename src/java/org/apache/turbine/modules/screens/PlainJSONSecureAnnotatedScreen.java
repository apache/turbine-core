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

import java.lang.reflect.Method;

import org.apache.fulcrum.security.model.turbine.TurbineAccessControlList;
import org.apache.turbine.annotation.AnnotationProcessor;
import org.apache.turbine.annotation.AnnotationProcessor.ConditionType;
import org.apache.turbine.annotation.TurbineRequiredRole;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;

public class PlainJSONSecureAnnotatedScreen extends PlainJSONScreen
{
    
    /**
     * This method overrides the method in JSONScreen to perform a security
     * check prior to producing the output.
     *
     * @param pipelineData Turbine information.
     * @throws Exception a generic exception.
     */
    @Override
    public void doOutput(PipelineData pipelineData) throws Exception
    {
        if (isAuthorized(pipelineData))
        {
            super.doOutput(pipelineData);
        }
    }

    /**
     * Use this method to perform the necessary security check with Turbine annotations {@link TurbineRequiredRole} in 
     * a newly overridden {@link #doOutput(PipelineData)} method.
     *
     * @param pipelineData Turbine information.
     * @return <code>true</code> if the user is authorized to access the screen, by default it is required ACL is populated.
     * If {@link TurbineRequiredRole} is not set, it is allowed by default 
     * @throws Exception A generic exception.
     */
    protected boolean isAuthorized(PipelineData pipelineData) throws Exception {
        RunData data = getRunData(pipelineData);
        Method[] methods = getClass().getMethods();
        for (Method m : methods) {
            if (m.getName().equals( "doOutput" )) {
                if ((TurbineAccessControlList)data.getACL() == null) return false;
                return AnnotationProcessor.isAuthorized( m, (TurbineAccessControlList)data.getACL(), ConditionType.ANY );
            }
        }
        return false;
    }
}
