package org.apache.turbine.services.pull;

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
 * Tools that go into the Toolbox should implement this interface.
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 */
public interface ApplicationTool
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
     * session happens to the be login action.
     * <p>
     * If your session tool depends on having a <code>User</code> object, you
     * should look at implementing the {@link RunDataApplicationTool} interface
     * instead.
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
     */
    void refresh();
}
