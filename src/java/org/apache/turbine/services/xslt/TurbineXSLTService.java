package org.apache.turbine.services.xslt;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import java.io.File;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Hashtable;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.servlet.TurbineServlet;

/**
 * Implementation of the Turbine XSLT Service.  It transforms xml with a given
 * xsl file.  XSL stylesheets are compiled and cached (if the property in
 * TurbineResources.properties is set) to improve speeds.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:rubys@us.ibm.com">Sam Ruby</a>
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
    protected Hashtable cache = new Hashtable();

    /**
     * Factory for producing templates and null transformers
     */
    private static TransformerFactory tfactory;

    /**
     * Initialize the TurbineXSLT Service.  Load the path to search for
     * xsl files and initiates the cache.
     */
    public void init()
    {
        if (getInit())
        {
            return;
        }

        path = getConfiguration().getString(
                TurbineServices.SERVICE_PREFIX +
                XSLTService.SERVICE_NAME + ".path");

        path = TurbineServlet.getRealPath(path);

        if (!path.endsWith("/") && !path.endsWith("\\"))
        {
            path = path + File.separator;
        }

        caching = getConfiguration().getBoolean(
                TurbineServices.SERVICE_PREFIX +
                XSLTService.SERVICE_NAME + ".cache");

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
    public void transform(String xslName, org.w3c.dom.Node in, Writer out)
            throws Exception
    {
        Source xmlin = new DOMSource(in);
        Result xmlout = new StreamResult(out);

        transform(xslName, xmlin, xmlout);
    }

    /**
     * Execute an xslt
     */
    public String transform(String xslName, org.w3c.dom.Node in)
            throws Exception
    {
        StringWriter sw = new StringWriter();
        transform(xslName, in, sw);
        return sw.toString();
    }

}
