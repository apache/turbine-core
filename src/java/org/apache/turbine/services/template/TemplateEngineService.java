package org.apache.turbine.services.template;

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

import java.util.Hashtable;

/**
 * This is the interface that all template engine services must adhere
 * to. This includes the Velocity, WebMacro, FreeMarker, and JSP
 * services.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$ */
public interface TemplateEngineService
{
    static final String TEMPLATE_EXTENSIONS = "template.extension";
    static final String DEFAULT_TEMPLATE_EXTENSION = "template.default.extension";
    static final String DEFAULT_PAGE = "default.page";
    static final String DEFAULT_SCREEN = "default.screen";
    static final String DEFAULT_LAYOUT = "default.layout";
    static final String DEFAULT_NAVIGATION = "default.navigation";
    static final String DEFAULT_ERROR_SCREEN = "default.error.screen";
    static final String DEFAULT_LAYOUT_TEMPLATE = "default.layout.template";
    static final String DEFAULT_SCREEN_TEMPLATE = "default.screen.template";

    /**
     * Return the configuration of the template engine in
     * the form of a Hashtable.
     */
    Hashtable getTemplateEngineServiceConfiguration();

    /**
     * Initializes file extension associations and registers with the
     * template service.
     *
     * @param defaultExt The default file extension association to use
     *                   in case of properties file misconfiguration.
     */
    void registerConfiguration(String defaultExt);

    /**
     * Supplies the file extension to key this engine in {@link
     * org.apache.turbine.services.template.TemplateService}'s
     * registry with.
     */
    String[] getAssociatedFileExtensions();

    /**
     * Use the specific template engine to determine whether
     * a given template exists. This allows Turbine the TemplateService
     * to delegate the search for a template to the template
     * engine being used for the view. This gives us the
     * advantage of fully utilizing the capabilities of
     * template engine with respect to retrieving templates
     * from arbitrary sources.
     *
     * @param template The name of the template to check the existance of.
     * @return         Whether the specified template exists.
     */
    boolean templateExists(String template);
}
