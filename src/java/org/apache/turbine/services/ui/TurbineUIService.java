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

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.pull.PullService;
import org.apache.turbine.services.pull.tools.UITool;
import org.apache.turbine.services.servlet.ServletService;
import org.apache.turbine.util.ServerData;
import org.apache.turbine.util.uri.DataURI;

/**
 * The UI service provides for shared access to User Interface (skin) files,
 * as well as the ability for non-default skin files to inherit properties from
 * a default skin.  Use TurbineUI to access skin properties from your screen
 * classes and action code. UITool is provided as a pull tool for accessing
 * skin properties from your templates.
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:james_coltman@majorband.co.uk">James Coltman</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @author <a href="thomas.vandahl@tewisoft.de">Thomas Vandahl</a>
 * @version $Id$
 * @see UIService
 * @see UITool
 */
public class TurbineUIService
        extends TurbineBaseService
        implements UIService
{
    /** Logging. */
    private static Log log = LogFactory.getLog(TurbineUIService.class);

    /**
     * The location of the skins within the application resources directory.
     */
    private static final String SKINS_DIRECTORY = "/ui/skins";

    /**
     * The name of the directory where images are stored for this skin.
     */
    private static final String IMAGES_DIRECTORY = "/images";

    /**
     * Property tag for the default skin that is to be used for the web
     * application.
     */
    private static final String SKIN_PROPERTY = "tool.ui.skin";

    /**
     * Property tag for the image directory inside the skin that is to be used
     * for the web application.
     */
    private static final String IMAGEDIR_PROPERTY = "tool.ui.dir.image";

    /**
     * Property tag for the skin directory that is to be used for the web
     * application.
     */
    private static final String SKINDIR_PROPERTY = "tool.ui.dir.skin";

    /**
     * Property tag for the css file that is to be used for the web application.
     */
    private static final String CSS_PROPERTY = "tool.ui.css";

    /**
     * Property tag for indicating if relative links are wanted for the web
     * application.
     */
    private static final String RELATIVE_PROPERTY = "tool.ui.want.relative";

    /**
     * Default skin name. This name refers to a directory in the
     * WEBAPP/resources/ui/skins directory. There is a file called skin.props
     * which contains the name/value pairs to be made available via the skin.
     */
    public static final String SKIN_PROPERTY_DEFAULT = "default";

    /**
     * The skins directory, qualified by the resources directory (which is
     * relative to the webapp context). This is used for constructing URIs and
     * for retrieving skin files.
     */
    private String skinsDirectory;

    /**
     * The file within the skin directory that contains the name/value pairs for
     * the skin.
     */
    private static final String SKIN_PROPS_FILE = "skin.props";

    /**
     * The file name for the skin style sheet.
     */
    private static final String DEFAULT_SKIN_CSS_FILE = "skin.css";

    /**
     * The servlet service.
     */
    private ServletService servletService;

    /**
     * The directory within the skin directory that contains the skin images.
     */
    private String imagesDirectory;

    /**
     * The name of the css file within the skin directory.
     */
    private String cssFile;

    /**
     * The flag that determines if the links that are returned are are absolute
     * or relative.
     */
    private boolean wantRelative = false;

    /**
     * The skin Properties store.
     */
    private ConcurrentHashMap<String, Properties> skins = new ConcurrentHashMap<String, Properties>();

    /**
     * Refresh the service by clearing all skins.
     */
    @Override
    public void refresh()
    {
        clearSkins();
    }

    /**
     * Refresh a particular skin by clearing it.
     *
     * @param skinName the name of the skin to clear.
     */
    @Override
    public void refresh(String skinName)
    {
        clearSkin(skinName);
    }

    /**
     * Retrieve the Properties for a specific skin.  If they are not yet loaded
     * they will be.  If the specified skin does not exist properties for the
     * default skin configured for the webapp will be returned and an error
     * level message will be written to the log.  If the webapp skin does not
     * exist the default skin will be used and id that doesn't exist an empty
     * Properties will be returned.
     *
     * @param skinName the name of the skin whose properties are to be
     * retrieved.
     * @return the Properties for the named skin or the properties for the
     * default skin configured for the webapp if the named skin does not exist.
     */
    private Properties getSkinProperties(String skinName)
    {
        Properties skinProperties = skins.get(skinName);
        return null != skinProperties ? skinProperties : loadSkin(skinName);
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
    @Override
    public String get(String skinName, String key)
    {
        Properties skinProperties = getSkinProperties(skinName);
        return skinProperties.getProperty(key);
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
    @Override
    public String get(String key)
    {
        return get(getWebappSkinName(), key);
    }

    /**
     * Provide access to the list of available skin names.
     *
     * @return the available skin names.
     */
    @Override
    public String[] getSkinNames()
    {
        File skinsDir = new File(servletService.getRealPath(skinsDirectory));
        return skinsDir.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                File directory = new File(dir, name);
                return directory.isDirectory();
            }
        });
    }

    /**
     * Clear the map of stored skins.
     */
    private void clearSkins()
    {
        skins.clear();
        log.debug("All skins were cleared.");
    }

    /**
     * Clear a particular skin from the map of stored skins.
     *
     * @param skinName the name of the skin to clear.
     */
    private void clearSkin(String skinName)
    {
        if (!skinName.equals(SKIN_PROPERTY_DEFAULT))
        {
            skins.remove(SKIN_PROPERTY_DEFAULT);
        }
        skins.remove(skinName);
        log.debug("The skin \"" + skinName
                + "\" was cleared (will also clear \"default\" skin).");
    }

    /**
     * Load the specified skin.
     *
     * @param skinName the name of the skin to load.
     * @return the Properties for the named skin if it exists, or the skin
     * configured for the web application if it does not exist, or the default
     * skin if that does not exist, or an empty Parameters object if even that
     * cannot be found.
     */
    private Properties loadSkin(String skinName)
    {
        Properties defaultSkinProperties = null;

        if (!StringUtils.equals(skinName, SKIN_PROPERTY_DEFAULT))
        {
            defaultSkinProperties = getSkinProperties(SKIN_PROPERTY_DEFAULT);
        }

        // The following line is okay even for default.
        Properties skinProperties = new Properties(defaultSkinProperties);

        StringBuilder sb = new StringBuilder();
        sb.append('/').append(skinsDirectory);
        sb.append('/').append(skinName);
        sb.append('/').append(SKIN_PROPS_FILE);
        if (log.isDebugEnabled())
        {
            log.debug("Loading selected skin from: " + sb.toString());
        }

        try (InputStream is = servletService.getResourceAsStream(sb.toString()))
        {
            // This will NPE if the directory associated with the skin does not
            // exist, but it is handled correctly below.
            skinProperties.load(is);
        }
        catch (Exception e)
        {
            log.error("Cannot load skin: " + skinName + ", from: "
                    + sb.toString(), e);
            if (!StringUtils.equals(skinName, getWebappSkinName())
                    && !StringUtils.equals(skinName, SKIN_PROPERTY_DEFAULT))
            {
                log.error("Attempting to return the skin configured for "
                        + "webapp instead of " + skinName);
                return getSkinProperties(getWebappSkinName());
            }
            else if (!StringUtils.equals(skinName, SKIN_PROPERTY_DEFAULT))
            {
                log.error("Return the default skin instead of " + skinName);
                return skinProperties; // Already contains the default skin.
            }
            else
            {
                log.error("No skins available - returning an empty Properties");
                return new Properties();
            }
        }

        // Replace in skins HashMap
        skins.put(skinName, skinProperties);

        return skinProperties;
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
    @Override
    public String getWebappSkinName()
    {
        return Turbine.getConfiguration()
                .getString(SKIN_PROPERTY, SKIN_PROPERTY_DEFAULT);
    }

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
     */
    @Override
    public String image(String skinName, String imageId, ServerData serverData)
    {
        return getSkinResource(serverData, skinName, imagesDirectory, imageId);
    }

    /**
     * Retrieve the URL for an image that is part of a skin. The images are
     * stored in the WEBAPP/resources/ui/skins/[SKIN]/images directory.
     *
     * @param skinName the name of the skin to retrieve the image from.
     * @param imageId the id of the image whose URL will be generated.
     */
    @Override
    public String image(String skinName, String imageId)
    {
        return image(skinName, imageId, Turbine.getDefaultServerData());
    }

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
     */
    @Override
    public String getStylecss(String skinName, ServerData serverData)
    {
        return getSkinResource(serverData, skinName, null, cssFile);
    }

    /**
     * Retrieve the URL for the style sheet that is part of a skin. The style is
     * stored in the WEBAPP/resources/ui/skins/[SKIN] directory with the
     * filename skin.css
     *
     * @param skinName the name of the skin to retrieve the style sheet from.
     */
    @Override
    public String getStylecss(String skinName)
    {
        return getStylecss(skinName, Turbine.getDefaultServerData());
    }

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
     */
    @Override
    public String getScript(String skinName, String filename,
            ServerData serverData)
    {
        return getSkinResource(serverData, skinName, null, filename);
    }

    /**
     * Retrieve the URL for a given script that is part of a skin. The script is
     * stored in the WEBAPP/resources/ui/skins/[SKIN] directory.
     *
     * @param skinName the name of the skin to retrieve the image from.
     * @param filename the name of the script file.
     */
    @Override
    public String getScript(String skinName, String filename)
    {
        return getScript(skinName, filename, Turbine.getDefaultServerData());
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
     * Construct the URL to the skin resource.
     *
     * @param serverData the serverData to use as the basis for the URL.
     * @param skinName the name of the skin.
     * @param subDir the sub-directory in which the resource resides or
     * <code>null</code> if it is in the root directory of the skin.
     * @param resourceName the name of the resource to be retrieved.
     * @return the path to the resource.
     */
    private String getSkinResource(ServerData serverData, String skinName,
            String subDir, String resourceName)
    {
        StringBuilder sb = new StringBuilder(skinsDirectory);
        sb.append("/").append(skinName);
        if (subDir != null)
        {
            sb.append("/").append(subDir);
        }
        sb.append("/").append(stripSlashes(resourceName));

        DataURI du = new DataURI(serverData);
        du.setScriptName(sb.toString());
        return wantRelative ? du.getRelativeLink() : du.getAbsoluteLink();
    }

    // ---- Service initilization ------------------------------------------

    /**
     * Initializes the service.
     */
    @Override
    public void init() throws InitializationException
    {
        Configuration cfg = Turbine.getConfiguration();

        servletService = (ServletService)TurbineServices.getInstance().getService(ServletService.SERVICE_NAME);
        PullService pullService = (PullService)TurbineServices.getInstance().getService(PullService.SERVICE_NAME);
        // Get the resources directory that is specified in the TR.props or
        // default to "resources", relative to the webapp.
        StringBuilder sb = new StringBuilder();
        sb.append(stripSlashes(pullService.getResourcesDirectory()));
        sb.append("/");
        sb.append(stripSlashes(
                cfg.getString(SKINDIR_PROPERTY, SKINS_DIRECTORY)));
        skinsDirectory = sb.toString();

        imagesDirectory = stripSlashes(
                cfg.getString(IMAGEDIR_PROPERTY, IMAGES_DIRECTORY));
        cssFile = cfg.getString(CSS_PROPERTY, DEFAULT_SKIN_CSS_FILE);
        wantRelative = cfg.getBoolean(RELATIVE_PROPERTY, false);

        setInit(true);
    }

    /**
     * Returns to uninitialized state.
     */
    @Override
    public void shutdown()
    {
        clearSkins();
        setInit(false);
    }
}
