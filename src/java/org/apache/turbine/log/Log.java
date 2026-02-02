package org.apache.turbine.log;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Objects;

/**
 * This is a borrowed and stripped-down version of the log4j2 Logger interface.
 * All logging operations, except configuration, are done through this interface.
 *
 * <p>
 * The canonical way to obtain a Logger for a class is through {@link Log#getLog(String)}}.
 * Typically, each class should get its own Log named after its fully qualified class name
 * </p>
 *
 * <pre>
 * public class MyClass {
 *     private static final Log log = Log.getLog(MyClass.class);
 *     // ...
 * }
 * </pre>
 */
public class Log
{
    /**
     * The name of the root Log.
     */
    public static final String ROOT_LOGGER_NAME = "";

    private final Logger logger;

    /**
     * Constructs a System Logger wrapper
     *
     * @param logger the System Logger
     */
    public Log(String name)
    {
        this.logger = System.getLogger(name);
    }

    /**
     * Logs a message object with the DEBUG level.
     *
     * @param message the message object to log.
     */
    public void debug(final Object message)
    {
        log(Level.DEBUG, message);
    }

    /**
     * Logs a message object with the DEBUG level.
     *
     * @param message the message string to log.
     */
    public void debug(final String message)
    {
        log(Level.DEBUG, message);
    }

    /**
     * Logs a message with parameters at the DEBUG level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     */
    public void debug(final String message, final Object... params)
    {
        log(Level.DEBUG, message, params);
    }

    /**
     * Logs a message at the DEBUG level including the stack trace of the {@link Throwable}
     * {@code t} passed as parameter.
     *
     * @param message the message to log.
     * @param t the exception to log, including its stack trace.
     */
    public void debug(final String message, final Throwable t)
    {
        log(Level.DEBUG, message, t);
    }

    /**
     * Logs a message object with the ERROR level.
     *
     * @param message the message object to log.
     */
    public void error(final Object message)
    {
        log(Level.ERROR, message);
    }

    /**
     * Logs a message object with the ERROR level.
     *
     * @param message the message string to log.
     */
    public void error(final String message)
    {
        log(Level.ERROR, message);
    }

    /**
     * Logs a message with parameters at the ERROR level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     */
    public void error(final String message, final Object... params)
    {
        log(Level.ERROR, message, params);
    }

    /**
     * Logs a message at the ERROR level including the stack trace of the {@link Throwable}
     * {@code t} passed as parameter.
     *
     * @param message the message object to log.
     * @param t the exception to log, including its stack trace.
     */
    public void error(final String message, final Throwable t)
    {
        log(Level.ERROR, message, t);
    }

    /**
     * Logs a message object with the FATAL level.
     *
     * @param message the message object to log.
     */
    public void fatal(final Object message)
    {
        log(Level.ERROR, message);
    }

    /**
     * Logs a message object with the FATAL level.
     *
     * @param message the message string to log.
     */
    public void fatal(final String message)
    {
        log(Level.ERROR, message);
    }

    /**
     * Logs a message with parameters at the FATAL level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     */
    public void fatal(final String message, final Object... params)
    {
        log(Level.ERROR, message, params);
    }

    /**
     * Logs a message at the FATAL level including the stack trace of the {@link Throwable}
     * {@code t} passed as parameter.
     *
     * @param message the message object to log.
     * @param t the exception to log, including its stack trace.
     */
    public void fatal(final String message, final Throwable t)
    {
        log(Level.ERROR, message, t);
    }

    /**
     * Gets the logger name.
     *
     * @return the logger name.
     */
    public String getName()
    {
        return logger.getName();
    }

    /**
     * Logs a message object with the INFO level.
     *
     * @param message the message object to log.
     */
    public void info(final Object message)
    {
        log(Level.INFO, message);
    }

    /**
     * Logs a message object with the INFO level.
     *
     * @param message the message string to log.
     */
    public void info(final String message)
    {
        log(Level.INFO, message);
    }

    /**
     * Logs a message with parameters at the INFO level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     */
    public void info(final String message, final Object... params)
    {
        log(Level.INFO, message, params);
    }

    /**
     * Logs a message at the INFO level including the stack trace of the {@link Throwable}
     * {@code t} passed as parameter.
     *
     * @param message the message object to log.
     * @param t the exception to log, including its stack trace.
     */
    public void info(final String message, final Throwable t)
    {
        log(Level.INFO, message, t);
    }

    /**
     * Checks whether this Logger is enabled for the DEBUG Level.
     *
     * @return boolean - {@code true} if this Logger is enabled for level DEBUG, {@code false}
     *         otherwise.
     */
    public boolean isDebugEnabled()
    {
        return logger.isLoggable(Level.DEBUG);
    }

    /**
     * Checks whether this Logger is enabled for the ERROR Level.
     *
     * @return boolean - {@code true} if this Logger is enabled for level ERROR, {@code false}
     *         otherwise.
     */
    public boolean isErrorEnabled()
    {
        return logger.isLoggable(Level.ERROR);
    }

    /**
     * Checks whether this Logger is enabled for the FATAL Level.
     *
     * @return boolean - {@code true} if this Logger is enabled for level FATAL, {@code false}
     *         otherwise.
     */
    public boolean isFatalEnabled()
    {
        return logger.isLoggable(Level.ERROR);
    }

    /**
     * Checks whether this Logger is enabled for the INFO Level.
     *
     * @return boolean - {@code true} if this Logger is enabled for level INFO, {@code false}
     *         otherwise.
     */
    public boolean isInfoEnabled()
    {
        return logger.isLoggable(Level.INFO);
    }

    /**
     * Checks whether this Logger is enabled for the TRACE level.
     *
     * @return boolean - {@code true} if this Logger is enabled for level TRACE, {@code false}
     *         otherwise.
     */
    public boolean isTraceEnabled()
    {
        return logger.isLoggable(Level.TRACE);
    }

    /**
     * Checks whether this Logger is enabled for the WARN Level.
     *
     * @return boolean - {@code true} if this Logger is enabled for level WARN, {@code false}
     *         otherwise.
     */
    public boolean isWarnEnabled()
    {
        return logger.isLoggable(Level.WARNING);
    }

    private void log(final Level level, final Object message)
    {
        if (logger.isLoggable(level))
        {
            if (message instanceof Throwable)
            {
                logger.log(level, "Exception:", message);
            }
            else
            {
                logger.log(level, Objects.toString(message, null));
            }
        }
    }

    private void log(final Level level, final String message)
    {
        if (logger.isLoggable(level))
        {
            logger.log(level, message);
        }
    }

    private void log(final Level level, final String message, final Object... params)
    {
        if (logger.isLoggable(level))
        {
            logger.log(level, message, params);
        }
    }

    private void log(final Level level, final String message, final Throwable t)
    {
        if (logger.isLoggable(level))
        {
            logger.log(level, message, t);
        }
    }

    /**
     * Logs a message object with the TRACE level.
     *
     * @param message the message object to log.
     */
    public void trace(final Object message)
    {
        log(Level.TRACE, message);
    }

    /**
     * Logs a message object with the TRACE level.
     *
     * @param message the message string to log.
     */
    public void trace(final String message)
    {
        log(Level.TRACE, message);
    }

    /**
     * Logs a message with parameters at the TRACE level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     */
    public void trace(final String message, final Object... params)
    {
        log(Level.TRACE, message, params);
    }

    /**
     * Logs a message at the TRACE level including the stack trace of the {@link Throwable}
     * {@code t} passed as parameter.
     *
     * @param message the message object to log.
     * @param t the exception to log, including its stack trace.
     * @see #debug(String)
     */
    public void trace(final String message, final Throwable t)
    {
        log(Level.TRACE, message, t);
    }

    /**
     * Logs a message object with the WARN level.
     *
     * @param message the message object to log.
     */
    public void warn(final Object message)
    {
        log(Level.WARNING, message);
    }

    /**
     * Logs a message object with the WARN level.
     *
     * @param message the message string to log.
     */
    public void warn(final String message)
    {
        log(Level.WARNING, message);
    }

    /**
     * Logs a message with parameters at the WARN level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     */
    public void warn(final String message, final Object... params)
    {
        log(Level.WARNING, message, params);
    }

    /**
     * Logs a message at the WARN level including the stack trace of the {@link Throwable}
     * {@code t} passed as parameter.
     *
     * @param message the message object to log.
     * @param t the exception to log, including its stack trace.
     */
    public void warn(final String message, final Throwable t)
    {
        log(Level.WARNING, message, t);
    }

    /**
     * Returns the root logger.
     *
     * @return the root logger, named {@link ROOT_LOGGER_NAME}.
     */
    public static Log getRootLogger()
    {
        return getLog(ROOT_LOGGER_NAME);
    }

    /**
     * Returns a Log with the specified name.
     *
     * @param name
     *            The logger name.
     * @return The Log.
     * @throws UnsupportedOperationException
     *             if {@code name} is {@code null}
     */
    public static Log getLog(final String name)
    {
        return new Log(name);
    }

    /**
     * Returns a Log using the fully qualified name of the Class as the Log
     * name.
     *
     * @param clazz
     *            The Class whose name should be used as the Log name.
     * @return The Log.
     * @throws UnsupportedOperationException
     *             if {@code clazz} is {@code null}
     */
    public static Log getLog(final Class<?> clazz)
    {
        return Log.getLog(clazz.getName());
    }
}