package org.apache.turbine.services.intake.transform;

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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.apache.turbine.util.Log;

/**
 * A resolver to get the database.dtd file for the XML parser from the jar.
 * This does not work with jdk1.3 on linux and OSX, see
 * <a href="http://developer.java.sun.com/developer/bugParade/bugs/4337703.html">
 * Bug 4337703</a>
 *
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class DTDResolver implements EntityResolver
{
    private static final String WEB_SITE_DTD =
            "http://jakarta.apache.org/turbine/dtd/intake_2_2_1.dtd";

    /** InputSource for intake.dtd */
    InputSource intakeDTD = null;

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
                Log.warn("Could not locate intake.dtd in classpath");
            }
        }
        catch (Exception ex)
        {
            Log.error("Could not get stream for intake.dtd", ex );
        }
    }

    /**
     * called by the XML parser
     *
     * @return an InputSource for the database.dtd file
     */
    public InputSource resolveEntity(String publicId, String systemId)
    {
        if (intakeDTD != null && WEB_SITE_DTD.equals(systemId))
        {
            String pkg = getClass().getName()
                    .substring(0, getClass().getName().lastIndexOf("."));

            Log.info("Resolver: used intake.dtd from " +
                    pkg + " package ");

            return intakeDTD;
        }
        else if (systemId == null)
        {
            Log.info("Resolver: used intake.dtd from Jakarta Web site");
            return getInputSource(WEB_SITE_DTD);
        }
        else
        {
            Log.info("Resolver: used System DTD for " + systemId);
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
            Log.error("Could not get InputSource for "+urlString, ex);
        }
        return new InputSource();
    }
}





