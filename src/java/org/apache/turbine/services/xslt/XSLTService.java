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

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import org.apache.turbine.services.Service;
import org.w3c.dom.Node;

/**
 * The Turbine XSLT Service is used to transform xml with a xsl stylesheet.
 * The service makes use of the Xalan xslt engine available from apache.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="thomas.vandahl@tewisoft.de">Thomas Vandahl</a>
 * @version $Id$
 */
public interface XSLTService
        extends Service
{
    /** Service name */
    String SERVICE_NAME = "XSLTService";

    /** Name of the Style sheet path property */
    String STYLESHEET_PATH = "path";

    /** Default value of the Style sheet path */
    String STYLESHEET_PATH_DEFAULT = "/";

    /** Property for caching the stylesheets */
    String STYLESHEET_CACHING = "cache";

    /** Default for caching the stylesheets */
    boolean STYLESHEET_CACHING_DEFAULT = false;

    /**
     * Uses an xsl file to transform xml input from a reader and writes the
     * output to a writer.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param in The reader that passes the xml to be transformed
     * @param out The writer for the transformed output
     */
    void transform(String xslName, Reader in, Writer out) throws Exception;

    /**
     * Uses an xsl file to transform xml input from a reader and returns a
     * string containing the transformed output.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param in The reader that passes the xml to be transformed
     */
    String transform(String xslName, Reader in) throws Exception;

    /**
     * Uses an xsl file to transform xml input from a DOM note and writes the
     * output to a writer.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param in The DOM Node to be transformed
     * @param out The writer for the transformed output
     */
    void transform(String xslName, Node in, Writer out) throws Exception;

    /**
     * Uses an xsl file to transform xml input from a DOM note and returns a
     * string containing the transformed output.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param out The writer for the transformed output
     */
    String transform(String xslName, Node in) throws Exception;

    /**
     * Uses an xsl file to transform xml input from a reader and writes the
     * output to a writer.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param in The reader that passes the xml to be transformed
     * @param out The writer for the transformed output
     * @param params A set of parameters that will be forwarded to the XSLT
     */
    void transform(String xslName, Reader in, Writer out, Map params) throws Exception;

    /**
     * Uses an xsl file to transform xml input from a reader and returns a
     * string containing the transformed output.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param in The reader that passes the xml to be transformed
     * @param params A set of parameters that will be forwarded to the XSLT
     */
    String transform(String xslName, Reader in, Map params) throws Exception;

    /**
     * Uses an xsl file to transform xml input from a DOM note and writes the
     * output to a writer.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param in The DOM Node to be transformed
     * @param out The writer for the transformed output
     * @param params A set of parameters that will be forwarded to the XSLT
     */
    void transform(String xslName, Node in, Writer out, Map params) throws Exception;

    /**
     * Uses an xsl file to transform xml input from a DOM note and returns a
     * string containing the transformed output.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param out The writer for the transformed output
     * @param params A set of parameters that will be forwarded to the XSLT
     */
    String transform(String xslName, Node in, Map params) throws Exception;
}
