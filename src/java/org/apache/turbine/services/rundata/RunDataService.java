package org.apache.turbine.services.rundata;

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

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.turbine.services.Service;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * The RunData Service provides the implementations for RunData and
 * related interfaces required by request processing. It supports
 * different configurations of implementations, which can be selected
 * by specifying a configuration key. It may use pooling, in which case
 * the implementations should implement the Recyclable interface.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public interface RunDataService
    extends Service
{
    /** The key under which this service is stored in TurbineServices. */
    static final String SERVICE_NAME = "RunDataService";

    /** The default parser configuration key. */
    static final String DEFAULT_CONFIG = "default";

    /** The property for the implemention of the RunData object */
    public static final String RUN_DATA_KEY = "run.data";

    /** The property for the implemention of the ParameterParser. */
    public static final String PARAMETER_PARSER_KEY = "parameter.parser";

    /** The property for the implemention of the CookieParser. */
    public static final String COOKIE_PARSER_KEY = "cookie.parser";

    /**
     * Gets a default RunData object.
     *
     * @param req a servlet request.
     * @param res a servlet response.
     * @param config a servlet config.
     * @return a new or recycled RunData object.
     * @throws TurbineException if the operation fails.
     */
    RunData getRunData(HttpServletRequest req,
                       HttpServletResponse res,
                       ServletConfig config)
            throws TurbineException;

    /**
     * Gets a RunData object from a specific configuration.
     *
     * @param key a configuration key.
     * @param req a servlet request.
     * @param res a servlet response.
     * @param config a servlet config.
     * @return a new or recycled RunData object.
     * @throws TurbineException if the operation fails.
     */
    RunData getRunData(String key,
                       HttpServletRequest req,
                       HttpServletResponse res,
                       ServletConfig config)
            throws TurbineException;

    /**
     * Puts the used RunData object back to the factory for recycling.
     *
     * @param data the used RunData object.
     * @return true, if pooling is supported and the object was accepted.
     */
    boolean putRunData(RunData data);
}
