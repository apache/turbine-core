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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.services.ui.UIService;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.ServerData;

/**
 * Manages all UI elements for a Turbine Application. Any UI element can be
 * accessed in any template using the $ui handle (assuming you use the default
 * PullService configuration). So, for example, you could access the background
 * color for your pages by using $ui.bgcolor
 * <p>
 * This implementation provides a single level of inheritance in that if a
 * property does not exist in a non-default skin, the value from the default
 * skin will be used. By only requiring values different to those stored in
 * the default skin to appear in the non-default skins the amount of memory
 * consumed in cases where the UserManager instance is used at a non-global
 * scope will potentially be reduced due to the fact that a shared instance of
 * the default skin properties can be used. Note that this inheritance only
 * applies to property values - it does not apply to any images or stylesheets
 * that may form part of your skins.
 * <p>
 * This is an application pull tool for the template system. You should not
 * use it in a normal application!  Within Java code you should use TurbineUI.
 * <p>
 *
 * This is an application pull tool for the template system. You should
 * <strong>only</strong> use it in a normal application to set the skin
 * attribute for a user (setSkin(User user, String skin)) and to initialize it
 * for the user, otherwise use TurbineUI is probably the way to go.
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:james_coltman@majorband.co.uk">James Coltman</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id$
 * @see UIService
 */
public class UITool implements ApplicationTool
{
    /** Logging */
    private static Log log = LogFactory.getLog(UITool.class);

    /**
     * Attribute name of skinName value in User's temp hashmap.
     */
    public static final String SKIN_ATTRIBUTE = UITool.class.getName()+ ".skin";

    /**
     * The actual skin being used for the webapp.
     */
    private String skinName;

    /**
     * The UI service.
     */
    @TurbineService
    private UIService uiService;

    /**
     * Refresh the tool.
     */
    @Override
    public void refresh()
    {
        uiService.refresh(getSkin());
        log.debug("UITool refreshed for skin: " + getSkin());
    }

    /**
     * Provide access to the list of available skin names.
     *
     * @return the available skin names.
     */
    public String[] getSkinNames()
    {
        return uiService.getSkinNames();
    }

    /**
     * Get the name of the default skin name for the web application from the
     * TurbineResources.properties file. If the property is not present the
     * name of the default skin will be returned.  Note that the web application
     * skin name may be something other than default, in which case its
     * properties will default to the skin with the name "default".
     *
     * @return the name of the default skin for the web application.
     */
    public String getWebappSkinName()
    {
        return uiService.getWebappSkinName();
    }

    /**
     * Retrieve a skin property.  If the property is not defined in the current
     * skin the value for the default skin will be provided.  If the current
     * skin does not exist then the skin configured for the webapp will be used.
     * If the webapp skin does not exist the default skin will be used.  If the
     * default skin does not exist then <code>null</code> will be returned.
     *
     * @param key the key to retrieve from the skin.
     * @return the value of the property for the named skin (defaulting to the
     * default skin), the webapp skin, the default skin or <code>null</code>,
     * depending on whether or not the property or skins exist.
     */
    public String get(String key)
    {
        return uiService.get(getSkin(), key);
    }

    /**
     * Retrieve the skin name.
     * @return the selected skin name
     */
    public String getSkin()
    {
        return skinName;
    }

    /**
     * Set the skin name to the skin from the TurbineResources.properties file.
     * If the property is not present use the "default" skin.
     */
    public void setSkin()
    {
        skinName = uiService.getWebappSkinName();
    }

    /**
     * Set the skin name to the specified skin.
     *
     * @param skinName the skin name to use.
     */
    public void setSkin(String skinName)
    {
        this.skinName = skinName;
    }

    /**
     * Set the skin name when the tool is configured to be loaded on a
     * per-request basis. By default it calls getSkin to return the skin
     * specified in TurbineResources.properties. Developers can write a subclass
     * of UITool that overrides this method to determine the skin to use based
     * on information held in the request.
     *
     * @param data a RunData instance
     */
    protected void setSkin(RunData data)
    {
        setSkin();
    }

    /**
     * Set the skin name when the tool is configured to be loaded on a
     * per-session basis. If the user's temp hashmap contains a value in the
     * attribute specified by the String constant SKIN_ATTRIBUTE then that is
     * returned. Otherwise it calls getSkin to return the skin specified in
     * TurbineResources.properties.
     *
     * @param user a User instance
     */
    protected void setSkin(User user)
    {
        if (user.getTemp(SKIN_ATTRIBUTE) == null)
        {
            setSkin();
        }
        else
        {
            setSkin((String) user.getTemp(SKIN_ATTRIBUTE));
        }
    }

    /**
     * Set the skin name in the user's temp hashmap for the current session.
     *
     * @param user a User instance
     * @param skin the skin name for the session
     */
    public static void setSkin(User user, String skin)
    {
        user.setTemp(SKIN_ATTRIBUTE, skin);
    }

    /**
     * Retrieve the URL for an image that is part of the skin. The images are
     * stored in the WEBAPP/resources/ui/skins/[SKIN]/images directory.
     *
     * <p>Use this if for some reason your server name, server scheme, or server
     * port change on a per request basis. I'm not sure if this would happen in
     * a load balanced situation. I think in most cases the image(String image)
     * method would probably be enough, but I'm not absolutely positive.
     *
     * @param imageId the id of the image whose URL will be generated.
     * @param data the RunData to use as the source of the ServerData to use as
     * the basis for the URL.
     * @return the image URL
     */
    public String image(String imageId, RunData data)
    {
        return image(imageId, data.getServerData());
    }

    /**
     * Retrieve the URL for an image that is part of the skin. The images are
     * stored in the WEBAPP/resources/ui/skins/[SKIN]/images directory.
     *
     * <p>Use this if for some reason your server name, server scheme, or server
     * port change on a per request basis. I'm not sure if this would happen in
     * a load balanced situation. I think in most cases the image(String image)
     * method would probably be enough, but I'm not absolutely positive.
     *
     * @param imageId the id of the image whose URL will be generated.
     * @param serverData the serverData to use as the basis for the URL.
     * @return the image URL
     */
    public String image(String imageId, ServerData serverData)
    {
        return uiService.image(getSkin(), imageId, serverData);
    }

    /**
     * Retrieve the URL for an image that is part of the skin. The images are
     * stored in the WEBAPP/resources/ui/skins/[SKIN]/images directory.
     *
     * @param imageId the id of the image whose URL will be generated.
     * @return the image URL
     */
    public String image(String imageId)
    {
        return uiService.image(getSkin(), imageId);
    }

    /**
     * Retrieve the URL for the style sheet that is part of the skin. The style
     * is stored in the WEBAPP/resources/ui/skins/[SKIN] directory with the
     * filename skin.css
     *
     * <p>Use this if for some reason your server name, server scheme, or server
     * port change on a per request basis. I'm not sure if this would happen in
     * a load balanced situation. I think in most cases the style() method would
     * probably be enough, but I'm not absolutely positive.
     *
     * @param data the RunData to use as the source of the ServerData to use as
     * the basis for the URL.
     * @return the CSS URL
     */
    public String getStylecss(RunData data)
    {
        return getStylecss(data.getServerData());
    }

    /**
     * Retrieve the URL for the style sheet that is part of the skin. The style
     * is stored in the WEBAPP/resources/ui/skins/[SKIN] directory with the
     * filename skin.css
     *
     * <p>Use this if for some reason your server name, server scheme, or server
     * port change on a per request basis. I'm not sure if this would happen in
     * a load balanced situation. I think in most cases the style() method would
     * probably be enough, but I'm not absolutely positive.
     *
     * @param serverData the serverData to use as the basis for the URL.
     * @return the CSS URL
     */
    public String getStylecss(ServerData serverData)
    {
        return uiService.getStylecss(getSkin(), serverData);
    }

    /**
     * Retrieve the URL for the style sheet that is part of the skin. The style
     * is stored in the WEBAPP/resources/ui/skins/[SKIN] directory with the
     * filename skin.css
     * @return the CSS URL
     */
    public String getStylecss()
    {
        return uiService.getStylecss(getSkin());
    }

    /**
     * Retrieve the URL for a given script that is part of the skin. The script
     * is stored in the WEBAPP/resources/ui/skins/[SKIN] directory.
     *
     * <p>Use this if for some reason your server name, server scheme, or server
     * port change on a per request basis. I'm not sure if this would happen in
     * a load balanced situation. I think in most cases the image(String image)
     * method would probably be enough, but I'm not absolutely positive.
     *
     * @param filename the name of the script file whose URL will be generated.
     * @param data the RunDate to use as the source of the ServerData to use as
     * the basis for the URL.
     * @return the script URL
     */
    public String getScript(String filename, RunData data)
    {
        return getScript(filename, data.getServerData());
    }

    /**
     * Retrieve the URL for a given script that is part of the skin. The script
     * is stored in the WEBAPP/resources/ui/skins/[SKIN] directory.
     *
     * <p>Use this if for some reason your server name, server scheme, or server
     * port change on a per request basis. I'm not sure if this would happen in
     * a load balanced situation. I think in most cases the image(String image)
     * method would probably be enough, but I'm not absolutely positive.
     *
     * @param filename the name of the script file whose URL will be generated.
     * @param serverData the serverData to use as the basis for the URL.
     * @return the script URL
     */
    public String getScript(String filename, ServerData serverData)
    {
        return uiService.getScript(getSkin(), filename, serverData);
    }

    /**
     * Retrieve the URL for a given script that is part of the skin. The script
     * is stored in the WEBAPP/resources/ui/skins/[SKIN] directory.
     *
     * @param filename the name of the script file whose URL will be generated.
     * @return the script URL
     */
    public String getScript(String filename)
    {
        return uiService.getScript(getSkin(), filename);
    }

    /**
     * Initialize the UITool object.
     *
     * @param data This is null, RunData or User depending upon specified tool
     * scope.
     */
    @Override
    public void init(Object data)
    {
        if (data == null)
        {
            log.debug("UITool scope is global");
            setSkin();
        }
        else if (data instanceof RunData)
        {
            log.debug("UITool scope is request");
            setSkin((RunData) data);
        }
        else if (data instanceof PipelineData)
        {
            PipelineData pipelineData = (PipelineData) data;
            RunData runData = (RunData)pipelineData;
            log.debug("UITool scope is request");
            setSkin(runData);
        }
        else if (data instanceof User)
        {
            log.debug("UITool scope is session");
            setSkin((User) data);
        }
    }

}
