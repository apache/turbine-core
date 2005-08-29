package org.apache.turbine.services.xslt;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import org.apache.turbine.services.TurbineServices;
import org.w3c.dom.Node;

/**
 * This is a static accesor class for {@link XSLTService}.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="thomas.vandahl@tewisoft.de">Thomas Vandahl</a>
 * @version $Id$
 */
public class TurbineXSLT
{
    /**
     * Utility method for accessing the service
     * implementation
     *
     * @return a XSLTService implementation instance
     */
    protected static XSLTService getService()
    {
        return (XSLTService) TurbineServices
                .getInstance().getService(XSLTService.SERVICE_NAME);
    }

    public static void transform(String xslName, Reader in, Writer out)
            throws Exception
    {
        getService().transform(xslName, in, out);
    }

    public static String transform(String xslName, Reader in)
            throws Exception
    {
        return getService().transform(xslName, in);
    }

    public void transform(String xslName, Node in, Writer out)
            throws Exception
    {
        getService().transform(xslName, in, out);
    }

    public String transform(String xslName, Node in)
            throws Exception
    {
        return getService().transform(xslName, in);
    }

    public static void transform(String xslName, Reader in, Writer out, Map params)
            throws Exception
    {
        getService().transform(xslName, in, out, params);
    }

    public static String transform(String xslName, Reader in, Map params)
            throws Exception
    {
        return getService().transform(xslName, in, params);
    }

    public void transform(String xslName, Node in, Writer out, Map params)
            throws Exception
    {
        getService().transform(xslName, in, out, params);
    }

    public String transform(String xslName, Node in, Map params)
            throws Exception
    {
        return getService().transform(xslName, in, params);
    }
}
