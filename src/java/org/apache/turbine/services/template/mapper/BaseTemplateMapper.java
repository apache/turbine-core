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

import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.services.template.TurbineTemplate;

/**
 * This is a mapper like the BaseMapper but it returns its
 * results with the extension of the template names passed or (if no
 * extension is passed), the default extension.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public abstract class BaseTemplateMapper
    extends BaseMapper
{
    /** A prefix which is used to separate the various template types (screen, layouts, navigation) */
    protected String prefix = "";

    /**
     * Default C'tor. If you use this C'tor, you must use
     * the bean setter to set the various properties needed for
     * this mapper before first usage.
     */
    public BaseTemplateMapper()
    {
        super();
    }

    /**
     * Get the Prefix value.
     * @return the Prefix value.
     */
    public String getPrefix()
    {
        return prefix;
    }

    /**
     * Set the Prefix value.
     * @param prefix The new Prefix value.
     */
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    /**
     * Returns the default name for the passed Template.
     * If the template has no extension, the default extension
     * is added.
     * If the template is empty, the default template is
     * returned.
     *
     * @param template The template name.
     *
     * @return the mapped default name for the template.
     */
    public String getDefaultName(String template)
    {
        String res = super.getDefaultName(template);

        // Does the Template Name component have an extension?
        String [] components
            = StringUtils.split(res, String.valueOf(separator));

        if (components[components.length -1 ].indexOf(TemplateService.EXTENSION_SEPARATOR) < 0)
        {
            StringBuffer resBuf = new StringBuffer();
            resBuf.append(res);
            String [] templateComponents = StringUtils.split(template, String.valueOf(TemplateService.TEMPLATE_PARTS_SEPARATOR));

            // Only the extension of the Template name component is interesting...
            int dotIndex = templateComponents[templateComponents.length -1].lastIndexOf(TemplateService.EXTENSION_SEPARATOR);
            if (dotIndex < 0)
            {
                if (StringUtils.isNotEmpty(TurbineTemplate.getDefaultExtension()))
                {
                    resBuf.append(TemplateService.EXTENSION_SEPARATOR);
                    resBuf.append(TurbineTemplate.getDefaultExtension());
                }
            }
            else
            {
                resBuf.append(templateComponents[templateComponents.length -1].substring(dotIndex));
            }
            res = resBuf.toString();
        }
        return res;
    }
}
