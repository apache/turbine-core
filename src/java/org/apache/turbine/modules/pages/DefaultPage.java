package org.apache.turbine.modules.pages;


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


import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ecs.Doctype;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.Action;
import org.apache.turbine.modules.ActionLoader;
import org.apache.turbine.modules.Layout;
import org.apache.turbine.modules.LayoutLoader;
import org.apache.turbine.modules.Page;
import org.apache.turbine.modules.Screen;
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.assemblerbroker.TurbineAssemblerBroker;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * When building sites using templates, Screens need only be defined
 * for templates which require dynamic (database or object) data.
 *
 * <p>
 *
 * This page can be used on sites where the number of Screens can be
 * much less than the number of templates.  The templates can be
 * grouped in directories with common layouts.  Screen modules are
 * then expected to be placed in packages corresponding with the
 * templates' directories and follow a specific naming scheme.
 *
 * <p>
 *
 * The template parameter is parsed and and a Screen whose package
 * matches the templates path and shares the same name minus any
 * extension and beginning with a capital letter is searched for.  If
 * not found, a Screen in a package matching the template's path with
 * name Default is searched for.  If still not found, a Screen with
 * name Default is looked for in packages corresponding to parent
 * directories in the template's path until a match is found.
 *
 * <p>
 *
 * For example if data.getParameters().getString("template") returns
 * /about_us/directions/driving.wm, the search follows
 * about_us.directions.Driving, about_us.directions.Default,
 * about_us.Default, Default, VelocitySiteScreen.
 *
 * <p>
 *
 * Only one Layout module is used, since it is expected that any
 * dynamic content will be placed in navigations and screens.  The
 * layout template to be used is found in a similar way to the Screen.
 * For example the following paths will be searched in the layouts
 * subdirectory: /about_us/directions/driving.wm,
 * /about_us/directions/default.wm, /about_us/default.wm, /default.wm.
 *
 * <p>
 *
 * This approach allows a site with largely static content to be
 * updated and added to regularly by those with little Java
 * experience.
 *
 * <p>
 *
 * The code is an almost a complete clone of the FreeMarkerSitePage
 * written by John McNally.  I've only modified it for Template use.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class DefaultPage
    extends Page
{
    /** Logging */
    protected Log log = LogFactory.getLog(this.getClass());

    protected ActionLoader actionLoader;
    protected ScreenLoader screenLoader;
    protected LayoutLoader layoutLoader;

    /**
     * Default constructor
     */
    public DefaultPage()
    {
        super();

        this.actionLoader = (ActionLoader)TurbineAssemblerBroker.getLoader(Action.NAME);
        this.screenLoader = (ScreenLoader)TurbineAssemblerBroker.getLoader(Screen.NAME);
        this.layoutLoader = (LayoutLoader)TurbineAssemblerBroker.getLoader(Layout.NAME);
    }

    /**
     * Builds the Page.
     *
     * @deprecated Use PipelineData version instead
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    public void doBuild(RunData data)
            throws Exception
    {
        // Template pages can use this to set up the context, so it is
        // available to the Action and Screen.  It does nothing here.
        doBuildBeforeAction(data);

        // If an action has been defined, execute it here.  Actions
        // can re-define the template definition.
        if (data.hasAction())
        {
            actionLoader.exec(data, data.getAction());
        }

        // if a redirect was setup in data, don't do anything else
        if (StringUtils.isNotEmpty(data.getRedirectURI()))
        {
            return;
        }

        // Set the default doctype from the value given in
        // TurbineResources.properties.
        setDefaultDoctype(data);

        // Template pages can use this to set up default templates and
        // associated class modules.  It does nothing here.
        doBuildAfterAction(data);

        String screenName = data.getScreen();

        log.debug("Building " + screenName);

        // Ask the Screen for its Layout and then execute the Layout.
        // The Screen can override the getLayout() method to re-define
        // the Layout depending on data passed in via the
        // data.parameters object.
        Screen aScreen = screenLoader.getInstance(screenName);
        String layout = aScreen.getLayout(data);

        // If the Layout has been set to be null, attempt to execute
        // the Screen that has been defined.
        if (layout != null)
        {
            layoutLoader.exec(data, layout);
        }
        else
        {
            screenLoader.exec(data, screenName);
        }

        // Do any post build actions (overridable by subclasses -
        // does nothing here).
        doPostBuild(data);
    }

    /**
     * Builds the Page.
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    public void doBuild(PipelineData pipelineData)
            throws Exception
    {
        RunData data = getRunData(pipelineData);
        // Template pages can use this to set up the context, so it is
        // available to the Action and Screen.  It does nothing here.
        doBuildBeforeAction(pipelineData);

        // If an action has been defined, execute it here.  Actions
        // can re-define the template definition.
        if (data.hasAction())
        {
            actionLoader.exec(pipelineData, data.getAction());
        }

        // if a redirect was setup in data, don't do anything else
        if (StringUtils.isNotEmpty(data.getRedirectURI()))
        {
            return;
        }

        // Set the default doctype from the value given in
        // TurbineResources.properties.
        setDefaultDoctype(data);

        // Template pages can use this to set up default templates and
        // associated class modules.  It does nothing here.
        doBuildAfterAction(pipelineData);

        String screenName = data.getScreen();

        log.debug("Building " + screenName);

        // Ask the Screen for its Layout and then execute the Layout.
        // The Screen can override the getLayout() method to re-define
        // the Layout depending on data passed in via the
        // data.parameters object.
        Screen aScreen = screenLoader.getInstance(screenName);
        String layout = aScreen.getLayout(pipelineData);

        // If the Layout has been set to be null, attempt to execute
        // the Screen that has been defined.
        if (layout != null)
        {
            layoutLoader.exec(pipelineData, layout);
        }
        else
        {
            screenLoader.exec(pipelineData, screenName);
        }

        // Do any post build actions (overridable by subclasses -
        // does nothing here).
        doPostBuild(pipelineData);
    }



    /**
     * Can be used by template Pages to stuff the Context into the
     * RunData so that it is available to the Action module and the
     * Screen module via getContext().  It does nothing here.
     *
     * @deprecated Use PipelineData version instead
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected void doBuildBeforeAction(RunData data)
            throws Exception
    {
        // do nothing by default
    }

    /**
     * Can be overridden by template Pages to set up data needed to
     * process a template.  It does nothing here.
     *
     * @deprecated Use PipelineData version instead
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected void doBuildAfterAction(RunData data)
            throws Exception
    {
        // do nothing by default
    }

    /**
     * Can be overridden to perform actions when the request is
     * fully processed. It does nothing here.
     * @deprecated Use PipelineData version instead
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected void doPostBuild(RunData data)
            throws Exception
    {
        // do nothing by default
    }


    /**
     * Can be used by template Pages to stuff the Context into the
     * RunData so that it is available to the Action module and the
     * Screen module via getContext().  It does nothing here.
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected void doBuildBeforeAction(PipelineData pipelineData)
            throws Exception
    {
        // do nothing by default
    }

    /**
     * Can be overridden by template Pages to set up data needed to
     * process a template.  It does nothing here.
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected void doBuildAfterAction(PipelineData pipelineData)
            throws Exception
    {
        // do nothing by default
    }

    /**
     * Can be overridden to perform actions when the request is
     * fully processed. It does nothing here.
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected void doPostBuild(PipelineData pipelineData)
            throws Exception
    {
        // do nothing by default
    }

    /**
     * Set the default Doctype.  If Doctype is set to null, it will
     * not be added.  The default Doctype can be set in
     * TurbineResources by using the single strings: Html40Strict,
     * Html40Transitional, or Html40Frameset.  Additionally the
     * default can be supplied as two strings giving the dtd and uri.
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    private void setDefaultDoctype(RunData data)
            throws Exception
    {
        String errMsg =
                "default.doctype property not set properly in TurbineResources.properties!";
        List doctypeProperty =
            Turbine.getConfiguration().getList(TurbineConstants.DEFAULT_DOCUMENT_TYPE_KEY);

        if (doctypeProperty != null)
        {
            switch(doctypeProperty.size())
            {
            case 0:
                {
                    // Don't add a doctype.
                    break;
                }
            case 1:
                {
                    String doc = (String) doctypeProperty.get(0);
                    if (doc.equalsIgnoreCase(TurbineConstants.DOCUMENT_TYPE_HTML40TRANSITIONAL))
                    {
                        data.getPage().setDoctype(new Doctype.Html40Transitional());
                    }
                    else if (doc.equalsIgnoreCase(TurbineConstants.DOCUMENT_TYPE_HTML40STRICT))
                    {
                        data.getPage().setDoctype(new Doctype.Html40Strict());
                    }
                    else if (doc.equalsIgnoreCase(TurbineConstants.DOCUMENT_TYPE_HTML40FRAMESET))
                    {
                        data.getPage().setDoctype(new Doctype.Html40Frameset());
                    }
                    else
                    {
                        throw new TurbineException(errMsg);
                    }
                    break;
                }
            case 2:
                {
                    data.getPage()
                        .setDoctype(new Doctype()
                                    .setIdentifier((String) doctypeProperty.get(0))
                                    .setUri((String) doctypeProperty.get(1)));
                    break;
                }
            default:
                {
                    throw new TurbineException(errMsg);
                }
            }
        }
    }
}
