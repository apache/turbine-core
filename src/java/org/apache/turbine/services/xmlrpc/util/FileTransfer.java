package org.apache.turbine.services.xmlrpc.util;

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

import java.net.URL;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.services.xmlrpc.TurbineXmlRpc;
import org.apache.turbine.util.TurbineException;

/**
 * Test class for FileHandler.
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
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
