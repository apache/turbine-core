package org.apache.turbine.modules;


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


import org.apache.ecs.ConcreteElement;

import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.InputFilterUtils;
import org.apache.turbine.util.RunData;

/**
 * This is the base class that defines what a Navigation module is.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class Navigation
    extends Assembler
{
    /**
     * A subclass must override this method to build itself.
     * Subclasses override this method to store the navigation in
     * RunData or to write the navigation to the output stream
     * referenced in RunData.
     * @deprecated Use PipelineData version instead
     *
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    protected abstract ConcreteElement doBuild(RunData data)
        throws Exception;

    /**
     * Subclasses can override this method to add additional
     * functionality.  This method is protected to force clients to
     * use NavigationLoader to build a Navigation.
     * @deprecated Use PipelineData version instead
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    protected ConcreteElement build(RunData data)
        throws Exception
    {
        return doBuild(data);
    }

    /**
     * A subclass must override this method to build itself.
     * Subclasses override this method to store the navigation in
     * RunData or to write the navigation to the output stream
     * referenced in RunData.
     *
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    protected ConcreteElement doBuild(PipelineData pipelineData)
        throws Exception
    {
        RunData data = (RunData)getRunData(pipelineData);
        return doBuild(data);
    }

    /**
     * Subclasses can override this method to add additional
     * functionality.  This method is protected to force clients to
     * use NavigationLoader to build a Navigation.
     *
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    protected ConcreteElement build(PipelineData pipelineData)
        throws Exception
    {
        return doBuild(pipelineData);
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
}
