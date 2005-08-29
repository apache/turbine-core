package org.apache.turbine.services.db;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.sql.Connection;

import org.apache.torque.Torque;
import org.apache.torque.adapter.DB;
import org.apache.torque.map.DatabaseMap;
import org.apache.turbine.util.TurbineException;

/**
 * This class provides a common front end to all database - related
 * services in Turbine. This class contains static methods that you
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
     * specified name. The pool must be specified in the property file using
     * the following syntax:
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
