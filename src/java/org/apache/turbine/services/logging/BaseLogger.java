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

import org.apache.turbine.services.resources.TurbineResources;

/** 
 * This is an abbstract class that implements Logger interface.
 * User implementation has to redefine only five methods that can
 * handle different type of destinations.
 *
 * @author <a href="mailto:Tomasz.Zielinski@e-point.pl">Tomasz Zielinski</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
public abstract class BaseLogger 
    implements Logger
{
    /** Current log level for logger */
    protected int logLevel;

    /** Name of the logger */
    protected String name;

    /** Store status of the logger */
    protected boolean initialize;

    /** Extracts data from RunData */
    protected RunDataFilter runDataFilter = null;

    /** The LoggingConfig object for this logger */
    protected LoggingConfig loggingConfig = null;

    /** flag is set when console writing is allowed */
    protected boolean console = false;

    /**
     * Default Constructor
     */
    public BaseLogger()
    {
        logLevel = DEBUG;
        name = null;
        initialize = false;
        runDataFilter = new BaseRunDataFilter();
    }

    /** 
     * This method should be reimplemented by user if class use need 
     * some objects intialization.
     *
     * @param loggingConfig Configuration describing the logger.
     */
    public void init(LoggingConfig loggingConfig)
    {
        this.loggingConfig = loggingConfig;
        doBaseInit(loggingConfig);     
        
        // Initialization of objects goes here
        doDispatch(loggingConfig);
    }

    /** 
     * Starts configuration of the logger, sets name, format, loging level
     * (defaults to <code>DEBUG</code>).
     *
     * @param loggingConfig Configuration describing the logger.
     */
    private void doBaseInit(LoggingConfig loggingConfig)
    {
        setName(loggingConfig.getName());
        setLogLevel(loggingConfig.getLevel());
        setFormat(loggingConfig.getFormat());
    }

    /** 
     * Dispatches tasks for different types of destinations.
     * Checks if the logger were configure properly
     *
     * @param loggingConfig Configuration describing the logger.
     */
    protected void doDispatch(LoggingConfig loggingConfig)
    {
        configureFiles(loggingConfig);
        configureConsole(loggingConfig);
        configureRemote(loggingConfig);
        configureSyslog(loggingConfig);
        configureEmail(loggingConfig);
        configureDatabase(loggingConfig);

        //chcecking configuration
        initialize=checkLogger();
    }

    /** Returns logger's name */
    public String getName()
    {
        return name;
    }

    /** Sets the name of the logger */
    public void setName(String logName)
    {
        name = logName;
    }

    /** 
     * Sets format output of the <code>Rundata</code>.
     * Format style is defined in the BaseRunDataFilter class.
     *
     * @see org.apache.turbine.services.logging.BaseRunDataFilter
     *
     * @param format Text describing which data should be extracted from
     *               RunData
     */
    public void setFormat(String format)
    {
        runDataFilter.setFormat(format);
    }

    /**
     * Sets the logging level based on the text passed in.  Uses reasonable
     * defaults if passed bogus data.  Delegates to
     * <code>setLogLevel(int)</code>.
     *
     * @param level The logging level represented as text.
     */
    protected void setLogLevel(String level)
    {
        if (level != null && !level.trim().equals(""))
        {
            int newLevel = DEBUG;
            level = level.toUpperCase();
            if (level.equals(LEVELINFO))
            {
                newLevel = INFO;
            }
            else if (level.equals(LEVELWARN))
            {
                newLevel = WARN;
            }
            else if (level.equals(LEVELERROR))
            {
                newLevel = ERROR;
            }

            setLogLevel(newLevel);
        }
    }

    /**
     * Sets the logging level based on the numeric level passed in.  Uses 
     * reasonable defaults if passed bogus data.
     *
     * @param level The logging level.
     */
    public void setLogLevel(int level)
    {
        if (level < DEBUG)
        {
            level = DEBUG;
        }
        else if (level > ERROR)
        {
            level = ERROR;
        }

        logLevel = level;
    }

    /**
     * Checks if DEBUG statements are enabled.
     */
    public boolean isDebugEnabled()
    {
        return (logLevel == DEBUG);
    }

    /**
     * Checks if INFO statements are enabled.
     */
    public boolean isInfoEnabled()
    {
        return (logLevel <= INFO);
    }

    /**
     * Checks if WARN statements are enabled.
     */
    public boolean isWarnEnabled()
    {
        return (logLevel <= WARN);
    }

    /**
     * Checks if ERROR statements are enabled.
     */
    public boolean isErrorEnabled()
    {
        return (logLevel <= ERROR);
    }

    /**
     * This method should be implemented by user if the logger can handle files.
     * It adds local file as destinations for logger.
     *
     * @param LoggingConfig configuration
     */
    protected void configureFiles(LoggingConfig loggingConfig)
    {
    }

    /** 
     * This method should be implemented by user if the logger can handle
     * remote server.  It adds remote servers as destinations for logger.
     *
     * @param loggingConfig Configuration describing the logger.
     */
    protected void configureConsole(LoggingConfig loggingConfig)
    {
    }

    /** 
     * This method should be implemented by user if the logger can handle
     * console.  It adds console as a destination for logger.
     *
     * @param loggingConfig Configuration describing the logger.
     */
    protected void configureRemote(LoggingConfig loggingConfig)
    {
    }

    /** 
     * This method should be implemented by user if the logger can handle
     * syslog demon.  It adds syslog demon as destinations for logger.
     *
     * @param loggingConfig Configuration describing the logger.
     */
    protected void configureSyslog(LoggingConfig loggingConfig)
    {
    }

    /** 
     * This method should be implemented by user if the logger can handle
     * emailing logs.  It adds email as a destination for logger.
     *
     * @param loggingConfig Configuration describing the logger.
     */
    protected void configureEmail(LoggingConfig loggingConfig)
    {
    }

    /** 
     * This method should be implemented by user if the logger can handle
     * database logs.  It adds a db as a destination for logger.
     *
     * @param loggingConfig Configuration describing the logger.
     */
    protected void configureDatabase(LoggingConfig loggingConfig)
    {
    }

    /** 
     * This method should be implemented by user. 
     * It performs action that are need for deterimne whether
     * logger was well configured or has any output
     */
    public abstract boolean checkLogger();
}
