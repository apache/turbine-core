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
import java.util.Date;
import java.util.List;

import org.apache.velocity.context.Context;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.texen.ant.TexenTask;

import org.apache.turbine.torque.engine.database.model.AppData;
import org.apache.turbine.torque.engine.database.model.Database;
import org.apache.turbine.torque.engine.database.transform.XmlToAppData;
import org.apache.turbine.torque.engine.database.transform.XmlToData;

/**
 *  An extended Texen task used for generating SQL source from
 *  an XML data file
 *
 * @author   <a href="mailto:jvanzyl@periapt.com"> Jason van Zyl </a>
 * @author   <a href="mailto:jmcnally@collab.net"> John McNally </a>
 * @author   <a href="mailto:fedor.karpelevitch@home.com"> Fedor Karpelevitch </a>
 * @version  $Id$
 */
public class TorqueDataSQLTask extends TexenTask
{
    /**
     *  Application model. In this case a database model.
     */
    private AppData app;

    /**
     *  XML that describes the database model, this is transformed
     *  into the application model object.
     */
    private String xmlFile;
    private String dataXmlFile;
    private String dataDTD;

    /**
     *  The target database(s) we are generating SQL
     *  for. Right now we can only deal with a single
     *  target, but we will support multiple targets
     *  soon.
     */
    private String targetDatabase;

    private String databaseName;


    /**
     *  Get the xml schema describing the application
     *  model.
     *
     * @return  String xml schema file.
     */
    public String getXmlFile()
    {
        return xmlFile;
    }


    /**
     *  Set the xml schema describing the application
     *  model.
     *
     * @param  v The new XmlFile value
     */
    public void setXmlFile(String v)
    {
        xmlFile = v;
    }


    /**
     *  Sets the DataXmlFile attribute of the TorqueDataSQLTask object
     *
     * @param  v The new DataXmlFile value
     */
    public void setDataXmlFile(String v)
    {
        dataXmlFile = v;
    }


    /**
     *  Gets the DataXmlFile attribute of the TorqueDataSQLTask object
     *
     * @return  The DataXmlFile value
     */
    public String getDataXmlFile()
    {
        return dataXmlFile;
    }


    /**
     *  Get the current target database.
     *
     * @return  String target database(s)
     */
    public String getTargetDatabase()
    {
        return targetDatabase;
    }


    /**
     *  Set the current target database.  This is where
     *  generated java classes will live.
     *
     * @param  v The new TargetDatabase value
     */
    public void setTargetDatabase(String v)
    {
        targetDatabase = v;
    }


    /**
     *  Gets the DatabaseName attribute of the TorqueDataSQLTask object
     *
     * @return  The DatabaseName value
     */
    public String getDatabaseName()
    {
        return databaseName;
    }


    /**
     *  Sets the DatabaseName attribute of the TorqueDataSQLTask object
     *
     * @param  v The new DatabaseName value
     */
    public void setDatabaseName(String v)
    {
        databaseName = v;
    }


    /**
     *  Gets the DataDTD attribute of the TorqueDataSQLTask object
     *
     * @return  The DataDTD value
     */
    public String getDataDTD()
    {
        return dataDTD;
    }


    /**
     *  Sets the DataDTD attribute of the TorqueDataSQLTask object
     *
     * @param  v The new DataDTD value
     */
    public void setDataDTD(String v)
    {
        dataDTD = v;
    }


    /**
     *  Set up the initialial context for generating the
     *  SQL from the XML schema.
     *
     * @return  Description of the Returned Value
     */
    public Context initControlContext()
    {
        /*
         * Create a new Velocity context.
         */
        Context context = new VelocityContext();

        /*
         * Transform the XML database schema into an
         * object that represents our model.
         */
        XmlToAppData xmlParser = new XmlToAppData();
        app = xmlParser.parseFile(xmlFile);

        Database db = app.getDatabase(databaseName);
        if (db == null)
        {
            db = app.getDatabases()[0];
        }
        try
        {
            XmlToData dataXmlParser = new XmlToData(db, dataDTD);
            List data = dataXmlParser.parseFile(dataXmlFile);
            context.put("data", data);
        }
        catch (Exception e)
        {
            System.err.println("Exception parsing data XML:");
            e.printStackTrace();
        }
        /*
         * Place our model in the context.
         */
        context.put("appData", app);

        /*
         * Place the target database in the context.
         */
        context.put("targetDatabase", targetDatabase);

        return context;
    }
}


