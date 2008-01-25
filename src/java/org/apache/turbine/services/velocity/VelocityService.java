package org.apache.turbine.services.velocity;


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


import java.io.OutputStream;
import java.io.Writer;

import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.Service;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

import org.apache.velocity.context.Context;

/**
 * Implementations of the VelocityService interface.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public interface VelocityService
        extends Service
{
    /** The Service Name */
    String SERVICE_NAME = "VelocityService";

    /** Key for storing the Context in the RunData object */
    String CONTEXT = "VELOCITY_CONTEXT";

    /** The default extension of Velocity Pages */
    String VELOCITY_EXTENSION = "vm";

    /** The Key for storing the RunData Object in the Context */
    String RUNDATA_KEY = "data";

    /** The Key for storing the PipelineData Object in the Context */
    String PIPELINEDATA_KEY = "pipelineData";

    /** Shall we catch Velocity Errors and report them? */
    String CATCH_ERRORS_KEY = "catch.errors";

    /** Default: Yes */
    boolean CATCH_ERRORS_DEFAULT = true;

    /**
     * Process the request and fill in the template with the values
     * you set in the Context.
     *
     * @param context A Context.
     * @param template A String with the filename of the template.
     * @return The process template as a String.
     * @exception Exception a generic exception.
     */
    String handleRequest(Context context, String template)
            throws Exception;

    /**
     * Process the request and fill in the template with the values
     * you set in the Context.
     *
     * @param context A Context.
     * @param filename A String with the filename of the template.
     * @param out A OutputStream where we will write the process template as
     *        a String.
     * @throws TurbineException Any exception trown while processing will be
     *         wrapped into a TurbineException and rethrown.
     */
    void handleRequest(Context context, String filename, OutputStream out)
            throws TurbineException;

    /**
     * Process the request and fill in the template with the values
     * you set in the Context.
     *
     * @param context A Context.
     * @param filename A String with the filename of the template.
     * @param writer A Writer where we will write the process template as
     *        a String.
     * @throws TurbineException Any exception trown while processing will be
     *         wrapped into a TurbineException and rethrown.
     */
    void handleRequest(Context context, String filename, Writer writer)
            throws TurbineException;

    /**
     * Create an empty WebContext object.
     *
     * @return An empty WebContext object.
     */
    Context getContext();

    /**
     * This method returns a new, empty Context object.
     *
     * @return A WebContext.
     */
    Context getNewContext();

    /**
     * Create a Context from the RunData object.  Adds a pointer to
     * the RunData object to the Context so that RunData is available in
     * the templates.
     *
     * @param data The Turbine RunData object.
     * @return A clone of the Context needed by Velocity.
     */
    Context getContext(RunData data);

    /**
     * Create a Context from the RunData object.  Adds a pointer to
     * the RunData object to the Context so that RunData is available in
     * the templates.
     *
     * @param data The Turbine RunData object.
     * @return A clone of the Context needed by Velocity.
     */
    Context getContext(PipelineData pipelineData);



    /**
     * Performs post-request actions (releases context
     * tools back to the object pool).
     *
     * @param context a Velocity Context
     */
    void requestFinished(Context context);
}
