package org.apache.turbine.services.pull.util;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.Turbine;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.services.pull.TurbinePull;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.ServerData;
import org.apache.turbine.util.uri.DataURI;

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
 * <p>
 *
 * This is an application pull tool for the template system. You should <b>not</b>
 * use it in a normal application!
 *
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:james_coltman@majorband.co.uk">James Coltman</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="thomas.vandahl@tewisoft.de">Thomas Vandahl</a>
 * @version $Id$
 */
public class UIManager implements ApplicationTool
{
    /** Logging */
    private static Log log = LogFactory.getLog(UIManager.class);

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
     * Property tag for the default skin that is to be
     * used for the web application.
     */
    private static final String SKIN_PROPERTY = "tool.ui.skin";

    /**
     * Property tag for the image directory inside the skin that is to be
     * used for the web application.
     */
    private static final String IMAGEDIR_PROPERTY = "tool.ui.dir.image";

    /**
     * Property tag for the skin directory that is to be
     * used for the web application.
     */
    private static final String SKINDIR_PROPERTY = "tool.ui.dir.skin";

    /**
     * Property tag for the css file that is to be
     * used for the web application.
     */
    private static final String CSS_PROPERTY = "tool.ui.css";

    /**
     * Property tag for the css file that is to be
     * used for the web application.
     */
    private static final String RELATIVE_PROPERTY = "tool.ui.want.relative";

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
            UIManager.class.getName()+ ".skin";

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
    private static final String DEFAULT_SKIN_CSS_FILE = "skin.css";

    /**
     * This the resources directory relative to the
     * webapp context. Used for constructing correct
     * URIs for retrieving images in image().
     */
    private String resourcesDirectory;
    private String imagesDirectory;
    private String cssFile;

    private boolean wantRelative = false;

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
        Configuration cfg = Turbine.getConfiguration();

        resourcesDirectory = stripSlashes(TurbinePull.getResourcesDirectory());

        if (data == null)
        {
            log.debug("UI Manager scope is global");
            setSkin();
        }
        else if (data instanceof RunData)
        {
            log.debug("UI Manager scope is request");
            setSkin((RunData) data);
        }
        else if (data instanceof User)
        {
            log.debug("UI Manager scope is session");
            setSkin((User) data);
        }

        skinsDirectory = stripSlashes(cfg.getString(SKINDIR_PROPERTY, SKINS_DIRECTORY));

        imagesDirectory = stripSlashes(cfg.getString(IMAGEDIR_PROPERTY, IMAGES_DIRECTORY));

        cssFile = cfg.getString(CSS_PROPERTY, DEFAULT_SKIN_CSS_FILE);

        wantRelative = cfg.getBoolean(RELATIVE_PROPERTY, false);

        loadSkin();
    }

    private String stripSlashes(final String path)
    {
        if (StringUtils.isEmpty(path))
        {
            return "";
        }

        String ret = path;
        int len = ret.length() - 1;

        if (ret.charAt(len) == '/')
        {
            ret = ret.substring(0, len);
        }

        if (len > 0 && ret.charAt(0) == '/')
        {
            ret = ret.substring(1);
        }

        return ret;
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
        log.debug("Refreshing UI manager");

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
     * WEBAPP/resources/ui/skins/&lt;SKIN&gt;/images
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
        DataURI du = new DataURI(data);

        StringBuffer sb = new StringBuffer();

        sb.append(resourcesDirectory).
                append('/').
                append(skinsDirectory).
                append('/').
                append(getSkin()).
                append('/').
                append(imagesDirectory).
                append('/').
                append(stripSlashes(imageId));

        du.setScriptName(sb.toString());

        return wantRelative ? du.getRelativeLink() : du.getAbsoluteLink();
    }

    /**
     * Retrieve the URL for an image that is part
     * of a skin. The images are stored in the
     * WEBAPP/resources/ui/skins/&lt;SKIN&gt;/images
     * directory.
     */
    public String image(String imageId)
    {
        ServerData sd = Turbine.getDefaultServerData();
        DataURI du = new DataURI(sd);

        StringBuffer sb = new StringBuffer();

        sb.append(resourcesDirectory).
                append('/').
                append(skinsDirectory).
                append('/').
                append(getSkin()).
                append('/').
                append(imagesDirectory).
                append('/').
                append(stripSlashes(imageId));

        du.setScriptName(sb.toString());
        return wantRelative ? du.getRelativeLink() : du.getAbsoluteLink();
    }

    /**
     * Retrieve the URL for the style sheet that is part
     * of a skin. The style is stored in the
     * WEBAPP/resources/ui/skins/&lt;SKIN&gt; directory with the
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
        return getScript(cssFile, data);
    }

    /**
     * Retrieve the URL for the style sheet that is part
     * of a skin. The style is stored in the
     * WEBAPP/resources/ui/skins/&lt;SKIN&gt; directory with the
     * filename skin.css
     */
    public String getStylecss()
    {
        return getScript(cssFile);
    }

    /**
     * Retrieve the URL for a given script that is part
     * of a skin. The script is stored in the
     * WEBAPP/resources/ui/skins/<SKIN> directory
     */
    public String getScript(String filename, RunData data)
    {
        DataURI du = new DataURI(data);

        StringBuffer sb = new StringBuffer();

        sb.append(resourcesDirectory).
                append('/').
                append(skinsDirectory).
                append('/').
                append(getSkin()).
                append('/').
                append(stripSlashes(filename));

        du.setScriptName(sb.toString());
        return wantRelative ? du.getRelativeLink() : du.getAbsoluteLink();
    }

    /**
     * Retrieve the URL for a given script that is part
     * of a skin. The script is stored in the
     * WEBAPP/resources/ui/skins/<SKIN> directory
     */
    public String getScript(String filename)
    {
        ServerData sd = Turbine.getDefaultServerData();
        DataURI du = new DataURI(sd);

        StringBuffer sb = new StringBuffer();

        sb.append(resourcesDirectory).
                append('/').
                append(skinsDirectory).
                append('/').
                append(getSkin()).
                append('/').
                append(stripSlashes(filename));

        du.setScriptName(sb.toString());
        return wantRelative ? du.getRelativeLink() : du.getAbsoluteLink();
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
            StringBuffer sb = new StringBuffer();

            sb.append(resourcesDirectory).
                    append('/').
                    append(skinsDirectory).
                    append('/').
                    append(getSkin()).
                    append('/').
                    append(SKIN_PROPS_FILE);

            InputStream is = TurbineServlet.getResourceAsStream(sb.toString());

            skinProperties.load(is);
        }
        catch (Exception e)
        {
            log.error("Cannot load skin: " + skinName);
        }
    }

    /**
     * Set the skin name to the skin from the TR.props
     * file. If the property is not present use the
     * default skin.
     */
    public void setSkin()
    {
        this.skinName = Turbine.getConfiguration()
                .getString(SKIN_PROPERTY,
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
