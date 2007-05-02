package org.apache.turbine.modules.navigations;

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

import java.util.Iterator;
import java.util.Map;

import org.apache.ecs.AlignType;
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.ElementContainer;
import org.apache.ecs.HtmlColor;
import org.apache.ecs.html.B;
import org.apache.ecs.html.BR;
import org.apache.ecs.html.Font;
import org.apache.ecs.html.Form;
import org.apache.ecs.html.H4;
import org.apache.ecs.html.HR;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.PRE;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;

import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.Navigation;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.uri.TurbineURI;

/**
 * This is a sample navigation module.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 * @deprecated The use of ECS for the view is deprecated.
 * Use a templating solution.
 */
public class DefaultBottomNavigation extends Navigation
{
    /** Specify whether to output detailed information */
    private static final boolean DEBUG = false;
    /** The string to display */
    private static String txt =
        "Turbine - A Servlet Framework for building Secure Dynamic Websites.";

    /**
     * Build the Navigation.
     *
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @throws Exception a generic exception.
     */
    public ConcreteElement doBuild(RunData data) throws Exception
    {
        Form form;
        form = new Form(
            new TurbineURI(data,
                           TurbineConstants.SCREEN_DEFAULT_DEFAULT,
                           TurbineConstants.ACTION_LOGOUT_DEFAULT,
                           true).getRelativeLink(),
            Form.POST).addElement(new Input("SUBMIT", "Logout", "Logout"));

        ElementContainer body = new ElementContainer()
                .addElement(new HR().setSize(1).setNoShade(true))
                .addElement(
                    new B().addElement(
                        new Font().setColor(HtmlColor.green).setSize(
                            2).addElement(
                            txt)))
                .addElement(form);

        if (DEBUG && data.getUser() != null)
        {
            TD perm = new TD().setVAlign(AlignType.TOP);
            TD temp = new TD().setVAlign(AlignType.TOP);

            perm.addElement("Perm values:").addElement(new BR());
            for (Iterator it = data.getUser().getPermStorage().keySet().iterator();
                 it.hasNext();)
            {
                String key = (String) it.next();
                String value = data.getUser().getPerm(key).toString();
                perm.addElement(key + "=" + value).addElement(new BR());
            }

            temp.addElement("Temp values:").addElement(new BR());
            for (Iterator it = data.getUser().getTempStorage().keySet().iterator();
                 it.hasNext();)
            {
                String key = (String) it.next();
                String value = data.getUser().getTemp(key).toString();
                temp.addElement(key + "=" + value).addElement(new BR());
            }

            body.addElement(new BR()).addElement(new BR()).addElement(
                new Table().setBorder(2).setCellPadding(10).addElement(
                    new TR().addElement(perm).addElement(temp)));
        }
        if (DEBUG)
        {
            // If there are GET/POST/PATH_INFO variables put them into
            // a <PRE></PRE> tag so that they can be displayed on the
            // page. This is of course only for example purposes.
            PRE pre = new PRE();

            for (Iterator it = data.getParameters().keySet().iterator();
                 it.hasNext();)
            {
                String key = (String) it.next();
                String[] values = data.getParameters().getStrings(key);
                if (values.length == 1)
                {
                    pre.addElement(key + " = " + values[0] + "\n");
                }
                else
                {
                    pre.addElement(key + " = ");
                    for (int i = 0; i < values.length; i++)
                    {
                        pre.addElement(values[i] + " ");
                    }
                    pre.addElement("\n");
                }
            }
            body
                .addElement(new B("Query/PathInfo Parameters"))
                .addElement(new BR())
                .addElement(pre);

            Table table2 = new Table().setBorder(0);
            Map varDebug = data.getDebugVariables();

            boolean hasValues2 = false;

            for (Iterator i = varDebug.keySet().iterator(); i.hasNext();)
            {
                String key = (String) i.next();
                String value = varDebug.get(key).toString();
                TR tr =
                    new TR().addElement(
                        new TD().addElement(new B(key))).addElement(
                        new TD().addElement(" = " + value));
                table2.addElement(tr);
                hasValues2 = true;
            }
            if (hasValues2)
            {
                body.addElement(new H4().addElement("Debugging Data:"));
                body.addElement(table2);
            }
        }

        if (DEBUG && data.getACL() != null)
        {
            // Print out user's permissions.
            PRE pre = new PRE();
            for (Iterator rs = data.getACL().getRoles().iterator();
                rs.hasNext();
                )
            {
                String roleName = ((Role) rs.next()).getName();
                pre.addElement(roleName + "\n");
            }
            body
                .addElement(new BR())
                .addElement(new B("ROLES"))
                .addElement(new BR())
                .addElement(pre);

            pre = new PRE();
            for (Iterator ps = data.getACL().getPermissions().iterator();
                ps.hasNext();
                )
            {
                String permissionName = ((Permission) ps.next()).getName();
                pre.addElement(permissionName + "\n");
            }
            body
                .addElement(new BR())
                .addElement(new B("PERMISSIONS"))
                .addElement(new BR())
                .addElement(pre);
        }
        return body;
    }
}
