package org.apache.turbine.util.hibernate;
 
/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.JDBCException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is used to get Hibernate Sessions and may
 * also contain methods (in the future) to get DBConnections
 * or Transactions from JNDI.
 * 
 * @version $Id$
 */
public class HibernateUtils
{
    //~ Static fields/initializers =============================================
    public final static String SESSION_FACTORY = "hibernate/sessionFactory";
    public static final ThreadLocal session = new ThreadLocal();
    private static SessionFactory sf = null;
    private static HibernateUtils me;
    private static Log log = LogFactory.getLog(HibernateUtils.class);

    static {
        try
        {
            me = new HibernateUtils();
        }
        catch (Exception e)
        {
            log.fatal("Error occurred initializing HibernateUtils");
            e.printStackTrace();
        }
    }

    //~ Constructors ===========================================================

    private HibernateUtils() throws HibernateException, JDBCException
    {}

    //~ Methods ================================================================

    public static Session currentSession() throws PersistenceException
    {
        Session s = (Session) session.get();

        if (s == null)
        {
            s = PersistenceManager.openSession();
            if (log.isDebugEnabled())
            {
                log.debug("Opened hibernate session.");
            }

            session.set(s);
        }

        return s;
    }

    public static void closeSession() throws HibernateException, JDBCException
    {
        Session s = (Session) session.get();
        session.set(null);

        if (s != null)
        {
            if (s.isOpen())
            {
                s.flush();
                s.close();

                if (log.isDebugEnabled())
                {
                    log.debug("Closed hibernate session.");
                }
            }
        }
        else
        {
            log.warn("Hibernate session was inadvertently already closed.");

        }
    }
}
