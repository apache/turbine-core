package org.apache.turbine.modules.pages;

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

import org.apache.turbine.services.jsp.TurbineJsp;
import org.apache.turbine.util.RunData;

/**
 * Extends TemplatePage to add some convenience objects to the request.
 *
 * @version $Id$
 */
public class JspPage
    extends TemplatePage
{
    /**
     * Stuffs some useful objects into the request so that
     * it is available to the Action module and the Screen module
     */
    protected void doBuildBeforeAction(RunData data)
        throws Exception
    {
        TurbineJsp.addDefaultObjects(data);

        try
        {
            //We try to set the buffer size from defaults
            data.getResponse().setBufferSize(TurbineJsp.getDefaultBufferSize());
        }
        catch (IllegalStateException ise)
        {
            // If the response was already commited, we die silently
            // No logger here?
        }
    }
}
