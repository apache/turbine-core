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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.util.RunData;
import org.apache.turbine.Turbine;

/**
 * The default implementation of the logging service in Turbine.
 *
 * This service functions as a logger provider.
 * It allows access to loggers: explicite by the getLogger method,
 * or by printing methods (info, error...).
 * Real work is done by classes that implement interface: Logger.
 * The configuration of the service is read from the TurbineResources.properties.
 * The rest of the configuration is done through a defined LoggingConfig class.
 *
 * Names of the loggers, classes, log levels, destinations are defined in that file.
 *
 * @see org.apache.turbine.services.logging.Logger
 * @author <a href="mailto:Tomasz.Zielinski@e-point.pl">Tomasz Zielinski</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public class TurbineLoggingService
    extends TurbineBaseService
    implements LoggingService
{
    /** loggers repository */
    protected Hashtable loggersTable;

    /** logger for methods without target */
    protected Logger defaultLogger;

    /** bootstrap and shutdown logger using context.log */
    protected Logger simpleLogger;

    /** context for resolving paths and servlet logging */
    protected ServletContext context = null;

    /** Resources for this Service */
    private ResourceService resources = null;

    /** Name of the class that is cached for doing the reading
        of the properties file for logging. */
    private String loggingConfigClassName = null;

    public TurbineLoggingService()
    {
        loggersTable = new Hashtable();
        defaultLogger = null;
    }

    /**
     * Load all configured components and initialize them. This is
     * a zero parameter variant which queries the Turbine Servlet
     * for its config.
     *
     * @throws InitializationException Something went wrong in the init
     *         stage
     */ 
    public void init()
        throws InitializationException
    {
        ServletConfig conf = Turbine.getTurbineServletConfig();
        init(conf);
    }

    /**
     * Inits the service using servlet parameters to obtain path to the
     * configuration file. Change relatives paths.
     *
     * @param config The ServletConfiguration from Turbine
     *
     * @throws InitializationException Something went wrong when starting up.
     */
    public void init(ServletConfig config) 
        throws InitializationException
    {
        context = config.getServletContext();

        // Create bootstrap logger, for handling exceptions during service
        // initialization.
        defaultLogger = new ServletLogger();

        LoggingConfig lc = getLoggingConfig();
        lc.setName(LoggingConfig.DEFAULT);
        lc.setInitResource(null);
        lc.setServletContext(context);
        lc.init();

        defaultLogger.init(lc);
        simpleLogger = defaultLogger;

        internalInit();
        setInit(true);
    }

    /**
     * Creates a new LoggingConfig object. By default, this
     * is a PropertiesLoggingConfig object. You can override this
     * by changing your TurbineResources.properties file to something
     * else. Prop: services.TurbineLoggingService.loggingConfig
     * FIXME: Not built for speed. :-(
     */
    public LoggingConfig getLoggingConfig()
        throws InitializationException
    {
        if (loggingConfigClassName == null)
        {
            ResourceService props = getResources();
            loggingConfigClassName = props.getString(LoggingConfig.LOGGINGCONFIG,
                "org.apache.turbine.services.logging.PropertiesLoggingConfig" );
        }

        try
        {
            return (LoggingConfig)Class
                .forName(loggingConfigClassName).newInstance();
        }
        catch (java.lang.InstantiationException ie)
        {
                throw new InitializationException(
                    "LoggingService: Failed to instantiate LoggingConfig: " +
                        loggingConfigClassName);
        }
        catch (java.lang.IllegalAccessException iae)
        {
                throw new InitializationException(
                    "LoggingService: Failed to instantiate LoggingConfig: " +
                        loggingConfigClassName);
        }
        catch (java.lang.ClassNotFoundException cnfe)
        {
                throw new InitializationException(
                    "LoggingService: Failed to instantiate LoggingConfig: " +
                        loggingConfigClassName);
        }
    }

    /**
     * This gets the ResourceService associated to this Service
     */
    public ResourceService getResources()
    {
        if (resources == null)
        {
            // Get the properties for this Service
            resources = TurbineResources
                .getResources(TurbineServices.SERVICE_PREFIX +
                LoggingService.SERVICE_NAME);

            //add webappRoot manually - cos logging is a primary
            //service and so it is not yet defined
            String webappRoot = context.getRealPath("/");
            resources.setProperty(Turbine.WEBAPP_ROOT,webappRoot);

        }
        return (resources);
    }

    /**
     * This method initializes the service.
     */
    private void internalInit() throws InitializationException
    {
        ResourceService props = getResources();
        if (props == null)
        {
            throw new InitializationException("LoggingService failed to " +
                "get access to the properties for this service.");
        }

        // contains the logging configuration
        Vector loggingConfig = new Vector(10);

        //looking for default logger name
        String defaultLoggerName = props.getString(LoggingConfig.DEFAULT);

        //checking whether default logger is properly configured
        if (defaultLoggerName == null)
        {
            throw new InitializationException("LoggingService can't find " +
                "default logger name in the configuration file.");
        }

        // Get the list of facilities to configure
        Vector facilities = props.getVector(LoggingConfig.FACILITIES);
        for (Enumeration f = facilities.elements(); f.hasMoreElements();)
        {
            String facility = (String) f.nextElement();

            LoggingConfig lc = getLoggingConfig();
            lc.setName(facility);
            lc.setInitResource(props);
            lc.setServletContext(context);
            lc.init();

            loggingConfig.add((Object)lc);
        }

        //adds and configures loggers
        for (Enumeration loggers = loggingConfig.elements();
             loggers.hasMoreElements(); )
        {
            LoggingConfig lc = (LoggingConfig) loggers.nextElement();
            loadLogger(lc);
        }

        defaultLogger = (Logger)loggersTable.get(defaultLoggerName);

        //checking whether default logger is properly configured
        if (defaultLogger == null)
        {
            throw new InitializationException("LoggingService can't find " +
                "default logger in working loggers.");
        }
    }

    /**
     * Creates instances of the logger, configures it, and adds it to the
     * hashTable. It skips loggers if there where errors.
     *
     *  @param loggerDescription xml-Node defining the logger
     */
    protected void loadLogger(LoggingConfig loggingConfig)
        throws InitializationException
    {
        String className = loggingConfig.getClassName();
        String loggerName = loggingConfig.getName();

        if (className == null || className.trim().equals(""))
        {
            throw new InitializationException("LoggingService can't find " +
                "logger provider class name");
        }

        if (loggerName == null || loggerName.trim().equals(""))
        {
            throw new InitializationException("LoggingService can't find " +
                "logger provider name");
        }

        if (loggersTable.containsKey(loggerName))
        {
            throw new InitializationException("LoggingService has found " +
                "another logger of the name: " + loggerName);
        }

        Logger logger=null;
        try
        {
            Class loggerClass= Class.forName(className);
            logger=(Logger)loggerClass.newInstance();
        }
        catch (Exception e)
        {
            throw new InitializationException("LoggingService can't load " +
                "logger provider: class doesn't implement Logger interface: " +
                e.getMessage());
        }

        //inits logger
        logger.init(loggingConfig);

        // store it for later
        loggersTable.put(loggerName, logger);
    }

    /**
     * Shutdowns all loggers. After shutdown servlet logger is still available
     * using the servlet log method
     */
    public void shutdown()
    {
        if (!getInit())
        {
            return;
        }
        Enumeration iter = loggersTable.elements();

        while(iter.hasMoreElements())
        {
            ((Logger)iter.nextElement()).shutdown();
        }

        //HACK!!!!!
        //some services may log using our services after shutdown
        loggersTable.clear();
        defaultLogger = simpleLogger;

        //we don't set init as false, because we can still log.
    }

    /**
     * This method returns default logger for Turbine System
     */
    public final Logger getLogger()
    {
        return defaultLogger;
    }

    /**
     * This method returns logger with given name.
     */
    public Logger getLogger(String logName)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger == null)
        {
            return defaultLogger;
        }
        return logger;
    }

    /**
     * This method sets the log level of the default logger.
     */
    public void setLogLevel(int level)
    {
        defaultLogger.setLogLevel(level);
    }

    /**
     * This method sets the log level of the logger of given name.
     */
    public void setLogLevel(String logName, int level)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.setLogLevel(level);
        }
    }

    /**
     * This method sets format style of the default logger
     */
    public void setFormat(String format)
    {
        defaultLogger.setFormat(format);
    }

    /**
     * This method sets format style of the given logger.
     */
    public void setFormat(String logName, String format)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.setFormat(format);
        }
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the default logger
     */
    public void debug(String message)
    {
        defaultLogger.debug(message);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the default logger
     */
    public void debug(String message, Throwable t)
    {
        defaultLogger.debug(message, t);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the given logger
     */
    public void debug(String logName, String message, Throwable t)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.debug(message, t);
        }
        else
        {
            defaultLogger.debug("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the given logger
     */
    public void debug(String logName, String message)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.debug(message);
        }
        else
        {
            defaultLogger.debug("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the default logger
     */
    public void debug(String message, RunData data)
    {
        defaultLogger.debug(message, data);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the default logger
     */
    public void debug(String message, RunData data, Throwable t)
    {
        defaultLogger.debug(message, data, t);
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the given logger
     */
    public void debug(String logName, String message, RunData data, Throwable t)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.debug(message, data, t);
        }
        else
        {
            defaultLogger.debug("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == DEBUG, printing is done by
     * the given logger
     */
    public void debug(String logName, String message, RunData data)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.debug(message, data);
        }
        else
        {
            defaultLogger.debug("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the default logger
     */
    public void info(String message)
    {
        defaultLogger.info(message);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the default logger
     */
    public void info(String message, Throwable t)
    {
        defaultLogger.info(message, t);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the given logger
     */
    public void info(String logName, String message)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.info(message);
        }
        else
        {
            defaultLogger.info("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the given logger
     */
    public void info(String logName, String message, Throwable t)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.info(message, t);
        }
        else
        {
            defaultLogger.info("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the default logger
     */
    public void info(String message, RunData data)
    {
        defaultLogger.info(message, data);
    }

    /**
     * This is a log method with logLevel == INFO,printing is done by
     * the default logger
     */
    public void info(String message, RunData data, Throwable t)
    {
        defaultLogger.info(message, data, t);
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the given logger
     */
    public void info(String logName, String message, RunData data)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.info(message, data);
        }
        else
        {
            defaultLogger.info("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == INFO, printing is done by
     * the given logger
     */
    public void info(String logName, String message, RunData data, Throwable t)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.info(message, data, t);
        }
        else
        {
            defaultLogger.info("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the default logger
     */
    public void warn(String message)
    {
        defaultLogger.warn(message);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the default logger
     */
    public void warn(String message, Throwable t)
    {
        defaultLogger.warn(message, t);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the given logger
     */
    public void warn(String logName, String message)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.warn(message);
        }
        else
        {
            defaultLogger.warn("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the given logger
     */
    public void warn(String logName, String message, Throwable t)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.warn(message, t);
        }
        else
        {
            defaultLogger.warn("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == WARN,printing is done by
     * the default logger
     */
    public void warn(String message, RunData data)
    {
        defaultLogger.warn(message, data);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the default logger
     */
    public void warn(String message, RunData data, Throwable t)
    {
        defaultLogger.warn(message, data, t);
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the given logger
     */
    public void warn(String logName, String message, RunData data)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.warn(message, data);
        }
        else
        {
            defaultLogger.warn("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == WARN, printing is done by
     * the given logger
     */
    public void warn(String logName, String message, RunData data, Throwable t)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.warn(message, data, t);
        }
        else
        {
            defaultLogger.warn("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     */
    public void error(String message)
    {
        defaultLogger.error(message);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     */
    public void error(String message, Throwable t)
    {
        defaultLogger.error(message, t);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the given logger
     */
    public void error(String logName, String message)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.error(message);
        }
        else
        {
            defaultLogger.error("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the given logger
     */
    public void error(String logName, String message, Throwable t)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.error(message, t);
        }
        else
        {
            defaultLogger.error("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     */
    public void error(String message, RunData data)
    {
        defaultLogger.error(message, data);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the default logger
     */
    public void error(String message, RunData data, Throwable t)
    {
        defaultLogger.error(message, data, t);
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the given logger
     */
    public void error(String logName, String message, RunData data)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.error(message, data);
        }
        else
        {
            defaultLogger.error("FROM logger:" + logName + ": " + message);
        }
    }

    /**
     * This is a log method with logLevel == ERROR, printing is done by
     * the given logger
     */
    public void error(String logName, String message, RunData data, Throwable t)
    {
        Logger logger = (Logger)loggersTable.get(logName);
        if (logger != null)
        {
            logger.error(message, data, t);
        }
        else
        {
            defaultLogger.error("FROM logger:" + logName + ": " + message);
        }
    }
}
