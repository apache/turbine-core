package org.apache.turbine.modules.navigations;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

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
import org.apache.turbine.modules.Navigation;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;

/**
 * This is a sample navigation module.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 * @deprecated The use of ECS for the view is deprecated. Use a templating solution.
 */
public class DefaultBottomNavigation extends Navigation
{
    private static final boolean DEBUG = false;
    private static String txt =
            "Turbine - A Servlet Framework for building Secure Dynamic Websites.";

    /**
     * Build the Navigation.
     *
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @exception Exception, a generic exception.
     */
    public ConcreteElement doBuild(RunData data)
            throws Exception
    {
        Form form;
        form = new Form(
                new DynamicURI(data, "DefaultScreen", "LogoutUser", true).toString(),
                Form.POST)
                .addElement(new Input("SUBMIT", "Logout", "Logout"));
        ElementContainer body = new ElementContainer()
                .addElement(new HR().setSize(1).setNoShade(true))
                .addElement(new B().addElement(
                        new Font().setColor(HtmlColor.green).setSize(2).addElement(txt)))
                .addElement(form);

        if (DEBUG && data.getUser() != null)
        {
            TD perm = new TD().setVAlign(AlignType.TOP);
            TD temp = new TD().setVAlign(AlignType.TOP);
            java.util.Enumeration ePerm =
                    data.getUser().getPermStorage().keys();
            java.util.Enumeration eTemp =
                    data.getUser().getTempStorage().keys();

            perm.addElement("Perm values:").addElement(new BR());
            temp.addElement("Temp values:").addElement(new BR());
            while (ePerm.hasMoreElements())
            {
                String key = (String) ePerm.nextElement();
                String value = data.getUser().getPerm(key).toString();
                perm.addElement(key + "=" + value)
                        .addElement(new BR());
            }
            while (eTemp.hasMoreElements())
            {
                String key = (String) eTemp.nextElement();
                String value = data.getUser().getTemp(key).toString();
                temp.addElement(key + "=" + value)
                        .addElement(new BR());
            }
            body.addElement(new BR()).addElement(new BR())
                    .addElement(new Table().setBorder(2).setCellPadding(10)
                    .addElement(new TR()
                    .addElement(perm).addElement(temp)));
        }
        if (DEBUG)
        {
            // If there are GET/POST/PATH_INFO variables put them into
            // a <PRE></PRE> tag so that they can be displayed on the
            // page. This is of course only for example purposes.
            PRE pre = new PRE();
            Enumeration keys = data.getParameters().keys();
            while (keys.hasMoreElements())
            {
                String key = (String) keys.nextElement();
                String[] values = data.getParameters().getStrings(key);
                if (values.length == 1)
                    pre.addElement(key + " = " + values[0] + "\n");
                else
                {
                    pre.addElement(key + " = ");
                    for (int i = 0; i < values.length; i++)
                        pre.addElement(values[i] + " ");
                    pre.addElement("\n");
                }
            }
            body.addElement(new B("Query/PathInfo Parameters"))
                    .addElement(new BR())
                    .addElement(pre);

            Table table2 = new Table().setBorder(0);
            Hashtable varDebug = data.getVarDebug();
            keys = varDebug.keys();
            boolean hasValues2 = false;
            while (keys.hasMoreElements())
            {
                String key = (String) keys.nextElement();
                String value = varDebug.get(key).toString();
                TR tr = new TR()
                        .addElement(new TD().addElement(new B(key)))
                        .addElement(new TD().addElement(" = " + value));
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
            for (Iterator rs = data.getACL().getRoles().elements(); rs.hasNext();)
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
            for (Iterator ps = data.getACL().getPermissions().elements(); ps.hasNext();)
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
