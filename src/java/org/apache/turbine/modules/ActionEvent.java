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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.parser.ParameterParser;
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
	/** Logging */
	protected Log log = LogFactory.getLog(this.getClass());

	/** Constant needed for Reflection */
	private static final Class [] methodParams
			= new Class [] { RunData.class };

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
	 * If true, the eventSubmit_do<xxx> variable must contain
	 * a not null value to be executed.
	 */
	private boolean submitValueKey = false;

	/** 
	 * If true, then exceptions raised in eventSubmit_do<xxx> methods 
	 * as well as in doPerform methods are bubbled up to the Turbine
	 * servlet's handleException method.
	 */
	protected boolean bubbleUpException = true;    
	/**
	 * C'tor
	 */
	public ActionEvent()
	{
		super();
        
		submitValueKey = Turbine.getConfiguration()
				.getBoolean(TurbineConstants.ACTION_EVENTSUBMIT_NEEDSVALUE_KEY,
						TurbineConstants.ACTION_EVENTSUBMIT_NEEDSVALUE_DEFAULT);
		bubbleUpException = Turbine.getConfiguration()
				.getBoolean(TurbineConstants.ACTION_EVENT_BUBBLE_EXCEPTION_UP,
						TurbineConstants.ACTION_EVENT_BUBBLE_EXCEPTION_UP_DEFAULT);                        

		if (log.isDebugEnabled()){
		log.debug(submitValueKey 
				? "ActionEvent accepts only eventSubmit_do Keys with a value != 0"
				: "ActionEvent accepts all eventSubmit_do Keys");
		log.debug(bubbleUpException 
				  ? "ActionEvent will bubble exceptions up to Turbine.handleException() method"
				  : "ActionEvent will not bubble exceptions up.");                
		}
	}
    
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
		String key = null;

		// Loop through and find the button.
		for (Iterator it = pp.keySet().iterator(); it.hasNext();)
		{
			key = (String) it.next();
			if (key.startsWith(button))
			{
				if (considerKey(key, pp))
				{
					theButton = formatString(key);
					break;
				}
			}
		}

		if (theButton == null)
		{
			throw new NoSuchMethodException("ActionEvent: The button was null");
		}

		Method method = null;

		try
		{
			method = getClass().getMethod(theButton, methodParams);
			Object[] methodArgs = new Object[] { data };

			if (log.isDebugEnabled())
			{
				log.debug("Invoking " + method);
			}

			method.invoke(this, methodArgs);
		}
		catch (InvocationTargetException ite)
		{
			Throwable t = ite.getTargetException();
			log.error("Invokation of " + method , t);
		}
		finally
		{
			pp.remove(key);
		}
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
		String tmp = input;
        
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
				tmp = input.toLowerCase().substring(BUTTON_LENGTH + METHOD_NAME_LENGTH);
				tmp = METHOD_NAME_PREFIX + StringUtils.capitalise(tmp);
			}
			else
			{
				tmp = input.substring(BUTTON_LENGTH);
			}
		}
		return tmp;
	}

	/**
	 * Checks whether the selected key really is a valid event.
	 *
	 * @param key The selected key
	 * @param pp The parameter parser to look for the key value
	 *
	 * @return true if this key is really an ActionEvent Key
	 */
	protected boolean considerKey(String key, ParameterParser pp)
	{
		if (!submitValueKey)
		{
			log.debug("No Value required, accepting " + key);
			return true;
		}
		else
		{
			// If the action.eventsubmit.needsvalue key is true, 
			// events with a "0" or empty value are ignored.
			// This can be used if you have multiple eventSubmit_do<xxx>
			// fields in your form which are selected by client side code, 
			// e.g. JavaScript.
			//
			// If this key is unset or missing, nothing changes for the
			// current behaviour.
			//
			String keyValue = pp.getString(key);
			log.debug("Key Value is " + keyValue);
			if (StringUtils.isEmpty(keyValue))
			{
				log.debug("Key is empty, rejecting " + key);
				return false;
			}

			try
			{
				if (Integer.parseInt(keyValue) != 0)
				{
					log.debug("Integer != 0, accepting " + key);
					return true;
				}
			}
			catch (NumberFormatException nfe)
			{
				// Not a number. So it might be a
				// normal Key like "continue" or "exit". Accept
				// it.
				log.debug("Not a number, accepting " + key);
				return true;
			}
		}
		log.debug("Rejecting " + key);
		return false;
	}
}
