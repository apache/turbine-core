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

import java.io.PrintStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.EventObject;
import java.util.Vector;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;

import org.apache.turbine.util.Log;

/**
 * This class wraps the JDBC <code>Connection</code> class, providing
 * built-in exception logging to stderr and a timestamp which
 * indicates how old the <code>Connection</code> is.
 * <code>DBConnection</code>'s timestamp is used by the
 * <code>ConnectionPool</code> to expire connections when they get too
 * old.  Do not use this class as a shared resource (it is not thread-safe).
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:dlr@collab.net">Daniel L. Rall</a>
 * @author <a href="mailto:magnus@handtolvur.is">Magnús Þór Torfason</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public class DBConnection implements ConnectionEventListener
{
    /**
     * The ConnectionPool that this DBConnection came from.
     * A null means that the DBConnection either does not
     * belong to any pool, or that it is currently inside it's pool
     */
    private ConnectionPool pool = null;

    /**
     * The JDBC database connection.
     */
    private Connection connection = null;

    /**
     * The JDBC PooledConnection (if supported by the JDBC driver). If this
     * is null, then this class uses the classic Connection object to manage
     * connection. Else, the PooledConnection object is used.
     */
     private PooledConnection pooledConnection = null;

    /**
     * The URL of this connection.
     */
    private String url = null;

    /**
     * The user name for this connection.
     */
    private String username = null;

    /**
     * The time in milliseconds at which the connection was created.
     */
    private long timestamp;

    /**
     * ConnectionEventListeners
     */
    private Vector eventListeners;

    /**
     * Creates a Turbine <code>DBConnection</code> specifying
     * nothing but a single database Connection.
     *
     * @param connection The JDBC connection to wrap.
     */
    protected DBConnection(Connection connection)
    {
        this(connection, null, null, null);
    }

    /**
     * Creates a Turbine <code>DBConnection</code> with the specified
     * attributes.
     *
     * @param connection The JDBC connection to wrap.
     * @param url        The URL we're connecting to.
     */
    protected DBConnection(Connection connection, String url)
    {
        this(connection, url, null, null);
    }

    /**
     * Creates a Turbine <code>DBConnection</code> with the specified
     * attributes.
     *
     * @param connection The JDBC connection to wrap.
     * @param url        The URL we're connecting to.
     * @param username   The user name we are connecting as
     */
    protected DBConnection(Connection connection, String url, String username)
    {
        this(connection, url, username, null);
    }

    /**
     * Creates a Turbine <code>DBConnection</code> that is part of
     * a pool.
     *
     * @param connection The JDBC connection to wrap.
     * @param url        The URL we're connecting to.
     * @param username   The user name we are connecting as
     * @param pool       The ConnectionPool that this DBConnection belongs to
     */
    protected DBConnection(Connection connection, String url, String username,
                           ConnectionPool pool)
    {
        this.connection = connection;
        this.url = url;
        this.username = username;
        this.pool = pool;
        this.timestamp = System.currentTimeMillis();
        eventListeners = new Vector();
    }

    /**
     * Creates a Turbine <code>DBConnection</code> that is part of
     * a pool.
     *
     * @param connection The JDBC connection to wrap.
     * @param url        The URL we're connecting to.
     * @param username   The user name we are connecting as
     * @param pool       The ConnectionPool that this DBConnection belongs to
     */
    protected DBConnection(PooledConnection pooledConnection, String url, String username,
                           ConnectionPool pool)
    {
        this.pooledConnection = pooledConnection;
        pooledConnection.addConnectionEventListener(this);
        this.url = url;
        this.username = username;
        this.pool = pool;
        this.timestamp = System.currentTimeMillis();
        eventListeners = new Vector();
    }

    /**
     * The finalizer helps prevent <code>ConnectionPool</code> leakage.
     */
    protected void finalize()
        throws Throwable
    {
        if (pool != null)
        {
            // If this DBConnection object is finalized while linked
            // to a ConnectionPool, it means that it was taken from a pool
            // and not returned.  We log this fact, close the underlying
            // Connection, and return it to the ConnectionPool.
            Log.warn( "A DBConnection was finalized, without being returned "
                      + "to the ConnectionPool it belonged to" );

            // Closing the Connection ensures that if anyone tries to use it,
            // an error will occur.
            connection.close();

            // Releasing a new DBConnection object will prevent leaks
            // from the pool
            pool.releaseConnection(pool.getNewConnection());
        }
    }


    /**
     * Links this DBConnection with a ConnectionPool.
     *
     * @param The pool to link to.
     */
    protected void link(ConnectionPool pool)
    {
        if (pool == null)
        {
            throw new NullPointerException
                ("Cannot link to a null database ConnectionPool");
        }

        this.pool = pool;

            //If we use a PooledConnection object, then request from it a Connection
            //object. This forces the PooledConnection to create a wrapper for the
            //physical connection it represents and return a Logical Connection object
            //that is currently in control of it.
        if ( pooledConnection != null )
        {
            try
            {
                if ( connection == null )
                {
                    connection = pooledConnection.getConnection();
                }
            }
            catch (Exception ignore)
            {
                //ignore this
            }
        }

    }

    /**
     * Unlink the DBConnection from it's pool.
     *
     * @param pool The pool to unlink from.
     * @exception Exception Attempt to unlink from another pool.
     */
    protected void unlink(ConnectionPool pool) throws Exception
    {
        if (this.pool != pool && pool != null)
        {
            throw new IllegalArgumentException
                ("Trying to unlink from the wrong pool");
        }
        this.pool = null;

            //if we use a PooledConnection object, then this method will release
            //the physical connection that this PooledConnection represents and
            //allow for its reuse.
        if ( pooledConnection != null )
        {
            if ( connection != null )
                try
                {
                    //this will not close the physical connection, just the
                    //logical connection
                    connection.close();
                }
                catch (Exception ex)
                {
                    //ignore this exception
                }
            connection = null;
        }
    }

    /**
     * Returns the pool this DBConnection came from, or null if
     * it is not linked to any pool
     *
     * @return The pool this connection came from.
     */
    public ConnectionPool getPool()
    {
        return pool;
    }

    /**
     * Add an event listener.
     */
    public void addConnectionEventListener(ConnectionEventListener listener)
    {
        if ( pooledConnection != null )
        {
            pooledConnection.addConnectionEventListener( listener );
        }
    }

    /**
     * Remove an event listener.
     */
    public void removeConnectionEventListener(ConnectionEventListener listener)
    {
        if ( pooledConnection != null )
        {
            pooledConnection.removeConnectionEventListener( listener );
        }
    }

    /**
     * Returns a JDBC connection.
     *
     * @return The database connection.
     */
    public Connection getConnection()
        throws SQLException
    {
            //if we manage an actual PooledConnection, just delegate the call
        if ( pooledConnection != null )
        {
                //if the link method has been called first, then the reference will
                //contain a valid connection object. If not, get it from here.
            if ( connection != null )
            {
                return connection;
            }
            else
            {
                return pooledConnection.getConnection();
            }
        }

            //else, we try to mimic the PooledConnection interface
        if (connection == null)
        {
            throw new SQLException ("Connection object is null!");
        }
        else if (connection.isClosed())
        {
            throw new SQLException ("Connection is closed!");
        }
        else
        {
            return connection;
        }
    }

    /**
     * Returns a long representing the time this connection was
     * created.
     *
     * @return The time in milliseconds that this connection was
     * created.
     */
    public long getTimestamp()
    {
        return timestamp;
    }

    /**
     * Returns the connection URL.
     *
     * @return A String with the connection URL.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Returns the connection user name.
     *
     * @return A String with the connection username.
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Commit the connection.
     */
    public void commit()
    {
        try
        {
            connection.commit();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            Log.error(e);
        }
    }

    /**
     * Roll back the connection.
     */
    public void rollback()
    {
        try
        {
            connection.rollback();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            Log.error(e);
        }
    }

    /**
     * Set the autocommit flag for the connection.
     *
     * @param b True if autocommit should be set to true.
     */
    public void setAutoCommit(boolean b)
    {
        try
        {
            connection.setAutoCommit(b);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            Log.error(e);
        }
    }

    /**
     * Create a Java SQL statement for this connection.
     *
     * @return A new <code>Statement</code>.
     */
    public java.sql.Statement createStatement()
    {
        java.sql.Statement stmt = null;
        try
        {
            stmt = connection.createStatement();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Log.error(e);
        }
        return stmt;
    }

    /**
     * Create a prepared Java SQL statement for this connection.
     *
     * @param sql The SQL statement to prepare.
     * @return A new <code>PreparedStatement</code>.
     */
    public java.sql.PreparedStatement prepareStatement(String sql)
    {
        java.sql.PreparedStatement stmt = null;
        try
        {
            stmt = connection.prepareStatement(sql);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Log.error(e);
        }
        return stmt;
    }

    /**
     * Force the close of this database connection.
     *
     * @exception SQLException The database connection couldn't be closed.
     */
    public void close()
        throws SQLException
    {
            //if using pooledConnection, close this (not the connection
        if ( pooledConnection != null ){
            connection = null;
            pooledConnection.removeConnectionEventListener(this);
            pooledConnection.close();
            return;
        }

        try
        {
            if ( connection != null && !connection.isClosed() )
            {
                connection.close();
            }
        }
        catch (Exception e)
        {
            String errMsg = "Couldn't close database connection: " + e;
            Log.warn(errMsg);
            throw new SQLException(errMsg);
        }
    }

    /**
     * This will be called if the Connection returned by the getConnection
     * method came from a PooledConnection, and the user calls the close()
     * method of this connection object. What we need to do here is to
     * release this DBConnection from our pool...
     */
    public void connectionClosed(ConnectionEvent event) {
        try
        {
            pool.releaseConnection(this);
        }
        catch(Exception ignore)
        {
            //ignore
        }
    }

    /**
     * If a fatal error occurs, close the undelying physical connection so as not to
     * be returned in the future
     */
    public void connectionErrorOccurred(ConnectionEvent e) {
        try {
            System.err.println("CLOSING DOWN CONNECTION DUE TO INTERNAL ERROR");
                //remove this from the listener list because we are no more interested in errors
                //since we are about to close this connection
            ( (PooledConnection) e.getSource() ).removeConnectionEventListener(this);
            try
            {
                //this one will close the underlying physical connection
                ( (PooledConnection) e.getSource() ).close();
            }
            catch (Exception ignore)
            {
                //just ignore
            }

                //this will also close the Logical Connection object so a future
                //call to isClosed() will return true
            try
            {
                connection.close();
            }
            catch (Exception ex)
            {
                //ignore
            }

        }
        catch (Exception ignore) {
            //just ignore
        }
    }


    /*
    public CallableStatement prepareCall(String sql) throws SQLException {
        return connection.prepareCall(sql);
    }
    public String nativeSQL(String sql) throws SQLException {
        return connection.nativeSQL(sql);
    }
    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }
    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }
    public DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData();
    }
    public void setReadOnly(boolean readOnly) throws SQLException {
        connection.setReadOnly(readOnly);
    }
    public boolean isReadOnly() throws SQLException {
        return connection.isReadOnly();
    }
    public void setCatalog(String catalog) throws SQLException {
        connection.setCatalog(catalog);
    }
    public String getCatalog() throws SQLException {
        return connection.getCatalog();
    }
    public void setTransactionIsolation(int level) throws SQLException {
        connection.setTransactionIsolation(level);
    }
    public int getTransactionIsolation() throws SQLException {
        return connection.getTransactionIsolation();
    }
    public SQLWarning getWarnings() throws SQLException {
        return connection.getWarnings();
    }
    public void clearWarnings() throws SQLException {
        connection.clearWarnings();
    }
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency);
    }
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }
    public Map getTypeMap() throws SQLException {
        return connection.getTypeMap();
    }
    public void setTypeMap(Map map) throws SQLException {
        connection.setTypeMap(map);
    }
    */
}
