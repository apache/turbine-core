package org.apache.turbine.services.xslt;

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

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.w3c.dom.Node;

/**
 * Implementation of the Turbine XSLT Service.  It transforms xml with a given
 * xsl file.  XSL stylesheets are compiled and cached (if the property in
 * TurbineResources.properties is set) to improve speeds.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:rubys@us.ibm.com">Sam Ruby</a>
 * @author <a href="thomas.vandahl@tewisoft.de">Thomas Vandahl</a>
 * @version $Id$
 */
public class TurbineXSLTService
        extends TurbineBaseService
        implements XSLTService
{
    /**
     * Property to control the caching of StyleSheetRoots.
     */
    protected boolean caching = false;

    /**
     * Path to style sheets used for tranforming well-formed
     * XML documents. The path is relative to the webapp context.
     */
    protected String path;

    /**
     * Cache of compiled StyleSheetRoots.
     */
    private LRUMap cache = new LRUMap(20);

    /**
     * Factory for producing templates and null transformers
     */
    private static TransformerFactory tfactory;

    /**
     * Initialize the TurbineXSLT Service.  Load the path to search for
     * xsl files and initiates the cache.
     */
    public void init()
            throws InitializationException
    {
        Configuration conf = getConfiguration();

        path = conf.getString(STYLESHEET_PATH, STYLESHEET_PATH_DEFAULT);
        caching = conf.getBoolean(STYLESHEET_CACHING, STYLESHEET_CACHING_DEFAULT);

        tfactory = TransformerFactory.newInstance();

        setInit(true);
    }

    /**
     * Try to create a valid url object from the style parameter.
     *
     * @param style the xsl-Style
     * @return a <code>URL</code> object or null if the style sheet could not be found
     */
    private URL getStyleURL(String style)
    {
        StringBuffer sb = new StringBuffer(128);

        if (StringUtils.isNotEmpty(path))
        {
            if (path.charAt(0) != '/')
            {
                sb.append('/');
            }

            sb.append(path);

            if (path.charAt(path.length() - 1) != '/')
            {
                sb.append('/');
            }
        }
        else
        {
            sb.append('/');
        }

        // we chop off the existing extension
        int colon = style.lastIndexOf(".");

        if (colon > 0)
        {
            sb.append(style.substring(0, colon));
        }
        else
        {
            sb.append(style);
        }

        sb.append(".xsl");

        return TurbineServlet.getResource(sb.toString());
    }

    /**
     * Compile Templates from an input URL.
     */
    protected Templates compileTemplates(URL source) throws Exception
    {
        StreamSource xslin = new StreamSource(source.openStream());
        Templates root = tfactory.newTemplates(xslin);
        return root;
    }

    /**
     * Retrieves Templates.  If caching is switched on the
     * first attempt is to load the Templates from the cache.
     * If caching is switched of or if the Stylesheet is not found
     * in the cache a new StyleSheetRoot is compiled from an input
     * file.
     * <p>
     * This method is synchronized on the xsl cache so that a thread
     * does not attempt to load a StyleSheetRoot from the cache while
     * it is still being compiled.
     */
    protected Templates getTemplates(String xslName) throws Exception
    {
        synchronized (cache)
        {
            if (caching && cache.containsKey(xslName))
            {
                return (Templates) cache.get(xslName);
            }

            URL url = getStyleURL(xslName);

            if (url == null)
            {
                return null;
            }

            Templates sr = compileTemplates(url);

            if (caching)
            {
                cache.put(xslName, sr);
            }

            return sr;
        }

    }

    /**
     * Transform the input source into the output source using the given style
     *
     * @param style the stylesheet parameter
     * @param in the input source
     * @param out the output source
     * @param params XSLT parameter for the style sheet
     *
     * @throws TransformerException
     */
    protected void transform(String style, Source in, Result out, Map params)
            throws TransformerException, IOException, Exception
    {
        Templates styleTemplate = getTemplates(style);

        Transformer transformer = (styleTemplate != null)
                ? styleTemplate.newTransformer()
                : tfactory.newTransformer();

        if (params != null)
        {
            for (Iterator it = params.entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) it.next();
                transformer.setParameter(String.valueOf(entry.getKey()), entry.getValue());
            }
        }

        //      Start the transformation and rendering process
        transformer.transform(in, out);
    }

    /**
     * Execute an xslt
     */
    public void transform(String xslName, Reader in, Writer out)
            throws Exception
    {
        Source xmlin = new StreamSource(in);
        Result xmlout = new StreamResult(out);

        transform(xslName, xmlin, xmlout, null);
    }

    /**
     * Execute an xslt
     */
    public String transform(String xslName, Reader in)
            throws Exception
    {
        StringWriter sw = new StringWriter();
        transform(xslName, in, sw);
        return sw.toString();
    }

    /**
     * Execute an xslt
     */
    public void transform (String xslName, Node in, Writer out)
            throws Exception
    {
        Source xmlin = new DOMSource(in);
        Result xmlout = new StreamResult(out);

        transform(xslName, xmlin, xmlout, null);
    }

    /**
     * Execute an xslt
     */
    public String transform (String xslName, Node in)
            throws Exception
    {
        StringWriter sw = new StringWriter();
        transform(xslName, in, sw);
        return sw.toString();
    }

    /**
     * Execute an xslt
     */
    public void transform(String xslName, Reader in, Writer out, Map params)
            throws Exception
    {
        Source xmlin = new StreamSource(in);
        Result xmlout = new StreamResult(out);

        transform(xslName, xmlin, xmlout, params);
    }

    /**
     * Execute an xslt
     */
    public String transform(String xslName, Reader in, Map params) throws Exception
    {
        StringWriter sw = new StringWriter();
        transform(xslName, in, sw, params);
        return sw.toString();
    }

    /**
     * Execute an xslt
     */
    public void transform (String xslName, Node in, Writer out, Map params)
            throws Exception
    {
        Source xmlin = new DOMSource(in);
        Result xmlout = new StreamResult(out);

        transform(xslName, xmlin, xmlout, params);
    }

    /**
     * Execute an xslt
     */
    public String transform (String xslName, Node in, Map params)
            throws Exception
    {
        StringWriter sw = new StringWriter();
        transform(xslName, in, sw, params);
        return sw.toString();
    }

}
