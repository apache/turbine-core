package org.apache.turbine.util;

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

import org.apache.commons.logging.LogFactory;
import org.apache.turbine.TurbineConstants;

/**
 * A facade class for Turbine Logging
 *
 * Use this class to conveniently access the system logging facility.
 *
 * @deprecated Use the commons.logging system for logging
 *
 * @author <a href="mailto:Tomasz.Zielinski@e-point.pl">Tomasz Zielinski</a>
 * @author <a href="mailto:hps@intermeta.de>Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class Log
{

    /**
     * This method returns default logger for Turbine System
     *
     * @return The default logger for system.
     * @deprecated Use the commons.logging system for logging
     */
    public static org.apache.commons.logging.Log getLogger()
    {
        return LogFactory.getLog(TurbineConstants.DEFAULT_LOGGER);
    }

    /**
     * This method returns logger with given name if such logger exists,
     * or the default logger.
     *
     * @return The default logger for system.
     * @deprecated Use the commons.logging system for logging
     */
    public static org.apache.commons.logging.Log getLogger(String logName)
    {
        org.apache.commons.logging.Log log = LogFactory.getLog(logName);

        if(log == null)
        {
            log = getLogger();
        }
        return log;
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the default logger
     *
     * @deprecated Use the commons.logging system for logging
     */
    public static void debug(String message)
    {
        getLogger().debug(message);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the default logger
     *
     * @deprecated Use the commons.logging system for logging
     */
    public static void debug(String message, Throwable t)
    {
        getLogger().debug(message, t);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the given logger
     *
     * @deprecated Use the commons.logging system for logging
     */
    public static void debug(String logName ,String message, Throwable t)
    {
        getLogger(logName).debug(message, t);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the given logger
     *
     * @deprecated Use the commons.logging system for logging
     */
    public static void debug(String logName, String message)
    {
        getLogger(logName).debug(message);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the default logger
     *
     * @deprecated Use the commons.logging system for logging
     */
    public static void info(String message)
    {
        getLogger().info(message);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the default logger
     *
     * @deprecated Use the commons.logging system for logging
     */
    public static void info(String message, Throwable t)
    {
        getLogger().info(message, t);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the given logger
     *
     * @deprecated Use the commons.logging system for logging
     */
    public static void info(String logName, String message)
    {
        getLogger(logName).info(message);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the given logger
     *
     * @deprecated Use the commons.logging system for logging
     */
    public static void info(String logName, String message, Throwable t)
    {
        getLogger(logName).info(message, t);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the default logger
     *
     * @deprecated Use the commons.logging system for logging
     */
    public static void warn(String message)
    {
        getLogger().warn(message);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the default logger
     *
     * @deprecated Use the commons.logging system for logging
     */
    public static void warn(String message, Throwable t)
    {
        getLogger().warn(message, t);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the given logger
     *
     * @deprecated Use the commons.logging system for logging
     */
    public static void warn(String logName, String message)
    {
        getLogger(logName).warn(message);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the given logger
     *
     * @deprecated Use the commons.logging system for logging
     */
    public static void warn(String logName, String message, Throwable t)
    {
        getLogger(logName).warn(message, t);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     *
     * @deprecated Use the commons.logging system for logging
     */
    public static void error(String message)
    {
        getLogger().error(message);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     *
     * @deprecated Use the commons.logging system for logging
     */
    public static void error(String message, Throwable t)
    {
        getLogger().error(message, t);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the given logger
     *
     * @deprecated Use the commons.logging system for logging
     */
    public static void error(String logName, String message)
    {
        getLogger(logName).error(message);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the given logger
     *
     * @deprecated Use the commons.logging system for logging
     */
    public static void error(String logName, String message, Throwable t)
    {
        getLogger(logName).error(message, t);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     *
     * @deprecated Use the commons.logging system for logging
     */
    public static void error(Throwable e)
    {
        error("", e);
    }
}
