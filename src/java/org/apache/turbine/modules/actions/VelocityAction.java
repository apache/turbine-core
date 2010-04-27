package org.apache.turbine.modules.actions;

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

import org.apache.turbine.modules.screens.TemplateScreen;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.velocity.VelocityActionEvent;
import org.apache.velocity.context.Context;

/**
 * This class provides a convenience methods for Velocity Actions
 * to use. Since this class is abstract, it should only be extended
 * and not used directly.
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class VelocityAction extends VelocityActionEvent
{
  /**
   * You SHOULD NOT override this method and implement it in your
   * action.
   * @deprecated Use PipelineData version instead.
   * @param data Turbine information.
   * @throws Exception a generic exception.
   */
  public void doPerform(RunData data)
      throws Exception
  {
    doPerform(data, getContext(data));
  }

  /**
   * You SHOULD NOT override this method and implement it in your
   * action.
   *
   * @param data Turbine information.
   * @throws Exception a generic exception.
   */
  public void doPerform(PipelineData pipelineData)
      throws Exception
  {
      doPerform(pipelineData, getContext(pipelineData));
  }


  /**
   * Initialize the module.
   *
   * @throws Exception a generic exception.
   */
    public void initialize()
        throws Exception
    {
        initialized = true;
    }

  /**
   * You SHOULD override this method and implement it in your
   * action.
   * @deprecated Use PipelineData version instead.
   * @param data Turbine information.
   * @param context Context for web pages.
   * @throws Exception a generic exception.
   */
  public abstract void doPerform(RunData data,
                   Context context)
      throws Exception;

  /**
   * You SHOULD override this method and implement it in your
   * action.
   *
   * This should become abstract when the RunData version is
   * removed. For compatibility reasons this method will default
   * to using the RunData method unles it is overidden, which it
   * should be.
   *
   * @param data Turbine information.
   * @param context Context for web pages.
   * @throws Exception a generic exception.
   */
  public void doPerform(PipelineData pipelineData,
                   Context context)
      throws Exception
  {
      RunData data = (RunData) getRunData(pipelineData);
      doPerform(data, context);
  }


  /**
   * Sets up the context and then calls super.perform(); thus,
   * subclasses don't have to worry about getting a context
   * themselves!  If a subclass throws an exception then depending
   * on whether action.event.bubbleexception is true, then it bubbles
   * it farther up, or traps it there.
   *
   * @deprecated Use PipelineData version instead.
   * @param data Turbine information.
   * @throws Exception a generic exception.
   */
  protected void perform(RunData data)
      throws Exception
  {
    try {
      super.perform(data);
    }
    catch (Exception e){
      if(bubbleUpException){
      throw e;
      }

    }
  }

  /**
   * Sets up the context and then calls super.perform(); thus,
   * subclasses don't have to worry about getting a context
   * themselves!  If a subclass throws an exception then depending
   * on whether action.event.bubbleexception is true, then it bubbles
   * it farther up, or traps it there.
   *
   * @param data Turbine information.
   * @throws Exception a generic exception.
   */
  protected void perform(PipelineData pipelineData)
      throws Exception
  {
    try {
      super.perform(pipelineData);
    }
    catch (Exception e){
      if(bubbleUpException){
      throw e;
      }

    }
  }



  /**
   * This method is used when you want to short circuit an Action
   * and change the template that will be executed next.
   *
   * @deprecated Use PipelineData version instead.
   * @param data Turbine information.
   * @param template The template that will be executed next.
   */
  public void setTemplate(RunData data,
              String template)
  {
    TemplateScreen.setTemplate(data, template);
  }

  /**
   * This method is used when you want to short circuit an Action
   * and change the template that will be executed next.
   *
   * @param data Turbine information.
   * @param template The template that will be executed next.
   */
  public void setTemplate(PipelineData pipelineData,
              String template)
  {
      TemplateScreen.setTemplate(pipelineData, template);
  }



  /**
   * Return the Context needed by Velocity.
   *
   * @deprecated Use PipelineData version instead.
   * @param data Turbine information.
   * @return Context, a context for web pages.
   */
  protected Context getContext(RunData data)
  {
    return TurbineVelocity.getContext(data);
  }

  /**
   * Return the Context needed by Velocity.
   *
   * @param data Turbine information.
   * @return Context, a context for web pages.
   */
  protected Context getContext(PipelineData pipelineData)
  {
      return TurbineVelocity.getContext(pipelineData);
  }

}
