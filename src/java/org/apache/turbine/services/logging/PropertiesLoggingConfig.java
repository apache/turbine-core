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

import java.util.Iterator;
import java.util.Vector;
import java.util.Properties;

import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.util.Log;
import org.apache.stratum.configuration.Configuration;

/**
 * Small helper class that encapsulates the logging configuration 
 * information. This class reads its information from a Properties
 * file.
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public class PropertiesLoggingConfig implements LoggingConfig
{
    private String name = null;
    private Object context = null;

    private Vector files = null;

    private String syslogHost = null;
    private String syslogFacility = null;

    private String remoteHost = null;
    private int remotePort = -1;

    private String emailTo = null;
    private String emailSubject = null;
    private String emailFrom = null;
    private String emailBufferSize = null;

    private String dbLogger = null;
    private String dbPool = null;

    private String className = null;
    private String level = null;
    private String format = null;
    private boolean console = false;
    private long fileSize = -1;
    private int backupFiles = DEFAULT_BACKUP_FILES;
    private ResourceService props = null;
    
    protected PropertiesLoggingConfig()
    {
    }

    public void setInitResource (Object props)
    {
        this.props = (ResourceService) props;
    }

    /** 
     * returns all properties in a properties object - used by log4j
     * initialization
     **/
    public Properties getFacilityProperties(String facilityName)
    {
        // Extract the log4j values out of the configuration and
        // place them in a Properties object so that we can
        // use the log4j PropertyConfigurator.
        Properties p = new Properties();

        Configuration facilityConfiguration =
            props.getConfiguration(facilityName);
        Iterator i = facilityConfiguration.getKeys();
        while (i.hasNext())
        {
            String key = (String) i.next();

            // We have to deal with ExtendedProperties way
            // of dealing with "," in properties which is to
            // make them separate values. Log4j category
            // properties contain commas so we must stick them
            // back together for log4j.
            String[] values = facilityConfiguration.getStringArray(key);

            String value = null;
            if (values.length == 1)
            {
                value = values[0];
            }
            else if (values.length > 1)
            {
                StringBuffer valueSB = new StringBuffer();
                for (int j=0; j<values.length-1; j++)
                {
                    valueSB.append(values[j]).append(",");
                }
                value = valueSB.append(values[values.length-1]).toString();
            }

            p.put(key, value);
        }

        return p;
    }

    public void init()
        throws InitializationException
    {
        if (this.props == null)
        {
            return;
        }

        // Just get the resources for the particular facility that
        // we are interested in.
        ResourceService res = props.getResources(name);
        Iterator keys = res.getKeys();

        while (keys.hasNext())
        {
            String key = (String) keys.next();

            if (key.equals(LoggingConfig.CLASSNAME))
            {
                setClassName(res.getString(key));
            }
            else if (key.equals(LoggingConfig.LEVEL))
            {
                setLevel(res.getString(key));
            }
            else if (key.equals(Logger.SIZE_KEY))
            {
                setFileSize(res.getLong(key));
            }
            else if (key.equals(Logger.BACKUP_KEY))
            {
                setBackupFiles(res.getInt(key));
            }
            else if (key.equals(Logger.FORMAT_KEY))
            {
                setFormat(res.getString(key));
            }
            else if (key.indexOf(LoggingConfig.DESTINATION) > -1)
            {
                if (key.indexOf(Logger.FILE_KEY) > -1)
                {
                    files = res.getVector(key);
                }
                else if (key.indexOf(Logger.REMOTE_KEY) > -1)
                {
                    if (key.indexOf(Logger.HOST_KEY) > -1)
                    {
                        setRemoteHost(res.getString(key));
                    }
                    else if (key.indexOf(Logger.PORT_KEY) > -1)
                    {
                        setRemotePort(res.getInt(key));
                    }
                }
                else if (key.indexOf(Logger.CONSOLE_KEY) > -1)
                {
                    setConsole(res.getBoolean(key));
                }
                else if (key.indexOf(Logger.SYSLOGD_KEY) > -1)
                {
                    if (key.indexOf(Logger.HOST_KEY) > -1)
                    {
                        setSyslogHost(res.getString(key));
                    }
                    else if (key.indexOf(Logger.FACILITY_KEY) > -1)
                    {
                        setSyslogFacility(res.getString(key));
                    }
                }
                else if (key.indexOf(Logger.EMAIL_KEY) > -1)
                {
                    if (key.indexOf(Logger.EMAILFROM_KEY) > -1)
                    {
                        setEmailFrom(res.getString(key));
                    }
                    else if (key.indexOf(Logger.EMAILTO_KEY) > -1)
                    {
                        setEmailTo(res.getString(key));
                    }
                    else if (key.indexOf(Logger.EMAILSUBJECT_KEY) > -1)
                    {
                        setEmailSubject(res.getString(key));
                    }
                    else if (key.indexOf(Logger.EMAILBUFFERSIZE_KEY) > -1)
                    {
                        setEmailBufferSize(res.getString(key));
                    }
                }
                else if (key.indexOf(Logger.DB_KEY) > -1)
                {
                    if (key.indexOf(Logger.DB_LOGGER_KEY) > -1)
                    {
                        setDbLogger(res.getString(key));
                    }
                    else if (key.indexOf(Logger.DB_POOL_KEY) > -1)
                    {
                        setDbPool(res.getString(key));
                    }
                }                    
            }
        }
    }

    public Object getServletContext()
    {
        return context;
    }

    public void setServletContext(Object value)
    {
        this.context = value;
    }        

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String value)
    {
        this.format = value;
    }        

    public String getName()
    {
        return name;
    }

    public void setName(String value)
    {
        this.name = value;
    }        

    public String getRemoteHost()
    {
        return remoteHost;
    }

    public void setRemoteHost(String value)
    {
        this.remoteHost = value;
    }        

    public int getRemotePort()
    {
        return remotePort;
    }

    public void setRemotePort(int value)
    {
        this.remotePort = value;
    }        

    public int getBackupFiles()
    {
        return backupFiles;
    }

    public void setBackupFiles(int value)
    {
        this.backupFiles = value;
    }        

    public long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(long value)
    {
        this.fileSize = value;
    }        

    public Vector getFiles()
    {
        return this.files;
    }

    public void setFiles(Vector value)
    {
        this.files = value;
    }        

    public boolean getConsole()
    {
        return console;
    }

    public void setConsole(boolean value)
    {
        this.console = value;
    }        

    public String getSyslogHost()
    {
        return syslogHost;
    }

    public void setSyslogHost(String syslogHost)
    {
        this.syslogHost = syslogHost;
    }        

    public String getSyslogFacility()
    {
        return syslogFacility;
    }

    public void setSyslogFacility(String syslogFacility)
    {
        this.syslogFacility = syslogFacility;
    }

    public String getEmailFrom()
    {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom)
    {
        this.emailFrom = emailFrom;
    }

    public String getEmailTo()
    {
        return emailTo;
    }

    public void setEmailTo(String emailTo)
    {
        this.emailTo = emailTo;
    }

    public String getEmailSubject()
    {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject)
    {
        this.emailSubject = emailSubject;
    }

    public String getEmailBufferSize()
    {
        return emailBufferSize;
    }

    public void setEmailBufferSize(String bufferSize)
    {
        this.emailBufferSize = bufferSize;
    }

    public void setDbLogger(String v)
    {
        dbLogger = v;
    }

    public String getDbLogger()
    {
        return dbLogger;
    }        

    public void setDbPool(String v)
    {
        dbPool = v;
    }

    public String getDbPool()
    {
        return dbPool;
    }        

    public void setClassName(String className)
    {
        this.className = className;
    }        

    public String getClassName()
    {
        return className;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }
}
