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

import org.apache.ecs.HtmlColor;
import org.apache.ecs.html.Link;
import org.apache.ecs.html.Meta;
import org.apache.ecs.html.Title;
import org.apache.ecs.html.Style;
import org.apache.ecs.html.Script;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.RunData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Template context tool that will set various attributes of the HTML
 * page.  It is automatically placed in the Template context as
 * '$page'.  Here's an example of some uses:
 *
 * <p>
 * $page.setBgColor("#ffffff");
 * $page.setBgColor("white");
 * $page.setBackground("/images/standardbg.jpeg");
 * $page.setTitle("This is the title!");
 * $page.setKeywords("turbine, cool, servlet framework");
 * $page.setStyleSheet("/style.css");
 *
 * @author <a href="mailto:sean@somacity.com">Sean Legassick</a>
 * @deprecated Use HtmlPageAttributes along with VelocityOnlyLayout instead
 * @version $Id$
 */
public class TemplatePageAttributes
    implements ApplicationTool
{
    /** Logging */
    private static Log log = LogFactory.getLog(TemplatePageAttributes.class);

    /** The RunData object. */
    private RunData data = null;

    /** The title. */
    private String cachedTitle = null;


    /**
     * Default constructor. The init method must be called before use
     */
    public TemplatePageAttributes()
    {
    }

    /**
     * Construct a new instance with the given RunData object.
     *
     * @param data a RunData instance
     */
    public TemplatePageAttributes(RunData data)
    {
        this.data = data;
    }

    /**
     * Initialise this instance with the given RunData object.
     * (ApplicationTool method)
     *
     * @param data Assumed to be a RunData instance
     */
    public void init(Object data)
    {
        log.warn("This class is deprecated.  Use HtmlPageAttributes instead.");

        // we blithely cast to RunData as the runtime error thrown
        // if data is null or not RunData is appropriate.
        this.data = (RunData) data;

        // clear cached title
        this.cachedTitle = null;
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
     * @param intitle A String with the title.
     */
    public TemplatePageAttributes setTitle(String intitle)
    {
        Title title = data.getPage().getTitle();
        if (cachedTitle != null)
        {
            cachedTitle += intitle;
        }
        else
        {
            cachedTitle = intitle;
        }
        title.addElement(intitle);
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
        if (cachedTitle == null)
        {
            return "";
        }
        return cachedTitle;
    }

    /**
     * Adds a LINK to a CSS styleshet to the HEAD of the page.
     *
     * @param url A String.
     * @return A TemplatePageAttributes (self).
     */
    public TemplatePageAttributes setStyleSheet(String url)
    {
        data.getPage().getHead().addElement(new Link()
                .setRel("stylesheet").setType("text/css").setHref(url));
        return this;
    }

    /**
     * Adds a LINK to a CSS stylesheet to the HEAD of the page, allowing the
     * media type to be specified.
     *
     * @param url The value for the <code>href</code> attribute.
     * @param media The value for the <code>media</code> attribute.
     * @return a <code>TemplatePageAttributes</code> (self).
     */
    public TemplatePageAttributes setStyleSheet(String url, String media)
    {
        data.getPage().getHead().addElement(new Link().setRel("stylesheet")
                .setType("text/css").setMedia(media).setHref(url));
        return this;
    }

    /**
     * Adds a STYLE element to the HEAD of the page with the provided content.
     *
     * @param styleText The contents of the <code>style</code> tag.
     * @return a <code>TemplatePageAttributes</code> (self).
     */
    public TemplatePageAttributes setStyle(String styleText)
    {
        data.getPage().getHead().addElement(new Style("text/css", styleText));
        return this;
    }

    /**
     * Adds a LINK to a javascript file to the HEAD of the page.
     *
     * @param url A String.
     * @return A TemplatePageAttributesEx (self).
     */
    public TemplatePageAttributes setScript(String url)
    {
        data.getPage().getHead().addElement(new Script().setSrc(url)
                .setType("text/javascript").setLanguage("JavaScript"));
        return this;
    }

    /**
     * Set a keywords META tag in the HEAD of the page.
     *
     * @param keywords A String.
     * @return A TemplatePageAttributes (self).
     */
    public TemplatePageAttributes setKeywords(String keywords)
    {
       data.getPage().getHead().addElement(
               new Meta().setName("keywords").setContent(keywords));
       return this;
    }

    /**
     * Sets a HttpEquiv META tag in the HEAD of the page, usage:
     * <br><code>setHttpEquiv("refresh", "5; URL=http://localhost/nextpage.html")</code>
     * <br><code>setHttpEquiv("Expires", "Tue, 20 Aug 1996 14:25:27 GMT")</code>
     *
     * @param httpEquiv The value to use for the http-equiv attribute.
     * @param content   The text for the content attribute of the meta tag.
     * @return A TemplatePageAttributes (self).
     */
    public TemplatePageAttributes setHttpEquiv(String httpEquiv, String content)
    {
       data.getPage().getHead().addElement(
               new Meta().setHttpEquiv(httpEquiv).setContent(content));
       return this;
    }

    /**
     * Add a description META tag to the HEAD of the page.
     *
     * @param description A String.
     * @return A TemplatePageAttributes (self).
     */
    public TemplatePageAttributes setDescription(String description)
    {
        data.getPage().getHead().addElement(
                new Meta().setName("description").setContent(description));
        return this;
    }

    /**
     * Set the background image for the BODY tag.
     *
     * @param url A String.
     * @return A TemplatePageAttributes (self).
     */
    public TemplatePageAttributes setBackground(String url)
    {
        data.getPage().getBody().setBackground(url);
        return this;
    }

    /**
     * Set the background color for the BODY tag.  You can use either
     * color names or color values (e.g. "white" or "#ffffff" or
     * "ffffff").
     *
     * @param color A String.
     * @return A TemplatePageAttributes (self).
     */
    public TemplatePageAttributes setBgColor(String color)
    {
        String hexColor = HtmlColor.getColor(color);
        if (hexColor == null)
        {
            hexColor = color;
        }
        data.getPage().getBody().setBgColor(hexColor);
        return this;
    }

    /**
     * Set the text color for the BODY tag.  You can use either color
     * names or color values (e.g. "white" or "#ffffff" or "ffffff").
     *
     * @param color A String.
     * @return A TemplatePageAttributes (self).
     */
    public TemplatePageAttributes setTextColor(String color)
    {
        String hexColor = HtmlColor.getColor(color);
        if (hexColor == null)
        {
            hexColor = color;
        }
        data.getPage().getBody().setText(hexColor);
        return this;
    }

    /**
     * Set the link color for the BODY tag.  You can use either color
     * names or color values (e.g. "white" or "#ffffff" or "ffffff").
     *
     * @param color A String.
     * @return A TemplatePageAttributes (self).
     */
    public TemplatePageAttributes setLinkColor(String color)
    {
        String hexColor = HtmlColor.getColor(color);
        if (hexColor == null)
        {
            hexColor = color;
        }
        data.getPage().getBody().setLink(hexColor);
        return this;
    }

    /**
     * Set the visited link color for the BODY tag.
     *
     * @param color A String.
     * @return A TemplatePageAttributes (self).
     */
    public TemplatePageAttributes setVlinkColor(String color)
    {
        String hexColor = HtmlColor.getColor(color);
        if (hexColor == null)
        {
            hexColor = color;
        }
        data.getPage().getBody().setVlink(hexColor);
        return this;
    }

    /**
     * Adds an attribute to the BODY tag.
     *
     * @param name A String.
     * @param value A String.
     * @return A TemplatePageAttributes (self).
     */
    public TemplatePageAttributes addAttribute(String name, String value)
    {
        data.getPage().getBody().addAttribute(name, value);
        return this;
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
