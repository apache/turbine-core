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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.turbine.services.BaseService;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.db.IDGeneratorFactory;
import org.apache.turbine.util.db.IDBroker;
import org.apache.turbine.util.db.adapter.DB;
import org.apache.turbine.util.db.adapter.DBFactory;
import org.apache.turbine.util.db.map.DatabaseMap;
import org.apache.turbine.util.db.map.TableMap;
import org.apache.commons.configuration.Configuration;


/**
 * Turbine's default implmentation of {@link MapBrokerService}.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:magnus@handtolvur.is">Magnús Þór Torfason</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public class TurbineMapBrokerService extends BaseService
    implements MapBrokerService
{
    /** The global cache of database maps */
    private Map dbMaps;

    /** Default database map */
    private String defaultMap;

    /**
     * Initializes the service.
     */
    public void init()
    {
        dbMaps = (Map)new HashMap();
        Configuration configuration = getConfiguration();

        // Get the value for the default map, but if there
        // isn't a value than fall back to the standard
        // "default" value.
        defaultMap = configuration.getString(DEFAULT_MAP, DEFAULT);

        // indicate that the service initialized correctly
        setInit(true);
    }

    /**
     * Return the default map.
     */
    public String getDefaultMap()
    {
        return defaultMap;
    }

    /**
     * Shuts down the service.
     *
     * This method halts the IDBroker's daemon thread in all of
     * the DatabaseMap's.
     */
    public void shutdown()
    {
        Iterator maps = dbMaps.values().iterator();
        while ( maps.hasNext() )
        {
            DatabaseMap map = (DatabaseMap) maps.next();
            IDBroker idBroker = map.getIDBroker();
            if (idBroker != null)
            {
                idBroker.stop();
            }
        }
    }

    /**
     * Returns the default database map information.
     *
     * @return A DatabaseMap.
     * @throws TurbineException Any exceptions caught during procssing will be
     *         rethrown wrapped into a TurbineException.
     */
    public DatabaseMap getDatabaseMap()
        throws TurbineException
    {
        return getDatabaseMap(defaultMap);
    }

    /**
     * Returns the database map information. Name relates to the name
     * of the connection pool to associate with the map.
     *
     * @param name The name of the <code>DatabaseMap</code> to
     * retrieve.
     * @return The named <code>DatabaseMap</code>.
     * @throws TurbineException Any exceptions caught during procssing will be
     *         rethrown wrapped into a TurbineException.
     */
    public DatabaseMap getDatabaseMap(String name)
        throws TurbineException
    {
            if ( name == null )
        {
            throw new TurbineException ("DatabaseMap name was null!");
        }

        // Quick (non-sync) check for the map we want.
        DatabaseMap map = (DatabaseMap)dbMaps.get(name);
        if ( map == null )
        {
            // Map not there...
            synchronized( dbMaps )
            {
                // ... sync and look again to avoid race condition.
                map = (DatabaseMap)dbMaps.get(name);
                if ( map == null )
                {
                    // Still not there.  Create and add.
                    map = new DatabaseMap(name);

                    // Add info about IDBroker's table.
                    setupIdTable(map);
                    // setup other id generators
                    try
                    {
                        DB db = DBFactory.create(
                            getDatabaseProperty(name, "driver") );
                        for (int i = 0; i < IDGeneratorFactory.ID_GENERATOR_METHODS.length;
                             i++)
                        {
                            map.addIdGenerator(IDGeneratorFactory.ID_GENERATOR_METHODS[i],
                                               IDGeneratorFactory.create(db));
                        }
                    }
                    catch (java.lang.InstantiationException e)
                    {
                        throw new TurbineException(e);
                    }

                    dbMaps.put(name, map);
                }
            }
        }
        return map;
    }

    /**
     * Returns the specified property of the given database, or the empty
     * string if no value is set for the property.
     *
     * @param db   The name of the database whose property to get.
     * @param prop The name of the property to get.
     * @return     The property's value.
     */
    private static final String getDatabaseProperty(String db, String prop)
    {
        return TurbineResources.getString
            ( new StringBuffer("database.")
                .append(db)
                .append('.')
                .append(prop)
                .toString(), "" );
    }

    /**
     * Setup IDBroker's table information within given database map.
     *
     * This method should be called on all new database map to ensure that
     * IDBroker functionality is available in all databases userd by the
     * application.
     *
     * @param map the DataBaseMap to setup.
     */
    private void setupIdTable(DatabaseMap map)
    {
        map.setIdTable("ID_TABLE");
        TableMap tMap = map.getIdTable();
        tMap.addPrimaryKey("ID_TABLE_ID", new Integer(0));
        tMap.addColumn("TABLE_NAME", new String(""));
        tMap.addColumn("NEXT_ID", new Integer(0));
        tMap.addColumn("QUANTITY", new Integer(0));
    }
}








