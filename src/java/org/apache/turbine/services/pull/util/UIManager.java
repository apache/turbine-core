package org.apache.turbine.services.pull.util;

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

import java.io.FileInputStream;
import java.util.Properties;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.services.pull.TurbinePull;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.util.ContentURI;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.RunData;


/**
 * UIManager.java
 * <br>
 * Manages all UI elements for a Turbine Application. Any
 * UI element can be accessed in any template using the
 * $ui handle (assuming you use the default PullService
 * configuration). So, for example, you could access
 * the background colour for your pages by using
 * $ui.bgcolor
 * <p>
 * <h3>Questions:</h3>
 * What is the best way to allow an application
 * to be skinned. And how to allow the flexible
 * altering of a particular UI element in certain
 * parts of the template hierarchy. For example
 * on one section of your site you might like
 * a certain bgcolor, on another part of your
 * site you might want another. How can be let
 * the designer specify these properties and
 * still use the single $app.ui.bgcolor in
 * all the templates.
 * <p>
 * It would also be very cool to use some form
 * of inheritence for UI elements. Say a $ui.bgcolor
 * is used in a template where the bgcolor is not
 * set for that part of hierarch, it would be cool
 * if it could find the setting for the bgcolor
 * in the parent directory. So you could override
 * a UI element where you wanted and the system
 * would fall back to the parent when necessary.
 * <p>
 * How to specify skins, how to deal with images,
 * how could this be handled with a web app.
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:james_coltman@majorband.co.uk">James Coltman</a>
 * @version $Id$
 */
public class UIManager implements ApplicationTool
{
    /**
     * The location of the skins within the application
     * resources directory.
     */
    private static final String SKINS_DIRECTORY = "/ui/skins";

    /**
     * The name of the directory where images are
     * stored for this skin.
     */
    private static final String IMAGES_DIRECTORY = "/images";


    /**
     * Property tag for the skin that is to be
     * used for the web application.
     */
    private static final String SKIN_PROPERTY = "tool.ui.skin";

    /**
     * Default skin name. This name actually represents
     * a directory in the WEBAPP/resources/ui/skins
     * directory. There is a file called skin.props
     * which actually contains the name/value pairs.
     */
    private static final String SKIN_PROPERTY_DEFAULT = "default";

    /**
     * Attribute name of skinName value in User's temp hashmap.
     */
    private static final String SKIN_ATTRIBUTE =
        "org.apache.turbine.services.pull.util.UIManager.skin";

    /**
     * The actual skin being used for the webapp.
     */
    private String skinName;

    /**
     * The skins directory.
     */
    private String skinsDirectory;

    /**
     * The file within the skin directory that actually
     * contains the name/value pairs for the skin.
     */
    private static final String SKIN_PROPS_FILE = "skin.props";

    /**
     * The file name for the skin style sheet.
     */
    private static final String SKIN_CSS_FILE = "skin.css";

    /**
     * This the resources directory relative to the
     * webapp context. Used for constructing correct
     * URIs for retrieving images in image().
     */
    private String resourcesDirectory;

    /**
     * Properties to hold the name/value pairs
     * for the skin.
     */
    private Properties skinProperties;

    /**
     * Initialize the UIManager object.
     *
     * @param data This is null, RunData or User depending upon specified tool scope.
     */
    public void init(Object data)
    {
        /**
         * Store the resources directory for use in image().
         */
        resourcesDirectory = TurbinePull.getResourcesDirectory();

        if (data == null)
        {
            Log.debug("UI Manager scope is global");
            setSkin();
        }
        else if (data instanceof RunData)
        {
            Log.debug("UI Manager scope is request");
            setSkin((RunData) data);
        }
        else if (data instanceof User)
        {
            Log.debug("UI Manager scope is session");
            setSkin((User) data);
        }

        skinsDirectory =
            TurbinePull.getAbsolutePathToResourcesDirectory() + SKINS_DIRECTORY;

        loadSkin();
    }

    /**
     * This lets the tool know that it should be
     * refreshed. The tool can perform whatever actions
     * are necessary to refresh itself. This is necessary
     * for sane development where you probably want the
     * tools to refresh themselves on every request.
     */
    public void refresh()
    {
        Log.debug("Refreshing UI manager");

        loadSkin();
    }

    /**
     * Retrieve a property from the properties held
     * within the properties file for this skin.
     */
    public String get(String key)
    {
        return skinProperties.getProperty(key);
    }

    /**
     * Retrieve the skin name.
     */
    public String getSkin()
    {
        return skinName;
    }

    /**
     * Retrieve the URL for an image that is part
     * of a skin. The images are stored in the
     * WEBAPP/resources/ui/skins/<SKIN>/images
     * directory.
     *
     * Use this if for some reason your server name,
     * server scheme, or server port change on a
     * per request basis. I'm not sure if this
     * would happend in a load balanced situation.
     * I think in most cases the image(String image)
     * method would probably be enough, but I'm not
     * absolutely positive.
     */
    public String image(String imageId, RunData data)
    {
        ContentURI cu = new ContentURI(data);
        StringBuffer sb = new StringBuffer();

        sb.append(resourcesDirectory).
           append(SKINS_DIRECTORY).
           append("/").
           append(getSkin()).
           append(IMAGES_DIRECTORY).
           append("/").
           append(imageId);

        return cu.getURI(sb.toString());
    }

    /**
     * Retrieve the URL for an image that is part
     * of a skin. The images are stored in the
     * WEBAPP/resources/ui/skins/<SKIN>/images
     * directory.
     */
    public String image(String imageId)
    {
        StringBuffer sb = new StringBuffer();

        sb.append(TurbineServlet.getServerScheme()).
           append("://").
           append(TurbineServlet.getServerName()).
           append(":").
           append(TurbineServlet.getServerPort()).
           append(TurbineServlet.getContextPath()).
           append(resourcesDirectory).
           append(SKINS_DIRECTORY).
           append("/").
           append(getSkin()).
           append(IMAGES_DIRECTORY).
           append("/").
           append(imageId);

        return sb.toString();
    }

    /**
     * Retrieve the URL for the style sheet that is part
     * of a skin. The style is stored in the
     * WEBAPP/resources/ui/skins/<SKIN> directory with the
     * filename skin.css
     *
     * Use this if for some reason your server name,
     * server scheme, or server port change on a
     * per request basis. I'm not sure if this
     * would happend in a load balanced situation.
     * I think in most cases the style()
     * method would probably be enough, but I'm not
     * absolutely positive.
     */
    public String getStylecss(RunData data)
    {
        ContentURI cu = new ContentURI(data);
        StringBuffer sb = new StringBuffer();

        sb.append(resourcesDirectory).
           append(SKINS_DIRECTORY).
           append("/").
           append(getSkin()).
           append("/").
           append(SKIN_CSS_FILE);

        return cu.getURI(sb.toString());
    }

    /**
     * Retrieve the URL for the style sheet that is part
     * of a skin. The style is stored in the
     * WEBAPP/resources/ui/skins/<SKIN> directory with the
     * filename skin.css
     */
    public String getStylecss()
    {
        StringBuffer sb = new StringBuffer();

        sb.append(TurbineServlet.getServerScheme()).
           append("://").
           append(TurbineServlet.getServerName()).
           append(":").
           append(TurbineServlet.getServerPort()).
           append(TurbineServlet.getContextPath()).
           append("/").
           append(resourcesDirectory).
           append(SKINS_DIRECTORY).
           append("/").
           append(getSkin()).
           append("/").
           append(SKIN_CSS_FILE);

        return sb.toString();
    }

    /**
     * Load the specified skin. In development mode
     * this may occur frequently as the skin properties
     * are being changed.
     */
    private void loadSkin()
    {
        skinProperties = new Properties();

        try
        {
            FileInputStream is = new FileInputStream(
                skinsDirectory + "/" + getSkin() + "/" + SKIN_PROPS_FILE);

            skinProperties.load(is);
        }
        catch (Exception e)
        {
            Log.error("Cannot load skin: " + skinName);
        }
    }

    /**
     * Set the skin name to the skin from the TR.props
     * file. If the property is not present use the
     * default skin.
     */
    public void setSkin()
    {
        this.skinName = TurbineResources.getString(SKIN_PROPERTY,
                SKIN_PROPERTY_DEFAULT);
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
     * Set the skin name when the tool is configured to be
     * loaded on a per-request basis. By default it calls getSkin
     * to return the skin specified in TR.properties. Developers can
     * write a subclass of UIManager that overrides this method to
     * determine the skin to use based on information held in the request.
     *
     * @param data a RunData instance
     */
    protected void setSkin(RunData data)
    {
        setSkin();
    }

    /**
     * Set the skin name when the tool is configured to be
     * loaded on a per-session basis. It the user's temp hashmap contains
     * a value in the attribute specified by the String constant SKIN_ATTRIBUTE
     * then that is returned. Otherwise it calls getSkin to return the skin
     * specified in TR.properties.
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
     * Set the skin name user's temp hashmap for the current session.
     *
     * @param user a User instance
     * @param skin the skin name for the session
     */
    public static void setSkin(User user, String skin)
    {
        user.setTemp(SKIN_ATTRIBUTE, skin);
    }
}
