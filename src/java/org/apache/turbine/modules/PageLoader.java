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
 * The purpose of this class is to allow one to load and execute Page
 * modules.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class PageLoader
    extends GenericLoader<Page>
    implements Loader<Page>
{
    /** The single instance of this class. */
    private static PageLoader instance = new PageLoader();

    /**
     * These ctor's are private to force clients to use getInstance()
     * to access this class.
     */
    private PageLoader()
    {
        super();
    }

    /**
     * Attempts to load and execute the external page.
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @param name Name of object that will execute the page.
     * @exception Exception a generic exception.
     */
    @Deprecated
    @Override
    public void exec(RunData data, String name)
            throws Exception
    {
        // Execute page
        getAssembler(name).build(data);
    }

    /**
     * Attempts to load and execute the external page.
     *
     * @param data Turbine information.
     * @param name Name of object that will execute the page.
     * @exception Exception a generic exception.
     */
    @Override
    public void exec(PipelineData pipelineData, String name)
            throws Exception
    {
        // Execute page
        getAssembler(name).build(pipelineData);
    }



    /**
     * Pulls out an instance of the object by name.  Name is just the
     * single name of the object. This is equal to getInstance but
     * returns an Assembler object and is needed to fulfil the Loader
     * interface.
     *
     * @param name Name of object instance.
     * @return A Screen with the specified name, or null.
     * @exception Exception a generic exception.
     */
    public Page getAssembler(String name)
        throws Exception
    {
        return getAssembler(Page.NAME, name);
    }

    /**
     * @see org.apache.turbine.modules.Loader#getCacheSize()
     */
    public int getCacheSize()
    {
        return PageLoader.getConfiguredCacheSize();
    }

    /**
     * The method through which this class is accessed.
     *
     * @return The single instance of this class.
     */
    public static PageLoader getInstance()
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
        return Turbine.getConfiguration().getInt(Page.CACHE_SIZE_KEY,
                Page.CACHE_SIZE_DEFAULT);
    }
}
