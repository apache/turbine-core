package org.apache.turbine.services.xmlrpc.util;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.mail.internet.MimeUtility;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;

import org.apache.turbine.services.servlet.TurbineServlet;

/**
 * A Handler for use with the XML-RPC service that will deal
 * with clients sending file to the server (Turbine application)
 * and clients getting files from the server (Turbine application).
 *
 * 1) In the first case where the client sends a file to the server,
 * the client has encoded the file contents and passes those
 * encoded file contents on to the server:
 *
 * Client --------> encoded file contents -------------> Server
 *
 * The server must then decode the file contents and write the
 * decoded file contents to disk.
 *
 * 2) In the second case where the client gets a file from the
 * the server, the server has encoded the file contents and
 * passes those encoded file contents on to the client:
 *
 * Client <-------  encoded file contents <------------- Server
 *
 * The client must then decode the file contents and write the
 * decoded file contents to disk.
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 * @deprecated This is not scope of the Service itself but of an
 *             application which uses the service. This class shouldn't
 *             be part of Turbine but of an addon application.
 */
public class FileHandler
{
    /** Logging */
    private static Log log = LogFactory.getLog(FileHandler.class);

    /**
     * Default Constructor
     */
    public FileHandler()
    {
    }

    /**
     * The client has indicated that it would like
     * to send a file to the server and have it
     * stored in a certain location on the server.
     *
     * So a client Turbine application might use the
     * following bit of code to send a file to a server
     * Turbine application:
     *
     * TurbineXmlRpc.executeRpc("file.send", params)
     *
     * Where:
     *
     * params.get(0) = contents of the file as a string.
     * params.get(1) = the name the file should have when it lands.
     * params.get(2) = property describing where the file should land.
     *
     * @param fileContents The contents of the file to store. It
     *    is assumed that any xml content is properly encoded!
     *
     * @param fileName Name to give the file created to store
     *    the contents.
     *
     * @param targetLocationProperty storage location of this file
     *    is controlled by this property that is specified in
     *    the TR.props file or an included properties file.
     */
    public boolean send(String fileContents,
                        String targetLocationProperty,
                        String fileName)
    {
        /*
         * Simply take the file contents that have been sent
         * by the client and write them to disk in the
         * specified location: targetLocationProperty specifies
         * the directory in which to place the fileContents
         * with the name fileName.
         */
        return writeFileContents(fileContents, targetLocationProperty,
                fileName);
    }

    /**
     * The client has indicated that it would like
     * to get a file from the server.
     *
     * So a client Turbine application might use the
     * following bit of code to get a file from a server
     * Turbine application:
     *
     * TurbineXmlRpc.executeRpc("file.get", params)
     *
     * Where:
     *
     * params.get(0) = the name the file should have when it lands.
     * params.get(1) = property describing where the file should land.
     *
     * @param fileName Name to give the file created to store
     *    the contents.
     *
     * @param targetLocationProperty storage location of this file
     *    is controlled by this property that is specified in
     *    the TR.props file or an included properties file.
     *
     * @return the file contents encoded with base64.
     */
    public String get(String targetLocationProperty,
                      String fileName)
    {
        /*
         * Place the contents of the file with the name
         * fileName in the directory specified by
         * targetLocationProperty.
         */
        return readFileContents(targetLocationProperty, fileName);
    }

    /**
     * Return the content of file encoded for transfer
     *
     * @param targetLocationProperty path to file to encode.
     * @param fileName file to encode
     * @return String encoded contents of the requested file.
     */
    public static String readFileContents(String targetLocationProperty,
                                          String fileName)
    {
        String location =
          Turbine.getConfiguration().getString(targetLocationProperty);

        if (StringUtils.isEmpty(location))
        {
          log.error("Could not load Property for location "
              + targetLocationProperty);
          return null;
        }

        File tmpF = new File(".");

        StringBuffer sb = new StringBuffer();
        sb.append(location);
        sb.append(File.separator);
        sb.append(fileName);

        String file = TurbineServlet.getRealPath(sb.toString());

        StringWriter sw = null;
        BufferedReader reader = null;
        try
        {
            /*
             * This little routine was borrowed from the
             * velocity ContentResource class.
             */

            sw = new StringWriter();

            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file)));

            char buf[] = new char[1024];
            int len = 0;

            while ((len = reader.read(buf, 0, 1024)) != -1)
            {
                sw.write(buf, 0, len);
            }

            return MimeUtility.encodeText(sw.toString(), "UTF-8", "B");
        }
        catch (IOException ioe)
        {
            log.error("[FileHandler] Unable to encode the contents " +
                    "of the request file.", ioe);

            return null;
        }
        finally
        {
            try
            {
                if (sw != null)
                {
                    sw.close();
                }
                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (Exception e)
            {
            }
        }
    }

    public static boolean writeFileContents(String fileContents,
                                            String targetLocationProperty,
                                            String fileName)
    {
        String location =
          Turbine.getConfiguration().getString(targetLocationProperty);

        if (StringUtils.isEmpty(location))
        {
          log.error("Could not load Property for location "
              + targetLocationProperty);
          return false;
        }

        /*
         * The target location is always within the webapp to
         * make the application fully portable. So use the TurbineServlet
         * service to map the target location in the webapp space.
         */

        File targetLocation = new File(
            TurbineServlet.getRealPath(location));

        if (!targetLocation.exists())
        {
            /*
             * If the target location doesn't exist then
             * attempt to create the target location and any
             * necessary parent directories as well.
             */
            if (!targetLocation.mkdirs())
            {
                log.error("[FileHandler] Could not create target location: " +
                        targetLocation + ". Cannot transfer file from client.");

                return false;
            }
            else
            {
                log.info("[FileHandler] Creating target location:" +
                        targetLocation +
                        " in order to complete file transfer from client.");
            }
        }

        FileWriter fileWriter = null;
        try
        {
            /*
             * Try to create the target file and write it out
             * to the target location.
             */
            fileWriter = new FileWriter(
                    targetLocation + "/" + fileName);

            /*
             * It is assumed that the file has been encoded
             * and therefore must be decoded before the
             * contents of the file are stored to disk.
             */
            fileWriter.write(MimeUtility.decodeText(fileContents));

            return true;
        }
        catch (IOException ioe)
        {
            log.error("[FileHandler] Could not write the decoded file " +
                    "contents to disk for the following reason.", ioe);

            return false;
        }
        finally
        {
            try
            {
                if (fileWriter != null)
                {
                    fileWriter.close();
                }
            }
            catch (Exception e)
            {
            }
        }
    }

    /**
     * Method to allow a client to remove a file from
     * the server
     *
     * @param sourceLocationProperty
     * @param sourceFileName
     */
    public static void remove(String sourceLocationProperty,
                              String sourceFileName)
    {
        String location =
          Turbine.getConfiguration().getString(sourceLocationProperty);

        if (StringUtils.isEmpty(location))
        {
          log.error("Could not load Property for location "
              + sourceLocationProperty);
          return;
        }

        /*
         * The target location is always within the webapp to
         * make the application fully portable. So use the TurbineServlet
         * service to map the target location in the webapp space.
         */
        File sourceFile =
            new File(TurbineServlet.getRealPath(sourceLocationProperty
                         + "/" + sourceFileName));

        if (sourceFile.exists())
        {
            sourceFile.delete();
        }
    }
}
