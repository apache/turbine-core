package org.apache.turbine.services.schedule;

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

import com.workingdogs.village.Record;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.apache.torque.TorqueException;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;
import org.apache.turbine.util.ObjectUtils;
import org.apache.turbine.util.db.map.TurbineMapBuilder;

/**
 * Peer class for JobEntry database access.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 */
public class JobEntryPeer extends BasePeer
{
    /** Get the MapBuilder. */
    private static final TurbineMapBuilder mapBuilder =
        (TurbineMapBuilder) getMapBuilder("org.apache.turbine.util.db.map.TurbineMapBuilder");

    /** Name of the table. */
    private static final String  TABLE_NAME = mapBuilder.getTableJobentry();

    // Table name + column name.
    public static final String OID = mapBuilder.getJobentry_JobId();
    public static final String SECOND = mapBuilder.getJobentry_Second();
    public static final String MINUTE = mapBuilder.getJobentry_Minute();
    public static final String HOUR = mapBuilder.getJobentry_Hour();
    public static final String WEEKDAY = mapBuilder.getJobentry_Weekday();
    public static final String DAY_OF_MONTH = mapBuilder.getJobentry_DayOfMonth();
    public static final String TASK = mapBuilder.getJobentry_Task();
    public static final String EMAIL = mapBuilder.getJobentry_Email();
    public static final String PROPERTY = mapBuilder.getJobentry_Property();

    /**
     * Update an existing Job.
     *
     * @param Criteria The information to update.
     * @exception Exception, a generic exception.
     */
    public static void doUpdate(Criteria criteria)
        throws TorqueException
    {
        Criteria selectCriteria = new Criteria(2);
        selectCriteria.put( OID, criteria.remove(OID) );
        BasePeer.doUpdate( selectCriteria, criteria );
    }

    /**
     * Called from the SchedulerService init() to batch load Jobs into
     * the queue.
     *
     * @param Criteria The information for the where.
     * @return Vector of JobEntries.
     * @exception Exception, a generic exception.
     */
    public static List doSelect(Criteria criteria)
        throws TorqueException
    {
        addSelectColumns(criteria);

        List rows = BasePeer.doSelect(criteria);
        List results = new ArrayList();

        try
        {
            // Populate the object(s).
            for ( int i=0; i<rows.size(); i++ )
            {
                Record rec = (Record)rows.get(i);
                int oid = rec.getValue(1).asInt();
                int sec = rec.getValue(2).asInt();
                int min = rec.getValue(3).asInt();
                int hr  = rec.getValue(4).asInt();
                int wd  = rec.getValue(5).asInt();
                int d_m = rec.getValue(6).asInt();
                String task = rec.getValue(7).asString();
                String email = rec.getValue(8).asString();
                byte[] objectData = (byte[]) rec.getValue(9).asBytes();
                Hashtable tempHash = (Hashtable) ObjectUtils.deserialize(objectData);

                JobEntry je = new JobEntry(sec, min, hr, wd, d_m, task);
                je.setPrimaryKey(oid);
                je.setEmail(email);
                je.setProperty(tempHash);
                je.setModified(false);

                results.add(je);
            }
        }
        catch (Exception ex)
        {
            throw new TorqueException(ex);
        }
        return results;
    }

    /**
     * Perform a SQL <code>insert</code>, handling connection details
     * internally.
     */
    public static ObjectKey doInsert(Criteria criteria)
        throws TorqueException
    {
        criteria.setDbName(mapBuilder.getDatabaseMap().getName());
        return BasePeer.doInsert(criteria);
    }

    /**
     * Method to do inserts.  This method is to be used during a transaction,
     * otherwise use the doInsert(Criteria) method.  It will take care of
     * the connection details internally.
     */
    public static ObjectKey doInsert(Criteria criteria, Connection dbCon)
        throws TorqueException
    {
        criteria.setDbName(mapBuilder.getDatabaseMap().getName());
        return BasePeer.doInsert(criteria, dbCon);
    }

    /**
     * Add all the columns needed to create a new object.
     */
    protected static void addSelectColumns(Criteria criteria)
        throws TorqueException
    {
        criteria.addSelectColumn(OID)
            .addSelectColumn(SECOND)
            .addSelectColumn(MINUTE)
            .addSelectColumn(HOUR)
            .addSelectColumn(WEEKDAY)
            .addSelectColumn(DAY_OF_MONTH)
            .addSelectColumn(TASK)
            .addSelectColumn(EMAIL)
            .addSelectColumn(PROPERTY);
    }

    /**
     * Retrieve a JobEntry based on its id.
     *
     * @param oid The JobEntry int id.
     * @return A JobEntry.
     * @exception Exception, a generic exception.
     */
    public static JobEntry getJob(int oid)
        throws Exception
    {
        JobEntry je = null;

        Criteria c = new Criteria(9);
        c.add(OID,new Integer(oid));

        List results = JobEntryPeer.doSelect(c);

        if (results != null)
        {
            je = (JobEntry) results.get(0);
        }
        return je;
    }
}
