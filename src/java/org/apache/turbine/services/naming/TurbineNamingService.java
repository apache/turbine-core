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
import javax.naming.NamingException;

import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;

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
    private static Logger log = LogManager.getLogger(TurbineNamingService.class);

    /**
     * A global Map of Property objects which are initialised using
     * parameters from the ResourcesFile
     */
    private static Map<String, Properties> contextPropsList = null;

    /** All initial contexts known to this service */
    private final Map<String, InitialContext> initialContexts = new HashMap<String, InitialContext>();

    /**
     * Called the first time the Service is used.<br>
     *
     */
    @Override
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
            contextPropsList = new HashMap<String, Properties>();

            for (Iterator<String> contextKeys = conf.subset("context").getKeys();
                 contextKeys.hasNext();)
            {
                String key = contextKeys.next();
                int end = key.indexOf(".");

                if (end == -1)
                {
                    continue;
                }

                String contextName = key.substring(0, end);
                Properties contextProps = null;

                if (contextPropsList.containsKey(contextName))
                {
                    contextProps = contextPropsList.get(contextName);
                }
                else
                {
                    contextProps = new Properties();
                }

                contextProps.put(key.substring(end + 1),
                        conf.getString(key));

                contextPropsList.put(contextName, contextProps);
            }

            for (Map.Entry<String, Properties> entry : contextPropsList.entrySet())
            {
                String key = entry.getKey();
                Properties contextProps = entry.getValue();
                InitialContext context = new InitialContext(contextProps);
                initialContexts.put(key, context);
            }

            setInit(true);
        }
        catch (NamingException e)
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
            contextProps = contextPropsList.get(contextName);
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
