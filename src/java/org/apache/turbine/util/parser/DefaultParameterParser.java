package org.apache.turbine.util.parser;

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

import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.services.upload.TurbineUpload;
import org.apache.turbine.services.upload.UploadService;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.pool.Recyclable;

/**
 * DefaultParameterParser is a utility object to handle parsing and
 * retrieving the data passed via the GET/POST/PATH_INFO arguments.
 *
 * <p>NOTE: The name= portion of a name=value pair may be converted
 * to lowercase or uppercase when the object is initialized and when
 * new data is added.  This behaviour is determined by the url.case.folding
 * property in TurbineResources.properties.  Adding a name/value pair may
 * overwrite existing name=value pairs if the names match:
 *
 * <pre>
 * ParameterParser pp = data.getParameters();
 * pp.add("ERROR",1);
 * pp.add("eRrOr",2);
 * int result = pp.getInt("ERROR");
 * </pre>
 *
 * In the above example, result is 2.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class DefaultParameterParser
    extends BaseValueParser
    implements ParameterParser,
               Recyclable
{
    /** Logging */
    private static Log log = LogFactory.getLog(DefaultParameterParser.class);

    /**
     * The servlet request to parse.
     */
    private HttpServletRequest request = null;

    /**
     * The raw data of a file upload.
     */
    private byte[] uploadData = null;

    /** Turbine Upload Service reference */
    private static UploadService uploadService = null;

    /** Do we have an upload Service? */
    private static boolean uploadServiceIsAvailable = false;

    /**
     * Create a new empty instance of ParameterParser.  Uses the
     * default character encoding (US-ASCII).
     *
     * <p>To add name/value pairs to this set of parameters, use the
     * <code>add()</code> methods.
     *
     */
    public DefaultParameterParser()
    {
        super();
        configureUploadService();
    }

    /**
     * Create a new empty instance of ParameterParser. Takes a
     * character encoding name to use when converting strings to
     * bytes.
     *
     * <p>To add name/value pairs to this set of parameters, use the
     * <code>add()</code> methods.
     *
     * @param characterEncoding The character encoding of strings.
     */
    public DefaultParameterParser(String characterEncoding)
    {
        super (characterEncoding);
        configureUploadService();
    }

    /**
     * Checks for availability of the Upload Service. We do this
     * check only once at Startup, because the getService() call
     * is really expensive and we don't have to run it every time
     * we process a request.
     */
    private void configureUploadService()
    {
        uploadServiceIsAvailable = TurbineUpload.isAvailable();
        if (uploadServiceIsAvailable)
        {
            uploadService = TurbineUpload.getService();
        }
    }

    /**
     * Disposes the parser.
     */
    public void dispose()
    {
        this.request = null;
        this.uploadData = null;
        super.dispose();
    }

    /**
     * Gets the parsed servlet request.
     *
     * @return the parsed servlet request or null.
     */
    public HttpServletRequest getRequest()
    {
        return this.request;
    }

    /**
     * Sets the servlet request to be parser.  This requires a
     * valid HttpServletRequest object.  It will attempt to parse out
     * the GET/POST/PATH_INFO data and store the data into a Hashtable.
     * There are convenience methods for retrieving the data as a
     * number of different datatypes.  The PATH_INFO data must be a
     * URLEncoded() string.
     *
     * <p>To add name/value pairs to this set of parameters, use the
     * <code>add()</code> methods.
     *
     * @param req An HttpServletRequest.
     */
    public void setRequest(HttpServletRequest req)
    {
        clear();

        uploadData = null;

        String enc = req.getCharacterEncoding();
        setCharacterEncoding(enc != null ? enc : "US-ASCII");

        // String object re-use at its best.
        String tmp = null;

        tmp = req.getHeader("Content-type");

        if (uploadServiceIsAvailable
            && uploadService.getAutomatic()
            && tmp != null
            && tmp.startsWith("multipart/form-data"))
        {
            try
            {
                TurbineUpload.parseRequest(req, this);
            }
            catch (TurbineException e)
            {
                log.error(new TurbineException("File upload failed", e));
            }
        }

        Enumeration names = req.getParameterNames();
        if (names != null)
        {
            while (names.hasMoreElements())
            {
                tmp = (String) names.nextElement();
                parameters.put(convert(tmp),
                        (Object) req.getParameterValues(tmp));
            }
        }

        // Also cache any pathinfo variables that are passed around as
        // if they are query string data.
        try
        {
            StringTokenizer st = new StringTokenizer(req.getPathInfo(), "/");
            boolean isNameTok = true;
            String pathPart = null;
            while (st.hasMoreTokens())
            {
                if (isNameTok)
                {
                    tmp = URLDecoder.decode(st.nextToken());
                    isNameTok = false;
                }
                else
                {
                    pathPart = URLDecoder.decode(st.nextToken());
                    if (tmp.length() > 0)
                    {
                        add(convert(tmp), pathPart);
                    }
                    isNameTok = true;
                }
            }
        }
        catch (Exception e)
        {
            // If anything goes wrong above, don't worry about it.
            // Chances are that the path info was wrong anyways and
            // things that depend on it being right will fail later
            // and should be caught later.
        }

        this.request = req;
    }

    /**
     * Sets the uploadData byte[]
     *
     * @param uploadData A byte[] with data.
     */
    public void setUploadData(byte[] uploadData)
    {
        this.uploadData = uploadData;
    }

    /**
     * Gets the uploadData byte[]
     *
     * @return uploadData A byte[] with data.
     */
    public byte[] getUploadData()
    {
        return this.uploadData;
    }

    /**
     * Add a FileItem object as a parameters.  If there are any
     * FileItems already associated with the name, append to the
     * array.  The reason for this is that RFC 1867 allows multiple
     * files to be associated with single HTML input element.
     *
     * @param name A String with the name.
     * @param value A FileItem with the value.
     */
    public void append(String name, FileItem value)
    {
        FileItem[] items = this.getFileItems(name);
        if (items == null)
        {
            items = new FileItem[1];
            items[0] = value;
            parameters.put(convert(name), items);
        }
        else
        {
            FileItem[] newItems = new FileItem[items.length + 1];
            System.arraycopy(items, 0, newItems, 0, items.length);
            newItems[items.length] = value;
            parameters.put(convert(name), newItems);
        }
    }

    /**
     * Return a FileItem object for the given name.  If the name does
     * not exist or the object stored is not a FileItem, return null.
     *
     * @param name A String with the name.
     * @return A FileItem.
     */
    public FileItem getFileItem(String name)
    {
        try
        {
            FileItem value = null;
            Object object = parameters.get(convert(name));
            if (object != null)
            {
                value = ((FileItem[]) object)[0];
            }
            return value;
        }
        catch (ClassCastException e)
        {
            return null;
        }
    }

    /**
     * Return an array of FileItem objects for the given name.  If the
     * name does not exist or the object stored is not a FileItem
     * array, return null.
     *
     * @param name A String with the name.
     * @return A FileItem[].
     */
    public FileItem[] getFileItems(String name)
    {
        try
        {
            return (FileItem[]) parameters.get(convert(name));
        }
        catch (ClassCastException e)
        {
            return null;
        }
    }
}
