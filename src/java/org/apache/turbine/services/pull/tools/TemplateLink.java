package org.apache.turbine.services.pull.tools;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.parser.ParameterParser;
import org.apache.turbine.util.uri.TemplateURI;

/**
 * This is a pull to to be used in Templates to convert links in
 * Templates into the correct references.
 *
 * The pull service might insert this tool into the Context.
 * in templates.  Here's an example of its Velocity use:
 *
 * <p><code>
 * $link.setPage("index.vm").addPathInfo("hello","world")
 * This would return: http://foo.com/Turbine/template/index.vm/hello/world
 * </code>
 *
 * <p>
 *
 * This is an application pull tool for the template system. You should <b>not</b>
 * use it in a normal application!
 *
 * @author <a href="mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */

public class TemplateLink
    implements ApplicationTool
{
    /** cache of the template name for getPage() */
    private String template = null;

    /** TemplateURI used as backend for this object */
    private TemplateURI templateURI = null;

    /** Logging */
    private static Log log = LogFactory.getLog(TemplateLink.class);

    /**
     * Default constructor
     * <p>
     * The init method must be called before use.
     */
    public TemplateLink()
    {
    }

    /*
     * ========================================================================
     *
     * Application Tool Interface
     *
     * ========================================================================
     *
     */

    /**
     * This will initialise a TemplateLink object that was
     * constructed with the default constructor (ApplicationTool
     * method).
     *
     * @param data assumed to be a RunData object
     */
    public void init(Object data)
    {
        // we just blithely cast to RunData as if another object
        // or null is passed in we'll throw an appropriate runtime
        // exception.

        templateURI = new TemplateURI((RunData) data);
    }

    /**
     * Refresh method - does nothing
     */
    public void refresh()
    {
        // empty
    }

    /*
     * ========================================================================
     *
     * getter/setter
     *
     * All setter return "this" so you can "chain" them together in the Context
     *
     * ========================================================================
     */

    /**
     * This will turn off the execution of res.encodeURL()
     * by making res == null. This is a hack for cases
     * where you don't want to see the session information
     *
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink setEncodeURLOff()
    {
        templateURI.clearResponse();
        return this;
    }

    /**
     * Sets the template variable used by the Template Service.
     *
     * @param template A String with the template name.
     * @return A TemplateLink.
     */
    public TemplateLink setPage(String template)
    {
        log.debug("setPage(" + template + ")");
        this.template = template;
        templateURI.setTemplate(template);
        return this;
    }

    /**
     * Gets the template variable used by the Template Service.
     * It is only available after setPage() has been called.
     *
     * @return The template name.
     */
    public String getPage()
    {
        return template;
    }

    /**
     * Sets the action= value for this URL.
     *
     * By default it adds the information to the path_info instead
     * of the query data.
     *
     * @param action A String with the action value.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink setAction(String action)
    {
        log.debug("setAction(" + action + ")");
        templateURI.setAction(action);
        return this;
    }

    /**
     * Sets the action= and eventSubmit= values for this URL.
     *
     * By default it adds the information to the path_info instead
     * of the query data.
     *
     * @param action A String with the action value.
     * @param event A string with the event name.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink setActionEvent(String action, String event)
    {
        log.debug("setActionEvent(" + action + ", "+ event +")");
        templateURI.setActionEvent(action, event);
        return this;
    }

    /**
     * Sets the screen= value for this URL.
     *
     * By default it adds the information to the path_info instead
     * of the query data.
     *
     * @param screen A String with the screen value.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink setScreen(String screen)
    {
        log.debug("setScreen(" + screen + ")");
        templateURI.setScreen(screen);
        return this;
    }

    /**
     * Sets a reference anchor (#ref).
     *
     * @param reference A String containing the reference.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink setReference(String reference)
    {
        templateURI.setReference(reference);
        return this;
    }

    /**
     * Returns the current reference anchor.
     *
     * @return A String containing the reference.
     */
    public String getReference()
    {
        return templateURI.getReference();
    }

    /*
     * ========================================================================
     *
     * Adding and removing Data from the Path Info and Query Data
     *
     * ========================================================================
     */


    /**
     * Adds a name=value pair for every entry in a ParameterParser
     * object to the path_info string.
     *
     * @param pp A ParameterParser.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink addPathInfo(ParameterParser pp)
    {
        templateURI.addPathInfo(pp);
        return this;
    }

    /**
     * Adds a name=value pair to the path_info string.
     *
     * @param name A String with the name to add.
     * @param value An Object with the value to add.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink addPathInfo(String name, Object value)
    {
        templateURI.addPathInfo(name, value);
        return this;
    }

    /**
     * Adds a name=value pair to the path_info string.
     *
     * @param name A String with the name to add.
     * @param value A String with the value to add.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink addPathInfo(String name, String value)
    {
        templateURI.addPathInfo(name, value);
        return this;
    }

    /**
     * Adds a name=value pair to the path_info string.
     *
     * @param name A String with the name to add.
     * @param value A double with the value to add.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink addPathInfo(String name, double value)
    {
        templateURI.addPathInfo(name, value);
        return this;
    }

    /**
     * Adds a name=value pair to the path_info string.
     *
     * @param name A String with the name to add.
     * @param value An int with the value to add.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink addPathInfo(String name, int value)
    {
        templateURI.addPathInfo(name, value);
        return this;
    }

    /**
     * Adds a name=value pair to the path_info string.
     *
     * @param name A String with the name to add.
     * @param value A long with the value to add.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink addPathInfo(String name, long value)
    {
        templateURI.addPathInfo(name, value);
        return this;
    }

    /**
     * Adds a name=value pair to the query string.
     *
     * @param name A String with the name to add.
     * @param value An Object with the value to add.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink addQueryData(String name, Object value)
    {
        templateURI.addQueryData(name, value);
        return this;
    }

    /**
     * Adds a name=value pair to the query string.
     *
     * @param name A String with the name to add.
     * @param value A String with the value to add.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink addQueryData(String name, String value)
    {
        templateURI.addQueryData(name, value);
        return this;
    }

    /**
     * Adds a name=value pair to the query string.
     *
     * @param name A String with the name to add.
     * @param value A double with the value to add.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink addQueryData(String name, double value)
    {
        templateURI.addQueryData(name, value);
        return this;
    }

    /**
     * Adds a name=value pair to the query string.
     *
     * @param name A String with the name to add.
     * @param value An int with the value to add.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink addQueryData(String name, int value)
    {
        templateURI.addQueryData(name, value);
        return this;
    }

    /**
     * Adds a name=value pair to the query string.
     *
     * @param name A String with the name to add.
     * @param value A long with the value to add.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink addQueryData(String name, long value)
    {
        templateURI.addQueryData(name, value);
        return this;
    }

    /**
     * Adds a name=value pair for every entry in a ParameterParser
     * object to the query string.
     *
     * @param pp A ParameterParser.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink addQueryData(ParameterParser pp)
    {
        templateURI.addQueryData(pp);
        return this;
    }

    /**
     * Removes all the path info elements.
     *
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink removePathInfo()
    {
        templateURI.removePathInfo();
        return this;
    }

    /**
     * Removes a name=value pair from the path info.
     *
     * @param name A String with the name to be removed.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink removePathInfo(String name)
    {
        templateURI.removePathInfo(name);
        return this;
    }

    /**
     * Removes all the query string elements.
     *
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink removeQueryData()
    {
        templateURI.removeQueryData();
        return this;
    }

    /**
     * Removes a name=value pair from the query string.
     *
     * @param name A String with the name to be removed.
     * @return A <code>TemplateLink</code> (self).
     */
    public TemplateLink removeQueryData(String name)
    {
        templateURI.removeQueryData(name);
        return this;
    }

    /**
     * Builds the URL with all of the data URL-encoded as well as
     * encoded using HttpServletResponse.encodeUrl(). The resulting
     * URL is absolute; it starts with http/https...
     *
     * <p>
     * <code><pre>
     * TemplateURI tui = new TemplateURI (data, "UserScreen");
     * tui.addPathInfo("user","jon");
     * tui.getAbsoluteLink();
     * </pre></code>
     *
     *  The above call to absoluteLink() would return the String:
     *
     * <p>
     * http://www.server.com/servlets/Turbine/screen/UserScreen/user/jon
     *
     * <p>
     * After rendering the URI, it clears the
     * pathInfo and QueryString portions of the TemplateURI. So you can
     * use the $link reference multiple times on a page and start over
     * with a fresh object every time.
     *
     * @return A String with the built URL.
     */
    public String getAbsoluteLink()
    {
        String output = templateURI.getAbsoluteLink();

        // This was added to use $link multiple times on a page and start
        // over with a fresh set of data every time.
        templateURI.removePathInfo();
        templateURI.removeQueryData();

        return output;
    }


    /**
     * Builds the URL with all of the data URL-encoded as well as
     * encoded using HttpServletResponse.encodeUrl(). The resulting
     * URL is relative to the webserver root.
     *
     * <p>
     * <code><pre>
     * TemplateURI tui = new TemplateURI (data, "UserScreen");
     * tui.addPathInfo("user","jon");
     * tui.getRelativeLink();
     * </pre></code>
     *
     *  The above call to absoluteLink() would return the String:
     *
     * <p>
     * /servlets/Turbine/screen/UserScreen/user/jon
     *
     * <p>
     * After rendering the URI, it clears the
     * pathInfo and QueryString portions of the TemplateURI. So you can
     * use the $link reference multiple times on a page and start over
     * with a fresh object every time.
     *
     * @return A String with the built URL.
     */
    public String getRelativeLink()
    {
        String output = templateURI.getRelativeLink();

        // This was added to use $link multiple times on a page and start
        // over with a fresh set of data every time.
        templateURI.removePathInfo();
        templateURI.removeQueryData();

        return output;
    }

    /**
     * Returns the URI. After rendering the URI, it clears the
     * pathInfo and QueryString portions of the TemplateURI. Equivalent
     * to the getAbsoluteLink() method of this class.
     *
     * @return A String with the URI in the form
     * http://foo.com/Turbine/template/index.wm/hello/world
     */
    public String toString()
    {
        return getAbsoluteLink();
    }

    /**
     * Returns the relative URI leaving the source intact. Use this
     * if you need the path_info and query data multiple times.
     * This is equivalent to $link.AbsoluteLink or just $link,
     * but does not reset the path_info and query data.
     *
     * @return A String with the URI in the form
     * http://foo.com/Turbine/template/index.wm/hello/world
     */
    public String getURI()
    {
        return templateURI.getAbsoluteLink();
    }

    /**
     * Returns the relative URI leaving the source intact. Use this
     * if you need the path_info and query data multiple times.
     * This is equivalent to $link.RelativeLink but does not reset
     * the path_info and query data.
     *
     * @return A String with the URI in the form
     * http://foo.com/Turbine/template/index.wm/hello/world
     */
    public String getRelativeURI()
    {
        return templateURI.getRelativeLink();
    }
}
