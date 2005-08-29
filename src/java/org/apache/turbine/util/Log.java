package org.apache.turbine.util;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.logging.LogFactory;
import org.apache.turbine.TurbineConstants;

/**
 * A facade class for Turbine Logging
 *
 * Use this class to conveniently access the system logging facility.
 *
 * @author <a href="mailto:Tomasz.Zielinski@e-point.pl">Tomasz Zielinski</a>
 * @author <a href="mailto:hps@intermeta.de>Henning P. Schmiedehausen</a>
 * @version $Id$
 * @deprecated Use the commons.logging system for logging
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

        if (log == null)
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
    public static void debug(String logName, String message, Throwable t)
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
