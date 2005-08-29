package org.apache.turbine.modules.navigations;

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

import org.apache.ecs.ConcreteElement;
import org.apache.ecs.HtmlColor;
import org.apache.ecs.html.B;
import org.apache.ecs.html.Font;
import org.apache.ecs.html.HR;
import org.apache.turbine.modules.Navigation;
import org.apache.turbine.util.RunData;

/**
 * This is a sample navigation module.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 * @deprecated The use of ECS for the view is deprecated. Use a templating solution.
 */
public class DefaultTopNavigation
        extends Navigation
{
    /** The string to display */
    private static String txt = "Turbine - A Servlet Framework for building "
            + "Secure Dynamic Websites.";

    /**
     * Build the Navigation.
     *
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @exception Exception a generic exception.
     */
    public ConcreteElement doBuild(RunData data)
            throws Exception
    {
        data.getPage().getBody()
                .addElement(new B().addElement(
                        new Font().setColor(HtmlColor.green).setSize(2)
                .addElement(txt))
                .addElement(new HR().setSize(1).setNoShade(true)));

        return null;
    }
}
