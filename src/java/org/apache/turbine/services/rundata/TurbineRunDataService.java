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

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.stratum.configuration.Configuration;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.CookieParser;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.ServerData;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.pool.PoolService;

/**
 * The RunData Service provides the implementations for RunData and
 * related interfaces required by request processing. It supports
 * different configurations of implementations, which can be selected
 * by specifying a configuration key. It may use pooling, in which case
 * the implementations should implement the Recyclable interface.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @version $Id$
 */
public class TurbineRunDataService
    extends TurbineBaseService
    implements RunDataService
{
    /**
     * The property for the implemention of RunData.
     */
    public static final String RUN_DATA = "run.data";

    /**
     * The property for the implemention of ParameterParser.
     */
    public static final String PARAMETER_PARSER = "parameter.parser";

    /**
     * The property for the implemention of CookieParser.
     */
    public static final String COOKIE_PARSER = "cookie.parser";

    /**
     * The default implementations.
     */
    private static final String DEFAULT_RUN_DATA =
        "org.apache.turbine.services.rundata.DefaultTurbineRunData";
    private static final String DEFAULT_PARAMETER_PARSER =
        "org.apache.turbine.util.parser.DefaultParameterParser";
    private static final String DEFAULT_COOKIE_PARSER =
        "org.apache.turbine.util.parser.DefaultCookieParser";

    /**
     * The map of configurations.
     */
    private HashMap configurations = new HashMap();

    /**
     * The getContextPath method from servet API >2.0.
     */
    private Method getContextPath;

    /**
     * Constructs a RunData Service.
     */
    public TurbineRunDataService()
    {
        // Allow Turbine to work with both 2.2 (and 2.1) and 2.0 Servlet API.
        try
        {
            getContextPath =
                HttpServletRequest.class.getDeclaredMethod("getContextPath",null);
        }
        catch (NoSuchMethodException x)
        {
            // Ignore a NoSuchMethodException because
            // it means we are using Servlet API 2.0.
        }
    }

    /**
     * Initializes the service by setting the pool capacity.
     *
     * @param config initialization configuration.
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
        configurations.put(DEFAULT_CONFIG,def.clone());

        // Check other configurations.
        Configuration conf = getConfiguration();
        if (conf != null)
        {
            String key,value;
            String[] config;
            String[] plist = new String[]
            {
                RUN_DATA,
                PARAMETER_PARSER,
                COOKIE_PARSER
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
                        key = key.substring(0,key.length() - plist[j].length() - 1);
                        config = (String[]) configurations.get(key);
                        if (config == null)
                        {
                            config = (String[]) def.clone();
                            configurations.put(key,config);
                        }
                        config[j] = value;
                        break;
                    }
                }
            }
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
        return getRunData(DEFAULT_CONFIG,req,res,config);
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
        if ((req == null) ||
            (res == null) ||
            (config == null) )
        {
            throw new IllegalArgumentException(
                "RunDataFactory fatal error: HttpServletRequest, HttpServletResponse or ServletConfig was null.");
        }

        // Get the specified configuration.
        String[] cfg = (String[]) configurations.get(key);
        if (cfg == null)
        {
            throw new TurbineException("RunTime configuration '" + key + "' is undefined");
        }

        // Use the Pool Service for recycling the implementing objects.
        PoolService pool = (PoolService)
            TurbineServices.getInstance().getService(PoolService.SERVICE_NAME);

        TurbineRunData data;
        try
        {
            data = (TurbineRunData) pool.getInstance(cfg[0]);
            data.setParameterParser((ParameterParser) pool.getInstance(cfg[1]));
            data.setCookieParser((CookieParser) pool.getInstance(cfg[2]));
        }
        catch (ClassCastException x)
        {
            throw new TurbineException("RunData configuration '" + key + "' is illegal",x);
        }

        // Set the request and response.
        data.setRequest(req);
        data.setResponse(res);

        // Set the session object.
        data.setSession(data.getRequest().getSession(true));

        // Set the servlet configuration.
        data.setServletConfig(config);

        // Set the ServerData.
        String contextPath;
        try
        {
            contextPath = getContextPath != null ?
                (String) getContextPath.invoke(req,null) : "";
        }
        catch (Exception x)
        {
            contextPath = "";
        }
        String scriptName = contextPath + req.getServletPath();
        data.setServerData(new ServerData(req.getServerName(),
                                          req.getServerPort(),
                                          req.getScheme(),
                                          scriptName,
                                          contextPath));

        return (RunData) data;
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
            PoolService pool = (PoolService)
                TurbineServices.getInstance().getService(PoolService.SERVICE_NAME);
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
