package org.apache.turbine.services.naming;


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
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.configuration.Configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.util.RunData;

/**
 * This class is the default implementation of NamingService, which
 * provides JNDI naming contexts.
 *
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @author <a href="mailto:colin.chalmers@maxware.nl">Colin Chalmers</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class TurbineNamingService
        extends TurbineBaseService
        implements NamingService
{
    /** Logging */
    private static Log log = LogFactory.getLog(TurbineNamingService.class);

    /**
     * A global Map of Property objects which are initialised using
     * parameters from the ResourcesFile
     */
    private static Map contextPropsList = null;

    /** All initial contexts known to this service */
    private Map initialContexts = new HashMap();

    /**
     * Called the first time the Service is used.<br>
     *
     */
    public void init()
            throws InitializationException
    {
        // Context properties are specified in lines in the properties
        // file that begin with "context.contextname.", allowing
        // multiple named contexts to be used.  Everything after the
        // "contextname."  is the name of the property that will be
        // used by the InitialContext class to create a new context
        // instance.

        Configuration conf = Turbine.getConfiguration();
        try
        {
            contextPropsList = new HashMap();

            for (Iterator contextKeys = conf.subset("context").getKeys();
                 contextKeys.hasNext();)
            {
                String key = (String) contextKeys.next();
                int end = key.indexOf(".");

                if (end == -1)
                {
                    continue;
                }

                String contextName = key.substring(0, end);
                Properties contextProps = null;

                if (contextPropsList.containsKey(contextName))
                {
                    contextProps = (Properties)
                            contextPropsList.get(contextName);
                }
                else
                {
                    contextProps = new Properties();
                }

                contextProps.put(key.substring(end + 1),
                        conf.getString(key));

                contextPropsList.put(contextName, contextProps);
            }

            for (Iterator contextPropsKeys = contextPropsList.keySet().iterator();
                 contextPropsKeys.hasNext();)
            {
                String key = (String) contextPropsKeys.next();
                Properties contextProps = (Properties) contextPropsList.get(key);
                InitialContext context = new InitialContext(contextProps);
                initialContexts.put(key, context);
            }

            setInit(true);
        }
        catch (Exception e)
        {
            log.error("Failed to initialize JDNI contexts!", e);

            throw new InitializationException(
                    "Failed to initialize JDNI contexts!");
        }
    }


    /**
     * Return the Context with the specified name.  The Context is
     * constructed using the properties for the context with the
     * specified name; ie. those properties that start with
     * "services.servicename.properties.name.".
     *
     * @param contextName The name of the context.
     * @return The context with the specified name, or null if no
     * context exists with that name.
     */
    public Context getContext(String contextName)
    {
        // Get just the properties for the context with the specified
        // name.
        Properties contextProps = null;

        if (contextPropsList.containsKey(contextName))
        {
            contextProps = (Properties) contextPropsList.get(contextName);
        }
        else
        {
            contextProps = new Properties();
        }

        // Construct a new context with the properties.
        try
        {
            return new InitialContext(contextProps);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
