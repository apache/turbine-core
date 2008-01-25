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


import org.apache.ecs.ConcreteElement;

import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.InputFilterUtils;
import org.apache.turbine.util.RunData;

/**
 * This is the base class which defines the Screen modules.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class Screen
    extends Assembler
{
    /**
     * A subclass must override this method to build itself.
     * Subclasses override this method to store the screen in RunData
     * or to write the screen to the output stream referenced in
     * RunData.
     * Should revert to abstract when RunData has gone.
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    protected ConcreteElement doBuild(PipelineData pipelineData)
        throws Exception
    {
        RunData data = (RunData) getRunData(pipelineData);
        return doBuild(data);
    }

    /**
     * Subclasses can override this method to add additional
     * functionality.  This method is protected to force clients to
     * use ScreenLoader to build a Screen.
     *
     * @param pipelineData Turbine information.
     * @exception Exception a generic exception.
     */
    protected ConcreteElement build(PipelineData pipelineData)
        throws Exception
    {
        return doBuild(pipelineData);
    }

    /**
     * If the Layout has not been defined by the Screen then set the
     * layout to be "DefaultLayout".  The Screen object can also
     * override this method to provide intelligent determination of
     * the Layout to execute.  You can also define that logic here as
     * well if you want it to apply on a global scale.  For example,
     * if you wanted to allow someone to define Layout "preferences"
     * where they could dynamically change the Layout for the entire
     * site.  The information for the request is passed in with the
     * PipelineData object.
     *
     * @param pipelineData Turbine information.
     * @return A String with the Layout.
     */
    public String getLayout(PipelineData pipelineData)
    {
        RunData data = (RunData) getRunData(pipelineData);
        return data.getLayout();
    }

    /**
     * Set the layout for a Screen.
     *
     * @param data Turbine information.
     * @param layout The layout name.
     */
    public void setLayout(PipelineData pipelineData, String layout)
    {
        RunData data = (RunData) getRunData(pipelineData);
        data.setLayout(layout);
    }





    /**
     * A subclass must override this method to build itself.
     * Subclasses override this method to store the screen in RunData
     * or to write the screen to the output stream referenced in
     * RunData.
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    protected abstract ConcreteElement doBuild(RunData data)
        throws Exception;

    /**
     * Subclasses can override this method to add additional
     * functionality.  This method is protected to force clients to
     * use ScreenLoader to build a Screen.
     * @deprecated Use PipelineData version instead.
     *
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    protected ConcreteElement build(RunData data)
        throws Exception
    {
        return doBuild(data);
    }

    /**
     * If the Layout has not been defined by the Screen then set the
     * layout to be "DefaultLayout".  The Screen object can also
     * override this method to provide intelligent determination of
     * the Layout to execute.  You can also define that logic here as
     * well if you want it to apply on a global scale.  For example,
     * if you wanted to allow someone to define Layout "preferences"
     * where they could dynamically change the Layout for the entire
     * site.  The information for the request is passed in with the
     * RunData object.
     * @deprecated Use PipelineData version instead.
     *
     * @param data Turbine information.
     * @return A String with the Layout.
     */
    public String getLayout(RunData data)
    {
        return data.getLayout();
    }

    /**
     * Set the layout for a Screen.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @param layout The layout name.
     */
    public void setLayout(RunData data, String layout)
    {
        data.setLayout(layout);
    }

    /**
     * This function can/should be used in any screen that will output
     * User entered text.  This will help prevent users from entering
     * html (<SCRIPT>) tags that will get executed by the browser.
     *
     * @param s The string to prepare.
     * @return A string with the input already prepared.
     * @deprecated Use InputFilterUtils.prepareText(String s)
     */
    public static String prepareText(String s)
    {
        return InputFilterUtils.prepareText(s);
    }

    /**
     * This function can/should be used in any screen that will output
     * User entered text.  This will help prevent users from entering
     * html (<SCRIPT>) tags that will get executed by the browser.
     *
     * @param s The string to prepare.
     * @return A string with the input already prepared.
     * @deprecated Use InputFilterUtils.prepareTextMinimum(String s)
     */
    public static String prepareTextMinimum(String s)
    {
        return InputFilterUtils.prepareTextMinimum(s);
    }
}
