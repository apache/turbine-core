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

import org.apache.turbine.util.RunData;

/**
 * Generic Action class.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 */
public abstract class Action extends Assembler
{
    /**
     * A subclass must override this method to perform itself.  The
     * Action can also set the screen that is associated with RunData.
     *
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    public abstract void doPerform(RunData data) throws Exception;

    /**
     * Subclasses can override this method to add additional
     * functionality.  This method is protected to force clients to
     * use ActionLoader to perform an Action.
     *
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    protected void perform(RunData data) throws Exception
    {
        doPerform(data);
    }
}
