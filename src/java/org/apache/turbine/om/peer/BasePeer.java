package org.apache.turbine.om.peer;

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

import com.workingdogs.village.Column;
import com.workingdogs.village.DataSet;
import com.workingdogs.village.KeyDef;
import com.workingdogs.village.QueryDataSet;
import com.workingdogs.village.Record;
import com.workingdogs.village.Schema;
import com.workingdogs.village.TableDataSet;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.om.NumberKey;
import org.apache.turbine.om.ObjectKey;
import org.apache.turbine.om.SimpleKey;
import org.apache.turbine.om.StringKey;
import org.apache.turbine.services.db.TurbineDB;
import org.apache.turbine.services.logging.Logger;
import org.apache.turbine.services.logging.TurbineLogging;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.StringStackBuffer;
import org.apache.turbine.util.db.Criteria;
import org.apache.turbine.util.db.IdGenerator;
import org.apache.turbine.util.db.Query;
import org.apache.turbine.util.db.SqlExpression;
import org.apache.turbine.util.db.adapter.DB;
import org.apache.turbine.util.db.map.ColumnMap;
import org.apache.turbine.util.db.map.DatabaseMap;
import org.apache.turbine.util.db.map.MapBuilder;
import org.apache.turbine.util.db.map.TableMap;
import org.apache.turbine.util.db.pool.DBConnection;

/**
 * This is the base class for all Peer classes in the system.  Peer
 * classes are responsible for isolating all of the database access
 * for a specific business object.  They execute all of the SQL
 * against the database.  Over time this class has grown to include
 * utility methods which ease execution of cross-database queries and
 * the implementation of concrete Peers.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @version $Id$
 */
public abstract class BasePeer
{
    /** Constant criteria key to reference ORDER BY columns. */
    public static final String ORDER_BY = "ORDER BY";

    /*
     * Constant criteria key to remove Case Information from
     * search/ordering criteria.
     */
    public static final String IGNORE_CASE = "IgNOrE cAsE";

    /** Classes that implement this class should override this value. */
    public static final String TABLE_NAME = "TABLE_NAME";

    /** The Turbine default MapBuilder. */
    public static final String DEFAULT_MAP_BUILDER =
        "org.apache.turbine.util.db.map.TurbineMapBuilder";

    /** Hashtable that contains the cached mapBuilders. */
    private static Hashtable mapBuilders = new Hashtable(5);

    /**
     * Converts a hashtable to a byte array for storage/serialization.
     *
     * @param hash The Hashtable to convert.
     * @return A byte[] with the converted Hashtable.
     * @exception Exception, a generic exception.
     */
    public static byte[] hashtableToByteArray( Hashtable hash )
        throws Exception
    {
        Hashtable saveData = new Hashtable(hash.size());
        String key = null;
        Object value = null;
        byte[] byteArray = null;

        Enumeration keys = hash.keys();
        while(keys.hasMoreElements())
        {
            key = (String) keys.nextElement();
            value = hash.get(key);
            if ( value instanceof Serializable )
                saveData.put ( key, value );
        }

        ByteArrayOutputStream baos = null;
        BufferedOutputStream bos = null;
        ObjectOutputStream out = null;
        try
        {
            // These objects are closed in the finally.
            baos = new ByteArrayOutputStream();
            bos = new BufferedOutputStream(baos);
            out = new ObjectOutputStream(bos);

            out.writeObject(saveData);
            out.flush();
            bos.flush();
            byteArray = baos.toByteArray();
        }
        finally
        {
            if (out != null) out.close();
            if (bos != null) bos.close();
            if (baos != null) baos.close();
        }
        return byteArray;
    }

    /**
     * Sets up a Schema for a table.  This schema is then normally
     * used as the argument for initTableColumns().
     *
     * @param tableName The name of the table.
     * @return A Schema.
     */
    public static Schema initTableSchema(String tableName)
    {
        return initTableSchema(tableName, null);
    }

    /**
     * Sets up a Schema for a table.  This schema is then normally
     * used as the argument for initTableColumns
     *
     * @param tableName The propery name for the database in the
     * Turbineresources file.
     * @param dbName The name of the database.
     * @return A Schema.
     */
    public static Schema initTableSchema(String tableName,
                                         String dbName)
    {
        Schema schema = null;
        DBConnection db = null;

        try
        {
            if (dbName == null)
            {
                // Get a connection to the db.
                db = TurbineDB.getConnection();
            }
            else
            {
                // Get a connection to the db.
                db = TurbineDB.getConnection( dbName );
            }

            Connection connection = db.getConnection();

            schema = new Schema().schema(connection, tableName);
        }
        catch(Exception e)
        {
            Log.error(e);
            throw new Error("Error in BasePeer.initTableSchema(" +
                            tableName + "): " +
                            e.getMessage());
        }
        finally
        {
            try
            {
                TurbineDB.releaseConnection(db);
            }
            catch(Exception e)
            {
            }
        }
        return schema;
    }

    /**
     * Creates a Column array for a table based on its Schema.
     *
     * @param schema A Schema object.
     * @return A Column[].
     */
    public static Column[] initTableColumns(Schema schema)
    {
        Column[] columns = null;
        try
        {
            int numberOfColumns = schema.numberOfColumns();
            columns = new Column[numberOfColumns];
            for (int i=0; i<numberOfColumns; i++)
            {
                columns[i] = schema.column(i+1);
            }
        }
        catch(Exception e)
        {
            Log.error(e);
            throw new Error("Error in BasePeer.initTableColumns(): " +
                            e.getMessage());
        }
        return columns;
    }

    /**
     * Convenience method to create a String array of column names.
     *
     * @param columns A Column[].
     * @return A String[].
     */
    public static String[] initColumnNames(Column[] columns)
    {
        String[] columnNames = null;
        columnNames = new String[columns.length];
        for (int i = 0; i < columns.length; i++)
        {
            columnNames[i] = columns[i].name().toUpperCase();
        }
        return columnNames;
    }

    /**
     * Convenience method to create a String array of criteria keys.
     * Primary use is with TurbineUserPeer.
     *
     * @param tableName Name of table.
     * @param columnNames A String[].
     * @return A String[].
     */
    public static String[] initCriteriaKeys(String tableName,
                                            String[] columnNames)
    {
        String[] keys = new String[columnNames.length];
        for(int i = 0; i < columnNames.length; i++)
        {
            keys[i] = tableName + "." + columnNames[i].toUpperCase();
        }
        return keys;
    }

    /**
     * Begin a transaction.  This method will fallback gracefully to
     * return a normal connection, if the database being accessed does
     * not support transactions.
     *
     * @param dbName Name of database.
     * @return The DBConnection for the transaction.
     * @exception Exception, a generic exception.
     */
    public static DBConnection beginTransaction(String dbName)
        throws Exception
    {
        DBConnection dbCon = TurbineDB.getConnection( dbName );
        if ( dbCon.getConnection().getMetaData().supportsTransactions() )
        {
            dbCon.setAutoCommit(false);
        }

        return dbCon;
    }

    /**
     * Commit a transaction.  This method takes care of releasing the
     * connection after the commit.  in databases that do not support
     * transactions, it only returns the connection.
     *
     * @param dbCon The DBConnection for the transaction.
     * @exception Exception, a generic exception.
     */
    public static void commitTransaction(DBConnection dbCon)
        throws Exception
    {
        try
        {
            if ( dbCon.getConnection().getMetaData().supportsTransactions() )
            {
                dbCon.commit();
                dbCon.setAutoCommit(true);
            }
        }
        finally
        {
            // Release the connection to the pool.
            TurbineDB.releaseConnection( dbCon );
        }
    }

    /**
     * Roll back a transaction in databases that support transactions.
     * It also releases the connection.  in databases that do not support
     * transactions, this method will log the attempt and release the
     * connection.
     *
     * @param dbCon The DBConnection for the transaction.
     * @exception Exception, a generic exception.
     */
    public static void rollBackTransaction(DBConnection dbCon)
        throws Exception
    {
        try
        {
            if ( dbCon.getConnection().getMetaData().supportsTransactions() )
            {
                dbCon.rollback();
                dbCon.setAutoCommit(true);
            }
            else
            {
                Log.error("An attempt was made to rollback a transaction but the"
                          + " database did not allow the operation to be rolled back.");
            }
        }
        finally
        {
            // Release the connection to the pool.
            TurbineDB.releaseConnection( dbCon );
        }
    }


    /**
     * Convenience method that uses straight JDBC to delete multiple
     * rows.  Village throws an Exception when multiple rows are
     * deleted.
     *
     * @param dbCon A DBConnection.
     * @param table The table to delete records from.
     * @param column The column in the where clause.
     * @param value The value of the column.
     * @exception Exception, a generic exception.
     */
    public static void deleteAll( DBConnection dbCon,
                                  String table,
                                  String column,
                                  int value )
        throws Exception
    {
        Connection conn = dbCon.getConnection();
        Statement statement = null;

        try
        {
            statement = conn.createStatement();

            StringBuffer query = new StringBuffer();
            query.append( "DELETE FROM " )
            .append( table )
            .append( " WHERE " )
            .append( column )
            .append( " = " )
            .append( value );

            statement.executeUpdate( query.toString() );
        }
        finally
        {
            if (statement != null) statement.close();
        }
    }

    /**
     * Convenience method that uses straight JDBC to delete multiple
     * rows.  Village throws an Exception when multiple rows are
     * deleted.  This method attempts to get the default database from
     * the pool.
     *
     * @param table The table to delete records from.
     * @param column The column in the where clause.
     * @param value The value of the column.
     * @exception Exception, a generic exception.
     */
    public static void deleteAll( String table,
                                  String column,
                                  int value )
        throws Exception
    {
        DBConnection dbCon = null;
        try
        {
            // Get a connection to the db.
            dbCon = TurbineDB.getConnection();

            deleteAll( dbCon, table, column, value );
        }
        finally
        {
            TurbineDB.releaseConnection(dbCon);
        }
    }

    /**
     * Method to perform deletes based on values and keys in a
     * Criteria.
     *
     * @param criteria The criteria to use.
     * @exception Exception, a generic exception.
     */
    public static void doDelete(Criteria criteria)
        throws Exception
    {
        DBConnection dbCon = null;

        // Transaction stuff added for postgres.
        boolean doTransaction = (TurbineDB.getDB(criteria.getDbName()).
            objectDataNeedsTrans() &&
            criteria.containsObjectColumn(criteria.getDbName()));

        try
        {
            // Get a connection to the db.
            if (doTransaction)
            {
                dbCon = beginTransaction(criteria.getDbName());
            }
            else
            {
                dbCon = TurbineDB.getConnection( criteria.getDbName() );
            }
            doDelete(criteria, dbCon);
        }
        finally
        {
            if (doTransaction)
            {
                commitTransaction(dbCon);
            }
            else
            {
                TurbineDB.releaseConnection(dbCon);
            }
        }
    }

    /**
     * Method to perform deletes based on values and keys in a
     * Criteria.
     *
     * @param criteria The criteria to use.
     * @param dbCon A DBConnection.
     * @exception Exception, a generic exception.
     */
    public static void doDelete(Criteria criteria,
                                DBConnection dbCon)
        throws Exception
    {
        DB db = TurbineDB.getDB( criteria.getDbName() );
        DatabaseMap dbMap = TurbineDB.getDatabaseMap( criteria.getDbName() );
        Connection connection = dbCon.getConnection();

        // Set up a list of required tables and add extra entries to
        // criteria if directed to delete all related records.
        // StringStackBuffer.add() only adds element if it is unique.
        StringStackBuffer tables = new StringStackBuffer();
        Enumeration e = criteria.keys();
        while(e.hasMoreElements())
        {
            String key = (String)e.nextElement();
            Criteria.Criterion c = criteria.getCriterion(key);
            String[] tableNames = c.getAllTables();
            for (int i=0; i<tableNames.length; i++)
            {
                String tableName2 = criteria.getTableForAlias(tableNames[i]);
                if ( tableName2 != null )
                {
                    tables.add(
                        new StringBuffer(tableNames[i].length() +
                                         tableName2.length() + 1)
                        .append(tableName2).append(' ').append(tableNames[i])
                        .toString() );
                }
                else
                {
                    tables.add(tableNames[i]);
                }
            }

            if ( criteria.isCascade() )
            {
                // This steps thru all the columns in the database.
                TableMap[] tableMaps = dbMap.getTables();
                for (int i=0; i<tableMaps.length; i++)
                {
                    ColumnMap[] columnMaps = tableMaps[i].getColumns();
                    for (int j=0; j<columnMaps.length; j++)
                    {
                        // Only delete rows where the foreign key is
                        // also a primary key.  Other rows need
                        // updateing, but that is not implemented.
                        if ( columnMaps[j].isForeignKey()
                            && columnMaps[j].isPrimaryKey()
                            && key.equals(columnMaps[j].getRelatedName()) )
                        {
                            tables.add(tableMaps[i].getName());
                            criteria.add(columnMaps[j].getFullyQualifiedName(),
                                         criteria.getValue(key));
                        }
                    }
                }
            }
        }

        for (int i=0; i<tables.size(); i++)
        {
            KeyDef kd = new KeyDef();
            StringStackBuffer whereClause = new StringStackBuffer();

            ColumnMap[] columnMaps =
                dbMap.getTable( tables.get(i) ).getColumns();
            for (int j=0; j<columnMaps.length; j++)
            {
                ColumnMap colMap = columnMaps[j];
                if ( colMap.isPrimaryKey() )
                {
                    kd.addAttrib( colMap.getColumnName() );
                }
                String key = new StringBuffer(colMap.getTableName())
                    .append('.').append(colMap.getColumnName()).toString();
                if ( criteria.containsKey(key) )
                {
                    if ( criteria.getComparison(key).equals(Criteria.CUSTOM) )
                    {
                        whereClause.add( criteria.getString(key) );
                    }
                    else
                    {
                        whereClause.add( SqlExpression.build( colMap.getColumnName(),
                            criteria.getValue(key),
                            criteria.getComparison(key),
                            criteria.isIgnoreCase(),
                            db));
                    }
                }
            }

            // Execute the statement.
            TableDataSet tds = null;
            try
            {
                tds = new TableDataSet(connection, tables.get(i), kd);
                String sqlSnippet = whereClause.toString(" AND ");
                TurbineLogging.getLogger(TurbineConstants.SQL_LOG_FACILITY)
                    .debug("BasePeer.doDelete: whereClause=" + sqlSnippet);
                tds.where(sqlSnippet);
                tds.fetchRecords();
                if ( tds.size() > 1 && criteria.isSingleRecord() )
                {
                    handleMultipleRecords(tds);
                }
                for (int j=0; j<tds.size(); j++)
                {
                    Record rec = tds.getRecord(j);
                    rec.markToBeDeleted();
                    rec.save();
                }
            }
            finally
            {
                if (tds != null) tds.close();
            }
        }
    }

    /**
     * Method to perform inserts based on values and keys in a
     * Criteria.
     *
     * <p>
     *
     * If the primary key is auto incremented the data in Criteria
     * will be inserted and the auto increment value will be returned.
     *
     * <p>
     *
     * If the primary key is included in Criteria then that value will
     * be used to insert the row.
     *
     * <p>
     *
     * If no primary key is included in Criteria then we will try to
     * figure out the primary key from the database map and insert the
     * row with the next available id using util.db.IDBroker.
     *
     * <p>
     *
     * If no primary key is defined for the table the values will be
     * inserted as specified in Criteria and -1 will be returned.
     *
     * @param criteria Object containing values to insert.
     * @return An Object which is the id of the row that was inserted
     * (if the table has a primary key) or null (if the table does not
     * have a primary key).
     * @exception Exception, a generic exception.
     */
    public static ObjectKey doInsert(Criteria criteria)
        throws Exception
    {
        DBConnection dbCon = null;
        ObjectKey id = null;

        // Transaction stuff added for postgres.
        boolean doTransaction = (TurbineDB.getDB(criteria.getDbName()).
            objectDataNeedsTrans() &&
            criteria.containsObjectColumn(criteria.getDbName()));

        try
        {
            // Get a connection to the db.
            if (doTransaction)
            {
                dbCon = beginTransaction(criteria.getDbName());
            }
            else
            {
                dbCon = TurbineDB.getConnection( criteria.getDbName() );
            }
            id = doInsert(criteria, dbCon);
        }
        finally
        {
            if (doTransaction)
            {
                commitTransaction(dbCon);
            }
            else
            {
                TurbineDB.releaseConnection(dbCon);
            }
        }
        return id;
    }


    /**
     * Method to perform inserts based on values and keys in a
     * Criteria.
     *
     * <p>
     *
     * If the primary key is auto incremented the data in Criteria
     * will be inserted and the auto increment value will be returned.
     *
     * <p>
     *
     * If the primary key is included in Criteria then that value will
     * be used to insert the row.
     *
     * <p>
     *
     * If no primary key is included in Criteria then we will try to
     * figure out the primary key from the database map and insert the
     * row with the next available id using util.db.IDBroker.
     *
     * <p>
     *
     * If no primary key is defined for the table the values will be
     * inserted as specified in Criteria and null will be returned.
     *
     * @param criteria Object containing values to insert.
     * @param dbCon A DBConnection.
     * @return An Object which is the id of the row that was inserted
     * (if the table has a primary key) or null (if the table does not
     * have a primary key).
     * @exception Exception, a generic exception.
     */
    public static ObjectKey doInsert(Criteria criteria,
                                     DBConnection dbCon)
        throws Exception
    {
        SimpleKey id = null;

        // Get the table name and method for determining the primary
        // key value.
        String tableName = null;
        Enumeration keys = criteria.keys();
        if (keys.hasMoreElements())
        {
            tableName = criteria.getTableName((String)keys.nextElement());
        }
        else
        {
            throw new Exception("Database insert attempted without anything specified to insert");
        }
        DatabaseMap dbMap = TurbineDB.getDatabaseMap( criteria.getDbName() );
        TableMap tableMap = dbMap.getTable(tableName);
        Object keyInfo = tableMap.getPrimaryKeyMethodInfo();
        IdGenerator keyGen = tableMap.getIdGenerator();

        ColumnMap pk = getPrimaryKey(criteria);
        // only get a new key value if you need to
        // the reason is that a primary key might be defined
        // but you are still going to set its value. for example:
        // a join table where both keys are primary and you are
        // setting both columns with your own values
        boolean info = false;

        if (!criteria.containsKey(pk.getFullyQualifiedName()))
        {
            // If the keyMethod is SEQUENCE or IDBROKERTABLE, get the id
            // before the insert.

            if (keyGen.isPriorToInsert())
            {
                if ( pk.getType() instanceof Number )
                {
                    id = new NumberKey( tableMap.getIdGenerator()
                        .getIdAsBigDecimal(dbCon.getConnection(), keyInfo) );
                }
                else
                {
                    id = new StringKey( tableMap.getIdGenerator()
                        .getIdAsString(dbCon.getConnection(), keyInfo) );
                }
                criteria.add( pk.getFullyQualifiedName(), id );
            }
        }

        // Set up Village for the insert.
        TableDataSet tds = null;
        try
        {
            tds = new TableDataSet(dbCon.getConnection(), tableName );
            Record rec = tds.addRecord();
            insertOrUpdateRecord(rec, tableName, criteria);
        }
        finally
        {
            if (tds != null) tds.close();
        }

        // If the primary key column is auto-incremented, get the id
        // now.
        if ((keyGen != null) && (keyGen.isPostInsert()))
        {
            if ( pk.getType() instanceof Number )
            {
                id = new NumberKey( tableMap.getIdGenerator()
                    .getIdAsBigDecimal(dbCon.getConnection(), keyInfo) );
            }
            else
            {
                id = new StringKey( tableMap.getIdGenerator()
                    .getIdAsString(dbCon.getConnection(), keyInfo) );
            }
        }

        return id;
    }

    /**
     * Grouping of code used in both doInsert() and doUpdate()
     * methods.  Sets up a Record for saving.
     *
     * @param rec A Record.
     * @param tableName Name of table.
     * @param criteria A Criteria.
     * @exception Exception, a generic exception.
     */
    private static void insertOrUpdateRecord(Record rec,
                                             String tableName,
                                             Criteria criteria)
        throws Exception
    {
        DatabaseMap dbMap = TurbineDB.getDatabaseMap( criteria.getDbName() );

        ColumnMap[] columnMaps = dbMap.getTable( tableName ).getColumns();
        boolean shouldSave = false;
        for (int j=0; j<columnMaps.length; j++)
        {
            ColumnMap colMap = columnMaps[j];
            String key = new StringBuffer(colMap.getTableName())
                .append('.').append(colMap.getColumnName()).toString();
            if ( criteria.containsKey(key) )
            {
                // A village Record.setValue( String, Object ) would
                // be nice here.
                Object obj = criteria.getValue(key);
                if ( obj instanceof SimpleKey )
                {
                    obj = ((SimpleKey)obj).getValue();
                }
                if (obj == null)
                {
                    rec.setValueNull(colMap.getColumnName());
                }
                else if ( obj instanceof String )
                    rec.setValue( colMap.getColumnName(),
                                  (String)obj );
                else if ( obj instanceof Integer)
                    rec.setValue( colMap.getColumnName(),
                                  criteria.getInt(key) );
                else if ( obj instanceof BigDecimal)
                    rec.setValue( colMap.getColumnName(),
                                  (BigDecimal)obj );
                else if ( obj instanceof Long)
                    rec.setValue( colMap.getColumnName(),
                                  criteria.getLong(key) );
                else if ( obj instanceof java.util.Date)
                    rec.setValue( colMap.getColumnName(),
                                  (java.util.Date)obj );
                else if ( obj instanceof Float)
                    rec.setValue( colMap.getColumnName(),
                                  criteria.getFloat(key) );
                else if ( obj instanceof Double)
                    rec.setValue( colMap.getColumnName(),
                                  criteria.getDouble(key) );
                else if ( obj instanceof Hashtable )
                    rec.setValue( colMap.getColumnName(),
                                  hashtableToByteArray( (Hashtable)obj ) );
                else if ( obj instanceof byte[])
                    rec.setValue( colMap.getColumnName(),
                                  (byte[])obj);
                else if ( obj instanceof Boolean)
                    rec.setValue( colMap.getColumnName(),
                                   criteria.getBoolean(key) ? 1 : 0);
                shouldSave = true;
             }
        }
        if ( shouldSave )
            rec.save();
        else
           throw new Exception ( "BasePeer.doInsert() - Nothing to insert" );
    }

    /**
     * Method to create an SQL query based on values in a Criteria.
     *
     * @param criteria A Criteria.
     * @exception Exception Trouble creating the query string.
     */
    public static String createQueryString( Criteria criteria )
        throws Exception
    {
        Query query = new Query();
        DB db = TurbineDB.getDB( criteria.getDbName() );
        DatabaseMap dbMap = TurbineDB.getDatabaseMap( criteria.getDbName() );

        StringStackBuffer selectModifiers = query.getSelectModifiers();
        StringStackBuffer selectClause = query.getSelectClause();
        StringStackBuffer fromClause = query.getFromClause();
        StringStackBuffer whereClause = query.getWhereClause();
        StringStackBuffer orderByClause = query.getOrderByClause();

        StringStackBuffer orderBy = criteria.getOrderByColumns();
        boolean ignoreCase = criteria.isIgnoreCase();
        StringStackBuffer select = criteria.getSelectColumns();
        Hashtable aliases = criteria.getAsColumns();
        StringStackBuffer modifiers = criteria.getSelectModifiers();

        for (int i=0; i<modifiers.size(); i++)
        {
            selectModifiers.add( modifiers.get(i) );
        }

        for (int i=0; i<select.size(); i++)
        {
            String columnName = select.get(i);
            String tableName = null;
            selectClause.add(columnName);
            int parenPos = columnName.indexOf('(');
            if (parenPos == -1)
            {
                tableName = columnName.substring(0,
                                                 columnName.indexOf('.') );
            }
            else
            {
                tableName = columnName.substring(parenPos + 1,
                                                 columnName.indexOf('.') );
                // functions may contain qualifiers so only take the last
                // word as the table name.
                int lastSpace = tableName.lastIndexOf(' ');
                if ( lastSpace != -1 ) 
                {
                    tableName = tableName.substring(lastSpace+1);
                }
            }
            String tableName2 = criteria.getTableForAlias(tableName);
            if ( tableName2 != null )
            {
                fromClause.add(
                    new StringBuffer(tableName.length() +
                                     tableName2.length() + 1)
                    .append(tableName2).append(' ').append(tableName)
                    .toString() );
            }
            else
            {
                fromClause.add(tableName);
            }
        }

        Iterator it = aliases.keySet().iterator();
        while(it.hasNext())
        {
          String key = (String)it.next();
          selectClause.add((String)aliases.get(key) + " AS " + key);
        }

        Enumeration e = criteria.keys();
        while (e.hasMoreElements())
        {
            String key = (String)e.nextElement();
            Criteria.Criterion criterion =
                (Criteria.Criterion)criteria.getCriterion(key);
            Criteria.Criterion[] someCriteria =
                criterion.getAttachedCriterion();
            String table = null;
            for (int i=0; i<someCriteria.length; i++)
            {
                String tableName = someCriteria[i].getTable();
                table = criteria.getTableForAlias(tableName);
                if ( table != null )
                {
                    fromClause.add(
                        new StringBuffer(tableName.length() +
                                         table.length() + 1)
                        .append(table).append(' ').append(tableName)
                        .toString() );
                }
                else
                {
                    fromClause.add(tableName);
                    table = tableName;
                }

                boolean ignorCase = (criteria.isIgnoreCase() &&
                    (dbMap.getTable(table).getColumn(
                    someCriteria[i].getColumn()).getType() instanceof String));

                someCriteria[i].setIgnoreCase(ignorCase);
            }

            criterion.setDB(db);
            whereClause.add( criterion.toString() );

        }

        List join = criteria.getJoinL();
        if ( join != null)
        {
            for ( int i=0; i<join.size(); i++ )
            {
                String join1 = (String)join.get(i);
                String join2 = (String)criteria.getJoinR().get(i);

                String tableName = join1.substring(0, join1.indexOf('.'));
                String table = criteria.getTableForAlias(tableName);
                if ( table != null )
                {
                    fromClause.add(
                        new StringBuffer(tableName.length() +
                                         table.length() + 1)
                        .append(table).append(' ').append(tableName)
                        .toString() );
                }
                else
                {
                    fromClause.add(tableName);
                }

                int dot =  join2.indexOf('.');
                tableName = join2.substring(0, dot);
                table = criteria.getTableForAlias(tableName);
                if ( table != null )
                {
                    fromClause.add(
                        new StringBuffer(tableName.length() +
                                         table.length() + 1)
                        .append(table).append(' ').append(tableName)
                        .toString() );
                }
                else
                {
                    fromClause.add(tableName);
                    table = tableName;
                }

                boolean ignorCase = (criteria.isIgnoreCase() &&
                    (dbMap.getTable(table).getColumn(
                        join2.substring(dot+1, join2.length()) )
                    .getType() instanceof String));

                whereClause.add(
                    SqlExpression.buildInnerJoin(join1, join2,
                                                 ignorCase, db) );
            }
        }

        if ( orderBy != null && orderBy.size() > 0)
        {
            // Check for each String/Character column and apply
            // toUpperCase().
            for (int i=0; i<orderBy.size(); i++)
            {
                String orderByColumn = orderBy.get(i);
                String table = orderByColumn.substring(0,orderByColumn.indexOf('.') );
                // See if there's a space (between the column list and sort
                // order in ORDER BY table.column DESC).
                int spacePos = orderByColumn.indexOf(' ');
                String columnName;
                if (spacePos == -1)
                    columnName = orderByColumn.substring(orderByColumn.indexOf('.') + 1);
                else
                    columnName = orderByColumn.substring(orderByColumn.indexOf('.') + 1, spacePos);
                ColumnMap column = dbMap.getTable(table).getColumn( columnName );
                if ( column.getType() instanceof String )
                {
                    if (spacePos == -1)
                        orderByClause.add( db.ignoreCaseInOrderBy(orderByColumn) );
                    else
                        orderByClause.add( db.ignoreCaseInOrderBy(orderByColumn.substring(0, spacePos)) + orderByColumn.substring(spacePos) );
                }
                else
                {
                    orderByClause.add(orderByColumn);
                }
            }
        }

        setLimit(criteria, db, query);

        String sql = query.toString();
        TurbineLogging.getLogger(TurbineConstants.SQL_LOG_FACILITY).debug(sql);
        return sql;
    }


    /**
     * Returns all results.
     *
     * @param criteria A Criteria.
     * @return Vector of Record objects.
     * @exception Exception, a generic exception.
     */
    public static Vector doSelect(Criteria criteria)
        throws Exception
    {
        // Transaction stuff added for postgres.
        Vector results = null;
        if (TurbineDB.getDB(criteria.getDbName())
              .objectDataNeedsTrans() &&
              criteria.containsObjectColumn(criteria.getDbName()))
        {
            DBConnection dbCon = beginTransaction(criteria.getDbName());
            try
            {
                results = executeQuery( createQueryString(criteria),
                                        criteria.isSingleRecord(), dbCon );
                commitTransaction(dbCon);
            }
            catch (Exception e)
            {
                // make sure to return connection
                rollBackTransaction(dbCon);
                throw e;
            }
        }
        else
        {
            results = executeQuery( createQueryString(criteria),
                                    criteria.getDbName(),
                                    criteria.isSingleRecord() );
        }
        return results;
    }

    /**
     * Returns all results.
     *
     * @param criteria A Criteria.
     * @param dbCon A DBConnection.
     * @return Vector of Record objects.
     * @exception Exception, a generic exception.
     */
    public static Vector doSelect(Criteria criteria,
                                  DBConnection dbCon)
        throws Exception
    {
        return executeQuery( createQueryString(criteria),
                             criteria.isSingleRecord(), dbCon );
    }


    /**
     * Utility method which executes a given sql statement.  This
     * method should be used for select statements only.  Use
     * executeStatement for update, insert, and delete operations.
     *
     * @param queryString A String with the sql statement to execute.
     * @return Vector of Record objects.
     * @exception Exception, a generic exception.
     */
    public static Vector executeQuery(String queryString)
        throws Exception
    {
        return executeQuery(queryString, TurbineDB.getDefaultDB(), false);
    }

    /**
     * Utility method which executes a given sql statement.  This
     * method should be used for select statements only.  Use
     * executeStatement for update, insert, and delete operations.
     *
     * @param queryString A String with the sql statement to execute.
     * @param dbName The database to connect to.
     * @return Vector of Record objects.
     * @exception Exception, a generic exception.
     */
    public static Vector executeQuery(String queryString,
                                      String dbName)
        throws Exception
    {
        return executeQuery(queryString, dbName, false);
    }

    /**
     * Method for performing a SELECT.  Returns all results.
     *
     * @param queryString A String with the sql statement to execute.
     * @param dbName The database to connect to.
     * @param singleRecord Whether or not we want to select only a
     * single record.
     * @return Vector of Record objects.
     * @exception Exception, a generic exception.
     */
    public static Vector executeQuery(String queryString,
                                      String dbName,
                                      boolean singleRecord)
        throws Exception
    {
        return executeQuery(queryString, 0, -1, dbName, singleRecord);
    }

    /**
     * Method for performing a SELECT.  Returns all results.
     *
     * @param queryString A String with the sql statement to execute.
     * @param dbName The database to connect to.
     * @param singleRecord Whether or not we want to select only a
     * single record.
     * @param dbCon A DBConnection.
     * @return Vector of Record objects.
     * @exception Exception, a generic exception.
     */
    public static Vector executeQuery(String queryString,
                                      boolean singleRecord,
                                      DBConnection dbCon)
        throws Exception
    {
        return executeQuery(queryString, 0, -1, singleRecord, dbCon);
    }


    /**
     * Method for performing a SELECT.
     *
     * @param queryString A String with the sql statement to execute.
     * @param start The first row to return.
     * @param numberOfResults The number of rows to return.
     * @param dbName The database to connect to.
     * @param singleRecord Whether or not we want to select only a
     * single record.
     * @return Vector of Record objects.
     * @exception Exception, a generic exception.
     */
    public static Vector executeQuery(String queryString,
                                      int start,
                                      int numberOfResults,
                                      String dbName,
                                      boolean singleRecord)
        throws Exception
    {
        DBConnection db = null;
        Vector results = null;
        try
        {
            // get a connection to the db
            db = TurbineDB.getConnection( dbName );
            // execute the query
            results = executeQuery(queryString, start, numberOfResults,
                                   singleRecord, db);
        }
        finally
        {
            TurbineDB.releaseConnection(db);
        }
        return results;
    }

    /**
     * Method for performing a SELECT.  Returns all results.
     *
     * @param queryString A String with the sql statement to execute.
     * @param start The first row to return.
     * @param numberOfResults The number of rows to return.
     * @param dbName The database to connect to.
     * @param singleRecord Whether or not we want to select only a
     * single record.
     * @param dbCon A DBConnection.
     * @return Vector of Record objects.
     * @exception Exception, a generic exception.
     */
    public static Vector executeQuery(String queryString,
                                      int start,
                                      int numberOfResults,
                                      boolean singleRecord,
                                      DBConnection dbCon)
        throws Exception
    {
        Connection connection = dbCon.getConnection();

        QueryDataSet qds = null;
        Vector results = new Vector();
        try
        {
            // execute the query
            qds = new QueryDataSet( connection, queryString );
            results = getSelectResults( qds, start, numberOfResults,
                                        singleRecord);
        }
        finally
        {
            if (qds != null) qds.close();
        }
        return results;
    }


    /**
     * Returns all records in a QueryDataSet as a Vector of Record
     * objects.  Used for functionality like util.db.LargeSelect.
     *
     * @param qds A QueryDataSet.
     * @return Vector of Record objects.
     * @exception Exception, a generic exception.
     */
    public static Vector getSelectResults( QueryDataSet qds )
        throws Exception
    {
        return getSelectResults( qds, 0, -1, false);
    }

    /**
     * Returns all records in a QueryDataSet as a Vector of Record
     * objects.  Used for functionality like util.db.LargeSelect.
     *
     * @param qds A QueryDataSet.
     * @param singleRecord Whether or not we want to select only a
     * single record.
     * @exception Exception, a generic exception.
     */
    public static Vector getSelectResults( QueryDataSet qds,
                                           boolean singleRecord )
        throws Exception
    {
        return getSelectResults(qds, 0, -1, singleRecord);
    }

    /**
     * Returns numberOfResults records in a QueryDataSet as a Vector
     * of Record objects.  Starting at record 0.  Used for
     * functionality like util.db.LargeSelect.
     *
     * @param qds A QueryDataSet.
     * @param numberOfResults The number of results to return.
     * @param singleRecord Whether or not we want to select only a
     * single record.
     * @exception Exception, a generic exception.
     */
    public static Vector getSelectResults( QueryDataSet qds,
                                           int numberOfResults,
                                           boolean singleRecord )
        throws Exception
    {
        Vector results = null;
        if (numberOfResults != 0)
        {
            results = getSelectResults(qds, 0, numberOfResults, singleRecord);
        }
        return results;
    }

    /**
     * Returns numberOfResults records in a QueryDataSet as a Vector
     * of Record objects.  Starting at record start.  Used for
     * functionality like util.db.LargeSelect.
     *
     * @param qds A QueryDataSet.
     * @param start where to start retrieving Records.
     * @param numberOfResults The number of results to return.
     * @param singleRecord Whether or not we want to select only a
     * single record.
     * @exception Exception, a generic exception.
     */
    public static Vector getSelectResults( QueryDataSet qds,
                                           int start,
                                           int numberOfResults,
                                           boolean singleRecord )
        throws Exception
    {
        Vector results;
        if ( numberOfResults <= 0 )
        {
            results = new Vector();
            qds.fetchRecords();
        }
        else
        {
            results = new Vector(numberOfResults);
            qds.fetchRecords(start, numberOfResults);
        }
        if ( qds.size() > 1 && singleRecord )
        {
            handleMultipleRecords(qds);
        }

        // Return a Vector of Record objects.
        for ( int i=0; i<qds.size(); i++ )
        {
            Record rec = qds.getRecord(i);
            results.addElement(rec);
        }
        return results;
    }

    /**
     * Helper method which returns the primary key contained
     * in the given Criteria object.
     *
     * @param criteria A Criteria.
     * @return ColumnMap if the Criteria object contains a primary
     * key, or null if it doesn't.
     * @exception Exception, a generic exception.
     */
    private static ColumnMap getPrimaryKey(Criteria criteria)
        throws Exception
    {
        // Assume all the keys are for the same table.
        String key = (String)criteria.keys().nextElement();

        String table = criteria.getTableName(key);
        ColumnMap pk = null;

        if ( !table.equals("") )
        {
            DatabaseMap dbMap = TurbineDB.getDatabaseMap(criteria.getDbName());
            if (dbMap == null)
                throw new Exception ("dbMap is null");
            if (dbMap.getTable(table) == null)
                throw new Exception ("dbMap.getTable() is null");

            ColumnMap[] columns = dbMap.getTable(table).getColumns();

            for (int i=0; i<columns.length; i++)
            {
                if ( columns[i].isPrimaryKey() )
                {
                    pk = columns[i];
                    break;
                }
            }
        }
        return pk;
    }

    /**
     * Use this method for performing an update of the kind:
     *
     * <p>
     *
     * "WHERE primary_key_id = an int"
     *
     * <p>
     *
     * Convenience method used to update rows in the DB.  Checks if a
     * <i>single</i> int primary key is specified in the Criteria
     * object and uses it to perform the udpate.  If no primary key is
     * specified an Exception will be thrown.
     *
     * <p>
     *
     * To perform an update with non-primary key fields in the WHERE
     * clause use doUpdate(criteria, criteria).
     *
     * @param updateValues A Criteria object containing values used in
     * set clause.
     * @exception Exception, a generic exception.
     */
    public static void doUpdate(Criteria updateValues)
        throws Exception
    {
        // Transaction stuff added for postgres.
        boolean doTransaction = (TurbineDB.getDB(updateValues.getDbName())
            .objectDataNeedsTrans() &&
            updateValues.containsObjectColumn(updateValues.getDbName()));
        DBConnection db = null;
        try
        {
            // Get a connection to the db.
            if (doTransaction)
            {
                db = beginTransaction(updateValues.getDbName());
            }
            else
            {
                db = TurbineDB.getConnection( updateValues.getDbName() );
            }

            doUpdate(updateValues, db);
        }
        finally
        {
            if (doTransaction)
            {
                commitTransaction(db);
            }
            else
            {
                TurbineDB.releaseConnection(db);
            }
        }
    }

    /**
     * Use this method for performing an update of the kind:
     *
     * <p>
     *
     * "WHERE primary_key_id = an int"
     *
     * <p>
     *
     * Convenience method used to update rows in the DB.  Checks if a
     * <i>single</i> int primary key is specified in the Criteria
     * object and uses it to perform the udpate.  If no primary key is
     * specified an Exception will be thrown.
     *
     * <p>
     *
     * To perform an update with non-primary key fields in the WHERE
     * clause use doUpdate(criteria, criteria).
     *
     * @param updateValues A Criteria object containing values used in
     * set clause.
     * @param dbCon A DBConnection.
     * @exception Exception, a generic exception.
     */
    public static void doUpdate(Criteria updateValues,
                                DBConnection dbCon)
        throws Exception
    {
        ColumnMap pk = getPrimaryKey(updateValues);
        Criteria selectCriteria = null;

        if ( pk != null &&
             updateValues.containsKey(pk.getFullyQualifiedName()) )
        {
            selectCriteria = new Criteria(2);
            selectCriteria.put( pk.getFullyQualifiedName(),
                                updateValues.remove(pk.getFullyQualifiedName()) );
        }
        else
        {
            throw new Exception("BasePeer.doUpdate(criteria) - no PK specified");
        }

        doUpdate( selectCriteria, updateValues, dbCon );
    }

    /**
     * Use this method for performing an update of the kind:
     *
     * <p>
     *
     * WHERE some_column = some value AND could_have_another_column =
     * another value AND so on...
     *
     * <p>
     *
     * Method used to update rows in the DB.  Rows are selected based
     * on selectCriteria and updated using values in updateValues.
     *
     * @param selectCriteria A Criteria object containing values used
     * in where clause.
     * @param updateValues A Criteria object containing values used in
     * set clause.
     * @exception Exception, a generic exception.
     */
    public static void doUpdate(Criteria selectCriteria,
                                Criteria updateValues)
        throws Exception
    {
        // Transaction stuff added for postgres.
        boolean doTransaction = (TurbineDB.getDB(updateValues.getDbName())
            .objectDataNeedsTrans() &&
            updateValues.containsObjectColumn(selectCriteria.getDbName()));
        DBConnection db = null;
        try
        {
            // Get a connection to the db.
            if (doTransaction)
                db = beginTransaction(selectCriteria.getDbName());
            else
                db = TurbineDB.getConnection( selectCriteria.getDbName() );

            doUpdate(selectCriteria, updateValues, db);
        }
        finally
        {
            if (doTransaction)
            {
                commitTransaction(db);
            }
            else
            {
                TurbineDB.releaseConnection(db);
            }
        }
    }


    /**
     * Use this method for performing an update of the kind:
     *
     * <p>
     *
     * WHERE some_column = some value AND could_have_another_column =
     * another value AND so on.
     *
     * <p>
     *
     * Method used to update rows in the DB.  Rows are selected based
     * on selectCriteria and updated using values in updateValues.
     *
     * @param selectCriteria A Criteria object containing values used
     * in where clause.
     * @param updateValues A Criteria object containing values used in
     * set clause.
     * @param dbCon A DBConnection.
     * @exception Exception, a generic exception.
     */
    public static void doUpdate(Criteria selectCriteria,
                                Criteria updateValues,
                                DBConnection dbCon)
        throws Exception
    {
        DB db = TurbineDB.getDB( selectCriteria.getDbName() );
        DatabaseMap dbMap =
            TurbineDB.getDatabaseMap( selectCriteria.getDbName() );
        Connection connection = dbCon.getConnection();

        // Set up a list of required tables.  StringStackBuffer.add()
        // only adds element if it is unique.
        StringStackBuffer tables = new StringStackBuffer();
        Enumeration e = selectCriteria.keys();
        while(e.hasMoreElements())
        {
            tables.add(selectCriteria.getTableName( (String)e.nextElement() ));
        }

        for (int i=0; i<tables.size(); i++)
        {
            KeyDef kd = new KeyDef();
            StringStackBuffer whereClause = new StringStackBuffer();
            DatabaseMap tempDbMap = dbMap;

            ColumnMap[] columnMaps =
                tempDbMap.getTable( tables.get(i) ).getColumns();
            for (int j=0; j<columnMaps.length; j++)
            {
                ColumnMap colMap = columnMaps[j];
                if ( colMap.isPrimaryKey() )
                {
                    kd.addAttrib( colMap.getColumnName() );
                }
                String key = new StringBuffer(colMap.getTableName())
                    .append('.').append(colMap.getColumnName()).toString();
                if ( selectCriteria.containsKey(key) )
                {
                    if ( selectCriteria.getComparison( key ).equals(Criteria.CUSTOM) )
                    {
                        whereClause.add( selectCriteria.getString( key ));
                    }
                    else
                    {
                        whereClause.add( SqlExpression.build( colMap.getColumnName(),
                            selectCriteria.getValue( key ),
                            selectCriteria.getComparison( key ),
                            selectCriteria.isIgnoreCase(),
                            db ));
                    }
                }
            }
            TableDataSet tds = null;
            try
            {
                // Get affected records.
                tds = new TableDataSet(connection, tables.get(i), kd );
                String sqlSnippet = whereClause.toString(" AND ");
                TurbineLogging.getLogger(TurbineConstants.SQL_LOG_FACILITY)
                    .debug("BasePeer.doUpdate: whereClause=" + sqlSnippet);
                tds.where(sqlSnippet);
                tds.fetchRecords();

                if ( tds.size() > 1 && selectCriteria.isSingleRecord() )
                {
                    handleMultipleRecords(tds);
                }
                for (int j=0; j<tds.size(); j++)
                {
                    Record rec = tds.getRecord(j);
                    insertOrUpdateRecord(rec, tables.get(i), updateValues);
                }
            }
            finally
            {
                if (tds != null) tds.close();
            }
        }
    }

    /**
     * Utility method which executes a given sql statement.  This
     * method should be used for update, insert, and delete
     * statements.  Use executeQuery() for selects.
     *
     * @param stmt A String with the sql statement to execute.
     * @return The number of rows affected.
     * @exception Exception, a generic exception.
     */
    public static int executeStatement(String stmt)
        throws Exception
    {
        return executeStatement(stmt, TurbineDB.getDefaultDB());
    }

    /**
     * Utility method which executes a given sql statement.  This
     * method should be used for update, insert, and delete
     * statements.  Use executeQuery() for selects.
     *
     * @param stmt A String with the sql statement to execute.
     * @param dbName Name of database to connect to.
     * @return The number of rows affected.
     * @exception Exception, a generic exception.  */
    public static int executeStatement(String stmt,
                                       String dbName)
        throws Exception
    {
        DBConnection db = null;
        int rowCount = -1;
        try
        {
            // Get a connection to the db.
            db = TurbineDB.getConnection( dbName );

            rowCount = executeStatement( stmt, db );
        }
        finally
        {
            TurbineDB.releaseConnection(db);
        }
        return rowCount;
    }


    /**
     * Utility method which executes a given sql statement.  This
     * method should be used for update, insert, and delete
     * statements.  Use executeQuery() for selects.
     *
     * @param stmt A String with the sql statement to execute.
     * @param dbCon A DBConnection.
     * @return The number of rows affected.
     * @exception Exception, a generic exception.
     */
    public static int executeStatement(String stmt,
                                       DBConnection dbCon)
        throws Exception
    {
        Connection con = dbCon.getConnection();
        Statement statement = null;
        int rowCount = -1;

        try
        {
            statement = con.createStatement();
            rowCount = statement.executeUpdate( stmt );
        }
        finally
        {
            if (statement != null) statement.close();
        }
        return rowCount;
    }


    /**
     * If the user specified that (s)he only wants to retrieve a
     * single record and multiple records are retrieved, this method
     * is called to handle the situation.  The default behavior is to
     * throw an exception, but subclasses can override this method as
     * needed.
     *
     * @param ds The DataSet which contains multiple records.
     * @exception Exception Couldn't handle multiple records.
     */
    protected static void handleMultipleRecords(DataSet ds)
        throws Exception
    {
        throw new Exception("Criteria expected single Record and Multiple Records were selected.");
    }

    /**
     * @deprecated Use the better-named handleMultipleRecords() instead.
     */
    protected static void handleMultiple(DataSet ds)
        throws Exception
    {
        handleMultipleRecords(ds);
    }

    /**
     * This method returns the MapBuilder specified in the
     * TurbineResources.properties file. By default, this is
     * org.apache.turbine.util.db.map.TurbineMapBuilder.
     *
     * @return A MapBuilder.
     */
    public static MapBuilder getMapBuilder()
    {
        return getMapBuilder(TurbineResources.getString("database.maps.builder",
                DEFAULT_MAP_BUILDER).trim());
    }

    /**
     * This method returns the MapBuilder specified in the name
     * parameter.  You should pass in the full path to the class, ie:
     * org.apache.turbine.util.db.map.TurbineMapBuilder.  The
     * MapBuilder instances are cached in this class for speed.
     *
     * @return A MapBuilder, or null (and logs the error) if the
     * MapBuilder was not found.
     */
    public static MapBuilder getMapBuilder(String name)
    {
        try
        {
            MapBuilder mb = (MapBuilder)mapBuilders.get(name);
            // Use the 'double-check pattern' for syncing
            //  caching of the MapBuilder.
            if (mb == null)
            {
                synchronized(mapBuilders)
                {
                   mb = (MapBuilder)mapBuilders.get(name);
                   if (mb == null)
                  {
                      mb = (MapBuilder)Class.forName(name).newInstance();
                      // Cache the MapBuilder before it is built.
                      mapBuilders.put(name, mb);
                  }
               }
            }

            // Build the MapBuilder in its own synchronized block to
            //  avoid locking up the whole Hashtable while doing so.
            // Note that *all* threads need to do a sync check on isBuilt()
            //  to avoid grabing an uninitialized MapBuilder. This, however,
            //  is a relatively fast operation.
            synchronized(mb)
            {
               if (!mb.isBuilt())
               {
                  try
                  {
                     mb.doBuild();
                  }
                  catch ( Exception e )
                  {
                      // need to think about whether we'd want to remove
                      //  the MapBuilder from the cache if it can't be
                      //  built correctly...?  pgo
                      throw e;
                  }
               }
            }
            return mb;
        }
        catch(Exception e)
        {
            // Have to catch possible exceptions because method is
            // used in initialization of Peers.  Log the exception and
            // return null.
            Log.error("BasePeer.MapBuilder failed trying to instantiate: " +
                      name, e);
        }
        return null;
    }


    /**
     * Performs a SQL <code>select</code> using a PreparedStatement.
     *
     * @exception Exception Error performing database query.
     */
    public static Vector doPSSelect(Criteria criteria, DBConnection dbCon)
        throws Exception
    {
        Vector v = null;

        StringBuffer qry = new StringBuffer();
        Vector params = new Vector(criteria.size());

        createPreparedStatement (criteria, qry, params);

        PreparedStatement stmt = null;
        try
        {
            stmt = dbCon.getConnection().prepareStatement(qry.toString());

            for (int i = 0; i < params.size(); i++)
            {
                Object param = params.get(i);
                if (param instanceof java.sql.Date)
                {
                    stmt.setDate(i + 1, (java.sql.Date)param);
                }
                else
                {
                    stmt.setString(i + 1, param.toString());
                }
            }

            QueryDataSet qds = null;
            try
            {
                qds = new QueryDataSet( stmt.executeQuery() );
                v = getSelectResults(qds);
            }
            finally
            {
                if (qds != null)
                {
                    qds.close();
                }
            }
        }
        finally
        {
            if (stmt != null)
            {
                stmt.close();
            }
        }

        return v;
    }


    /**
     * Do a Prepared Statement select according to the given criteria
     */
    public static Vector doPSSelect(Criteria criteria) throws Exception
    {
        DBConnection dbCon = TurbineDB.getConnection( criteria.getDbName() );
        Vector v = null;

        try
        {
            v = doPSSelect (criteria,dbCon);
        }
        finally
        {
            TurbineDB.releaseConnection( dbCon );
        }

        return v;
    }


    /**
     * Create a new PreparedStatement.  It builds a string representation
     * of a query and a list of PreparedStatement parameters.
     */
    public static void createPreparedStatement(Criteria criteria,
                                               StringBuffer queryString,
                                               List params)
        throws Exception
    {
        DB db = TurbineDB.getDB( criteria.getDbName() );
        DatabaseMap dbMap = TurbineDB.getDatabaseMap( criteria.getDbName() );

        Query query = new Query();

        StringStackBuffer selectModifiers = query.getSelectModifiers();
        StringStackBuffer selectClause = query.getSelectClause();
        StringStackBuffer fromClause = query.getFromClause();
        StringStackBuffer whereClause = query.getWhereClause();
        StringStackBuffer orderByClause = query.getOrderByClause();

        StringStackBuffer orderBy = criteria.getOrderByColumns();
        boolean ignoreCase = criteria.isIgnoreCase();
        StringStackBuffer select = criteria.getSelectColumns();
        Hashtable aliases = criteria.getAsColumns();
        StringStackBuffer modifiers = criteria.getSelectModifiers();

        for (int i=0; i<modifiers.size(); i++)
        {
            selectModifiers.add( modifiers.get(i) );
        }

        for (int i=0; i<modifiers.size(); i++)
        {
            selectModifiers.add( modifiers.get(i) );
        }

        for (int i=0; i<select.size(); i++)
        {
            String columnName = select.get(i);
            String tableName = null;
            selectClause.add(columnName);
            int parenPos = columnName.indexOf('(');
            if (parenPos == -1)
            {
                tableName = columnName.substring(0,
                                                 columnName.indexOf('.') );
            }
            else
            {
                tableName = columnName.substring(parenPos + 1,
                                                 columnName.indexOf('.') );
            }
            String tableName2 = criteria.getTableForAlias(tableName);
            if ( tableName2 != null )
            {
                fromClause.add(
                    new StringBuffer(tableName.length() +
                                     tableName2.length() + 1)
                    .append(tableName2).append(' ').append(tableName)
                    .toString() );
            }
            else
            {
                fromClause.add(tableName);
            }
        }


        Iterator it = aliases.keySet().iterator();
        while(it.hasNext())
        {
          String key = (String)it.next();
          selectClause.add((String)aliases.get(key) + " AS " + key);
        }

        Enumeration e = criteria.keys();
        while (e.hasMoreElements())
        {
            String key = (String)e.nextElement();
            Criteria.Criterion criterion =
                (Criteria.Criterion)criteria.getCriterion(key);
            Criteria.Criterion[] someCriteria =
                criterion.getAttachedCriterion();

            String table = null;
            for (int i=0; i<someCriteria.length; i++)
            {
                String tableName = someCriteria[i].getTable();
                table = criteria.getTableForAlias(tableName);
                if ( table != null )
                {
                    fromClause.add(
                        new StringBuffer(tableName.length() +
                                         table.length() + 1)
                        .append(table).append(' ').append(tableName)
                        .toString() );
                }
                else
                {
                    fromClause.add(tableName);
                    table = tableName;
                }

                boolean ignorCase = (criteria.isIgnoreCase() &&
                    (dbMap.getTable(table).getColumn(
                    someCriteria[i].getColumn()).getType() instanceof String));

                someCriteria[i].setIgnoreCase(ignorCase);
            }

            criterion.setDB(db);
            StringBuffer sb = new StringBuffer();
            criterion.appendPsTo (sb,params);
            whereClause.add( sb.toString() );

        }

        List join = criteria.getJoinL();
        if ( join != null)
        {
            for ( int i=0; i<join.size(); i++ )
            {
                String join1 = (String)join.get(i);
                String join2 = (String)criteria.getJoinR().get(i);

                String tableName = join1.substring(0, join1.indexOf('.'));
                String table = criteria.getTableForAlias(tableName);
                if ( table != null )
                {
                    fromClause.add(
                        new StringBuffer(tableName.length() +
                                         table.length() + 1)
                        .append(table).append(' ').append(tableName)
                        .toString() );
                }
                else
                {
                    fromClause.add(tableName);
                }

                int dot =  join2.indexOf('.');
                tableName = join2.substring(0, dot);
                table = criteria.getTableForAlias(tableName);
                if ( table != null )
                {
                    fromClause.add(
                        new StringBuffer(tableName.length() +
                                         table.length() + 1)
                        .append(table).append(' ').append(tableName)
                        .toString() );
                }
                else
                {
                    fromClause.add(tableName);
                    table = tableName;
                }

                boolean ignorCase = (criteria.isIgnoreCase() &&
                    (dbMap.getTable(table).getColumn(
                        join2.substring(dot+1, join2.length()) )
                    .getType() instanceof String));

                whereClause.add(
                    SqlExpression.buildInnerJoin(join1, join2,
                                                 ignorCase, db) );
            }
        }

        if ( orderBy != null && orderBy.size() > 0)
        {
            // Check for each String/Character column and apply
            // toUpperCase().
            for (int i=0; i<orderBy.size(); i++)
            {
                String orderByColumn = orderBy.get(i);
                String table = orderByColumn.substring(0,orderByColumn.indexOf('.') );
                // See if there's a space (between the column list and sort
                // order in ORDER BY table.column DESC).
                int spacePos = orderByColumn.indexOf(' ');
                String columnName;
                if (spacePos == -1)
                    columnName = orderByColumn.substring(orderByColumn.indexOf('.') + 1);
                else
                    columnName = orderByColumn.substring(orderByColumn.indexOf('.') + 1, spacePos);
                ColumnMap column = dbMap.getTable(table).getColumn( columnName );
                if ( column.getType() instanceof String )
                {
                    if (spacePos == -1)
                        orderByClause.add( db.ignoreCaseInOrderBy(orderByColumn) );
                    else
                        orderByClause.add( db.ignoreCaseInOrderBy(orderByColumn.substring(0, spacePos)) + orderByColumn.substring(spacePos) );
                }
                else
                {
                    orderByClause.add(orderByColumn);
                }
            }
        }

        setLimit(criteria, db, query);

        String sql = query.toString();
        TurbineLogging.getLogger(TurbineConstants.SQL_LOG_FACILITY).debug(sql);
        queryString.append (sql);
    }

    private static void setLimit(Criteria criteria, DB db, Query query)
    {
        // Limit the number of rows returned.
        int limit = criteria.getLimit();
        int offset = criteria.getOffset();
        String limitString = null;
        String topString = null;
        String rcString = null;
        if ( offset > 0 && db.supportsNativeOffset() )
        {
            switch(db.getLimitStyle())
            {
                case DB.LIMIT_STYLE_MYSQL:
                    limitString = new StringBuffer().append(offset)
                                                    .append(", ")
                                                    .append(limit)
                                                    .toString();

                    criteria.setLimit(-1);
                    break;
                case DB.LIMIT_STYLE_POSTGRES:
                    limitString = new StringBuffer().append(limit)
                                                    .append(", ")
                                                    .append(offset)
                                                    .toString();

                    criteria.setLimit(-1);
                    break;
                default: //for DBs which support limit but not offset
                    limit +=offset; //this needs to be implemented in the retrieval to skip records
                    throw new UnsupportedOperationException("non-native offset is not implemented");
            }
            criteria.setOffset(0);
        }
        if (limit > 0 && db.supportsNativeLimit() )
        {
            switch(db.getLimitStyle())
            {
                case DB.LIMIT_STYLE_MYSQL:
                case DB.LIMIT_STYLE_POSTGRES:
                    limitString = String.valueOf(limit);
                    break;
                case DB.LIMIT_STYLE_MSSQL7:
                    topString = String.valueOf(limit);
                    break;
                case DB.LIMIT_STYLE_SYBASE:
                    rcString = String.valueOf(limit);
                    break;
                default:
                    throw new UnsupportedOperationException("non-native limit is not implemented");
            }

            // Now set the criteria's limit to return the full
            // resultset since the results are limited on the server.
            criteria.setLimit(-1);
        }

        query.setLimit(limitString);
        query.setRowcount(rcString);
        query.setTop(topString);
    }

}
