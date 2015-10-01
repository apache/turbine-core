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


import org.apache.ecs.ConcreteElement;
import org.apache.turbine.Turbine;
import org.apache.turbine.pipeline.PipelineData;

/**
 * The purpose of this class is to allow one to load and execute
 * Screen modules.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class ScreenLoader
    extends GenericLoader<Screen>
    implements Loader<Screen>
{
    /** The single instance of this class. */
    private static ScreenLoader instance = new ScreenLoader();

    /**
     * These ctor's are private to force clients to use getInstance()
     * to access this class.
     */
    private ScreenLoader()
    {
        super();
    }

    /**
     * Attempts to load and execute the external Screen. This is used
     * when you want to execute a Screen which returns its output via
     * a MultiPartElement instead of out the data.getPage() value.
     * This allows you to easily chain the execution of Screen modules
     * together.
     *
     * @param pipelineData Turbine information.
     * @param name Name of object that will execute the screen.
     * @return the output of the screen module
     * @exception Exception a generic exception.
     */
    public ConcreteElement eval(PipelineData pipelineData, String name)
            throws Exception
    {
        // Execute screen
        return getAssembler(name).build(pipelineData);
    }

    /**
     * Attempts to load and execute the Screen. This is used when you
     * want to execute a Screen which returns its output via the
     * data.getPage() object.
     *
     * @param pipelineData Turbine information.
     * @param name Name of object that will execute the screen.
     * @exception Exception a generic exception.
     */
    @Override
    public void exec(PipelineData pipelineData, String name)
	throws Exception
	{
        this.eval(pipelineData, name);
	}

    /**
     * Pulls out an instance of the object by name.  Name is just the
     * single name of the object.
     *
     * @param name Name of object instance.
     * @return A Screen with the specified name, or null.
     * @exception Exception a generic exception.
     */
    @Override
    public Screen getAssembler(String name)
        throws Exception
    {
        return getAssembler(Screen.class, name);
    }

    /**
     * @see org.apache.turbine.modules.Loader#getCacheSize()
     */
    @Override
    public int getCacheSize()
    {
        return ScreenLoader.getConfiguredCacheSize();
    }

    /**
     * The method through which this class is accessed.
     *
     * @return The single instance of this class.
     */
    public static ScreenLoader getInstance()
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
        return Turbine.getConfiguration().getInt(Screen.CACHE_SIZE_KEY,
                Screen.CACHE_SIZE_DEFAULT);
    }
}
