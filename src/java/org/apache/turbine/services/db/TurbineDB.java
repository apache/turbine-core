package org.apache.turbine.services.db;

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

import java.sql.Connection;
import org.apache.torque.Torque;
import org.apache.torque.adapter.DB;
import org.apache.torque.map.DatabaseMap;
import org.apache.turbine.util.TurbineException;

/**
 * This class provides a common front end to all database - related
 * services in Turbine. Currently these are {@link PoolBrokerService} and
 * {@link MapBrokerService}. This class contains static methods that you
 * can call to access the methods of system's configured service
 * implementations.
 * <p>
 * <b> This class is deprecated you should use org.apache.torque.Torque</b>
 *
 * Connection dbConn = null;
 * try
 * {
 *     dbConn = Torque.getConnection();
 *     // Do something with the connection here...
 * }
 * catch (Exception e)
 * {
 *     // Either from obtaining the connection or from your application code.
 * }
 * finally
 * {
 *     Torque.closeConnection(dbConn);
 * }
 * </pre></code></blockquote>
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 * @deprecated As of Turbine 2.2, use org.apache.torque.Torque
 */
public abstract class TurbineDB
{
    ///////////////////////////////////////////////////////////////////////////
    // Database maps
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the map name for the default database.
     *
     * @return the map name for the default database.
     */
    public static String getDefaultMap()
    {
        // Required due to the nasty coupling between
        // torque in 2.x and the db related services.
        // This is only called once in the peer so the
        // hit of catching the exception will only happen
        // once and while running torque from the command
        // line it won't incur an unbearable wait.
        return Torque.getDefaultDB();
    }

    /**
     * Returns the default database map information.
     *
     * @return A DatabaseMap.
     * @throws TurbineException Any exceptions caught during processing will be
     *         rethrown wrapped into a TurbineException.
     */
    public static DatabaseMap getDatabaseMap() throws TurbineException
    {
        try
        {
            return Torque.getDatabaseMap();
        }
        catch (Exception ex)
        {
            throw new TurbineException(ex);
        }
    }

    /**
     * Returns the database map information. Name relates to the name
     * of the connection pool to associate with the map.
     *
     * @param name The name of the <code>DatabaseMap</code> to
     * retrieve.
     * @return The named <code>DatabaseMap</code>.
     * @throws TurbineException Any exceptions caught during processing will be
     *         rethrown wrapped into a TurbineException.
     */
    public static DatabaseMap getDatabaseMap(String name)
            throws TurbineException
    {
        try
        {
            return Torque.getDatabaseMap(name);
        }
        catch (Exception ex)
        {
            throw new TurbineException(ex);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Connection pooling
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the pool name for the default database.
     *
     * @return the pool name for the default database.
     */
    public static String getDefaultDB()
    {
        // Required due to the nasty coupling between
        // torque in 2.x and the db related services.
        // This is only called once in the peer so the
        // hit of catching the exception will only happen
        // once and while running torque from the command
        // line it won't incur an unbearable wait.
        return Torque.getDefaultDB();
    }

    /**
     * This method returns a DBConnection from the default pool.
     *
     * @return The requested connection.
     * @throws Exception Any exceptions caught during processing will be
     *         rethrown wrapped into a TurbineException.
     */
    public static Connection getConnection() throws Exception
    {
        return Torque.getConnection();
    }

    /**
     * This method returns a DBConnection from the pool with the
     * specified name.  The pool must either have been registered
     * with the {@link #registerPool(String,String,String,String,String)}
     * method, or be specified in the property file using the
     * following syntax:
     *
     * <pre>
     * database.[name].driver
     * database.[name].url
     * database.[name].username
     * database.[name].password
     * </pre>
     *
     * @param name The name of the pool to get a connection from.
     * @return     The requested connection.
     * @throws Exception Any exceptions caught during processing will be
     *         rethrown wrapped into a TurbineException.
     */
    public static Connection getConnection(String name) throws Exception
    {
        return Torque.getConnection(name);
    }

    /**
     * Release a connection back to the database pool.
     *
     * @param dbconn the connection to release
     * @throws Exception A generic exception.
     */
    public static void releaseConnection(Connection dbconn) throws Exception
    {
        Torque.closeConnection(dbconn);
    }

    ///////////////////////////////////////////////////////////////////////////
    // DB Adapters
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the database adapter for the default connection pool.
     *
     * @return The database adapter.
     * @throws Exception Any exceptions caught during processing will be
     *         rethrown wrapped into a TurbineException.
     */
    public static DB getDB() throws Exception
    {
        return Torque.getDB(Torque.getDefaultDB());
    }

    /**
     * Returns database adapter for a specific connection pool.
     *
     * @param name A pool name.
     * @return     The corresponding database adapter.
     * @throws Exception Any exceptions caught during processing will be
     *         rethrown wrapped into a TurbineException.
     */
    public static DB getDB(String name) throws Exception
    {
        return Torque.getDB(name);
    }
}
