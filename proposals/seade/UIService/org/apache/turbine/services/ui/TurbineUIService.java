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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Properties;

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

    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog(TurbineUIService.class);

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
    public static final String SKIN_PROPERTY_DEFAULT = "default";

    /**
     * The skins directory.
     */
    private static final String skinsDirectory 
            = TurbinePull.getAbsolutePathToResourcesDirectory() 
                    + SKINS_DIRECTORY;

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
     * 
     * @param skinName the name of the skin whose Properties is to be retrieved.
     * @return the properties for the named skin, the webapp skin, the default
     * skin or an empty Properties, depending on which skins exist.
     */
    private synchronized Properties loadSkin(String skinName)
    {
        Properties defaultSkinProperties = null;
        
        if (!skinName.equals(SKIN_PROPERTY_DEFAULT))
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
            if (!skinName.equals(getWebappSkinName()) 
                    && !skinName.equals(SKIN_PROPERTY_DEFAULT))
            {
                log.error("Attempting to return the skin configured for " 
                        + "webapp instead of " + skinName);
                return getSkinProperties(getWebappSkinName());
            }
            else if (!skinName.equals(SKIN_PROPERTY_DEFAULT))
            {
                log.error("Attempting to return the default skin instead of " 
                        + skinName);
                return getSkinProperties(SKIN_PROPERTY_DEFAULT);
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
                .getString(SKIN_PROPERTY, SKIN_PROPERTY_DEFAULT);
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
