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

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.turbine.services.TurbineServices;

/**
 * An adapter for integrating external code (e.g., J2EE) that expects a simple
 * PrintWriter for logging.
 *
 * @author <a href="mailto:chrise@scardini.com">Christopher Elkins</a>
 * @version $Id$
 */
public class LogWriter extends PrintWriter
{
    private LoggingService logger;

    /**
     * Default constructor.
     */
    public LogWriter()
    {
        // We don't care what the parent class is doing. This class would
        // seem a lot less silly if PrintWriter were an interface. Oh, well.
        super(new StringWriter());

        logger = (LoggingService)TurbineServices.getInstance()
            .getService(LoggingService.SERVICE_NAME);
    }

    /**
     * Logs the specified message.
     */
    private void log(String message)
    {
        logger.info(message);
    }

    /**
     * Flush the stream.
     */
    public void flush()
    {
        // do nothing
    }

    /**
     * Close the stream.
     */
    public void close()
    {
        logger.shutdown();
    }

    /**
     * Flush the stream and check its error state.
     */
    public boolean checkError()
    {
        // false indicates lack of error
        return false;
    }

    /**
     * Write a single character.
     */
    public void write(int c)
    {
        log(String.valueOf(c));
    }

    /**
     * Write a portion of an array of characters.
     */
    public void write(char buf[], int off, int len)
    {
        log(String.valueOf(buf, off, len));
    }

    /**
     * Write an array of characters.
     */
    public void write(char buf[])
    {
        log(String.valueOf(buf));
    }

    /**
     * Write a portion of a string.
     */
    public void write(String s, int off, int len)
    {
        log(s.substring(off, off + len));
    }

    /**
     * Write a string.
     */
    public void write(String s)
    {
        log(s);
    }

    /**
     * Print a boolean.
     */
    public void print(boolean b)
    {
        log(String.valueOf(b));
    }

    /**
     * Print a character.
     */
    public void print(char c)
    {
        log(String.valueOf(c));
    }

    /**
     * Print an integer.
     */
    public void print(int i)
    {
        log(String.valueOf(i));
    }

    /**
     * Print a long integer.
     */
    public void print(long l)
    {
        log(String.valueOf(l));
    }

    /**
     * Print a floating-point number.
     */
    public void print(float f)
    {
        log(String.valueOf(f));
    }

    /**
     * Print a double-precision floating-point number.
     */
    public void print(double d)
    {
        log(String.valueOf(d));
    }

    /**
     * Print an array of characters.
     */
    public void print(char s[])
    {
        log(String.valueOf(s));
    }

    /**
     * Print a string.
     */
    public void print(String s)
    {
        log(s);
    }

    /**
     * Print an object.
     */
    public void print(Object obj)
    {
        log(String.valueOf(obj));
    }

    /**
     * Terminate the current line by writing the line separator string.
     */
    public void println()
    {
        // do nothing
    }

    /**
     * Print a boolean value and then terminate the line.
     */
    public void println(boolean x)
    {
        log(String.valueOf(x));
    }

    /**
     * Print a character and then terminate the line.
     */
    public void println(char x)
    {
        log(String.valueOf(x));
    }

    /**
     * Print an integer and then terminate the line.
     */
    public void println(int x)
    {
        log(String.valueOf(x));
    }

    /**
     * Print a long integer and then terminate the line.
     */
    public void println(long x)
    {
        log(String.valueOf(x));
    }

    /**
     * Print a floating-point number and then terminate the line.
     */
    public void println(float x)
    {
        log(String.valueOf(x));
    }

    /**
     * Print a double-precision floating-point number and then terminate the 
     * line.
     */
    public void println(double x)
    {
        log(String.valueOf(x));
    }

    /**
     * Print an array of characters and then terminate the line.
     */
    public void println(char x[])
    {
        log(String.valueOf(x));
    }

    /**
     * Print a String and then terminate the line.
     */
    public void println(String x)
    {
        log(x);
    }

    /**
     * Print an Object and then terminate the line.
     */
    public void println(Object x)
    {
        log(String.valueOf(x));
    }

    /**
     * This method returns default logger for Turbine System
     *
     * @return The default logger for system.
     */
    public Logger getLogger()
    {
        return logger.getLogger();
    }

    /**
     * This method returns logger with given name if such logger exsists,
     * or the default logger.
     */
    public Logger getLogger(String logName)
    {
        return logger.getLogger(logName);
    }

    /**
     * This method sets the log level in default logger
     */
    public void setLogLevel(int level)
    {
        logger.setLogLevel(level);
    }

    /**
     * This method sets the log level in the logger of given name
     */
    public void setLogLevel(String logName, int level)
    {
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
    public void setFormat(String format)
    {
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
    public void setFormat(String logName, String format)
    {
        logger.setFormat(logName, format);
    }
}
