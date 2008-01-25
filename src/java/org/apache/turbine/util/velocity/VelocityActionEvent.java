package org.apache.turbine.util.velocity;


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


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Iterator;

import org.apache.turbine.modules.ActionEvent;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.util.RunData;
import org.apache.fulcrum.parser.ParameterParser;

import org.apache.velocity.context.Context;

/**
 * If you are using VelocitySite stuff, then your Action's should
 * extend this class instead of extending the ActionEvent class.  The
 * difference between this class and the ActionEvent class is that
 * this class will first attempt to execute one of your doMethod's
 * with a constructor like this:
 *
 * <p><code>doEvent(RunData data, Context context)</code></p>
 *
 * <p>It gets the context from the TemplateInfo.getTemplateContext()
 * method. If it can't find a method like that, then it will try to
 * execute the method without the Context in it.</p>
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class VelocityActionEvent extends ActionEvent
{
    /** Constant needed for Reflection */
    private static final Class [] methodParams
            = new Class [] { RunData.class, Context.class };

    /** Indicates whether or not this module has been initialized. */
    protected boolean initialized = false;

    /**
     * You need to implement this in your classes that extend this
     * class.
     *
     * @deprecated Use PipelineData version instead.
     * @param data A Turbine RunData object.
     * @exception Exception a generic exception.
     */
    public abstract void doPerform(RunData data)
            throws Exception;

	/**
	 * You need to implement this in your classes that extend this class.
	 * Should revert to abstract once RunData is gone.
	 * @param data Turbine information.
	 * @exception Exception a generic exception.
	 */
	public void doPerform(PipelineData pipelineData)
			throws Exception
	{
	      RunData data = (RunData) getRunData(pipelineData);
	      doPerform(data);
	}
    /**
     * Provides a means of initializing the module.
     *
     * @throws Exception a generic exception.
     */
    protected abstract void initialize()
        throws Exception;

    /**
     * This overrides the default Action.perform() to execute the
     * doEvent() method.  If that fails, then it will execute the
     * doPerform() method instead.
     *
     * @deprecated Use PipelineData version instead.
     * @param data A Turbine RunData object.
     * @exception Exception a generic exception.
     */
    protected void perform(RunData data)
            throws Exception
    {
        try
        {
            if (!initialized)
            {
                initialize();
            }
            executeEvents(data, TurbineVelocity.getContext(data));
        }
        catch (NoSuchMethodException e)
        {
            doPerform(data);
        }
    }

    /**
     * This overrides the default Action.perform() to execute the
     * doEvent() method.  If that fails, then it will execute the
     * doPerform() method instead.
     *
     * @param data A Turbine RunData object.
     * @exception Exception a generic exception.
     */
    protected void perform(PipelineData pipelineData)
            throws Exception
    {
	    RunData data = (RunData) getRunData(pipelineData);
        try
        {
            if (!initialized)
            {
                initialize();
            }
            executeEvents(pipelineData, TurbineVelocity.getContext(pipelineData));
        }
        catch (NoSuchMethodException e)
        {
            doPerform(pipelineData);
        }
    }
    /**
     * This method should be called to execute the event based system.
     * @deprecated Use PipelineData version instead.
     * @param data A Turbine RunData object.
     * @param context Velocity context information.
     * @exception Exception a generic exception.
     */
    public void executeEvents(RunData data, Context context)
            throws Exception
    {
        // Name of the button.
        String theButton = null;

        // ParameterParser.
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
            throw new NoSuchMethodException(
                    "ActionEvent: The button was null");
        }

        Method method = null;
        try
        {
            method = getClass().getMethod(theButton, methodParams);
            Object[] methodArgs = new Object[] { data, context };

            if (log.isDebugEnabled())
            {
                log.debug("Invoking " + method);
            }

            method.invoke(this, methodArgs);
        }
        catch (NoSuchMethodException nsme)
        {
            // Attempt to execute things the old way..
            if (log.isDebugEnabled())
            {
                log.debug("Couldn't locate the Event ( " + theButton
                        + "), running executeEvents() in "
                        + super.getClass().getName());
            }

            super.executeEvents(data);
        }
        catch (InvocationTargetException ite)
        {
            Throwable t = ite.getTargetException();
            log.error("Invokation of " + method , t);
            throw ite;
        }
        finally
        {
            pp.remove(key);
        }
    }

    /**
     * This method should be called to execute the event based system.
     *
     * @param data A Turbine RunData object.
     * @param context Velocity context information.
     * @exception Exception a generic exception.
     */
    public void executeEvents(PipelineData pipelineData, Context context)
            throws Exception
    {
	    RunData data = (RunData) getRunData(pipelineData);
        // Name of the button.
        String theButton = null;

        // ParameterParser.
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
            throw new NoSuchMethodException(
                    "ActionEvent: The button was null");
        }

        Method method = null;
        try
        {
            method = getClass().getMethod(theButton, methodParams);
            Object[] methodArgs = new Object[] { pipelineData, context };

            if (log.isDebugEnabled())
            {
                log.debug("Invoking " + method);
            }

            method.invoke(this, methodArgs);
        }
        catch (NoSuchMethodException nsme)
        {
            // Attempt to execute things the old way..
            if (log.isDebugEnabled())
            {
                log.debug("Couldn't locate the Event ( " + theButton
                        + "), running executeEvents() in "
                        + super.getClass().getName());
            }

            super.executeEvents(pipelineData);
        }
        catch (InvocationTargetException ite)
        {
            Throwable t = ite.getTargetException();
            log.error("Invokation of " + method , t);
            throw ite;
        }
        finally
        {
            pp.remove(key);
        }
    }

}
