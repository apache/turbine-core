package org.apache.turbine.modules.screens;

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

import org.apache.ecs.ConcreteElement;
import org.apache.turbine.services.jsp.JspService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.apache.turbine.services.jsp.TurbineJsp;

/**
 * Jsp Screen with support for conext. The buildTemplate() assumes the
 * template parameter has been set in the RunData object.  This provides
 * the ability to execute several templates from one Screen.
 *
 * <p>
 *
 * If you need more specific behavior in your application, extend this
 * class and override the doBuildTemplate() method.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:gabrielm@itcsoluciones.com">Gabriel A. Moreno</a>
 * @version $Id$
 */
public class JspScreen extends BaseJspScreen {
    
    /**
     * Jsp Screens extending this class should overide this
     * method to perform any particular business logic and add
     * information to the context.
     *
     * @param data Turbine information.
     * @param context Context for web pages.
     * @exception Exception, a generic exception.
     */
    protected void doBuildTemplate( RunData data,
                                    Context context )
        throws Exception
    {
    }

    /**
     * Needs to be implemented to make TemplateScreen like us.  The
     * actual method that you should override is the one with the
     * context in the parameter list.
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected void doBuildTemplate( RunData data )
        throws Exception
    {
        doBuildTemplate(data, TurbineJsp.getContext(data));
    }

    /**
     * Return the Context needed which can then be used in the Jsp template
     *
     * @param data Turbine information.
     * @return A Context.
     */
    public static Context getContext(RunData data)
    {
        return TurbineJsp.getContext(data);
    }
   
}
