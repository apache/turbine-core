package org.apache.turbine.modules;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A common interface for Screen, Layout and Navigation Loader
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public interface Loader
{
    /**
     * Pulls out an instance of an Assembler Object by name.
     *
     * @param name Name of requested Object.
     * @return An Assembler object or null.
     * @exception Exception a generic exception.
     */
    public Assembler getAssembler(String name)
        throws Exception;
}
