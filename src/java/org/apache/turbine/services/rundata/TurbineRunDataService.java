package org.apache.turbine.services.rundata;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;

import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.pool.PoolService;
import org.apache.turbine.services.pool.TurbinePool;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.ServerData;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.parser.CookieParser;
import org.apache.turbine.util.parser.DefaultCookieParser;
import org.apache.turbine.util.parser.DefaultParameterParser;
import org.apache.turbine.util.parser.ParameterParser;

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
public class TurbineRunDataService
    extends TurbineBaseService
    implements RunDataService
{
    /** @deprecated Use RunDataService.RUN_DATA_KEY */
    public static final String RUN_DATA =
        RunDataService.RUN_DATA_KEY;

    /** @deprecated Use RunDataService.PARAMETER_PARSER_KEY */
    public static final String PARAMETER_PARSER =
        RunDataService.PARAMETER_PARSER_KEY;

    /** @deprecated Use RunDataService.COOKIE_PARSER_KEY */
    public static final String COOKIE_PARSER =
        RunDataService.COOKIE_PARSER_KEY;

    /** The default implementation of the RunData object*/
    private static final String DEFAULT_RUN_DATA =
        DefaultTurbineRunData.class.getName();

    /** The default implementation of the Parameter Parser object */
    private static final String DEFAULT_PARAMETER_PARSER =
        DefaultParameterParser.class.getName();

    /** The default implementation of the Cookie parser object */
    private static final String DEFAULT_COOKIE_PARSER =
        DefaultCookieParser.class.getName();

    /** The map of configurations. */
    private Map configurations = new HashMap();

    /** Private reference to the pool service for object recycling */
    private PoolService pool = null;

    /**
     * Constructs a RunData Service.
     */
    public TurbineRunDataService()
    {
    }

    /**
     * Initializes the service by setting the pool capacity.
     *
     * @throws InitializationException if initialization fails.
     */
    public void init()
            throws InitializationException
    {
        // Create a default configuration.
        String[] def = new String[]
        {
            DEFAULT_RUN_DATA,
            DEFAULT_PARAMETER_PARSER,
            DEFAULT_COOKIE_PARSER
        };
        configurations.put(DEFAULT_CONFIG, def.clone());

        // Check other configurations.
        Configuration conf = getConfiguration();
        if (conf != null)
        {
            String key,value;
            String[] config;
            String[] plist = new String[]
            {
                RUN_DATA_KEY,
                PARAMETER_PARSER_KEY,
                COOKIE_PARSER_KEY
            };
            for (Iterator i = conf.getKeys(); i.hasNext();)
            {
                key = (String) i.next();
                value = conf.getString(key);
                for (int j = 0; j < plist.length; j++)
                {
                    if (key.endsWith(plist[j]) &&
                            (key.length() > (plist[j].length() + 1)))
                    {
                        key = key.substring(0, key.length() - plist[j].length() - 1);
                        config = (String[]) configurations.get(key);
                        if (config == null)
                        {
                            config = (String[]) def.clone();
                            configurations.put(key, config);
                        }
                        config[j] = value;
                        break;
                    }
                }
            }
        }
        pool = TurbinePool.getService();

        if (pool == null)
        {
            throw new InitializationException("RunData Service requires"
                + " configured Pool Service!");
        }

        setInit(true);
    }

    /**
     * Gets a default RunData object.
     *
     * @param req a servlet request.
     * @param res a servlet response.
     * @param config a servlet config.
     * @return a new or recycled RunData object.
     * @throws TurbineException if the operation fails.
     */
    public RunData getRunData(HttpServletRequest req,
                              HttpServletResponse res,
                              ServletConfig config)
            throws TurbineException
    {
        return getRunData(DEFAULT_CONFIG, req, res, config);
    }

    /**
     * Gets a RunData instance from a specific configuration.
     *
     * @param key a configuration key.
     * @param req a servlet request.
     * @param res a servlet response.
     * @param config a servlet config.
     * @return a new or recycled RunData object.
     * @throws TurbineException if the operation fails.
     * @throws IllegalArgumentException if any of the parameters are null.
     */
    public RunData getRunData(String key,
                              HttpServletRequest req,
                              HttpServletResponse res,
                              ServletConfig config)
            throws TurbineException,
            IllegalArgumentException
    {
        // The RunData object caches all the information that is needed for
        // the execution lifetime of a single request. A RunData object
        // is created/recycled for each and every request and is passed
        // to each and every module. Since each thread has its own RunData
        // object, it is not necessary to perform syncronization for
        // the data within this object.
        if ((req == null)
            || (res == null)
            || (config == null))
        {
            throw new IllegalArgumentException("HttpServletRequest, "
                + "HttpServletResponse or ServletConfig was null.");
        }

        // Get the specified configuration.
        String[] cfg = (String[]) configurations.get(key);
        if (cfg == null)
        {
            throw new TurbineException("RunTime configuration '" + key + "' is undefined");
        }

        TurbineRunData data;
        try
        {
            data = (TurbineRunData) pool.getInstance(cfg[0]);
            data.setParameterParser((ParameterParser) pool.getInstance(cfg[1]));
            data.setCookieParser((CookieParser) pool.getInstance(cfg[2]));
        }
        catch (ClassCastException x)
        {
            throw new TurbineException("RunData configuration '" + key + "' is illegal", x);
        }

        // Set the request and response.
        data.setRequest(req);
        data.setResponse(res);

        // Set the servlet configuration.
        data.setServletConfig(config);

        // Set the ServerData.
        data.setServerData(new ServerData(req));

        return data;
    }

    /**
     * Puts the used RunData object back to the factory for recycling.
     *
     * @param data the used RunData object.
     * @return true, if pooling is supported and the object was accepted.
     */
    public boolean putRunData(RunData data)
    {
        if (data instanceof TurbineRunData)
        {
            pool.putInstance(((TurbineRunData) data).getParameterParser());
            pool.putInstance(((TurbineRunData) data).getCookieParser());

            return pool.putInstance(data);
        }
        else
        {
            return false;
        }
    }
}
