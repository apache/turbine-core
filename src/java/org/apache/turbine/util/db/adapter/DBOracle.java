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

import java.lang.reflect.Method;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.ConnectionPoolDataSource;

/**
 * This code should be used for an Oracle database pool.
 *
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
public class DBOracle
    extends DB
{
    /**
     * Empty constructor.
     */
    protected DBOracle()
    {
    }

    /**
     * This method is used to ignore case.
     *
     * @param in The string to transform to upper case.
     * @return The upper case string.
     */
    public String toUpperCase(String in)
    {
        return new StringBuffer("UPPER(").append(in).append(")").toString();
    }

    /**
     * This method is used to ignore case.
     *
     * @param in The string whose case to ignore.
     * @return The string in a case that can be ignored.
     */
    public String ignoreCase(String in)
    {
        return new StringBuffer("UPPER(").append(in).append(")").toString();
    }

    /**
     * Gets the string delimiter (usually '\'').
     *
     * @return The delimeter.
     */
    public char getStringDelimiter()
    {
        return '\'';
    }

    /**
     * @see org.apache.torque.adapter.DB#getIDMethodType()
     */
    public String getIDMethodType()
    {
        return SEQUENCE;
    }

    /**
     * Returns the next key from a sequence.  Uses the following
     * implementation:
     *
     * <blockquote><code><pre>
     * select sequenceName.nextval from dual
     * </pre></code></blockquote>
     *
     * @param sequenceName The name of the sequence (should be of type
     * <code>String</code>).
     * @return SQL to retreive the next database key.
     * @see org.apache.torque.adapter.DB#getIDMethodSQL()
     */
    public String getIDMethodSQL(Object sequenceName)
    {
        return ("select " + sequenceName + ".nextval from dual");
    }

    /**
     * Locks the specified table.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to lock.
     * @exception SQLException, No Statement could be created or
     * executed.
     */
    public void lockTable(Connection con,
                          String table)
        throws SQLException
    {
        Statement statement = con.createStatement();

        StringBuffer stmt = new StringBuffer();
        stmt.append( "SELECT next_id FROM " )
        .append( table )
        .append( " FOR UPDATE" );

        statement.executeQuery( stmt.toString() );
    }

    /**
     * Unlocks the specified table.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to unlock.
     * @exception SQLException No Statement could be created or
     * executed.
     */
    public void unlockTable(Connection con, String table) throws SQLException
    {
        // Tables in Oracle are unlocked when a commit is issued.  The
        // user may have issued a commit but do it here to be sure.
        con.commit();
    }

    public ConnectionPoolDataSource getConnectionPoolDataSource()
        throws java.sql.SQLException
    {
        try
        {
            Class c = Class.forName("oracle.jdbc.pool.OracleConnectionPoolDataSource");
            ConnectionPoolDataSource instance =
                            (ConnectionPoolDataSource) c.newInstance();

            // use introspection to set the url in order to avoid import
            // of JDBC specific classes
            Method m = c.getMethod("setURL", new Class[]{ String.class });
            m.invoke(instance, new Object[]{ DB_CONNECTION });

            return (ConnectionPoolDataSource) instance;
        }
        catch (Exception e)
        {
            throw new
                SQLException("Could not create OracleConnectioPoolDataSource object: " + e);
        }
    }

   /**
    * This method is for the SqlExpression.quoteAndEscape rules.  The rule is,
    * any string in a SqlExpression with a BACKSLASH will either be changed to
    * "\\" or left as "\".  SapDB does not need the escape character.
    *
    * @return false.
    */
    
    public boolean escapeText()
    {
        return false;
    }


    /**
     * This method is used to format any date string.
     * Database can use different default date formats.
     *
     * @return The proper date formated String.
     */
  public String getDateString(String dateString)
  {
    return "TO_DATE('" + dateString + "', 'yyyy-mm-dd hh24:mi:ss..' )";
  }
}
