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
import org.apache.turbine.services.BaseService;


/**
 * Turbine's default implementation of {@link PoolBrokerService}.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:magnus@handtolvur.is">Magnús Þór Torfason</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 * @deprecated use org.apache.torque.Torque instead
 */
public class TurbinePoolBrokerService extends BaseService
        implements PoolBrokerService
{
    /**
     * Initialize the connection pool broker.
     */
    public void init()
    {
        // indicate that the service initialized correctly
        setInit(true);
    }

    /**
     * Return the default pool.
     */
    public String getDefaultDB()
    {
        return Torque.getDefaultDB();
    }

    /**
     * Release the database connections for all pools on service shutdown.
     */
    public synchronized void shutdown()
    {
        Torque.shutdown();
    }

    /**
     * This method returns a DBConnection from the default pool.
     *
     * @return The requested connection.
     * @throws Exception A generic exception.
     */
    public Connection getConnection() throws Exception
    {
        return Torque.getConnection();
    }

    /**
     * This method returns a DBConnection from the pool with the
     * specified name.
     *
     * @param name The name of the pool to get a connection from.
     * @return     The requested connection.
     * @throws Exception A generic exception.
     */
    public Connection getConnection(String name) throws Exception
    {
        return Torque.getConnection(name);
    }

    /**
     * Release a connection back to the database pool.  <code>null</code>
     * references are ignored.
     *
     * @throws Exception A generic exception.
     */
    public void releaseConnection(Connection dbconn) throws Exception
    {
        Torque.closeConnection(dbconn);
    }

    /**
     * Returns the database adapter for the default connection pool.
     *
     * @return The database adapter.
     * @throws Exception A generic exception.
     */
    public DB getDB() throws Exception
    {
        return Torque.getDB(Torque.getDefaultDB());
    }

    /**
     * Returns database adapter for a specific connection pool.
     *
     * @param name A pool name.
     * @return     The corresponding database adapter.
     * @throws Exception A generic exception.
     */
    public DB getDB(String name) throws Exception
    {
        return Torque.getDB(name);
    }
}
