package org.apache.turbine.modules.screens;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;

/**
 * This is a sample Error Screen module.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class Error extends Screen
{
    /**
     * Build screen.
     *
     * @deprecated Use PipelineData version instead.
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
    
    
    /**
     * Build screen.
     *
     * @param data Turbine information.
     * @return ConcreteElement the page with all the error information.
     * @throws Exception a generic exception.
     */
    public ConcreteElement doBuild(PipelineData pipelineData) throws Exception
    {
        RunData data = (RunData) getRunData(pipelineData);
        return doBuild(data);
    }
}
