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

/**
 * The purpose of this class is to allow one to load and execute
 * Navigation modules.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public final class NavigationLoader
    extends GenericLoader<Navigation>
{
    /** The single instance of this class. */
    private static NavigationLoader instance = new NavigationLoader();

    /**
     * These ctor's are private to force clients to use getInstance()
     * to access this class.
     */
    private NavigationLoader()
    {
        super(Navigation.class,
                () -> Turbine.getConfiguration().getInt(Navigation.CACHE_SIZE_KEY,
                        Navigation.CACHE_SIZE_DEFAULT));
    }

    /**
     * Attempts to load and execute the external Navigation. This is
     * used when you want to execute a Navigation which returns its
     * output via a MultiPartElement instead of out the data.getPage()
     * value.  This allows you to easily chain the execution of
     * Navigation modules together.
     *
     * @param pipelineData Turbine information.
     * @param name Name of object that will execute the navigation.
     * @return the navigation module output
     * @throws Exception a generic exception.
     */
    public String eval(PipelineData pipelineData, String name)
            throws Exception
    {
        // Execute Navigation
        return getAssembler(name).build(pipelineData);
    }

    /**
     * Attempts to load and execute the external Navigation.
     *
     * @param pipelineData Turbine information.
     * @param name Name of object instance.
     * @throws Exception a generic exception.
     */
    @Override
    public void exec(PipelineData pipelineData, String name)
    		throws Exception
    {
        this.eval(pipelineData, name);
    }

    /**
     * The method through which this class is accessed.
     *
     * @return The single instance of this class.
     */
    public static NavigationLoader getInstance()
    {
        return instance;
    }
}
