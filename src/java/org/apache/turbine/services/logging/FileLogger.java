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

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.servlet.ServletContext;
import org.apache.turbine.util.RunData;
import org.apache.turbine.Turbine;

/**
 * This class implements Logger interface using simple file
 * writing. It handles only files and console as destinations.
 *
 * @see org.apache.turbine.services.logging.Logger
 * @author <a href="mailto:Tomasz.Zielinski@e-point.pl">Tomasz Zielinski</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public class FileLogger extends BaseLogger
{
    /** global table containing file writers */
    protected static Hashtable globalFilesTable;

    /** instance table containing file writers */
    protected Hashtable localFilesTable;

    /** line separator */
    protected String lf = System.getProperty("line.separator", "\n");

    /** class initialization */
    static
    {
        globalFilesTable = new Hashtable();
    }

    public FileLogger()
    {
        super();
        localFilesTable = new Hashtable();
    }

    /** Initialize and create the writers */
    public void init(LoggingConfig loggingConfig)
    {
        super.init(loggingConfig);
    }

    /**
     * Adds local file as destinations for logger.
     * @param LoggingConfig configuration
     */
    protected void configureFiles(LoggingConfig loggingConfig)
    {
        Vector files = loggingConfig.getFiles();
        for (Enumeration fileList = files.elements(); fileList.hasMoreElements(); )
        {
            String path = (String) fileList.nextElement();
            
            // Resolve relative paths using Turbine.getRealPath(s) so that
            // the paths are translated against the applicationRoot which
            // may be the webContext or the CVS layout of a Turbine application.
            String pathTmp = Turbine.getRealPath(path);
            
            if (pathTmp != null)
            {
                path = pathTmp;
            }

            // check if the file is being used by another FileLogger instance.
            // if so, use the writer from the global table to synchronize access
            FileWriter writer = (FileWriter)globalFilesTable.get(path);
            if (writer == null)
            {
                try
                {
                    writer = new FileWriter(path, true);
                    globalFilesTable.put(path, writer);
                }
                catch(Exception e)
                {
                    continue;
                }
            }
            localFilesTable.put(path, writer);
        }
    }

    /**
     * Adds console as a destination for logger.
     * @param LoggingConfig configuration
     */
    protected void configureConsole(LoggingConfig loggingConfig)
    {
        this.console = loggingConfig.getConsole();
    }

    /**
     * It performs action that are need for deterimne whether
     * logger was well configured or has any output
     */
    public boolean checkLogger()
    {
        if (console) return true;
        if (localFilesTable.size() > 0) return true;
        return false;
    }

    /**
     * Also do a shutdown if the object is destroy()'d.
     */
    protected void finalize() throws Throwable
    {
        shutdown();
    }

    public void shutdown()
    {
        for (Enumeration files = localFilesTable.elements(); files.hasMoreElements(); )
        {
            FileWriter fw = (FileWriter) files.nextElement();
            try
            {
                fw.close();
            }
            catch (Exception e)
            {
            }
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

    /**
     * Checks if logging is allowed and appends loglevel message
     */
    protected void log(int level,String message, RunData data, Throwable e)
    {
        if (level < logLevel)
        {
            return;
        }
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

    /** log message to all open loging files */
    protected void logAll(String level,String description, RunData data, Throwable t)
    {

        Date date = new Date();
        StringBuffer logEntry = new StringBuffer();
        logEntry.append( "[" );
        logEntry.append( date.toString() );
        logEntry.append( "] -- " );
        logEntry.append( level );
        logEntry.append( " -- " );
        logEntry.append( description );
        if (data!=null)
        {
            logEntry.append( " -- " );
            logEntry.append(runDataFilter.getString(data));
        }
        if (t != null)
        {
            logEntry.append ( lf );
            ByteArrayOutputStream ostr = new ByteArrayOutputStream();
            PrintWriter out = new PrintWriter(ostr, true);
            out.write(logEntry.toString());
            out.write("\tException:  ");
            out.write(t.toString());
            out.write(lf+"\tStack Trace follows:" + lf + "\t");
            t.printStackTrace(out);
            logEntry = new StringBuffer( ostr.toString() );
        }
        if (console)
        {
            System.out.println(logEntry.toString());
        }
        logEntry.append ( lf );

        for (Enumeration logFile = localFilesTable.elements(); logFile.hasMoreElements(); )
        {
            FileWriter fw = (FileWriter)logFile.nextElement();
            synchronized( fw )
            {
                try
                {
                    fw.write( logEntry.toString(), 0, logEntry.length());
                    fw.flush();
                }
                catch(IOException e)
                {
                    System.out.println("Cannot log message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
