package org.apache.turbine.modules.screens;

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

import org.apache.ecs.ConcreteElement;
import org.apache.ecs.html.B;
import org.apache.ecs.html.H3;
import org.apache.ecs.html.H4;
import org.apache.ecs.html.PRE;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;
import org.apache.turbine.modules.Screen;
import org.apache.turbine.util.RunData;

/**
 * This is a sample Error Screen module.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 */
public class Error extends Screen
{
    /**
     * Build screen.
     *
     * @param data Turbine information.
     * @return ConcreteElement the page with all the error information.
     * @throws Exception a generic exception.
     */
    public ConcreteElement doBuild(RunData data) throws Exception
    {
        data.setTitle("There has been an error!");

        Table table = new Table().setBorder(0);
        boolean hasValues = false;
        for (Iterator it = data.getParameters().keySet().iterator();
             it.hasNext();)
        {
            String key = (String) it.next();
            String value = data.getParameters().getString(key);
            TR tr =
                new TR().addElement(
                    new TD().addElement(new B(key))).addElement(
                    new TD().addElement(" = " + value));
            table.addElement(tr);
            hasValues = true;
        }

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

        data.getPage().getBody().addElement(
            new H3(
                data.getTitle()
                    + " Please review the exception below "
                    + "for more information."));

        if (hasValues)
        {
            data.getPage().getBody().addElement(
                new H4().addElement("Get/Post Data:"));
            data.getPage().getBody().addElement(table);
        }

        if (hasValues2)
        {
            data.getPage().getBody().addElement(
                new H4().addElement("Debugging Data:"));
            data.getPage().getBody().addElement(table2);
        }

        String trace = data.getStackTrace();
        if (trace != null)
        {
            data
                .getPage()
                .getBody()
                .addElement(new H4().addElement("The exception is:"))
                .addElement(new PRE(trace))
                .addElement(new PRE(data.getStackTraceException().toString()));
        }
        return null;
    }
}
