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

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.services.template.TemplateEngineService;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.services.template.TurbineTemplate;

/**
 * This mapper is responsible for the lookup of templates for the Layout
 * It tries to look in various packages for a match:
 *
 * 1. about,directions,Driving.vm      &lt;- exact match
 * 2. about,directions,Default.vm      &lt;- package match, Default name
 * 3. about,Default.vm                 &lt;- stepping up in the hierarchy
 * 4. Default.vm                       &lt;- The name configured as default.layout.template
 *                                        in the corresponding Templating Engine

 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class TemplateLayoutMapper
    extends TemplateBaseLayoutMapper
    implements TemplateMapper
{
    /** Logging */
    private static Log log = LogFactory.getLog(TemplateLayoutMapper.class);

    /**
     * Default C'tor. If you use this C'tor, you must use
     * the bean setter to set the various properties needed for
     * this mapper before first usage.
     */
    public TemplateLayoutMapper()
    {
    }

    /**
     * Look for a given Template, then try the
     * defaults until we hit the root.
     *
     * @param template The template name.
     * @return The parsed module name.
     */
    public String doMapping(String template)
    {
        log.debug("doMapping(" + template + ")");
        // Copy our elements into an array
        List components
            = new ArrayList(Arrays.asList(StringUtils.split(
                                              template,
                                              String.valueOf(TemplateService.TEMPLATE_PARTS_SEPARATOR))));
        int componentSize = components.size() - 1 ;

        // This method never gets an empty string passed.
        // So this is never < 0
        String templateName = (String) components.get(componentSize);
        components.remove(componentSize--);

        log.debug("templateName is " + templateName);

        // Last element decides, which template Service to use...
        TemplateEngineService tes = TurbineTemplate.getTemplateEngineService(templateName);

        if (tes == null)
        {
            return null;
        }

        String defaultName =
            TurbineTemplate.getDefaultLayoutTemplateName(templateName); // We're, after all, a Layout Template Mapper...

        // This is an optimization. If the name we're looking for is
        // already the default name for the template, don't do a "first run"
        // which looks for an exact match.
        boolean firstRun = !templateName.equals(defaultName);

        for(;;)
        {
            String templatePackage = StringUtils.join(components.iterator(), String.valueOf(separator));

            log.debug("templatePackage is now: " + templatePackage);

            StringBuffer testName = new StringBuffer();

            if (!components.isEmpty())
            {
                testName.append(templatePackage);
                testName.append(separator);
            }

            testName.append((firstRun)
                ? templateName
                : defaultName);

            // But the Templating service must look for the name with prefix
            StringBuffer templatePath = new StringBuffer();
            if (StringUtils.isNotEmpty(prefix))
            {
                templatePath.append(prefix);
                templatePath.append(separator);
            }
            templatePath.append(testName);

            log.debug("Looking for " + templatePath);

            if (tes.templateExists(templatePath.toString()))
            {
                log.debug("Found it, returning " + testName);
                return testName.toString();
            }

            if (firstRun)
            {
                firstRun = false;
            }
            else
            {
                // We're no longer on the first Run (so we
                // already tested the "Default" template)
                // and the package is empty (we've hit the
                // root. So we now break the endless loop.
                if (components.isEmpty())
                {
                    break; // for(;;)
                }
                // We still have components. Remove the
                // last one and go through the loop again.
                components.remove(componentSize--);
            }
        }

        log.debug("Returning default");
        return getDefaultName(template);
    }
}
