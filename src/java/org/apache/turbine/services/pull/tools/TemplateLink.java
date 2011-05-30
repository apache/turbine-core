package org.apache.turbine.services.pull.tools;


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


import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.parser.ParameterParser;
import org.apache.turbine.Turbine;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.RunData;
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
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */

public class TemplateLink
    implements ApplicationTool
{
    /** Prefix for Parameters for this tool */
    public static final String TEMPLATE_LINK_PREFIX = "tool.link";

    /** Should this tool return relative URIs or absolute? Default: Absolute. */
    public static final String TEMPLATE_LINK_RELATIVE_KEY = "want.relative";

    /** Default Value for TEMPLATE_LINK_RELATIVE_KEY */
    public static final boolean TEMPLATE_LINK_RELATIVE_DEFAULT = false;


    /** Do we want a relative link? */
    protected boolean wantRelative = false;

    /** cache of the template name for getPage() */
    protected String template = null;

    /** TemplateURI used as backend for this object */
    protected TemplateURI templateURI = null;

    /** Logging */
    private static Log log = LogFactory.getLog(TemplateLink.class);

    /**
     * Default constructor
     * <p>
     * The init method must be called before use.
     */
    public TemplateLink()
    {
        // empty
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
        if (data instanceof PipelineData)
        {
            PipelineData pipelineData = (PipelineData) data;
            RunData runData = (RunData)pipelineData;
            templateURI = new TemplateURI(runData);
        }
        else
        {
            templateURI = new TemplateURI((RunData) data);
        }

        Configuration conf =
                Turbine.getConfiguration().subset(TEMPLATE_LINK_PREFIX);

        if (conf != null)
        {
            wantRelative = conf.getBoolean(TEMPLATE_LINK_RELATIVE_KEY,
                    TEMPLATE_LINK_RELATIVE_DEFAULT);
        }

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
     * pathInfo and QueryString portions of the TemplateURI.
     *
     * @return A String with the URI in the form
     * http://foo.com/Turbine/template/index.wm/hello/world
     */
    public String getLink()
    {
        return wantRelative ?
                getRelativeLink() : getAbsoluteLink();
    }

    /**
     * Returns the relative URI leaving the source intact. Use this
     * if you need the path_info and query data multiple times.
     * This is equivalent to $link.Link or just $link,
     * but does not reset the path_info and query data.
     *
     * @return A String with the URI in the form
     * http://foo.com/Turbine/template/index.wm/hello/world
     */
    public String getURI()
    {
        return wantRelative ?
                templateURI.getRelativeLink() : templateURI.getAbsoluteLink();
    }

    /**
     * Returns the absolute URI leaving the source intact. Use this
     * if you need the path_info and query data multiple times.
     * This is equivalent to $link.AbsoluteLink but does not reset
     * the path_info and query data.
     *
     * @return A String with the URI in the form
     * http://foo.com/Turbine/template/index.wm/hello/world
     */
    public String getAbsoluteURI()
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

    /**
     * Same as getLink().
     *
     * @return A String with the URI represented by this object.
     *
     */
    @Override
    public String toString()
    {
        return getLink();
    }
}
