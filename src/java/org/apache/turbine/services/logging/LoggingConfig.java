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

import java.util.Vector;
import java.util.Properties;
import org.apache.turbine.services.InitializationException;

/**
 * This is the interface that one must implement if they wish to
 * provide alternative configuration resources for the Logging
 * system.
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public interface LoggingConfig
{
    public final static String LOGGINGCONFIG = "loggingConfig";
    public final static String DEFAULT = "default";
    public final static String FACILITIES = "facilities";
    public final static String DESTINATION = "destination";
    public final static String CLASSNAME = "className";
    public final static String LEVEL = "level";

    /** default rollover file size */
    public static final long DEFAULT_FILE_SIZE = 80000;

    /** default number of backup files */
    public static final int DEFAULT_BACKUP_FILES = 1;

    public abstract void setInitResource(Object props);

    /**
     * returns all properties in a properties object - used by log4j
     * initialization
     **/
    public abstract Properties getFacilityProperties(String facilityName);

    public abstract void init() throws InitializationException;

    public abstract Object getServletContext();

    public abstract void setServletContext(Object value);

    public abstract String getFormat();

    public abstract void setFormat(String value);

    public abstract String getName();

    public abstract void setName(String value);

    public abstract String getRemoteHost();

    public abstract void setRemoteHost(String value);

    public abstract int getRemotePort();

    public abstract void setRemotePort(int value);

    public abstract int getBackupFiles();

    public abstract void setBackupFiles(int value);

    public abstract long getFileSize();

    public abstract void setFileSize(long value);

    public abstract Vector getFiles();

    public abstract void setFiles(Vector value);

    public abstract boolean getConsole();

    public abstract void setConsole(boolean value);

    public abstract String getSyslogHost();

    public abstract void setSyslogHost(String syslogHost);

    public abstract String getSyslogFacility();

    public abstract void setSyslogFacility(String syslogFacility);

    public abstract String getEmailFrom();

    public abstract void setEmailFrom(String emailFrom);

    public abstract String getEmailTo();

    public abstract void setEmailTo(String emailTo);

    public abstract String getEmailSubject();

    public abstract void setEmailSubject(String emailSubject);

    public abstract void setDbLogger(String v);
    public abstract String getDbLogger();

    public abstract void setDbPool(String v);
    public abstract String getDbPool();

    public abstract String getEmailBufferSize();

    public abstract void setEmailBufferSize(String emailBufferSize);

    public abstract void setClassName(String className);

    public abstract String getClassName();

    public abstract String getLevel();

    public abstract void setLevel(String level);
}
