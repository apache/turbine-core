package org.apache.turbine.services.webmacro;

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

import java.io.OutputStream;
import org.apache.turbine.services.Service;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;
import org.webmacro.FastWriter;
import org.webmacro.NotFoundException;
import org.webmacro.Template;
import org.webmacro.servlet.WebContext;

/**
 * Implementations of the WebMacroService interface.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @version $Id$
 * @deprecated
 */
public interface WebMacroService
    extends Service
{
    /**
     * The name of this service.  Used to retrieve it from the broker.
     */
    public static final String SERVICE_NAME = "WebMacroService";

    /**
     * The key for a WebMacro context.
     */
    public static final String WEBMACRO_CONTEXT = "WEBMACRO_CONTEXT";

    /**
     * Process the request and fill in the template with the values
     * you set in the WebContext.
     *
     * @param wc A WebContext.
     * @param template A String with the filename of the template.
     * @return The process template as a String.
     * @exception TurbineException Error processing request.
     */
    public String handleRequest(WebContext wc,
                                String template)
        throws TurbineException;


    /**
     * Process the request and fill in the template with the values
     * you set in the WebContext.
     *
     * @param wc The populated context.
     * @param filename The file name of the template.
     * @param out A stream to write the processed template to.
     * @exception Exception Error processing request.
     */
    public void handleRequest(WebContext wc,
                              String filename,
                              OutputStream out)
        throws Exception;

    /**
     * Process the request and fill in the template with the values
     * you set in the WebContext.
     *
     * @param wc A WebContext.
     * @param filename A String with the filename of the template.
     * @param out A Writer where we will write the process template as
     * a String.
     * @exception Exception Error processing request.
     */
    public void handleRequest(WebContext wc,
                              String template,
                              FastWriter out)
        throws Exception;

    /**
     * Create an empty WebContext object.
     *
     * @return An empty WebContext object.
     */
    public WebContext getContext();

    /**
     * Create a WebContext from the RunData object.  Adds a pointer to
     * the RunData object to the WC so that RunData is available in
     * the templates.
     *
     * @param data The Turbine RunData object.
     * @return A clone of the WebContext needed by WebMacro.
     */
    public WebContext getContext(RunData data);

    /**
     * Return a template from WebMacro.
     *
     * @param filename A String with the name of the template.
     * @return A Template.
     * @exception NotFoundException The template could not be found.
     */
    public Template getTemplate(String template)
        throws NotFoundException;
}
