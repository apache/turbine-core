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

import org.apache.turbine.services.Service;
import org.apache.turbine.util.RunData;

/**
 * A service that provides logging capabilities to Turbine.
 * <p>
 * This interface specifies the various possibilities of
 * log message writing that are available to Turbine components
 * and applications.
 *
 * @see org.apache.turbine.services.logging.Logger
 * @author <a href="mailto:Tomasz.Zielinski@e-point.pl">Tomasz Zielinski</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public interface LoggingService extends Service
{
    /** The name of the service */
    public static final String SERVICE_NAME = "LoggingService";

    /**
     * This method returns default logger for Turbine System
     */
    public Logger getLogger();

    /**
     * This method returns logger with given name or default logger if
     * the logger of specific name can't be found.
     */
    public Logger getLogger(String logName);

    /**
     * This method sets the log level in default logger
     */
    public void setLogLevel(int level);

    /**
     * This method sets the log level in the logger of given name
     */
    public void setLogLevel(String logName, int level);

    /**
     * This method sets format style of the default logger
     */
    public void setFormat(String format);

    /**
     * This method sets format style of the given logger.
     */
    public void setFormat(String logName, String format);


    /**
     * This is a log method with logLevel == DEBUG,printing is done by
     * the default logger
     */
    public void debug(String message);

    /**
     * This is a log method with logLevel == DEBUG,printing is done by
     * the default logger
     */
    public void debug(String message, Throwable t);

    /**
     * This is a log method with logLevel == DEBUG,printing is done by
     * the given logger
     */
    public void debug(String logName, String message, Throwable t);

    /**
     * This is a log method with logLevel == DEBUG,printing is done by
     * the given logger
     */
    public void debug(String logName, String message);

    /**
     * This is a log method with logLevel == DEBUG,printing is done by
     * the default logger
     */
    public void debug(String message, RunData data);

    /**
     * This is a log method with logLevel == DEBUG,printing is done by
     * the default logger
     */
    public void debug(String message, RunData data, Throwable t);

    /**
     * This is a log method with logLevel == DEBUG,printing is done by
     * the given logger
     */
    public void debug(String logName, String message, RunData data,
                      Throwable t);

    /**
     * This is a log method with logLevel == DEBUG,printing is done by
     * the given logger
     */
    public void debug(String logName, String message, RunData data);

    /**
     * This is a log method with logLevel == INFO,printing is done by
     * the default logger
     */
    public void info(String message);

    /**
     * This is a log method with logLevel == INFO,printing is done by
     * the default logger
     */
    public void info(String message, Throwable t);

    /**
     * This is a log method with logLevel == INFO,printing is done by
     * the given logger
     */
    public void info(String logName, String message);

    /**
     * This is a log method with logLevel == INFO,printing is done by
     * the given logger
     */
    public void info(String logName, String message, Throwable t);

    /**
     * This is a log method with logLevel == INFO,printing is done by
     * the default logger
     */
    public void info(String message, RunData data);

    /**
     * This is a log method with logLevel == INFO,printing is done by
     * the default logger
     */
    public void info(String message, RunData data, Throwable t);

    /**
     * This is a log method with logLevel == INFO,printing is done by
     * the given logger
     */
    public void info(String logName, String message, RunData data);

    /**
     * This is a log method with logLevel == INFO,printing is done by
     * the given logger
     */
    public void info(String logName, String message, RunData data, Throwable t);

    /**
     * This is a log method with logLevel == WARN,printing is done by
     * the default logger
     */
    public void warn(String message);

    /**
     * This is a log method with logLevel == WARN,printing is done by
     * the default logger
     */
    public void warn(String message, Throwable t);

    /**
     * This is a log method with logLevel == WARN,printing is done by
     * the given logger
     */
    public void warn(String logName, String message);

    /**
     * This is a log method with logLevel == WARN,printing is done by
     * the given logger
     */
    public void warn(String logName, String message, Throwable t);

    /**
     * This is a log method with logLevel == WARN,printing is done by
     * the default logger
     */
    public void warn(String message, RunData data);

    /**
     * This is a log method with logLevel == WARN,printing is done by
     * the default logger
     */
    public void warn(String message, RunData data, Throwable t);

    /**
     * This is a log method with logLevel == WARN,printing is done by
     * the given logger
     */
    public void warn(String logName, String message, RunData data);

    /**
     * This is a log method with logLevel == WARN,printing is done by
     * the given logger
     */
    public void warn(String logName, String message, RunData data, Throwable t);

    /**
     * This is a log method with logLevel == ERROR,printing is done by
     * the default logger
     */
    public void error(String message);

    /**
     * This is a log method with logLevel == ERROR,printing is done by
     * the default logger
     */
    public void error(String message, Throwable t);

    /**
     * This is a log method with logLevel == ERROR,printing is done by
     * the given logger
     */
    public void error(String logName, String message);

    /**
     * This is a log method with logLevel == ERROR,printing is done by
     * the given logger
     */
    public void error(String logName, String message, Throwable t);

    /**
     * This is a log method with logLevel == ERROR,printing is done by
     * the default logger
     */
    public void error(String message, RunData data);

    /**
     * This is a log method with logLevel == ERROR,printing is done by
     * the default logger
     */
    public void error(String message, RunData data, Throwable t);

    /**
     * This is a log method with logLevel == ERROR,printing is done by
     * the given logger
     */
    public void error(String logName, String message, RunData data);

    /**
     * This is a log method with logLevel == ERROR,printing is done by
     * the given logger
     */
    public void error(String logName, String message, RunData data,
                      Throwable t);
}
