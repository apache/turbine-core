package org.apache.turbine.services.xslt;

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

import java.io.File;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.configuration.Configuration;

import org.apache.commons.lang.StringUtils;

import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;

import org.w3c.dom.Node;

/**
 * Implementation of the Turbine XSLT Service.  It transforms xml with a given
 * xsl file.  XSL stylesheets are compiled and cached (if the property in
 * TurbineResources.properties is set) to improve speeds.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:rubys@us.ibm.com">Sam Ruby</a>
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
    protected Map cache = new HashMap();

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

        path = Turbine.getRealPath(conf.getString(STYLESHEET_PATH, null));

        if (StringUtils.isNotEmpty(path))
        {
            if (!path.endsWith("/") && !path.endsWith ("\\"))
            {
                path = path + File.separator;
            }
        }

        caching = conf.getBoolean(STYLESHEET_CACHING);

        tfactory = TransformerFactory.newInstance();

        setInit(true);
    }

    /**
     * Get a valid and existing filename from a template name.
     * The extension is removed and replaced with .xsl.  If this
     * file does not exist the method attempts to find default.xsl.
     * If it fails to find default.xsl it returns null.
     */
    protected String getFileName(String templateName)
    {
        // First we chop of the existing extension
        int colon = templateName.lastIndexOf(".");
        if (colon > 0)
        {
            templateName = templateName.substring(0, colon);
        }

        // Now we try to find the file ...
        File f = new File(path + templateName + ".xsl");
        if (f.exists())
        {
            return path + templateName + ".xsl";
        }
        else
        {
            // ... or the default file
            f = new File(path + "default.xsl");
            if (f.exists())
            {
                return path + "default.xsl";
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * Compile Templates from an input file.
     */
    protected Templates compileTemplates(String source) throws Exception
    {
        StreamSource xslin = new StreamSource(new File(source));
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

            String fn = getFileName(xslName);

            if (fn == null) return null;

            Templates sr = compileTemplates(fn);

            if (caching)
            {
                cache.put(xslName, sr);
            }

            return sr;
        }

    }

    protected void transform(String xslName, Source xmlin, Result xmlout)
            throws Exception
    {
        Templates sr = getTemplates(xslName);
        Transformer transformer;


        // If there is no stylesheet we just echo the xml
        if (sr == null)
        {
            transformer = tfactory.newTransformer();
        }
        else
        {
            transformer = sr.newTransformer();
        }

        transformer.transform(xmlin, xmlout);
    }

    /**
     * Execute an xslt
     */
    public void transform(String xslName, Reader in, Writer out)
            throws Exception
    {
        Source xmlin = new StreamSource(in);
        Result xmlout = new StreamResult(out);

        transform(xslName, xmlin, xmlout);
    }

    /**
     * Execute an xslt
     */
    public String transform(String xslName, Reader in) throws Exception
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

        transform(xslName, xmlin, xmlout);
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

}
