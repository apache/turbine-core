package org.apache.turbine.services.osgi;

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

import org.apache.commons.logging.Log;
import org.apache.felix.framework.Logger;
import org.osgi.framework.ServiceReference;

/**
 * This encapsulates a logger for OSGi components.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: OSGiComponentService.java 615328 2008-01-25 20:25:05Z tv $
 */
public class OSGiLogger
        extends Logger
{
    private Log log;
    
    /**
     * Constructor
     * 
     * @param log a Commons logger
     */
    public OSGiLogger(Log log)
    {
        super();
        this.log = log;
    }

    /**
     * @see org.apache.felix.framework.Logger#doLog(org.osgi.framework.ServiceReference, int, java.lang.String, java.lang.Throwable)
     */
    protected void doLog(ServiceReference sr, int level, String msg, Throwable throwable)
    {
        String prefix = (sr != null) ? sr.toString() : "OSGi-Framework";
        
        switch (level)
        {
            case Logger.LOG_DEBUG:
                log.debug(prefix + ": " + msg, throwable);
                break;

            case Logger.LOG_INFO:
                log.info(prefix + ": " + msg, throwable);
                break;

            case Logger.LOG_WARNING:
                log.warn(prefix + ": " + msg, throwable);
                break;

            case Logger.LOG_ERROR:
                log.error(prefix + ": " + msg, throwable);
                break;

            default:
                log.fatal(prefix + ": " + msg, throwable);
        }
    }
}
