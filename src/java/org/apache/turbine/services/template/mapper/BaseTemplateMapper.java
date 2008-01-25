package org.apache.turbine.services.template.mapper;


/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
