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
import java.util.Vector;

import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.RunData;

/**
 * Template context tool that can be used to set various attributes of a
 * HTML page.  This tool does not automatically make the changes in the HTML
 * page for you.  You must use this tool in your layout template to retrieve
 * the attributes.
 *
 * Here's an example of some uses:
 *
 * <p>
 * $page.setTitle("This is the title!");
 * $page.setStyleSheet("/style.css");
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class HtmlPageAttributes
        implements ApplicationTool
{
    /** The title */
    private String title;

    /** Body Attributes */
    private Hashtable bodyAttributes;

    /** Script references */
    private Vector scripts;

    /** Stylesheet references */
    private Vector styleSheets;

    /**
     * Default constructor. The init method must be called before use
     */
    public HtmlPageAttributes()
    {
    }

    /**
     * Construct a new instance with the given RunData object.
     *
     * @param data a RunData instance
     */
    public HtmlPageAttributes(RunData data)
    {
        init(data);
    }

    /**
     * Initialise this instance with the given RunData object.
     * (ApplicationTool method)
     *
     * @param data Assumed to be a RunData instance
     */
    public void init(Object data)
    {
        this.title = null;

        if(this.bodyAttributes == null)
        {
            this.bodyAttributes = new Hashtable();
        }
        else
        {
            this.bodyAttributes.clear();
        }

        if(this.scripts == null)
        {
            this.scripts = new Vector();
        }
        else
        {
            this.scripts.clear();
        }

        if(this.styleSheets == null)
        {
            this.styleSheets = new Vector();
        }
        else
        {
            this.styleSheets.clear();
        }
    }

    /**
     * Refresh method - does nothing
     */
    public void refresh()
    {
        // empty
    }

    /**
     * Set the title in the page.  This returns an empty String so
     * that the template doesn't complain about getting a null return
     * value.
     *
     * @param title A String with the title.
     */
    public HtmlPageAttributes setTitle(String title)
    {
        this.title = title;
        return this;
    }

    /**
     * Get the title in the page.  This returns an empty String if
     * empty so that the template doesn't complain about getting a null
     * return value.
     *
     * @return A String with the title.
     */
    public String getTitle()
    {
        if(title == null)
        {
            return "";
        }
        return title;
    }

    /**
     * Adds an attribute to the BODY tag.
     *
     * @param name A String.
     * @param value A String.
     * @return A TemplatePageAttributes (self).
     */
    public HtmlPageAttributes addBodyAttribute(String name, String value)
    {
        this.bodyAttributes.put(name, value);
        return this;
    }

    /**
     * Returns the map of body attributes
     *
     * @return the map
     */
    public Hashtable getBodyAttributes()
    {
        return this.bodyAttributes;
    }

    /**
     * Adds a script reference
     *
     * @param scriptURL
     * @return
     */
    public HtmlPageAttributes addScript(String scriptURL)
    {
        this.scripts.add(scriptURL);
        return this;
    }

    /**
     * Returns a collection of script URLs
     *
     * @return
     */
    public Vector getScripts()
    {
        return this.scripts;
    }

    /**
     * Adds a style sheet reference
     *
     * @param styleSheetURL
     * @return
     */
    public HtmlPageAttributes addStyleSheet(String styleSheetURL)
    {
        this.styleSheets.add(styleSheetURL);
        return this;
    }

    /**
     * Returns a collection of script URLs
     *
     * @return
     */
    public Vector getStyleSheets()
    {
        return this.styleSheets;
    }

    /**
     * A dummy toString method that returns an empty string.
     *
     * @return An empty String ("").
     */
    public String toString()
    {
        return "";
    }
}
