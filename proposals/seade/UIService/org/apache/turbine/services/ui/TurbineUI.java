package org.apache.turbine.services.ui;

/*
 * Copyright 2003-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.ServerData;

/** 
 * This is a convenience class provided to allow access to the UIService
 * through static methods.  The UIService should ALWAYS be accessed via
 * either this class or UITool.
 *
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id$
 * @see UIService
 * @see UITool
 */
public class TurbineUI
{
    /**
     * Refresh all skins.
     */
    public static void refresh()
    {
        ((UIService) TurbineServices.getInstance()
                .getService(UIService.SERVICE_NAME)).refresh();
    }

    /**
     * Refresh a particular skin.
     * 
     * @param skinName the name of the skin to clear.
     */
    public static void refresh(String skinName)
    {
        ((UIService) TurbineServices.getInstance()
                .getService(UIService.SERVICE_NAME)).refresh(skinName);
    }

    /**
     * Provide access to the list of available skin names.
     * 
     * @return the available skin names.
     */
    public static String[] getSkinNames()
    {
        return ((UIService) TurbineServices.getInstance()
                .getService(UIService.SERVICE_NAME)).getSkinNames();
    }

    /**
     * Get the name of the default skin name for the web application from the 
     * TurbineResources.propertiess file. If the property is not present the 
     * name of the default skin will be returned.  Note that the web application
     * skin name may be something other than default, in which case its 
     * properties will default to the skin with the name "default".
     * 
     * @return the name of the default skin for the web application.
     */
    public static String getWebappSkinName()
    {
        return ((UIService) TurbineServices.getInstance()
                .getService(UIService.SERVICE_NAME)).getWebappSkinName();
    }

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
    public static String get(String skinName, String key)
    {
        return ((UIService) TurbineServices.getInstance()
                .getService(UIService.SERVICE_NAME)).get(skinName, key);
    }

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
    public static String get(String key)
    {
        return ((UIService) TurbineServices.getInstance()
            .getService(UIService.SERVICE_NAME)).get(key);
    }

    /**
     * Retrieve the URL for an image that is part of a skin. The images are 
     * stored in the WEBAPP/resources/ui/skins/[SKIN]/images directory.
     *
     * <p>Use this if for some reason your server name, server scheme, or server 
     * port change on a per request basis. I'm not sure if this would happend in 
     * a load balanced situation. I think in most cases the image(String image)
     * method would probably be enough, but I'm not absolutely positive.
     * 
     * @param skinName the name of the skin to retrieve the image from.
     * @param imageId the id of the image whose URL will be generated.
     * @param data the RunData to use as the source of the ServerData to use as 
     * the basis for the URL.
     */
    public static String image(String skinName, String imageId, 
            ServerData serverData)
    {
        return ((UIService) TurbineServices.getInstance()
                .getService(UIService.SERVICE_NAME))
                        .image(skinName, imageId, serverData);
    }

//    public static String image(String skinName, String imageId, RunData data)
//    {
//        return image(skinName, imageId, data.getServerData());
//    }

    /**
     * Retrieve the URL for an image that is part of a skin. The images are 
     * stored in the WEBAPP/resources/ui/skins/[SKIN]/images directory.
     * 
     * @param skinName the name of the skin to retrieve the image from.
     * @param imageId the id of the image whose URL will be generated.
     */
    public static String image(String skinName, String imageId)
    {
        return ((UIService) TurbineServices.getInstance()
                .getService(UIService.SERVICE_NAME)).image(skinName, imageId);
    }

    /**
     * Retrieve the URL for the style sheet that is part of a skin. The style is 
     * stored in the WEBAPP/resources/ui/skins/[SKIN] directory with the 
     * filename skin.css
     *
     * <p>Use this if for some reason your server name, server scheme, or server 
     * port change on a per request basis. I'm not sure if this would happend in 
     * a load balanced situation. I think in most cases the style() method would 
     * probably be enough, but I'm not absolutely positive.
     * 
     * @param skinName the name of the skin to retrieve the style sheet from.
     * @param data the RunData to use as the source of the ServerData to use as 
     * the basis for the URL.
     */
    public static String getStylecss(String skinName, ServerData serverData)
    {
        return ((UIService) TurbineServices.getInstance()
                .getService(UIService.SERVICE_NAME))
                        .getStylecss(skinName, serverData);
    }

//    public static String getStylecss(String skinName, RunData data)
//    {
//        return getStylecss(skinName, data.getServerData());
//    }

    /**
     * Retrieve the URL for the style sheet that is part of a skin. The style is 
     * stored in the WEBAPP/resources/ui/skins/[SKIN] directory with the 
     * filename skin.css
     * 
     * @param skinName the name of the skin to retrieve the style sheet from.
     */
    public static String getStylecss(String skinName)
    {
        return ((UIService) TurbineServices.getInstance()
                .getService(UIService.SERVICE_NAME)).getStylecss(skinName);
    }

}
