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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.apache.turbine.modules.Loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.services.template.TemplateService;

/**
 * This mapper tries to map Template names to class names. If no direct match
 * is found, it tries matches "upwards" in the package hierarchy until either
 * a match is found or the root is hit. Then it returns the name of the
 * default class from the TemplateEngineService.
 *
 * 1. about.directions.Driving     &lt;- direct matching the template to the class name
 * 2. about.directions.Default     &lt;- matching the package, class name is Default
 * 3. about.Default                &lt;- stepping up in the package hierarchy, looking for Default
 * 4. Default                      &lt;- Class called "Default" without package
 * 5. VelocityScreen               &lt;- The class configured by the Service (VelocityService) to
 *
 * Please note, that no actual packages are searched. This is the scope of the
 * TemplateEngine Loader which is passed at construction time.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class ClassMapper
    extends BaseMapper
    implements Mapper
{
    /** The loader for actually trying out the package names */
    private Loader loader = null;

    /** Logging */
    private static Log log = LogFactory.getLog(ClassMapper.class);

    /**
     * Default C'tor. If you use this C'tor, you must use
     * the bean setter to set the various properties needed for
     * this mapper before first usage.
     */
    public ClassMapper()
    {
    }

    /**
     * Get the Loader value.
     * @return the Loader value.
     */
    public Loader getLoader()
    {
        return loader;
    }

    /**
     * Set the Loader value.
     * @param loader The new Loader value.
     */
    public void setLoader(Loader loader)
    {
        this.loader = loader;
        log.debug("Loader is " + this.loader);
    }

    /**
     * Strip off a possible extension, replace all "," with "."
     * Look through the given package path until a match is found.
     *
     * @param template The template name.
     * @return A class name for the given template.
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
        String className = (String) components.get(componentSize);
        components.remove(componentSize--);

        log.debug("className is " + className);

        // Strip off a possible Extension
        int dotIndex = className.lastIndexOf(TemplateService.EXTENSION_SEPARATOR);
        className = (dotIndex < 0) ? className : className.substring(0, dotIndex);

        // This is an optimization. If the name we're looking for is
        // already the default name for the template, don't do a "first run"
        // which looks for an exact match.
        boolean firstRun = !className.equals(TemplateService.DEFAULT_NAME);

        for(;;)
        {
            String pkg = StringUtils.join(components.iterator(), String.valueOf(separator));
            StringBuffer testName = new StringBuffer();

            log.debug("classPackage is now: " + pkg);

            if (!components.isEmpty())
            {
                testName.append(pkg);
                testName.append(separator);
            }

            testName.append((firstRun)
                ? className
                : TemplateService.DEFAULT_NAME);

            log.debug("Looking for " + testName);
            try
            {
                loader.getAssembler(testName.toString());
                log.debug("Found it, returning " + testName);
                return testName.toString();
            }
            catch (Exception e)
            {
                // Not found. Go on.
            }

            if (firstRun)
            {
                firstRun = false;
            }
            else
            {
                if (components.isEmpty())
                {
                    break; // for(;;)
                }
                components.remove(componentSize--);
            }
        }

        log.debug("Returning default");
        return getDefaultName(template);
    }
}




