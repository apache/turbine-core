package org.apache.turbine.services.rundata;

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

import javax.servlet.ServletConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.ServerData;
import org.apache.turbine.util.parser.CookieParser;
import org.apache.turbine.util.parser.ParameterParser;
import org.apache.turbine.util.pool.Recyclable;

/**
 * TurbineRunData is an extension to the RunData interface to be
 * implemented by RunData implementations to be distributed by
 * the Turbine RunData Service. The extensions define methods
 * that are used by the service for initilizing the implementation,
 * but which are not meant to be called by the actual client objects.
 *
 * <p>TurbineRunData extends also the Recyclable interface making
 * it possible to pool its implementations for recycling.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:bhoeneis@ee.ethz.ch">Bernie Hoeneisen</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public interface TurbineRunData
    extends RunData,
            Recyclable
{
    /**
     * Gets the parameter parser without parsing the parameters.
     *
     * @return the parameter parser.
     */
    ParameterParser getParameterParser();

    /**
     * Sets the parameter parser.
     *
     * @param parser a parameter parser.
     */
    void setParameterParser(ParameterParser parser);

    /**
     * Gets the cookie parser without parsing the cookies.
     *
     * @return the cookie parser.
     */
    CookieParser getCookieParser();

    /**
     * Sets the cookie parser.
     *
     * @param parser a cookie parser.
     */
    void setCookieParser(CookieParser parser);

    /**
     * Sets the servlet request.
     *
     * @param req a request.
     */
    void setRequest(HttpServletRequest req);

    /**
     * Sets the servlet response.
     *
     * @param res a response.
     */
    void setResponse(HttpServletResponse res);

    /**
     * Sets the servlet session information.
     *
     * @param sess a session.
     * @deprecated No replacement. This method no longer does anything.
     */
    void setSession(HttpSession sess);

    /**
     * Sets the servlet configuration used during servlet init.
     *
     * @param config a configuration.
     */
    void setServletConfig(ServletConfig config);

    /**
     * Sets the server data of the request.
     *
     * @param serverData server data.
     */
    void setServerData(ServerData serverData);
}
