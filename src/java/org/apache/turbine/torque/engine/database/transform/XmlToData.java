package org.apache.turbine.torque.engine.database.transform;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import org.apache.turbine.torque.engine.database.model.Column;
import org.apache.turbine.torque.engine.database.model.Database;
import org.apache.turbine.torque.engine.database.model.Table;
import org.apache.xerces.framework.XMLParser;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A Class that is used to parse an input
 * xml schema file and creates and AppData java structure.
 * It uses apache Xerces to do the xml parsing.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:fedor.karpelevitch@home.com">Fedor Karpelevitch</a>
 * @version $Id$
 */
public class XmlToData extends DefaultHandler implements EntityResolver
{
    private Database database;
    private String errorMessage;
    private Vector data;
    private String dtdFileName;
    private File dtdFile;
    private InputSource dataDTD;

    /**
     * Default custructor
     */
    public XmlToData(Database database, String dtdFilePath) throws
            MalformedURLException, IOException
    {
        this.database = database;
        dtdFile = new File(dtdFilePath);
        this.dtdFileName = "file://" + dtdFile.getName();
        dataDTD = new InputSource(dtdFile.toURL().openStream());
        errorMessage = "";
    }


    /**
     *
     */
    public List parseFile(String xmlFile)
    {
        try
        {
            data = new Vector();

            SAXParser parser = new SAXParser();

            // set the Resolver for the DTD
            parser.setEntityResolver(this);

            // We don't use an external content handler - we use this object
            parser.setContentHandler(this);

            // Validate the input file
            parser.setFeature
                ("http://apache.org/xml/features/validation/dynamic", true);
            parser.setFeature("http://xml.org/sax/features/validation", true);

            parser.setErrorHandler(this);

            FileReader fr = new FileReader (xmlFile);
            BufferedReader br = new BufferedReader (fr);
            try
            {
                InputSource is = new InputSource (br);
                parser.parse(is);
            }
            finally
            {
                br.close();
            }
        }
        catch (Exception e)
        {
            //System.out.println("Error : "+e);
            e.printStackTrace();
        }
        if ( errorMessage.length() > 0 )
        {
            System.out.println("ERROR in data file!!!\n" + errorMessage);
        }

        return data;
    }



    /**
     * Handles opening elements of the xml file.
     */
    public void startElement(String uri, String localName, String rawName,
                             Attributes attributes)
    {
        try
        {
            if (rawName.equals("dataset"))
            {
                //ignore <dataset> for now.
            }
            else
            {
                Table table = database.getTableByJavaName(rawName);
                Vector columnValues = new Vector();
                for (int i=0; i<attributes.getLength(); i++)
                {
                    Column col = table
                        .getColumnByJavaName(attributes.getLocalName(i));
                    String value = attributes.getValue(i);
                    columnValues.add(new ColumnValue(col, value));
                }
                data.add(new DataRow(table, columnValues));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Warning callback.
     *
     * @exception spe The parse exception that caused the callback to be
     *                invoked.
     */
    public void warning(SAXParseException spe)
    {
        System.out.println("Warning Line: " + spe.getLineNumber() +
                           " Row: " + spe.getColumnNumber() +
                           " Msg: " + spe.getMessage());
    }

    /**
     * Error callback.
     *
     * @exception spe The parse exception that caused the callback to be
     *                invoked.
     */
    public void error(SAXParseException spe)
    {
        System.out.println("Error Line: " + spe.getLineNumber() +
                           " Row: " + spe.getColumnNumber() +
                           " Msg: " + spe.getMessage());
    }

    /**
     * Fatal error callback.
     *
     * @exception spe The parse exception that caused the callback to be
     *                invoked.
     */
    public void fatalError(SAXParseException spe)
    {
        System.out.println("Fatal Error Line: " + spe.getLineNumber() +
                           " Row: " + spe.getColumnNumber() +
                           " Msg: " + spe.getMessage());
    }


    /**
     * called by the XML parser
     *
     * @return an InputSource for the database.dtd file
     */
    public InputSource resolveEntity(String publicId, String systemId)
    {
        if (dataDTD != null &&
            dtdFileName.equals(systemId))
        {
            System.out.println("Resolver: used " + dtdFile.getPath());
            return dataDTD;
        }
        else
        {
            System.out.println("Resolver: used " + systemId);
            return getInputSource(systemId);
        }
    }

    /**
     * get an InputSource for an URL String
     *
     * @param urlString
     * @return an InputSource for the URL String
     */
    public InputSource getInputSource(String urlString)
    {
        try
        {
            URL url = new URL(urlString);
            return new InputSource(url.openStream());
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return new InputSource();
    }

    public class DataRow
    {
        private Table table;
        private Vector columnValues;

        public DataRow(Table table, Vector columnValues)
        {
            this.table = table;
            this.columnValues = columnValues;
        }

        public Table getTable()
        {
            return table;
        }

        public Vector getColumnValues()
        {
            return columnValues;
        }
    }

    public class ColumnValue
    {
        private Column col;
        private String val;

        public ColumnValue(Column col, String val)
        {
            this.col = col;
            this.val = val;
        }

        public Column getColumn()
        {
            return col;
        }

        public String getValue()
        {
            return val;
        }
    }
}
