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
 * This is the interface that defines what a Page module is.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
@FunctionalInterface
public interface Page extends Assembler
{
    /** Prefix for page related classes and templates */
    String PREFIX = "pages";

    /** Property for the size of the page cache if caching is on */
    String CACHE_SIZE_KEY = "page.cache.size";

    /** The default size for the page cache */
    int CACHE_SIZE_DEFAULT = 5;

    /** Represents Page Objects */
    String NAME = "page";

    /**
     * A subclass must override this method to build itself.
     * Subclasses override this method to store the page in PipelineData or
     * to write the page to the output stream referenced in PipelineData.
     * @param pipelineData Turbine information.
     * @throws Exception a generic exception.
     */
    void doBuild(PipelineData pipelineData) throws Exception;

    /**
     * Subclasses can override this method to add additional
     * functionality.
     *
     * @param pipelineData Turbine information.
     * @throws Exception a generic exception.
     */
    default void build(PipelineData pipelineData)
    	throws Exception
    {
        doBuild(pipelineData);
    }
}
