package org.apache.turbine.util.template;

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
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.util.RunData;

/**
 * This is a wrapper for Template specific information.  It's part of
 * the RunData object and can extract the information it needs to do
 * the job directly from the data.getParameters().
 *
 * @author <a href="mailto:mbryson@mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id$
 */
public class TemplateInfo
{
    /* Constants for tempStorage hashtable. */
    public static final String NAVIGATION_TEMPLATE = "00navigation_template00";
    public static final String LAYOUT_TEMPLATE = "00layout_template00";
    public static final String SERVICE_NAME = "template_service";

    /* Handle to the RunData object. */
    private RunData data = null;

    /* Place to store information about templates. */
    private Hashtable tempStorage = null;

    /**
     * Constructor
     *
     * @param RunData A Turbine Rundata object.
     */
    public TemplateInfo(RunData data)
    {
        this.data = data;
        tempStorage = new Hashtable(10);
    }

    /**
     * Get the value of navigationTemplate.
     *
     * @return A String with the value of navigationTemplate.
     */
    public String getNavigationTemplate()
    {
        String temp = getString(TemplateInfo.NAVIGATION_TEMPLATE);
        if (temp != null)
        {
            temp = temp.replace(',', '/');
        }
        return temp;
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
     * information comes from PathInfo or a QueryString.  Note: The
     * template name is always "cleaned" with this method, which
     * replaces ',' with '/'.
     *
     * @return A String with the value of screen.
     */
    public String getScreenTemplate()
    {
        String temp = data.getParameters().getString("template", null);
        if (temp != null)
        {
            temp = temp.replace(',', '/');
        }
        return temp;
    }

    /**
     * Set the value of screen.  This is really just a method to hide
     * using the RunData Parameter.
     *
     * @param v Value to assign to screen.
     */
    public void setScreenTemplate(String v)
    {
       data.getParameters().setString("template", v);

       /*
        * We have changed the screen template so
        * we should now update the layout template
        * as well. We will use the template service
        * to help us out.
        */
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
        setTemp(TemplateInfo.LAYOUT_TEMPLATE,v);
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
            if (val == null)
            {
                return def;
            }
            return val;
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
     * Return a String[] from the temp hashtable.
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
     * Return a String from the temp hashtable.
     *
     * @param name A String with the name of the object.
     * @return A String.
     */
    public String getString(String name)
    {
        String value = null;
        Object object = getTemp(name,null);
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
