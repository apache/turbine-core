package org.apache.turbine.util.db.adapter;

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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.turbine.services.resources.TurbineResources;

import org.apache.turbine.util.Log;

/**
 * This class creates different DB objects based on the database
 * driver that is provided.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:ralf@reswi.ruhr.de">Ralf Stranzenbach</a>
 * @version $Id$
 */
public class DBFactory
{
    // List of registered drivers.
    private static Hashtable drivers = null;

    // This static code creates the list of possible drivers and adds
    // the "NO DATABASE" adaptor to this list.  After all the
    // TurbineResources is queried to get a list of JDBC drivers and
    // their associated adaptors.
    static
    {
        drivers = new Hashtable();

        // Add the null db driver.
        registerDriver("", DBNone.class);

        Enumeration adaptors =
            TurbineResources.getVector("database.adaptor").elements();
        while (adaptors.hasMoreElements())
        {
            String adaptor = (String)adaptors.nextElement();
            String driver =
                TurbineResources.getString("database.adaptor." + adaptor);

            Class c = null;
            try
            {
                c = Class.forName("org.apache.turbine.util.db.adapter." +
                                  adaptor);
            }
            catch (ClassNotFoundException ign1)
            {
                try
                {
                    c = Class.forName(adaptor);
                }
                catch (ClassNotFoundException ign2)
                {
                    Log.error(ign2);
                }
            }
            if ((c != null) && (driver != null))
            {
                registerDriver(driver, c);
            }
        }
    }

    /**
     * Try to register the class of a database driver at the factory.
     * This concept allows for dynamically adding new database drivers
     * using the configuration files instead of changing the codebase.
     *
     * @param driver The fully-qualified name of the JDBC driver to create.
     * @param dc     The named JDBC driver.
     */
    private static void registerDriver(String driver, Class dc)
    {
        if (!drivers.containsKey(driver))
            // Add this new driver class to the list of known drivers.
            drivers.put(driver, dc);
    }

    /**
     * Creates an instance of the Turbine database adapter associated with the
     * specified JDBC driver.
     *
     * NOTE: This method used to be <code>protected</code>.  I'd like to try
     * to get it back that way ASAP.  I had to change its access level since
     * it is called by <code>ConnectionPool</code>, and these two
     * classes are no longer in the same package.  -Daniel <dlr@collab.net>
     *
     * @param driver The fully-qualified name of the JDBC driver to create.
     * @return       An instance of a Turbine database adapter.
     */
    public static DB create(String driver)
        throws InstantiationException
    {
        Class dc = (Class)drivers.get(driver);

        if (dc != null)
        {
            try
            {
                // Create an instantiation of the driver.
                DB retVal = (DB)dc.newInstance();
                retVal.setJDBCDriver(driver);
                return retVal;
            }
            catch (IllegalAccessException e)
            {
                throw new InstantiationException(
                    "Driver " + driver + " not instantiated.");
            }
        }
        else
        {
            throw new InstantiationException(
                "Database type " + driver + " not implemented.");
        }
    }
}
