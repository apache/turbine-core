package org.apache.turbine.modules.actions;

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

import org.apache.turbine.modules.screens.TemplateScreen;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.velocity.VelocityActionEvent;
import org.apache.velocity.context.Context;

/**
 * This class provides a convenience methods for Velocity Actions
 * to use. Since this class is abstract, it should only be extended
 * and not used directly.
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class VelocityAction extends VelocityActionEvent
{
    /**
     * You SHOULD NOT override this method and implement it in your
     * action.
     *
     * @param data Turbine information.
     * @throws Exception a generic exception.
     */
    public void doPerform(RunData data)
            throws Exception
    {
        doPerform(data, getContext(data));
    }

    /**
     * You SHOULD override this method and implement it in your
     * action.
     *
     * @param data Turbine information.
     * @param context Context for web pages.
     * @throws Exception a generic exception.
     */
    public abstract void doPerform(RunData data,
                                   Context context)
            throws Exception;

    /**
     * Sets up the context and then calls super.perform(); thus,
     * subclasses don't have to worry about getting a context
     * themselves!
     *
     * @param data Turbine information.
     * @throws Exception a generic exception.
     */
    protected void perform(RunData data)
            throws Exception
    {
        super.perform(data);
    }

    /**
     * This method is used when you want to short circuit an Action
     * and change the template that will be executed next.
     *
     * @param data Turbine information.
     * @param template The template that will be executed next.
     */
    public void setTemplate(RunData data,
                            String template)
    {
        TemplateScreen.setTemplate(data, template);
    }

    /**
     * Return the Context needed by Velocity.
     *
     * @param data Turbine information.
     * @return Context, a context for web pages.
     */
    protected Context getContext(RunData data)
    {
        return TurbineVelocity.getContext(data);
    }
}
