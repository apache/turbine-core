package org.apache.turbine.util.template;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * Template context tool that can be used to set various attributes of a
 * HTML page.  This tool does not automatically make the changes in the HTML
 * page for you.  You must use this tool in your layout template to retrieve
 * the attributes.
 * <p>
 * The set/add methods are can be used from a screen template, action, screen
 * class, layour template, or anywhere else.  The get methods should be used in
 * your layout template(s) to construct the appropriate HTML tags.
 *<p>
 * Example usage of this tool to build the HEAD and BODY tags in your layout
 * templates:
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
 * <p>
 * Example usages of this tool in your screen templates:<br>
 *   <code>$page.addScript($content.getURI("myJavascript.js")<br>
 *   $page.setTitle("My page title")<br>
 *   $page.setHttpEquiv("refresh","5; URL=http://localhost/nextpage.html")</code>
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class HtmlPageAttributes
        implements ApplicationTool
{
    /** Logging */
    private static Log log = LogFactory.getLog(HtmlPageAttributes.class);

    /** The title */
    private String title;

    /** Body Attributes */
    private Map bodyAttributes = new HashMap();

    /** Script references */
    private List scripts = new ArrayList();

    /** Stylesheet references */
    private List styleSheets = new ArrayList();

    /** Inline styles */
    private List styles = new ArrayList();

    /** Meta tags for the HEAD */
    private Map metaTags = new HashMap();

    /** http-equiv tags */
    private Map httpEquivs = new HashMap();

    /** Doctype */
    private static String doctype = null;

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
        this.bodyAttributes.clear();
        this.scripts.clear();
        this.styleSheets.clear();
        this.styles.clear();
        this.metaTags.clear();
        this.httpEquivs.clear();
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
     * @deprecated Use addBodyAttribute instead.
     */
    public HtmlPageAttributes addAttribute(String name, String value)
    {
        log.info("Use of the addAttribute(name,value) method is deprecated.  Please use " +
                "addBodyAttribute(name,value) instead.");
        return addBodyAttribute(name, value);
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
    public Map getBodyAttributes()
    {
        return this.bodyAttributes;
    }

    /**
     * Adds a script reference
     *
     * @param scriptURL
     * @return a <code>HtmlPageAttributes</code> (self).
     */
    public HtmlPageAttributes addScript(String scriptURL)
    {
        this.scripts.add(scriptURL);
        return this;
    }

    /**
     * Adds a script reference
     *
     * @param scriptURL
     * @return a <code>HtmlPageAttributes</code> (self).
     * @deprecated Use addScript instead
     */
    public HtmlPageAttributes setScript(String scriptURL)
    {
        log.info("Use of the setScript(scriptURL) method is deprecated.  Please use " +
                "addScript(scriptURL) instead.");
        return addScript(scriptURL);
    }

    /**
     * Returns a collection of script URLs
     *
     * @return list of String objects constainings URLs of javascript files
     * to include
     */
    public List getScripts()
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
        StyleSheet ss = new StyleSheet(styleSheetURL);
        ss.setMedia(media);
        ss.setTitle(title);
        ss.setType(type);
        this.styleSheets.add(ss);
        return this;
    }

    /**
     * Adds a style sheet reference
     *
     * @param styleSheetURL
     * @return a <code>HtmlPageAttributes</code> (self).
     * @deprecated use addStyleSheet instead
     */
    public HtmlPageAttributes setStyleSheet(String styleSheetURL)
    {
        log.info("Use of the setStyleSheet(styleSheetURL) method is deprecated.  Please use " +
                "addStyleSheet(styleSheetURL) instead.");
        return addStyleSheet(styleSheetURL);
    }

    /**
     * Adds a style sheet reference
     *
     * @param styleSheetURL
     * @param media name of the media
     * @return a <code>HtmlPageAttributes</code> (self).
     * @deprecated use addStyleSheet instead
     */
    public HtmlPageAttributes setStyleSheet(String styleSheetURL, String media)
    {
        log.info("Use of the setStyleSheet(styleSheetURL,media) method is deprecated.  " +
                "Please use addStyleSheet(styleSheetURL,media) instead.");
        return addStyleSheet(styleSheetURL, media, null, "text/css");
    }

    /**
     * Returns a collection of script URLs
     *
     * @return list StyleSheet objects (inner class)
     */
    public List getStyleSheets()
    {
        return this.styleSheets;
    }

    /**
     * Adds a STYLE element to the HEAD of the page with the provided content.
     *
     * @param styleText The contents of the <code>style</code> tag.
     * @return a <code>HtmlPageAttributes</code> (self).
     * @deprecated use addStyle instead
     */
    public HtmlPageAttributes setStyle(String styleText)
    {
        log.info("Use of the setStyle(styleText) method is deprecated.  Please use " +
                "addStyle(styleText) instead.");
        return addStyle(styleText);
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
    public List getStyles()
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
        this.bodyAttributes.put("backgroup", url);
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
    public Map getHttpEquivs()
    {
        return this.httpEquivs;
    }

    /**
     * Gets the map of meta tags
     *
     * @return Map of http equiv names to the contents
     */
    public Map getMetaTags()
    {
        return this.metaTags;
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

    /**
     * Helper class to hold data about a stylesheet
     */
    public class StyleSheet
    {
        private String url;
        private String title;
        private String media;
        private String type;

        /**
         * Constructor requiring the URL to be set
         *
         * @param url URL of the external style sheet
         */
        public StyleSheet(String url)
        {
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
         * @param title
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

    }
    
    /**
     * Retrieve the default Doctype.  If Doctype is set to null, then an empty
     * string will be returned.  The default Doctype can be set in
     * TurbineResources as three strings giving the tag (e.g. "HTML"), dtd (e.g. 
     * "-//W3C//DTD HTML 4.01 Transitional//EN") and uri (e.g. 
     * "http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd").  For
     * backwards compatibility the default can be set using one of the single 
     * strings: Html40Strict, Html40Transitional, or Html40Frameset or as two 
     * strings providing the the dtd and uri (tag is assumed to be "HTML") - 
     * all but the three string configuration will result in an info level 
     * deprecation message being written to the log.
     *
     * @exception TurbineException If the default doctype is not specified in 
     * TurbineResources.properties.
     */
    public static String getDefaultDoctype()
            throws TurbineException
    {
        if (doctype == null)
        {
            String errMsg = "default.doctype property not set properly in " 
                    + "TurbineResources.properties!";
            Vector doctypeProperty = Turbine.getConfiguration()
                    .getVector(TurbineConstants.DEFAULT_DOCUMENT_TYPE_KEY);

            if (doctypeProperty != null)
            {
                String tag;
                String identifier;
                String uri;
                switch(doctypeProperty.size())
                {
                case 1:
                    {
                        String doc = (String) doctypeProperty.firstElement();
                        tag = "HTML";
                        identifier = "-//W3C//DTD HTML 4.0 ";
                        uri = "http://www.w3.org/TR/REC-html40/";
                        if (doc.equalsIgnoreCase(
                             TurbineConstants.DOCUMENT_TYPE_HTML40TRANSITIONAL))
                        {
                            identifier += "Transitional";
                            uri += "loose";
                        }
                        else if (doc.equalsIgnoreCase(
                                TurbineConstants.DOCUMENT_TYPE_HTML40STRICT))
                        {
                            uri += "strict";
                        }
                        else if (doc.equalsIgnoreCase(
                                TurbineConstants.DOCUMENT_TYPE_HTML40FRAMESET))
                        {
                            identifier = "Frameset";
                            uri += "frameset";
                        }
                        else
                        {
                            throw new TurbineException(errMsg);
                        }
                        identifier += "//EN";
                        uri += ".dtd";

                        log.info("Defining default.doctype with a single string"
                            + " in TurbineResources.properties is deprecated.  "
                            + "Please use three strings instead (tag, dtd and "
                            + "uri).");
                        break;
                    }
                case 2:
                    {
                        tag = "HTML";
                        identifier = (String) doctypeProperty.elementAt(0); 
                        uri = (String) doctypeProperty.elementAt(1);

                        log.info("Defining default.doctype with two strings"
                            + " in TurbineResources.properties is deprecated.  "
                            + "Please use three strings instead (tag, dtd and "
                            + "uri).");
                        break;
                    }
                case 3:
                    {
                        tag = (String) doctypeProperty.elementAt(0);
                        identifier = (String) doctypeProperty.elementAt(1); 
                        uri = (String) doctypeProperty.elementAt(2);
                        break;
                    }
                default:
                    {
                        throw new TurbineException(errMsg);
                    }
                }
                doctype = getDoctype(tag, identifier, uri);
            }
            else
            {
                doctype = "";
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
    private static String getDoctype(String tag, String identifier, String uri)
    {
        StringBuffer doctypeBuf = new StringBuffer("<!DOCTYPE ");
        doctypeBuf.append(tag);
        doctypeBuf.append(" PUBLIC \"");
        doctypeBuf.append(identifier);
        doctypeBuf.append("\" \"");
        doctypeBuf.append(uri);
        doctypeBuf.append("\">");
        return doctypeBuf.toString();
    }
    
}
