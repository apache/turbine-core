package org.apache.turbine.modules.actions;

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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import javax.naming.InitialContext;

import org.apache.turbine.modules.Action;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.util.RunData;

/**
 * Used to initialize JNDI contexts.
 *
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @version $Id$
 */
public class InitContextsAction extends Action
{
    /**
     * This action will place the contexts defined in the
     * TurbineResources instance (if any) into the data.contexts
     * Hashtable.
     *
     * @param data The RunData object for the current request.
     * @exception Exception, a generic exception.
     */
    public void doPerform(RunData data)
            throws Exception
    {
        // Context properties are specified in lines in the properties
        // file that begin with "context.contextname.", allowing
        // multiple named contexts to be used.  Everything after the
        // "contextname." is the name of the property that will be
        // used by the InitialContext class to create a new context
        // instance.

        Hashtable contextPropsList = new Hashtable();
        for (Iterator contextKeys = TurbineResources.getKeys("context."); contextKeys.hasNext();)
        {
            String key = (String) contextKeys.next();
            int start = key.indexOf(".") + 1;
            int end = key.indexOf(".", start);
            String contextName = key.substring(start, end);
            Properties contextProps = null;
            if (contextPropsList.containsKey(contextName))
            {
                contextProps =
                        (Properties) contextPropsList.get(contextName);
            }
            else
            {
                contextProps = new Properties();
            }
            contextProps.put(key.substring(end + 1),
                    TurbineResources.getString(key));
            contextPropsList.put(contextName, contextProps);
        }
        for (Iterator contextPropsKeys = contextPropsList.keySet().iterator(); contextPropsKeys.hasNext();)
        {
            String key = (String) contextPropsKeys.next();
            Properties contextProps =
                    (Properties) contextPropsList.get(key);
            InitialContext context = new InitialContext(contextProps);
            data.getJNDIContexts().put(key, context);
        }
    }
}
