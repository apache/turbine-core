package org.apache.turbine.services.logging.jdbc;

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

import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * The JDBCAppender, writes messages into a database.
 *
 * The JDBCAppender is configurable at runtime by setting options in
 * two alternatives :
 *
 * @author <a href="mailto:t.fenner@klopotek.de">Thomas Fenner</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 */
public class JDBCAppender extends AppenderSkeleton
{
    /**
     * Logger class option.
     */
    public static final String LOGGER_CLASS_OPTION = "logger.class";

    /**
     * An option to set for the logger, these are name:value pairs
     * that are used to initialize a logger.
     */
    public static final String LOGGER_OPTION = "logger.option";

    /**
     * Logger class to instantiate for logging to
     * a database.
     */
    private String loggerClass = null;

    /**
     * Hashtable of options that are used to initialize
     * the logger being used.
     */
    private Hashtable loggerOptions = new Hashtable();

    /*
     * This class encapsulate the logic which is necessary
     * to log into a table.
     */
    private JDBCLogger logger = null;

    /**
     * Hold bin for messages that need to be pushed into
     * the database into which we are logging.
     */
    private ArrayList buffer = new ArrayList();

    /**
     * How many messages should we buffer until to
     * push the messages into the database.
     */
    private int bufferSize = 1;

    /*
     * A flag to indicate that everything is ready to
     * get append() commands.
     */
    private boolean ready = false;

    /**
     * If program terminates close the database-connection and
     * flush the buffer.
     */
    public void finalize()
    {
        close();
        super.finalize();
    }

    /**
     * Internal method. Returns a array of strings containing the available
     * options which can be set with method setOption()
     */
    public String[] getOptionStrings()
    {
        /*
         * The sequence of options in this string is important, because
         * setOption() is called this way ...
         */
        return new String[]
        {
            LOGGER_CLASS_OPTION,
            LOGGER_OPTION
        };
    }

    /**
     * Sets all necessary options
     *
     * @param String option
     * @param String value
     */
    public void setOption(String option, String value)
    {
        option = option.trim();
        value = value.trim();

        if (option == null || value == null)
        {
            return;
        }
        if (option.length() == 0 || value.length() == 0)
        {
            return;
        }

        value = value.trim();

        if (option.equals(LOGGER_CLASS_OPTION))
        {
            loggerClass = value;
        }
        else if (option.equals(LOGGER_CLASS_OPTION))
        {
            String loggerOptionKey = value.substring(0,value.indexOf(":") - 1);
            String loggerOptionValue = value.substring(value.indexOf(":"));
            loggerOptions.put(loggerOptionKey, loggerOptionValue);
        }
    }

    /**
     * Active our logger to be used for appending messages
     * to the database.
     */
    public void activateOptions()
    {
        /*
         * Set up the logger.
         */
        try
        {
            logger = (JDBCLogger) Class.forName(loggerClass).newInstance();
            logger.init(loggerOptions);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Internal method. Returns true, you may define
     * your own layout...
     *
     * @return boolean
     */
    public boolean requiresLayout()
    {
        return true;
    }

    /**
     * Internal method. Close the database connection & flush the buffer.
     */
    public void close()
    {
        flushBuffer();
        logger.close();
        closed = true;
    }

    /**
     * Internal method. Appends the message to the database table.
     *
     * @param LoggingEvent event
     */
    public void append(LoggingEvent event)
    {
        if (!ready)
        {
            if (!isReady())
            {
                errorHandler.error("Not ready to append!");
                return;
            }
        }

        buffer.add(event);

        if (buffer.size() >= bufferSize)
        {
            flushBuffer();
        }
    }


    /**
     * Internal method. Flushes the buffer.
     */
    public void flushBuffer()
    {
        try
        {
            int size = buffer.size();

            if (size < 1)
            {
                return;
            }

            for (int i = 0; i < size; i++)
            {
                LoggingEvent event = (LoggingEvent) buffer.get(i);

                /*
                 * Insert message into database
                 */
                logger.append(layout.format(event));
            }

            buffer.clear();
        }
        catch (Exception e)
        {
            errorHandler.error(
                "JDBCAppender.flushBuffer(), " + e + " : " +
                    logger.getErrorMsg());

            return;
        }
    }

    /**
     * Internal method. Returns true, when the JDBCAppender is ready to
     * append messages to the database, else false.
     *
     * @return boolean
     */
    public boolean isReady()
    {
        if (ready)
        {
            return true;
        }

        ready = logger.isReady();

        if (!ready)
        {
            errorHandler.error(logger.getErrorMsg());
        }

        return ready;
    }
}
