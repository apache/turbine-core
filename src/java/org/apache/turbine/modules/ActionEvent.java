package org.apache.turbine.modules;

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
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.parser.ParserUtils;

/**
 * <p>
 *
 * This is an alternative to the Action class that allows you to do
 * event based actions. Essentially, you label all your submit buttons
 * with the prefix of "eventSubmit_" and the suffix of "methodName".
 * For example, "eventSubmit_doDelete". Then any class that subclasses
 * this class will get its "doDelete(RunData data)" method executed.
 * If for any reason, it was not able to execute the method, it will
 * fall back to executing the doPeform() method which is required to
 * be implemented.
 *
 * <p>
 *
 * Limitations:
 *
 * <p>
 *
 * Because ParameterParser makes all the key values lowercase, we have
 * to do some work to format the string into a method name. For
 * example, a button name eventSubmit_doDelete gets converted into
 * eventsubmit_dodelete. Thus, we need to form some sort of naming
 * convention so that dodelete can be turned into doDelete.
 *
 * <p>
 *
 * Thus, the convention is this:
 *
 * <ul>
 * <li>The variable name MUST have the prefix "eventSubmit_".</li>
 * <li>The variable name after the prefix MUST begin with the letters
 * "do".</li>
 * <li>The first letter after the "do" will be capitalized and the
 * rest will be lowercase</li>
 * </ul>
 *
 * If you follow these conventions, then you should be ok with your
 * method naming in your Action class.
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens </a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public abstract class ActionEvent extends Action
{
    /**
     * You need to implement this in your classes that extend this class.
     *
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    public abstract void doPerform(RunData data)
            throws Exception;

    /** The name of the button to look for. */
    protected static final String BUTTON = "eventSubmit_";
    /** The length of the button to look for. */
    protected static final int BUTTON_LENGTH = BUTTON.length();
    /** The prefix of the method name. */
    protected static final String METHOD_NAME_PREFIX = "do";
    /** The length of the method name. */
    protected static final int METHOD_NAME_LENGTH = METHOD_NAME_PREFIX.length();
    /** The length of the button to look for. */
    protected static final int LENGTH = BUTTON.length();

    /**
     * This overrides the default Action.perform() to execute the
     * doEvent() method. If that fails, then it will execute the
     * doPerform() method instead.
     *
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    protected void perform(RunData data)
            throws Exception
    {
        try
        {
            executeEvents(data);
        }
        catch (NoSuchMethodException e)
        {
            doPerform(data);
        }
    }

    /**
     * This method should be called to execute the event based system.
     *
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    public void executeEvents(RunData data)
            throws Exception
    {
        // Name of the button.
        String theButton = null;
        // Parameter parser.
        ParameterParser pp = data.getParameters();

        String button = pp.convert(BUTTON);

        // Loop through and find the button.
        for (Iterator it = pp.keySet().iterator(); it.hasNext();)
        {
            String key = (String) it.next();
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

        Class[] classes = new Class[]{RunData.class};
        Method method = getClass().getMethod(theButton, classes);
        Object[] args = new Object[]{data};

        method.invoke(this, args);
    }

    /**
     * This method does the conversion of the lowercase method name
     * into the proper case.
     *
     * @param input The unconverted method name.
     * @return A string with the method name in the proper case.
     */
    protected final String formatString(String input)
    {
        String tmp = null;
        
        if (StringUtils.isNotEmpty(input))
        {
            tmp = input.toLowerCase();
            
            // Chop off suffixes (for image type)
            input = (tmp.endsWith(".x") || tmp.endsWith(".y"))
                    ? input.substring(0, input.length() - 2)
                    : input;
            
            if (ParserUtils.getUrlFolding() 
                    != ParserUtils.URL_CASE_FOLDING_NONE)
            {
                
                tmp = tmp.substring(BUTTON_LENGTH + METHOD_NAME_LENGTH);
                tmp = METHOD_NAME_PREFIX + StringUtils.capitalise(tmp);
            }
            else
            {
                tmp = input.substring(BUTTON_LENGTH);
            }
        }
        return tmp;
    }
}


