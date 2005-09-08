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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.pull.TurbinePull;
import org.apache.turbine.util.ServerData;
import org.apache.turbine.util.uri.DataURI;

/**
 * The UI service provides for shared access to User Interface (skin) files,
 * as well as the ability for non-default skin files to inherit properties from 
 * a default skin.  Use TurbineUI to access skin properties from your screen 
 * classes and action code. UITool is provided as a pull tool for accessing 
 * skin properties from your templates. 
 *
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id$
 * @see UIService
 * @see UITool
 */
public class TurbineUIService
        extends TurbineBaseService
        implements UIService
{
    /**
     * A FilenameFilter that returns only directories.
     * todo Replace with commons-io DirectoryFileFilter
     */
    private class DirectoryFileFilter implements FilenameFilter
    {
        public boolean accept(File dir, String fileName)
        {
            File file = new File(dir, fileName);
            if (file.isDirectory())
            {
                return true;
            }
            return false;
        }
    }

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
     * Property tag for the skin that is to be used for the web application.
     */
    private static final String SKIN_PROPERTY_KEY = "tool.ui.skin";

    /**
     * Default skin name. This name refers to a directory in the 
     * WEBAPP/resources/ui/skins directory. There is a file called skin.props
     * which contains the name/value pairs to be made available via the skin.
     */
    public static final String SKIN_PROPERTY_DEFAULT = "default";

    /**
     * The skins directory.
     */
    private static final String skinsDirectory 
            = TurbinePull.getAbsolutePathToResourcesDirectory() 
                    + SKINS_DIRECTORY;

    /**
     * The file within the skin directory that actually contains the name/value 
     * pairs for the skin.
     */
    private static final String SKIN_PROPS_FILE = "skin.props";

    /**
     * The file name for the skin style sheet.
     */
    private static final String SKIN_CSS_FILE = "skin.css";

    /**
     * The directory where application tool resources are stored.
     */
    private static String resourcesDirectory;

    /**
     * The skin Properties store.
     */
    private HashMap skins = new HashMap();

    /**
     * Refresh the service by clearing all skins.
     */
    public void refresh()
    {
        clearSkins();
    }

    /**
     * Refresh a particular skin by clearing it.
     * 
     * @param skinName the name of the skin to clear.
     */
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
        Properties skinProperties = (Properties) skins.get(skinName);
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
    public String get(String key)
    {
        return get(getWebappSkinName(), key);
    }

    /**
     * Provide access to the list of available skin names.
     * 
     * @return the available skin names.
     */
    public String[] getSkinNames()
    {
        File skinsDir = new File(skinsDirectory);
        return skinsDir.list(new DirectoryFileFilter());
    }

    /**
     * Clear the map of stored skins. 
     */
    private void clearSkins()
    {
        synchronized (skins)
        {
            skins = new HashMap();
        }
        log.debug("All skins were cleared.");
    }
    
    /**
     * Clear a particular skin from the map of stored skins.
     * 
     * @param skinName the name of the skin to clear.
     */
    private void clearSkin(String skinName)
    {
        synchronized (skins)
        {
            if (!skinName.equals(SKIN_PROPERTY_DEFAULT))
            {
                skins.remove(SKIN_PROPERTY_DEFAULT);
            }
            skins.remove(skinName);
        }
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
    private synchronized Properties loadSkin(String skinName)
    {
        Properties defaultSkinProperties = null;
        
        if (!StringUtils.equals(skinName, SKIN_PROPERTY_DEFAULT))
        {
            defaultSkinProperties = getSkinProperties(SKIN_PROPERTY_DEFAULT);
        }

        // The following line is okay even for default.
        Properties skinProperties = new Properties(defaultSkinProperties);
        
        try
        {
            log.debug("Loading selected skin from: " + skinsDirectory + "/" 
                    + skinName + "/" + SKIN_PROPS_FILE);
            FileInputStream is = new FileInputStream(
                    skinsDirectory + "/" + skinName + "/" + SKIN_PROPS_FILE);

            skinProperties.load(is);
        }
        catch (Exception e)
        {
            log.error("Cannot load skin: " + skinName, e);
            if (!StringUtils.equals(skinName, getWebappSkinName()) 
                    && !StringUtils.equals(skinName, SKIN_PROPERTY_DEFAULT))
            {
                log.error("Attempting to return the skin configured for " 
                        + "webapp instead of " + skinName);
                return getSkinProperties(getWebappSkinName());
            }
            else if (!StringUtils.equals(skinName, SKIN_PROPERTY_DEFAULT))
            {
                log.error("Return the default skin instead of " 
                        + skinName);
                return skinProperties; // Already contains the default skin.
            }
            else
            {
                log.error("No skins available - returning an empty Properties");
                return new Properties();
            }
        }
        
        // Replace in skins HashMap
        synchronized (skins)
        {
            skins.put(skinName, skinProperties);
        }
        
        return skinProperties;
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
        return Turbine.getConfiguration()
                .getString(SKIN_PROPERTY_KEY, SKIN_PROPERTY_DEFAULT);
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
     * @param serverData the serverData to use as the basis for the URL.
     */
    public String image(String skinName, String imageId, ServerData serverData)
    {
        DataURI du = new DataURI(serverData);

        StringBuffer sb = new StringBuffer();

        sb.append(resourcesDirectory).
                append(SKINS_DIRECTORY).
                append("/").
                append(skinName).
                append(IMAGES_DIRECTORY).
                append("/").
                append(imageId);

        du.setScriptName(sb.toString());
        return du.getAbsoluteLink();
    }

    /**
     * Retrieve the URL for an image that is part of a skin. The images are 
     * stored in the WEBAPP/resources/ui/skins/[SKIN]/images directory.
     * 
     * @param skinName the name of the skin to retrieve the image from.
     * @param imageId the id of the image whose URL will be generated.
     */
    public String image(String skinName, String imageId)
    {
        ServerData sd = Turbine.getDefaultServerData();
        DataURI du = new DataURI(sd);

        StringBuffer sb = new StringBuffer();

        sb.append(resourcesDirectory).
           append(SKINS_DIRECTORY).
           append("/").
           append(skinName).
           append(IMAGES_DIRECTORY).
           append("/").
           append(imageId);

        du.setScriptName(sb.toString());
        return du.getAbsoluteLink();
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
     * @param serverData the serverData to use as the basis for the URL.
     */
    public String getStylecss(String skinName, ServerData serverData)
    {
        DataURI du = new DataURI(serverData);
        StringBuffer sb = new StringBuffer();

        sb.append(resourcesDirectory).
                append(SKINS_DIRECTORY).
                append("/").
                append(skinName).
                append("/").
                append(SKIN_CSS_FILE);

        du.setScriptName(sb.toString());
        return du.getAbsoluteLink();
    }

    /**
     * Retrieve the URL for the style sheet that is part of a skin. The style is 
     * stored in the WEBAPP/resources/ui/skins/[SKIN] directory with the 
     * filename skin.css
     * 
     * @param skinName the name of the skin to retrieve the style sheet from.
     */
    public String getStylecss(String skinName)
    {
        ServerData sd = Turbine.getDefaultServerData();
        DataURI du = new DataURI(sd);

        StringBuffer sb = new StringBuffer();

        sb.append(resourcesDirectory).
           append(SKINS_DIRECTORY).
           append("/").
           append(skinName).
           append("/").
           append(SKIN_CSS_FILE);

        du.setScriptName(sb.toString());
        return du.getAbsoluteLink();
    }

    // ---- Service initilization ------------------------------------------

    /**
     * Initializes the service.
     */
    public void init() throws InitializationException
    {
        // Ensure PullService has been initialised.
        getServiceBroker().initService("PullService");

        // Get the resources directory that is specificed in the TR.props or 
        // default to "resources", relative to the webapp.
        resourcesDirectory = TurbinePull.getResourcesDirectory();

        setInit(true);
    }

    /**
     * Returns to uninitialized state.
     */
    public void shutdown()
    {
        clearSkins();

        setInit(false);
    }

}
