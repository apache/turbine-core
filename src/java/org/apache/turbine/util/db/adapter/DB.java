package org.apache.turbine.util.db.adapter;

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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.ConnectionPoolDataSource;

/**
 * <code>DB</code> defines the interface for a Turbine database
 * adapter.  Support for new databases is added by subclassing
 * <code>DB</code> and implementing its abstract interface, and by
 * registering the new Turbine database adapter and its corresponding
 * JDBC driver in the TurbineResources.properties file.
 *
 * <p>The Turbine database adapters exist to present a uniform
 * interface to database access across all available databases.  Once
 * the necessary adapters have been written and configured,
 * transparent swapping of databases is theoretically supported with
 * <i>zero code changes</i> and minimal configuration file
 * modifications.
 *
 * <p>Your adapter must be added to the list of available adapters,
 * using a <code>database.adapter</code> entry.  The established
 * naming convention for adapters is the String "DB" prepended to a
 * mutation of the short class name of the driver (i.e. no package
 * name) or database name.  A JDBC driver corresponding to your
 * adapter should also be added, using the fullly-qualified class name
 * of the driver.  If no driver is specified for your adapter,
 * <code>driver.default</code> is used.
 *
 * <pre>
 * # Configuration for the Mysql/MM adapter.
 * database.adaptor=DBMM
 * database.adaptor.DBMM=org.gjt.mm.mysql.Driver
 * </pre>
 *
 * Hooks to make <i>your</i> adapter the default may also be added.
 *
 * <pre>
 * #### MySQL MM Driver
 * database.default.driver=org.gjt.mm.mysql.Driver
 * database.default.url=jdbc:mysql://localhost/DATABASENAME
 * </pre>
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @version $Id$
 */
public abstract class DB implements Serializable
{
    /** The database user name. */
    protected String DB_USER;

    /** The database password. */
    protected String DB_PASS;

    /** The database name. */
    protected String DB_CONNECTION;

    /** The JDBC driver. */
    private String JDBCDriver = null;

    /** database does not support limiting resultsets */
    public static final int LIMIT_STYLE_NONE = 0;

    /** SELECT ... LIMIT <limit>, [<offset>] */
    public static final int LIMIT_STYLE_POSTGRES = 1;

    /** SELECT ... LIMIT [<offset>, ] <limit>  */
    public static final int LIMIT_STYLE_MYSQL = 2;

    /** SET ROWCOUNT <limit> SELECT ... SET ROWCOUNT 0 */
    public static final int LIMIT_STYLE_SYBASE = 3;

    /** SELECT TOP <limit> ...  */
    public static final int LIMIT_STYLE_MSSQL7 = 4;


    /**
     * Empty constructor.
     */
    protected DB()
    {
    }

    /**
     * Returns a JDBC <code>Connection</code> from the
     * <code>DriverManager</code>.
     *
     * @return A JDBC <code>Connection</code> object for this
     * database.
     * @exception SQLException
     */
    public Connection getConnection()
        throws SQLException
    {
        // Workaround for buggy WebLogic 5.1 classloader - ignore the
        // exception upon first invocation.
        try
        {
            return DriverManager.getConnection( DB_CONNECTION,
                                                DB_USER,
                                                DB_PASS );
        }
        catch( ClassCircularityError e )
        {
            return DriverManager.getConnection( DB_CONNECTION,
                                                DB_USER,
                                                DB_PASS );
        }
    }

    /**
     * Returns a new JDBC <code>PooledConnection</code>.
     * The JDBC driver should support the JDBC 2.0 extenstions. Since the
     * implementation of this class is driver specific, the actual class
     * of the JDBC driver that implements the PooledConnection interface
     * should be defined in the specific DB Adapter
     *
     * @return A JDBC <code>PooledConnection</code> object for this
     * database.
     * @exception SQLException if the driver does not support PooledConnection
     * objects
     */
    public ConnectionPoolDataSource getConnectionPoolDataSource()
        throws SQLException
    {
        throw new SQLException("ConnectionPoolDataSource objects not supported by JDBC driver");
    }

    /**
     * Performs basic initialization.  Calls Class.forName() to assure
     * that the JDBC driver for this adapter can be loaded.
     *
     * @param url The URL of the database to connect to.
     * @param username The name of the user to use when connecting.
     * @param password The user's password.
     * @exception Exception The JDBC driver could not be loaded or
     * instantiated.
     */
    public void init(String url,
                     String username,
                     String password)
        throws Exception
    {
        DB_USER = username;
        DB_PASS = password;
        DB_CONNECTION = url;

        if (JDBCDriver != null)
        {
            Class.forName( JDBCDriver ).newInstance();
        }
        else
        {
            throw new Exception("The JDBC driver must be set for the DB " +
                                "object with a URL of " + url);
        }
    }

    /**
     * This method is used to ignore case.
     *
     * @param in The string to transform to upper case.
     * @return The upper case string.
     */
    public abstract String toUpperCase(String in);

    /**
     * Gets the string delimiter (usually '\'').
     *
     * @return The delimeter.
     */
    public abstract char getStringDelimiter();

    /**
     * Returns the last auto-increment key.  Databases like MySQL
     * which support this feature will return a result, others will
     * return null.
     *
     * @param obj The string information for generating a key.
     * @return The most recently inserted database key.
     */
    public abstract String getIdSqlForAutoIncrement(Object obj);

    /**
     * Returns the last auto-increment key.  Databases like Oracle
     * which support this feature will return a result, others will
     * return null.
     *
     * @return The most recently inserted database key.
     */
    public abstract String getSequenceSql(Object obj);

    /**
     * Locks the specified table.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to lock.
     * @exception SQLException
     */
    public abstract void lockTable(Connection con,
                                   String table)
        throws SQLException;

    /**
     * Unlocks the specified table.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to unlock.
     * @exception SQLException
     */
    public abstract void unlockTable(Connection con,
                                     String table)
        throws SQLException;


    /**
     * This method is used to ignore case.
     *
     * @param in The string whose case to ignore.
     * @return The string in a case that can be ignored.
     */
    public abstract String ignoreCase(String in);

    /**
     * This method is used to ignore case in an ORDER BY clause.
     * Usually it is the same as ignoreCase, but some databases
     * (Interbase for example) does not use the same SQL in ORDER BY
     * and other clauses.
     *
     * @param in The string whose case to ignore.
     * @return The string in a case that can be ignored.
     */
    public String ignoreCaseInOrderBy(String in)
    {
        return ignoreCase(in);
    }

    /**
     * Sets the JDBC driver used by this adapter.
     *
     * @param newDriver The fully-qualified class name of the JDBC
     * driver to use.
     */
    public void setJDBCDriver(String newDriver)
    {
        JDBCDriver = newDriver;
    }

    /**
     * Gets the JDBC driver used by this adapter.
     *
     * @return The JDBC Driver classname to use for this DB.
     */
    public String getJDBCDriver()
    {
        return(JDBCDriver);
    }

    /**
     * This method is used to chek whether writing large objects to
     * the DB requires a transaction.  Since this is only true for
     * Postgres, only the DBPostgres needs to override this method and
     * return true.
     *
     * @return True if writing large objects to the DB requires a
     * transaction.
     */
    public boolean objectDataNeedsTrans()
    {
        return false;
    }

    /**
     * This method is used to chek whether the database natively
     * supports limiting the size of the resultset.
     *
     * @return True if the database natively supports limiting the
     * size of the resultset.
     */
    public boolean supportsNativeLimit()
    {
        return false;
    }

    /**
     * This method is used to chek whether the database natively
     * supports returning results starting at an offset position other
     * than 0.
     *
     * @return True if the database natively supports returning
     * results starting at an offset position other than 0.
     */
    public boolean supportsNativeOffset()
    {
        return false;
    }

   /**
    * This method is for the SqlExpression.quoteAndEscape rules.  The rule is,
    * any string in a SqlExpression with a BACKSLASH will either be changed to
    * "\\" or left as "\".  SapDB does not need the escape character.
    *
    * @return true if the database needs to escape text in SqlExpressions.
    */

    public boolean escapeText()
    {
        return true;
    }

    /**
     * This method is used to chek whether the database supports
     * limiting the size of the resultset.
     *
     * @return The limit style for the database.
     */
    public int getLimitStyle()
    {
        return LIMIT_STYLE_NONE;
    }

    /**
     * This method is used to format any date string.
     * Database can use different default date formats.
     *
     * @return The proper date formated String.
     */
    public String getDateString(String dateString)
    {
       return "\'" + dateString + "\'";
    }
}
