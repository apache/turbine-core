package org.apache.turbine.services.schedule;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.TurbineConstants;

import org.apache.turbine.modules.ScheduledJobLoader;

/**
 * Wrapper for a <code>JobEntry</code> to actually perform the job's action.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class WorkerThread
    implements Runnable
{
    /**
     * The <code>JobEntry</code> to run.
     */
    private JobEntry je = null;

    /** Logging class from commons.logging */
    private static Log log = LogFactory.getLog(TurbineConstants.SCHEDULER_LOG_FACILITY);

    /**
     * Creates a new worker to run the specified <code>JobEntry</code>.
     *
     * @param je The <code>JobEntry</code> to create a worker for.
     */
    public WorkerThread(JobEntry je)
    {
        this.je = je;
    }

    /**
     * Run the job.
     */
    public void run()
    {
        if (je == null || je.isActive())
            return;

        try
        {
            if (! je.isActive())
            {
                je.setActive(true);
                logStateChange("started");
                ScheduledJobLoader.getInstance().exec(je, je.getTask());
            }
        }
        catch (Exception e)
        {
            log.error("Error in WorkerThread for sheduled job #" + 
                      je.getPrimaryKey() + ", task: " + je.getTask(), e);
        }
        finally
        {
            if (je.isActive())
            {
                je.setActive(false);
                logStateChange("completed");
            }
        }
    }

    /**
     * Macro to log <code>JobEntry</code> status information.
     *
     * @param state The new state of the <code>JobEntry</code>.
     */
    private final void logStateChange(String state)
    {
        log.debug("Scheduled job #" + je.getPrimaryKey() + ' ' + state + 
                  ", task: " + je.getTask());
    }
}
