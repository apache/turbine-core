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

import org.apache.turbine.util.RunData;

/**
 * Classes that implement the Logger interface allows loging.
 * There is set of standart printing methods (info, debug ...).
 * The implementation has to read xml-node describing properities,
 * skiping options that are not recognizeable.
 *
 * <p>Uh, we need better javadoc here (Rafal)<br>
 *
 * @author <a href="mailto:Tomasz.Zielinski@e-point.pl">Tomasz Zielinski</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public interface Logger
{
    /**Log level*/
    public static final int DEBUG = 1;
    /**Log level*/
    public static final int INFO = 2;
    /**Log level*/
    public static final int WARN = 3;
    /**Log level*/
    public static final int ERROR = 4;

    /** String denoting log level */
    public static final String LEVELDEBUG = "DEBUG";
    /** String denoting log level */
    public static final String LEVELINFO = "INFO";
    /** String denoting log level */
    public static final String LEVELWARN = "WARN";
    /** String denoting log level */
    public static final String LEVELERROR = "ERROR";

    /** Destination type - file */
    public static final String FILE_KEY = "file";
    /** Destination type - syslogdemon */
    public static final String SYSLOGD_KEY = "syslogd";
    /** Destination type - remote server */
    public static final String REMOTE_KEY = "remote";
    /** Destination type - console */
    public static final String CONSOLE_KEY = "console";
    /** Destination type - email */
    public static final String EMAIL_KEY = "email";
    /** Destination type - db */
    public static final String DB_KEY = "database";

    /** Destination parameter - format */
    public static final String FORMAT_KEY = "format";
    /** Destination parameter - file path */
    public static final String PATH_KEY = "path";
    /** Destination parameter - remote url */
    public static final String HOST_KEY = "host";
    /** Destination parameter - remote port */
    public static final String PORT_KEY = "port";
    /** Destination parameter - syslogd facility */
    public static final String FACILITY_KEY = "facility";
    /** Destination parameter - rollover file size */
    public static final String SIZE_KEY = "file.size";
    /** Destination parameter - number of backup files */
    public static final String BACKUP_KEY = "file.backups";
    /** Destination parameter - email from */
    public static final String EMAILFROM_KEY = "from";
    /** Destination parameter - email to */
    public static final String EMAILTO_KEY = "to";
    /** Destination parameter - email subject */
    public static final String EMAILSUBJECT_KEY = "subject";
    /** Destination parameter - email buffer size */
    public static final String EMAILBUFFERSIZE_KEY = "buffer.size";
    /** Destination parameter - db sql */
    public static final String DB_LOGGER_KEY = "logger";
    public static final String DB_POOL_KEY = "pool";

    /** name of the logger */
    public String getName();

    /** Setings the name */
    public void setName(String logName);

    /**
     * This method sets parameters for the logger implementation.
     * If the implementation cannot handle some type of destination should ignore
     * that output.
     *
     * @param LoggingConfig configuration object for logging
     */
    public void init(LoggingConfig loggingConfig);

    /** Close all destinations*/
    public void shutdown();

    /**
     * Sets log level for the logger
     */
    public void setLogLevel(int level);

    /**
     * Checks if DEBUG statements are enabled.
     */
     public boolean isDebugEnabled();

    /**
     * Checks if INFO statements are enabled.
     */
     public boolean isInfoEnabled();

    /**
     * Checks if WARN statements are enabled.
     */
     public boolean isWarnEnabled();

    /**
     * Checks if ERROR statements are enabled.
     */
     public boolean isErrorEnabled();

    /**
     * This method should be implemented by user.
     * It performs action that are need for deterimne whether
     * logger was well configured or has any output
     */
    public boolean checkLogger();

    /**
     * Sets format style for extracting data from RunData
     */
    public void setFormat(String format);

    /**
     * This is a log metod with logLevel == DEBUG
     */
    public void debug(String message);

    /**
     * This is a log metod with logLevel == DEBUG
     */
    public void debug(String message, Throwable t);

    /**
     * This is a log metod with logLevel == DEBUG
     */
    public void debug(String message, RunData data);

    /**
     * This is a log metod with logLevel == DEBUG
     */
    public void debug(String message, RunData data, Throwable t);

    /**
     * This is a log metod with logLevel == INFO
     */
    public void info(String message);

    /**
     * This is a log metod with logLevel == INFO
     */
    public void info(String message, Throwable t);

    /**
     * This is a log metod with logLevel == INFO
     */
    public void info(String message, RunData data);

    /**
     * This is a log metod with logLevel == INFO
     */
    public void info(String message, RunData data, Throwable t);

    /**
     * This is a log metod with logLevel == WARN
     */
    public void warn(String message);

    /**
     * This is a log metod with logLevel == WARN
     */
    public void warn(String message, Throwable t);

    /**
     * This is a log metod with logLevel == WARN
     */
    public void warn(String message, RunData data);

    /**
     * This is a log metod with logLevel == WARN
     */
    public void warn(String message, RunData data, Throwable t);

    /**
     * This is a log metod with logLevel == ERROR
     */
    public void error(String message);

    /**
     * This is a log metod with logLevel == ERROR
     */
    public void error(String message, Throwable e);

    /**
     * This is a log metod with logLevel == ERROR
     */
    public void error(Throwable e);

    /**
     * This is a log metod with logLevel == ERROR
     */
    public void error(String message, RunData data);

    /**
     * This is a log metod with logLevel == ERROR
     */
    public void error(String message, RunData data, Throwable e);
}
