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

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.turbine.util.Log;

/**
 * This is used to connect to PostgresQL databases.
 *
 * <a href="http://www.pgsql.org">http://www.pgsql.org</a>
 *
 * @author <a href="mailto:hakan42@gmx.de">Hakan Tandogan</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
public class DBPostgres
    extends DB
{
    /**
     * Empty constructor.
     */
    protected DBPostgres()
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
        String s =
            new StringBuffer("UPPER(").append(in).append(")").toString();

        Log.info("DBPostgres.toUpperCase(" + in + "), returning " + s);

        return s;
    }

    /**
     * This method is used to ignore case.
     *
     * @param in The string whose case to ignore.
     * @return The string in a case that can be ignored.
     */
    public String ignoreCase(String in)
    {
        String s =
            new StringBuffer("UPPER(").append(in).append(")").toString();

        Log.info("DBPostgres.toUpperCase(" + in + "), returning " + s);

        return s;
    }

    /**
     * Gets the string delimiter (usually '\'').
     *
     * @return The delimeter.
     */
    public char getStringDelimiter()
    {
        Log.info("DBPostgres.getStringDelimiter()");

        return '\'';
    }

    /**
     * @see org.apache.turbine.util.db.adapter.DB#getIDMethodType()
     */
    public String getIDMethodType()
    {
         return AUTO_INCREMENT;
    }
 
    /**
     * @param name The name of the field (should be of type
     * <code>String</code>).
     * @return SQL to retreive the next database key.
     * @see org.apache.turbine.util.db.adapter.DB#getIDMethodSQL()
     */
    public String getIDMethodSQL(Object name)
    {
        return ("select currval('" + name + "')");
    }

    /**
     * Returns the last auto-increment key.  Databases like Oracle
     * which support this feature will return a result, others will
     * return null.
     *
     * @return The most recently inserted database key.
     */
    public String getSequenceSql(Object obj)
    {
        return "SELECT nextval('" + (String)obj + "')";
    }

    /**
     * Locks the specified table.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to lock.
     * @exception SQLException
     */
    public void lockTable(Connection con,
                          String table)
        throws SQLException
    {
    }

    /**
     * Unlocks the specified table.
     *
     * @param con The JDBC connection to use.
     * @param table The name of the table to unlock.
     * @exception SQLException
     */
    public void unlockTable(Connection con,
                            String table)
        throws SQLException
    {
    }

    /**
     * This method is used to chek whether writing large objects to
     * the DB requires a transaction.  Since this is only true for
     * Postgres, only the DBPostgres needs to override this method and
     * return true.
     *
     * @return True.
     */
    public boolean objectDataNeedsTrans()
    {
        return true;
    }

    /**
     * This method is used to chek whether the database natively
     * supports limiting the size of the resultset.
     *
     * @return True.
     */
    public boolean supportsNativeLimit()
    {
        return true;
    }

    /**
     * This method is used to chek whether the database natively
     * supports returning results starting at an offset position other
     * than 0.
     *
     * @return True.
     */
    public boolean supportsNativeOffset()
    {
        return true;
    }

    /**
     * This method is used to chek whether the database supports
     * limiting the size of the resultset.
     *
     * @return LIMIT_STYLE_POSTGRES.
     */
    public int getLimitStyle()
    {
        return DB.LIMIT_STYLE_POSTGRES;
    }
}
