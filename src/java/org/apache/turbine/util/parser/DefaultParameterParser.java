package org.apache.turbine.util.parser;

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

import java.net.URLDecoder;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.set.CompositeSet;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.upload.TurbineUpload;
import org.apache.turbine.services.upload.UploadService;
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
    implements ParameterParser, Recyclable
{
    /** Logging */
    private static Log log = LogFactory.getLog(DefaultParameterParser.class);

    /** The servlet request to parse. */
    private HttpServletRequest request = null;

    /** The raw data of a file upload. */
    private byte[] uploadData = null;

    /** Map of request parameters to FileItem[]'s */
    private Map fileParameters = new HashMap();

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
        super(characterEncoding);
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
        this.fileParameters.clear();
        super.dispose();
    }

    /**
     * Gets the parsed servlet request.
     *
     * @return the parsed servlet request or null.
     */
    public HttpServletRequest getRequest()
    {
        return request;
    }

    /**
     * Sets the servlet request to the parser.  This requires a
     * valid HttpServletRequest object.  It will attempt to parse out
     * the GET/POST/PATH_INFO data and store the data into a Map.
     * There are convenience methods for retrieving the data as a
     * number of different datatypes.  The PATH_INFO data must be a
     * URLEncoded() string.
     * <p>
     * To add name/value pairs to this set of parameters, use the
     * <code>add()</code> methods.
     *
     * @param request An HttpServletRequest.
     */
    public void setRequest(HttpServletRequest request)
    {
        clear();

        uploadData = null;

        String enc = request.getCharacterEncoding();
        setCharacterEncoding(enc != null
                ? enc
                : TurbineConstants.PARAMETER_ENCODING_DEFAULT);

        String contentType = request.getHeader("Content-type");

        if (uploadServiceIsAvailable
                && uploadService.getAutomatic()
                && contentType != null
                && contentType.startsWith("multipart/form-data"))
        {
            if (log.isDebugEnabled())
            {
                log.debug("Running the Turbine Upload Service");
            }

            try
            {
                TurbineUpload.parseRequest(request, this);
            }
            catch (TurbineException e)
            {
                log.error("File upload failed", e);
            }
        }

        for (Enumeration names = request.getParameterNames();
             names.hasMoreElements();)
        {
            String paramName = (String) names.nextElement();
            add(paramName,
                    request.getParameterValues(paramName));
        }

        // Also cache any pathinfo variables that are passed around as
        // if they are query string data.
        try
        {
            boolean isNameTok = true;
            String paramName = null;
            String paramValue = null;

            for ( StringTokenizer st =
                          new StringTokenizer(request.getPathInfo(), "/");
                  st.hasMoreTokens();)
            {
                if (isNameTok)
                {
                    paramName = URLDecoder.decode(st.nextToken(), getCharacterEncoding());
                    isNameTok = false;
                }
                else
                {
                    paramValue = URLDecoder.decode(st.nextToken(), getCharacterEncoding());
                    if (paramName.length() > 0)
                    {
                        add(paramName, paramValue);
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

        this.request = request;

        if (log.isDebugEnabled())
        {
            log.debug("Parameters found in the Request:");
            for (Iterator it = keySet().iterator(); it.hasNext();)
            {
                String key = (String) it.next();
                log.debug("Key: " + key + " -> " + getString(key));
            }
        }
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
        return uploadData;
    }

    /**
     * Add a FileItem object as a parameters.  If there are any
     * FileItems already associated with the name, append to the
     * array.  The reason for this is that RFC 1867 allows multiple
     * files to be associated with single HTML input element.
     *
     * @param name A String with the name.
     * @param value A FileItem with the value.
     * @deprecated Use add(String name, FileItem item)
     */
    public void append(String name, FileItem item)
    {
        add(name, item);
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
    public void add(String name, FileItem item)
    {
        FileItem[] items = getFileItemParam(name);
        items = (FileItem []) ArrayUtils.add(items, item);
        putFileItemParam(name, items);
    }

    /**
     * Gets the set of keys (FileItems and regular parameters)
     *
     * @return A <code>Set</code> of the keys.
     */
    public Set keySet()
    {
        return new CompositeSet(new Set[] { super.keySet(), fileParameters.keySet() } );
    }

    /**
     * Determine whether a given key has been inserted.  All keys are
     * stored in lowercase strings, so override method to account for
     * this.
     *
     * @param key An Object with the key to search for.
     * @return True if the object is found.
     */
    public boolean containsKey(Object key)
    {
        if (super.containsKey(key))
        {
            return true;
        }

        return fileParameters.containsKey(convert(String.valueOf(key)));
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
        FileItem [] value = getFileItemParam(name);

        return (value == null
                || value.length == 0)
                ? null : value[0];
    }

    /**
     * Return an array of FileItem objects for the given name.  If the
     * name does not exist, return null.
     *
     * @param name A String with the name.
     * @return An Array of  FileItems or null.
     */
    public FileItem[] getFileItems(String name)
    {
        return getFileItemParam(name);
    }

    /**
     * Puts a key into the parameters map. Makes sure that the name is always
     * mapped correctly. This method also enforces the usage of arrays for the
     * parameters.
     *
     * @param name A String with the name.
     * @param value An array of Objects with the values.
     *
     */
    protected void putFileItemParam(final String name, final FileItem [] value)
    {
        String key = convert(name);
        if (key != null)
        {
            fileParameters.put(key, value);
        }
    }

    /**
     * fetches a key from the parameters map. Makes sure that the name is
     * always mapped correctly.
     *
     * @param name A string with the name
     *
     * @return the value object array or null if not set
     */
    protected FileItem [] getFileItemParam(final String name)
    {
        String key = convert(name);

        return (key != null) ? (FileItem []) fileParameters.get(key) : null;
    }

    /**
     * This method is only used in toString() and can be used by
     * derived classes to add their local parameters to the toString()

     * @param name A string with the name
     *
     * @return the value object array or null if not set
     */
    protected Object [] getToStringParam(final String name)
    {
        if (super.containsKey(name))
        {
            return getParam(name);
        }
        else
        {
            return getFileItemParam(name);
        }
    }
}
