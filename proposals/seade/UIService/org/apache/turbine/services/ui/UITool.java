package org.apache.turbine.services.ui;

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

import org.apache.turbine.om.security.User;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.ServerData;

/**
 * Manages all UI elements for a Turbine Application. Any UI element can be 
 * accessed in any template using the $ui handle (assuming you use the default 
 * PullService configuration). So, for example, you could access the background 
 * colour for your pages by using $ui.bgcolor
 * <p>
 * This implementation provides a single level of inheritance in that if a 
 * property does not exist in a non-default skin, the value from the default 
 * skin will be used. By only requiring values different to those stored in 
 * the default skin to appear in the non-default skins the amount of memory
 * consumed in cases where the UserManager insance is used at a non-global 
 * scope will potentially be reduced due to the fact that a shared instance of 
 * the default skin properties can be used. Note that this inheritance only
 * applies to property values - it does not apply to any images or stylesheets
 * that may form part of your skins.
 * <p>
 * This is an application pull tool for the template system. You should not  
 * use it in a normal application!
 * <p>
 *
 * This is an application pull tool for the template system. You should 
 * <b>not</b> use it in a normal application (use UIService instead).
 *
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
    public static final String SKIN_ATTRIBUTE =
            UITool.class.getName()+ ".skin";

    /**
     * The actual skin being used for the webapp.
     */
    private String skinName;

    /**
     * Refresh the tool.
     */
    public void refresh()
    {
        TurbineUI.refresh(getSkin());
        log.debug("UITool refreshed for skin: " + getSkin());
    }

    /**
     * Provide access to the list of available skin names.
     * 
     * @return the available skin names.
     */
    public String[] getSkinNames()
    {
        return TurbineUI.getSkinNames();
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
    public String getWebappSkinName()
    {
        return TurbineUI.getWebappSkinName();
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
        return TurbineUI.get(getSkin(), key);
    }

    /**
     * Retrieve the skin name.
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
        skinName = TurbineUI.getWebappSkinName();
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
     * port change on a per request basis. I'm not sure if this would happend in 
     * a load balanced situation. I think in most cases the image(String image)
     * method would probably be enough, but I'm not absolutely positive.
     * 
     * @param imageId the id of the image whose URL will be generated.
     * @param data the RunDate to use as the source of the ServerData to use as 
     * the basis for the URL.
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
     * port change on a per request basis. I'm not sure if this would happend in 
     * a load balanced situation. I think in most cases the image(String image)
     * method would probably be enough, but I'm not absolutely positive.
     * 
     * @param imageId the id of the image whose URL will be generated.
     * @param serverData the serverData to use as the basis for the URL.
     */
    public String image(String imageId, ServerData serverData)
    {
        return TurbineUI.image(getSkin(), imageId, serverData);
    }

    /**
     * Retrieve the URL for an image that is part of the skin. The images are 
     * stored in the WEBAPP/resources/ui/skins/[SKIN]/images directory.
     * 
     * @param imageId the id of the image whose URL will be generated.
     */
    public String image(String imageId)
    {
        return TurbineUI.image(getSkin(), imageId);
    }

    /**
     * Retrieve the URL for the style sheet that is part of the skin. The style 
     * is stored in the WEBAPP/resources/ui/skins/[SKIN] directory with the 
     * filename skin.css
     *
     * <p>Use this if for some reason your server name, server scheme, or server 
     * port change on a per request basis. I'm not sure if this would happend in 
     * a load balanced situation. I think in most cases the style() method would 
     * probably be enough, but I'm not absolutely positive.
     * 
     * @param data the RunDate to use as the source of the ServerData to use as 
     * the basis for the URL.
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
     * port change on a per request basis. I'm not sure if this would happend in 
     * a load balanced situation. I think in most cases the style() method would 
     * probably be enough, but I'm not absolutely positive.
     * 
     * @param serverData the serverData to use as the basis for the URL.
     */
    public String getStylecss(ServerData serverData)
    {
        return TurbineUI.getStylecss(getSkin(), serverData);
    }

    /**
     * Retrieve the URL for the style sheet that is part of the skin. The style 
     * is stored in the WEBAPP/resources/ui/skins/[SKIN] directory with the 
     * filename skin.css
     */
    public String getStylecss()
    {
        return TurbineUI.getStylecss(getSkin());
    }

    /**
     * Initialize the UIManager object.
     *
     * @param data This is null, RunData or User depending upon specified tool scope.
     */
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
        else if (data instanceof User)
        {
            log.debug("UITool scope is session");
            setSkin((User) data);
        }
    }

}
