package org.apache.turbine.modules;

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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.fulcrum.parser.ParameterParser;
import org.apache.fulcrum.parser.ValueParser.URLCaseFolding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.annotation.AnnotationProcessor;
import org.apache.turbine.annotation.TurbineActionEvent;
import org.apache.turbine.annotation.TurbineConfiguration;
import org.apache.turbine.pipeline.PipelineData;

/**
 * <p>
 *
 * This is an alternative to the Action class that allows you to do
 * event based actions. Essentially, you label all your submit buttons
 * with the prefix of "eventSubmit_" and the suffix of "methodName".
 * For example, "eventSubmit_doDelete". Then any class that subclasses
 * this class will get its "doDelete(PipelineData data)" method executed.
 * If for any reason, it was not able to execute the method, it will
 * fall back to executing the doPerform() method which is required to
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
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class ActionEvent implements Action
{
	/** Logging */
	protected Logger log = LogManager.getLogger(this.getClass());

	/** The name of the button to look for. */
	protected static final String BUTTON = "eventSubmit_";
	/** The length of the button to look for. */
	protected static final int BUTTON_LENGTH = BUTTON.length();
    /** The default method. */
    protected static final String DEFAULT_METHOD = "doPerform";
	/** The prefix of the method name. */
	protected static final String METHOD_NAME_PREFIX = "do";
	/** The length of the method name. */
	protected static final int METHOD_NAME_LENGTH = METHOD_NAME_PREFIX.length();
	/** The length of the button to look for. */
	protected static final int LENGTH = BUTTON.length();

	/**
	 * If true, the eventSubmit_do&lt;xxx&gt; variable must contain
	 * a not null value to be executed.
	 */
    @TurbineConfiguration( TurbineConstants.ACTION_EVENTSUBMIT_NEEDSVALUE_KEY )
	private boolean submitValueKey = TurbineConstants.ACTION_EVENTSUBMIT_NEEDSVALUE_DEFAULT;

	/**
	 * If true, then exceptions raised in eventSubmit_do&lt;xxx&gt; methods
	 * as well as in doPerform methods are bubbled up to the Turbine
	 * servlet's handleException method.
	 */
    @TurbineConfiguration( TurbineConstants.ACTION_EVENT_BUBBLE_EXCEPTION_UP )
	protected boolean bubbleUpException = TurbineConstants.ACTION_EVENT_BUBBLE_EXCEPTION_UP_DEFAULT;

	/**
	 * Cache for the methods to invoke
	 */
	private ConcurrentMap<String, Method> methodCache = new ConcurrentHashMap<>();

	/**
	 * Retrieve a method of the given name and signature. The value is cached.
	 *
	 * @param name the name of the method
	 * @param signature an array of classes forming the signature of the method
	 * @param pp ParameterParser for correct folding
	 *
	 * @return the method object
	 * @throws NoSuchMethodException if the method does not exist
	 */
	protected Method getMethod(String name, Class<?>[] signature, ParameterParser pp) throws NoSuchMethodException
	{
	    StringBuilder cacheKey = new StringBuilder(name);
	    for (Class<?> clazz : signature)
	    {
	        cacheKey.append(':').append(clazz.getCanonicalName());
	    }

	    Method method = this.methodCache.get(cacheKey.toString());

	    if (method == null)
	    {
	        // Try annotations of public methods
	        Method[] methods = getClass().getMethods();

        methodLoop:
	        for (Method m : methods)
	        {
	            Annotation[] annotations = AnnotationProcessor.getAnnotations(m);
	            for (Annotation a : annotations)
	            {
    	            if (a instanceof TurbineActionEvent)
    	            {
    	                TurbineActionEvent tae = (TurbineActionEvent) a;
    	                if (name.equals(pp.convert(tae.value()))
                            && Arrays.equals(signature, m.getParameterTypes()))
    	                {
    	                    method = m;
    	                    break methodLoop;
    	                }
    	            }
	            }
	        }

	        // Try legacy mode
	        if (method == null)
	        {
                String tmp = name.toLowerCase().substring(METHOD_NAME_LENGTH);
	            method = getClass().getMethod(METHOD_NAME_PREFIX + StringUtils.capitalize(tmp), signature);
	        }

	        Method oldMethod = this.methodCache.putIfAbsent(cacheKey.toString(), method);
	        if (oldMethod != null)
	        {
	            method = oldMethod;
	        }
	    }

	    return method;
	}

	/**
	 * This overrides the default Action.doPerform() to execute the
	 * doEvent() method. If that fails, then it will execute the
	 * doPerform() method instead.
	 *
	 * @param pipelineData Turbine information.
	 * @throws Exception a generic exception.
	 */
	@Override
    public void doPerform(PipelineData pipelineData)
			throws Exception
	{
	    ParameterParser pp = pipelineData.get(Turbine.class, ParameterParser.class);
		executeEvents(pp, new Class<?>[]{ PipelineData.class }, new Object[]{ pipelineData });
	}

	/**
	 * This method should be called to execute the event based system.
	 *
	 * @param pp the parameter parser
	 * @param signature the signature of the method to call
	 * @param parameters the parameters for the method to call
	 *
	 * @throws Exception a generic exception.
	 */
	protected void executeEvents(ParameterParser pp, Class<?>[] signature, Object[] parameters)
			throws Exception
	{
		// Name of the button.
		String theButton = null;

		String button = pp.convert(BUTTON);
		String key = null;

		// Loop through and find the button.
		for (String k : pp)
		{
			key = k;
			if (key.startsWith(button))
			{
				if (considerKey(key, pp))
				{
					theButton = key;
					break;
				}
			}
		}

		if (theButton == null)
		{
		    theButton = BUTTON + DEFAULT_METHOD;
		    key = null;
		}

		theButton = formatString(theButton, pp);
		Method method = null;

        try
        {
            method = getMethod(theButton, signature, pp);
        }
        catch (NoSuchMethodException e)
        {
            method = getMethod(DEFAULT_METHOD, signature, pp);
        }
        finally
        {
            if (key != null)
            {
                pp.remove(key);
            }
        }

		try
		{
			log.debug("Invoking {}", method);

			method.invoke(this, parameters);
		}
		catch (InvocationTargetException ite)
		{
			Throwable t = ite.getTargetException();
			if (bubbleUpException)
			{
                if (t instanceof Exception)
                {
                    throw (Exception) t;
                }
                else
                {
                    throw ite;
                }
			}
			else
			{
			    log.error("Invokation of {}", method, t);
			}
		}
	}

	/**
	 * This method does the conversion of the lowercase method name
	 * into the proper case.
	 *
	 * @param input The unconverted method name.
	 * @param pp The parameter parser (for correct folding)
	 * @return A string with the method name in the proper case.
	 */
	protected String formatString(String input, ParameterParser pp)
	{
		String tmp = input;

		if (StringUtils.isNotEmpty(input))
		{
			tmp = input.toLowerCase();

			// Chop off suffixes (for image type)
			String methodName = (tmp.endsWith(".x") || tmp.endsWith(".y"))
					? input.substring(0, input.length() - 2)
					: input;

			if (pp.getUrlFolding() == URLCaseFolding.NONE)
			{
                tmp = methodName.substring(BUTTON_LENGTH);
			}
			else
			{
                tmp = methodName.toLowerCase().substring(BUTTON_LENGTH);
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
			log.debug("No Value required, accepting {}", key);
			return true;
		}
		else
		{
			// If the action.eventsubmit.needsvalue key is true,
			// events with a "0" or empty value are ignored.
			// This can be used if you have multiple eventSubmit_do&lt;xxx&gt;
			// fields in your form which are selected by client side code,
			// e.g. JavaScript.
			//
			// If this key is unset or missing, nothing changes for the
			// current behavior.
			//
			String keyValue = pp.getString(key);
			log.debug("Key Value is {}", keyValue);
			if (StringUtils.isEmpty(keyValue))
			{
				log.debug("Key is empty, rejecting {}", key);
				return false;
			}

			try
			{
				if (Integer.parseInt(keyValue) != 0)
				{
					log.debug("Integer != 0, accepting {}", key);
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
		log.debug("Rejecting {}", key);
		return false;
	}
}
