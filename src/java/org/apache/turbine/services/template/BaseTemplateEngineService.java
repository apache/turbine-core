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
import org.apache.commons.configuration.Configuration;
import org.apache.turbine.services.TurbineBaseService;


/**
 * The base implementation of Turbine {@link
 * org.apache.turbine.services.template.TemplateEngineService}.
 *
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class BaseTemplateEngineService extends TurbineBaseService
    implements TemplateEngineService
{
    /**
     * A Map containing the configuration for the template
     * engine service. The configuration contains:
     *
     * 1) template extensions
     * 2) default page
     * 3) default screen
     * 4) default layout
     * 5) default navigation
     * 6) default error screen
     */
    private Hashtable configuration = new Hashtable();

    /**
     * @see org.apache.turbine.services.template.TemplateEngineService#registerConfiguration
     */
    public void registerConfiguration(String defaultExt)
    {
        initConfiguration(defaultExt);
        TurbineTemplate.registerTemplateEngineService(this);
    }

    /**
     * @see org.apache.turbine.services.template.TemplateEngineService#getTemplateEngineServiceConfiguration
     */
    public Hashtable getTemplateEngineServiceConfiguration()
    {
        return configuration;
    }

    /**
     * @see org.apache.turbine.services.template.TemplateEngineService#getAssociatedFileExtensions
     */
    public String[] getAssociatedFileExtensions()
    {
        return (String []) configuration.get(TEMPLATE_EXTENSIONS);
    }

    /**
     * Note engine file extension associations.  First attempts to
     * pull a list of custom extensions from the property file value
     * keyed by <code>template.extension</code>.  If none are defined,
     * uses the value keyed by
     * <code>template.default.extension</code>, defaulting to the
     * emergency value supplied by <code>defaultExt</code>.
     *
     * @param defaultExt The default used when the default defined in the
     *                   properties file is missing or misconfigured.
     */
    protected void initConfiguration(String defaultExt)
    {
        Configuration config = getConfiguration();

        /*
         * Should modify the configuration class to take defaults
         * here, should have to do this.
         */
        String[] fileExtensionAssociations =
            config.getStringArray(TEMPLATE_EXTENSIONS);

        if (fileExtensionAssociations == null ||
            fileExtensionAssociations.length == 0)
        {
            fileExtensionAssociations = new String[1];
            fileExtensionAssociations[0] = config.getString(
                DEFAULT_TEMPLATE_EXTENSION, defaultExt);
        }

        configuration.put(TEMPLATE_EXTENSIONS, fileExtensionAssociations);

        /*
         * We need some better error checking here and should probably
         * throw an exception here if these things aren't set
         * up correctly.
         */

        String [] copyParams = {
            DEFAULT_PAGE,
            DEFAULT_SCREEN,
            DEFAULT_LAYOUT,
            DEFAULT_NAVIGATION,
            DEFAULT_ERROR_SCREEN,
            DEFAULT_LAYOUT_TEMPLATE
        };
        
        for(int i = 0; i < copyParams.length; i++)
        {
            configuration.put(copyParams[i], config.getString(copyParams[i]));
        }
    }

    /**
     * @see org.apache.turbine.services.template.TemplateEngineService#templateExists
     */
    public abstract boolean templateExists(String template);
}
