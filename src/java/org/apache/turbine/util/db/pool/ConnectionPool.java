package org.apache.turbine.util.db.pool;

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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Stack;
import java.util.Vector;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.util.db.adapter.DB;
import org.apache.turbine.util.db.adapter.DBFactory;

/**
 * This class implements a simple connection pooling scheme.  Multiple
 * pools are available through use of the <code>PoolBrokerService</code>.
 *
 * @author <a href="mailto:csterg@aias.gr">Costas Stergiou</a>
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @author <a href="mailto:dlr@collab.net">Daniel L. Rall</a>
 * @author <a href="mailto:paul@evolventtech.com">Paul O'Leary</a>
 * @author <a href="mailto:magnus@handtolvur.is">Magnús Þór Torfason</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public class ConnectionPool
{
    /**
     * Pool containing database connections.
     */
    private Stack pool = null;

    /**
     * The driver type for this pool.
     */
    private String driver = null;

    /**
     * The url for this pool.
     */
    private String url = null;

    /**
     * The user name for this pool.
     */
    private String username = null;

    /**
     * The password for this pool.
     */
    private String password = null;

    /**
     * The current number of database connections that have been
     * created.
     */
    private int totalConnections = 0;

    /**
     * The maximum number of database connections that can be
     * created.
     */
    private int maxConnections = 10;

    /**
     * The amount of time in milliseconds that a connection will be
     * pooled.
     */
    private long expiryTime = 3600000;   // 1 hour

    /**
     * The number of times to attempt to obtain a pooled connection
     * before giving up.
     */
    private long maxConnectionAttempts = 50;

    /**
     * The number of times that an attempt to obtain a pooled
     * connection has been made.
     */
    private long connectionAttemptsCounter = 0;

    /**
     * Thread sleep time between checks for database connectivity
     * problems.
     */
    private long dbCheckFrequency = 5000;

    /**
     * The class containing the database specific info for connections
     * in this pool (i.e. the Turbine database adapter).
     */
    private DB db = null;

    /**
     * Amount of time a thread asking the pool for a cached connection will
     * wait before timing out and throwing an error.
     */
    private long connectionWaitTimeout = 10 * 1000; // ten seconds

    /**
     * The ConnectionPoolDataSource if the driver is JDBC 2.0 compliant
     */
    private ConnectionPoolDataSource cpds = null;

    /**
     * Creates a <code>ConnectionPool</code> with the default
     * attributes.
     * @deprecated Use the constructor specifying db parameters.
     */
    public ConnectionPool()
    {
        this(null, null, null, null);
    }

    /**
     * Creates a <code>ConnectionPool</code> with the specified
     * attributes.
     *
     * @param maxCons The maximum number of connections for this pool.
     * @param expiryTime The expiration time in milliseconds.
     * @deprecated Use the constructor specifying db parameters.
     */
    public ConnectionPool(int maxCons, long expiryTime)
    {
        this(null, null, null, null, maxCons, expiryTime);
    }

    /**
     * Creates a <code>ConnectionPool</code> with the default
     * attributes.
     *
     * @param driver   The driver type for this pool.
     * @param url      The url for this pool.
     * @param usernam  The user name for this pool.
     * @param password The password for this pool.
     */
    public ConnectionPool(String driver,
                          String url,
                          String username,
                          String password)
    {
        pool = new Stack();

        maxConnections =
            TurbineResources.getInt("database.maxConnections", 10);
        expiryTime =
            TurbineResources.getLong("database.expiryTime", 3600000);
        maxConnectionAttempts =
            TurbineResources.getLong("database.maxConnectionAttempts", 50);
        connectionWaitTimeout =
            TurbineResources.getLong("database.connectionWaitTimeout",
                                     10 * 1000);

        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;

        try
        {
            cpds = getDB().getConnectionPoolDataSource();
        }
        catch(Exception ignore)
        {
            // if the JDBC driver does not support the standard extensions, we
            // will get an exception here. Just ignore it and use the old way.
        }
    }

    /**
     * Creates a <code>ConnectionPool</code> with the specified
     * attributes.
     *
     * @param driver     The driver type for this pool.
     * @param url        The url for this pool.
     * @param usernam    The user name for this pool.
     * @param password   The password for this pool.
     * @param maxCons    The maximum number of connections for this pool.
     * @param expiryTime The expiration time in milliseconds.
     */
    public ConnectionPool(String driver,
                          String url,
                          String username,
                          String password,
                          int maxCons,
                          long expiryTime)
    {
        pool = new Stack();

        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;

        this.maxConnections = maxCons;
        this.expiryTime = expiryTime;

        maxConnectionAttempts =
            TurbineResources.getLong("database.maxConnectionAttempts", 50);
        connectionWaitTimeout =
            TurbineResources.getLong("database.connectionWaitTimeout",
                                     10 * 1000);

        try
        {
            cpds = getDB().getConnectionPoolDataSource();
        }
        catch(Exception ignore)
        {
            // if the JDBC driver does not support the standard extensions, we
            // will get an exception here. Just ignore it and use the old way.
        }
    }

    /**
     * Close any open connections when this object is garbage collected.
     *
     * @exception Throwable Anything might happen...
     */
    protected void finalize()
        throws Throwable
    {
        shutdown();
    }

    /**
     * Attempt to establish a database connection.
     */
    public synchronized PooledConnection getPooledConnection()
        throws SQLException
    {
        try
        {
            return (PooledConnection) getConnection();
        }
        catch (Exception e)
        {
            throw new SQLException (e.toString());
        }
    }

    /**
     * Attempt to establish a database connection.
     */
    public synchronized PooledConnection getPooledConnection(String user,
                                                String password)
        throws SQLException
    {
        try
        {
            return (PooledConnection)
                getConnection(driver, url, user, password);
        }
        catch (Exception e)
        {
            throw new SQLException (e.toString());
        }
    }


    /**
     * Returns a connection that maintains a link to the pool it came from.
     *
     * @param driver   The fully-qualified name of the JDBC driver to use.
     * @param url      The URL of the database from which the connection is
     *                 desired.
     * @param username The name of the database user.
     * @param password The password of the database user.
     * @return         A database connection.
     *
     * @exception Exception
     */
    public synchronized final DBConnection getConnection()
        throws Exception
    {
        DBConnection dbconn = null;

        if ( pool.empty() && totalConnections < maxConnections )
        {
            dbconn = getNewConnection();
        }
        else
        {
            dbconn = getInternalPooledConnection();
        }
        dbconn.link(this);
        return dbconn;
    }

    /**
     * This function is unsafe to use, since the user passed parameters
     * that he has no way to know if are used.
     *
     * @param driver   The fully-qualified name of the JDBC driver to use.
     * @param url      The URL of the database from which the connection is
     *                 desired.
     * @param username The name of the database user.
     * @param password The password of the database user.
     * @return         A database connection.
     * @deprecated Database parameters should not be specified each
     * time a DBConnection is fetched from the pool.
     * @exception Exception.
     */
    public synchronized final DBConnection getConnection(String driver,
                                                   String url,
                                                   String username,
                                                   String password)
        throws Exception
    {
        if ( (this.driver == null) && (this.url == null) &&
             (this.username == null) && (this.password == null) )
        {
            this.driver = driver;
            this.url = url;
            this.username = username;
            this.password = password;
        }

        return getConnection();
    }

    /**
     * Returns an instance of the database adapter associated with
     * this pool.
     *
     * @return The <code>DB</code> associated with this pool.
     */
    public DB getDB()
        throws Exception
    {
        // PoolBrokerService keeps a collection of ConnectionPools,
        // each one of which contains connections to a single database.
        // The initialization of a pool should only occur once.
        if (db == null)
        {
            db = DBFactory.create( driver );
            db.init( url, username, password );
        }
        return db;
    }

    /**
     * The log writer is a character output stream to which all
     * logging and tracing messages for this data source object
     * instance will be printed. This includes messages printed by
     * the methods of this object, messages printed by methods of
     * other objects manufactured by this object, and so on.
     * Messages printed to a data source specific log writer are
     * not printed to the log writer associated with the
     * java.sql.Drivermanager class. When a data source object is
     * created the log writer is initially null, in other words,
     * logging is disabled.
     *
     * If using JDBC2.0 dispatch to the ConnectionPoolDataSource
     */
    public PrintWriter getLogWriter()
        throws SQLException
    {
        if ( cpds != null )
            return cpds.getLogWriter();
        else
            return null;
    }

    /**
     * The log writer is a character output stream to which all
     * logging and tracing messages for this data source object
     * instance will be printed. This includes messages printed by
     * the methods of this object, messages printed by methods of
     * other objects manufactured by this object, and so on.
     * Messages printed to a data source specific log writer are
     * not printed to the log writer associated with the
     * java.sql.Drivermanager class. When a data source object is
     * created the log writer is initially null, in other words,
     * logging is disabled.
     *
     * If using JDBC2.0 dispatch to the ConnectionPoolDataSource
     */
    public void setLogWriter(PrintWriter out)
        throws SQLException
    {
        if ( cpds != null )
            cpds.setLogWriter(out);
    }

    /**
     * Sets the maximum time in seconds that this data source will
     * wait while attempting to connect to a database. A value of
     * zero specifies that the timeout is the default system
     * timeout if there is one; otherwise it specifies that there
     * is no timeout. When a data source object is created the
     * login timeout is initially zero.
     */
    public void setLoginTimeout(int seconds)
        throws SQLException
    {
        this.connectionWaitTimeout =
            new Integer(seconds).longValue() * 1000;
    }

    /*
     * Gets the maximum time in seconds that this data source can wait while
     * attempting to connect to a database. A value of zero means that the
     * timeout is the default system timeout if there is one; otherwise it
     * means that there is no timeout. When a data source object is created
     * the login timeout is initially zero.
     */
    public int getLoginTimeout()
        throws SQLException
    {
        return new Long(connectionWaitTimeout).intValue() / 1000;
    }

    /**
     * Returns a fresh connection to the database.  The database type
     * is specified by <code>driver</code>, and its connection
     * information by <code>url</code>, <code>username</code>, and
     * <code>password</code>.
     *
     * @return A database connection.
     * @exception Exception
     */
    protected DBConnection getNewConnection()
        throws Exception
    {
        // This is a JDBC 2.0 compliant driver so use the
        // ConnectionPoolDataSource to get a PooledConnection
        if ( cpds != null )
        {
            PooledConnection pc = cpds.getPooledConnection(this.username,
                this.password);
            DBConnection dbc = new DBConnection( pc, url, null, null );
            totalConnections++;
            return dbc;
        }
        // no JDBC 2.0 compliance, go the old way...
        else
        {
            DBConnection dbc = new DBConnection( getDB().getConnection(), url );
            totalConnections++;
            return dbc;
        }
    }

    /**
     * Helper function that attempts to pop a connection off the pool's stack,
     * handling the case where the popped connection has become invalid by
     * creating a new connection.
     *
     * @return An existing or new database connection.
     * @exception Exception
     */
    private DBConnection popConnection()
        throws Exception
    {
        while ( !pool.empty() )
        {
            DBConnection con = (DBConnection) pool.pop();

            // It's really not safe to assume this connection is
            // valid even though it's checked before being pooled.
            if ( isValid(con) )
            {
                connectionAttemptsCounter = 0;
                return con;
            }
            else
            {
                // Close invalid connection.
                con.close();
                totalConnections--;
                connectionAttemptsCounter = 0;

                // If the pool is now empty, create a new connection.  We're
                // guaranteed not to exceed the connection limit since we
                // just killed off one or more invalid connections, and no
                // one else can be accessing this cache right now.
                if ( pool.empty() )
                {
                    return getNewConnection();
                }
            }
        }

        // The connection pool was empty to start with--don't call this
        // routine if there's no connection to pop!
        // TODO: Propose general Turbine assertion failure exception? -PGO
        throw new Exception("Assertaion failure: Attempted to pop " +
                            "connection from empty pool!");
    }

    /**
     * Gets a pooled database connection.
     *
     * @return A database connection.
     * @exception ConnectionWaitTimeoutException Wait time exceeded.
     * @exception Exception No pooled connections.
     */
    private synchronized DBConnection getInternalPooledConnection()
        throws ConnectionWaitTimeoutException, Exception
    {
        DBConnection dbconn = null;

        if ( pool.empty() )
        {
            connectionAttemptsCounter++;

            // The connection pool is empty and we cannot allocate any new
            // connections.  Wait the prescribed amount of time and see if
            // a connection is returned.
            try
            {
                wait( connectionWaitTimeout );
            }
            catch (InterruptedException ignored)
            {
                // Don't care how we come out of the wait state.
            }

            // Check for a returned connection.
            if ( pool.empty() )
            {
                // If the pool is still empty here, we were not awoken by
                // someone returning a connection.
                throw new ConnectionWaitTimeoutException(url);
            }
            dbconn = popConnection();
        }
        else
        {
            dbconn = popConnection();
        }

        return dbconn;
    }

    /**
     * Helper method which determines whether a connection has expired.
     *
     * @param connection The connection to test.
     * @return True if the connection is expired, false otherwise.
     * @exception Exception
     */
    private boolean isExpired( DBConnection connection )
        throws Exception
    {
        // Test the age of the connection (defined as current time
        // minus connection birthday) against the connection pool
        // expiration time.
        return ((System.currentTimeMillis() -
                 connection.getTimestamp()) > expiryTime);
    }

    /**
     * Determines if a connection is still valid.
     *
     * @param connection The connection to test.
     * @return True if the connection is valid, false otherwise.
     * @exception Exception
     */
    private boolean isValid( DBConnection connection )
        throws Exception
    {
        try
        {
            // This will throw an exception if:
            //     The connection is null
            //     The connection is closed
            // Therefore, it would be false.
            connection.getConnection();
            // Check for expiration
            return !isExpired(connection);
        }
        catch (SQLException e)
        {
            return false;
        }
    }

    /**
     * This method returns a connection to the pool, and <b>must</b>
     * be called by the requestor when finished with the connection.
     *
     * @param connection The database connection to release.
     * @exception Exception Trouble releasing the connection.
     */
    public synchronized void releaseConnection(DBConnection dbconn)
        throws Exception
    {
        // DBConnections MUST be unlinked when returned to the pool
        dbconn.unlink(this);

        if ( isValid(dbconn) )
        {
            pool.push(dbconn);
            notify();
        }
        else
        {
            try
            {
                dbconn.close();
            }
            catch (Exception ignored)
            {
                // ignored
            }
            finally
            {
                decrementConnections();
            }
        }
    }

    /**
     * Close all connections to the database,
     */
    public void shutdown()
    {
        if ( pool != null )
        {
            while ( !pool.isEmpty() )
            {
                try
                {
                    ((DBConnection)pool.pop()).close();
                }
                catch (SQLException ignore)
                {
                }
                finally
                {
                    totalConnections--;
                }
            }
        }
    }

    /**
     * Re
     turns the Total connections in the pool
     */
    public int getTotalCount()
    {
        return totalConnections;
    }

    /**
     * Returns the available connections in the pool
     */
    public int getNbrAvailable()
    {
        return pool.size();
    }

    /**
     * Returns the checked out connections in the pool
     */
    public int getNbrCheckedOut()
    {
        return (totalConnections - pool.size());
    }

    /**
     * Decreases the count of connections in the pool
     * and also calls <code>notify()</code>.
     */
    public void decrementConnections()
    {
        totalConnections--;
        notify();
    }

    /**
     * This method does nothing in the current implementation
     */
/*    public void connectionClosed(ConnectionEvent event) {
        System.err.println("CONNECTION CLOSED" + event);
    }


    public void connectionErrorOccurred(ConnectionEvent e) {
        //System.out.println("CONNECTION EVENT ERROR: " + e);

        PooledConnection pc = (PooledConnection) e.getSource();
        try {
            pc.close();
        }
        catch (Exception ex) {
            System.err.println("ERR: CONN: " + ex);
        }

    }
*/
    /*
      This is being left commented for now because it requires more
      thought that I'm not ready to give to it yet. -JSS

    public void run()
    {
        while (true)
        {
            // Wait for a bit.
            try
            {
                Thread.sleep(dbCheckFrequency);
            }
            catch (InterruptedException e)
            {
            }

            // Check for database connectivity problems.
            if ( dbconnAttemptsCounter >= maxConnectionAttempts )
            {
                // Dump all connections.
                try
                {
                    finalize();
                }
                catch (Throwable e)
                {
                }

                connectionAttemptsCounter = 0;
                notify();
            }
        }
    }
    */
}
