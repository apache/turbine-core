package org.apache.turbine.torque.engine.database.model;

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

import org.xml.sax.AttributeList;

import java.util.ArrayList;
import java.util.List;

/**
 * A Class for information about indices of a table
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 * @deprecated use turbine-torque
 */
public class Index
{
    private String indexName;
    private Table parentTable;
    private List indexColumns = new ArrayList(3);
    private boolean isUnique;

    /**
     * Default Constructor
     */
    public Index()
    {
    }

    /**
     * Imports index from an XML specification
     */
    public void loadFromXML (AttributeList attrib)
    {
        indexName     = attrib.getValue("name");
        String unique = attrib.getValue("unique");
        isUnique = (unique != null && unique.equalsIgnoreCase("true"));
    }

    /**
     * Get unique attribute of the index
     *
     */
     public boolean getIsUnique()
     {
         return isUnique;
     }

    /**
     * Get the name of the index
     *
     */
    public String getIndexName()
    {
        return indexName;
    }

    /**
     * Set the name of the index
     */
    public void setIndexName(String indexName)
    {
        this.indexName = indexName;
    }

    /**
     * Set the parent Table of the index
     */
    public void setTable(Table parent)
    {
        parentTable = parent;
    }

    /**
     * Get the parent Table of the index
     */
    public Table getTable()
    {
        return parentTable;
    }

    /**
     * Returns the Name of the table the index is in
     */
    public String getTableName()
    {
        return parentTable.getName();
    }

    /**
     *  adds a new column to an index
     */
    public void addColumn(AttributeList attrib)
    {
        indexColumns.add(attrib.getValue("name"));
    }

    /**
     * Creates a list of columns delimited by commas
     */
    private String makeColumnList(List cols)
    {
        StringBuffer res = new StringBuffer(cols.get(0).toString());
        for (int i=1; i < cols.size(); i++)
            res.append(", ")
                .append(cols.get(i).toString());
        return res.toString();
    }

    /**
     * Return a comma delimited string of the index columns
     */
    public String getIndexColumnList()
    {
        return makeColumnList(indexColumns);
    }

    /**
     * Return the vector of local columns.  You should not edit
     * this vector.
     */
    public List getIndexColumns()
    {
        return indexColumns;
    }

    /**
     * String representation of the index. This
     * is an xml representation.
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append(" <index name=\"")
              .append(indexName)
              .append("\"");

        result.append(">\n");

        for (int i=0; i<indexColumns.size(); i++)
            result.append("  <index-column name=\"")
                .append(indexColumns.get(i))
                .append("\"/>\n");
        result.append(" </index>\n");
        return result.toString();
    }
}
