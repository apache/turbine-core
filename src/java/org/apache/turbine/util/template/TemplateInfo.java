package org.apache.turbine.util.template;

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
import java.util.Map;

import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.uri.URIConstants;


/**
 * This is a wrapper for Template specific information.  It's part of
 * the RunData object and can extract the information it needs to do
 * the job directly from the data.getParameters().
 *
 * @author <a href="mailto:mbryson@mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TemplateInfo
{

    /* Constants for tempStorage hash map. */
    public static final String NAVIGATION_TEMPLATE = "00navigation_template00";
    public static final String LAYOUT_TEMPLATE = "00layout_template00";
    public static final String SERVICE_NAME = "template_service";

    /* Handle to the RunData object. */
    private RunData data = null;

    /* Place to store information about templates. */
    private Map tempStorage = null;

    /**
     * Constructor
     *
     * @param RunData A Turbine Rundata object.
     */
    public TemplateInfo(RunData data)
    {
        this.data = data;
        tempStorage = new HashMap(10);
    }

    /**
     * Get the value of navigationTemplate.
     *
     * @return A String with the value of navigationTemplate.
     */
    public String getNavigationTemplate()
    {
        return getString(TemplateInfo.NAVIGATION_TEMPLATE);
    }

    /**
     * Set the value of navigationTemplate.
     *
     * @param v Value to assign to navigationTemplate.
     */
    public void setNavigationTemplate(String v)
    {
        setTemp(TemplateInfo.NAVIGATION_TEMPLATE, v);
    }

    /**
     * Get the value of screen for the RunData parameters.  This
     * information comes from PathInfo or a QueryString.
     *
     * @return A String with the value of screen.
     */
    public String getScreenTemplate()
    {
        return data.getParameters().getString(URIConstants.CGI_TEMPLATE_PARAM, null);
    }

    /**
     * Set the value of screen.  This is really just a method to hide
     * using the RunData Parameter.
     *
     * @param v Value to assign to screen.
     */
    public void setScreenTemplate(String v)
    {
        data.getParameters().setString(URIConstants.CGI_TEMPLATE_PARAM, v);

        // We have changed the screen template so
        // we should now update the layout template
        // as well. We will use the template service
        // to help us out.
        try
        {
            setLayoutTemplate(TurbineTemplate.getLayoutTemplateName(v));
        }
        catch (Exception e)
        {
            /*
             * do nothing.
             */
        }
    }

    /**
     * Get the value of layout.
     *
     * @return A String with the value of layout.
     */
    public String getLayoutTemplate()
    {
        String value = getString(TemplateInfo.LAYOUT_TEMPLATE);
        return value;
    }

    /**
     * Set the value of layout.
     *
     * @param v Value to assign to layout.
     */
    public void setLayoutTemplate(String v)
    {
        setTemp(TemplateInfo.LAYOUT_TEMPLATE, v);
    }

    /**
     * Get the value of Template context.  This will be cast to the
     * proper Context by its Service.
     *
     * @param name The name of the template context.
     * @return An Object with the Value of context.
     */
    public Object getTemplateContext(String name)
    {
        return getTemp(name);
    }

    /**
     * Set the value of context.
     *
     * @param name The name of the template context.
     * @param v Value to assign to context.
     */
    public void setTemplateContext(String name, Object v)
    {
        setTemp(name, v);
    }

    /**
     * Get the value of service.
     *
     * @return A String with the value of service.
     */
    public String getService()
    {
        return getString(TemplateInfo.SERVICE_NAME);
    }

    /**
     * Set the value of service.
     *
     * @param v Value to assign to service.
     */
    public void setService(String v)
    {
        setTemp(TemplateInfo.SERVICE_NAME, v);
    }

    /**
     * Get an object from temporary storage.
     *
     * @param name A String with the name of the object.
     * @return An Object.
     */
    public Object getTemp(String name)
    {
        return tempStorage.get(name);
    }

    /**
     * Get an object from temporary storage, or a default value.
     *
     * @param name A String with the name of the object.
     * @param def An Object, the default value.
     * @return An Object.
     */
    public Object getTemp(String name, Object def)
    {
        try
        {
            Object val = tempStorage.get(name);
            return (val != null) ? val : def;
        }
        catch (Exception e)
        {
            return def;
        }
    }

    /**
     * Put an object into temporary storage.
     *
     * @param name A String with the name of the object.
     * @param value An Object, the value.
     */
    public void setTemp(String name, Object value)
    {
        tempStorage.put(name, value);
    }

    /**
     * Return a String[] from the temp hash map.
     *
     * @param name A String with the name of the object.
     * @return A String[].
     */
    public String[] getStringArray(String name)
    {
        String[] value = null;
        Object object = getTemp(name, null);
        if (object != null)
        {
            value = (String[]) object;
        }
        return value;
    }

    /**
     * Return a String from the temp hash map.
     *
     * @param name A String with the name of the object.
     * @return A String.
     */
    public String getString(String name)
    {
        String value = null;
        Object object = getTemp(name, null);
        if (object != null)
        {
            value = (String) object;
        }
        return value;
    }

    /**
     * Remove an object from the  temporary storage.
     *
     * @param name A String with the name of the object.
     * @return The object that was removed or <code>null</code>
     *         if the name was not a key.
     */
    public Object removeTemp(String name)
    {
        return tempStorage.remove(name);
    }

    /*
     * Returns all the available names in the temporary storage.
     *
     * @return A object array with the keys.
     */
    public Object[] getTempKeys()
    {
        return tempStorage.keySet().toArray();
    }
}
