package org.apache.turbine.util.mail;

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
