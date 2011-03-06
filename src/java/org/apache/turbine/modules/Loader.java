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

/**
 * A common interface for Screen, Layout and Navigation Loader
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public interface Loader<T extends Assembler>
{
    /**
     * Get an Assembler.
     *
     * @param name name of the requested Assembler
     * @return an Assembler
     * @throws Exception generic exception
     */
    T getAssembler(String name) throws Exception;

    /**
     * Get the size of a possibly configured cache
     *
     * @return the size of the cache in bytes
     */
    int getCacheSize();
}
