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

// Turbine stuff.

import org.apache.ecs.ConcreteElement;
import org.apache.turbine.modules.Screen;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;

/**
 * Base class for writing Screens that output binary data.  This class
 * is provided as a helper class for those who want to write Screens
 * that output raw binary data.  For example, it may be extended into
 * a Screen that outputs a SVG file or a SWF (Flash Player format)
 * movie.  The only thing one has to do is to implement the two
 * methods <code>getContentType(RunData data)</code> and
 * <code>doOutput(RunData data)</code> (see below).
 *
 * <p> You migth want to take a look at the ImageServer screen class
 * contained in the TDK.<br>
 *
 * @author <a href="mailto:rkoenig@chez.com">Regis Koenig</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class RawScreen extends Screen
{
    /**
     * Build the Screen.  This method actually makes a call to the
     * doOutput() method in order to generate the Screen content.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @exception Exception, a generic exception.
     */
    protected final ConcreteElement doBuild(RunData data)
            throws Exception
    {
        data.getResponse().setContentType(getContentType(data));
        data.declareDirectResponse();
        doOutput(data);
        return null;
    }

    /**
     * Build the Screen.  This method actually makes a call to the
     * doOutput() method in order to generate the Screen content.
     *
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @exception Exception, a generic exception.
     */
    protected final ConcreteElement doBuild(PipelineData pipelineData)
            throws Exception
    {
        RunData data = (RunData) getRunData(pipelineData);
        return doBuild(data);
    }

    
    /**
     * Set the content type.  This method should be overidden to
     * actually set the real content-type header of the output.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @return A String with the content type.
     */
    protected abstract String getContentType(RunData data);

    /**
     * Set the content type.  This method should be overidden to
     * actually set the real content-type header of the output.
     *
     * @param data Turbine information.
     * @return A String with the content type.
     */
    protected String getContentType(PipelineData pipelineData)
    {
        RunData data = (RunData) getRunData(pipelineData);
        return getContentType(data);
    }

    
    /**
     * Actually output the dynamic content.  The OutputStream can be
     * accessed like this: <pre>OutputStream out =
     * data.getResponse().getOutputStream();</pre>.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected abstract void doOutput(RunData data)
            throws Exception;

    /**
     * Actually output the dynamic content.  The OutputStream can be
     * accessed like this: <pre>OutputStream out =
     * data.getResponse().getOutputStream();</pre>.
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected void doOutput(PipelineData pipelineData)
            throws Exception
    {
        RunData data = (RunData) getRunData(pipelineData);
        doOutput(data);
    }

    
    /**
     * The layout must be set to null.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @return A null String.
     */
    public final String getLayout(RunData data)
    {
        return null;
    }
    
    /**
     * The layout must be set to null.
     *
     * @param data Turbine information.
     * @return A null String.
     */
    public final String getLayout(PipelineData pipelineData)
    {
        return null;
    }
    
    
    
}
