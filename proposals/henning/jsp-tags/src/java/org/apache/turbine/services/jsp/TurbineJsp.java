package org.apache.turbine.services.jsp;

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

import java.io.OutputStream;
import java.io.Writer;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * This is a simple static accessor to common Jsp tasks such as
 * getting an instance of a context as well as handling a request for
 * processing a template.
 *
 * @version $Id$
 */
public abstract class TurbineJsp
{
    /**
     * Utility method for accessing the service
     * implementation
     *
     * @return a JspService implementation instance
     */
    protected static JspService getService()
    {
        return (JspService)TurbineServices
            .getInstance().getService(JspService.SERVICE_NAME);
    }

    /**
     * This returns a Context that can be used in the Jsp template
     * once you have populated it with information that the template
     * will know about.
     *
     * @param data A Turbine RunData.
     * @return A Context.
     */
    public static Context getContext(RunData data)
    {
        return getService().getContext(data);
    }

    /**
     * This method returns a blank Context object.
     *
     * @return A WebContext.
     */
    public static Context getContext()
    {
        return getService().getContext();
    }

}

