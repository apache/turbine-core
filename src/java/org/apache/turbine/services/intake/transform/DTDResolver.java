package org.apache.turbine.services.intake.transform;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * A resolver to get the database.dtd file for the XML parser from the jar.
 * This does not work with jdk1.3 on linux and OSX, see
 * <a href="http://developer.java.sun.com/developer/bugParade/bugs/4337703.html">
 * Bug 4337703</a>
 *
 * @deprecated Use the Fulcrum Intake component instead.
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class DTDResolver implements EntityResolver
{
    private static final String WEB_SITE_DTD =
            "http://jakarta.apache.org/turbine/dtd/intake_2_3.dtd";

    /** InputSource for <code>intake.dtd</code>. */
    private InputSource intakeDTD = null;

    /** Logging */
    private static Log log = LogFactory.getLog(DTDResolver.class);

    /**
     * constructor
     */
    public DTDResolver()
    {
        try
        {
            InputStream dtdStream =
                    getClass().getResourceAsStream("intake.dtd");

            // getResource was buggy on many systems including Linux,
            // OSX, and some versions of windows in jdk1.3.
            // getResourceAsStream works on linux, maybe others?
            if (dtdStream != null)
            {
                intakeDTD = new InputSource(dtdStream);
            }
            else
            {
                log.warn("Could not located the intake.dtd");
            }
        }
        catch (Exception ex)
        {
            log.error("Could not get stream for dtd", ex);
        }
    }

    /**
     * called by the XML parser
     *
     * @return an InputSource for the intake.dtd file
     */
    public InputSource resolveEntity(String publicId, String systemId)
    {
        if (intakeDTD != null && WEB_SITE_DTD.equals(systemId))
        {
            String pkg = getClass().getName()
                    .substring(0, getClass().getName().lastIndexOf("."));

            log.info("Resolver: used intake.dtd from " +
                    pkg + " package ");

            return intakeDTD;
        }
        else if (systemId == null)
        {
            log.info("Resolver: used intake.dtd from Jakarta Web site");
            return getInputSource(WEB_SITE_DTD);
        }
        else
        {
            log.info("Resolver: used System DTD for " + systemId);
            return getInputSource(systemId);
        }
    }

    /**
     * Retrieves a XML input source for the specified URL.
     *
     * @param urlString The URL of the input source.
     * @return <code>InputSource</code> for the URL.
     */
    private InputSource getInputSource(String urlString)
    {
        try
        {
            URL url = new URL(urlString);
            return new InputSource(url.openStream());
        }
        catch (IOException ex)
        {
            log.error("Could not get InputSource for " + urlString, ex);
        }
        return new InputSource();
    }
}
