package org.apache.turbine.util.uri;


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
