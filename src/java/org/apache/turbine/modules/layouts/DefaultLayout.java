package org.apache.turbine.modules.layouts;

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
import org.apache.ecs.HtmlColor;

import org.apache.ecs.html.Font;
import org.apache.ecs.html.P;

import org.apache.turbine.modules.Layout;
import org.apache.turbine.modules.NavigationLoader;
import org.apache.turbine.modules.ScreenLoader;

import org.apache.turbine.util.RunData;

/**
 * This is an example Layout module that is executed by default.
 *
 * @version $Id$
 * @deprecated The use of ECS for the view is deprecated.
 *             Use a templating solution.
 */
public class DefaultLayout extends Layout
{
    /**
     * Build the layout.
     *
     * <p><em>NOTE: Unless otherwise specified, the page background
     * defaults to 'white'</em></p>
     *
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    public void doBuild(RunData data) throws Exception
    {
        // Execute the Top Navigation portion for this Layout.
        ConcreteElement topNav = NavigationLoader.getInstance()
                .eval(data, "DefaultTopNavigation");

        if (topNav != null)
        {
            data.getPage().getBody().addElement(topNav);
        }

        // If an Action has defined a message, attempt to display it here.
        if (data.getMessage() != null)
        {
            data.getPage().getBody().addElement(new P())
                    .addElement(new Font().setColor(HtmlColor.red)
                    .addElement(data.getMessageAsHTML()));
        }

        // Now execute the Screen portion of the page.
        ConcreteElement screen = ScreenLoader.getInstance()
                .eval(data, data.getScreen());

        if (screen != null)
        {
            data.getPage().getBody().addElement(screen);
        }

        // The screen should have attempted to set a Title for itself,
        // otherwise, a default title is set.
        data.getPage().getTitle().addElement(data.getTitle());

        // The screen should have attempted to set a Body bgcolor for
        // itself, otherwise, a default body bgcolor is set.
        data.getPage().getBody().setBgColor(HtmlColor.white);

        // Execute the Bottom Navigation portion for this Layout.
        ConcreteElement bottomNav = NavigationLoader.getInstance().eval(data,
                "DefaultBottomNavigation");

        if (bottomNav != null)
        {
            data.getPage().getBody().addElement(bottomNav);
        }
    }
}
