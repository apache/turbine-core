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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.StringWriter;

import javax.mail.internet.MimeUtility;

import org.apache.turbine.util.Log;

import org.apache.turbine.services.resources.TurbineResources;
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
 */
public class FileHandler
{
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
     * @param fileContents: The contents of the file to store. It
     *    is assumed that any xml content is properly encoded!
     *
     * @param fileName: Name to give the file created to store
     *    the contents.
     * 
     * @param targetLocationProperty: storage location of this file
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
        return writeFileContents(fileContents, targetLocationProperty, fileName);
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
     * @param fileName: Name to give the file created to store
     *    the contents.
     * 
     * @param targetLocationProperty: storage location of this file
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
     * @param file: path to file to encode.
     * @return String encoded contents of the requested file.
     */
    public static String readFileContents(String targetLocationProperty,
                                          String fileName)
    {
        String file = TurbineServlet.getRealPath(
            TurbineResources.getString(targetLocationProperty) + 
                "/" + fileName);
        
        try
        {
            /*
             * This little routine was borrowed from the
             * velocity ContentResource class.
             */
            
            StringWriter sw = new StringWriter();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(file)));
            
            char buf[] = new char[1024];
            int len = 0;
            
            while ((len = reader.read(buf, 0, 1024)) != -1)
            {
                sw.write( buf, 0, len );
            }
            
            return MimeUtility.encodeText(sw.toString(), "UTF-8", "B");
        }
        catch (IOException ioe)
        {
            Log.error("[FileHandler] Unable to encode the contents " +
                "of the request file.", ioe);
            
            return null;
        }
    }

    public static boolean writeFileContents(String fileContents,
                                            String targetLocationProperty,
                                            String fileName)
    {
        /*
         * The target location is always within the webapp to
         * make the application fully portable. So use the TurbineServlet
         * service to map the target location in the webapp space.
         */
        
        File targetLocation = new File(
            TurbineServlet.getRealPath(
                TurbineResources.getString(
                    targetLocationProperty)));

        if (targetLocation.exists() == false)
        {
            /*
             * If the target location doesn't exist then
             * attempt to create the target location and any
             * necessary parent directories as well.
             */
            
            if (targetLocation.mkdirs() == false)
            {
                Log.error("[FileHandler] Could not create target location: " + 
                    targetLocation + ". Cannot transfer file from client.");
                    
                return false;
            }
            else
            {
                Log.info("[FileHandler] Creating target location:" + targetLocation +
                 " in order to complete file transfer from client.");
            }
        }            
        
        try
        {
            /*
             * Try to create the target file and write it out
             * to the target location.
             */
            
            FileWriter fileWriter = new FileWriter(
                targetLocation + "/" + fileName);
            
            /*
             * It is assumed that the file has been encoded
             * and therefore must be decoded before the
             * contents of the file are stored to disk.
             */
            
            fileWriter.write(MimeUtility.decodeText(fileContents));
            fileWriter.close();
            
            return true;
        }
        catch (IOException ioe)
        {
            Log.error("[FileHandler] Could not write the decoded file " +
                "contents to disk for the following reason.", ioe);
                
            return false;
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
    public static void remove(String sourceLocationProperty,
                              String sourceFileName)
    {
        /*
         * The target location is always within the webapp to
         * make the application fully portable. So use the TurbineServlet
         * service to map the target location in the webapp space.
         */
        
        File sourceFile = new File(
            TurbineServlet.getRealPath(
                TurbineResources.getString(sourceLocationProperty) +
                    "/" + sourceFileName));

        if (sourceFile.exists())
        {
            sourceFile.delete();
        }
    }
}    
