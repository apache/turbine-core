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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.servlet.ServletContext;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Category;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.net.SMTPAppender;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.net.SyslogAppender;
import org.apache.turbine.services.logging.jdbc.JDBCAppender;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.util.RunData;

/**
 * Class implements the Logger interface using log4java package
 * Messages can be written to following destination:
 * <ul>
 * <li>console</li>
 * <li>file</li>
 * <li>rollover file</li>
 * <li>syslog</li>
 * <li>remote server</li>
 * <li>email</li>
 * <li>database</li>
 * </ul>
 *
 * @author <a href="mailto:Tomasz.Zielinski@e-point.pl">Tomasz Zielinski</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id$
 */
public class Log4JavaLogger extends BaseLogger
{
    /** global files appenders table */
    protected static Hashtable filesTable;

    /** console appender */
    protected static Appender consoleAppender = null;

    /** log4java logging interface */
    protected Category logger;

    /** logging layout */
    protected Layout layout;

    /** global initialization */
    static
    {
        filesTable = new Hashtable();
        consoleAppender = new WriterAppender(
            new PatternLayout("%d [%t] %-5p %c - %m%n"), System.out);
    }

    public Log4JavaLogger()
    {
        super();
    }

    /** Initialize and create new category (logger handler)*/
    public void init(LoggingConfig loggingConfig)
    {
        // setup the logger
        logger = Category.getInstance(loggingConfig.getName());
        logger.setAdditivity(false);
        //Priority is set for DEBUG becouse this implementation checks log level.
        logger.setPriority(Priority.DEBUG);
        // FIXME: set the pattern
        layout = new PatternLayout("%d [%t] %-5p %c - %m%n");

        // now do the rest of initialization
        super.init(loggingConfig);
    }


    /**
     * Adds a local file as destinations for logger.
     *
     * @param loggingConfig The configuration of this logger.
     */
    protected void configureFiles(LoggingConfig loggingConfig)
    {
        Vector files = loggingConfig.getFiles();

        for (Enumeration filesEnum = files.elements(); filesEnum.hasMoreElements(); )
        {
            String path = (String) filesEnum.nextElement();
            //resolves relative paths
            String pathTmp = ((ServletContext)loggingConfig.getServletContext())
                .getRealPath(path);
            if (pathTmp != null)
            {
                path=pathTmp;
            }

            Appender appender = null;
            //checking if thereis such appender in the system
            appender = (Appender)filesTable.get(path);
            if (appender == null)
            {
                try
                {
                    appender = new RollingFileAppender(layout, path, true);
                    ((RollingFileAppender)appender)
                        .setMaxBackupIndex(loggingConfig.getBackupFiles());
                    //finding file size
                    if (loggingConfig.getFileSize() > -1)
                    {
                        ((RollingFileAppender)appender)
                            .setMaximumFileSize(loggingConfig.getFileSize());
                    }
                    filesTable.put(path, appender);
                }
                catch (java.io.IOException e)
                {
                    return;
                }
                logger.addAppender(appender);
            }
        }
    }

    /**
     * It adds console as a destination for logger.
     * @param loggingConfig configuration
     */
    protected void configureConsole(LoggingConfig loggingConfig)
    {
        this.console = loggingConfig.getConsole();
        if (console)
        {
            if (consoleAppender != null)
            {
                logger.addAppender(consoleAppender);
            }
        }
    }

    /**
     * This method should be implemented by user if the logger can handle console.
     * It adds console as a destination for logger.
     *
     * @param loggingConfig configuration
     */
    protected void configureRemote(LoggingConfig loggingConfig)
    {
        String remoteHost = loggingConfig.getRemoteHost();
        int remotePort=loggingConfig.getRemotePort();
        if (remoteHost == null || remoteHost.trim().equals("") || remotePort <= 0)
        {
            return;
        }
        Appender appender = new SocketAppender(remoteHost, remotePort);
        logger.addAppender(appender);
    }

    /**
     * It adds remote demon as a destination for logger.
     *
     * @param loggingConfig configuration
     */
    protected void configureSyslog(LoggingConfig loggingConfig)
    {
        String syslogHost = loggingConfig.getSyslogHost();
        if (syslogHost == null || syslogHost.trim().equals("") )
        {
            return;
        }

        Appender appender = new SyslogAppender(layout, syslogHost, logLevel);
        logger.addAppender(appender);
    }

    /**
     * It adds email as a destination for logger.
     *
     * @param loggingConfig configuration
     */
    protected void configureEmail(LoggingConfig loggingConfig)
    {
        String smtpHost = TurbineResources.getString("mail.server");
        String emailFrom = loggingConfig.getEmailFrom();
        String emailTo = loggingConfig.getEmailTo();
        String emailSubject = loggingConfig.getEmailSubject();
        String bufferSize = loggingConfig.getEmailBufferSize();

        if (smtpHost == null || smtpHost.trim().equals("")
                || emailFrom == null || emailFrom.trim().equals("")
                || emailTo == null || emailTo.trim().equals("")
                || emailSubject == null || emailSubject.trim().equals("")
                || bufferSize == null || bufferSize.trim().equals("") )
        {
            return;
        }

        SMTPAppender appender = new SMTPAppender();
        appender.setSMTPHost(smtpHost);
        appender.setFrom(emailFrom);
        appender.setTo(emailTo);
        appender.setSubject(emailSubject);
        appender.setBufferSize(new Integer(bufferSize).intValue());
        appender.setLayout(layout);
        appender.activateOptions();
        logger.addAppender(appender);
    }

    /**
     * It adds a db as a destination for logger.
     *
     * @param loggingConfig configuration
     */
    protected void configureDatabase(LoggingConfig loggingConfig)
    {
        String dbLogger = loggingConfig.getDbLogger();
        String dbPool = loggingConfig.getDbPool();

        if (dbLogger == null || dbLogger.trim().equals(""))
        {
            return;
        }

        JDBCAppender appender = new JDBCAppender();
        appender.setOption(JDBCAppender.LOGGER_CLASS_OPTION, dbLogger);

        /*
         * This sucks, but we are stuck with setOption(String, String).
         * I want people to be able to specify their own db logger
         * implementation and the options without having to touch
         * anything else, but log4j doesn't really make it that easy.
         */
        //appender.setOption(JDBCAppender.LOGGER_OPTION, "pool:" + dbPool);
        appender.setLayout(layout);
        appender.activateOptions();
        logger.addAppender(appender);
    }

    /**
     * It performs action that are need for deterimne whether
     * logger was well configured or has any output
     */
    public boolean checkLogger()
    {
        Enumeration enum = logger.getAllAppenders();
        if (enum.hasMoreElements())
        {
            return true;
        }

        return false;

    }

    /**
     * Also do a shutdown if the object is destroy()'d.
     */
    protected void finalize() throws Throwable
    {
        shutdown();
    }

    /** Close all destinations*/
    public void shutdown()
    {
        Enumeration appenders = logger.getAllAppenders();
        while (appenders.hasMoreElements())
        {
            Appender appender = (Appender)appenders.nextElement();
            appender.close();
        }
    }

    /**
     * This is a log metod with logLevel == DEBUG
     */
    public void debug(String message)
    {
        log(DEBUG, message, null, null);
    }

    /**
     * This is a log metod with logLevel == DEBUG
     */
    public void debug(String message, Throwable t)
    {
        log(DEBUG, message, null, t);
    }

    /**
     * This is a log metod with logLevel == DEBUG
     */
    public void debug(String message, RunData data)
    {
        log(DEBUG, message, data, null);
    }

    /**
     * This is a log metod with logLevel == DEBUG
     */
    public void debug(String message, RunData data, Throwable t)
    {
        log(DEBUG, message, data, t);
    }

    /**
     * This is a log metod with logLevel == INFO
     */
    public void info(String message)
    {
        log(INFO, message, null, null);

    }

    /**
     * This is a log metod with logLevel == INFO
     */
    public void info(String message, Throwable t)
    {
        log(INFO, message, null, t);

    }

    /**
     * This is a log metod with logLevel == INFO
     */
    public void info(String message, RunData data)
    {
        log(INFO, message, data, null);

    }

    /**
     * This is a log metod with logLevel == INFO
     */
    public void info(String message, RunData data, Throwable t)
    {
        log(INFO, message, data, t);

    }

    /**
     * This is a log metod with logLevel == WARN
     */
    public void warn(String message)
    {
        log(WARN, message, null, null);
    }

    /**
     * This is a log metod with logLevel == WARN
     */
    public void warn(String message, Throwable t)
    {
        log(WARN, message, null, t);
    }

    /**
     * This is a log metod with logLevel == WARN
     */
    public void warn(String message, RunData data)
    {
        log(WARN, message, data, null);
    }

    /**
     * This is a log metod with logLevel == WARN
     */
    public void warn(String message, RunData data, Throwable t)
    {
        log(WARN, message, data, t);
    }

    /**
     * This is a log metod with logLevel == ERROR
     */
    public void error(String message)
    {
        log(ERROR, message, null, null);
    }

    /**
     * This is a log metod with logLevel == ERROR
     */
    public void error(String message, Throwable e)
    {
        log(ERROR, message, null, e);
    }

    /**
     * This is a log metod with logLevel == ERROR
     */
    public void error(Throwable e)
    {
        log(ERROR, null, null, e);
    }

    /**
     * This is a log metod with logLevel == ERROR
     */
    public void error(String message, RunData data)
    {
        log(ERROR, message, data, null);
    }

    /**
     * This is a log metod with logLevel == ERROR
     */
    public void error(String message, RunData data, Throwable e)
    {
        log(ERROR, message, data, e);
    }

    /**
     *  Creates new loging message form message and RunData, and sends it to the category.
     */
    private void log(int level, String message, RunData data, Throwable e)
    {

        if (level < logLevel)
        {
            return;
        }

        if (data != null)
        {
            message += runDataFilter.getString(data);
        }
        switch (level)
        {
            case DEBUG: logger.debug(message, e); break;
            case INFO:  logger.info(message, e); break;
            case WARN:  logger.warn(message, e); break;
            case ERROR: logger.error(message, e); break;
            default: logger.debug(message, e);
        }
    }
}
