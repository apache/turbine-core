package org.apache.turbine.pipeline;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

import java.io.IOException;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * The idea of a pipeline is being taken from Catalina
 * in its entirety :-)
 *
 * I would like to take the idea further and implement
 * Valves instead of hardcoding particular methods
 * in a pipeline.
 *
 * It would be more flexible to specify Valves for
 * a pipeline in an XML file (we can also rip off the
 * digester rules from T4) and have invoke() as part
 * of the interface.
 *
 * So a set of Valves would be added to the pipeline
 * and the pipeline would 'invoke' each valve. In the
 * case Turbine each Valve would produce some output
 * to be sent out the pipe. I think with another days
 * work this can be fully working. The first pipeline
 * to be fully implemented will the ClassicPipeline
 * will emulate the Turbine 2.1 way of doing things.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 */
public interface Pipeline
{
    /**
     * Initializes this instance.  Called once by the Turbine servlet.
     */
    public void initialize()
        throws Exception;

    /**
     * <p>Add a new Valve to the end of the pipeline.</p>
     *
     * @param valve Valve to be added.
     *
     * @exception IllegalStateException If the pipeline has not been
     * initialized.
     */
    public void addValve(Valve valve);

    /**
     * Return the set of all Valves in the pipeline.  If there are no
     * such Valves, a zero-length array is returned.
     *
     * @return An array of valves.
     */
    public Valve[] getValves();

    /**
     * <p>Cause the specified request and response to be processed by
     * the sequence of Valves associated with this pipeline, until one
     * of these Valves decides to end the processing.</p>
     *
     * <p>The implementation must ensure that multiple simultaneous
     * requests (on different threads) can be processed through the
     * same Pipeline without interfering with each other's control
     * flow.</p>
     *
     * @param data The run-time information, including the servlet
     * request and response we are processing.
     *
     * @exception IOException an input/output error occurred.
     */
    public void invoke(PipelineData data)
        throws TurbineException, IOException;

    /**
     * Remove the specified Valve from the pipeline, if it is found;
     * otherwise, do nothing.
     *
     * @param valve Valve to be removed.
     */
    public void removeValve(Valve valve);
}
