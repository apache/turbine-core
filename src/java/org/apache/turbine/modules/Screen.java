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
