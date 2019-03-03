package org.apache.turbine.modules;

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
 * Generic Action class.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class Action implements Assembler
{
    /** Prefix for action related classes and templates */
    public static final String PREFIX = "actions";

    /** Property for the size of the module cache if caching is on */
    public static final String CACHE_SIZE_KEY = "action.cache.size";

    /** The default size for the action cache */
    public static final int CACHE_SIZE_DEFAULT = 20;

    /** Represents Action Objects */
    public static final String NAME = "action";

    /**
     * @see org.apache.turbine.modules.Assembler#getPrefix()
     */
    @Override
    public String getPrefix()
    {
        return PREFIX;
    }

    /**
     * A subclass must override this method to perform itself.  The
     * Action can also set the screen that is associated with {@link PipelineData}.
     *
     * @param pipelineData Turbine information.
     * @throws Exception a generic exception.
     */
    public abstract void doPerform(PipelineData pipelineData) throws Exception;

    /**
     * Subclasses can override this method to add additional
     * functionality.  This method is protected to force clients to
     * use ActionLoader to perform an Action.
     *
     * @param pipelineData Turbine information.
     * @throws Exception a generic exception.
     */
    protected void perform(PipelineData pipelineData) throws Exception
    {
        doPerform(pipelineData);
    }
}
