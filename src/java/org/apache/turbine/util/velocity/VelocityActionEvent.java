package org.apache.turbine.util.velocity;

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

import java.lang.reflect.Method;
import java.util.Enumeration;

import org.apache.turbine.modules.ActionEvent;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * If you are using VelocitySite stuff, then your Action's should
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
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class VelocityActionEvent extends ActionEvent
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
            executeEvents(data, TurbineVelocity.getContext(data));
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
     * @param context Velocity context information.
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
        for (Enumeration e = pp.keys(); e.hasMoreElements();)
        {
            String key = (String) e.nextElement();
            if (key.startsWith(button))
            {
                theButton = formatString(key);
                break;
            }
        }

        if (theButton == null)
        {
            throw new NoSuchMethodException("ActionEvent: The button was null");
        }

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
