package org.apache.turbine.services.rundata;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.fulcrum.parser.CookieParser;
import org.apache.fulcrum.parser.DefaultCookieParser;
import org.apache.fulcrum.parser.DefaultParameterParser;
import org.apache.fulcrum.parser.ParameterParser;
import org.apache.fulcrum.parser.ParserService;
import org.apache.fulcrum.pool.PoolException;
import org.apache.fulcrum.pool.PoolService;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.ServerData;
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
public class TurbineRunDataService
    extends TurbineBaseService
    implements RunDataService
{

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
    private final Map<String, Object> configurations = new HashMap<String, Object>();

    /** Private reference to the pool service for object recycling */
    private PoolService pool = null;

    /** Private reference to the parser service for parser recycling */
    private ParserService parserService = null;

    /**
     * Constructs a RunData Service.
     */
    public TurbineRunDataService()
    {
        super();
    }

    /**
     * Initializes the service by setting the pool capacity.
     *
     * @throws InitializationException if initialization fails.
     */
    @SuppressWarnings("unchecked")
    @Override
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
            for (Iterator<String> i = conf.getKeys(); i.hasNext();)
            {
                key = i.next();
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
                            config = def.clone();
                            configurations.put(key, config);
                        }
                        config[j] = value;
                        break;
                    }
                }
            }
        }

		pool = (PoolService)TurbineServices.getInstance().getService(PoolService.ROLE);

        if (pool == null)
        {
            throw new InitializationException("RunData Service requires"
                + " configured Pool Service!");
        }

        parserService = (ParserService)TurbineServices.getInstance().getService(ParserService.ROLE);

        if (parserService == null)
        {
            throw new InitializationException("RunData Service requires"
                + " configured Parser Service!");
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
     * @todo The "key" parameter should be removed in favor of just looking up what class via the roleConfig avalon file.
     */
    public RunData getRunData(String key,
                              HttpServletRequest req,
                              HttpServletResponse res,
                              ServletConfig config)
            throws TurbineException,
            IllegalArgumentException
    {
        // a map to hold information to be added to pipelineData
        Map<Class<?>, Object> pipelineDataMap = new HashMap<Class<?>, Object>();
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
    		Class<?> runDataClazz = Class.forName(cfg[0]);
    		Class<?> parameterParserClazz = Class.forName(cfg[1]);
    		Class<?> cookieParserClazz = Class.forName(cfg[2]);

            data = (TurbineRunData) pool.getInstance(runDataClazz);
            ParameterParser pp = (ParameterParser) parserService.getParser(parameterParserClazz);
            data.setParameterParser(pp);
            CookieParser cp = (CookieParser) parserService.getParser(cookieParserClazz);
            data.setCookieParser(cp);
            // also copy data directly into pipelineData
            pipelineDataMap.put(ParameterParser.class, pp);
            pipelineDataMap.put(CookieParser.class, cp);

            Locale locale = req.getLocale();

            if (locale == null)
            {
                // get the default from the Turbine configuration
                locale = data.getLocale();
            }

            // set the locale detected and propagate it to the parsers
            data.setLocale(locale);
        }
        catch (PoolException pe)
        {
            throw new TurbineException("RunData configuration '" + key + "' is illegal caused a pool exception", pe);
        }
        catch (ClassNotFoundException x)
        {
            throw new TurbineException("RunData configuration '" + key + "' is illegal", x);
        }
        catch (ClassCastException x)
        {
            throw new TurbineException("RunData configuration '" + key + "' is illegal", x);
        }
        catch (InstantiationException e)
        {
            throw new TurbineException("RunData configuration '" + key + "' is illegal", e);
        }

        // Set the request and response.
        data.setRequest(req);
        data.setResponse(res);
        // also copy data directly into pipelineData
        pipelineDataMap.put(HttpServletRequest.class, req);
        pipelineDataMap.put(HttpServletResponse.class, res);

        // Set the servlet configuration.
        data.setServletConfig(config);
        // also copy data directly into pipelineData
        pipelineDataMap.put(ServletConfig.class, config);

        // Set the ServerData.
        ServerData sd = new ServerData(req);
        data.setServerData(sd);
        // also copy data directly into pipelineData
        pipelineDataMap.put(ServerData.class, sd);

        // finally put the pipelineDataMap into the pipelineData object
        data.put(Turbine.class, pipelineDataMap);
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
            parserService.putParser(((TurbineRunData) data).getParameterParser());
            parserService.putParser(((TurbineRunData) data).getCookieParser());

            return pool.putInstance(data);
        }
        else
        {
            return false;
        }
    }
}
