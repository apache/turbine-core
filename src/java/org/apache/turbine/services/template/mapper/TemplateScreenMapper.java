package org.apache.turbine.services.template.mapper;

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

import org.apache.commons.lang.StringUtils;

import org.apache.turbine.services.template.TemplateEngineService;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.services.template.TurbineTemplate;

/**
 * This is a pretty simple mapper which returns template pathes for
 * a supplied template name. This path can be used by the TemplateEngine
 * to access a certain resource to actually render the template.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class TemplateScreenMapper
    extends TemplateBaseLayoutMapper
    implements TemplateMapper
{
    /**
     * Default C'tor. If you use this C'tor, you must use
     * the bean setter to set the various properties needed for
     * this mapper before first usage.
     */
    public TemplateScreenMapper()
    {
    }

    /**
     * C'tor
     *
     * @param useCache If true, then the resulting mapper will cache mappings.
     * @param cacheSize Size of the internal map cache. Must be > 0 if useCache is true.
     * @param defaultProperty The name of the default property to pull from the TemplateEngine
     * @param separator The separator for this mapper.
     * @param prefix A prefix used to provide various hierarchies for screens, layouts, navigation...
     */
    public TemplateScreenMapper(boolean useCache,
        int cacheSize,
        String defaultProperty,
        char separator,
        String prefix)
    {
        super(useCache, cacheSize, defaultProperty, separator, prefix);
    }

    /**
     * Check, whether the provided name exists. Returns null
     * if the screen does not exist.
     *
     * @param template The template name.
     * @return The matching screen name.
     */
    public String doMapping(String template)
    {
        String [] components = StringUtils.split(template, String.valueOf(TemplateService.TEMPLATE_PARTS_SEPARATOR));

        // Last element decides, which template Service to use...
        TemplateEngineService tes =
            TurbineTemplate.getTemplateEngineService(components[components.length - 1]);

        String templatePackage = StringUtils.join(components, String.valueOf(separator));

        // But the Templating service must look for the name with prefix
        StringBuffer testPath = new StringBuffer();
        if (StringUtils.isNotEmpty(prefix))
        {
            testPath.append(prefix);
            testPath.append(separator);
        }
        testPath.append(templatePackage);

        return (tes != null && tes.templateExists(testPath.toString()))
            ? templatePackage
            : null;
    }
}




