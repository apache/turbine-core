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

import org.apache.turbine.services.Service;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * The Pull Service manages the creation of application
 * tools that are available to all templates in a
 * Turbine application. By using the Pull Service you
 * can avoid having to make Screens to populate a
 * context for use in a particular template. The Pull
 * Service creates a set of tools, as specified in
 * the TR.props file.
 *
 * These tools can have global scope, request scope,
 * authorized or session scope (i.e. stored in user temp hashmap)
 * or persistent scope (i.e. stored in user perm hashmap)
 *
 * The standard way of referencing these global
 * tools is through the toolbox handle. This handle
 * is typically $toolbox, but can be specified in the
 * TR.props file.
 *
 * So, for example, if you had a UI Manager tool
 * which created a set of UI attributes from a
 * properties file, and one of the properties
 * was 'bgcolor', then you could access this
 * UI attribute with $ui.bgcolor. The identifier
 * that is given to the tool, in this case 'ui', can
 * be specified as well.
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public interface PullService
        extends Service
{
    /** The key under which this service is stored in TurbineServices. */
    String SERVICE_NAME = "PullService";

    /** Property Key for the global tools */
    String GLOBAL_TOOL = "tool.global";

    /** Property Key for the request tools */
    String REQUEST_TOOL = "tool.request";

    /** Property Key for the session tools */
    String SESSION_TOOL = "tool.session";

    /** Property Key for the authorized tools */
    String AUTHORIZED_TOOL = "tool.authorized";

    /** Property Key for the persistent tools */
    String PERSISTENT_TOOL = "tool.persistent";

    /** Property tag for application tool resources directory */
    String TOOL_RESOURCES_DIR_KEY = "tools.resources.dir";

    /**
     * Default value for the application tool resources. This is relative
     * to the webapp root
     */
    String TOOL_RESOURCES_DIR_DEFAULT = "resources";

    /**
     * Property tag for per request tool refreshing (for obvious reasons
     * has no effect for per-request tools)
     */
    String TOOLS_PER_REQUEST_REFRESH_KEY = "tools.per.request.refresh";

    /** Default value for per request tool refreshing */
    boolean TOOLS_PER_REQUEST_REFRESH_DEFAULT = false;

    /** prefix for key used in the session to store session scope pull tools */
    String SESSION_TOOLS_ATTRIBUTE_PREFIX = "turbine.sessiontools.";

    /**
     * Get the context containing global tools that will be
     * use as part of the Turbine Pull Model.
     *
     * @return A Context object which contains the
     *         Global Tool instances.
     */
    Context getGlobalContext();

    /**
     * Populate the given context with all request, session, authorized
     * and persistent scope tools (it is assumed that the context
     * already wraps the global context, and thus already contains
     * the global tools).
     *
     * @param context a Velocity Context to populate
     * @param data a RunData object for request specific data
     */
    void populateContext(Context context, RunData data);

    /**
     * Return the absolute path of the resources directory
     * used by application tools.
     *
     * @return A directory path in the file system or null.
     */
    String getAbsolutePathToResourcesDirectory();

    /**
     * Return the resources directory. This is relative
     * to the webapp context.
     *
     * @return A directory path to the resources directory relative to the webapp root or null.
     */
    String getResourcesDirectory();

    /**
     * Refresh the global tools .
     * @deprecated No longer needed as Pull and Velocity Service are now more separate.
     */
    void refreshGlobalTools();

    /**
     * Shoud we refresh the tools
     * on each request. For development purposes.
     *
     * @return true if we should refresh the tools on every request.
     * @deprecated No longer needed as Pull and Velocity Service are now more separate.
     */
    boolean refreshToolsPerRequest();

    /**
     * Release tool instances from the given context to the
     * object pool
     *
     * @param context a Velocity Context to release tools from
     */
    void releaseTools(Context context);
}
