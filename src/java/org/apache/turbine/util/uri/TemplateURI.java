package org.apache.turbine.util.uri;

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

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.ServerData;
import org.apache.turbine.util.parser.ParameterParser;

/**
 * This class allows you to keep all the information needed for a single
 * link at one place. It keeps your query data, path info, the server
 * scheme, name, port and the script path. It is tuned for usage with a
 * Template System e.g. Velocity.
 *
 * If you must generate a Turbine Link in a Template System, use this class.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class TemplateURI
        extends TurbineURI
{
    /**
     * Empty C'tor. Uses Turbine.getDefaultServerData().
     *
     */
    public TemplateURI()
    {
        super();
    }

    /**
     * Constructor with a RunData object
     *
     * @param runData A RunData object
     */
    public TemplateURI(RunData runData)
    {
        super(runData);
    }

    /**
     * Constructor, set explicit redirection
     *
     * @param runData A RunData object
     * @param redirect True if redirection allowed.
     */
    public TemplateURI(RunData runData, boolean redirect)
    {
        super(runData, redirect);
    }

    /**
     * Constructor, set Template
     *
     * @param runData A RunData object
     * @param template A Template Name
     */
    public TemplateURI(RunData runData, String template)
    {
        super(runData);
        setTemplate(template);
    }

    /**
     * Constructor, set Template, set explicit redirection
     *
     * @param runData A RunData object
     * @param template A Template Name
     * @param redirect True if redirection allowed.
     */
    public TemplateURI(RunData runData, String template, boolean redirect)
    {
        super(runData, redirect);
        setTemplate(template);
    }

    /**
     * Constructor, set Template and Action
     *
     * @param runData A RunData object
     * @param template A Template Name
     * @param action An Action Name
     */
    public TemplateURI(RunData runData, String template, String action)
    {
        this(runData, template);
        setAction(action);
    }

    /**
     * Constructor, set Template and Action, set explicit redirection
     *
     * @param runData A RunData object
     * @param template A Template Name
     * @param action An Action Name
     * @param redirect True if redirection allowed.
     */
    public TemplateURI(RunData runData, String template, String action, boolean redirect)
    {
        this(runData, template, redirect);
        setAction(action);
    }

    /**
     * Constructor with a ServerData object
     *
     * @param serverData A ServerData object
     */
    public TemplateURI(ServerData serverData)
    {
        super(serverData);
    }

    /**
     * Constructor, set explicit redirection
     *
     * @param serverData A ServerData object
     * @param redirect True if redirection allowed.
     */
    public TemplateURI(ServerData serverData, boolean redirect)
    {
        super(serverData, redirect);
    }

    /**
     * Constructor, set Template
     *
     * @param serverData A ServerData object
     * @param template A Template Name
     */
    public TemplateURI(ServerData serverData, String template)
    {
        super(serverData);
        setTemplate(template);
    }

    /**
     * Constructor, set Template, set explicit redirection
     *
     * @param serverData A ServerData object
     * @param template A Template Name
     * @param redirect True if redirection allowed.
     */
    public TemplateURI(ServerData serverData, String template, boolean redirect)
    {
        super(serverData, redirect);
        setTemplate(template);
    }

    /**
     * Constructor, set Template and Action
     *
     * @param serverData A ServerData object
     * @param template A Template Name
     * @param action An Action Name
     */
    public TemplateURI(ServerData serverData, String template, String action)
    {
        this(serverData, template);
        setAction(action);
    }

    /**
     * Constructor, set Template and Action, set explicit redirection
     *
     * @param serverData A ServerData object
     * @param template A Template Name
     * @param action An Action Name
     * @param redirect True if redirection allowed.
     */
    public TemplateURI(ServerData serverData, String template, String action, boolean redirect)
    {
        this(serverData, template, redirect);
        setAction(action);
    }

    /**
     * Constructor, user Turbine.getDefaultServerData(), set Template and Action
     *
     * @param serverData A ServerData object
     * @param template A Template Name
     * @param action An Action Name
     */
    public TemplateURI(String template, String action)
    {
        this();
        setTemplate(template);
        setAction(action);
    }

    /**
     * Sets the template= value for this URL.
     *
     * By default it adds the information to the path_info instead
     * of the query data. An empty value (null or "") cleans out
     * an existing value.
     *
     * @param template A String with the template value.
     */
    public void setTemplate(String template)
    {
        if(StringUtils.isNotEmpty(template))
        {
            add(PATH_INFO, CGI_TEMPLATE_PARAM, template);
        }
        else
        {
            clearTemplate();
        }
    }

    /**
     * Clears the template= value for this URL.
     *
     */
    public void clearTemplate()
    {
        removePathInfo(CGI_TEMPLATE_PARAM);
    }

    /*
     * ========================================================================
     *
     * Protected / Private Methods
     *
     * ========================================================================
     *
     */

    /**
     * Method for a quick way to add all the parameters in a
     * ParameterParser.
     *
     * <p>If the type is P (0), then add name/value to the pathInfo
     * hashtable.
     *
     * <p>If the type is Q (1), then add name/value to the queryData
     * hashtable.
     *
     * @param type Type of insertion (@see #add(char type, String name, String value))
     * @param pp A ParameterParser.
     */
    protected void add(int type,
            ParameterParser pp)
    {
        for(Iterator it = pp.keySet().iterator(); it.hasNext();)
        {
            String key = (String) it.next();

            if (!key.equalsIgnoreCase(CGI_ACTION_PARAM) &&
                    !key.equalsIgnoreCase(CGI_SCREEN_PARAM) &&
                    !key.equalsIgnoreCase(CGI_TEMPLATE_PARAM))
            {
                String[] values = pp.getStrings(key);
                for (int i = 0; i < values.length; i++)
                {
                    add(type, key, values[i]);
                }
            }
        }
    }
}
