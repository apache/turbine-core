package org.apache.turbine.util.db;

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

import java.util.Vector;

import org.apache.turbine.om.peer.BasePeer;
import org.apache.turbine.services.db.TurbineDB;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.db.pool.DBConnection;

import com.workingdogs.village.QueryDataSet;

/**
 * This class can be used to retrieve a large result set from a
 * database query.  The query is started and then a small number of
 * rows are returned at any one time.  The LargeSelect is meant to be
 * placed into the Session, so that it can be used in response to
 * several related requests.
 *
 * It was written as an example in response to questions regarding
 * Village's and Turbine's ability to handle large queries.  The
 * author hoped that the people who were asking for the feature
 * would comment and improve the code, but has received no comments.
 * As the author has had no need for such a class, it remains
 * untested and in all likelihood contains several bugs.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @version $Id$
 */
public class LargeSelect
    implements Runnable
{
    private int miniblock;
    private int memoryLimit;
    private String name;
    private int blockBegin = 0;
    private int blockEnd;
    private int currentlyFilledTo = -1;
    private String query;
    private String dbName;
    private DBConnection db = null;
    private QueryDataSet qds = null;
    private Vector results = null;
    private Thread thread = null;
    private boolean killThread = false;
    private int position;

    /**
     * Key which may be used to store and retrieve the LargeSelect
     * from the session.
     */
    public static final String DEFAULT_NAME = "default.large.select";

    /**
     * Creates a LargeSelect whose results are broken up into smaller
     * chunks of approximately 1/100 the maximum number allowed in
     * memory or 100, whichever is smaller.  The LargeSelect is stored
     * in the session under the default name "default.large.select".
     *
     * @param criteria Object used by BasePeer to build the query.
     * @param memoryLimit Maximum number of rows to be in memory at
     * one time.
     * @exception Exception, a generic exception.
     */
    public LargeSelect(Criteria criteria,
                       int memoryLimit)
        throws Exception
    {
        miniblock = Math.min(100, memoryLimit/100+1);
        name = DEFAULT_NAME;
        init(name, criteria, memoryLimit);
    }

    /**
     * Creates a LargeSelect whose results are broken up into smaller
     * chunks of approximately 1/100 the maximum number allowed in
     * memory or 100, whichever is smaller.
     *
     * @param name Key used to store the LargeSelect in the session.
     * @param criteria Object used by BasePeer to build the query.
     * @param memoryLimit Maximum number of rows to be in memory at
     * one time.
     * @exception Exception, a generic exception.
     */
    public LargeSelect(String name,
                       Criteria criteria,
                       int memoryLimit)
        throws Exception
    {
        miniblock = Math.min(100, memoryLimit/100);
        init(name, criteria, memoryLimit);
    }

    /**
     * Creates a LargeSelect whose results are returned a page at a
     * time.  The LargeSelect is stored in the session under the
     * default name "default.large.select".
     *
     * @param criteria Object used by BasePeer to build the query.
     * @param memoryLimit Maximum number of rows to be in memory at
     * one time.
     * @param pageSize Number of rows to return in one block.
     * @exception Exception, a generic exception.
     */
    public LargeSelect(Criteria criteria,
                       int memoryLimit,
                       int pageSize)
        throws Exception
    {
        miniblock = pageSize;
        name = DEFAULT_NAME;
        init(name, criteria, memoryLimit);
    }

    /**
     * Creates a LargeSelect whose results are returned a page at a
     * time.
     *
     * @param name Key used to store the LargeSelect in the session.
     * @param criteria Object used by BasePeer to build the query.
     * @param memoryLimit Maximum number of rows to be in memory at
     * one time.
     * @param pageSize Number of rows to return in one block.
     * @exception Exception, a generic exception.
     */
    public LargeSelect(String name,
                       Criteria criteria,
                       int memoryLimit,
                       int pageSize)
        throws Exception
    {
        miniblock = pageSize;
        init(name, criteria, memoryLimit);
    }

    /**
     * Called by the constructors to start the query.
     *
     * @param name Key used to store the LargeSelect in the session.
     * @param criteria Object used by BasePeer to build the query.
     * @param memoryLimit Maximum number of rows to be in memory at
     * one time.
     * @exception Exception, a generic exception.
     */
    private void init(String name,
                      Criteria criteria,
                      int memoryLimit)
        throws Exception
    {
        this.memoryLimit = memoryLimit;
        this.name = name;
        query = BasePeer.createQueryString(criteria);
        dbName = criteria.getDbName();
        blockEnd = blockBegin + memoryLimit - 1;
        startQuery(miniblock);
    }

    /**
     * Gets the next block of rows.
     *
     * @return A Vector of query results.
     * @exception Exception, a generic exception.
     */
    public Vector getNextResults()
        throws Exception
    {
        return getResults(position, miniblock);
    }

    /**
     * Gets a block of rows which have previously been retrieved.
     *
     * @return a Vector of query results.
     * @exception Exception, a generic exception.
     */
    public Vector getPreviousResults()
        throws Exception
    {
        return getResults(position-2*miniblock, miniblock);
    }

    /**
     * Gets a block of rows starting at a specified row.  Number of
     * rows in the block was specified in the constructor.
     *
     * @param start The starting row.
     * @return a Vector of query results.
     * @exception Exception, a generic exception.
     */
    public Vector getResults(int start) throws Exception
    {
        return getResults(start, miniblock);
    }

    /**
     * Gets a block of rows starting at a specified row and containing
     * a specified number of rows.
     *
     * @param start The starting row.
     * @param size The number of rows.
     * @return a Vector of query results.
     * @exception Exception, a generic exception.
     */
    synchronized public Vector getResults(int start,
                                          int size)
        throws Exception
    {
        if (size > memoryLimit)
        {
            throw new Exception("Memory limit does not permit a range this large.");
        }

        // Request was for a block of rows which should be in progess.
        // If the rows have not yet been returned, wait for them to be
        // retrieved.
        if ( start >= blockBegin  &&  (start+size-1) < blockEnd )
        {
            while ( (start+size-1) > currentlyFilledTo )
            {
                Thread.currentThread().sleep(500);
            }
        }

        // Going in reverse direction, trying to limit db hits so
        // assume user might want at least 2 sets of data.
        else if ( start < blockBegin  &&  start >= 0 )
        {
            stopQuery();
            if (memoryLimit >= 2*size)
            {
                blockBegin = start - size;
                blockEnd = blockBegin + memoryLimit - 1;
                startQuery(size);
            }
            else
            {
                blockBegin = start;
                blockEnd = blockBegin + memoryLimit - 1;
                startQuery(size);
            }
        }

        // Assume we are moving on, do not retrieve any records prior
        // to start.
        else if ( (start+size-1) >= blockEnd )
        {
            stopQuery();
            blockBegin = start;
            blockEnd = blockBegin + memoryLimit - 1;
            startQuery(size);
        }

        else
        {
            throw new Exception("parameter configuration not accounted for");
        }

        Vector returnResults = new Vector(size);
        for (int i=(start-blockBegin); i<(start-blockBegin+size); i++)
        {
            returnResults.addElement( results.elementAt(i) );
        }
        position = start+size;
        return returnResults;
    }


    /**
     * A background thread that retrieves the rows.
     */
    public void run()
    {
        int size = miniblock;
        try
        {
            // Get a connection to the db.
            db = TurbineDB.getConnection(dbName);
            Connection connection = db.getConnection();

            // Execute the query.
            qds = new QueryDataSet( connection, query );

            // Continue getting rows until the memory limit is
            // reached, all results have been retrieved, or the rest
            // of the results have been determined to be irrelevant.
            while( !killThread &&
                   !qds.allRecordsRetrieved() &&
                   currentlyFilledTo + size  <= blockEnd )
            {
                if ( (currentlyFilledTo + size)  > blockEnd )
                {
                    size = blockEnd - currentlyFilledTo;
                }
                Vector tempResults = BasePeer.getSelectResults( qds,
                                                                size,
                                                                false);
                for (int i=0; i<tempResults.size(); i++)
                {
                    results.addElement( tempResults.elementAt(i) );
                }
                currentlyFilledTo += miniblock;
            }
        }
        catch (Exception e)
        {
            Log.error(e);
        }
        finally
        {
            try
            {
                if (qds != null)
                {
                    qds.close();
                }
                TurbineDB.releaseConnection(db);
            }
            catch(Exception e)
            {
                Log.error("Release of connection failed.", e);
            }
        }
    }

    /**
     * Starts a new thread to retrieve the result set.
     *
     * @param initialSize The initial size for each block.
     * @exception Exception, a generic exception.
     */
    private void startQuery(int initialSize)
        throws Exception
    {
        miniblock = initialSize;
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Used to stop filling the memory with the current block of
     * results, if it has been determined that they are no longer
     * relevant.
     *
     * @exception Exception, a generic exception.
     */
    private void stopQuery()
        throws Exception
    {
        killThread = true;
        while (thread.isAlive())
        {
            Thread.currentThread().sleep(100);
        }
        killThread = false;
    }
}
