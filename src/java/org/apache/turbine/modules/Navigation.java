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
 * This is the interface that defines what a Navigation module is.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
@FunctionalInterface
public interface Navigation extends Assembler
{
    /** Prefix for navigation related classes and templates */
    String PREFIX = "navigations";

    /** Property for the size of the navigation cache if caching is on */
    String CACHE_SIZE_KEY = "navigation.cache.size";

    /** The default size for the navigation cache */
    int CACHE_SIZE_DEFAULT = 10;

    /** Represents Navigation Objects */
    String NAME = "navigation";

    /**
     * A subclass must implement this method to build itself.
     * Subclasses override this method to store the navigation in
     * RunData or to write the navigation to the output stream
     * referenced in RunData.
     *
     * @param pipelineData Turbine information.
     * @return the content of the navigation module
     * @throws Exception a generic exception.
     */
    String doBuild(PipelineData pipelineData) throws Exception;

    /**
     * Subclasses can override this method to add additional
     * functionality.
     *
     * @param pipelineData Turbine information.
     * @return the content of the navigation module
     * @throws Exception a generic exception.
     */
    default String build(PipelineData pipelineData)
        throws Exception
    {
        return doBuild(pipelineData);
    }
}
