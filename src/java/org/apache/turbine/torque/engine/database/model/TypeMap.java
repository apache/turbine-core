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

import java.util.Hashtable;
import java.util.Date;
import java.sql.Types;

import java.math.BigDecimal;

// I don't know if the peer system deals
// with the recommended mappings.
//
//import java.sql.Date;
//import java.sql.Time;
//import java.sql.Timestamp;

/**
 * A class that maps JDBC types to their corresponding
 * Java object types, and Java native types. Used
 * by Column.java to perform object/native mappings.
 *
 * These are the official SQL type to Java type mappings.
 * These don't quite correspond to the way the peer
 * system works so we'll have to make some adjustments.
 *
 * -------------------------------------------------------
 * SQL Type      | Java Type            | Peer Type
 * -------------------------------------------------------
 * CHAR          | String               | String
 * VARCHAR       | String               | String
 * LONGVARCHAR   | String               | String
 * NUMERIC       | java.math.BigDecimal | java.math.BigDecimal
 * DECIMAL       | java.math.BigDecimal | java.math.BigDecimal
 * BIT           | boolean              | Boolean
 * TINYINT       | byte                 | Byte
 * SMALLINT      | short                | Short
 * INTEGER       | int                  | Integer
 * BIGINT        | long                 | Long
 * REAL          | float                | Float
 * FLOAT         | double               | Double
 * DOUBLE        | double               | Double
 * BINARY        | byte[]               | ?
 * VARBINARY     | byte[]               | ?
 * LONGVARBINARY | byte[]               | ?
 * DATE          | java.sql.Date        | java.util.Date
 * TIME          | java.sql.Time        | java.util.Date
 * TIMESTAMP     | java.sql.Timestamp   | java.util.Date
 *
 * -------------------------------------------------------
 * A couple variations have been introduced to cover cases 
 * that may arise, but are not covered above
 * BOOLEANCHAR   | boolean              | String
 * BOOLEANINT    | boolean              | Integer
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 */
public class TypeMap
{
    public static final String CHAR = "CHAR";
    public static final String VARCHAR = "VARCHAR";
    public static final String LONGVARCHAR = "LONGVARCHAR";
    public static final String CLOB = "CLOB";
    public static final String NUMERIC = "NUMERIC";
    public static final String DECIMAL = "DECIMAL";
    public static final String BIT = "BIT";
    public static final String TINYINT = "TINYINT";
    public static final String SMALLINT = "SMALLINT";
    public static final String INTEGER = "INTEGER";
    public static final String BIGINT = "BIGINT";
    public static final String REAL = "REAL";
    public static final String FLOAT = "FLOAT";
    public static final String DOUBLE = "DOUBLE";
    public static final String BINARY = "BINARY";
    public static final String VARBINARY = "VARBINARY";
    public static final String LONGVARBINARY = "LONGVARBINARY";
    public static final String BLOB = "BLOB";
    public static final String DATE = "DATE";
    public static final String TIME = "TIME";
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String BOOLEANCHAR = "BOOLEANCHAR";
    public static final String BOOLEANINT = "BOOLEANINT";
    private static final String[] TEXT_TYPES =
    {
        CHAR, VARCHAR, LONGVARCHAR, CLOB, DATE, TIME, TIMESTAMP, BOOLEANCHAR
    };


    public static final String CHAR_OBJECT_TYPE = "new String()";
    public static final String VARCHAR_OBJECT_TYPE = "new String()";
    public static final String LONGVARCHAR_OBJECT_TYPE = "new String()";
    public static final String CLOB_OBJECT_TYPE = "new String()";
    public static final String NUMERIC_OBJECT_TYPE = "new BigDecimal(0)";
    public static final String DECIMAL_OBJECT_TYPE = "new BigDecimal(0)";
    public static final String BIT_OBJECT_TYPE = "new Boolean(true)";
    public static final String TINYINT_OBJECT_TYPE = "new Byte((byte)0)";
    public static final String SMALLINT_OBJECT_TYPE = "new Short((short)0)";
    public static final String INTEGER_OBJECT_TYPE = "new Integer(0)";
    public static final String BIGINT_OBJECT_TYPE = "new Long(0)";
    public static final String REAL_OBJECT_TYPE = "new Float(0)";
    public static final String FLOAT_OBJECT_TYPE = "new Double(0)";
    public static final String DOUBLE_OBJECT_TYPE = "new Double(0)";
    public static final String BINARY_OBJECT_TYPE = "new Object()"; //?
    public static final String VARBINARY_OBJECT_TYPE = "new Object()"; //?
    public static final String LONGVARBINARY_OBJECT_TYPE = "new Object()"; //?
    public static final String BLOB_OBJECT_TYPE = "new Object()"; //?
    public static final String DATE_OBJECT_TYPE = "new Date()";
    public static final String TIME_OBJECT_TYPE = "new Date()";
    public static final String TIMESTAMP_OBJECT_TYPE = "new Date()";
    public static final String BOOLEANCHAR_OBJECT_TYPE = "new String()";
    public static final String BOOLEANINT_OBJECT_TYPE = "new Integer(0)";

    public static final String CHAR_NATIVE_TYPE = "String";
    public static final String VARCHAR_NATIVE_TYPE = "String";
    public static final String LONGVARCHAR_NATIVE_TYPE = "String";
    public static final String CLOB_NATIVE_TYPE = "String";
    public static final String NUMERIC_NATIVE_TYPE = "BigDecimal";
    public static final String DECIMAL_NATIVE_TYPE = "BigDecimal";
    public static final String BIT_NATIVE_TYPE = "boolean";
    public static final String TINYINT_NATIVE_TYPE = "byte";
    public static final String SMALLINT_NATIVE_TYPE = "short";
    public static final String INTEGER_NATIVE_TYPE = "int";
    public static final String BIGINT_NATIVE_TYPE = "long";
    public static final String REAL_NATIVE_TYPE = "float";
    public static final String FLOAT_NATIVE_TYPE = "double";
    public static final String DOUBLE_NATIVE_TYPE = "double";
    public static final String BINARY_NATIVE_TYPE = "byte[]";
    public static final String VARBINARY_NATIVE_TYPE = "byte[]";
    public static final String LONGVARBINARY_NATIVE_TYPE = "byte[]";
    public static final String BLOB_NATIVE_TYPE = "byte[]";
    public static final String DATE_NATIVE_TYPE = "Date";
    public static final String TIME_NATIVE_TYPE = "Date";
    public static final String TIMESTAMP_NATIVE_TYPE = "Date";
    public static final String BOOLEANCHAR_NATIVE_TYPE = "boolean";
    public static final String BOOLEANINT_NATIVE_TYPE = "boolean";

    public static final String CHAR_VILLAGE_METHOD = "asString()";
    public static final String VARCHAR_VILLAGE_METHOD = "asString()";
    public static final String LONGVARCHAR_VILLAGE_METHOD = "asString()";
    public static final String CLOB_VILLAGE_METHOD = "asString()";
    public static final String NUMERIC_VILLAGE_METHOD = "asBigDecimal()";
    public static final String DECIMAL_VILLAGE_METHOD = "asBigDecimal()";
    public static final String BIT_VILLAGE_METHOD = "asBoolean()";
    public static final String TINYINT_VILLAGE_METHOD = "asByte()";
    public static final String SMALLINT_VILLAGE_METHOD = "asShort()";
    public static final String INTEGER_VILLAGE_METHOD = "asInt()";
    public static final String BIGINT_VILLAGE_METHOD = "asLong()";
    public static final String REAL_VILLAGE_METHOD = "asFloat()";
    public static final String FLOAT_VILLAGE_METHOD = "asDouble()";
    public static final String DOUBLE_VILLAGE_METHOD = "asDouble()";
    public static final String BINARY_VILLAGE_METHOD = "asBytes()";
    public static final String VARBINARY_VILLAGE_METHOD = "asBytes()";
    public static final String LONGVARBINARY_VILLAGE_METHOD = "asBytes()";
    public static final String BLOB_VILLAGE_METHOD = "asBytes()";
    public static final String DATE_VILLAGE_METHOD = "asUtilDate()";
    public static final String TIME_VILLAGE_METHOD = "asUtilDate()";
    public static final String TIMESTAMP_VILLAGE_METHOD = "asUtilDate()";
    public static final String BOOLEANCHAR_VILLAGE_METHOD = "asString()";
    public static final String BOOLEANINT_VILLAGE_METHOD = "asInt()";

    public static final String CHAR_PP_METHOD = "getString(ppKey)";
    public static final String VARCHAR_PP_METHOD = "getString(ppKey)";
    public static final String LONGVARCHAR_PP_METHOD = "getString(ppKey)";
    public static final String NUMERIC_PP_METHOD = "getBigDecimal(ppKey)";
    public static final String DECIMAL_PP_METHOD = "getBigDecimal(ppKey)";
    public static final String BIT_PP_METHOD = "getBoolean(ppKey)";
    public static final String TINYINT_PP_METHOD = "getByte(ppKey)";
    public static final String SMALLINT_PP_METHOD = "getShort(ppKey)";
    public static final String INTEGER_PP_METHOD = "getInt(ppKey)";
    public static final String BIGINT_PP_METHOD = "getLong(ppKey)";
    public static final String REAL_PP_METHOD = "getFloat(ppKey)";
    public static final String FLOAT_PP_METHOD = "getDouble(ppKey)";
    public static final String DOUBLE_PP_METHOD = "getDouble(ppKey)";
    public static final String BINARY_PP_METHOD = "getBytes(ppKey)";
    public static final String VARBINARY_PP_METHOD = "getBytes(ppKey)";
    public static final String LONGVARBINARY_PP_METHOD = "getBytes(ppKey)";
    public static final String DATE_PP_METHOD = "getDate(ppKey)";
    public static final String TIME_PP_METHOD = "getDate(ppKey)";
    public static final String TIMESTAMP_PP_METHOD = "getDate(ppKey)";
    public static final String BOOLEANCHAR_PP_METHOD = "getBoolean(ppKey)";
    public static final String BOOLEANINT_PP_METHOD = "getBoolean(ppKey)";

    private static Hashtable jdbcToJavaObjectMap = null;
    private static Hashtable jdbcToJavaNativeMap = null;
    private static Hashtable jdbcToVillageMethodMap = null;
    private static Hashtable jdbcToPPMethodMap = null;
    private static Hashtable torqueTypeToJdbcTypeMap = null;
    private static Hashtable jdbcToTorqueTypeMap = null;
    private static boolean isInitialized = false;

    /**
     * Initializes the SQL to Java map so that it
     * can be used by client code.
     */
    public synchronized static void initialize()
    {
        if (isInitialized == false)
        {
            /*
             * Create JDBC -> Java object mappings.
             */
            
            jdbcToJavaObjectMap = new Hashtable();
            
            jdbcToJavaObjectMap.put(CHAR, CHAR_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(VARCHAR, VARCHAR_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(LONGVARCHAR, LONGVARCHAR_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(CLOB, CLOB_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(NUMERIC, NUMERIC_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(DECIMAL, DECIMAL_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(BIT, BIT_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(TINYINT, TINYINT_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(SMALLINT, SMALLINT_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(INTEGER, INTEGER_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(BIGINT, BIGINT_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(REAL, REAL_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(FLOAT, FLOAT_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(DOUBLE, DOUBLE_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(BINARY, BINARY_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(VARBINARY, VARBINARY_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(LONGVARBINARY, LONGVARBINARY_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(BLOB, BLOB_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(DATE, DATE_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(TIME, TIME_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(TIMESTAMP, TIMESTAMP_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(BOOLEANCHAR, BOOLEANCHAR_OBJECT_TYPE);
            jdbcToJavaObjectMap.put(BOOLEANINT, BOOLEANINT_OBJECT_TYPE);
        
            /*
             * Create JDBC -> native Java type mappings.
             */
            
            jdbcToJavaNativeMap = new Hashtable();
            
            jdbcToJavaNativeMap.put(CHAR, CHAR_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(VARCHAR, VARCHAR_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(LONGVARCHAR, LONGVARCHAR_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(CLOB, CLOB_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(NUMERIC, NUMERIC_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(DECIMAL, DECIMAL_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(BIT, BIT_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(TINYINT, TINYINT_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(SMALLINT, SMALLINT_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(INTEGER, INTEGER_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(BIGINT, BIGINT_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(REAL, REAL_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(FLOAT, FLOAT_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(DOUBLE, DOUBLE_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(BINARY, BINARY_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(VARBINARY, VARBINARY_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(LONGVARBINARY, LONGVARBINARY_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(BLOB, BLOB_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(DATE, DATE_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(TIME, TIME_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(TIMESTAMP, TIMESTAMP_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(BOOLEANCHAR, BOOLEANCHAR_NATIVE_TYPE);
            jdbcToJavaNativeMap.put(BOOLEANINT, BOOLEANINT_NATIVE_TYPE);

            /*
             * Create JDBC -> Village asX() mappings.
             */
            
            jdbcToVillageMethodMap = new Hashtable();
            
            jdbcToVillageMethodMap.put(CHAR, CHAR_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(VARCHAR, VARCHAR_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(LONGVARCHAR, LONGVARCHAR_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(CLOB, CLOB_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(NUMERIC, NUMERIC_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(DECIMAL, DECIMAL_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(BIT, BIT_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(TINYINT, TINYINT_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(SMALLINT, SMALLINT_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(INTEGER, INTEGER_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(BIGINT, BIGINT_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(REAL, REAL_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(FLOAT, FLOAT_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(DOUBLE, DOUBLE_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(BINARY, BINARY_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(VARBINARY, VARBINARY_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(LONGVARBINARY, LONGVARBINARY_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(BLOB, BLOB_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(DATE, DATE_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(TIME, TIME_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(TIMESTAMP, TIMESTAMP_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(BOOLEANCHAR, BOOLEANCHAR_VILLAGE_METHOD);
            jdbcToVillageMethodMap.put(BOOLEANINT, BOOLEANINT_VILLAGE_METHOD);

            /*
             * Create JDBC -> ParameterParser getX() mappings.
             */
            
            jdbcToPPMethodMap = new Hashtable();
            
            jdbcToPPMethodMap.put(CHAR, CHAR_PP_METHOD);
            jdbcToPPMethodMap.put(VARCHAR, VARCHAR_PP_METHOD);
            jdbcToPPMethodMap.put(LONGVARCHAR, LONGVARCHAR_PP_METHOD);
            jdbcToPPMethodMap.put(NUMERIC, NUMERIC_PP_METHOD);
            jdbcToPPMethodMap.put(DECIMAL, DECIMAL_PP_METHOD);
            jdbcToPPMethodMap.put(BIT, BIT_PP_METHOD);
            jdbcToPPMethodMap.put(TINYINT, TINYINT_PP_METHOD);
            jdbcToPPMethodMap.put(SMALLINT, SMALLINT_PP_METHOD);
            jdbcToPPMethodMap.put(INTEGER, INTEGER_PP_METHOD);
            jdbcToPPMethodMap.put(BIGINT, BIGINT_PP_METHOD);
            jdbcToPPMethodMap.put(REAL, REAL_PP_METHOD);
            jdbcToPPMethodMap.put(FLOAT, FLOAT_PP_METHOD);
            jdbcToPPMethodMap.put(DOUBLE, DOUBLE_PP_METHOD);
            jdbcToPPMethodMap.put(BINARY, BINARY_PP_METHOD);
            jdbcToPPMethodMap.put(VARBINARY, VARBINARY_PP_METHOD);
            jdbcToPPMethodMap.put(LONGVARBINARY, LONGVARBINARY_PP_METHOD);
            jdbcToPPMethodMap.put(DATE, DATE_PP_METHOD);
            jdbcToPPMethodMap.put(TIME, TIME_PP_METHOD);
            jdbcToPPMethodMap.put(TIMESTAMP, TIMESTAMP_PP_METHOD);
            jdbcToPPMethodMap.put(BOOLEANCHAR, BOOLEANCHAR_PP_METHOD);
            jdbcToPPMethodMap.put(BOOLEANINT, BOOLEANINT_PP_METHOD);

            /*
             * Create JDBC -> Java object mappings.
             */
            
            torqueTypeToJdbcTypeMap = new Hashtable();
            
            torqueTypeToJdbcTypeMap.put(CHAR, CHAR);
            torqueTypeToJdbcTypeMap.put(VARCHAR, VARCHAR);
            torqueTypeToJdbcTypeMap.put(LONGVARCHAR, LONGVARCHAR);
            torqueTypeToJdbcTypeMap.put(CLOB, CLOB);
            torqueTypeToJdbcTypeMap.put(NUMERIC, NUMERIC);
            torqueTypeToJdbcTypeMap.put(DECIMAL, DECIMAL);
            torqueTypeToJdbcTypeMap.put(BIT, BIT);
            torqueTypeToJdbcTypeMap.put(TINYINT, TINYINT);
            torqueTypeToJdbcTypeMap.put(SMALLINT, SMALLINT);
            torqueTypeToJdbcTypeMap.put(INTEGER, INTEGER);
            torqueTypeToJdbcTypeMap.put(BIGINT, BIGINT);
            torqueTypeToJdbcTypeMap.put(REAL, REAL);
            torqueTypeToJdbcTypeMap.put(FLOAT, FLOAT);
            torqueTypeToJdbcTypeMap.put(DOUBLE, DOUBLE);
            torqueTypeToJdbcTypeMap.put(BINARY, BINARY);
            torqueTypeToJdbcTypeMap.put(VARBINARY, VARBINARY);
            torqueTypeToJdbcTypeMap.put(LONGVARBINARY, LONGVARBINARY);
            torqueTypeToJdbcTypeMap.put(BLOB, BLOB);
            torqueTypeToJdbcTypeMap.put(DATE, DATE);
            torqueTypeToJdbcTypeMap.put(TIME, TIME);
            torqueTypeToJdbcTypeMap.put(TIMESTAMP, TIMESTAMP);
            torqueTypeToJdbcTypeMap.put(BOOLEANCHAR, CHAR);
            torqueTypeToJdbcTypeMap.put(BOOLEANINT, INTEGER);
            
            /*
             * Create JDBC type code to torque type map.
             */
            jdbcToTorqueTypeMap = new Hashtable();

            jdbcToTorqueTypeMap.put(new Integer(Types.CHAR), CHAR);
            jdbcToTorqueTypeMap.put(new Integer(Types.VARCHAR), VARCHAR);
            jdbcToTorqueTypeMap.put(new Integer(Types.LONGVARCHAR), LONGVARCHAR);
            jdbcToTorqueTypeMap.put(new Integer(Types.CLOB), CLOB);
            jdbcToTorqueTypeMap.put(new Integer(Types.NUMERIC), NUMERIC);
            jdbcToTorqueTypeMap.put(new Integer(Types.DECIMAL), DECIMAL);
            jdbcToTorqueTypeMap.put(new Integer(Types.BIT), BIT);
            jdbcToTorqueTypeMap.put(new Integer(Types.TINYINT), TINYINT);
            jdbcToTorqueTypeMap.put(new Integer(Types.SMALLINT), SMALLINT);
            jdbcToTorqueTypeMap.put(new Integer(Types.INTEGER), INTEGER);
            jdbcToTorqueTypeMap.put(new Integer(Types.BIGINT), BIGINT);
            jdbcToTorqueTypeMap.put(new Integer(Types.REAL), REAL);
            jdbcToTorqueTypeMap.put(new Integer(Types.FLOAT), FLOAT);
            jdbcToTorqueTypeMap.put(new Integer(Types.DOUBLE), DOUBLE);
            jdbcToTorqueTypeMap.put(new Integer(Types.BINARY), BINARY);
            jdbcToTorqueTypeMap.put(new Integer(Types.VARBINARY), VARBINARY);
            jdbcToTorqueTypeMap.put(new Integer(Types.LONGVARBINARY), LONGVARBINARY);
            jdbcToTorqueTypeMap.put(new Integer(Types.BLOB), BLOB);
            jdbcToTorqueTypeMap.put(new Integer(Types.DATE), DATE);
            jdbcToTorqueTypeMap.put(new Integer(Types.TIME), TIME);
            jdbcToTorqueTypeMap.put(new Integer(Types.TIMESTAMP), TIMESTAMP);

            isInitialized = true;
        }
    }

    /**
     * Report whether this object has been initialized.
     */
    public static boolean isInitialized()
    {
        return isInitialized;
    }        

    /**
     * Return a Java object which corresponds to the
     * JDBC type provided. Use in MapBuilder generation.
     */
    public static String getJavaObject(String jdbcType)
    {
        /*
         * Make sure the we are initialized.
         */
        if (isInitialized == false)
            initialize();
        
        return (String)jdbcToJavaObjectMap.get(jdbcType);
    }

    /**
     * Return native java type which corresponds to the
     * JDBC type provided. Use in the base object class
     * generation.
     */
    public static String getJavaNative(String jdbcType)
    {
        /*
         * Make sure the we are initialized.
         */
        if (isInitialized == false)
            initialize();
        
        return (String) jdbcToJavaNativeMap.get(jdbcType);
    }

    /**
     * Return Village asX() method which corresponds to the
     * JDBC type provided. Use in the Peer class
     * generation.
     */
    public static String getVillageMethod(String jdbcType)
    {
        /*
         * Make sure the we are initialized.
         */
        if (isInitialized == false)
            initialize();
        
        return (String) jdbcToVillageMethodMap.get(jdbcType);
    }

    /**
     * Return ParameterParser getX() method which corresponds to the
     * JDBC type provided. Use in the Object class
     * generation.
     */
    public static String getPPMethod(String jdbcType)
    {
        /*
         * Make sure the we are initialized.
         */
        if (isInitialized == false)
            initialize();
        
        return (String) jdbcToPPMethodMap.get(jdbcType);
    }

    /**
     * Returns the correct jdbc type for torque added types
     */
    public static String getJdbcType(String type)
    {
        /*
         * Make sure the we are initialized.
         */
        if (isInitialized == false)
            initialize();
        
        return (String) torqueTypeToJdbcTypeMap.get(type);
    }
    
    /**
     * Returns Torque type constant corresponding to JDBC type code.
     * Used but Torque JDBC task. 
     */
    public static String getTorqueType(Integer sqlType)
    {
         /*
         * Make sure the we are initialized.
         */
        if (isInitialized == false)
            initialize();

        return (String)jdbcToTorqueTypeMap.get(sqlType);
    }

    /**
     * Returns true if the type is boolean in the java
     * object and a numeric (1 or 0) in the db.
     *
     * @param type The type to check.
     */
    public static boolean isBooleanInt(String type)
    {
        return BOOLEANINT.equals(type);
    }

    /**
     * Returns true if the type is boolean in the 
     * java object and a String "Y" or "N" in the db.
     *
     * @param type The type to check.
     */
    public static boolean isBooleanChar(String type)
    {
        return BOOLEANCHAR.equals(type);
    }

    /**
     * Returns true if values for the type need to be quoted.
     *
     * @param type The type to check.
     */
    public static final boolean isTextType(String type)
    {
        for (int i = 0; i < TEXT_TYPES.length; i++)
        {
            if (type.equals(TEXT_TYPES[i]))
            {
                return true;
            }
        }

        // If we get this far, there were no matches.
        return false;
    }
}


