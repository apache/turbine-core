package org.apache.turbine.services.logging;

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

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;

/**
 * This is a facade class for {@link TurbineLoggingService}.
 *
 * @author <a href="mailto:Tomasz.Zielinski@e-point.pl">Tomasz Zielinski</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public class TurbineLogging
{
    /**
     * Utility method for accessing the service
     * implementation
     *
     * @return a LoggingService implementation instance
     */
    protected static LoggingService getService()
    {
        return (LoggingService)TurbineServices
            .getInstance().getService(LoggingService.SERVICE_NAME);
    }

    /**
     * Attempt to close the log files when the class is GC'd.
     */
    protected void finalize() throws Throwable
    {
        getService().shutdown();
    }

    /**
    * This metod returns default logger for Turbine System
    *
    * @return The default logger for system.
    */
    public static Logger getLogger()
    {
        return getService().getLogger();
    }

    /**
    * This metod returns logger with given name.
    */
    public static Logger getLogger(String logName)
    {
        return getService().getLogger(logName);
    }

    /**
    * This metod sets the log level in default logger
    */
    public static void setLogLevel(int level)
    {
        getService().setLogLevel(level);
    }

    /**
    * This metod sets the log level in the logger of given name
    */
    public static void setLogLevel(String logName,int level)
    {
        getService().setLogLevel(logName,level);
    }

    /**
    * This metod sets format style of the default logger
    */
    public static void setFormat(String format)
    {
        getService().setFormat(format);
    }

    /**
    * This metod sets format style of the given logger.
    */
    public static void setFormat(String logName, String format)
    {
        getService().setFormat(logName,format);
    }

    /**
    * This is a log metod with logLevel == DEBUG, printing is done by
    * the default logger
    */
    public static void debug(String message)
    {
        getService().debug(message);
    }

    /**
    * This is a log metod with logLevel == DEBUG, printing is done by
    * the default logger
    */
    public static void debug(String message, Throwable t)
    {
        getService().debug(message, t);
    }

    /**
    * This is a log metod with logLevel == DEBUG, printing is done by
    * the given logger
    */
    public static void debug(String logName, String message, Throwable t)
    {
        getService().debug(logName, message, t);
    }

    /**
    * This is a log metod with logLevel == DEBUG, printing is done by
    * the given logger
    */
    public static void debug(String logName, String message)
    {
        getService().debug(logName, message);
    }

    /**
    * This is a log metod with logLevel == DEBUG, printing is done by
    * the default logger
    */
    public static void debug(String message, RunData data)
    {
        getService().debug(message, data);
    }

    /**
    * This is a log metod with logLevel == DEBUG, printing is done by
    * the default logger
    */
    public static void debug(String message, RunData data, Throwable t)
    {
        getService().debug(message, data, t);
    }

    /**
    * This is a log metod with logLevel == DEBUG, printing is done by
    * the given logger
    */
    public static void debug(String logName, String message, RunData data, Throwable t)
    {
        getService().debug(logName, message, data, t);
    }

    /**
    * This is a log metod with logLevel == DEBUG, printing is done by
    * the given logger
    */
    public static void debug(String logName, String message, RunData data)
    {
        getService().debug(logName, message, data);
    }

    /**
    * This is a log metod with logLevel == INFO, printing is done by
    * the default logger
    */
    public static void info(String message)
    {
        getService().info(message);
    }

    /**
    * This is a log metod with logLevel == INFO, printing is done by
    * the default logger
    */
    public static void info(String message, Throwable t)
    {
        getService().info(message, t);
    }

    /**
    * This is a log metod with logLevel == INFO, printing is done by
    * the given logger
    */
    public static void info(String logName, String message)
    {
        getService().info(logName, message);
    }

    /**
    * This is a log metod with logLevel == INFO, printing is done by
    * the given logger
    */
    public static void info(String logName, String message, Throwable t)
    {
        getService().info(logName, message, t);
    }

    /**
    * This is a log metod with logLevel == INFO, printing is done by
    * the default logger
    */
    public static void info(String message, RunData data)
    {
        getService().info(message, data);
    }

    /**
    * This is a log metod with logLevel == INFO, printing is done by
    * the default logger
    */
    public static void info(String message, RunData data, Throwable t)
    {
        getService().info(message, data, t);
    }

    /**
    * This is a log metod with logLevel == INFO, printing is done by
    * the given logger
    */
    public static void info(String logName, String message, RunData data)
    {
        getService().info(logName, message, data);
    }

    /**
    * This is a log metod with logLevel == INFO, printing is done by
    * the given logger
    */
    public static void info(String logName, String message, RunData data, Throwable t)
    {
        getService().info(logName, message, data, t);
    }

    /**
    * This is a log metod with logLevel == WARN, printing is done by
    * the default logger
    */
    public static void warn(String message)
    {
        getService().warn(message);
    }

    /**
    * This is a log metod with logLevel == WARN, printing is done by
    * the default logger
    */
    public static void warn(String message, Throwable t)
    {
        getService().warn(message, t);
    }

    /**
    * This is a log metod with logLevel == WARN, printing is done by
    * the given logger
    */
    public static void warn(String logName, String message)
    {
        getService().warn(logName, message);
    }

    /**
    * This is a log metod with logLevel == WARN, printing is done by
    * the given logger
    */
    public static void warn(String logName, String message, Throwable t)
    {
        getService().warn(logName, message, t);
    }

    /**
    * This is a log metod with logLevel == WARN, printing is done by
    * the default logger
    */
    public static void warn(String message, RunData data)
    {
        getService().warn(message, data);
    }

    /**
    * This is a log metod with logLevel == WARN, printing is done by
    * the default logger
    */
    public static void warn(String message, RunData data, Throwable t)
    {
        getService().warn(message, data, t);
    }

    /**
    * This is a log metod with logLevel == WARN, printing is done by
    * the given logger
    */
    public static void warn(String logName, String message, RunData data)
    {
        getService().warn(logName, message, data);
    }

    /**
    * This is a log metod with logLevel == WARN, printing is done by
    * the given logger
    */
    public static void warn(String logName, String message, RunData data, Throwable t)
    {
        getService().warn(logName, message, data, t);
    }

    /**
    * This is a log metod with logLevel == ERROR, printing is done by
    * the default logger
    */
    public static void error(String message)
    {
        getService().error(message);
    }

    /**
    * This is a log metod with logLevel == ERROR, printing is done by
    * the default logger
    */
    public static void error(String message, Throwable t)
    {
        getService().error(message, t);
    }

    /**
    * This is a log metod with logLevel == ERROR, printing is done by
    * the given logger
    */
    public static void error(String logName, String message)
    {
        getService().error(logName, message);
    }

    /**
    * This is a log metod with logLevel == ERROR, printing is done by
    * the given logger
    */
    public static void error(String logName, String message, Throwable t)
    {
        getService().error(logName, message, t);
    }

    /**
    * This is a log metod with logLevel == ERROR, printing is done by
    * the default logger
    */
    public static void error(String message, RunData data)
    {
        getService().error(message, data);
    }

    /**
    * This is a log metod with logLevel == ERROR, printing is done by
    * the default logger
    */
    public static void error(String message, RunData data, Throwable t)
    {
        getService().error(message, data, t);
    }

    /**
    * This is a log metod with logLevel == ERROR, printing is done by
    * the given logger
    */
    public static void error(String logName, String message, RunData data)
    {
        getService().error(logName, message, data);
    }

    /**
    * This is a log metod with logLevel == ERROR, printing is done by
    * the given logger
    */
    public static void error(String logName, String message, RunData data, Throwable t)
    {
        getService().error(logName, message, data, t);
    }
}
