package org.apache.turbine.services.pull;

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

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;

import org.apache.velocity.context.Context;

/**
 * This is a Facade class for PullService.
 *
 * This class provides static methods that call related methods of the
 * implementation of the PullService used by the System, according to
 * the settings in TurbineResources.
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public abstract class TurbinePull
{
    /**
     * Utility method for accessing the service
     * implementation
     *
     * @return a PullService implementation instance
     */
    public static PullService getService()
    {
        return (PullService) TurbineServices
                .getInstance().getService(PullService.SERVICE_NAME);
    }

    /**
     * Get the context containing global tools that will be
     * use as part of the Turbine Pull Model.
     *
     * @return A Context object which contains the
     *         Global Tool instances.
     */
    public static final Context getGlobalContext()
    {
        return getService().getGlobalContext();
    }

    /**
     * Checks whether this service has been registered.  This is
     * required by the TurbineVelocityService so it can determine
     * whether to attempt to place the ToolBox in the context.
     * <p>
     * So users can use Turbine with templates in the traditional
     * manner. If the Pull Service is not listed in
     * <code>TurbineResources.props</code>, or no tools are specified
     * the TurbineVelocityService will behave in its traditional
     * manner.
     */
    public static final boolean isRegistered()
    {
        return TurbineServices.getInstance()
                .isRegistered(PullService.SERVICE_NAME);
    }

    /**
     * Return the absolute path of the resources directory
     * used by application tools.
     *
     * @return A directory path in the file system or null.
     */
    public static final String getAbsolutePathToResourcesDirectory()
    {
        return getService().getAbsolutePathToResourcesDirectory();
    }

    /**
     * Return the resources directory. This is relative
     * to the webapp context.
     *
     * @return A directory path to the resources directory relative to the webapp root or null.
     */
    public static final String getResourcesDirectory()
    {
        return getService().getResourcesDirectory();
    }

    /**
     * Populate the given context with all request, session
     * and persistent scope tools (it is assumed that the context
     * already wraps the global context, and thus already contains
     * the global tools).
     *
     * @param context a Velocity Context to populate
     * @param data a RunData object for request specific data
     */
    public static void populateContext(Context context, RunData data)
    {
        getService().populateContext(context, data);
    }

    /**
     * Refresh the global tools. This is necessary
     * for development work where tools depend
     * on configuration information. The configuration
     * information is typically cached after initialization
     * but during development you might want the tool
     * to refresh itself on each request.
     * <p>
     * If there are objects that don't implement
     * the ApplicationTool interface, then they won't
     * be refreshed.
     * @deprecated No longer needed as Pull and Velocity Service are now more separate.
     */
    public static final void refreshGlobalTools()
    {
        getService().refreshGlobalTools();
    }

    /**
     * Shoud we refresh the tools
     * on each request. For development purposes.
     *
     * @return true if we should refresh the tools on every request.
     * @deprecated No longer needed as Pull and Velocity Service are now more separate.
     */
    public static final boolean refreshToolsPerRequest()
    {
        return getService().refreshToolsPerRequest();
    }

    /**
     * Release tool instances from the given context to the
     * object pool
     *
     * @param context a Velocity Context to release tools from
     */
    public static void releaseTools(Context context)
    {
        getService().releaseTools(context);
    }

    /**
     * Helper method that allows you to easily get a tool
     * from a Context. Essentially, it just does the cast
     * to an Application tool for you.
     *
     * @param context a Velocity Context to get tools from
     * @param name the name of the tool to get
     * @return ApplicationTool null if no tool could be found
     */
    public static ApplicationTool getTool(Context context,
                                          String name)
    {
        try
        {
            return (ApplicationTool) context.get(name);
        }
        catch (Exception e)
        {
        }
        return null;
    }
}
