package org.apache.turbine.util.db;

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

import java.math.BigDecimal;
import java.sql.Connection;

import org.apache.turbine.util.db.adapter.DB;

import com.workingdogs.village.DataSet;
import com.workingdogs.village.QueryDataSet;
import com.workingdogs.village.Record;
import com.workingdogs.village.Value;

/**
 * This generator works with databases that have an sql syntax that
 * allows the retrieval of the last id used to insert a row for a
 * Connection.
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @version $Id$
 */
public class AutoIncrementIdGenerator
    implements IdGenerator
{
    private DB dbAdapter;

    /**
     * Creates an IdGenerator which will work with the specified database.
     *
     * @param dbAdapter, the adapter that knows the correct sql syntax.
     */
    public AutoIncrementIdGenerator(DB adapter)
    {
        dbAdapter = adapter;
    }

    /**
     * Returns the last ID used by this connection. 
     *
     * @param connection A Connection.
     * @param keyInfo, an Object that contains additional info.
     * @return An int with the value for the id.
     * @exception Exception Database error.
     */
    public int getIdAsInt(Connection connection, Object keyInfo)
        throws Exception
    {
        return getIdAsVillageValue(connection, keyInfo).asInt();
    }

    /**
     * Returns the last ID used by this connection. 
     *
     * @param connection A Connection.
     * @param keyInfo, an Object that contains additional info.
     * @return A long with the value for the id.
     * @exception Exception Database error.
     */
    public long getIdAsLong(Connection connection, Object keyInfo)
        throws Exception
    {
        return getIdAsVillageValue(connection, keyInfo).asLong();
    }

    /** 
     * Returns the last ID used by this connection. 
     *
     * @param connection A Connection.
     * @param keyInfo, an Object that contains additional info.
     * @return A BigDecimal with the last value auto-incremented as a
     * result of an insert.
     * @exception Exception Database error.
     */
    public BigDecimal getIdAsBigDecimal(Connection connection, Object keyInfo)
        throws Exception
    {
        return getIdAsVillageValue(connection, keyInfo).asBigDecimal();
    }


    /**
     * Returns the last ID used by this connection. 
     *
     * @param connection A Connection.
     * @param keyInfo, an Object that contains additional info.
     * @return A String with the last value auto-incremented as a
     * result of an insert.
     * @exception Exception Database error.
     */
    public String getIdAsString(Connection connection, Object keyInfo)
        throws Exception
    {
        return getIdAsVillageValue(connection, keyInfo).asString();
    }

    /**
     * A flag to determine the timing of the id generation
     *
     * @return a <code>boolean</code> value
     */
    public boolean isPriorToInsert()
    {
        return false;
    }

    /**
     * A flag to determine the timing of the id generation
     *
     * @return a <code>boolean</code> value
     */
    public boolean isPostInsert()
    {
        return true;
    }

    /**
     * A flag to determine whether a Connection is required to 
     * generate an id.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isConnectionRequired()
    {
        return true;
    }

    /**
     * Returns the last ID used by this connection. 
     *
     * @param connection A Connection.
     * @param keyInfo, an Object that contains additional info.
     * @return A Village Value with the last value auto-incremented as a
     * result of an insert.
     * @exception Exception Database error.
     */
    private Value getIdAsVillageValue(Connection connection, 
                                      Object keyInfo)
        throws Exception
    {
        String IDSql = dbAdapter.getIdSqlForAutoIncrement(keyInfo);
        Value id = null;
        QueryDataSet qds = null;
        try
        {
            qds = new QueryDataSet(connection, IDSql);
            qds.fetchRecords(1);
            Record rec = qds.getRecord(0);
            id = rec.getValue(1);
        }
        finally
        {
            if (qds != null) qds.close();
        }
        return id;
    }
}
