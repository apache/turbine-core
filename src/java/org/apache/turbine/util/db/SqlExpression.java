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

import java.lang.reflect.Array;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.sql.Timestamp;

import org.apache.turbine.om.DateKey;
import org.apache.turbine.om.ObjectKey;
import org.apache.turbine.om.StringKey;

import org.apache.turbine.util.StringStackBuffer;
import org.apache.turbine.util.db.adapter.DB;

/**
 * This class represents a part of an SQL query found in the <code>WHERE</code>
 * section.  For example:
 * <pre>
 * table_a.column_a = table_b.column_a
 * column LIKE 'F%'
 * table.column < 3
 * </pre>
 * This class is used primarily by {@link org.apache.turbine.om.peer.BasePeer}.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
public class SqlExpression
{
    private static final String LIKE = " LIKE ";
    private static final char SINGLE_QUOTE = '\'';
    private static final char BACKSLASH = '\\';

    /**
     * Used to specify a join on two columns.
     *
     * @param column A column in one of the tables to be joined.
     * @param relatedColumn The column in the other table to be
     * joined.
     * @return A join expression, e.g. UPPER(table_a.column_a) =
     * UPPER(table_b.column_b).
     */
    public static String buildInnerJoin( String column,
                                         String relatedColumn )
    {
        // 'db' can be null because 'ignoreCase' is null.
        return buildInnerJoin( column, relatedColumn, false, null );
    }

    /**
     * Used to specify a join on two columns.
     *
     * @param column A column in one of the tables to be joined.
     * @param relatedColumn The column in the other table to be
     * joined.
     * @param ignoreCase If true and columns represent Strings, the
     * appropriate function defined for the database will be used to
     * ignore differences in case.
     * @param db Represents the database in use for vendor-specific
     * functions.
     * @return A join expression, e.g. UPPER(table_a.column_a) =
     * UPPER(table_b.column_b).
     */
    public static String buildInnerJoin( String column,
                                         String relatedColumn,
                                         boolean ignoreCase,
                                         DB db )
    {
        int addlength = (ignoreCase) ? 25 : 1;
        StringBuffer sb = new StringBuffer(column.length() + 
            relatedColumn.length() + addlength );
        buildInnerJoin(column, relatedColumn, ignoreCase, db, sb);
        return sb.toString();
    }

    /**
     * Used to specify a join on two columns.
     *
     * @param column A column in one of the tables to be joined.
     * @param relatedColumn The column in the other table to be
     * joined.
     * @param ignoreCase If true and columns represent Strings, the
     * appropriate function defined for the database will be used to
     * ignore differences in case.
     * @param db Represents the database in use for vendor-specific
     * functions.
     * @param whereClause A StringBuffer to which the sql expression
     * will be appended.
     * @return A join expression, e.g. UPPER(table_a.column_a) =
     * UPPER(table_b.column_b).
     */
    public static void buildInnerJoin( String column,
                                       String relatedColumn,
                                       boolean ignoreCase,
                                       DB db,
                                       StringBuffer whereClause)
    {
        if (ignoreCase)
        {
            whereClause
                .append(db.ignoreCase(column))
                .append('=')
                .append(db.ignoreCase(relatedColumn));
        }
        else
        {
            whereClause
                .append(column)
                .append('=')
                .append( relatedColumn );
        }
    }


    /**
     * Builds a simple SQL expression.
     *
     * @param columnName A column.
     * @param criteria The value to compare the column against.
     * @param comparison One of =, &lt;, &gt;, ^lt;=, &gt;=, &lt;&gt;,
     * !=, LIKE, etc.
     * @return A simple SQL expression, e.g. UPPER(table_a.column_a)
     * LIKE UPPER('ab%c').
     * @exception Exception, a generic exception.
     */
    public static String build( String columnName,
                                Object criteria,
                                String comparison )
        throws Exception
    {
        // 'db' can be null because 'ignoreCase' is null
        return build( columnName, criteria, comparison, false, null );
    }

    /**
     * Builds a simple SQL expression.
     *
     * @param columnName A column.
     * @param criteria The value to compare the column against.
     * @param comparison One of =, &lt;, &gt;, ^lt;=, &gt;=, &lt;&gt;,
     * !=, LIKE, etc.
     * @param ignoreCase If true and columns represent Strings, the
     * appropriate function defined for the database will be used to
     * ignore differences in case.
     * @param db Represents the database in use, for vendor specific
     * functions.
     * @return A simple sql expression, e.g. UPPER(table_a.column_a)
     * LIKE UPPER('ab%c').
     * @exception Exception, a generic exception.
     */
    public static String build( String columnName,
                                Object criteria,
                                String comparison,
                                boolean ignoreCase,
                                DB db )
        throws Exception
    {
        int addlength = (ignoreCase ? 40 : 20);
        StringBuffer sb = new StringBuffer(columnName.length() + addlength );
        build(columnName, criteria, comparison, ignoreCase, db, sb);
        return sb.toString();
    }

    /**
     * Builds a simple SQL expression.
     *
     * @param columnName A column.
     * @param criteria The value to compare the column against.
     * @param comparison One of =, &lt;, &gt;, ^lt;=, &gt;=, &lt;&gt;,
     * !=, LIKE, etc.
     * @param ignoreCase If true and columns represent Strings, the
     * appropriate function defined for the database will be used to
     * ignore differences in case.
     * @param db Represents the database in use, for vendor specific
     * functions.
     * @param whereClause A StringBuffer to which the sql expression
     * will be appended.
     * @return A simple sql expression, e.g. UPPER(table_a.column_a)
     * LIKE UPPER('ab%c').
     * @exception Exception, a generic exception.
     */
    public static void build( String columnName,
                              Object criteria,
                              String comparison,
                              boolean ignoreCase,
                              DB db, 
                              StringBuffer whereClause)
    {
        // Allow null criteria
        // This will result in queries like
        // insert into table (name, parent) values ('x', null);
        //
        
        /* Check to see if the criteria is an ObjectKey
         * and if the value of that ObjectKey is null.
         * In that case, criteria should be null.
         */
        
        if (criteria != null && criteria instanceof ObjectKey)
        {
            if (((ObjectKey)criteria).getValue() == null)
            {
                criteria = null;
            }
        }
        /*  If the criteria is null, check to see comparison
         *  is an =, <>, or !=.  If so, replace the comparison
         *  with the proper IS or IS NOT.
         */
        
        if (criteria == null)
        {
            criteria = "null";
            if (comparison.equals(Criteria.EQUAL))
            {
                comparison = Criteria.ISNULL;
            }
            else if (comparison.equals(Criteria.NOT_EQUAL))
            {
                comparison = Criteria.ISNOTNULL;
            }
            else if (comparison.equals(Criteria.ALT_NOT_EQUAL))
            {
                comparison = Criteria.ISNOTNULL;
            }
        }
        else 
        {
           if (criteria instanceof String ||
               criteria instanceof StringKey)
           {
               criteria = quoteAndEscapeText(criteria.toString(), db);
           }
           else if( criteria instanceof java.util.Date ||
                    criteria instanceof DateKey)
           {
               Date dt = criteria instanceof Date?(Date) criteria:((DateKey)criteria).getDate();
               criteria = "{ts '" + new Timestamp(dt.getTime()).toString() + "'}";
           }
           else if( criteria instanceof Boolean )
           {
               criteria = criteria.equals(Boolean.TRUE) ? "1" : "0";
           }
        }

        if ( comparison.equals(Criteria.LIKE) )
        {
            buildLike( columnName, (String)criteria, 
                       ignoreCase, db, whereClause);
        }
        else if ( comparison.equals(Criteria.IN) ||
                  comparison.equals(Criteria.NOT_IN) )
        {
            buildIn( columnName, criteria, comparison, 
                     ignoreCase, db, whereClause);
        }
        else
        {
          // Do not put the upper/lower keyword around IS NULL
          //  or IS NOT NULL
          if ( comparison.equals(Criteria.ISNULL) ||
               comparison.equals(Criteria.ISNOTNULL))
          {
            whereClause.append(columnName)
            .append(comparison);
          }
          else
          {
            String columnValue = criteria.toString();
            if (ignoreCase && db != null)
            {
                columnName = db.ignoreCase(columnName);
                columnValue = db.ignoreCase(columnValue);
            }
            whereClause.append(columnName)
                .append(comparison)
                .append(columnValue);
           }
        }
    }

    /**
     * Takes a columnName and criteria and builds an SQL phrase based
     * on whether wildcards are present and the state of the
     * ignoreCase flag.  Multicharacter wildcards % and * may be used
     * as well as single character wildcards, _ and ?.  These
     * characters can be escaped with \.
     *
     * e.g. criteria = "fre%" -> columnName LIKE 'fre%'
     *                        -> UPPER(columnName) LIKE UPPER('fre%')
     *      criteria = "50\%" -> columnName = '50%'
     *
     * @param columnName A column.
     * @param criteria The value to compare the column against.
     * @param ignoreCase If true and columns represent Strings, the
     * appropriate function defined for the database will be used to
     * ignore differences in case.
     * @param db Represents the database in use, for vendor specific
     * functions.
     * @return An SQL expression.
     */
    static String buildLike( String columnName,
                             String criteria,
                             boolean ignoreCase,
                             DB db )
    {
        StringBuffer whereClause = new StringBuffer();
        buildLike( columnName, criteria, ignoreCase, db, whereClause );
        return whereClause.toString();
    }

    /**
     * Takes a columnName and criteria and builds an SQL phrase based
     * on whether wildcards are present and the state of the
     * ignoreCase flag.  Multicharacter wildcards % and * may be used
     * as well as single character wildcards, _ and ?.  These
     * characters can be escaped with \.
     *
     * e.g. criteria = "fre%" -> columnName LIKE 'fre%'
     *                        -> UPPER(columnName) LIKE UPPER('fre%')
     *      criteria = "50\%" -> columnName = '50%'
     *
     * @param columnName A column name.
     * @param criteria The value to compare the column against.
     * @param ignoreCase If true and columns represent Strings, the
     * appropriate function defined for the database will be used to
     * ignore differences in case.
     * @param db Represents the database in use, for vendor specific
     * functions.
     * @param whereClause A StringBuffer to which the sql expression
     * will be appended.
     */
    static void buildLike( String columnName,
                           String criteria,
                           boolean ignoreCase,
                           DB db,
                           StringBuffer whereClause )
    {
        // If selection is case insensitive use SQL UPPER() function
        // on column name.
        if (ignoreCase)
        {
            columnName = db.ignoreCase(columnName);
        }
        whereClause.append(columnName);

        // If selection criteria contains wildcards use LIKE otherwise
        // use = (equals).  Wildcards can be escaped by prepending
        // them with \ (backslash).
        String equalsOrLike = " = ";
        String parsedCriteria = null;
        int position = 0;
        StringBuffer sb = new StringBuffer();
        while ( position < criteria.length() )
        {
            char checkWildcard = criteria.charAt(position);
            
            switch (checkWildcard)
            {
            case BACKSLASH:
                // Determine whether to skip over next character.
                switch (criteria.charAt(position + 1))
                {
                case '%':
                case '_':
                case '*':
                case '?':
                case BACKSLASH:
                    position++;
                    break;
                }
                break;
            case '%':
            case '_':
                equalsOrLike = LIKE;
                break;
            case '*':
                equalsOrLike = LIKE;
                checkWildcard = '%';
                break;
            case '?':
                equalsOrLike = LIKE;
                checkWildcard = '_';
                break;
            }

            sb.append( checkWildcard );
            position++;
        }
        whereClause.append(equalsOrLike);

        // If selection is case insensitive use SQL UPPER() function
        // on criteria.
        String clauseItem = sb.toString();
        if (ignoreCase)
        {
            clauseItem = db.ignoreCase(clauseItem);
        }
        whereClause.append(clauseItem);
    }

    /**
     * Takes a columnName and criteria (which must be an array) and
     * builds a SQL 'IN' expression taking into account the ignoreCase
     * flag.
     *
     * @param columnName A column.
     * @param criteria The value to compare the column against.
     * @param comparison Either " IN " or " NOT IN ".
     * @param ignoreCase If true and columns represent Strings, the
     * appropriate function defined for the database will be used to
     * ignore differences in case.
     * @param db Represents the database in use, for vendor specific
     * functions.
     * @return An SQL expression.
     */
    static String buildIn(String columnName,
                          Object criteria,
                          String comparison,
                          boolean ignoreCase,
                          DB db)
    {
        StringBuffer whereClause = new StringBuffer();
        buildIn(columnName, criteria, comparison,
                ignoreCase, db, whereClause);
        return whereClause.toString();
    }

    /**
     * Takes a columnName and criteria (which must be an array) and
     * builds a SQL 'IN' expression taking into account the ignoreCase
     * flag.
     *
     * @param columnName A column.
     * @param criteria The value to compare the column against.
     * @param comparison Either " IN " or " NOT IN ".
     * @param ignoreCase If true and columns represent Strings, the
     * appropriate function defined for the database will be used to
     * ignore differences in case.
     * @param db Represents the database in use, for vendor specific
     * functions.
     * @param whereClause A StringBuffer to which the sql expression
     * will be appended.
     */
    static void buildIn(String columnName,
                        Object criteria,
                        String comparison,
                        boolean ignoreCase,
                        DB db,
                        StringBuffer whereClause)
    {
        if (ignoreCase)
        {
            whereClause.append(db.ignoreCase(columnName));
        }
        else
        {
            whereClause.append(columnName);
        }

        whereClause.append(comparison);
        StringStackBuffer inClause = new StringStackBuffer();
        if (criteria instanceof List)
        {
            Iterator iter = ((List)criteria).iterator();
            while (iter.hasNext())
            {
                Object value = iter.next();

                // The method processInValue() quotes the string
                // and/or wraps it in UPPER().
                inClause.add(processInValue(value, ignoreCase, db));
            }
        }
        else
        {
            // Assume array.
            for (int i = 0; i < Array.getLength(criteria); i++)
            {
                Object value = Array.get(criteria, i);

                // The method processInValue() quotes the string
                // and/or wraps it in UPPER().
                inClause.add(processInValue(value, ignoreCase, db));
            }
        }
        whereClause.append('(')
                   .append(inClause.toString(","))
                   .append(')');
    }

    /**
     * Creates an appropriate string for an 'IN' clause from an
     * object.  Adds quoting and/or UPPER() as appropriate.  This is
     * broken out into a seperate method as it is used in two places
     * in buildIn, depending on whether an array or Vector is being
     * looped over.
     *
     * @param value The value to process.
     * @param ignoreCase Coerce the value suitably for ignoring case.
     * @param db Represents the database in use for vendor specific
     * functions.
     * @return Processed value as String.
     */
    static String processInValue(Object value,
                                 boolean ignoreCase,
                                 DB db)
    {
        String ret = null;
        if (value instanceof String)
        {
            ret = quoteAndEscapeText((String)value, db);
        }
        else
        {
            ret = value.toString();
        }
        if (ignoreCase)
        {
            ret = db.ignoreCase(ret);
        }
        return ret;
    }

    /**
     * Quotes and escapes raw text for placement in a SQL expression.
     * For simplicity, the text is assumed to be neither quoted nor
     * escaped.
     *
     * @param rawText The <i>unquoted</i>, <i>unescaped</i> text to process.
     * @return Quoted and escaped text.
     */
    public static String quoteAndEscapeText(String rawText, DB db)
    {
        StringBuffer buf = new StringBuffer( (int)(rawText.length() * 1.1) );

        /* Some databases do not need escaping.  */
        String escapeString = new String();
        if (db != null && db.escapeText() == false)
        {
            escapeString = String.valueOf(BACKSLASH);
        }
        else
        {
            escapeString = String.valueOf(BACKSLASH) + String.valueOf(BACKSLASH);
        }

        char[] data = rawText.toCharArray();
        buf.append(SINGLE_QUOTE);
        for (int i = 0; i < data.length; i++)
        {
            switch (data[i])
            {
            case SINGLE_QUOTE:
                buf.append(SINGLE_QUOTE).append(SINGLE_QUOTE);
                break;
            case BACKSLASH:
                buf.append(escapeString);
                break;
            default:
                buf.append(data[i]);
            }
        }
        buf.append(SINGLE_QUOTE);

        return buf.toString();
    }

    /**
     *
     * @deprecated Use quoteAndEscapeText(String rawText, DB db) instead.
     * the quoteAndEscapeText rules depend on the database.
     */
    public static String quoteAndEscapeText(String rawText)
    {
        return quoteAndEscapeText(rawText, null);
    }
}
