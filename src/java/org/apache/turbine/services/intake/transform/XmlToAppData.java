package org.apache.turbine.services.intake.transform;

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
import java.io.FileReader;
import java.io.Reader;
import org.apache.turbine.services.intake.xmlmodel.AppData;
import org.apache.turbine.services.intake.xmlmodel.Rule;
import org.apache.turbine.services.intake.xmlmodel.XmlField;
import org.apache.turbine.services.intake.xmlmodel.XmlGroup;
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
 * @author <a href="mailto:jmcnally@collab.net>John McNally</a>
 * @version $Id$
 */
public class XmlToAppData extends DefaultHandler
{
    private AppData app;
    private XmlGroup currGroup;
    private XmlField currField;
    private Rule currRule;
    private String currElement;

    /**
     * Default custructor
     */
    public XmlToAppData()
    {
        app = new AppData();
    }


    /**
     * Parse and xml input file and returns a newly
     * created and populated AppData structure
     */
    public AppData parseFile(String xmlFile)
        throws Exception
    {
        return parseFile(xmlFile, false);
    }
        
    /**
     * Parse and xml input file and returns a newly
     * created and populated AppData structure
     */
    public AppData parseFile(String xmlFile, boolean skipValidation)
        throws Exception
    {
        SAXParser parser = new SAXParser();
        
        // set the Resolver for the database DTD
        DTDResolver dtdResolver = new DTDResolver();
        parser.setEntityResolver(dtdResolver);

        // We don't use an external content handler - we use this object
        parser.setContentHandler(this);
        parser.setErrorHandler(this);
        
        // Validate the input file
        parser.setFeature(
            "http://apache.org/xml/features/validation/dynamic", true);
        parser.setFeature("http://xml.org/sax/features/validation", true);

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

        return app;
    }



    /**
     * Handles opening elements of the xml file.
     */
    public void startElement(String uri, String localName, 
                             String rawName, Attributes attributes)
    {
        currElement = rawName;
        if (rawName.equals("input-data"))
        {
            app.loadFromXML(attributes);
        }
        else if (rawName.equals("group"))
        {
            currGroup = app.addGroup(attributes);
        }
        else if (rawName.equals("field"))
        {
            currField = currGroup.addField(attributes);
        }
        else if (rawName.equals("rule"))
        {
            currRule = currField.addRule(attributes);
        }
    }


    /**
     * Handles the character data, which we are using to specify the
     * error message.
     */
    public void characters(char[] mesgArray, int start, int length)
    {
        String cdata = new String(mesgArray, start, length).trim();
        if ( "rule".equals(currElement) && cdata.length() > 0) 
        {
            currRule.setMessage(cdata);
        }
        if ( "required-message".equals(currElement) && cdata.length() > 0) 
        {
            currField.setIfRequiredMessage(cdata);
        }
    }

    /**
     * Callback function for the xml parser to give warnings.
     *
     * @param spe a <code>SAXParseException</code> value
     */
    public void warning(SAXParseException spe)
    {
        System.out.println("Warning Line: " + spe.getLineNumber() +
                           " Row: " + spe.getColumnNumber() +
                           " Msg: " + spe.getMessage());
    }

    /**
     * Callback function for the xml parser to give errors.
     *
     * @param spe a <code>SAXParseException</code> value
     */
    public void error(SAXParseException spe)
    {
        System.out.println("Error Line: " + spe.getLineNumber() +
                           " Row: " + spe.getColumnNumber() +
                           " Msg: " + spe.getMessage());
    }

    /**
     * Callback function for the xml parser to give fatalErrors.
     *
     * @param spe a <code>SAXParseException</code> value
     */
    public void fatalError(SAXParseException spe)
    {
        System.out.println("Fatal Error Line: " + spe.getLineNumber() +
                           " Row: " + spe.getColumnNumber() +
                           " Msg: " + spe.getMessage());
    }
}
