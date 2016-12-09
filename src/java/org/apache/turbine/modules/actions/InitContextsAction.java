package org.apache.turbine.modules.actions;

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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.configuration.Configuration;
import org.apache.turbine.annotation.TurbineConfiguration;
import org.apache.turbine.modules.Action;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;

/**
 * Used to initialize JNDI contexts.
 *
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class InitContextsAction
        extends Action
{
    /** Injected configuration instance */
    @TurbineConfiguration
    private Configuration conf;

    /**
     * This action will place the contexts defined in the TurbineResources
     * instance (if any) into the data.contexts Hashtable.
     *
     * @param pipelineData
     *            The PipelineRunData object for the current request.
     * @throws NamingException
     *                could not create InitialContext
     */
    @Override
    public void doPerform(PipelineData pipelineData)
            throws NamingException
    {
        RunData data = getRunData(pipelineData);
        // Context properties are specified in lines in the properties
        // file that begin with "context.contextname.", allowing
        // multiple named contexts to be used. Everything after the
        // "contextname." is the name of the property that will be
        // used by the InitialContext class to create a new context
        // instance.

        Hashtable<String, Properties> contextPropsList = new Hashtable<String, Properties>();
        for (Iterator<String> contextKeys = conf.getKeys("context."); contextKeys.hasNext();)
        {
            String key = contextKeys.next();
            int start = key.indexOf(".") + 1;
            int end = key.indexOf(".", start);
            String contextName = key.substring(start, end);
            Properties contextProps = null;
            if (contextPropsList.containsKey(contextName))
            {
                contextProps = contextPropsList.get(contextName);
            }
            else
            {
                contextProps = new Properties();
            }
            contextProps.put(key.substring(end + 1), conf.getString(key));
            contextPropsList.put(contextName, contextProps);
        }

        for (Entry<String, Properties> contextProps : contextPropsList.entrySet())
        {
            InitialContext context = new InitialContext(contextProps.getValue());
            data.getJNDIContexts().put(contextProps.getKey(), context);
        }
    }
}
