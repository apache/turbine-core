package org.apache.turbine.util.uri;

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

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.ServerData;

/**
 * This class can convert a simple link into a turbine relative
 * URL. It should be used to convert references for images, style
 * sheets and similar references.
 *
 * The resulting links have no query data or path info. If you need
 * this, use TurbineURI or TemplateURI.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 *
 */

public class DataURI
        extends BaseURI
        implements URIConstants
{
    /**
     * Empty C'tor. Uses Turbine.getDefaultServerData().
     *
     */
    public DataURI()
    {
        super();
    }

    /**
     * Constructor with a RunData object
     *
     * @param runData A RunData object
     */
    public DataURI(RunData runData)
    {
        super(runData);
    }

    /**
     * Constructor, set explicit redirection
     *
     * @param runData A RunData object
     * @param redirect True if redirection allowed.
     */
    public DataURI(RunData runData, boolean redirect)
    {
        super(runData, redirect);
    }

    /**
     * Constructor with a ServerData object
     *
     * @param serverData A ServerData object
     */
    public DataURI(ServerData serverData)
    {
        super(serverData);
    }

    /**
     * Constructor, set explicit redirection
     *
     * @param serverData A ServerData object
     * @param redirect True if redirection allowed.
     */
    public DataURI(ServerData serverData, boolean redirect)
    {
        super(serverData, redirect);
    }


    /**
     * Content Tool wants to be able to turn the encoding
     * of the servlet container off. After calling this method,
     * the encoding will not happen any longer.
     */
    public void clearResponse()
    {
        setResponse(null);
    }

    /**
     * Builds the URL with all of the data URL-encoded as well as
     * encoded using HttpServletResponse.encodeUrl(). The resulting
     * URL is absolute; it starts with http/https...
     *
     * <p>
     * <code><pre>
     * TurbineURI tui = new TurbineURI (data, "UserScreen");
     * tui.addPathInfo("user","jon");
     * tui.getAbsoluteLink();
     * </pre></code>
     *
     *  The above call to absoluteLink() would return the String:
     *
     * <p>
     * http://www.server.com/servlets/Turbine/screen/UserScreen/user/jon
     *
     * @return A String with the built URL.
     */
    public String getAbsoluteLink()
    {
        StringBuffer output = new StringBuffer();

        getSchemeAndPort(output);
        getContextAndScript(output);

        if (hasReference())
        {
            output.append('#');
            output.append(getReference());
        }

        //
        // Encode Response does all the fixup for the Servlet Container
        //
        return encodeResponse(output.toString());
    }

    /**
     * Builds the URL with all of the data URL-encoded as well as
     * encoded using HttpServletResponse.encodeUrl(). The resulting
     * URL is relative to the webserver root.
     *
     * <p>
     * <code><pre>
     * TurbineURI tui = new TurbineURI (data, "UserScreen");
     * tui.addPathInfo("user","jon");
     * tui.getRelativeLink();
     * </pre></code>
     *
     *  The above call to absoluteLink() would return the String:
     *
     * <p>
     * /servlets/Turbine/screen/UserScreen/user/jon
     *
     * @return A String with the built URL.
     */
    public String getRelativeLink()
    {
        StringBuffer output = new StringBuffer();

        getContextAndScript(output);

        if (hasReference())
        {
            output.append('#');
            output.append(getReference());
        }

        //
        // Encode Response does all the fixup for the Servlet Container
        //
        return encodeResponse(output.toString());
    }

    /**
     * toString() simply calls getAbsoluteLink. You should not use this in your
     * code unless you have to. Use getAbsoluteLink.
     *
     * @return This URI as a String
     *
     */
    public String toString()
    {
        return getAbsoluteLink();
    }
}
