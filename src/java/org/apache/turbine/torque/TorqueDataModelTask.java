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

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.velocity.context.Context;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.texen.ant.TexenTask;
import org.apache.turbine.torque.engine.database.model.AppData;
import org.apache.turbine.torque.engine.database.model.Database;
import org.apache.turbine.torque.engine.database.transform.XmlToAppData;

/**
 * A base torque task that uses either a single XML schema
 * representing a data model, or a <fileset> of XML schemas.
 * We are making the assumption that an XML schema representing
 * a data model contains tables for a <strong>single</strong>
 * database.
 *
 * @author <a href="mailto:jvanzyl@zenplex.com">Jason van Zyl</a>
 */
public class TorqueDataModelTask
    extends TexenTask
{
    /**
     *  XML that describes the database model, this is transformed
     *  into the application model object.
     */
    protected String xmlFile;

    /**
     * Fileset of XML schemas which represent our data models.
     */
    protected Vector filesets = new Vector();
    
    /**
     * Data models that we collect. One from each XML schema file.
     */
    protected Vector dataModels = new Vector();

    /**
     * Velocity context which exposes our objects
     * in the templates.
     */
    protected Context context;

    /**
     * Map of data model name to database name.
     * Should probably stick to the convention
     * of them being the same but I know right now
     * in a lot of cases they won't be.
     */
    protected Hashtable dataModelDbMap;
    
    /**
     * Hashtable containing the names of all the databases
     * in our collection of schemas.
     */
    protected Hashtable databaseNames;

    /**
     * Name of the properties file that maps an SQL file
     * to a particular database.
     */
    protected String sqldbmap;

    /**
     * Set the sqldbmap.
     *
     * @param String sqldbmap
     */
    public void setSqlDbMap(String sqldbmap)
    {
        this.sqldbmap = sqldbmap;
    }
    
    /**
     * Get the sqldbmap.
     *
     * @return String sqldbmap.
     */
    public String getSqlDbMap()
    {
        return sqldbmap;
    }        

    /**
     * Return the data models that have been
     * processed.
     *
     * @return Vector data models
     */
    public Vector getDataModels()
    {
        return dataModels;
    }        

    /**
     * Return the data model to database name map.
     *
     * @return Hashtable data model name to database name map.
     */
    public Hashtable getDataModelDbMap()
    {
        return dataModelDbMap;
    }        

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
     * Adds a set of files (nested fileset attribute).
     */
    public void addFileset(FileSet set) 
    {
        filesets.addElement(set);
    }

    /**
     *  Set up the initialial context for generating the
     *  SQL from the XML schema.
     *
     * @return  Description of the Returned Value
     */
    public Context initControlContext()
        throws Exception
    {
        XmlToAppData xmlParser;
    
        if (xmlFile == null && filesets.isEmpty())
        {
            throw new BuildException("You must specify an XML schema or " +
                "fileset of XML schemas!");
        }            

        if (xmlFile != null)
        {
            // Transform the XML database schema into
            // data model object.
            xmlParser = new XmlToAppData();
            AppData ad = xmlParser.parseFile(xmlFile);
            xmlParser.parseFile(xmlFile);
            ad.setName(xmlFile.substring(0,xmlFile.indexOf(".")));
            dataModels.addElement(ad);
        } 
        else 
        { 
            // Deal with the filesets.
            for (int i=0; i < filesets.size(); i++) 
            {
                FileSet fs = (FileSet) filesets.elementAt(i);
                DirectoryScanner ds = fs.getDirectoryScanner(project);
                File srcDir = fs.getDir(project);

                String[] dataModelFiles = ds.getIncludedFiles();

                // Make a transaction for each file
                for ( int j = 0 ; j < dataModelFiles.length ; j++ ) 
                {
                    xmlParser = new XmlToAppData();
                    AppData ad = xmlParser.parseFile(
                        new File(srcDir, dataModelFiles[j]).toString());
                    xmlParser.parseFile(
                        new File(srcDir, dataModelFiles[j]).toString());
                    
                    ad.setName(dataModelFiles[j].substring(0,dataModelFiles[j].indexOf(".")));
                    dataModels.addElement(ad);
                }
            }
        }
        
        Iterator i = dataModels.iterator();
        databaseNames = new Hashtable();
        dataModelDbMap = new Hashtable();        
        
        // Different datamodels may state the same database
        // names, we just want the unique names of databases.
        while (i.hasNext())
        {
            AppData ad = (AppData) i.next();
            Database database = ad.getDatabase();
            databaseNames.put(database.getName(), database.getName());
            dataModelDbMap.put(ad.getName(), database.getName());
        }

        // Create a new Velocity context.
        context = new VelocityContext();
        
        // Place our set of data models into the context along
        // with the names of the databases as a convenience for
        // now. 
        context.put("dataModels", dataModels);
        context.put("databaseNames", databaseNames);
    
        return context;
    }
}

