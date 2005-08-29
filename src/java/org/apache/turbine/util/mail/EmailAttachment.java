package org.apache.turbine.util.mail;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

/**
 * This class models an email attachment.  Used by MultiPartEmail.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @version $Id$
 * @deprecated Use org.apache.commons.mail.EmailAttachment instead.
 */
public class EmailAttachment
{
    public final static String ATTACHMENT = javax.mail.Part.ATTACHMENT;
    public final static String INLINE = javax.mail.Part.INLINE;

    /** The name of this attachment. */
    private String name = "";

    /** The description of this attachment. */
    private String description = "";

    /** The full path to this attachment (ie c:/path/to/file.jpg). */
    private String path = "";

    /** The HttpURI where the file can be got. */
    private URL url = null;

    /** The disposition. */
    private String disposition = ATTACHMENT;

    /**
     * Get the description.
     *
     * @return A String.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Get the name.
     *
     * @return A String.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the path.
     *
     * @return A String.
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Get the URL.
     *
     * @return A URL.
     */
    public URL getURL()
    {
        return url;
    }

    /**
     * Get the disposition.
     *
     * @return A String.
     */
    public String getDisposition()
    {
        return disposition;
    }

    /**
     * Set the description.
     *
     * @param desc A String.
     */
    public void setDescription(String desc)
    {
        this.description = desc;
    }

    /**
     * Set the name.
     *
     * @param name A String.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Set the path.
     *
     * @param path A String.
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /**
     * Set the URL.
     *
     * @param url A URL.
     */
    public void setURL(URL url)
    {
        this.url = url;
    }

    /**
     * Set the disposition.
     *
     * @param disposition A String.
     */
    public void setDisposition(String disposition)
    {
        this.disposition = disposition;
    }
}
