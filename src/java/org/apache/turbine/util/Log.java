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

import org.apache.turbine.services.Service;
import org.apache.turbine.services.ServiceBroker;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.logging.Logger;
import org.apache.turbine.services.logging.LoggingService;

/**
 * A facade class for {@link org.apache.turbine.services.logging.LoggingService}.
 *
 * Use this class to conviniently access the system's configured LoggingSerice.
 * <P>
 * @see org.apache.turbine.services.logging.TurbineLoggingService
 * @author <a href="mailto:Tomasz.Zielinski@e-point.pl">Tomasz Zielinski</a>
 */
public class Log
{
    /**
     * This method has been deprecated, attempts to shutdown logger service.
     * @deprecated The service should be shut down by the broker class only.
     */
    public static void destroy()
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.shutdown();
    }

    /**
     * This method returns default logger for Turbine System
     *
     * @return The default logger for system.
     */
    public static Logger getLogger()
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        return logger.getLogger();
    }

    /**
     * This method returns logger with given name if such logger exsists,
     * or the default logger.
     */
    public static Logger getLogger(String logName)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        return logger.getLogger(logName);
    }

    /**
     * This method sets the log level in default logger
     */
    public static void setLogLevel(int level)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.setLogLevel(level);
    }

    /**
     * This method sets the log level in the logger of given name
     */
    public static void setLogLevel(String logName, int level)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.setLogLevel(logName, level);
    }

    /**
     * This method sets format style of the default logger.
     * Format tokens are described in RunDataFilter implementation.
     *
     * @see org.apache.turbine.services.logging.BaseRunDataFilter
     *
     * @param format String describing what information should be extracted from
     *        RunData
     */
    public static void setFormat(String format)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.setFormat(format);
    }

    /**
     * This method sets format style of the given logger.
     * Format tokens are described in RunDataFilter implementation.
     *
     * @see org.apache.turbine.services.logging.BaseRunDataFilter
     *
     * @param format String describing what information should be extracted from
     *        RunData
     */
    public static void setFormat(String logName, String format)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.setFormat(logName, format);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the default logger
     */
    public static void debug(String message)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.debug(message);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the default logger
     */
    public static void debug(String message, Throwable t)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.debug(message, t);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the given logger
     */
    public static void debug(String logName ,String message, Throwable t)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.debug(logName, message, t);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the given logger
     */
    public static void debug(String logName, String message)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.debug(logName, message);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the default logger
     */
    public static void debug(String message, RunData data)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.debug(message, data);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the default logger
     */
    public static void debug(String message, RunData data, Throwable t)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.debug(message, data, t);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the given logger
     */
    public static void debug(String logName,
                            String message,
                            RunData data,
                            Throwable t)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.debug(logName, message, data, t);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the given logger
     */
    public static void debug(String logName, String message, RunData data)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.debug(logName, message, data);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the default logger
     */
    public static void info(String message)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.info(message);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the default logger
     */
    public static void info(String message, Throwable t)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.info(message, t);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the given logger
     */
    public static void info(String logName, String message)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.info(logName, message);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the given logger
     */
    public static void info(String logName, String message, Throwable t)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.info(logName, message, t);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the default logger
     */
    public static void info(String message, RunData data)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.info(message, data);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the default logger
     */
    public static void info(String message, RunData data, Throwable t)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.info(message, data, t);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the given logger
     */
    public static void info(String logName, String message, RunData data)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.info(logName, message, data);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the given logger
     */
    public static void info(String logName,
                            String message,
                            RunData data,
                            Throwable t)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.info(logName, message, data, t);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the default logger
     */
    public static void warn(String message)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.warn(message);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the default logger
     */
    public static void warn(String message, Throwable t)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.warn(message, t);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the given logger
     */
    public static void warn(String logName, String message)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.warn(logName, message);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the given logger
     */
    public static void warn(String logName, String message, Throwable t)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.warn(logName, message, t);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the default logger
     */
    public static void warn(String message, RunData data)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.warn(message, data);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the default logger
     */
    public static void warn(String message, RunData data, Throwable t)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.warn(message, data, t);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the given logger
     */
    public static void warn(String logName, String message, RunData data)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.warn(logName, message, data);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the given logger
     */
    public static void warn(String logName,
                            String message,
                            RunData data,
                            Throwable t)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.warn(logName, message, data, t);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     */
    public static void error(String message)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.error(message);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     */
    public static void error(String message, Throwable t)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.error(message, t);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the given logger
     */
    public static void error(String logName, String message)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.error(logName, message);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the given logger
     */
    public static void error(String logName, String message, Throwable t)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.error(logName, message, t);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     */
    public static void error(String message, RunData data)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.error(message, data);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     */
    public static void error(String message, RunData data, Throwable t)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.error(message, data, t);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the given logger
     */
    public static void error(String logName, String message, RunData data)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.error(logName, message, data);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the given logger
     */
    public static void error(String logName,
                            String message,
                            RunData data,
                            Throwable t)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.error(logName, message, data, t);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     */
    public static void error(Throwable e)
    {
        error("", e);
    }

    /**
     * This method has been deprecated.
     * This is method is kept for historical reason.
     *
     * @deprecated You should use info or debug methods instead.
     */
    public static void note(String message)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.info(message);
    }

    /**
     * This method has been deprecated.
     * This is method is kept for historical reason.
     *
     * @deprecated You should use info or debug methods instead.
     */
    public static void note(String logName, String message)
    {
        LoggingService logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
        logger.info(logName, message);
    }
}
