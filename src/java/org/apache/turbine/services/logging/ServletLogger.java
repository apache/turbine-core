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

import javax.servlet.ServletContext;
import org.apache.turbine.util.RunData;

/**
 * This class implements Logger interface using log method from ServletContext.
 * This implementation is very simple, so there is no extracting data from RunData,
 * and no log levels.
 *
 * @see org.apache.turbine.services.logging.Logger
 * @author <a href="mailto:Tomasz.Zielinski@e-point.pl">Tomasz Zielinski</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public class ServletLogger extends BaseLogger
{
    /** context for calling method: "log" */
    protected ServletContext context=null;

    protected String lf = System.getProperty("line.separator", "\n");

    public ServletLogger()
    {
        super();
    }

    /** Initialize*/
    public void init(LoggingConfig loggingConfig)
    {
        this.context = (ServletContext) loggingConfig.getServletContext();
    }

    /** Empty method*/
    public void shutdown()
    {
    }

    /**
     * It performs action that are need for deterimne whether
     * logger was well configured or has any output
     */
    public boolean checkLogger()
    {
        return (true);
    }

    /**
     * This is a log method with logLevel == DEBUG
     */
    public void debug(String message)
    {
        log(DEBUG, message, null, null);
    }

    /**
     * This is a log method with logLevel == DEBUG
     */
    public void debug(String message, Throwable t)
    {
        log(DEBUG, message, null, t);
    }

    /**
     * This is a log method with logLevel == DEBUG
     */
    public void debug(String message, RunData data)
    {
        log(DEBUG, message, data, null);
    }

    /**
     * This is a log method with logLevel == DEBUG
     */
    public void debug(String message, RunData data, Throwable t)
    {
        log(DEBUG, message, data, t);
    }

    /**
     * This is a log method with logLevel == INFO
     */
    public void info(String message)
    {
        log(INFO, message, null, null);
    }

    /**
     * This is a log method with logLevel == INFO
     */
    public void info(String message, Throwable t)
    {
        log(INFO, message, null, t);
    }

    /**
     * This is a log method with logLevel == INFO
     */
    public void info(String message, RunData data)
    {
        log(INFO, message, data, null);
    }

    /**
     * This is a log method with logLevel == INFO
     */
    public void info(String message, RunData data, Throwable t)
    {
        log(INFO, message, data, t);
    }

    /**
     * This is a log method with logLevel == WARN
     */
    public void warn(String message)
    {
        log(WARN, message, null, null);
    }

    /**
     * This is a log method with logLevel == WARN
     */
    public void warn(String message, Throwable t)
    {
        log(WARN, message, null, t);
    }

    /**
     * This is a log method with logLevel == WARN
     */
    public void warn(String message, RunData data)
    {
        log(WARN, message, data, null);
    }

    /**
     * This is a log method with logLevel == WARN
     */
    public void warn(String message, RunData data, Throwable t)
    {
        log(WARN, message, data, t);
    }

    /**
     * This is a log method with logLevel == ERROR
     */
    public void error(String message)
    {
        log(ERROR, message, null, null);
    }

    public void error(String message, Throwable e)
    {
        log(ERROR, message, null, e);

    }

    public void error(Throwable e)
    {
        log(ERROR, null, null, e);
    }

    public void error(String message, RunData data)
    {
        log(ERROR, message, data, null);
    }

    public void error(String message, RunData data, Throwable e)
    {
        log(ERROR, message, data, e);
    }

    /** appends log level to the message */
    protected void log(int level, String message, RunData data, Throwable e)
    {
        String levelS;
        switch (level)
        {
            case DEBUG: levelS = LEVELDEBUG; break;
            case INFO:  levelS = LEVELINFO;  break;
            case WARN:  levelS = LEVELWARN;  break;
            case ERROR: levelS = LEVELERROR; break;
            default: levelS = LEVELDEBUG;
        }

        logAll(levelS, message, data, e);
    }

    /** log message using context log method. */
    protected void logAll(String level, String description, RunData data,
            Throwable t)
    {

        boolean odp=true;

        StringBuffer logEntry = new StringBuffer();
        logEntry.append ( " -- " );
        logEntry.append ( level );
        logEntry.append ( " -- " );
        logEntry.append ( description );

        if (t != null)
        {
            context.log(logEntry.toString(), t);
        }
        else
        {
            context.log(logEntry.toString());
        }
    }
}
