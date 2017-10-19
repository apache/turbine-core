package org.apache.turbine.services.ui;

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

import org.apache.turbine.services.Service;
import org.apache.turbine.util.ServerData;

/**
 * The UI service provides for shared access to User Interface (skin) files,
 * as well as the ability for non-default skin files to inherit properties from
 * a default skin. UITool is provided as a pull tool for accessing
 * skin properties from your templates.
 *
 * <p>Skins are lazy loaded in that they are not loaded until first used.
 *
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id$
 */
public interface UIService extends Service
{
    /**
     * The service identifier.
     */
    public String SERVICE_NAME = "UIService";

    /**
     * Refresh all skins.
     */
    public void refresh();

    /**
     * Refresh a particular skin.
     *
     * @param skinName the name of the skin to clear.
     */
    public void refresh(String skinName);

    /**
     * Provide access to the list of available skin names.
     *
     * @return the available skin names.
     */
    public String[] getSkinNames();

    /**
     * Get the name of the default skin name for the web application from the
     * TurbineResources.properties file. If the property is not present the
     * name of the default skin will be returned.  Note that the web application
     * skin name may be something other than default, in which case its
     * properties will default to the skin with the name "default".
     *
     * @return the name of the default skin for the web application.
     */
    public String getWebappSkinName();

    /**
     * Retrieve a skin property from the named skin.  If the property is not
     * defined in the named skin the value for the default skin will be
     * provided.  If the named skin does not exist then the skin configured for
     * the webapp will be used.  If the webapp skin does not exist the default
     * skin will be used.  If the default skin does not exist then
     * <code>null</code> will be returned.
     *
     * @param skinName the name of the skin to retrieve the property from.
     * @param key the key to retrieve from the skin.
     * @return the value of the property for the named skin (defaulting to the
     * default skin), the webapp skin, the default skin or <code>null</code>,
     * depending on whether or not the property or skins exist.
     */
    public String get(String skinName, String key);

    /**
     * Retrieve a skin property from the default skin for the webapp.  If the
     * property is not defined in the webapp skin the value for the default skin
     * will be provided.  If the webapp skin does not exist the default skin
     * will be used.  If the default skin does not exist then <code>null</code>
     * will be returned.
     *
     * @param key the key to retrieve.
     * @return the value of the property for the webapp skin (defaulting to the
     * default skin), the default skin or <code>null</code>, depending on
     * whether or not the property or skins exist.
     */
    public String get(String key);

    /**
     * Retrieve the URL for an image that is part of a skin. The images are
     * stored in the WEBAPP/resources/ui/skins/[SKIN]/images directory.
     *
     * <p>Use this if for some reason your server name, server scheme, or server
     * port change on a per request basis. I'm not sure if this would happen in
     * a load balanced situation. I think in most cases the image(String image)
     * method would probably be enough, but I'm not absolutely positive.
     *
     * @param skinName the name of the skin to retrieve the image from.
     * @param imageId the id of the image whose URL will be generated.
     * @param serverData the serverData to use as the basis for the URL.
     * @return the image URL
     */
    public String image(String skinName, String imageId, ServerData serverData);

    /**
     * Retrieve the URL for an image that is part of a skin. The images are
     * stored in the WEBAPP/resources/ui/skins/[SKIN]/images directory.
     *
     * @param skinName the name of the skin to retrieve the image from.
     * @param imageId the id of the image whose URL will be generated.
     * @return the image URL
     */
    public String image(String skinName, String imageId);

    /**
     * Retrieve the URL for the style sheet that is part of a skin. The style is
     * stored in the WEBAPP/resources/ui/skins/[SKIN] directory with the
     * filename skin.css
     *
     * <p>Use this if for some reason your server name, server scheme, or server
     * port change on a per request basis. I'm not sure if this would happen in
     * a load balanced situation. I think in most cases the style() method would
     * probably be enough, but I'm not absolutely positive.
     *
     * @param skinName the name of the skin to retrieve the style sheet from.
     * @param serverData the serverData to use as the basis for the URL.
     * @return the CSS URL
     */
    public String getStylecss(String skinName, ServerData serverData);

    /**
     * Retrieve the URL for the style sheet that is part of a skin. The style is
     * stored in the WEBAPP/resources/ui/skins/[SKIN] directory with the
     * filename skin.css
     *
     * @param skinName the name of the skin to retrieve the style sheet from.
     * @return the CSS URL
     */
    public String getStylecss(String skinName);

    /**
     * Retrieve the URL for a given script that is part of a skin. The script is
     * stored in the WEBAPP/resources/ui/skins/[SKIN] directory.
     *
     * <p>Use this if for some reason your server name, server scheme, or server
     * port change on a per request basis. I'm not sure if this would happen in
     * a load balanced situation. I think in most cases the style() method would
     * probably be enough, but I'm not absolutely positive.
     *
     * @param skinName the name of the skin to retrieve the image from.
     * @param filename the name of the script file.
     * @param serverData the serverData to use as the basis for the URL.
     * @return the script URL
     */
    public String getScript(String skinName, String filename,
            ServerData serverData);

    /**
     * Retrieve the URL for a given script that is part of a skin. The script is
     * stored in the WEBAPP/resources/ui/skins/[SKIN] directory.
     *
     * @param skinName the name of the skin to retrieve the image from.
     * @param filename the name of the script file.
     * @return the script URL
     */
    public String getScript(String skinName, String filename);

}
