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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.turbine.om.DateKey;
import org.apache.turbine.om.ObjectKey;
import org.apache.turbine.om.peer.BasePeer;
import org.apache.turbine.services.db.TurbineDB;
import org.apache.turbine.util.StringStackBuffer;
import org.apache.turbine.util.db.adapter.DB;
import org.apache.turbine.util.db.map.DatabaseMap;
import org.apache.turbine.util.db.map.TableMap;

/**
 * This is a utility class that is used for retrieving different types
 * of values from a hashtable based on a simple name string.  This
 * class is meant to minimize the amount of casting that needs to be
 * done when working with Hashtables.
 *
 * NOTE: other methods will be added as needed and as time permits.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:eric@dobbse.net">Eric Dobbs</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class Criteria extends Hashtable
{
    /**
     * Comparison type.
     */
    public static final String EQUAL = "=";

    /**
     * Comparison type.
     */
    public static final String NOT_EQUAL = "<>";

    /**
     * Comparison type.
     */
    public static final String ALT_NOT_EQUAL = "!=";

    /**
     * Comparison type.
     */
    public static final String GREATER_THAN = ">";

    /**
     * Comparison type.
     */
    public static final String LESS_THAN = "<";

    /**
     * Comparison type.
     */
    public static final String GREATER_EQUAL = ">=";

    /**
     * Comparison type.
     */
    public static final String LESS_EQUAL = "<=";

    /**
     * Comparison type.
     */
    public static final String LIKE = " LIKE ";

    // public static final String STRING_EQUAL = "='";

    /**
     * Comparison type.
     */
    public static final String CUSTOM = "CUSTOM";

    // public static final String FOREIGN_KEY = "fk";

    /**
     * Comparison type.
     */
    public static final String DISTINCT = "DISTINCT ";

    /**
     * Comparison type.
     */
    public static final String IN = " IN ";

    /**
     * Comparison type.
     */
    public static final String NOT_IN = " NOT IN ";

    /**
     * Comparison type.
     */
    public static final String ALL = "ALL ";

    /**
     * Comparison type.
     */
    public static final String JOIN = "JOIN";

    /**
     * "Order by" qualifier - ascending
     */
    private static final String ASC = "ASC";

    /**
     * "Order by" qualifier - descending
     */
    private static final String DESC = "DESC";

    /**
     * "IS NULL" null comparison
     */
    public static final String ISNULL = " IS NULL ";
  
    /**
     * "IS NOT NULL" null comparison
     */
    public static final String ISNOTNULL = " IS  NOT NULL ";

    private static final int DEFAULT_CAPACITY = 10;

    private boolean ignoreCase = false;
    private boolean singleRecord = false;
    private boolean cascade = false;
    private StringStackBuffer selectModifiers = new StringStackBuffer();
    private StringStackBuffer selectColumns = new StringStackBuffer();
    private StringStackBuffer orderByColumns = new StringStackBuffer();
    private Hashtable asColumns = new Hashtable(8);
    private ArrayList joinL = null;
    private ArrayList joinR = null;

    /** The name of the database. */
    private String dbName;

    /**
     * To limit the number of rows to return.  <code>-1</code> means return all
     * rows.
     */
    private int limit = -1;

    /**
     * To start the results at a row other than the first one.
     */
    private int offset = 0;

    private HashMap aliases = null;

    // flag to note that the criteria involves a blob.
    private boolean blobFlag = false;


    /**
     * Creates a new instance with the default capacity.
     */
    public Criteria()
    {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Creates a new instance with the specified capacity.
     *
     * @param initialCapacity An int.
     */
    public Criteria(int initialCapacity)
    {
        this(TurbineDB.getDefaultDB(), initialCapacity);
    }

    /**
     * Creates a new instance with the default capacity which corresponds to
     * the specified database.
     *
     * @param dbName The dabase name.
     */
    public Criteria(String dbName)
    {
        this(dbName, DEFAULT_CAPACITY);
    }

    /**
     * Creates a new instance with the specified capacity which corresponds to
     * the specified database.
     *
     * @param dbName          The dabase name.
     * @param initialCapacity The initial capacity.
     */
    public Criteria(String dbName,
                    int initialCapacity)
    {
        super(initialCapacity);
        this.dbName = dbName;
    }

    /**
     * Add an AS clause to the select columns. Usage:
     * <p>
     * <code>
     *
     * Criteria myCrit = new Criteria();
     * myCrit.addAsColumn("alias", "ALIAS("+MyPeer.ID+")");
     *
     * </code>
     *
     * @param name  wanted Name of the column
     * @param clause SQL clause to select from the table
     *
     * If the name already exists, it is replaced by the new clause.
     *
     * @return A modified Criteria object.
     */
    public Criteria addAsColumn(String name,
                                String clause)
    {
        asColumns.put(name, clause);
        return this;
    }

    /**
     * Get the column aliases.
     *
     * @return A Hashtable which map the column alias names
     * to the alias clauses.
     */
    public Hashtable getAsColumns()
    {
        return asColumns;
    }

    /**
     * Allows one to specify an alias for a table that can
     * be used in various parts of the SQL.
     *
     * @param alias a <code>String</code> value
     * @param table a <code>String</code> value
     */
    public void addAlias(String alias, String table)
    {
        if ( aliases == null )
        {
            aliases = new HashMap(8);
        }
        aliases.put(alias, table);
    }

    /**
     * Returns the table name associated with an alias.
     *
     * @param alias a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String getTableForAlias(String alias)
    {
        if ( aliases == null )
        {
            return null;
        }
        return (String)aliases.get(alias);
    }

    /**
     * Does this Criteria Object contain the specified key?
     *
     * @param table The name of the table.
     * @param column The name of the column.
     * @return True if this Criteria Object contain the specified key.
     */
    public boolean containsKey( String table,
                                String column )
    {
        return containsKey(table + '.' + column);
    }

    /**
     * Convenience method to return value as a boolean.
     *
     * @param column String name of column.
     * @return A boolean.
     */
    public boolean getBoolean(String column)
    {
        return ((Boolean)getCriterion(column).getValue()).booleanValue();
    }

    /**
     * Convenience method to return value as a boolean.
     *
     * @param table String name of table.
     * @param column String name of column.
     * @return A boolean.
     */
    public boolean getBoolean(String table,
                              String column)
    {
        return getBoolean(
            new StringBuffer(table.length() + column.length() + 1)
            .append(table).append('.').append(column)
            .toString() );
    }

    /**
     * Returns true if any of the tables in the criteria contain an
     * Object column.
     *
     * @return A boolean.
     * @exception Exception, a generic exception.
     */
    public boolean containsObjectColumn() throws Exception
    {
        return containsObjectColumn(dbName);
    }

    /**
     * Returns true if any of the tables in the criteria contain an
     * Object column.
     *
     * @param databaseMapName A String.
     * @return A boolean.
     * @exception Exception, a generic exception.
     */
    public boolean containsObjectColumn(String databaseMapName)
        throws Exception
    {
        // Peer or application may have noted the existence of a blob
        // so we can save the lookup.
        if ( blobFlag )
        {
            return true;
        }

        DatabaseMap map = TurbineDB.getDatabaseMap(databaseMapName);
        StringStackBuffer tables = new StringStackBuffer();
        for (Enumeration e = super.elements(); e.hasMoreElements(); )
        {
            Criterion co = (Criterion)e.nextElement();
            String tableName = co.getTable();
            if (!tables.contains(tableName))
            {
                if (map.getTable(tableName).containsObjectColumn())
                  return true;
                tables.add(tableName);
            }
        }
        return false;
    }

    /**
     * Method to return criteria related to columns in a table.
     *
     * @param column String name of column.
     * @return A Criterion.
     */
    public Criterion getCriterion(String column)
    {
        return (Criterion)super.get(column);
    }

    /**
     * Method to return criteria related to a column in a table.
     *
     * @param table String name of table.
     * @param column String name of column.
     * @return A Criterion.
     */
    public Criterion getCriterion(String table, String column)
    {
        return getCriterion(
            new StringBuffer(table.length() + column.length() + 1)
            .append(table).append('.').append(column)
            .toString() );
    }

    /**
     * Method to return criterion that is not added automatically
     * to this Criteria.  This can be used to chain the
     * Criterions to form a more complex where clause.
     *
     * @param column String full name of column (for example TABLE.COLUMN).
     * @return A Criterion.
     */
    public Criterion getNewCriterion(String column,
                                     Object value, String comparison)
    {
        return new Criterion(column,value,comparison);
    }

    /**
     * Method to return criterion that is not added automatically
     * to this Criteria.  This can be used to chain the
     * Criterions to form a more complex where clause.
     *
     * @param table String name of table.
     * @param column String name of column.
     * @return A Criterion.
     */
    public Criterion getNewCriterion(String table, String column,
                                     Object value, String comparison)
    {
        return new Criterion(table,column,value,comparison);
    }

    /**
     * This method adds a prepared Criterion object to the Criteria.
     * You can get a new, empty Criterion object with the
     * getNewCriterion() method. If a criterion for the requested column
     * already exists, it is replaced. This is used as follows:
     *
     * <p>
     * <code>
     * Criteria crit = new Criteria();
     * Criteria.Criterion c = crit
     * .getNewCriterion(BasePeer.ID, new Integer(5), Criteria.LESS_THAN);
     * crit.add(c);
     * </code>
     *
     * @param c A Criterion object
     *
     * @return A modified Criteria object.
     */
    public Criteria add(Criterion c)
    {
        StringBuffer sb = new StringBuffer(c.getTable().length() +
                                           c.getColumn().length() + 1);
        sb.append(c.getTable());
        sb.append('.');
        sb.append(c.getColumn());
        super.put(sb.toString(), c);
        return this;
    }

    /**
     * Method to return a String table name.
     *
     * @param name A String with the name of the key.
     * @return A String with the value of the object at key.
     */
    public String getColumnName(String name)
    {
        return getCriterion(name).getColumn();
    }

    /**
     * Method to return a comparison String.
     *
     * @param key String name of the key.
     * @return A String with the value of the object at key.
     */
    public String getComparison(String key)
    {
        return getCriterion(key).getComparison();
    }

    /**
     * Method to return a comparison String.
     *
     * @param table String name of table.
     * @param column String name of column.
     * @return A String with the value of the object at key.
     */
    public String getComparison(String table,
                                String column)
    {
        return getComparison(
            new StringBuffer(table.length() + column.length() + 1)
            .append(table).append('.').append(column)
            .toString() );
    }

    /**
     * Convenience method to return a Date.
     *
     * @param table String name.
     * @return A java.util.Date with the value of object at key.
     */
    public java.util.Date getDate(String name)
    {
        return (java.util.Date)getCriterion(name).getValue();
    }

    /**
     * Convenience method to return a Date.
     *
     * @param table String name of table.
     * @param column String name of column.
     * @return A java.util.Date with the value of object at key.
     */
    public java.util.Date getDate(String table,
                                  String column)
    {
        return getDate(
            new StringBuffer(table.length() + column.length() + 1)
            .append(table).append('.').append(column)
            .toString() );
    }

    /**
     * Get the Database(Map) name.
     *
     * @return A String with the Database(Map) name.  By default, this
     * is PoolBrokerService.DEFAULT.
     */
    public String getDbName()
    {
        return dbName;
    }

    /**
     * Set the DatabaseMap name.  If <code>null</code> is supplied, uses value
     * provided by <code>TurbineDB.getDefaultDB()</code>.
     *
     * @param map A String with the Database(Map) name.
     */
    public void setDbName(String dbName)
    {
        this.dbName = (dbName == null ? TurbineDB.getDefaultDB() : dbName);
    }

    /**
     * Convenience method to return a double.
     *
     * @param name A String with the name of the key.
     * @return A double with the value of object at key.
     */
    public double getDouble(String name)
    {
        Object obj = getCriterion(name).getValue();
        if (obj instanceof String)
            return new Double((String)obj).doubleValue();
        return ((Double) obj).doubleValue();
    }

    /**
     * Convenience method to return a double.
     *
     * @param table String name of table.
     * @param column String name of column.
     * @return A double with the value of object at key.
     */
    public double getDouble(String table,
                            String column)
    {
        return getDouble(
            new StringBuffer(table.length() + column.length() + 1)
            .append(table).append('.').append(column)
            .toString() );
    }

    /**
     * Convenience method to return a float.
     *
     * @param name A String with the name of the key.
     * @return A float with the value of object at key.
     */
    public float getFloat(String name)
    {
        Object obj = getCriterion(name).getValue();
        if (obj instanceof String)
            return new Float((String)obj).floatValue();
        return ((Float) obj).floatValue();
    }

    /**
     * Convenience method to return a float.
     *
     * @param table String name of table.
     * @param column String name of column.
     * @return A float with the value of object at key.
     */
    public float getFloat(String table,
                          String column)
    {
        return getFloat(
            new StringBuffer(table.length() + column.length() + 1)
            .append(table).append('.').append(column)
            .toString() );
    }

    /**
     * Convenience method to return an Integer.
     *
     * @param name A String with the name of the key.
     * @return An Integer with the value of object at key.
     */
    public Integer getInteger(String name)
    {
        Object obj = getCriterion(name).getValue();
        if (obj instanceof String)
            return new Integer((String)obj);
        return ((Integer)obj);
    }

    /**
     * Convenience method to return an Integer.
     *
     * @param table String name of table.
     * @param column String name of column.
     * @return An Integer with the value of object at key.
     */
    public Integer getInteger(String table,
                              String column)
    {
        return getInteger(
            new StringBuffer(table.length() + column.length() + 1)
            .append(table).append('.').append(column)
            .toString() );
    }

    /**
     * Convenience method to return an int.
     *
     * @param name A String with the name of the key.
     * @return An int with the value of object at key.
     */
    public int getInt(String name)
    {
        Object obj = getCriterion(name).getValue();
        if (obj instanceof String)
            return new Integer((String)obj).intValue();
        return ((Integer)obj).intValue();
    }

    /**
     * Convenience method to return an int.
     *
     * @param table String name of table.
     * @param column String name of column.
     * @return An int with the value of object at key.
     */
    public int getInt(String table,
                      String column)
    {
        return getInt(
            new StringBuffer(table.length() + column.length() + 1)
            .append(table).append('.').append(column)
            .toString() );
    }

    /**
     * Convenience method to return a BigDecimal.
     *
     * @param name A String with the name of the key.
     * @return A BigDecimal with the value of object at key.
     */
    public BigDecimal getBigDecimal(String name)
    {
        Object obj = getCriterion(name).getValue();
        if (obj instanceof String)
            return new BigDecimal((String)obj);
        return (BigDecimal)obj;
    }

    /**
     * Convenience method to return a BigDecimal.
     *
     * @param table String name of table.
     * @param column String name of column.
     * @return A BigDecimal with the value of object at key.
     */
    public BigDecimal getBigDecimal(String table,
                                    String column)
    {
        return getBigDecimal(
            new StringBuffer(table.length() + column.length() + 1)
            .append(table).append('.').append(column)
            .toString() );
    }

    /**
     * Convenience method to return a long.
     *
     * @param name A String with the name of the key.
     * @return A long with the value of object at key.
     */
    public long getLong(String name)
    {
        Object obj = getCriterion(name).getValue();
        if (obj instanceof String)
            return new Long((String)obj).longValue();
        return ((Long) obj).longValue();
    }

    /**
     * Convenience method to return a long.
     *
     * @param table String name of table.
     * @param column String name of column.
     * @return A long with the value of object at key.
     */
    public long getLong(String table,
                        String column)
    {
        return getLong(
            new StringBuffer(table.length() + column.length() + 1)
            .append(table).append('.').append(column)
            .toString() );
    }

    /**
     * Convenience method to return a String.
     *
     * @param name A String with the name of the key.
     * @return A String with the value of object at key.
     */
    public String getString(String name)
    {
        return (String) getCriterion(name).getValue();
    }

    /**
     * Convenience method to return a String.
     *
     * @param table String name of table.
     * @param column String name of column.
     * @return A String with the value of object at key.
     */
    public String getString(String table,
                            String column)
    {
        return getString(
            new StringBuffer(table.length() + column.length() + 1)
            .append(table).append('.').append(column)
            .toString() );
    }

    /**
     * Method to return a String table name.
     *
     * @param name A String with the name of the key.
     * @return A String with the value of object at key.
     */
     public String getTableName(String name)
    {
        return getCriterion(name).getTable();
    }

    /**
     * Convenience method to return a Vector.
     *
     * @param name A String with the name of the key.
     * @return A Vector with the value of object at key.
     */
    public Vector getVector(String name)
    {
        return (Vector) getCriterion(name).getValue();
    }

    /**
     * Convenience method to return a String.
     *
     * @param table String name of table.
     * @param column String name of column.
     * @return A String with the value of object at key.
     */
    public Vector getVector(String table,
                            String column)
    {
        return getVector(
            new StringBuffer(table.length() + column.length() + 1)
            .append(table).append('.').append(column)
            .toString() );
    }

    /**
     * Method to return the value that was added to Criteria.
     *
     * @param name A String with the name of the key.
     * @return An Object with the value of object at key.
     */
    public Object getValue( String name )
    {
        return getCriterion(name).getValue();
    }

    /**
     * Method to return the value that was added to Criteria.
     *
     * @param table String name of table.
     * @param column String name of column.
     * @return An Object with the value of object at key.
     */
    public Object getValue( String table,
                            String column )
    {
        return getValue(
            new StringBuffer(table.length() + column.length() + 1)
            .append(table).append('.').append(column)
            .toString() );
    }

    /**
     * Convenience method to return an ObjectKey.
     *
     * @param name A String with the name of the key.
     * @return An ObjectKey with the value of object at key.
     */
    public ObjectKey getObjectKey(String name)
    {
        return (ObjectKey)getCriterion(name).getValue();
    }

    /**
     * Convenience method to return an ObjectKey.
     *
     * @param table String name of table.
     * @param column String name of column.
     * @return A String with the value of object at key.
     */
    public ObjectKey getObjectKey(String table,
                               String column)
    {
        return getObjectKey(
            new StringBuffer(table.length() + column.length() + 1)
            .append(table).append('.').append(column)
            .toString() );
    }

    /**
     * Overrides Hashtable get, so that the value placed in the
     * Criterion is returned instead of the Criterion.
     *
     * @param key An Object.
     * @return An Object.
     */
    public Object get( Object key )
    {
        return getValue( (String)key );
    }

    /**
     * Overrides Hashtable put, so that this object is returned
     * instead of the value previously in the Criteria object.
     * The reason is so that it more closely matches the behavior
     * of the add() methods. If you want to get the previous value
     * then you should first Criteria.get() it yourself. Note, if
     * you attempt to pass in an Object that is not a String, it will
     * throw a NPE. The reason for this is that none of the add()
     * methods support adding anything other than a String as a key.
     *
     * @param key An Object. Must be instanceof String!
     * @param value An Object.
     * @throws NullPointerException if key != String or key/value is null.
     * @return Instance of self.
     */
    public Object put( Object key, Object value )
    {
        if (! (key instanceof String))
            throw new NullPointerException(
                            "Criteria: Key must be a String object.");
        return add((String)key,value);
    }

    /**
     * Copies all of the mappings from the specified Map to this Criteria
     * These mappings will replace any mappings that this Criteria had for any
     * of the keys currently in the specified Map.
     *
     * if the map was another Criteria, its attributes are copied to this
     * Criteria, overwriting previous settings.
     *
     * @param t Mappings to be stored in this map.
     */
    public synchronized void putAll(Map t)
    {
        Iterator i = t.entrySet().iterator();
        while (i.hasNext())
        {
            Map.Entry e = (Map.Entry) i.next();
            Object val = e.getValue();
            if ( val instanceof Criteria.Criterion )
            {
                super.put(e.getKey(), val);
            }
            else
            {
                put(e.getKey(), val);
            }
        }
        if ( t instanceof Criteria )
        {
            Criteria c = (Criteria)t;
            this.joinL = c.joinL;
            this.joinR = c.joinR;
        }
        /* this would make a copy, not included
           but might want to use some of it.
        if ( t instanceof Criteria )
        {
            Criteria c = (Criteria)t;
            this.ignoreCase = c.ignoreCase;
            this.singleRecord = c.singleRecord;
            this.cascade = c.cascade;
            this.selectModifiers = c.selectModifiers;
            this.selectColumns = c.selectColumns;
            this.orderByColumns = c.orderByColumns;
            this.dbName = c.dbName;
            this.limit = c.limit;
            this.offset = c.offset;
            this.aliases = c.aliases;
        }
        */
    }

    /**
     * This method adds a new criterion to the list of criterias. If a
     * criterion for the requested column already exists, it is
     * replaced. This is used as follows:
     *
     * <p>
     * <code>
     * Criteria crit = new Criteria().add(&quot;column&quot;,
     *                                      &quot;value&quot; );
     * </code>
     *
     * An EQUAL comparison is used for column and value.
     *
     * The name of the table must be used implicitly in the column name,
     * so the Column name must be something like 'TABLE.id'. If you
     * don't like this, you can use the add(table, column, value) method.
     *
     * @param column The column to run the comparison on
     * @param value An Object.
     *
     * @return A modified Criteria object.
     */
    public Criteria add ( String key,
                          Object value )
    {
        add(key, value, EQUAL);
        return this;
    }

    /**
     * This method adds a new criterion to the list of criterias.
     * If a criterion for the requested column already exists, it is
     * replaced. If is used as follow:
     *
     * <p>
     * <code>
     * Criteria crit = new Criteria().add(&quot;column&quot;,
     *                                      &quot;value&quot;
     *                                      &quot;Criterion.GREATER_THAN&quot;);
     * </code>
     *
     * Any comparison can be used.
     *
     * The name of the table must be used implicitly in the column name,
     * so the Column name must be something like 'TABLE.id'. If you
     * don't like this, you can use the add(table, column, value) method.
     *
     * @param column The column to run the comparison on
     * @param value An Object.
     * @param comparison A String.
     *
     * @return A modified Criteria object.
     */
    public Criteria add ( String column,
                          Object value,
                          String comparison )
    {
        super.put( column, new Criterion(column, value, comparison) );
        return this;
    }

    /**
     * This method adds a new criterion to the list of criterias.
     * If a criterion for the requested column already exists, it is
     * replaced. If is used as follows:
     *
     * <p>
     * <code>
     * Criteria crit = new Criteria().add(&quot;table&quot;,
     *                                      &quot;column&quot;,
     *                                      &quot;value&quot; );
     * </code>
     *
     * An EQUAL comparison is used for column and value.
     *
     * @param table Name of the table which contains the column
     * @param column The column to run the comparison on
     * @param value An Object.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria add ( String table,
                          String column,
                          Object value )
    {
        add(table, column, value, EQUAL);
        return this;
    }

    /**
     * This method adds a new criterion to the list of criterias.
     * If a criterion for the requested column already exists, it is
     * replaced. If is used as follows:
     *
     * <p>
     * <code>
     * Criteria crit = new Criteria().add(&quot;table&quot;,
     *                                      &quot;column&quot;,
     *                                      &quot;value&quot;,
     *                                      &quot;Criterion.GREATER_THAN&quot;);
     * </code>
     *
     * Any comparison can be used.
     *
     * @param table Name of table which contains the column
     * @param column The column to run the comparison on
     * @param value An Object.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria add( String table,
                          String column,
                          Object value,
                          String comparison )
    {
        StringBuffer sb = new StringBuffer(table.length()+column.length()+1);
        sb.append(table);
        sb.append('.');
        sb.append(column);
        super.put(sb.toString(),new Criterion(table, column, value, comparison));
        return this;
    }

    /**
     * Convenience method to add a boolean to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * add(column, new Boolean(value), EQUAL);
     * </code>
     *
     * @param column The column to run the comparison on
     * @param value A Boolean.
     *
     * @return A modified Criteria object.
     */
    public Criteria add( String column,
                          boolean value )
    {
        add(column, new Boolean(value) );
        return this;
    }

    /**
     * Convenience method to add a boolean to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * add(column, new Boolean(value), comparison);
     * </code>
     *
     * @param column The column to run the comparison on
     * @param value A Boolean.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria add( String column,
                          boolean value,
                          String comparison )
    {
        add(column, new Boolean(value), comparison );
        return this;
    }

    /**
     * Convenience method to add an int to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * add(column, new Integer(value), EQUAL);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value An int.
     *
     * @return A modified Criteria object.
     */
    public Criteria add( String column,
                          int value )
    {
        add(column, new Integer(value) );
        return this;
    }

    /**
     * Convenience method to add an int to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * add(column, new Integer(value), comparison);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value An int.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria add( String column,
                          int value,
                          String comparison )
    {
        add(column, new Integer(value), comparison );
        return this;
    }

    /**
     * Convenience method to add a long to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * add(column, new Long(value), EQUAL);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value A long.
     *
     * @return A modified Criteria object.
     */
    public Criteria add( String column,
                        long value)
    {
        add(column, new Long(value));
        return this;
    }

    /**
     * Convenience method to add a long to Criteria
     * Equal to
     *
     * <p>
     * <code>
     * add(column, new Long(value), comparison);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value A long.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria add( String column,
                        long value,
                        String comparison)
    {
        add(column, new Long(value), comparison);
        return this;
    }

    /**
     * Convenience method to add a float to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * add(column, new Float(value), EQUAL);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value A float.
     *
     * @return A modified Criteria object.
     */
    public Criteria add( String column,
                        float value)
    {
        add(column, new Float(value));
        return this;
    }

    /**
     * Convenience method to add a float to Criteria
     * Equal to
     *
     * <p>
     * <code>
     * add(column, new Float(value), comparison);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value A float.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria add( String column,
                        float value,
                        String comparison)
    {
        add(column, new Float(value), comparison);
        return this;
    }

    /**
     * Convenience method to add a double to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * add(column, new Double(value), EQUAL);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value A double.
     *
     * @return A modified Criteria object.
     */
    public Criteria add( String column,
                        double value)
    {
        add(column, new Double(value));
        return this;
    }

    /**
     * Convenience method to add a double to Criteria
     * Equal to
     *
     * <p>
     * <code>
     * add(column, new Double(value), comparison);
     * </code>
     *
     * @param column The column to run the comparison on
     * @param value A double.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria add( String column,
                        double value,
                        String comparison)
    {
        add(column, new Double(value), comparison);
        return this;
    }

    /**
     * @deprecated These methods were wrongly named and are misleading.
                   Use addDate() instead.
     */
    public Criteria addTime( String column,
                             int year,
                             int month,
                             int date )
        {
        add(column, new GregorianCalendar(year, month, date) );
        return this;
        }

    /**
     * @deprecated These methods were wrongly named and are misleading.
                   Use addDate() instead.
     */
    public Criteria addTime( String column,
                             int year,
                             int month,
                             int date,
                             String comparison)
    {
        add(column, new GregorianCalendar(year, month, date), comparison );
        return this;
    }

    /**
     * Convenience method to add a Date object specified by
     * year, month, and date into the Criteria
     * Equal to
     *
     * <p>
     * <code>
     * add(column, new GregorianCalendar(year, month,date), EQUAL);
     * </code>
     *
     * @param column A String value to use as column.
     * @param year An int with the year.
     * @param month An int with the month.
     * @param date An int with the date.
     * @return A modified Criteria object.
     */
    public Criteria addDate( String column,
                             int year,
                             int month,
                             int date )
    {
        add(column, new GregorianCalendar(year, month, date) );
        return this;
    }

    /**
     * Convenience method to add a Date object specified by
     * year, month, and date into the Criteria
     * Equal to
     *
     * <p>
     * <code>
     * add(column, new GregorianCalendar(year, month,date), comparison);
     * </code>
     *
     * @param column The column to run the comparison on
     * @param year An int with the year.
     * @param month An int with the month.
     * @param date An int with the date.
     * @param comparison String describing how to compare the column with the value
     * @return A modified Criteria object.
     */
    public Criteria addDate( String column,
                             int year,
                             int month,
                             int date,
                             String comparison)
    {
        add(column, new GregorianCalendar(year, month, date), comparison );
        return this;
    }

    /* *
     * Convenience method to add a Key to Criteria.
     *
     * @param key A String value to use as key.
     * @param value A Key.
     * @return A modified Criteria object.
     * /
    public Criteria add ( String key,
                          Key value )
    {
        add( key, value.getInternalObject() );
        return this;
    }
    */

    /**
     * This is the way that you should add a join of two tables.  For
     * example:
     *
     * <p>
     * AND PROJECT.PROJECT_ID=FOO.PROJECT_ID
     * <p>
     *
     * left = PROJECT.PROJECT_ID
     * right = FOO.PROJECT_ID
     *
     * @param left A String with the left side of the join.
     * @param right A String with the right side of the join.
     * @return A modified Criteria object.
     */
    public Criteria addJoin( String left,
                             String right)
    {
        if ( joinL == null )
        {
            joinL = new ArrayList(3);
            joinR = new ArrayList(3);
        }
        joinL.add(left);
        joinR.add(right);

        return this;
    }

    /**
     * get one side of the set of possible joins.  This method is meant to
     * be called by BasePeer.
     */
    public List getJoinL()
    {
        return joinL;
    }

    /**
     * get one side of the set of possible joins.  This method is meant to
     * be called by BasePeer.
     */
    public List getJoinR()
    {
        return joinR;
    }

    /**
     * Adds an 'IN' clause with the criteria supplied as an Object
     * array.  For example:
     *
     * <p>
     * FOO.NAME IN ('FOO', 'BAR', 'ZOW')
     * <p>
     *
     * where 'values' contains three objects that evaluate to the
     * respective strings above when .toString() is called.
     *
     * If a criterion for the requested column already exists, it is
     * replaced.
     *
     * @param column The column to run the comparison on
     * @param values An Object[] with the allowed values.
     * @return A modified Criteria object.
     */
    public Criteria addIn(String column,
                          Object[] values)
    {
        add(column, (Object)values, Criteria.IN);
        return this;
    }

    /**
     * Adds an 'IN' clause with the criteria supplied as an int array.
     * For example:
     *
     * <p>
     * FOO.ID IN ('2', '3', '7')
     * <p>
     *
     * where 'values' contains those three integers.
     *
     * If a criterion for the requested column already exists, it is
     * replaced.
     *
     * @param column The column to run the comparison on
     * @param values An int[] with the allowed values.
     * @return A modified Criteria object.
     */
    public Criteria addIn(String column,
                          int[] values)
    {
        add(column, (Object)values, Criteria.IN);
        return this;
    }

    /**
     * Adds an 'IN' clause with the criteria supplied as a Vector.
     * For example:
     *
     * <p>
     * FOO.NAME IN ('FOO', 'BAR', 'ZOW')
     * <p>
     *
     * where 'values' contains three objects that evaluate to the
     * respective strings above when .toString() is called.
     *
     * If a criterion for the requested column already exists, it is
     * replaced.
     *
     * @param column The column to run the comparison on
     * @param values A List with the allowed values.
     * @return A modified Criteria object.
     */
    public Criteria addIn(String column,
                          List values)
    {
        add(column, (Object)values, Criteria.IN);
        return this;
    }

    /**
     * Adds a 'NOT IN' clause with the criteria supplied as an Object
     * array.  For example:
     *
     * <p>
     * FOO.NAME NOT IN ('FOO', 'BAR', 'ZOW')
     * <p>
     *
     * where 'values' contains three objects that evaluate to the
     * respective strings above when .toString() is called.
     *
     * If a criterion for the requested column already exists, it is
     * replaced.
     *
     * @param column The column to run the comparison on
     * @param values An Object[] with the disallowed values.
     * @return A modified Criteria object.
     */
    public Criteria addNotIn(String column,
                             Object[] values)
    {
        add(column, (Object)values, Criteria.NOT_IN);
        return this;
    }

    /**
     * Adds a 'NOT IN' clause with the criteria supplied as an int
     * array.  For example:
     *
     * <p>
     * FOO.ID NOT IN ('2', '3', '7')
     * <p>
     *
     * where 'values' contains those three integers.
     *
     * If a criterion for the requested column already exists, it is
     * replaced.
     *
     * @param column The column to run the comparison on
     * @param values An int[] with the disallowed values.
     * @return A modified Criteria object.
     */
    public Criteria addNotIn(String column,
                             int[] values)
    {
        add(column, (Object)values, Criteria.NOT_IN);
        return this;
    }

    /**
     * Adds a 'NOT IN' clause with the criteria supplied as a Vector.
     * For example:
     *
     * <p>
     * FOO.NAME NOT IN ('FOO', 'BAR', 'ZOW')
     * <p>
     *
     * where 'values' contains three objects that evaluate to the
     * respective strings above when .toString() is called.
     *
     * If a criterion for the requested column already exists, it is
     * replaced.
     *
     * @param column The column to run the comparison on
     * @param values A List with the disallowed values.
     * @return A modified Criteria object.
     */
    public Criteria addNotIn(String column,
                             List values)
    {
        add(column, (Object)values, Criteria.NOT_IN);
        return this;
    }

    /**
     * Adds "ALL " to the SQL statement.
     */
    public void setAll()
    {
        selectModifiers.add(ALL);
    }

    /**
     * Adds "DISTINCT " to the SQL statement.
     */
    public void setDistinct()
    {
        selectModifiers.add(DISTINCT);
    }

    /**
     * Sets ignore case.
     *
     * @param b True if case should be ignored.
     * @return A modified Criteria object.
     */
    public Criteria setIgnoreCase(boolean b)
    {
        ignoreCase = b;
        return this;
    }

    /**
     * Is ignore case on or off?
     *
     * @return True if case is ignored.
     */
    public boolean isIgnoreCase()
    {
        return ignoreCase;
    }

    /**
     * Set single record?
     *
     * @param b True if a single record should be returned.
     * @return A modified Criteria object.
     */
    public Criteria setSingleRecord(boolean b)
    {
        singleRecord = b;
        return this;
    }

    /**
     * Is single record?
     *
     * @return True if a single record is being returned.
     */
    public boolean isSingleRecord()
    {
        return singleRecord;
    }

    /**
     * Set cascade.
     *
     * @param b True if cascade is set.
     * @return A modified Criteria object.
     */
    public Criteria setCascade(boolean b)
    {
        cascade = b;
        return this;
    }

    /**
     * Is cascade set?
     *
     * @return True if cascade is set.
     */
    public boolean isCascade()
    {
        return cascade;
    }

    /**
     * Set limit.
     *
     * @param limit An int with the value for limit.
     * @return A modified Criteria object.
     */
    public Criteria setLimit(int limit)
    {
        this.limit = limit;
        return this;
    }

    /**
     * Get limit.
     *
     * @return An int with the value for limit.
     */
    public int getLimit()
    {
        return limit;
    }

    /**
     * Set offset.
     *
     * @param offset An int with the value for offset.
     * @return A modified Criteria object.
     */
    public Criteria setOffset(int offset)
    {
        this.offset = offset;
        return this;
    }

    /**
     * Get offset.
     *
     * @return An int with the value for offset.
     */
    public int getOffset()
    {
        return offset;
    }

    /**
     * Add select column.
     *
     * @param name A String with the name of the select column.
     * @return A modified Criteria object.
     */
    public Criteria addSelectColumn( String name )
    {
        selectColumns.add( name );
        return this;
    }

    /**
     * Get select columns.
     *
     * @return A StringStackBuffer with the name of the select
     * columns.
     */
    public StringStackBuffer getSelectColumns()
    {
        return selectColumns;
    }

    /**
     * Get select modifiers.
     *
     * @return A StringStackBuffer with the select modifiers.
     */
    public StringStackBuffer getSelectModifiers()
    {
        return selectModifiers;
    }

    /**
     * @deprecated Use addAscendingOrderByColumn() instead.
     */
    public Criteria addOrderByColumn( String name )
    {
        orderByColumns.add( name );
        return this;
    }

    /**
     * Add order by column name, explicitly specifying ascending.
     *
     * @param name The name of the column to order by.
     * @return A modified Criteria object.
     */
    public Criteria addAscendingOrderByColumn( String name )
    {
        orderByColumns.add( name + " " + ASC );
        return this;
    }

    /**
     * Add order by column name, explicitly specifying descending.
     *
     * @param name The name of the column to order by.
     * @return A modified Criteria object.
     */
    public Criteria addDescendingOrderByColumn( String name )
    {
        orderByColumns.add( name + " " + DESC );
        return this;
    }

    /**
     * Get order by columns.
     *
     * @return A StringStackBuffer with the name of the order columns.
     */
    public StringStackBuffer getOrderByColumns()
    {
        return orderByColumns;
    }

    /**
     * Remove an object from the criteria.
     *
     * @param key A String with the key to be removed.
     * @return The removed object.
     */
    public Object remove(String key)
    {
        Object foo = super.remove(key);
        if (foo instanceof Criterion)
            return ((Criterion)foo).getValue();
        return foo;
    }

    /**
     * Build a string representation of the Criteria.
     *
     * @return A String with the representation of the Criteria.
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer("Criteria:: ");
        Enumeration e = keys();
        while (e.hasMoreElements())
        {
            String key = (String)e.nextElement();
            sb.append(key).append("<=>")
                .append(super.get(key).toString()).append(":  ");
        }

        try
        {
            sb.append("\nCurrent Query SQL (may not be complete or applicable): ")
              .append(BasePeer.createQueryString(this));
        }
        catch (Exception exc) {}

        return sb.toString();
    }

    /**
     * This method checks another Criteria to see if they contain
     * the same attributes and hashtable entries.
     */
    public boolean equals(Object crit)
    {
        boolean isEquiv = false;
        if ( crit == null || !(crit instanceof Criteria) )
        {
            isEquiv = false;
        }
        else if ( this == crit )
        {
            isEquiv = true;
        }
        else if ( this.size() == ((Criteria)crit).size() )
        {
            Criteria criteria = (Criteria)crit;
            if ( this.offset == criteria.getOffset()
                 && this.limit == criteria.getLimit()
                 && ignoreCase == criteria.isIgnoreCase()
                 && singleRecord == criteria.isSingleRecord()
                 && cascade == criteria.isCascade()
                 && dbName.equals(criteria.getDbName())
                 && selectModifiers.equals(criteria.getSelectModifiers())
                 && selectColumns.equals(criteria.getSelectColumns())
                 && orderByColumns.equals(criteria.getOrderByColumns())
               )
            {
                isEquiv = true;
                for (Enumeration e=criteria.keys(); e.hasMoreElements(); )
                {
                    String key = (String)e.nextElement();
                    if ( this.containsKey(key) )
                    {
                        Criterion a = this.getCriterion(key);
                        Criterion b = criteria.getCriterion(key);
                        if ( !a.equals(b) )
                        {
                            isEquiv = false;
                            break;
                        }
                    }
                    else
                    {
                        isEquiv = false;
                        break;
                    }
                }
            }
        }
        return isEquiv;
    }

    /*
     *------------------------------------------------------------------------
     *
     * Start of the "and" methods
     *
     *------------------------------------------------------------------------
     */

    /**
     * This method adds a prepared Criterion object to the Criteria.
     * You can get a new, empty Criterion object with the
     * getNewCriterion() method. If a criterion for the requested column
     * already exists, it is "AND"ed to the existing criterion.
     * This is used as follows:
     *
     * <p>
     * <code>
     * Criteria crit = new Criteria();
     * Criteria.Criterion c = crit.getNewCriterion(BasePeer.ID, new Integer(5), Criteria.LESS_THAN);
     * crit.and(c);
     * </code>
     *
     * @param c A Criterion object
     *
     * @return A modified Criteria object.
     */
    public Criteria and( Criterion c)
    {
        Criterion oc = getCriterion(c.getTable()+"."+c.getColumn());

        if(oc == null)
        {
            add(c);
        }
        else
        {
            oc.and(c);
        }
        return this;
    }

    /**
     * This method adds a new criterion to the list of criterias. If a
     * criterion for the requested column already exists, it is
     * "AND"ed to the existing criterion. This is used as follows:
     *
     * <p>
     * <code>
     * Criteria crit = new Criteria().and(&quot;column&quot;,
     *                                      &quot;value&quot; );
     * </code>
     *
     * An EQUAL comparison is used for column and value.
     *
     * The name of the table must be used implicitly in the column name,
     * so the Column name must be something like 'TABLE.id'. If you
     * don't like this, you can use the and(table, column, value) method.
     *
     * @param column The column to run the comparison on
     * @param value An Object.
     *
     * @return A modified Criteria object.
     */
    public Criteria and( String column,
                         Object value )
    {
        and(column, value, EQUAL);
        return this;
    }

    /**
     * This method adds a new criterion to the list of criterias.
     * If a criterion for the requested column already exists, it is
     * "AND"ed to the existing criterion. If is used as follow:
     *
     * <p>
     * <code>
     * Criteria crit = new Criteria().and(&quot;column&quot;,
     *                                      &quot;value&quot;
     *                                      &quot;Criterion.GREATER_THAN&quot;);
     * </code>
     *
     * Any comparison can be used.
     *
     * The name of the table must be used implicitly in the column name,
     * so the Column name must be something like 'TABLE.id'. If you
     * don't like this, you can use the and(table, column, value) method.
     *
     * @param column The column to run the comparison on
     * @param value An Object.
     * @param comparison A String.
     *
     * @return A modified Criteria object.
     */
    public Criteria and( String column,
                         Object value,
                         String comparison )
    {
        Criterion oc = getCriterion(column);
        Criterion nc = new Criterion(column, value, comparison);

        if(oc == null)
        {
            super.put( column, nc);
        }
        else
        {
            oc.and(nc);
        }
        return this;
    }

    /**
     * This method adds a new criterion to the list of criterias.
     * If a criterion for the requested column already exists, it is
     * "AND"ed to the existing criterion. If is used as follows:
     *
     * <p>
     * <code>
     * Criteria crit = new Criteria().and(&quot;table&quot;,
     *                                      &quot;column&quot;,
     *                                      &quot;value&quot; );
     * </code>
     *
     * An EQUAL comparison is used for column and value.
     *
     * @param table Name of the table which contains the column
     * @param column The column to run the comparison on
     * @param value An Object.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria and( String table,
                         String column,
                         Object value )
    {
        and(table, column, value, EQUAL);
        return this;
    }

    /**
     * This method adds a new criterion to the list of criterias.
     * If a criterion for the requested column already exists, it is
     * "AND"ed to the existing criterion. If is used as follows:
     *
     * <p>
     * <code>
     * Criteria crit = new Criteria().and(&quot;table&quot;,
     *                                      &quot;column&quot;,
     *                                      &quot;value&quot;,
     *                                      &quot;Criterion.GREATER_THAN&quot;);
     * </code>
     *
     * Any comparison can be used.
     *
     * @param table Name of table which contains the column
     * @param column The column to run the comparison on
     * @param value An Object.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria and( String table,
                         String column,
                         Object value,
                         String comparison )
    {
        StringBuffer sb = new StringBuffer(table.length()+column.length()+1);
        sb.append(table);
        sb.append('.');
        sb.append(column);

        Criterion oc = getCriterion(table, column);
        Criterion nc = new Criterion(table, column, value, comparison);

        if(oc == null)
        {
            super.put(sb.toString(),nc);
        }
        else
        {
            oc.and(nc);
        }
        return this;
    }

    /**
     * Convenience method to add a boolean to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * and(column, new Boolean(value), EQUAL);
     * </code>
     *
     * @param column The column to run the comparison on
     * @param value A Boolean.
     *
     * @return A modified Criteria object.
     */
    public Criteria and( String column,
                         boolean value )
    {
        and(column, new Boolean(value) );
        return this;
    }

    /**
     * Convenience method to add a boolean to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * and(column, new Boolean(value), comparison);
     * </code>
     *
     * @param column The column to run the comparison on
     * @param value A Boolean.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria and( String column,
                         boolean value,
                         String comparison )
    {
        and(column, new Boolean(value), comparison );
        return this;
    }

    /**
     * Convenience method to add an int to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * and(column, new Integer(value), EQUAL);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value An int.
     *
     * @return A modified Criteria object.
     */
    public Criteria and( String column,
                         int value )
    {
        and(column, new Integer(value) );
        return this;
    }

    /**
     * Convenience method to add an int to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * and(column, new Integer(value), comparison);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value An int.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria and( String column,
                         int value,
                         String comparison )
    {
        and(column, new Integer(value), comparison );
        return this;
    }

    /**
     * Convenience method to add a long to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * and(column, new Long(value), EQUAL);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value A long.
     *
     * @return A modified Criteria object.
     */
    public Criteria and( String column,
                         long value)
    {
        and(column, new Long(value));
        return this;
    }

    /**
     * Convenience method to add a long to Criteria
     * Equal to
     *
     * <p>
     * <code>
     * and(column, new Long(value), comparison);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value A long.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria and( String column,
                         long value,
                         String comparison)
    {
        and(column, new Long(value), comparison);
        return this;
    }

    /**
     * Convenience method to add a float to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * and(column, new Float(value), EQUAL);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value A float.
     *
     * @return A modified Criteria object.
     */
    public Criteria and( String column,
                         float value)
    {
        and(column, new Float(value));
        return this;
    }

    /**
     * Convenience method to add a float to Criteria
     * Equal to
     *
     * <p>
     * <code>
     * and(column, new Float(value), comparison);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value A float.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria and( String column,
                         float value,
                         String comparison)
    {
        and(column, new Float(value), comparison);
        return this;
    }

    /**
     * Convenience method to add a double to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * and(column, new Double(value), EQUAL);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value A double.
     *
     * @return A modified Criteria object.
     */
    public Criteria and( String column,
                         double value)
    {
        and(column, new Double(value));
        return this;
    }

    /**
     * Convenience method to add a double to Criteria
     * Equal to
     *
     * <p>
     * <code>
     * and(column, new Double(value), comparison);
     * </code>
     *
     * @param column The column to run the comparison on
     * @param value A double.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria and( String column,
                         double value,
                         String comparison)
    {
        and(column, new Double(value), comparison);
        return this;
    }

    /**
     * Convenience method to add a Date object specified by
     * year, month, and date into the Criteria
     * Equal to
     *
     * <p>
     * <code>
     * and(column, new GregorianCalendar(year, month,date), EQUAL);
     * </code>
     *
     * @param column A String value to use as column.
     * @param year An int with the year.
     * @param month An int with the month.
     * @param date An int with the date.
     * @return A modified Criteria object.
     */
    public Criteria andDate( String column,
                             int year,
                             int month,
                             int date )
    {
        and(column, new GregorianCalendar(year, month, date) );
        return this;
    }

    /**
     * Convenience method to add a Date object specified by
     * year, month, and date into the Criteria
     * Equal to
     *
     * <p>
     * <code>
     * and(column, new GregorianCalendar(year, month,date), comparison);
     * </code>
     *
     * @param column The column to run the comparison on
     * @param year An int with the year.
     * @param month An int with the month.
     * @param date An int with the date.
     * @param comparison String describing how to compare the column with the value
     * @return A modified Criteria object.
     */
    public Criteria andDate( String column,
                             int year,
                             int month,
                             int date,
                             String comparison)
    {
        and(column, new GregorianCalendar(year, month, date), comparison );
        return this;
    }

    /**
     * Adds an 'IN' clause with the criteria supplied as an Object
     * array.  For example:
     *
     * <p>
     * FOO.NAME IN ('FOO', 'BAR', 'ZOW')
     * <p>
     *
     * where 'values' contains three objects that evaluate to the
     * respective strings above when .toString() is called.
     *
     * If a criterion for the requested column already exists, it is
     * "AND"ed to the existing criterion.
     *
     * @param column The column to run the comparison on
     * @param values An Object[] with the allowed values.
     * @return A modified Criteria object.
     */
    public Criteria andIn(String column,
                          Object[] values)
    {
        and(column, (Object)values, Criteria.IN);
        return this;
    }

    /**
       * Adds an 'IN' clause with the criteria supplied as an int array.
       * For example:
       *
     * <p>
     * FOO.ID IN ('2', '3', '7')
     * <p>
     *
     * where 'values' contains those three integers.
     *
     * If a criterion for the requested column already exists, it is
     * "AND"ed to the existing criterion.
     *
     * @param column The column to run the comparison on
     * @param values An int[] with the allowed values.
     * @return A modified Criteria object.
     */
    public Criteria andIn(String column,
                          int[] values)
    {
        and(column, (Object)values, Criteria.IN);
        return this;
    }

    /**
     * Adds an 'IN' clause with the criteria supplied as a Vector.
     * For example:
     *
     * <p>
     * FOO.NAME IN ('FOO', 'BAR', 'ZOW')
     * <p>
     *
     * where 'values' contains three objects that evaluate to the
     * respective strings above when .toString() is called.
     *
     * If a criterion for the requested column already exists, it is
     * "AND"ed to the existing criterion.
     *
     * @param column The column to run the comparison on
     * @param values A Vector with the allowed values.
     * @return A modified Criteria object.
     */
    public Criteria andIn(String column,
                          Vector values)
    {
        and(column, (Object)values, Criteria.IN);
        return this;
    }

    /**
     * Adds a 'NOT IN' clause with the criteria supplied as an Object
     * array.  For example:
     *
     * <p>
     * FOO.NAME NOT IN ('FOO', 'BAR', 'ZOW')
     * <p>
     *
     * where 'values' contains three objects that evaluate to the
     * respective strings above when .toString() is called.
     *
     * If a criterion for the requested column already exists, it is
     * "AND"ed to the existing criterion.
     *
     * @param column The column to run the comparison on
     * @param values An Object[] with the disallowed values.
     * @return A modified Criteria object.
     */
    public Criteria andNotIn(String column,
                             Object[] values)
    {
        and(column, (Object)values, Criteria.NOT_IN);
        return this;
    }

    /**
     * Adds a 'NOT IN' clause with the criteria supplied as an int
     * array.  For example:
     *
     * <p>
     * FOO.ID NOT IN ('2', '3', '7')
     * <p>
     *
     * where 'values' contains those three integers.
     *
     * If a criterion for the requested column already exists, it is
     * "AND"ed to the existing criterion.
     *
     * @param column The column to run the comparison on
     * @param values An int[] with the disallowed values.
     * @return A modified Criteria object.
     */
     public Criteria andNotIn(String column,
                             int[] values)
    {
        and(column, (Object)values, Criteria.NOT_IN);
        return this;
    }

    /**
     * Adds a 'NOT IN' clause with the criteria supplied as a Vector.
     * For example:
     *
     * <p>
     * FOO.NAME NOT IN ('FOO', 'BAR', 'ZOW')
     * <p>
     *
     * where 'values' contains three objects that evaluate to the
     * respective strings above when .toString() is called.
     *
     * If a criterion for the requested column already exists, it is
     * "AND"ed to the existing criterion.
     *
     * @param column The column to run the comparison on
     * @param values A Vector with the disallowed values.
     * @return A modified Criteria object.
     */
    public Criteria andNotIn(String column,
                             Vector values)
    {
        and(column, (Object)values, Criteria.NOT_IN);
        return this;
    }

    /*
     *------------------------------------------------------------------------
     *
     * Start of the "or" methods
     *
     *------------------------------------------------------------------------
     */

    /**
     * This method adds a prepared Criterion object to the Criteria.
     * You can get a new, empty Criterion object with the
     * getNewCriterion() method. If a criterion for the requested column
     * already exists, it is "OR"ed to the existing criterion.
     * This is used as follows:
     *
     * <p>
     * <code>
     * Criteria crit = new Criteria();
     * Criteria.Criterion c = crit.getNewCriterion(BasePeer.ID, new Integer(5), Criteria.LESS_THAN);
     * crit.or(c);
     * </code>
     *
     * @param c A Criterion object
     *
     * @return A modified Criteria object.
     */
    public Criteria or( Criterion c)
    {
        Criterion oc = getCriterion(c.getTable()+"."+c.getColumn());

        if(oc == null)
        {
            add(c);
        }
        else
        {
            oc.or(c);
        }
        return this;
    }

    /**
     * This method adds a new criterion to the list of criterias. If a
     * criterion for the requested column already exists, it is
     * "OR"ed to the existing criterion. This is used as follows:
     *
     * <p>
     * <code>
     * Criteria crit = new Criteria().or(&quot;column&quot;,
     *                                      &quot;value&quot; );
     * </code>
     *
     * An EQUAL comparison is used for column and value.
     *
     * The name of the table must be used implicitly in the column name,
     * so the Column name must be something like 'TABLE.id'. If you
     * don't like this, you can use the or(table, column, value) method.
     *
     * @param column The column to run the comparison on
     * @param value An Object.
     *
     * @return A modified Criteria object.
     */
    public Criteria or( String column,
                        Object value )
    {
        or(column, value, EQUAL);
        return this;
    }

    /**
     * This method adds a new criterion to the list of criterias.
     * If a criterion for the requested column already exists, it is
     * "OR"ed to the existing criterion. If is used as follow:
     *
     * <p>
     * <code>
     * Criteria crit = new Criteria().or(&quot;column&quot;,
     *                                      &quot;value&quot;
     *                                      &quot;Criterion.GREATER_THAN&quot;);
     * </code>
     *
     * Any comparison can be used.
     *
     * The name of the table must be used implicitly in the column name,
     * so the Column name must be something like 'TABLE.id'. If you
     * don't like this, you can use the or(table, column, value) method.
     *
     * @param column The column to run the comparison on
     * @param value An Object.
     * @param comparison A String.
     *
     * @return A modified Criteria object.
     */
    public Criteria or( String column,
                        Object value,
                        String comparison )
    {
        Criterion oc = getCriterion(column);
        Criterion nc = new Criterion(column, value, comparison);

        if(oc == null)
        {
            super.put( column, nc);
        }
        else
        {
            oc.or(nc);
        }
        return this;
    }

    /**
     * This method adds a new criterion to the list of criterias.
     * If a criterion for the requested column already exists, it is
     * "OR"ed to the existing criterion. If is used as follows:
     *
     * <p>
     * <code>
     * Criteria crit = new Criteria().or(&quot;table&quot;,
     *                                      &quot;column&quot;,
     *                                      &quot;value&quot; );
     * </code>
     *
     * An EQUAL comparison is used for column and value.
     *
     * @param table Name of the table which contains the column
     * @param column The column to run the comparison on
     * @param value An Object.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria or( String table,
                        String column,
                        Object value )
    {
        or(table, column, value, EQUAL);
        return this;
    }

    /**
     * This method adds a new criterion to the list of criterias.
     * If a criterion for the requested column already exists, it is
     * "OR"ed to the existing criterion. If is used as follows:
     *
     * <p>
     * <code>
     * Criteria crit = new Criteria().or(&quot;table&quot;,
     *                                      &quot;column&quot;,
     *                                      &quot;value&quot;,
     *                                      &quot;Criterion.GREATER_THAN&quot;);
     * </code>
     *
     * Any comparison can be used.
     *
     * @param table Name of table which contains the column
     * @param column The column to run the comparison on
     * @param value An Object.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria or( String table,
                        String column,
                        Object value,
                        String comparison )
    {
        StringBuffer sb = new StringBuffer(table.length()+column.length()+1);
        sb.append(table);
        sb.append('.');
        sb.append(column);

        Criterion oc = getCriterion(table, column);
        Criterion nc = new Criterion(table, column, value, comparison);
        if(oc == null)
        {
            super.put(sb.toString(), nc);
        }
        else
        {
            oc.or(nc);
        }
        return this;
    }

    /**
     * Convenience method to add a boolean to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * or(column, new Boolean(value), EQUAL);
     * </code>
     *
     * @param column The column to run the comparison on
     * @param value A Boolean.
     *
     * @return A modified Criteria object.
     */
    public Criteria or( String column,
                        boolean value )
    {
        or(column, new Boolean(value) );
        return this;
    }

    /**
     * Convenience method to add a boolean to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * or(column, new Boolean(value), comparison);
     * </code>
     *
     * @param column The column to run the comparison on
     * @param value A Boolean.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria or( String column,
                        boolean value,
                        String comparison )
    {
        or(column, new Boolean(value), comparison );
        return this;
    }

    /**
     * Convenience method to add an int to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * or(column, new Integer(value), EQUAL);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value An int.
     *
     * @return A modified Criteria object.
     */
    public Criteria or( String column,
                        int value )
    {
        or(column, new Integer(value) );
        return this;
    }

    /**
     * Convenience method to add an int to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * or(column, new Integer(value), comparison);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value An int.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria or( String column,
                        int value,
                        String comparison )
    {
        or(column, new Integer(value), comparison );
        return this;
    }

    /**
     * Convenience method to add a long to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * or(column, new Long(value), EQUAL);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value A long.
     *
     * @return A modified Criteria object.
     */
    public Criteria or( String column,
                        long value)
    {
        or(column, new Long(value));
        return this;
    }

    /**
     * Convenience method to add a long to Criteria
     * Equal to
     *
     * <p>
     * <code>
     * or(column, new Long(value), comparison);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value A long.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria or( String column,
                        long value,
                        String comparison)
    {
        or(column, new Long(value), comparison);
        return this;
    }

    /**
     * Convenience method to add a float to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * or(column, new Float(value), EQUAL);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value A float.
     *
     * @return A modified Criteria object.
     */
    public Criteria or( String column,
                        float value)
    {
        or(column, new Float(value));
        return this;
    }

    /**
     * Convenience method to add a float to Criteria
     * Equal to
     *
     * <p>
     * <code>
     * or(column, new Float(value), comparison);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value A float.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria or( String column,
                        float value,
                        String comparison)
    {
        or(column, new Float(value), comparison);
        return this;
    }

    /**
     * Convenience method to add a double to Criteria.
     * Equal to
     *
     * <p>
     * <code>
     * or(column, new Double(value), EQUAL);
     * </code>
     *
     *
     * @param column The column to run the comparison on
     * @param value A double.
     *
     * @return A modified Criteria object.
     */
    public Criteria or( String column,
                        double value)
    {
        or(column, new Double(value));
        return this;
    }

    /**
     * Convenience method to add a double to Criteria
     * Equal to
     *
     * <p>
     * <code>
     * or(column, new Double(value), comparison);
     * </code>
     *
     * @param column The column to run the comparison on
     * @param value A double.
     * @param comparison String describing how to compare the column with the value
     *
     * @return A modified Criteria object.
     */
    public Criteria or( String column,
                        double value,
                        String comparison)
    {
        or(column, new Double(value), comparison);
        return this;
    }

    /**
     * Convenience method to add a Date object specified by
     * year, month, and date into the Criteria
     * Equal to
     *
     * <p>
     * <code>
     * or(column, new GregorianCalendar(year, month,date), EQUAL);
     * </code>
     *
     * @param column A String value to use as column.
     * @param year An int with the year.
     * @param month An int with the month.
     * @param date An int with the date.
     * @return A modified Criteria object.
     */
    public Criteria orDate( String column,
                            int year,
                            int month,
                            int date )
    {
        or(column, new GregorianCalendar(year, month, date) );
        return this;
    }

    /**
     * Convenience method to add a Date object specified by
     * year, month, and date into the Criteria
     * Equal to
     *
     * <p>
     * <code>
     * or(column, new GregorianCalendar(year, month,date), comparison);
     * </code>
     *
     * @param column The column to run the comparison on
     * @param year An int with the year.
     * @param month An int with the month.
     * @param date An int with the date.
     * @param comparison String describing how to compare the column with the value
     * @return A modified Criteria object.
     */
    public Criteria orDate( String column,
                            int year,
                            int month,
                            int date,
                            String comparison)
    {
        or(column, new GregorianCalendar(year, month, date), comparison );
        return this;
    }

    /**
     * Adds an 'IN' clause with the criteria supplied as an Object
     * array.  For example:
     *
     * <p>
     * FOO.NAME IN ('FOO', 'BAR', 'ZOW')
     * <p>
     *
     * where 'values' contains three objects that evaluate to the
     * respective strings above when .toString() is called.
     *
     * If a criterion for the requested column already exists, it is
     * "OR"ed to the existing criterion.
     *
     * @param column The column to run the comparison on
     * @param values An Object[] with the allowed values.
     * @return A modified Criteria object.
     */
    public Criteria orIn(String column,
                         Object[] values)
    {
        or(column, (Object)values, Criteria.IN);
        return this;
    }

    /**
     * Adds an 'IN' clause with the criteria supplied as an int array.
     * For example:
     *
     * <p>
     * FOO.ID IN ('2', '3', '7')
     * <p>
     *
     * where 'values' contains those three integers.
     *
     * If a criterion for the requested column already exists, it is
     * "OR"ed to the existing criterion.
     *
     * @param column The column to run the comparison on
     * @param values An int[] with the allowed values.
     * @return A modified Criteria object.
     */
    public Criteria orIn(String column,
                         int[] values)
    {
        or(column, (Object)values, Criteria.IN);
        return this;
    }

    /**
     * Adds an 'IN' clause with the criteria supplied as a Vector.
     * For example:
     *
     * <p>
     * FOO.NAME IN ('FOO', 'BAR', 'ZOW')
     * <p>
     *
     * where 'values' contains three objects that evaluate to the
     * respective strings above when .toString() is called.
     *
     * If a criterion for the requested column already exists, it is
     * "OR"ed to the existing criterion.
     *
     * @param column The column to run the comparison on
     * @param values A Vector with the allowed values.
     * @return A modified Criteria object.
     */
    public Criteria orIn(String column,
                         Vector values)
    {
        or(column, (Object)values, Criteria.IN);
        return this;
    }

    /**
     * Adds a 'NOT IN' clause with the criteria supplied as an Object
     * array.  For example:
     *
     * <p>
     * FOO.NAME NOT IN ('FOO', 'BAR', 'ZOW')
     * <p>
     *
     * where 'values' contains three objects that evaluate to the
     * respective strings above when .toString() is called.
     *
     * If a criterion for the requested column already exists, it is
     * "OR"ed to the existing criterion.
     *
     * @param column The column to run the comparison on
     * @param values An Object[] with the disallowed values.
     * @return A modified Criteria object.
     */
    public Criteria orNotIn(String column,
                            Object[] values)
    {
        or(column, (Object)values, Criteria.NOT_IN);
        return this;
    }

    /**
     * Adds a 'NOT IN' clause with the criteria supplied as an int
     * array.  For example:
     *
     * <p>
     * FOO.ID NOT IN ('2', '3', '7')
     * <p>
     *
     * where 'values' contains those three integers.
     *
     * If a criterion for the requested column already exists, it is
     * "OR"ed to the existing criterion.
     *
     * @param column The column to run the comparison on
     * @param values An int[] with the disallowed values.
     * @return A modified Criteria object.
     */
    public Criteria orNotIn(String column,
                            int[] values)
    {
        or(column, (Object)values, Criteria.NOT_IN);
        return this;
    }

    /**
     * Adds a 'NOT IN' clause with the criteria supplied as a Vector.
     * For example:
     *
     * <p>
     * FOO.NAME NOT IN ('FOO', 'BAR', 'ZOW')
     * <p>
     *
     * where 'values' contains three objects that evaluate to the
     * respective strings above when .toString() is called.
     *
     * If a criterion for the requested column already exists, it is
     * "OR"ed to the existing criterion.
     *
     * @param column The column to run the comparison on
     * @param values A Vector with the disallowed values.
     * @return A modified Criteria object.
     */
    public Criteria orNotIn(String column,
                            Vector values)
    {
        or(column, (Object)values, Criteria.NOT_IN);
        return this;
    }


    /**
     * Peers can set this flag to notify BasePeer that the table(s) involved
     * in the Criteria contain Blobs, so that the operation can be placed
     * in a transaction if the db requires it.
     * This is primarily to support Postgresql.
     */
    public void setBlobFlag()
    {
        blobFlag = true;
    }

    /**
     * This is an inner class that describes an object in the
     * criteria.
     */
    public final class Criterion implements Serializable
    {
        public static final String AND = " AND ";
        public static final String OR = " OR ";

        /** Value of the CO. */
        private Object value;

        /** Comparison value. */
        private String comparison;

        /** Table name. */
        private String table;

        /** Column name. */
        private String column;

        /** flag to ignore case in comparision */
        private boolean ignoreStringCase = false;

        /**
         * The DB adaptor which might be used to get db specific
         * variations of sql.
         */
        private DB db;

        /**
         * Another Criterion connected to this one by an OR clause.
         */
        private Criterion or;

        /**
         * Another criterion connected to this one by an AND clause.
         */
        private Criterion and;

        /**
         * Creates a new instance, initializing a couple members.
         */
        private Criterion( Object val, String comp )
        {
            this.value = val;
            this.comparison = comp;
        }

        /**
         * Create a new instance.
         *
         * @param table A String with the name of the table.
         * @param column A String with the name of the column.
         * @param val An Object with the value for the Criteria.
         * @param comp A String with the comparison value.
         */
        Criterion( String table,
                        String column,
                        Object val,
                        String comp )
        {
            this(val, comp);
            this.table = (table == null ? "" : table);
            this.column = (column == null ? "" : column);
        }

        /**
         * Create a new instance.
         *
         * @param tableColumn A String with the full name of the
         * column.
         * @param val An Object with the value for the Criteria.
         * @param comp A String with the comparison value.
         */
        Criterion( String tableColumn,
                        Object val,
                        String comp )
        {
            this(val, comp);
            int dot = tableColumn.indexOf('.');
            if ( dot == -1 )
            {
                table = "";
                column = tableColumn;
            }
            else
            {
                table = tableColumn.substring(0, dot);
                column = tableColumn.substring(dot + 1);
            }
        }

        /**
         * Create a new instance.
         *
         * @param table A String with the name of the table.
         * @param column A String with the name of the column.
         * @param val An Object with the value for the Criteria.
         */
        Criterion( String table,
                        String column,
                        Object val )
        {
            this(table, column, val, EQUAL);
        }

        /**
         * Create a new instance.
         *
         * @param tableColumn A String with the full name of the
         * column.
         * @param val An Object with the value for the Criteria.
         */
        Criterion( String tableColumn,
                        Object val )
        {
            this(tableColumn, val, EQUAL);
        }

        /**
         * Get the column name.
         *
         * @return A String with the column name.
         */
        public String getColumn()
        {
            return this.column;
        }

        /**
         * Set the table name.
         *
         * @param name A String with the table name.
         */
        public void setTable(String name)
        {
            this.table=name;
        }

        /**
         * Get the table name.
         *
         * @return A String with the table name.
         */
        public String getTable()
        {
            return this.table;
        }

        /**
         * Get the comparison.
         *
         * @return A String with the comparison.
         */
        public String getComparison()
        {
            return this.comparison;
        }

        /**
         * Get the value.
         *
         * @return An Object with the value.
         */
        public Object getValue()
        {
            return this.value;
        }

        /**
         * Get the value of db.
         * The DB adaptor which might be used to get db specific
         * variations of sql.
         * @return value of db.
         */
        public DB getDb()
        {
            return db;
        }

        /**
         * Set the value of db.
         * The DB adaptor might be used to get db specific
         * variations of sql.
         * @param v  Value to assign to db.
         */
        public void setDB(DB  v)
        {
            this.db = v;
            if ( and != null )
            {
                and.setDB(v);
            }
            if ( or != null )
            {
                or.setDB(v);
            }
        }

        /**
         * Sets ignore case.
         *
         * @param b True if case should be ignored.
         * @return A modified Criteria object.
         */
        public Criterion setIgnoreCase(boolean b)
        {
            ignoreStringCase = b;
            return this;
        }

        /**
         * Is ignore case on or off?
         *
         * @return True if case is ignored.
         */
         public boolean isIgnoreCase()
         {
             return ignoreStringCase;
         }

        /**
         *  get the criterion from this Criterion's AND field.
         */
        public Criterion getAnd()
        {
            return and;
        }

        /**
         * Append a Criteria onto this Criteria's AND field.
         */
        public Criterion and(Criterion criterion)
        {
            if (this.and == null)
            {
                this.and = criterion;
            }
            else
            {
                this.and.and(criterion);
            }
            return this;
        }

        /**
         *  get the criterion from this Criterion's AND field.
         */
        public Criterion getOr()
        {
            return or;
        }

        /**
         * Append a Criterion onto this Criterion's OR field.
         */
        public Criterion or(Criterion criterion)
        {
            if (this.or == null)
            {
                this.or = criterion;
            }
            else
            {
                this.or.or(criterion);
            }
            return this;
        }

        /**
         * Appends a representation of the Criterion onto the buffer.
         */
        public void appendTo(StringBuffer sb)
        {
            //
            // it is alright if value == null
            //

            if (column == null)
            {
                return;
            }

            sb.append('(');
            if ( CUSTOM == comparison )
            {
                if ( value != null && ! "".equals(value) )
                {
                    sb.append((String)value);
                }
            }
            else
            {
                String field = null;
                if  (table == null)
                {
                    field = column;
                }
                else
                {
                    field = new StringBuffer(
                        table.length() + 1 + column.length())
                        .append(table).append('.').append(column)
                        .toString();
                }
                SqlExpression.build(field, value, comparison,
                                    ignoreStringCase, db, sb);
            }

            if (or != null)
            {
                sb.append(OR);
                or.appendTo(sb);
            }

            if (and != null)
            {
                sb.append(AND);
                and.appendTo(sb);
            }
            sb.append(')');
        }

        /**
         * Appends a Prepared Statement representation of the Criterion
         * onto the buffer.
         *
         * @param sb The stringbuffer that will receive the Prepared Statement
         * @param params A list to which Prepared Statement parameters
         * will be appended
         */
        public void appendPsTo(StringBuffer sb, List params)
        {
            if (column == null || value == null)
            {
                return;
            }

            sb.append('(');
            if ( CUSTOM == comparison )
            {
                if ( !"".equals(value) )
                {
                    sb.append((String)value);
                }
            }
            else
            {
                String field = null;
                if  (table == null)
                {
                    field = column;
                }
                else
                {
                    field = new StringBuffer(
                        table.length() + 1 + column.length())
                        .append(table).append('.').append(column)
                        .toString();
                }

                if ( comparison.equals(Criteria.IN) || comparison.equals(Criteria.NOT_IN) )
                {
                    sb.append (field)
                      .append (comparison);

                    StringStackBuffer inClause = new StringStackBuffer();

                    if (value instanceof Vector)
                    {
                        value = ((Vector)value).toArray (new Object[0]);
                    }

                    for (int i = 0; i < Array.getLength(value); i++)
                    {
                        Object item = Array.get(value, i);

                        inClause.add(SqlExpression.processInValue(item, ignoreCase, db));
                    }

                    StringBuffer inString = new StringBuffer();
                    inString.append('(').append(inClause.toString(",")).append(')');

                    sb.append (inString.toString());
                }
                else
                {
                    if (ignoreCase)
                    {
                        sb.append (db.ignoreCase(field))
                          .append (comparison)
                          .append (db.ignoreCase("?"));
                    }
                    else
                    {
                        sb.append (field)
                          .append (comparison)
                          .append (" ? ");
                    }

                    if (value instanceof java.util.Date)
                    {
                        params.add ( new java.sql.Date (((java.util.Date)value).getTime()) );
                    }
                    else if (value instanceof DateKey)
                    {
                        params.add ( new java.sql.Date (((DateKey)value).getDate().getTime()) );
                    }
                    else
                    {
                        params.add (value.toString());
                    }
                }
            }

            if (or != null)
            {
                sb.append(OR);
                or.appendPsTo(sb,params);
            }

            if (and != null)
            {
                sb.append(AND);
                and.appendPsTo(sb,params);
            }
            sb.append(')');
        }

        /**
         * Build a string representation of the Criterion.
         *
         * @return A String with the representation of the Criterion.
         */
        public String toString()
        {
            //
            // it is alright if value == null
            //
            if (column == null)
            {
                return "";
            }

            StringBuffer expr = new StringBuffer(25);
            appendTo(expr);
            return expr.toString();
        }

        /**
         * This method checks another Criteria to see if they contain
         * the same attributes and hashtable entries.
         */
        public boolean equals(Object obj)
        {
            if ( this == obj )
            {
                return true;
            }

            if ( (obj == null) || !(obj instanceof Criterion) )
            {
                return false;
            }

            Criterion crit = (Criterion)obj;

            boolean isEquiv = ( (table == null && crit.getTable() == null)
                || (table != null && table.equals(crit.getTable()))
                              )
                && column.equals(crit.getColumn())
                && comparison.equals(crit.getComparison());

            // we need to check for value equality
            if ( isEquiv )
            {
                Object b = crit.getValue();
                if ( value instanceof Object[] && b instanceof Object[] )
                {
                    isEquiv &= Arrays.equals((Object[])value, (Object[])b);
                }
                else if (value instanceof int[] && b instanceof int[])
                {
                    isEquiv &= Arrays.equals((int[])value, (int[])b);
                }
                else
                {
                    isEquiv &= value.equals(b);
                }
            }

            // check chained criterion
            isEquiv &= (and == null && crit.getAnd() == null )
                || (and != null && and.equals(crit.getAnd()));

            isEquiv &= (or == null && crit.getOr() == null )
                || (or != null && or.equals(crit.getOr()));

            return isEquiv;
        }

        public String[] getAllTables()
        {
            StringStackBuffer tables = new StringStackBuffer();
            addCriterionTable(this, tables);
            return tables.toStringArray();
        }

        private void addCriterionTable(Criterion c, StringStackBuffer s)
        {
            if ( c != null )
            {
                s.add(c.getTable());
                addCriterionTable(c.getAnd(), s);
                addCriterionTable(c.getOr(), s);
            }
        }

        public Criterion[] getAttachedCriterion()
        {
            ArrayList crits = new ArrayList();
            traverseCriterion(this, crits);
            Criterion[]  crita = new Criterion[crits.size()];
            for ( int i=0; i<crits.size(); i++ )
            {
                crita[i] = (Criterion)crits.get(i);
            }

            return crita;
        }

        private void traverseCriterion(Criterion c, ArrayList a)
        {
            if ( c != null )
            {
                a.add(c);
                traverseCriterion(c.getAnd(), a);
                traverseCriterion(c.getOr(), a);
            }
        }
    }
}
