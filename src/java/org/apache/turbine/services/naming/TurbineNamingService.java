package org.apache.turbine.services.naming;

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

import java.util.Enumeration;
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
     * Places the contexts defined in the TurbineResources instance
     * (if any) into the data.contexts Map.
     *
     * @param data The RunData object for the current request.
     * @exception InitializationException, if there was a problem
     * during initialization.
     * @deprecated This should never have been here. No replacement.
     */
    public void init(RunData data) throws InitializationException
    {
        try
        {
            if (contextPropsList == null)
            {
                init();
            }

            for (Iterator it = contextPropsList.keySet().iterator(); it.hasNext();)
            {
                String key = (String) it.next();
                Properties contextProps =
                        (Properties) contextPropsList.get(key);
                InitialContext context = new InitialContext(contextProps);
                data.getJNDIContexts().put(key, context);
            }
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
