package org.apache.turbine.services.xslt;

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

import java.io.Reader;
import java.io.Writer;
import org.apache.turbine.services.Service;
import org.w3c.dom.Node;

/**
 * The Turbine XSLT Service is used to transform xml with a xsl stylesheet.
 * The service makes use of the Xalan xslt engine available from apache.
 * 
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 */
public interface XSLTService
    extends Service
{
    public static final String SERVICE_NAME = "XSLTService";

    /**
     * Uses an xsl file to transform xml input from a reader and writes the
     * output to a writer.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param in The reader that passes the xml to be transformed
     * @param out The writer for the transformed output
     */
    public void transform (String xslName, Reader in, Writer out) throws Exception;
    
    /**
     * Uses an xsl file to transform xml input from a reader and returns a
     * string containing the transformed output.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param in The reader that passes the xml to be transformed
     */
    public String transform (String xslName, Reader in) throws Exception;
    
    /**
     * Uses an xsl file to transform xml input from a DOM note and writes the
     * output to a writer.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param in The DOM Node to be transformed
     * @param out The writer for the transformed output
     */
    public void transform (String xslName, Node in, Writer out) throws Exception;
    
    /**
     * Uses an xsl file to transform xml input from a DOM note and returns a
     * string containing the transformed output.
     *
     * @param xslName The name of the file that contains the xsl stylesheet.
     * @param out The writer for the transformed output
     */
    public String transform (String xslName, Node in) throws Exception;
}
