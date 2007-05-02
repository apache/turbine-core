package org.apache.turbine.services.intake.transform;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.services.intake.xmlmodel.AppData;
import org.apache.turbine.services.intake.xmlmodel.Rule;
import org.apache.turbine.services.intake.xmlmodel.XmlField;
import org.apache.turbine.services.intake.xmlmodel.XmlGroup;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A Class that is used to parse an input
 * xml schema file and creates and AppData java structure.
 * It uses apache Xerces to do the xml parsing.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class XmlToAppData extends DefaultHandler
{
    /** Logging */
    private static Log log = LogFactory.getLog(XmlToAppData.class);

    private AppData app = null;
    private XmlGroup currGroup = null;
    private XmlField currField = null;
    private Rule currRule = null;

    private StringBuffer charBuffer = null;

    private static SAXParserFactory saxFactory = null;

    static
    {
        saxFactory = SAXParserFactory.newInstance();
        saxFactory.setValidating(true);
    }

    /**
     * Creates a new instance of the Intake XML Parser
     */
    public XmlToAppData()
    {
        app = new AppData();
    }

    /**
     * Parses a XML input file and returns a newly created and
     * populated AppData structure.
     *
     * @param xmlFile The input file to parse.
     * @return AppData populated by <code>xmlFile</code>.
     * @throws ParserConfigurationException When a serious parser configuration problem occurs.
     * @throws SAXException When a problem parsing the XML file occurs.
     * @throws IOException When an I/O error occurs.
     */
    public AppData parseFile(String xmlFile)
            throws ParserConfigurationException, SAXException, IOException
    {
        SAXParser parser = saxFactory.newSAXParser();

        FileReader fr = new FileReader(xmlFile);
        BufferedReader br = new BufferedReader(fr);
        try
        {
            InputSource is = new InputSource(br);
            parser.parse(is, this);
        }
        finally
        {
            br.close();
        }

        return app;
    }

    /**
     * EntityResolver implementation. Called by the XML parser
     *
     * @param publicId The public identifer, which might be null.
     * @param systemId The system identifier provided in the XML document.
     * @return an InputSource for the database.dtd file
     */
    public InputSource resolveEntity(String publicId, String systemId)
    {
        return new DTDResolver().resolveEntity(publicId, systemId);
    }

    /**
     * Handles opening elements of the xml file.
     * @param uri The current namespace URI.
     * @param localName The local name (without prefix), or the empty string if Namespace processing is not being performed.
     * @param rawName The qualified name (with prefix), or the empty string if qualified names are not available.
     * @param attributes The specified or defaulted attributes.
     */
    public void startElement(String uri, String localName,
                             String rawName, Attributes attributes)
    {
        charBuffer = new StringBuffer();

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
     * Handles the character data, which we are using to specify the error message.
     * @param mesgArray The characters.
     * @param start The start position in the character array.
     * @param length The number of characters to use from the character array.
     */
    public void characters(char[] mesgArray, int start, int length)
    {
        charBuffer.append(mesgArray, start, length);
    }

    /**
     * Handles closing Elements of the XML file
     * @param uri The current namespace URI.
     * @param localName The local name (without prefix), or the empty string if Namespace processing is not being performed.
     * @param rawName The qualified name (with prefix), or the empty string if qualified names are not available.
     */
    public void endElement(String uri, String localName,
            String rawName)
    {
        if (charBuffer.length() > 0)
        {
            String cdata = charBuffer.toString().trim();

            if ("rule".equals(rawName))
            {
                currRule.setMessage(cdata);
            }
            else if ("required-message".equals(rawName))
            {
                log.warn("The required-message element is deprecated!  " +
                        "You should update your intake.xml file to use the " +
                        "'required' rule instead.");
                currField.setIfRequiredMessage(cdata);
            }
        }
    }

    /**
     * Callback function for the xml parser to give warnings.
     *
     * @param spe a <code>SAXParseException</code> value
     */
    public void warning(SAXParseException spe)
    {
        StringBuffer sb = new StringBuffer(64);
        sb.append("Parser Exception: Line ");
        sb.append(spe.getLineNumber());
        sb.append(" Row ");
        sb.append(spe.getColumnNumber());
        sb.append(" Msg: ");
        sb.append(spe.getMessage());

        log.warn(sb.toString());
    }

    /**
     * Callback function for the xml parser to give errors.
     *
     * @param spe a <code>SAXParseException</code> value
     */
    public void error(SAXParseException spe)
    {
        StringBuffer sb = new StringBuffer(64);
        sb.append("Parser Exception: Line ");
        sb.append(spe.getLineNumber());
        sb.append(" Row ");
        sb.append(spe.getColumnNumber());
        sb.append(" Msg: ");
        sb.append(spe.getMessage());

        log.error(sb.toString());
    }

    /**
     * Callback function for the xml parser to give fatalErrors.
     *
     * @param spe a <code>SAXParseException</code> value
     */
    public void fatalError(SAXParseException spe)
    {
        StringBuffer sb = new StringBuffer(64);
        sb.append("Parser Exception: Line ");
        sb.append(spe.getLineNumber());
        sb.append(" Row ");
        sb.append(spe.getColumnNumber());
        sb.append(" Msg: ");
        sb.append(spe.getMessage());

        log.fatal(sb.toString());
    }
}
