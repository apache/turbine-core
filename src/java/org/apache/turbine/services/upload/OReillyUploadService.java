package org.apache.turbine.services.upload;

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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.ValueParser;

/**
 * <p> This is an implementation of the {@link UploadService} using
 * the O'Reilly multipart request parser from the book <i>Java Servlet
 * Programming</i> by Jason Hunter.
 *
 * <p> This class is intended for compatibity with old code.  Use
 * {@link TurbineUploadService} in new applications.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 * @deprecated use TurbineUploadService
 */
public class OReillyUploadService
    extends BaseUploadService
{
    /** Holds a vector of the filenames that were uploaded. */
    private Vector filenames = null;

    /**
     * Get a list of the files that were uploaded.
     *
     * @return A Vector with the filename(s) that were uploaded.
     */
    public Vector getFilenames()
    {
        return filenames;
    }

    /**
     * Initiate the multipartParse in the uploader.  This will upload
     * the files to the path given.
     *
     * @param req The servlet request to be parsed.
     * @param params The ParameterParser instance to insert form
     * fields into.
     * @param path The location where the files should be stored.
     * @exception IOException, if there are problems reading/parsing
     * the request or storing files.
     */
    public void parseRequest( HttpServletRequest req,
                              ParameterParser params,
                              String path )
        throws TurbineException
    {
        try
        {
            // Check if com.oreilly.servlet.MultipartRequest exists,
            // and if not, exit gracefully.  Do not want to force
            // Jason Hunter's package to be present for Dash
            // compilation.
            Class[] argSignature = { Class.forName("javax.servlet.ServletRequest"), path.getClass(),  Integer.TYPE };
            Class multiClass = Class.forName( "com.oreilly.servlet.MultipartRequest" );
            Constructor multiConstructor= multiClass.getConstructor( argSignature );
            // Pass in the request, a directory to save uploads to,
            // and the maximum POST size we should handle (set to
            // maximum since, we assume this is checked before calling
            // this method).
            Object[] args = { req, path, new Integer(Integer.MAX_VALUE) };
            Object multiInstance = (Object) multiConstructor.newInstance( args );
            argSignature = new Class[0];
            args = new Object[0];
            Enumeration parameters = (Enumeration)
                multiClass.getMethod("getParameterNames", argSignature)
                .invoke(multiInstance, args);
            Enumeration files = (Enumeration)
                multiClass.getMethod("getFileNames", argSignature)
                .invoke(multiInstance, args);

            argSignature = new Class[1];
            args = new Object[1];
            if ( parameters != null )
            {
                while(parameters.hasMoreElements())
                {
                    String tmp = (String) parameters.nextElement();
                    argSignature[0] = tmp.getClass();
                    args[0] = (Object)tmp;
                    // Method only returns last value, if parameter is
                    // multivalued.
                    params.add( tmp,
                                (String)multiClass
                                .getMethod("getParameter", argSignature)
                                .invoke(multiInstance, args) );
                }
            }

            filenames = new Vector();
            if ( files != null )
            {
                while(files.hasMoreElements())
                {
                    String tmp = (String) files.nextElement();
                    argSignature[0] = tmp.getClass();
                    args[0] = (Object)tmp;

                    String filename = (String)multiClass
                        .getMethod("getFilesystemName", argSignature)
                        .invoke(multiInstance, args);
                    if (filename != null)
                        filenames.addElement(filename);
                }
            }
        }
        catch(ClassNotFoundException cnfe)
        {
            throw new TurbineException ("UploadFile action was attempted and a handler has not been"+
                                   " registered in the TurbineResources.properties, so com.oreilly.servlet.Multipart was tried" +
                                   " and has not been installed.  Turbine uses a class from this package to" +
                                   " parse and save the file from the POST data.  You must supply a class" +
                                   " to use UploadFile.  com.oreilly.servlet is available as part of purchase of" +
                                   " Jason Hunter's Java Servlet Programming book.");
        }
        catch(Exception e)
        {
            throw new TurbineException("Failed to perform file upload using O'Reilly uploader", e);
        }
    }
}
