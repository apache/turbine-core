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

import org.apache.turbine.util.StringStackBuffer;

/**
 * Used to assemble an SQL SELECT query.  Attributes exist for the
 * sections of a SELECT: modifiers, columns, from clause, where
 * clause, and order by clause.  The various parts of the query are
 * appended to buffers which only accept unique entries.  This class
 * is used primarily by BasePeer.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @version $Id$
 */
public class Query
{
    private static final String SELECT = "SELECT ";
    private static final String FROM = " FROM ";
    private static final String WHERE = " WHERE ";
    private static final String AND = " AND ";
    private static final String OR = " OR ";
    private static final String ORDER_BY = " ORDER BY ";
    private static final String IN = " IN ";
    private static final String BETWEEN = " BETWEEN ";
    private static final String LIMIT = " LIMIT ";
    private static final String ROWCOUNT = " SET ROWCOUNT ";
    private static final String TOP = " TOP ";


    private StringStackBuffer selectModifiers = new StringStackBuffer();
    private StringStackBuffer selectColumns = new StringStackBuffer();
    private StringStackBuffer fromTables = new StringStackBuffer();
    private StringStackBuffer whereCriteria = new StringStackBuffer();
    private StringStackBuffer orderByColumns = new StringStackBuffer();
    private String limit;
    private String rowcount;

    public String getTop()
    {
        return top;
    }

    public void setTop(String top)
    {
        this.top = top;
    }

    private String top;

    /**
     * Retrieve the modifier buffer in order to add modifiers to this
     * query.  E.g. DISTINCT and ALL.
     *
     * @return A StringStackBuffer used to add modifiers.
     */
    public StringStackBuffer getSelectModifiers()
    {
        return selectModifiers;
    }

    /**
     * Retrieve the columns buffer in order to specify which columns
     * are returned in this query.
     *
     *
     * @return A StringStackBuffer used to add columns to be selected.
     */
    public StringStackBuffer getSelectClause()
    {
        return selectColumns;
    }

    /**
     * Retrieve the from buffer in order to specify which tables are
     * involved in this query.
     *
     *
     * @return A StringStackBuffer used to add tables involved in the
     * query.
     */
    public StringStackBuffer getFromClause()
    {
        return fromTables;
    }

    /**
     * Retrieve the where buffer in order to specify the selection
     * criteria E.g. column_a=3.  Expressions added to the buffer will
     * be separated using AND.
     *
     * @return A StringStackBuffer used to add selection criteria.
     */
    public StringStackBuffer getWhereClause()
    {
        return whereCriteria;
    }

    /**
     * Retrieve the order by columns buffer in order to specify which
     * columns are used to sort the results of the query.
     *
     * @return A StringStackBuffer used to add columns to sort on.
     */
    public StringStackBuffer getOrderByClause()
    {
        return orderByColumns;
    }

    /**
     * Set the limit number.  This is used to limit the number of rows
     * returned by a query, and the row where the resultset starts.
     *
     * @param limit A String.
     */
    public void setLimit(String limit)
    {
        this.limit = limit;
    }

    /**
     * Set the rowcount number.  This is used to limit the number of
     * rows returned by Sybase and MS SQL/Server.
     *
     * @param rowcount A String.
     */
    public void setRowcount(String rowcount)
    {
        this.rowcount = rowcount;
    }

    /**
     * Get the limit number.  This is used to limit the number of
     * returned by a query in Postgres.
     *
     * @return A String with the limit.
     */
    public String getLimit()
    {
        return limit;
    }

    /**
     * Get the rowcount number.  This is used to limit the number of
     * returned by a query in Sybase and MS SQL/Server.
     *
     * @return A String with the row count.
     */
    public String getRowcount()
    {
        return rowcount;
    }

    /**
     * Outputs the query statement.
     *
     * @return A String with the query statement.
     */
    public String toString()
    {
        StringBuffer stmt = new StringBuffer();
        if ( rowcount != null )
            stmt.append(ROWCOUNT)
                .append(rowcount)
                .append(" ");
        stmt.append(SELECT)
            .append(selectModifiers.toString(" "));
        if ( top != null )
            stmt.append(TOP)
                .append(top)
                .append(" ");
        stmt.append(selectColumns.toString(", "))
            .append(FROM)
            .append(fromTables.toString(", "));
        if ( !whereCriteria.empty() )
            stmt.append(WHERE)
                .append(whereCriteria.toString( AND ));
        if ( !orderByColumns.empty() )
            stmt.append(ORDER_BY)
                .append(orderByColumns.toString(", "));
        if ( limit != null )
            stmt.append(LIMIT)
                .append(limit);
        if ( rowcount != null )
            stmt.append(ROWCOUNT)
                .append("0");
        return stmt.toString();
    }
}
