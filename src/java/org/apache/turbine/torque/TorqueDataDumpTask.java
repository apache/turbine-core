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
import java.util.Iterator;
import java.util.Properties;
import java.util.NoSuchElementException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.velocity.context.Context;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.texen.ant.TexenTask;

import org.apache.turbine.torque.engine.database.model.AppData;
import org.apache.turbine.torque.engine.database.model.Database;
import org.apache.turbine.torque.engine.database.transform.XmlToAppData;

/**
 *  An extended Texen task used for dumping data from db into XML
 *
 * @author   <a href="mailto:fedor.karpelevitch@home.com">  Fedor Karpelevitch  </a>
 * @version  $Id$
 * @deprecated use turbine-torque
 */
public class TorqueDataDumpTask extends TexenTask
{
    /**
     *  Application model. In this case a database model.
     */
    private AppData app;

    private String databaseName;

    /**
     *  XML that describes the database model, this is transformed
     *  into the application model object.
     */
    private String xmlFile;

    /**
     *  Database URL used for JDBC connection.
     */
    private String databaseUrl;

    /**
     *  Database driver used for JDBC connection.
     */
    private String databaseDriver;

    /**
     *  Database user used for JDBC connection.
     */
    private String databaseUser;

    /**
     *  Database password used for JDBC connection.
     */
    private String databasePassword;


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
     *  Get the database name to dump
     *
     * @return  The DatabaseName value
     */
    public String getDatabaseName()
    {
        return databaseName;
    }


    /**
     *  Set the database name
     *
     * @param  v The new DatabaseName value
     */
    public void setDatabaseName(String v)
    {
        databaseName = v;
    }


    /**
     *  Get the database url
     *
     * @return  The DatabaseUrl value
     */
    public String getDatabaseUrl()
    {
        return databaseUrl;
    }


    /**
     *  Set the database url
     *
     * @param  v The new DatabaseUrl value
     */
    public void setDatabaseUrl(String v)
    {
        databaseUrl = v;
    }


    /**
     *  Get the database driver name
     *
     * @return  String database driver name
     */
    public String getDatabaseDriver()
    {
        return databaseDriver;
    }


    /**
     *  Set the database driver name
     *
     * @param  v The new DatabaseDriver value
     */
    public void setDatabaseDriver(String v)
    {
        databaseDriver = v;
    }


    /**
     *  Get the database user
     *
     * @return  String database user
     */
    public String getDatabaseUser()
    {
        return databaseUser;
    }


    /**
     *  Set the database user
     *
     * @param  v The new DatabaseUser value
     */
    public void setDatabaseUser(String v)
    {
        databaseUser = v;
    }


    /**
     *  Get the database password
     *
     * @return  String database password
     */
    public String getDatabasePassword()
    {
        return databasePassword;
    }


    /**
     *  Set the database password
     *
     * @param  v The new DatabasePassword value
     */
    public void setDatabasePassword(String v)
    {
        databasePassword = v;
    }


    /**
     *  Initializes initial context
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

        /*
         * Place our model in the context.
         */
        Database dbm = app.getDatabase(databaseName);
        if (dbm == null)
        {
            dbm = app.getDatabases()[0];
        }
        context.put("databaseModel", dbm);

        context.put("dataset", "all");

        System.err.println("Your DB settings are:");
        System.err.println("driver : " + databaseDriver);
        System.err.println("URL : " + databaseUrl);
        System.err.println("user : " + databaseUser);
        System.err.println("password : " + databasePassword);

        try
        {
            Class.forName(databaseDriver);
            System.err.println("DB driver sucessfuly instantiated");

            // Attemtp to connect to a database.

            Connection conn = DriverManager.getConnection(
                    databaseUrl, databaseUser, databasePassword);

            System.err.println("DB connection established");
            context.put("tableTool", new TableTool(conn));
        }
        catch (SQLException se)
        {
            System.err.println("SQLException while connecting to DB:");
            se.printStackTrace();
        }
        catch (ClassNotFoundException cnfe)
        {
            System.err.println("cannot load driver:");
            cnfe.printStackTrace();
        }

        return context;
    }


    /**
     *  A nasty do-it-all tool class. It serves as:
     *  <ul>
     *  <li>context tool to fetch a table iterator</li>
     *  <li>the abovenamed iterator which iterates over the table</li>
     *  <li>getter for the table fields</li>
     *  </ul>
     *
     * @author  fedor
     */
    public class TableTool implements Iterator
    {
        private Connection conn;
        private ResultSet rs;
        private boolean isEmpty;


        /**
         *  Constructor for the TableTool object
         *
         * @param  conn Description of Parameter
         */
        public TableTool(Connection conn)
        {
            this.conn = conn;
        }


        /**
         *  Constructor for the TableTool object
         *
         * @param  rs Description of Parameter
         * @exception  SQLException Description of Exception
         */
        public TableTool(ResultSet rs) throws SQLException
        {
            this.rs = rs;
            this.isEmpty = !rs.isBeforeFirst();
        }


        /**
         *  Description of the Method
         *
         * @param  tableName Description of Parameter
         * @return  Description of the Returned Value
         * @exception  SQLException Description of Exception
         */
        public TableTool fetch(String tableName) throws SQLException
        {
            System.err.println();
            System.err.print("fetching table " + tableName);
            return
                    new TableTool(conn.createStatement()
                    .executeQuery("SELECT * FROM " + tableName));
        }


        /**
         *  Description of the Method
         *
         * @return  Description of the Returned Value
         */
        public boolean hasNext()
        {
            try
            {
                return !(rs.isLast() || this.isEmpty);
            }
            catch (SQLException se)
            {
                System.err.println("SQLException :");
                se.printStackTrace();
            }
            return false;
        }


        /**
         *  Description of the Method
         *
         * @return  Description of the Returned Value
         * @exception  NoSuchElementException Description of Exception
         */
        public Object next() throws NoSuchElementException
        {
            try
            {
                System.err.print(".");
                rs.next();
            }
            catch (SQLException se)
            {
                System.err.println("SQLException while iterating:");
                se.printStackTrace();
                throw new NoSuchElementException(se.getMessage());
            }
            return this;
        }


        /**
         *  Description of the Method
         *
         * @param  columnName Description of Parameter
         * @return  Description of the Returned Value
         */
        public String get(String columnName)
        {
            try
            {
                return rs.getString(columnName);
            }
            catch (SQLException se)
            {
                System.err.println("SQLException fetching value " +
                        columnName + ":" + se.getMessage());
            }
            return null;
        }


        /**
         *  Description of the Method
         *
         * @exception  UnsupportedOperationException Description of Exception
         */
        public void remove() throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }
    }
}

