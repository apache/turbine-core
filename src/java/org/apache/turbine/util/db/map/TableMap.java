package org.apache.turbine.util.db.map;

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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.turbine.util.db.IdGenerator;

/**
 * TableMap is used to model a table in a database.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
 public class TableMap implements IDMethod
 {
    /**
     * The list of valid ID generation methods.
     */
    protected static final String[] VALID_ID_METHODS =
    {
         NATIVE, AUTO_INCREMENT, SEQUENCE, ID_BROKER, NO_ID_METHOD
    };

    /** The columns in the table. */
    private Hashtable columns;

    /** The database this table belongs to. */
    private DatabaseMap dbMap;

    /** The name of the table. */
    private String tableName;

    /** The prefix on the table name. */
    private String prefix;

    /** The primary key generation method. */
    private String primaryKeyMethod = NO_ID_METHOD;

    /** IdGenerator for this tableMap */
    private IdGenerator idGenerator;

    /**
     * Object to store information that is needed if the
     * for generating primary keys.
     */
    private Object pkInfo = null;

    /**
     * Constructor.
     *
     * @param tableName The name of the table.
     * @param numberOfColumns The number of columns in the table.
     * @param containingDB A DatabaseMap that this table belongs to.
     */
    public TableMap(String tableName,
                    int numberOfColumns,
                    DatabaseMap containingDB )
    {
        this.tableName = tableName;
        dbMap = containingDB;
        columns = new Hashtable( (int)(1.25*numberOfColumns) + 1 );
    }

    /**
     * Constructor.
     *
     * @param tableName The name of the table.
     * @param containingDB A DatabaseMap that this table belongs to.
     */
    public TableMap(String tableName,
                    DatabaseMap containingDB )
    {
        this.tableName = tableName;
        dbMap = containingDB;
        columns = new Hashtable( 20 );
    }

    /**
     * Constructor.
     *
     * @param tableName The name of the table.
     * @param prefix The prefix for the table name (ie: SCARAB for
     * SCARAB_PROJECT).
     * @param containingDB A DatabaseMap that this table belongs to.
     */
    public TableMap(String tableName,
                    String prefix,
                    DatabaseMap containingDB )
    {
        this.tableName = tableName;
        this.prefix = prefix;
        dbMap = containingDB;
        columns = new Hashtable( 20 );
    }

    /**
     * Does this table contain the specified column?
     *
     * @param column A ColumnMap.
     * @return True if the table contains the column.
     */
    public boolean containsColumn( ColumnMap column )
    {
        return containsColumn( column.getColumnName() );
    }

    /**
     * Does this table contain the specified column?
     *
     * @param name A String with the name of the column.
     * @return True if the table contains the column.
     */
    public boolean containsColumn( String name )
    {
        if ( name.indexOf('.') > 0 )
        {
            name = name.substring( name.indexOf('.')+1 );
        }
        return columns.containsKey(name);
    }

    /**
     * Get the DatabaseMap containing this TableMap.
     *
     * @return A DatabaseMap.
     */
    public DatabaseMap getDatabaseMap()
    {
        return dbMap;
    }

    /**
     * Returns true if this tableMap contains a column with object
     * data.  If the type of the column is not a string, a number or a
     * date, it is assumed that it is object data.
     *
     * @return True if map contains a column with object data.
     */
    public boolean containsObjectColumn()
    {
        Enumeration e = columns.elements();
        while (e.hasMoreElements())
        {
            Object theType = ((ColumnMap)e.nextElement()).getType();
            if (! ( theType instanceof String ||
                    theType instanceof Number ||
                    theType instanceof java.util.Date ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the name of the Table.
     *
     * @return A String with the name of the table.
     */
    public String getName()
    {
        return tableName;
    }

    /**
     * Get table prefix name.
     *
     * @return A String with the prefix.
     */
    public String getPrefix()
    {
        return this.prefix;
    }

    /**
     * Set table prefix name.
     *
     * @param prefix The prefix for the table name (ie: SCARAB for
     * SCARAB_PROJECT).
     */
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    /**
     * Get the method used to generate primary keys for this table.
     *
     * @return A String with the method.
     */
    public String getPrimaryKeyMethod()
    {
        return primaryKeyMethod;
    }

    /**
     * Get the value of idGenerator.
     * @return value of idGenerator.
     */
    public IdGenerator getIdGenerator()
    {
        return getDatabaseMap().getIdGenerator(primaryKeyMethod);
    }

    /**
     * Get the information used to generate a primary key
     *
     * @return An Object.
     */
    public Object getPrimaryKeyMethodInfo()
    {
        return pkInfo;
    }

    /**
     * Get the information used to generate a primary key using a
     * sequence.
     *
     * @deprecated.  Use getPrimaryKeyMethodInfo
     */
    public Object getSequenceInfo()
    {
        return getPrimaryKeyMethodInfo();
    }

    /**
     * Get a ColumnMap[] of the columns in this table.
     *
     * @return A ColumnMap[].
     */
    public ColumnMap[] getColumns()
    {
        ColumnMap[] tableColumns = new ColumnMap[columns.size()];
        Enumeration e = columns.elements();
        int i = 0;
        while (e.hasMoreElements())
        {
            tableColumns[i++] = (ColumnMap) e.nextElement();
        }
        return tableColumns;
    }

    /**
     * Get a ColumnMap for the named table.
     *
     * @param name A String with the name of the table.
     * @return A ColumnMap.
     */
    public ColumnMap getColumn( String name )
    {
        try
        {
            return (ColumnMap) columns.get( name );
        }
        catch (Exception e)
        {
        }
        return null;
    }

    /**
     * Add a pre-created column to this table.  It will replace any
     * existing column.
     *
     * @param cmap A ColumnMap.
     */
    public void addColumn ( ColumnMap cmap )
    {
        columns.put ( cmap.getColumnName(), cmap );
    }

    /**
     * Add a column to this table of a certain type.
     *
     * @param columnName A String with the column name.
     * @param type An Object specifying the type.
     */
    public void addColumn( String columnName,
                           Object type )
    {
        addColumn( columnName, type, false, null, null, 0 );
    }

    /**
     * Add a column to this table of a certain type and size.
     *
     * @param columnName A String with the column name.
     * @param type An Object specifying the type.
     * @param size An int specifying the size.
     */
    public void addColumn( String columnName,
                           Object type,
                           int size )
    {
        addColumn( columnName, type, false, null, null, size );
    }

    /**
     * Add a primary key column to this Table.
     *
     * @param columnName A String with the column name.
     * @param type An Object specifying the type.
     */
    public void addPrimaryKey( String columnName,
                               Object type )
    {
        addColumn( columnName, type, true, null, null, 0 );
    }

    /**
     * Add a primary key column to this Table.
     *
     * @param columnName A String with the column name.
     * @param type An Object specifying the type.
     * @param size An int specifying the size.
     */
    public void addPrimaryKey( String columnName,
                               Object type,
                               int size )
    {
        addColumn( columnName, type, true, null, null, size );
    }

    /**
     * Add a foreign key column to the table.
     *
     * @param columnName A String with the column name.
     * @param type An Object specifying the type.
     * @param fkTable A String with the foreign key table name.
     * @param fkColumn A String with the foreign key column name.
     */
    public void addForeignKey( String columnName,
                               Object type,
                               String fkTable,
                               String fkColumn )
    {
        addColumn( columnName, type, false, fkTable, fkColumn, 0 );
    }

    /**
     * Add a foreign key column to the table.
     *
     * @param columnName A String with the column name.
     * @param type An Object specifying the type.
     * @param fkTable A String with the foreign key table name.
     * @param fkColumn A String with the foreign key column name.
     * @param size An int specifying the size.
     */
    public void addForeignKey( String columnName,
                               Object type,
                               String fkTable,
                               String fkColumn,
                               int size )
    {
        addColumn( columnName, type, false, fkTable, fkColumn, size );
    }

    /**
     * Add a foreign primary key column to the table.
     *
     * @param columnName A String with the column name.
     * @param type An Object specifying the type.
     * @param fkTable A String with the foreign key table name.
     * @param fkColumn A String with the foreign key column name.
     */
    public void addForeignPrimaryKey( String columnName,
                                      Object type,
                                      String fkTable,
                                      String fkColumn )
    {
        addColumn( columnName, type, true, fkTable, fkColumn, 0 );
    }

    /**
     * Add a foreign primary key column to the table.
     *
     * @param columnName A String with the column name.
     * @param type An Object specifying the type.
     * @param fkTable A String with the foreign key table name.
     * @param fkColumn A String with the foreign key column name.
     * @param size An int specifying the size.
     */
    public void addForeignPrimaryKey( String columnName,
                                      Object type,
                                      String fkTable,
                                      String fkColumn,
                                      int size )
    {
        addColumn( columnName, type, true, fkTable, fkColumn, size );
    }

    /**
     * Add a column to the table.
     *
     * @param name A String with the column name.
     * @param type An Object specifying the type.
     * @param pk True if column is a primary key.
     * @param fkTable A String with the foreign key table name.
     * @param fkColumn A String with the foreign key column name.
     * @param size An int specifying the size.
     */
    private void addColumn( String name,
                            Object type,
                            boolean pk,
                            String fkTable,
                            String fkColumn,
                            int size )
    {
        // If the tablename is prefixed with the name of the column,
        // remove it ie: SCARAB_PROJECT.PROJECT_ID remove the
        // SCARAB_PROJECT.
        if (name.indexOf('.') > 0 && name.indexOf(getName()) != -1)
        {
            name = name.substring(getName().length()+1);
        }
        if ( fkTable != null &&
             fkTable.length() > 0 &&
             fkColumn !=null &&
             fkColumn.length() > 0 )
        {
            if (fkColumn.indexOf('.') > 0 &&
                fkColumn.indexOf(fkTable) != -1)
            {
                fkColumn = fkColumn.substring(fkTable.length()+1);
            }
        }
        ColumnMap col = new ColumnMap(name, this);
        col.setType(type);
        col.setPrimaryKey(pk);
        col.setForeignKey(fkTable, fkColumn);
        col.setSize(size);
        columns.put( name, col );
    }

    /**
     * Sets the method used to generate a key for this table.  Valid
     * values are as specified in the {@see IDMethod} interface.
     *
     * @param method The ID generation method type name.
     */
    public void setPrimaryKeyMethod(String method)
    {
        // Validate ID generation method.
        for (int i = 0; i < VALID_ID_METHODS.length; i++)
        {
            if (VALID_ID_METHODS[i].equals(method))
            {
                primaryKeyMethod = method;
                break;
            }
        }

        // Default to no ID generation method.
        if (primaryKeyMethod != method)
        {
            primaryKeyMethod = NO_ID_METHOD;
        }
    }

    /**
     * Sets the sequence information needed to generate a key
     *
     * @deprecated.  Use setPrimaryKeyMethodInfo
     */
    public void setSequenceInfo(Object pkInfo)
    {
        setPrimaryKeyMethodInfo(pkInfo);
    }

    /**
     * Sets the pk information needed to generate a key
     *
     * @param. pkInfo information needed to generate a key
     */
    public void setPrimaryKeyMethodInfo(Object pkInfo)
    {
        this.pkInfo = pkInfo;
    }


    //---Utility methods for doing intelligent lookup of table names


    /**
     * Tell me if i have PREFIX in my string.
     *
     * @param data A String.
     * @return True if prefix is contained in data.
     */
    private final boolean hasPrefix ( String data )
    {
        return ( data.indexOf(getPrefix()) != -1 );
    }

    /**
     * Removes the PREFIX.
     *
     * @param data A String.
     * @return A String with data, but with prefix removed.
     */
    private final String removePrefix ( String data )
    {
        return data.substring(getPrefix().length());
    }

    /**
     * Removes the PREFIX, removes the underscores and makes
     * first letter caps.
     *
     * SCARAB_FOO_BAR becomes FooBar.
     *
     * @param data A String.
     * @return A String with data processed.
     */
    public final String removeUnderScores (String data)
    {
        String tmp = null;
        StringBuffer out = new StringBuffer();
        if (hasPrefix(data))
        {
            tmp = removePrefix(data);
        }
        else
        {
            tmp = data;
        }

        StringTokenizer st = new StringTokenizer(tmp, "_");
        while (st.hasMoreTokens())
        {
            String element = (String) st.nextElement();
            out.append ( firstLetterCaps(element));
        }
        return out.toString();
    }

    /**
     * Makes the first letter caps and the rest lowercase.
     *
     * @param data A String.
     * @return A String with data processed.
     */
    private final String firstLetterCaps ( String data )
    {
        String firstLetter = data.substring(0,1).toUpperCase();
        String restLetters = data.substring(1).toLowerCase();
        return firstLetter + restLetters;
    }
}
