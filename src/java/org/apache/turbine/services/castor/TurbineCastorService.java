package org.apache.turbine.services.castor;

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

// Java Stuff
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Properties;

// Castor Stuff
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.DatabaseNotFoundException;
import org.exolab.castor.jdo.JDO;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.QueryException;
import org.exolab.castor.jdo.TransactionNotInProgressException;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.util.Logger;

// Turbine Stuff
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.util.Log;

// SAX Stuff
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * This is a Service that offers access to the object to relational
 * mapping from <a href="http://castor.exolab.org">Castor</a>.
 *
 * Here's an example of how you might use it:<br>
 *
 * <code><pre>
 * TurbineServices ts = TurbineServices.getInstance();
 * CastorService cs = (CastorService) ts.getService (CastorService.SERVICE_NAME);
 * Database db = cs.{@link #getDatabase};
 * db.begin();
 * ...
 * db.commit();
 * db.close();
 * </pre></code>
 *
 * The following properties are needed to configure this service:<br>
 *
 * <code><pre>
 * services.CastorService.classname=org.apache.turbine.services.castor.TurbineCastorService
 * services.CastorService.properties.logfile=/tmp/castor.log
 * services.CastorService.properties.logprefix=turbinecastor
 * services.CastorService.properties.databasefile=/tmp/database.xml
 * services.CastorService.properties.databasename=turbine
 * </pre></code>
 *
 * <dl>
 * <dt>classname</dt><dd>the classname of this service</dd>
 * <dt>logfile</dt><dd>the path to a writable file. Castor uses its own log file</dd>
 * <dt>logprefix</dt><dd>the prefix used in the logfile to distinguish from
 * Castor log entry and this Service. This is a recommended property</dd>
 * <dt>databasefile</dt><dd>the path to a readable file defining the mappings of the
 * java objects an the underlying tables/columns</dd>
 * <dt>databasename</dt><dd>references a name of a database definition tag used in
 * the mapping file</dd>
 * </dl>
 *
 * @author <a href="mailto:Giacomo.Pati@pwr.ch">Giacomo Pati</a>
 * @author <a href="mailto:celkins@scardini.com">Christopher Elkins</a>
 * @version $Id$
 * @deprecated
 */
public class TurbineCastorService
    extends TurbineBaseService
    implements CastorService
{
    /**
     * The name of the database to use (references the database tag in
     * the mapping file, not the real database name).
     */
    private String databasename = null;

    /** The mapping file for the database. */
    private String databasefile = null;

    /** Castor logger. */
    private PrintWriter logger = null;

    /** Castor JDO object. */
    private JDO jdo = null;

    /**
     * Called the first time the Service is used.
     */
    public void init()
        throws InitializationException
    {
        try
        {
            initCastor();
            setInit(true);
        }
        catch (Exception e)
        {
            throw new InitializationException("TurbineCastorService failed to initalize", e);
        }
    }

    /**
     * Checks the properties and hands the mapping file to Castor.
     *
     * @exception IOException, if there were problems to get the
     * logfile.
     * @exception MappingException, if the mapping file is invalid.
     * @exception Exception, if no databasefile or databasename is
     * given in the properties.
     */
    private void initCastor ()
        throws IOException,
               MappingException,
               Exception
    {
        Properties props = getProperties();

        String logprefix = props.getProperty (LOGPREFIX_PROPERTY);
        if (logprefix == null)
        {
            logprefix = DEFAULT_LOGPREFIX;
        }
        String logfile = props.getProperty (LOGFILE_PROPERTY);
        if (logfile == null)
        {
            Log.warn ("CastorService no LogFile property specified");
        }
        else
        {
            logger = new Logger (new FileWriter (logfile)).setPrefix(logprefix);
        }

        databasename = props.getProperty (DATABASENAME_PROPERTY);
        if (databasename == null)
        {
            throw new Exception ("TurbineCastor: missing databasename propertiy");
        }

        databasefile = props.getProperty (DATABASEFILE_PROPERTY);
        if (databasefile == null)
        {
            throw new Exception ("TurbineCastor: missing databasefile propertiy");
        }

        // FIXME: Upon the release and inclusion of Castor 0.8.9,
        // remove this block of code and un-comment the block in
        // getJDO().
        JDO.loadConfiguration (new InputSource (databasefile),
                               new LocalResolver(databasefile, logger),
                               getClass().getClassLoader());
    }

    /**
     * Gets you the PrintWriter object Castor is using for logging.
     *
     * @return PrintWriter logger object of Castor.
     */
    public PrintWriter getCastorLogger ()
    {
        return logger;
    }

    /**
     * Gets a JDO object initialized to the database mentioned in the
     * property databasename.
     *
     * @return JDO object initialized to the database mentioned in the
     * property databasename.
     */
    public JDO getJDO ()
    {
        if (jdo == null) {
            jdo = new JDO (databasename);
            /*
              // FIXME: Un-comment this block of code upon the release
              // and inclusion of Castor 0.8.9.
              jdo.setConfiguration(databasefile);
              jdo.setEntityResolver(new LocalResolver(databasefile,
                                                      logger));
              jdo.setClassLoader(getClass().getClassLoader());
            */
            jdo.setLogWriter (logger);
        }
        return jdo;
    }

    /**
     * Gets a Castor Database object in the context of the defined
     * environment.
     *
     * @return Database object
     * @exception DatabaseNotFoundException, if attempted to open a
     * database that does not exist.
     * @exception PersistenceException, if database access failed.
     */
    public Database getDatabase ()
        throws DatabaseNotFoundException,
               PersistenceException
    {
        return this.getJDO().getDatabase();
    }

    /**
     * Internal class to resolve entities in the database mapping
     * file. Included definition for object mappings must be relative
     * to the database mapping file.
     */
    private class LocalResolver
        implements EntityResolver
    {
        /** Absolute path to the mapping files. */
        private String prefix = "unknown";

        /** A PrintWriter to log activity during resolving. */
        private PrintWriter logger = null;

        /**
         * Constructor of this EntityResolver
         *
         * @param databasefile A String with the path to the main
         * mapping file.
         * @param logger A PrintWriter logger to log activity while
         * resolving entities.
         */
        public LocalResolver (String databasefile,
                              PrintWriter logger)
        {
            super ();
            int i = databasefile.lastIndexOf (File.separator);
            this.prefix = databasefile.substring (0, i+1);
            this.logger = logger;
        }

        /**
         * Resolves entities in the main database mapping file. Castor
         * uses this method to get an InputSource object for an
         * included object mapping definition.
         *
         * @param publicId A String with the public id.
         * @param systemId A String with the system id.
         * @exception FileNotFoundException, if file was not found.
         */
        public InputSource resolveEntity (String publicId,
                                          String systemId)
            throws FileNotFoundException
        {
            return new InputSource (new FileReader (prefix + systemId));
        }
    }
}
