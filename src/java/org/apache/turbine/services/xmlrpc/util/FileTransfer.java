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

import java.net.URL;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.services.xmlrpc.TurbineXmlRpc;
import org.apache.turbine.util.TurbineException;

/**
 * Test class for FileHandler.
 *
 * @deprecated This is not scope of the Service itself but of an
 *             application which uses the service. This class shouldn't
 *             be part of Turbine but of an addon application.
 * @version $Id$
 */
public class FileTransfer
{
    /** Logging */
    private static Log log = LogFactory.getLog(FileTransfer.class);

    /**
     * Method to allow a client to send a file to a server.
     *
     * @param serverURL
     * @param sourceLocationProperty
     * @param sourceFileName
     * @param destinationLocationProperty
     * @param destinationFileName
     */
    public static void send(String serverURL,
                            String sourceLocationProperty,
                            String sourceFileName,
                            String destinationLocationProperty,
                            String destinationFileName)
            throws TurbineException
    {
        try
        {
            Vector params = new Vector();

            /*
             * fileContents
             */
            params.add(FileHandler.readFileContents(
                    sourceLocationProperty, sourceFileName));

            /*
             * property in TR.props which refers to the directory
             * where the fileContents should land.
             */
            params.add(destinationLocationProperty);

            /*
             * name to give the file contents.
             */
            params.add(destinationFileName);

            Boolean b = (Boolean) TurbineXmlRpc.executeRpc(
                    new URL(serverURL), "file.send", params);

        }
        catch (Exception e)
        {
            log.error("Error sending file to server:", e);
            throw new TurbineException(e);
        }
    }

    /**
     * Method to allow a client to send a file to a server
     * that requires a user name and password.
     *
     * @param serverURL
     * @param username
     * @param password
     * @param sourceLocationProperty
     * @param sourceFileName
     * @param destinationLocationProperty
     * @param destinationFileName
     * @throws TurbineException
     */
    public static void send(String serverURL,
                            String username,
                            String password,
                            String sourceLocationProperty,
                            String sourceFileName,
                            String destinationLocationProperty,
                            String destinationFileName)
            throws TurbineException
    {
        try
        {
            Vector params = new Vector();

            /*
             * fileContents
             */
            params.add(FileHandler.readFileContents(
                    sourceLocationProperty, sourceFileName));

            /*
             * property in TR.props which refers to the directory
             * where the fileContents should land.
             */
            params.add(destinationLocationProperty);

            /*
             * name to give the file contents.
             */
            params.add(destinationFileName);

            Boolean b = (Boolean) TurbineXmlRpc.executeAuthenticatedRpc(
                    new URL(serverURL),
                    username,
                    password,
                    "file.send",
                    params);

        }
        catch (Exception e)
        {
            log.error("Error sending file to server:", e);
            throw new TurbineException(e);
        }
    }

    /**
     * Method to allow a client to get a file to a server.
     *
     * @param serverURL
     * @param sourceLocationProperty
     * @param sourceFileName
     * @param destinationLocationProperty
     * @param destinationFileName
     * @throws TurbineException
     */
    public static void get(String serverURL,
                           String sourceLocationProperty,
                           String sourceFileName,
                           String destinationLocationProperty,
                           String destinationFileName)
            throws TurbineException
    {

        try
        {
            Vector params = new Vector();

            /*
             * property in TR.props which refers to the directory
             * where the fileContents should land.
             */
            params.add(sourceLocationProperty);

            /*
             * name to give the file contents.
             */
            params.add(sourceFileName);

            String fileContents = (String) TurbineXmlRpc.executeRpc(
                    new URL(serverURL), "file.get", params);

            /*
             * Now we have the file contents, we can write
             * them out to disk.
             */
            FileHandler.writeFileContents(fileContents,
                    destinationLocationProperty, destinationFileName);
        }
        catch (Exception e)
        {
            log.error("Error getting file from server:", e);
            throw new TurbineException(e);
        }
    }

    /**
     * Method to allow a client to get a file from a server
     * that requires a user name and password.
     *
     * @param serverURL
     * @param username
     * @param password
     * @param sourceLocationProperty
     * @param sourceFileName
     * @param destinationLocationProperty
     * @param destinationFileName
     */
    public static void get(String serverURL,
                           String username,
                           String password,
                           String sourceLocationProperty,
                           String sourceFileName,
                           String destinationLocationProperty,
                           String destinationFileName)
            throws TurbineException
    {

        try
        {
            Vector params = new Vector();

            /*
             * property in TR.props which refers to the directory
             * where the fileContents should land.
             */
            params.add(sourceLocationProperty);

            /*
             * name to give the file contents.
             */
            params.add(sourceFileName);

            String fileContents = (String) TurbineXmlRpc.executeAuthenticatedRpc(
                    new URL(serverURL),
                    username,
                    password,
                    "file.get",
                    params);

            /*
             * Now we have the file contents, we can write
             * them out to disk.
             */
            FileHandler.writeFileContents(fileContents,
                    destinationLocationProperty, destinationFileName);
        }
        catch (Exception e)
        {
            log.error("Error getting file from server:", e);
            throw new TurbineException(e);
        }
    }

    /**
     * Method to allow a client to remove a file from
     * the server
     *
     * @param serverURL
     * @param sourceLocationProperty
     * @param sourceFileName
     */
    public static void remove(String serverURL,
                              String sourceLocationProperty,
                              String sourceFileName)
            throws TurbineException
    {
        try
        {
            Vector params = new Vector();

            /*
             * property in TR.props which refers to the directory
             * where the fileContents should land.
             */
            params.add(sourceLocationProperty);

            /*
             * name to give the file contents.
             */
            params.add(sourceFileName);

            TurbineXmlRpc.executeRpc(new URL(serverURL), "file.remove", params);
        }
        catch (Exception e)
        {
            log.error("Error removing file from server:", e);
            throw new TurbineException(e);
        }
    }

    /**
     * Method to allow a client to remove a file from
     * a server that requires a user name and password.
     *
     * @param serverURL
     * @param username
     * @param password
     * @param sourceLocationProperty
     * @param sourceFileName
     */
    public static void remove(String serverURL,
                              String username,
                              String password,
                              String sourceLocationProperty,
                              String sourceFileName)
            throws TurbineException
    {
        try
        {
            Vector params = new Vector();

            /*
             * property in TR.props which refers to the directory
             * where the fileContents should land.
             */
            params.add(sourceLocationProperty);

            /*
             * name to give the file contents.
             */
            params.add(sourceFileName);

            TurbineXmlRpc.executeAuthenticatedRpc(new URL(serverURL),
                    username,
                    password,
                    "file.remove",
                    params);
        }
        catch (Exception e)
        {
            log.error("Error removing file from server:", e);
            throw new TurbineException(e);
        }
    }
}
