package org.apache.turbine.modules.screens;

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

import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.util.RunData;

import org.apache.velocity.context.Context;

/**
 * VelocitySecureScreen
 *
 * Always performs a Security Check that you've defined before
 * executing the doBuildtemplate().  You should extend this class and
 * add the specific security check needed.  If you have a number of
 * screens that need to perform the same check, you could make a base
 * screen by extending this class and implementing the isAuthorized().
 * Then each screen that needs to perform the same check could extend
 * your base screen.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class VelocitySecureScreen
        extends VelocityScreen
{
    /**
     * Implement this to add information to the context.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @param context Context for web pages.
     * @exception Exception, a generic exception.
     */
    protected abstract void doBuildTemplate(RunData data,
                                            Context context)
            throws Exception;

    /**
     * Implement this to add information to the context.
     *
     * @param data Turbine information.
     * @param context Context for web pages.
     * @exception Exception, a generic exception.
     */
    protected void doBuildTemplate(PipelineData pipelineData,
                                            Context context)
            throws Exception
    {
        RunData data = (RunData) getRunData(pipelineData);
        doBuildTemplate(data);
    }

    
    /**
     * This method overrides the method in VelocityScreen to
     * perform a security check first.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected void doBuildTemplate(RunData data)
        throws Exception
    {
        if (isAuthorized(data))
        {
            doBuildTemplate(data, TurbineVelocity.getContext(data));
        }
    }

    /**
     * This method overrides the method in VelocityScreen to
     * perform a security check first.
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected void doBuildTemplate(PipelineData pipelineData)
        throws Exception
    {
        if (isAuthorized(pipelineData))
        {
            doBuildTemplate(pipelineData, TurbineVelocity.getContext(pipelineData));
        }
    }

    
    
    /**
     * Implement this method to perform the security check needed.
     * You should set the template in this method that you want the
     * user to be sent to if they're unauthorized.  See the
     * VelocitySecurityCheck utility.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @return True if the user is authorized to access the screen.
     * @exception Exception, a generic exception.
     */
    protected abstract boolean isAuthorized(RunData data)
            throws Exception;
    
    /**
     * Implement this method to perform the security check needed.
     * You should set the template in this method that you want the
     * user to be sent to if they're unauthorized.  See the
     * VelocitySecurityCheck utility.
     *
     * @param data Turbine information.
     * @return True if the user is authorized to access the screen.
     * @exception Exception, a generic exception.
     */
    protected boolean isAuthorized(PipelineData pipelineData)
    throws Exception
    {
        RunData data = (RunData) getRunData(pipelineData);
        return isAuthorized(data);
    }

    
    
}
