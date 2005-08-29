package org.apache.turbine.services.pull;

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
 * Tools in the Toolbox that need a Rundata Object on every refresh should
 * implement this interface.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public interface RunDataApplicationTool
{
    /**
     * Initialize the application tool. The data parameter holds a different
     * type depending on how the tool is being instantiated:
     * <ul>
     * <li>For global tools data will be null</li>
     * <li>For request tools data will be of type RunData</li>
     * <li>For session and authorized tools data will be of type User</li>
     * </ul>
     * <p>
     * It is possible that session scope tools will be initialized with a null
     * <code>User</code> object.  This happens when the first request on a
     * session happens to the be login action.  The next request on the session
     * will cause the session tool to be refreshed if
     * <code>tools.per.request.refresh</code> is set to <code>true</code>
     * in <code>TurbineResources.properties</code>.  You will then be able to
     * get a <code>User</code> object from the instance of
     * <code>RunData</object>.
     *
     * @param data initialization data
     */
    void init(Object data);

    /**
     * Refresh the application tool. This is
     * necessary for development work where you
     * probably want the tool to refresh itself
     * if it is using configuration information
     * that is typically cached after initialization
     *
     * @param data The current RunData Object
     */
    void refresh(RunData data);
}
