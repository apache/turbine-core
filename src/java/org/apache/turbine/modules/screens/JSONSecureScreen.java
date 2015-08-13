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

import org.apache.turbine.pipeline.PipelineData;

/**
 * An extension to JSONScreen that performs a Security Check before invoking
 * doBuildTemplate().  You should extend this class and add the specific
 * security check needed.  If you have a number of screens that need to perform
 * the same check, you could make a base screen by extending this class and
 * implementing the isAuthorized().  Then each screen that needs to perform the
 * same check could extend your base screen.
 *
 * <p>Typically you would extend this class and override the doOutput() method
 * to use TurbineJsonRpc to register the POJOs that will provide the functions
 * you are making available via JSON-RPC.  Use JSONScreen if you <p>do not</b>
 * need the user to be logged in prior to executing the functions you provide.
 *
 * <p>Here is an example from a superclass:
 * <code>
 * public void doOutput(PipelineData data) throws Exception
 * {
 *     User user = data.getUser();
 *
 *     MySecureJsonFunctions myFunctions
 *             = new MySecureJsonFunctions(user.getName());
 *
 *     // Session specific
 *     TurbineJsonRpc.registerObject(data.getSession(), "myFunctions", myFunctions);
 *
 *     // Global
 *     //TurbineJsonRpc.registerObjectGlobal("testGlobal", testObject);
 *
 *     super.doOutput(data);
 * }
 * </code>
 *
 * <p>The class MyFunctions would be something like:
 * <code>
 * public class MySecureJsonFunctions
 * {
 *     private final String name;
 *
 *     public MySecureJsonFunctions(String name)
 *     {
 *         this.name = name;
 *     }
 *
 *     private String getName(String clientParameter)
 *     {
 *         return "Client " + clientParameter + " says Hello World to " + name;
 *     }
 * }
 * </code>
 *
 * @author <a href="mailto:seade@policypoint.net">Scott Eade</a>
 * @version $Id$
 */
public abstract class JSONSecureScreen extends JSONScreen
{
    /**
     * This method overrides the method in JSONScreen to perform a security
     * check prior to producing the output.
     *
     * @param pipelineData Turbine information.
     * @exception Exception, a generic exception.
     */
    @Override
    protected void doOutput(PipelineData pipelineData) throws Exception
    {
        if (isAuthorized(pipelineData))
        {
            super.doOutput(pipelineData);
        }
    }

    /**
     * Override this method to perform the necessary security checks.
     *
     * @param pipelineData Turbine information.
     * @return <code>true</code> if the user is authorized to access the screen.
     * @exception Exception A generic exception.
     */
    protected abstract boolean isAuthorized(PipelineData pipelineData)
            throws Exception;
}
