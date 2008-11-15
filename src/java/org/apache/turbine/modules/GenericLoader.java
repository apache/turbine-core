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

import java.util.Hashtable;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;

/**
 * This is the base class for the loaders. It contains code that is
 * used across all of the loaders. It also specifies the interface
 * that is required to be called a Loader.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class GenericLoader
    extends Hashtable
{
    /** @serial This can be serialized */
    private boolean reload = false;

    /** @serial This can be serialized */
    private boolean isCaching = true;

    /** Base packages path for Turbine */
    private static final String TURBINE_PACKAGE = "org.apache.turbine.modules";

    /**
     * Basic constructor for creating a loader.
     */
    public GenericLoader()
    {
        super();
        isCaching = Turbine.getConfiguration()
            .getBoolean(TurbineConstants.MODULE_CACHE_KEY,
                        TurbineConstants.MODULE_CACHE_DEFAULT);
    }

    /**
     * Basic constructor for creating a loader.
     */
    public GenericLoader(int i)
    {
        super(i);
        isCaching = Turbine.getConfiguration()
            .getBoolean(TurbineConstants.MODULE_CACHE_KEY,
                        TurbineConstants.MODULE_CACHE_DEFAULT);
    }

    /**
     * If set to true, then cache the Loader objects.
     *
     * @return True if the Loader objects are being cached.
     */
    public boolean cache()
    {
        return this.isCaching;
    }

    /**
     * Attempts to load and execute the external action that has been
     * set.
     * Should revert to abstract when RunData has gone.
     * @exception Exception a generic exception.
     */
    public void exec(PipelineData pipelineData, String name)
            throws Exception
    {
        RunData data = getRunData(pipelineData);
        exec(data, name);
    }


    /**
     * Attempts to load and execute the external action that has been
     * set.
     * @deprecated Use of this method should be avoided. Use
     * <code>exec(PipelineData data, String name)</code> instead.
     * @exception Exception a generic exception.
     */
    public abstract void exec(RunData data, String name)
    	throws Exception;

    /**
     * Commented out.
     * This method should return the complete classpath + name.
     *
     * @param name
     * @return
     *
     public abstract String getClassName(String name);
     */

    /**
     * Returns whether or not this external action is reload itself.
     * This is in cases where the Next button would be clicked, but
     * since we are checking for that, we would go into an endless
     * loop.
     *
     * @return True if the action is reload.
     */
    public boolean reload()
    {
        return this.reload;
    }

    /**
     * Sets whether or not this external action is reload itself.
     * This is in cases where the Next button would be clicked, but
     * since we are checking for that, we would go into an endless
     * loop.
     *
     * @param reload True if the action must be marked as reload.
     * @return Itself.
     */
    public GenericLoader setReload(boolean reload)
    {
        this.reload = reload;
        return this;
    }

    /**
     * Gets the base package where Turbine should find its default
     * modules.
     *
     * @return A String with the base package name.
     */
    public static String getBasePackage()
    {
        return TURBINE_PACKAGE;
    }

    private RunData getRunData(PipelineData pipelineData)
    {
        if(!(pipelineData instanceof RunData)){
            throw new RuntimeException("Can't cast to rundata from pipeline data.");
        }
        return (RunData)pipelineData;
    }

}
