package org.apache.turbine.torque;

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

import org.apache.velocity.context.Context;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.texen.ant.TexenTask;

import org.apache.turbine.torque.engine.database.model.AppData;
import org.apache.turbine.torque.engine.database.transform.XmlToAppData;

/**
 * An extended Texen task used for generating simple scripts
 * for creating databases on various platforms.
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 */
public class TorqueCreateDatabase 
    extends TexenTask
{
    /**
     * Application model. In this case a database model.
     */
    private AppData app;

    /**
     * XML that describes the database model, this is transformed
     * into the application model object.
     */
    private String xmlFile;

    /**
     * The target database vendor: MySQL, Oracle.
     */
    private String targetDatabase;

    /**
     * The target platform we are creating the
     * script for. The target platform controls
     * which template is parsed
     */
    private String targetPlatform;

    /**
     * Database user.
     */
    private String databaseUser;
    
    /**
     * Password for specified database user.
     */
    private String databasePassword;
    
    /**
     * Host on which specified database resides.
     */
    private String databaseHost;

    /**
     * Get the xml schema describing the application
     * model.
     *
     * @return String xml schema file.
     */
    public String getXmlFile ()
    {
        return xmlFile;
    }

    /**
     * Set the xml schema describing the application
     * model.
     *
     * @param String xml schema file.
     */
    public void setXmlFile(String v)
    {
        xmlFile = v;
    }

    /**
     * Get the target database.
     *
     * @return String target database.
     */
    public String getTargetDatabase ()
    {
        return targetDatabase;
    }

    /**
     * Set the target database.
     *
     * @param String target database(s)
     */
    public void setTargetDatabase (String v)
    {
        targetDatabase = v;
    }

    /**
     * Get the target platform.
     *
     * @return String target platform.
     */
    public String getTargetPlatform ()
    {
        return targetPlatform;
    }

    /**
     * Set the target platform.
     *
     * @param String target platform
     */
    public void setTargetPlatform (String v)
    {
        targetPlatform = v;
    }

    /**
     * Get the database user.
     *
     * @return String target platform.
     */
    public String getDatabaseUser ()
    {
        return databaseUser;
    }

    /**
     * Set the database user.
     *
     * @param String databaseUser
     */
    public void setDatabaseUser (String v)
    {
        databaseUser = v;
    }

    /**
     * Get the database password.
     *
     * @return String database password.
     */
    public String getDatabasePassword()
    {
        return databasePassword;
    }

    /**
     * Set the databasePassword
     *
     * @param String target platform
     */
    public void setDatabasePassword (String v)
    {
        databasePassword = v;
    }

    /**
     * Get the database host.
     *
     * @return String database host.
     */
    public String getDatabaseHost ()
    {
        return databaseHost;
    }

    /**
     * Set the database host.
     *
     * @param String database host.
     */
    public void setDatabaseHost (String v)
    {
        databaseHost = v;
    }

    /**
     * Place our target database and target platform
     * values into the context for use in the
     * templates.
     */
    public Context initControlContext()
    {
        // Create a new Velocity context.
        Context context = new VelocityContext();
        
        // Transform the XML database schema into an
        // object that represents our model.
        XmlToAppData xmlParser = new XmlToAppData();
        app = xmlParser.parseFile(xmlFile);

        // Place our model in the context.
        context.put("appData", app);

        context.put("targetDatabase", targetDatabase);
        context.put("targetPlatform", targetPlatform);
        context.put("databaseUser", databaseUser);
        context.put("databasePassword", databasePassword);
        context.put("databaseHost", databaseHost);
        return context;
    }
}
