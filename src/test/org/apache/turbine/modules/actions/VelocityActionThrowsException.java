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


import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * This action is used in testing the ActionLoader for Velocity templates.  Verifies
 * that exceptions are properly bubbled out.
 *
 * @author     <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @created    October 11, 2002
 */
public class VelocityActionThrowsException extends VelocityAction
{
    private static Log log = LogFactory.getLog(VelocityActionThrowsException.class);
    /**
     *  Default action is throw an exception.
     *
     * @param  data           Current RunData information
     * @param  context        Context to populate
     * @exception  Exception  Thrown on error
     * @deprecated
     */
    @Override
    @Deprecated
    public void doPerform(RunData data, Context context) throws Exception
    {
        log.debug("Calling doPerform(RunData)");
        throw new Exception("From VelocityActionThrowsException.doPerform an Exception is always thrown!");
    }

    /**
     *  Default action is throw an exception.
     *
     * @param  data           Current PipelineData information
     * @param  context        Context to populate
     * @exception  Exception  Thrown on error
     */
    @Override
    public void doPerform(PipelineData data, Context context) throws Exception
    {
        log.debug("Calling doPerform(PipelineData)");
        throw new Exception("From VelocityActionThrowsException.doPerform an Exception is always thrown!");
    }



    /**
     * This action event also throws an exception.
     * @param data
     * @param context
     * @throws Exception
     */
    public void doCauseexception(RunData data, Context context) throws Exception
    {
        log.debug("Calling doCauseexception(RunData)");
        throw new Exception("From Action Event VelocityActionThrowsException.doCauseexception an Exception is always thrown!");
    }

    /**
     * This action event also throws an exception.
     * @param data
     * @param context
     * @throws Exception
     */
    public void doCauseexception(PipelineData data, Context context) throws Exception
    {
        log.debug("Calling doCauseexception(PipelineData)");
        throw new Exception("From Action Event VelocityActionThrowsException.doCauseexception an Exception is always thrown!");
    }


}
