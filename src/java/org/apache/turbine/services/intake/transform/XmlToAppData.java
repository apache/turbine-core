package org.apache.turbine.services.intake.transform;

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
 * @version $Id$
 */
public class XmlToAppData extends DefaultHandler
{
    /** Logging */
    private static Log log = LogFactory.getLog(XmlToAppData.class);

    private AppData app;
    private XmlGroup currGroup;
    private XmlField currField;
    private Rule currRule;
    private String currElement;

    private static SAXParserFactory saxFactory;

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
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
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
     * @return an InputSource for the database.dtd file
     */
    public InputSource resolveEntity(String publicId, String systemId)
    {
        return new DTDResolver().resolveEntity(publicId, systemId);
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
        if ("rule".equals(currElement) && cdata.length() > 0)
        {
            currRule.setMessage(cdata);
        }
        if ("required-message".equals(currElement) && cdata.length() > 0)
        {
            log.warn("The required-message element is deprecated!  " +
                    "You should update your intake.xml file to use the " +
                    "'required' rule instead.");
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
        log.warn("Parser Exception: " +
                "Line " + spe.getLineNumber() +
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
        log.error("Parser Exception: " +
                "Line " + spe.getLineNumber() +
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
        log.fatal("Parser Exception: " +
                "Line " + spe.getLineNumber() +
                " Row: " + spe.getColumnNumber() +
                " Msg: " + spe.getMessage());
    }
}
