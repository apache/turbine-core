package org.apache.turbine.pipeline;

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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.uri.URIConstants;

/**
 * This valve is responsible for setting the 'target' property of the RunData.
 * If it is not already set it attempts to get the target from the request
 * parameter 'template'. If the parameter is not set, we use the homepage
 * specified by the configuration property Turbine.TEMPLATE_HOMEPAGE.
 *
 * FIXME: The request parameter which determines the template should be
 *        configurable. 
 *
 * @author <a href="mailto:james@jamestaylor.org">James Taylor</a>
 */
public class DetermineTargetValve 
    extends AbstractValve
{
    private static final Log log
        = LogFactory.getLog( DetermineTargetValve.class );
        
    /**
     * @see org.apache.turbine.Valve#invoke(RunData, ValveContext)
     */
    public void invoke( PipelineData pipelineData, ValveContext context )
        throws IOException, TurbineException
    {
        RunData runData = (RunData)pipelineData.get(RunData.class);
        if ( ! runData.hasScreen() )
        {
            String target = runData.getParameters().getString(URIConstants.CGI_SCREEN_PARAM);

            if ( target != null )
            {
                runData.setScreen( target );
                
                log.debug( "Set screen target from request parameter" );
            }
            else
            {
            /*    data.setScreen( Turbine.getConfiguration().getString(
                    Turbine.TEMPLATE_HOMEPAGE ) );
                    
                log.debug( "Set target using default value" );
                */
				log.debug( "No target screen" );
            }
            
        }
        
        if ( log.isDebugEnabled() )
        {
            log.debug( "Screen Target is now: " + runData.getScreen() );
        }

        // Pass control to the next Valve in the Pipeline
        context.invokeNext( pipelineData );
    }
}
