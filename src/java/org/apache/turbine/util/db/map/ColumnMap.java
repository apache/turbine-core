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

/**
 * ColumnMap is used to model a column of a table in a database.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @version $Id$
 */
public class ColumnMap
{
    /** Type of the column. */
    private Object type = null;

    /** Size of the column. */
    private int size = 0;

    /** Is it a primary key? */
    private boolean pk = false;

    /** Is null value allowed ?*/
    private boolean notNull = false;

    /** Name of the table that this column is related to. */
    private String relatedTableName = "";

    /** Name of the column that this column is related to. */
    private String relatedColumnName = "";

    /** The TableMap for this column. */
    private TableMap table;

    /** The name of the column. */
    private String columnName;


    /**
     * Constructor.
     *
     * @param name The name of the column.
     * @param containingTable TableMap of the table this column is in.
     */
    public ColumnMap( String name,
                      TableMap containingTable )
    {
        this.columnName = name;
        table = containingTable;
    }

    /**
     * Get the name of a column.
     *
     * @return A String with the column name.
     */
    public String getColumnName()
    {
        return columnName;
    }

    /**
     * Get the table name + column name.
     *
     * @return A String with the full column name.
     */
    public String getFullyQualifiedName()
    {
        return table.getName() + "." + columnName;
    }

    /**
     * Get the name of the table this column is in.
     *
     * @return A String with the table name.
     */
    public String getTableName()
    {
        return table.getName();
    }

    /**
     * Set the type of this column.
     *
     * @param type An Object specifying the type.
     */
    public void setType ( Object type )
    {
        this.type = type;
    }

    /**
     * Set the size of this column.
     *
     * @param size An int specifying the size.
     */
    public void setSize( int size )
    {
        this.size = size;
    }

    /**
     * Set if this column is a primary key or not.
     *
     * @param pk True if column is a primary key.
     */
    public void setPrimaryKey( boolean pk )
    {
        this.pk = pk;
    }

    /**
     * Set if this column may be null.
     *
     * @param nn True if column may be null.
     */
    public void setNotNull( boolean nn )
    {
        this.notNull = nn;
    }

    /**
     * Set the foreign key for this column.
     *
     * @param fullyQualifiedName The name of the table.column that is
     * foreign.
     */
    public void setForeignKey(String fullyQualifiedName )
    {
        if ( fullyQualifiedName != null &&
             fullyQualifiedName.length() > 0)
        {
            relatedTableName = fullyQualifiedName.substring( 0, fullyQualifiedName.indexOf('.') );
            relatedColumnName = fullyQualifiedName.substring( fullyQualifiedName.indexOf('.')+1 );
        }
        else
        {
            relatedTableName = "";
            relatedColumnName = "";
        }
    }

    /**
     * Set the foreign key for this column.
     *
     * @param tableName The name of the table that is foreign.
     * @param columnName The name of the column that is foreign.
     */
    public void setForeignKey(String tableName,
                              String columnName)
    {
        if ( tableName != null &&
             tableName.length() > 0 &&
             columnName != null &&
             columnName.length() > 0 )
        {
            relatedTableName = tableName;
            relatedColumnName = columnName;
        }
        else
        {
            relatedTableName = "";
            relatedColumnName = "";
        }
    }

    /**
     * Get the type of this column.
     *
     * @return An Object specifying the type.
     */
    public Object getType()
    {
        return type;
    }

    /**
     * Get the size of this column.
     *
     * @return An int specifying the size.
     */
    public int getSize()
    {
        return size;
    }

    /**
     * Is this column a primary key?
     *
     * @return True if column is a primary key.
     */
    public boolean isPrimaryKey()
    {
        return pk;
    }

    /**
     * Is null value allowed ?
     *
     * @return True if column may be null.
     */
    public boolean isNotNull()
    {
        return (notNull || isPrimaryKey());
    }

    /**
     * Is this column a foreign key?
     *
     * @return True if column is a foreign key.
     */
    public boolean isForeignKey()
    {
        if ( relatedTableName != null && relatedTableName.length() > 0)
            return true;
        else
            return false;
    }

    /**
     * Get the table.column that this column is related to.
     *
     * @return A String with the full name for the related column.
     */
    public String getRelatedName()
    {
        return relatedTableName+"."+relatedColumnName;
    }

    /**
     * Get the table name that this column is related to.
     *
     * @return A String with the name for the related table.
     */
    public String getRelatedTableName()
    {
        return relatedTableName;
    }

    /**
     * Get the column name that this column is related to.
     *
     * @return A String with the name for the related column.
     */
    public String getRelatedColumnName()
    {
        return relatedColumnName;
    }
}
