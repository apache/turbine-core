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


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.annotation.TurbineConfiguration;
import org.apache.turbine.services.pull.ApplicationTool;

/**
 * Template context tool that can be used to set various attributes of a
 * HTML page.  This tool does not automatically make the changes in the HTML
 * page for you.  You must use this tool in your layout template to retrieve
 * the attributes.
 * <p>
 * The set/add methods are can be used from a screen template, action, screen
 * class, layour template, or anywhere else.  The get methods should be used in
 * your layout template(s) to construct the appropriate HTML tags.
 * </p>
 * 
 *<p>
 * Example usage of this tool to build the HEAD and BODY tags in your layout
 * templates:
 * </p>
 * 
 * <p>
 *  <code>
 *  ## Set defaults for all pages using this layout.  Anything set here can<br>
 *  ## be overridden in the screen template.<br>
 *  $page.setTitle("My default page title");<br>
 *  $page.setHttpEquiv("Content-Style-Type","text/css")<br>
 *  $page.addStyleSheet($content.getURI("myStyleSheet.css"))<br>
 *  $page.addScript($content.getURI("globalJavascriptCode.js"))<br>
 *  <br>
 *  ## build the HTML, HEAD, and BODY tags dynamically<br>
 *  &lt;html&gt;<br>
 *    &lt;head&gt;<br>
 *      #if( $page.Title != "" )<br>
 *      &lt;title&gt;$page.Title&lt;/title&gt;<br>
 *      #end<br>
 *      #foreach($metaTag in $page.MetaTags.keySet())<br>
 *      &lt;meta name="$metaTag" content="$page.MetaTags.get($metaTag)"&gt;<br>
 *      #end<br>
 *      #foreach($httpEquiv in $page.HttpEquivs.keySet())<br>
 *      &lt;meta http-equiv="$httpEquiv" content="$page.HttpEquivs.get($httpEquiv)"&gt;<br>
 *      #end<br>
 *      #foreach( $styleSheet in $page.StyleSheets )<br>
 *        &lt;link rel="stylesheet" href="$styleSheet.Url"<br>
 *          #if($styleSheet.Type != "" ) type="$styleSheet.Type" #end<br>
 *          #if($styleSheet.Media != "") media="$styleSheet.Media" #end<br>
 *          #if($styleSheet.Title != "") title="$styleSheet.Title" #end<br>
 *        &gt;<br>
 *      #end<br>
 *      #foreach( $script in $page.Scripts )<br>
 *        &lt;script type="text/javascript" src="$script" language="JavaScript"&gt;&lt;/script&gt;<br>
 *      #end<br>
 *    &lt;/head&gt;<br>
 *<br>
 *    ## Construct the body tag.  Iterate through the body attributes to build the opening tag<br>
 *    &lt;body<br>
 *      #foreach( $attributeName in $page.BodyAttributes.keySet() )<br>
 *        $attributeName = "$page.BodyAttributes.get($attributeName)"<br>
 *      #end<br>
 *     &gt;
 * </code>
 * </p>
 * 
 * <p>
 * Example usages of this tool in your screen templates:<br>
 *   <code>$page.addScript($content.getURI("myJavascript.js")<br>
 *   $page.setTitle("My page title")<br>
 *   $page.setHttpEquiv("refresh","5; URL=http://localhost/nextpage.html")</code>
 * </p>
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id$
 */
public class HtmlPageAttributes
        implements ApplicationTool
{
    /** The title */
    private String title;

    /** Body Attributes */
    private final Map<String, String> bodyAttributes = new LinkedHashMap<String, String>();

    /** Script references */
    private final List<String> scripts = new ArrayList<String>();

    /** External references */
    private final List<LinkTag> linkTags = new ArrayList<LinkTag>();

    /** Inline styles */
    private final List<String> styles = new ArrayList<String>();

    /** Meta tags for the HEAD */
    private final Map<String, String> metaTags = new LinkedHashMap<String, String>();

    /** http-equiv tags */
    private final Map<String, String> httpEquivs = new LinkedHashMap<String, String>();

    /** Doctype */
    private String doctype = null;

    @TurbineConfiguration( TurbineConstants.DEFAULT_HTML_DOCTYPE_ROOT_ELEMENT_KEY )
    private String defaultHtmlDoctypeRootElement = TurbineConstants.DEFAULT_HTML_DOCTYPE_ROOT_ELEMENT_DEFAULT;

    @TurbineConfiguration( TurbineConstants.DEFAULT_HTML_DOCTYPE_IDENTIFIER_KEY )
    private String defaultHtmlDoctypeIdentifier;

    @TurbineConfiguration( TurbineConstants.DEFAULT_HTML_DOCTYPE_URI_KEY )
    private String defaultHtmlDoctypeUri;

    /**
     * Construct a new instance
     */
    public HtmlPageAttributes()
    {
        init(null);
    }

    /**
     * Initialize this instance.
     * (ApplicationTool method)
     *
     * @param data not used
     */
    @Override
    public void init(Object data)
    {
        this.title = null;
        this.bodyAttributes.clear();
        this.scripts.clear();
        this.linkTags.clear();
        this.styles.clear();
        this.metaTags.clear();
        this.httpEquivs.clear();
    }

    /**
     * Refresh method - does nothing
     */
    @Override
    public void refresh()
    {
        // empty
    }

    /**
     * Set the title in the page.  This returns an empty String so
     * that the template doesn't complain about getting a null return
     * value.  Subsequent calls to this method will replace the current
     * title.
     *
     * @param title A String with the title.
     * @return a <code>HtmlPageAttributes</code> (self).
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
        if (StringUtils.isEmpty(this.title))
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
     * @return a <code>HtmlPageAttributes</code> (self).
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
    public Map<String, String> getBodyAttributes()
    {
        return this.bodyAttributes;
    }

    /**
     * Adds a script reference
     *
     * @param scriptURL the url
     * @return a <code>HtmlPageAttributes</code> (self).
     */
    public HtmlPageAttributes addScript(String scriptURL)
    {
        this.scripts.add(scriptURL);
        return this;
    }

    /**
     * Returns a collection of script URLs
     *
     * @return list of String objects containing URLs of javascript files
     * to include
     */
    public List<String> getScripts()
    {
        return this.scripts;
    }

    /**
     * Adds a style sheet reference
     *
     * @param styleSheetURL URL of the style sheet
     * @return a <code>HtmlPageAttributes</code> (self).
     */
    public HtmlPageAttributes addStyleSheet(String styleSheetURL)
    {
        addStyleSheet(styleSheetURL, "screen", null, "text/css");
        return this;
    }

    /**
     * Adds a style sheet reference
     *
     * @param styleSheetURL URL of the style sheet
     * @param media name of the media
     * @param title title of the stylesheet
     * @param type content type
     * @return a <code>HtmlPageAttributes</code> (self).
     */
    public HtmlPageAttributes addStyleSheet(String styleSheetURL,
                                            String media, String title, String type)
    {
        LinkTag ss = new LinkTag("stylesheet", styleSheetURL);
        ss.setMedia(media);
        ss.setTitle(title);
        ss.setType(type);
        this.linkTags.add(ss);
        return this;
    }

    /**
     * Adds a generic external reference
     *
     * @param relation type of the reference (prev, next, first, last, top, etc.)
     * @param linkURL URL of the reference
     * @return a <code>HtmlPageAttributes</code> (self).
     */
    public HtmlPageAttributes addLink(String relation, String linkURL)
    {
        return addLink(relation, linkURL, null, null);
    }

    /**
     * Adds a generic external reference
     *
     * @param relation type of the reference (prev, next, first, last, top, etc.)
     * @param linkURL URL of the reference
     * @param title title of the reference
     * @return a <code>HtmlPageAttributes</code> (self).
     */
    public HtmlPageAttributes addLink(String relation, String linkURL, String title)
    {
        return addLink(relation, linkURL, title, null);
    }

    /**
     * Adds a generic external reference
     *
     * @param relation type of the reference (prev, next, first, last, top, etc.)
     * @param linkURL URL of the reference
     * @param title title of the reference
     * @param type content type
     * @return a <code>HtmlPageAttributes</code> (self).
     */
    public HtmlPageAttributes addLink(String relation, String linkURL, String title,
                                        String type)
    {
        LinkTag ss = new LinkTag(relation, linkURL);
        ss.setTitle(title);
        ss.setType(type);
        this.linkTags.add(ss);
        return this;
    }

    /**
     * Returns a collection of link URLs
     *
     * @return list LinkTag objects (inner class)
     */
    public List<LinkTag> getLinks()
    {
        return this.linkTags;
    }

    /**
     * Adds a STYLE element to the HEAD of the page with the provided content.
     *
     * @param styleText The contents of the <code>style</code> tag.
     * @return a <code>HtmlPageAttributes</code> (self).
     */
    public HtmlPageAttributes addStyle(String styleText)
    {
        this.styles.add(styleText);
        return this;
    }

    /**
     * Returns a collection of styles
     *
     * @return list of String objects containing the contents of style tags
     */
    public List<String> getStyles()
    {
        return this.styles;
    }

    /**
     * Set a keywords META tag in the HEAD of the page.
     *
     * @param keywords A String.
     * @return a <code>HtmlPageAttributes</code> (self).
     */
    public HtmlPageAttributes setKeywords(String keywords)
    {
        this.metaTags.put("keywords", keywords);
        return this;
    }

    /**
     * Sets a HttpEquiv META tag in the HEAD of the page, usage:
     * <br><code>setHttpEquiv("refresh", "5; URL=http://localhost/nextpage.html")</code>
     * <br><code>setHttpEquiv("Expires", "Tue, 20 Aug 1996 14:25:27 GMT")</code>
     *
     * @param httpEquiv The value to use for the http-equiv attribute.
     * @param content   The text for the content attribute of the meta tag.
     * @return a <code>HtmlPageAttributes</code> (self).
     */
    public HtmlPageAttributes setHttpEquiv(String httpEquiv, String content)
    {
        this.httpEquivs.put(httpEquiv, content);
        return this;
    }

    /**
     * Add a description META tag to the HEAD of the page.
     *
     * @param description A String.
     * @return a <code>HtmlPageAttributes</code> (self).
     */
    public HtmlPageAttributes setDescription(String description)
    {
        this.metaTags.put("description", description);
        return this;
    }

    /**
     * Set the background image for the BODY tag.
     *
     * @param url A String.
     * @return a <code>HtmlPageAttributes</code> (self).
     */
    public HtmlPageAttributes setBackground(String url)
    {
        this.bodyAttributes.put("background", url);
        return this;
    }

    /**
     * Set the background color for the BODY tag.  You can use either
     * color names or color values (e.g. "white" or "#ffffff" or
     * "ffffff").
     *
     * @param color A String.
     * @return a <code>HtmlPageAttributes</code> (self).
     */
    public HtmlPageAttributes setBgColor(String color)
    {
        this.bodyAttributes.put("BGCOLOR", color);
        return this;
    }

    /**
     * Set the text color for the BODY tag.  You can use either color
     * names or color values (e.g. "white" or "#ffffff" or "ffffff").
     *
     * @param color A String.
     * @return a <code>HtmlPageAttributes</code> (self).
     */
    public HtmlPageAttributes setTextColor(String color)
    {
        this.bodyAttributes.put("TEXT", color);
        return this;
    }

    /**
     * Set the link color for the BODY tag.  You can use either color
     * names or color values (e.g. "white" or "#ffffff" or "ffffff").
     *
     * @param color A String.
     * @return a <code>HtmlPageAttributes</code> (self).
     */
    public HtmlPageAttributes setLinkColor(String color)
    {
        this.bodyAttributes.put("LINK", color);
        return this;
    }

    /**
     * Set the visited link color for the BODY tag.
     *
     * @param color A String.
     * @return a <code>HtmlPageAttributes</code> (self).
     */
    public HtmlPageAttributes setVlinkColor(String color)
    {
        this.bodyAttributes.put("VLINK", color);
        return this;
    }

    /**
     * Set the active link color for the BODY tag.
     *
     * @param color A String.
     * @return a <code>HtmlPageAttributes</code> (self).
     */
    public HtmlPageAttributes setAlinkColor(String color)
    {
        this.bodyAttributes.put("ALINK", color);
        return this;
    }

    /**
     * Gets the map of http equiv tags
     *
     * @return Map of http equiv names to the contents
     */
    public Map<String, String> getHttpEquivs()
    {
        return this.httpEquivs;
    }

    /**
     * Gets the map of meta tags
     *
     * @return Map of http equiv names to the contents
     */
    public Map<String, String> getMetaTags()
    {
        return this.metaTags;
    }

    /**
     * A dummy toString method that returns an empty string.
     *
     * @return An empty String ("").
     */
    @Override
    public String toString()
    {
        return "";
    }

    /**
     * Helper class to hold data about a &lt;link ... /&gt; html header tag
     */
    public static class LinkTag
    {
        private String relation;
        private String url;
        private String title;
        private String media;
        private String type;

        /**
         * Constructor requiring the URL and relation to be set
         *
         * @param relation Relation type the external link such as prev, next,
         *        stylesheet, shortcut icon
         * @param url URL of the external link
         */
        public LinkTag(String relation, String url)
        {
            setRelation(relation);
            setUrl(url);
        }

        /**
         * Gets the content type of the style sheet
         *
         * @return content type
         */
        public String getType()
        {
            return (StringUtils.isEmpty(type) ? "" : type);
        }

        /**
         * Sets the content type of the style sheet
         *
         * @param type content type
         */
        public void setType(String type)
        {
            this.type = type;
        }

        /**
         * @return String representation of the URL
         */
        public String getUrl()
        {
            return url;
        }

        /**
         * Sets the URL of the external style sheet
         *
         * @param url The URL of the stylesheet
         */
        private void setUrl(String url)
        {
            this.url = url;
        }

        /**
         * Gets the title of the style sheet
         *
         * @return title
         */
        public String getTitle()
        {
            return (StringUtils.isEmpty(title) ? "" : title);
        }

        /**
         * Sets the title of the stylesheet
         *
         * @param title of the stylesheet
         */
        public void setTitle(String title)
        {
            this.title = title;
        }

        /**
         * Gets the media for which the stylesheet should be applied.
         *
         * @return name of the media
         */
        public String getMedia()
        {
            return (StringUtils.isEmpty(media) ? "" : media);
        }

        /**
         * Sets the media for which the stylesheet should be applied.
         *
         * @param media name of the media
         */
        public void setMedia(String media)
        {
            this.media = media;
        }

        /**
         * Gets the relation type of the tag.
         *
         * @return name of the relation
         */
        public String getRelation()
        {
            return (StringUtils.isEmpty(relation) ? "" : relation);
        }

        /**
         * Sets the relation type of the tag.
         *
         * @param relation name of the relation
         */
        public void setRelation(String relation)
        {
            this.relation = relation;
        }
    }

    /**
     * Retrieve the default Doctype as configured by the
     * TurbineResources.peoperties
     * default.doctype.root.element, default.doctype.identifier and
     * default.doctype.url properties (defaults are "HTML",
     * "-//W3C//DTD HTML 4.01 Transitional//EN" and
     * "http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd" respectively).
     *
     * @return the DOCTYPE tag constructed from the properties in
     * TurbineResources.properties.
     */
    public String getDefaultDoctype()
    {
        if (doctype == null)
        {
            String tag = defaultHtmlDoctypeRootElement;

            if (StringUtils.isEmpty(tag))
            {
                doctype = "";
            }
            else
            {
                doctype = getDoctype(tag, defaultHtmlDoctypeIdentifier, defaultHtmlDoctypeUri);
            }
        }

        return doctype;
    }

    /**
     * Build the doctype element.
     *
     * @param tag the tag whose DTD is being declared.
     * @param identifier the identifier for the doctype declaration.
     * @param uri the uri for the doctype declaration.
     * @return the doctype.
     */
    public String getDoctype(String tag, String identifier, String uri)
    {
        StringBuilder doctypeBuf = new StringBuilder("<!DOCTYPE ");
        doctypeBuf.append(tag);

        if (StringUtils.isNotEmpty(identifier))
        {
            doctypeBuf.append(" PUBLIC \"");
            doctypeBuf.append(identifier);
            doctypeBuf.append("\" \"");
            doctypeBuf.append(uri);
            doctypeBuf.append('"');
        }
        else if (StringUtils.isNotEmpty(uri))
        {
            doctypeBuf.append(" SYSTEM \"");
            doctypeBuf.append(uri);
            doctypeBuf.append('"');
        }

        doctypeBuf.append('>');

        return doctypeBuf.toString();
    }
}
