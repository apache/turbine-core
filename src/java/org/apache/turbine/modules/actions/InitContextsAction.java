package org.apache.turbine.modules.actions;

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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.configuration.Configuration;

import org.apache.turbine.Turbine;
import org.apache.turbine.modules.Action;
import org.apache.turbine.util.RunData;

/**
 * Used to initialize JNDI contexts.
 *
 * @version $Id$
 */
public class InitContextsAction
        extends Action
{
    /**
     * This action will place the contexts defined in the
     * TurbineResources instance (if any) into the data.contexts
     * Hashtable.
     *
     * @param data The RunData object for the current request.
     * @exception NamingException could not create InitialContext
     */
    public void doPerform(RunData data)
            throws NamingException
    {
        Configuration conf = Turbine.getConfiguration();

        // Context properties are specified in lines in the properties
        // file that begin with "context.contextname.", allowing
        // multiple named contexts to be used.  Everything after the
        // "contextname." is the name of the property that will be
        // used by the InitialContext class to create a new context
        // instance.

        Hashtable contextPropsList = new Hashtable();
        for (Iterator contextKeys = conf.getKeys("context."); 
                contextKeys.hasNext();)
        {
            String key = (String) contextKeys.next();
            int start = key.indexOf(".") + 1;
            int end = key.indexOf(".", start);
            String contextName = key.substring(start, end);
            Properties contextProps = null;
            if (contextPropsList.containsKey(contextName))
            {
                contextProps = (Properties) contextPropsList.get(contextName);
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
            data.getJNDIContexts().put(key, context);
        }
    }
}
