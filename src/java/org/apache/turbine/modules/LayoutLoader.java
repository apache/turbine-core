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

import org.apache.turbine.Turbine;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;

/**
 * The purpose of this class is to allow one to load and execute
 * Layout modules.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class LayoutLoader
    extends GenericLoader<Layout>
    implements Loader<Layout>
{
    /** The single instance of this class. */
    private static LayoutLoader instance = new LayoutLoader();

    /**
     * These ctor's are private to force clients to use getInstance()
     * to access this class.
     */
    private LayoutLoader()
    {
        super();
    }

    /**
     * Attempts to load and execute the external layout.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @param name Name of object that will execute the layout.
     * @exception Exception a generic exception.
     */
    @Deprecated
    @Override
    public void exec(RunData data, String name)
            throws Exception
    {
        // Execute layout
        getAssembler(name).build(data);
    }

    /**
     * Attempts to load and execute the external layout.
     *
     * @param pipelineData Turbine information.
     * @param name Name of object that will execute the layout.
     * @exception Exception a generic exception.
     */
    @Override
    public void exec(PipelineData pipelineData, String name)
    		throws Exception
    {
        getAssembler(name).build(pipelineData);
    }

    /**
     * Pulls out an instance of the object by name.  Name is just the
     * single name of the object.
     *
     * @param name Name of object instance.
     * @return A Layout with the specified name, or null.
     * @exception Exception a generic exception.
     */
    public Layout getAssembler(String name)
        throws Exception
    {
        return getAssembler(Layout.class, name);
    }

    /**
     * @see org.apache.turbine.modules.Loader#getCacheSize()
     */
    public int getCacheSize()
    {
        return LayoutLoader.getConfiguredCacheSize();
    }

    /**
     * The method through which this class is accessed.
     *
     * @return The single instance of this class.
     */
    public static LayoutLoader getInstance()
    {
        return instance;
    }

    /**
     * Helper method to get the configured cache size for this module
     *
     * @return the configure cache size
     */
    private static int getConfiguredCacheSize()
    {
        return Turbine.getConfiguration().getInt(Layout.CACHE_SIZE_KEY,
                Layout.CACHE_SIZE_DEFAULT);
    }
}
