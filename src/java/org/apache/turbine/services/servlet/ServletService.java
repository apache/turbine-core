package org.apache.turbine.services.servlet;

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

import java.net.URL;
import java.io.InputStream;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.apache.turbine.services.Service;

/**
 * <p>This interface exposes methods of the runner context in order 
 * resolve or get access to external resources</p>
 *
 * @author <a href="mailto:ekkerbj@netscape.net">Jeff Brekke</a>
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 */
public interface ServletService extends Service
{
    /**
     * The service identifier
     */
    public String SERVICE_NAME = "ServletService";
    
    /**
     * Returns an URL object for a given URI string.
     *
     * @param uri the URI to resolve as an URL
     * @return an URL object or null is the uri is malformed or
     * can't be resolved
     */
    public URL getResource( String uri );
    
    /**
     * Same as getResource except that it returns an InputStream
     *
     * @param uri the URI to resolve
     * @return an InputStream on the URI content or null
     */
    public InputStream getResourceAsStream( String uri );
    
    /**
     * Returns the complete filesystem path for a 
     * given URI
     *
     * @param uri the URI to resolve
     * @return the full system path of this URI
     */
    public String getRealPath( String uri );
    
    /**
     * Returns the servlet config used by this
     * Turbine web application.
     *
     * @return turbine servlet config
     */
    public ServletConfig getServletConfig();

    /**
     * Returns the servlet context used by this
     * Turbine web application.
     *
     * @return turbine servlet context
     */
    public ServletContext getServletContext();

    /**
     * Returns the server scheme for this
     * Turbine application. This will either
     * be http or https.
     *
     * @return String
     */
    public String getServerScheme();
    
    /**
     * Returns the server name that this
     * Turbine application is running
     * on.
     *
     * @return String
     */
    public String getServerName();
    
    /**
     * Returns the port that this Turbine
     * application is running through
     * on the server.
     *
     * @return String
     */
    public String getServerPort();
    
    /**
     * Returns the context path for this
     * Turbine application.
     *
     * @return String
     */
    public String getContextPath();
}
