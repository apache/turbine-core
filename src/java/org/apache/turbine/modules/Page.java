package org.apache.turbine.modules;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import org.apache.turbine.util.RunData;

/**
 * This is the base class that defines what a Page module is.
 *
 * @version $Id$
 */
public abstract class Page
    extends Assembler
{
    /**
     * A subclass must override this method to build itself.
     * Subclasses override this method to store the page in RunData or
     * to write the page to the output stream referenced in RunData.
     *
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    protected abstract void doBuild(RunData data)
        throws Exception;

    /**
     * Subclasses can override this method to add additional
     * functionality.  This method is protected to force clients to
     * use PageLoader to build a Page.
     *
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    protected void build(RunData data)
        throws Exception
    {
        doBuild(data);
    }
}
