package org.apache.turbine.services.freemarker;

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

// FreeMarker Stuff
import freemarker.template.SimpleHash;
import freemarker.template.Template;

// Java Classes
import java.io.IOException;

import javax.servlet.ServletRequest;

// Turbine Stuff
import org.apache.turbine.services.Service;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * Implementations of the FreeMarkerService interface.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @version $Id$
 * @deprecated
 */
public interface FreeMarkerService
    extends Service
{
    /**
     * The name used to specify this service in
     * TurbineResources.properties.
     */
    public static final String SERVICE_NAME = "FreeMarkerService";

    /**
     * The name used as a key in TurbineInfo when storing the
     * Context.
     */
    public static final String CONTEXT = "FREEMARKER_CONTEXT";

    /**
     * Create a context needed by the FreeMarker template.  This
     * method just returns an SimpleHash with the request parameters
     * copied into a model called request.
     *
     * @return SimpleHash which can be used as the model for a
     * template.
     */
    public SimpleHash getContext();

    /**
     * Create a context needed by the FreeMarker template.  This
     * method just returns an SimpleHash with the request parameters
     * copied into a model called request.
     *
     * @param req A ServletRequest.
     * @return SimpleHash which can be used as the model for a
     * template.
     */
    public SimpleHash getContext(ServletRequest req);

    /**
     * Create a context from the RunData object.  Values found in
     * RunData are copied into the modelRoot under similar names as
     * they can be found in RunData. e.g. data.serverName,
     * data.parameters.form_field_name
     * data.acl.permissions.can_write_file.  Some default links are
     * also made available under links.
     *
     * @param data The Turbine RunData object.
     * @return a SimpleHash populated with RunData data.
     */
    public SimpleHash getContext(RunData data);

    /**
     * Process the request and fill in the template with the values
     * you set in the WebContext.
     *
     * @param context A SimpleHash with the context.
     * @param templateName A String with the filename of the template.
     * @param cache True if the parsed template should be cached.
     * @return The processed template as a String.
     * @throws TurbineException Any exception trown while processing will be
     *         wrapped into a TurbineException and rethrown.
     */
    public String handleRequest(SimpleHash context,
                                String templateName,
                                boolean cache)
        throws TurbineException;

    /**
     * Gets the base path for the FreeMarker templates.
     *
     * @return The base path for the FreeMarker templates.
     */
    public String getBasePath();

    /**
     * Return a FreeMarker template. It will not be added to the
     * cache.
     *
     * @param templateName A String with the name of the template.
     * @return A Template.
     * @exception IOException, if there was an I/O problem.
     */
    public Template getNonCachedTemplate(String templateName)
        throws IOException;

    /**
     * Return a FreeMarker template from the cache.  If the template
     * has not been cached yet, it will be added to the cache.
     *
     * @param templateName A String with the name of the template.
     * @return A Template.
     */
    public Template getCachedTemplate(String templateName);
}
