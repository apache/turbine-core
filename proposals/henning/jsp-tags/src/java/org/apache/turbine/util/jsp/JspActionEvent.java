package org.apache.turbine.util.jsp;

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

import java.lang.reflect.Method;
import java.util.Enumeration;
import org.apache.turbine.modules.ActionEvent;
import org.apache.turbine.services.jsp.TurbineJsp;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * In order to use Context in Jsp templates your Action's should
 * extend this class instead of extending the ActionEvent class.  The
 * difference between this class and the ActionEvent class is that
 * this class will first attempt to execute one of your doMethod's
 * with a constructor like this:
 *
 * <p>doEvent(RunData data, Context context)
 *
 * <p>It gets the context from the TemplateInfo.getTemplateContext()
 * method. If it can't find a method like that, then it will try to
 * execute the method without the Context in it.
 *
 * @version $Id$
 */
public abstract class JspActionEvent extends ActionEvent
{
    /**
     * You need to implement this in your classes that extend this
     * class.
     *
     * @param data A Turbine RunData object.
     * @exception Exception, a generic exception.
     */
    public abstract void doPerform(RunData data)
        throws Exception;

    /**
     * This overrides the default Action.perform() to execute the
     * doEvent() method.  If that fails, then it will execute the
     * doPerform() method instead.
     *
     * @param data A Turbine RunData object.
     * @exception Exception, a generic exception.
     */
    protected void perform(RunData data)
        throws Exception
    {
        try
        {
            executeEvents(data, TurbineJsp.getContext(data));
        }
        catch (NoSuchMethodException e)
        {
            doPerform(data);
        }
    }

    /**
     * This method should be called to execute the event based system.
     *
     * @param data A Turbine RunData object.
     * @param context Jsp context information.
     * @exception Exception, a generic exception.
     */
    public void executeEvents(RunData data, Context context) throws Exception
    {
        // Name of the button.
        String theButton = null;

        // ParameterParser.
        ParameterParser pp = data.getParameters();

        String button = pp.convert(BUTTON);

        // Loop through and find the button.
        for (Enumeration e = pp.keys() ; e.hasMoreElements() ;)
        {
            String key = (String) e.nextElement();
            if (key.startsWith(button))
            {
                theButton = formatString(key);
                break;
            }
        }

        if (theButton == null)
            throw new NoSuchMethodException("ActionEvent: The button was null");

        try
        {
            // The arguments to the method to find.
            Class[] classes = new Class[2];
            classes[0] = RunData.class;
            classes[1] = Context.class;

            // The arguments to pass to the method to execute.
            Object[] args = new Object[2];

            Method method = getClass().getMethod(theButton, classes);
            args[0] = data;
            args[1] = context;
            method.invoke(this, args);
        }
        catch (NoSuchMethodException nsme)
        {
            // Attempt to execut things the old way..
            super.executeEvents(data);
        }
    }
}



