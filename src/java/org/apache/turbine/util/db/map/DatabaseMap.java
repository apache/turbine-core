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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import org.apache.turbine.util.db.IDBroker;
import org.apache.turbine.util.db.IdGenerator;

/**
 * DatabaseMap is used to model a database.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
public class DatabaseMap
{
    /** Name of the database. */
    private String name;

    /** Name of the tables in the database. */
    private Hashtable tables;

    /**
     * A special table used to generate primary keys for the other
     * tables.
     */
    private TableMap idTable = null;

    /** The IDBroker that goes with the idTable. */
    private IDBroker idBroker = null;

    /** The IdGenerators, keyed by type of idMethod. */
    private HashMap idGenerators;

    /**
     * Constructor.
     *
     * @param name Name of the database.
     * @param numberOfTables Number of tables in the database.
     */
    public DatabaseMap(String name, int numberOfTables)
    {
        this.name = name;
        tables = new Hashtable( (int)(1.25*numberOfTables) + 1 );
        idGenerators = new HashMap(6);
    }

    /**
     * Constructor.
     *
     * @param name Name of the database.
     */
    public DatabaseMap(String name)
    {
        this.name = name;
        tables = new Hashtable();
        idGenerators = new HashMap(6);
    }

    /**
     * Does this database contain this specific table?
     *
     * @param table The TableMap representation of the table.
     * @return True if the database contains the table.
     */
    public boolean containsTable( TableMap table )
    {
        return containsTable( table.getName() );
    }

    /**
     * Does this database contain this specific table?
     *
     * @param name The String representation of the table.
     * @return True if the database contains the table.
     */
    public boolean containsTable( String name )
    {
        if ( name.indexOf('.') > 0 )
        {
            name = name.substring( 0, name.indexOf('.') );
        }
        return tables.containsKey(name);
    }

    /**
     * Get the ID table for this database.
     *
     * @return A TableMap.
     */
    public TableMap getIdTable()
    {
        return idTable;
    }

    /**
     * Get the IDBroker for this database.
     *
     * @return An IDBroker.
     */
    public IDBroker getIDBroker()
    {
        return idBroker;
    }

    /**
     * Get the name of this database.
     *
     * @return A String.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get a TableMap for the table by name.
     *
     * @param name Name of the table.
     * @return A TableMap, null if the table was not found.
     */
    public TableMap getTable( String name )
    {
        return (TableMap) tables.get(name);
    }

    /**
     * Get a TableMap[] of all of the tables in the database.
     *
     * @return A TableMap[].
     */
    public TableMap[] getTables()
    {
        TableMap[] dbTables = new TableMap[tables.size()];
        Enumeration e = tables.elements();
        int i = 0;
        while (e.hasMoreElements())
        {
            dbTables[i++] = (TableMap) e.nextElement() ;
        }
        return dbTables;
    }

    /**
     * Add a new table to the database by name.  It creates an empty
     * TableMap that you need to populate.
     *
     * @param tableName The name of the table.
     */
    public void addTable( String tableName )
    {
        TableMap tmap = new TableMap( tableName, this );
        tables.put( tableName, tmap );
    }

    /**
     * Add a new table to the database by name.  It creates an empty
     * TableMap that you need to populate.
     *
     * @param tableName The name of the table.
     * @param numberOfColumns The number of columns in the table.
     */
    public void addTable( String tableName,
                          int numberOfColumns )
    {
        TableMap tmap = new TableMap( tableName, numberOfColumns, this );
        tables.put( tableName, tmap );
    }

    /**
     * Add a new TableMap to the database.
     *
     * @param map The TableMap representation.
     */
    public void addTable( TableMap map )
    {
        tables.put( map.getName(), map );
    }


    /**
     * Set the ID table for this database.
     *
     * @param idTable The TableMap representation for the ID table.
     */
    public void setIdTable( TableMap idTable )
    {
        this.idTable = idTable;
        addTable(idTable);
        idBroker = new IDBroker(idTable);
        addIdGenerator(TableMap.ID_BROKER, idBroker);
    }

    /**
     * Set the ID table for this database.
     *
     * @param tableName The name for the ID table.
     */
    public void setIdTable( String tableName )
    {
        TableMap tmap = new TableMap( tableName, this );
        setIdTable( tmap );
    }

    /**
     * Add a type of id generator for access by a TableMap.
     *
     * @param type a <code>String</code> value
     * @param idGen an <code>IdGenerator</code> value
     */
    public void addIdGenerator(String type, IdGenerator idGen)
    {
        idGenerators.put(type, idGen);
    }

    /**
     * Get a type of id generator.  Valid values are listed in the
     * {@see IDMethod} interface.
     *
     * @param type a <code>String</code> value
     * @return an <code>IdGenerator</code> value
     */
    IdGenerator getIdGenerator(String type)
    {
        return (IdGenerator)idGenerators.get(type);
    }
}
