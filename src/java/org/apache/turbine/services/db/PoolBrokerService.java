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

import org.apache.turbine.util.db.pool.DBConnection;
import org.apache.turbine.util.db.adapter.DB;
import org.apache.turbine.services.Service;

/**
 * This service provides database connection pooling to Turbine applications.
 *
 * The service can manage a number of connection pools. Each pool is related
 * to a specific database, identified by it's driver class name, url, username
 * and password. The pools may be defined in TurbineResources.properties
 * file, or created at runtime using 
 * {@link #registerPool(String,String,String,String,String)} method.
 *
 * <p> You can use {@link #getConnection(String)} to acquire a
 * {@link org.apache.turbine.util.db.pool.DBConnection} object, which in
 * turn can be used to create <code>java.sql.Statement</code> objects.
 *
 * <p>When you are done using the <code>DBConnection</code> you <strong>must</strong>
 * return it to the pool using {@link #releaseConnection(DBConnection)} method.
 * This method call is often placed in <code>finally</code> clause of a <code>try /
 * catch</code> statement, to ensure that the connection is always returned
 * to the pool.<br>
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public interface PoolBrokerService extends Service
{
    /** the name of the service */
    public static final String SERVICE_NAME = "PoolBrokerService";

    /** the name of the default pool */
    public static final String DEFAULT = "default";

    /**
     * This method returns a DBConnection from the default pool.
     *
     * @return The requested connection.
     * @throws TurbineException Any exceptions caught during processing will be
     *         rethrown wrapped into a TurbineException.
     */
    public DBConnection getConnection()
        throws Exception;

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
     * @throws TurbineException Any exceptions caught during processing will be
     *         rethrown wrapped into a TurbineException.
     */
    public DBConnection getConnection(String name)
        throws Exception;


    /**
     * This method returns a DBConnecton using the given parameters.
     *
     * @param driver The fully-qualified name of the JDBC driver to use.
     * @param url The URL of the database from which the connection is
     * desired.
     * @param username The name of the database user.
     * @param password The password of the database user.
     * @return A DBConnection.
     * @throws TurbineException Any exceptions caught during processing will be
     *         rethrown wrapped into a TurbineException.
     *
     * @deprecated Database parameters should not be specified each
     * time a DBConnection is fetched from the service.
     */
    public DBConnection getConnection(String driver,
                                      String url,
                                      String username,
                                      String password)
        throws Exception;

    /**
     * Release a connection back to the database pool.
     *
     * @throws TurbineException Any exceptions caught during processing will be
     *         rethrown wrapped into a TurbineException.
     * @exception Exception A generic exception.
     */
    public void releaseConnection(DBConnection dbconn)
        throws Exception;

    /**
     * This method registers a new pool using the given parameters.
     *
     * @param name The name of the pool to register.
     * @param driver The fully-qualified name of the JDBC driver to use.
     * @param url The URL of the database to use.
     * @param username The name of the database user.
     * @param password The password of the database user.
     *
     * @throws TurbineException Any exceptions caught during processing will be
     *         rethrown wrapped into a TurbineException.
     */
    public void registerPool( String name,
                              String driver,
                              String url,
                              String username,
                              String password )
        throws Exception;


    /**
     * Returns the database adapter for the default connection pool.
     *
     * @return The database adapter.
     * @throws TurbineException Any exceptions caught during processing will be
     *         rethrown wrapped into a TurbineException.
     */
    public DB getDB()
        throws Exception;

    /**
     * Returns database adapter for a specific connection pool.
     *
     * @param name A pool name.
     * @return     The corresponding database adapter.
     * @throws TurbineException Any exceptions caught during processing will be
     *         rethrown wrapped into a TurbineException.
     */
    public DB getDB(String name)
        throws Exception;
}
