package org.apache.turbine.util.mail;

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

import javax.mail.MessagingException;
import org.apache.torque.util.Criteria;

/**
 * This class is used to send simple internet email messages without
 * attachments.
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:unknown">Regis Koenig</a>
 * @version $Id$
 * @deprecated Use org.apache.commons.mail.SimpleEmail instead.
 */
public class SimpleEmail
    extends Email
{
    /** the conentet type for body of the message */
    private String contentType = null;

    /**
     * Default constructor.
     *
     * @exception MessagingException.
     */
    public SimpleEmail() throws MessagingException
    {
        super.init();
    }

    /**
     * Constructor used to initialize attributes.  To create a simple
     * email you need to pass a Criteria object into the SimpleEmail
     * constructor which contains:
     *
     * <ul>
     * <li>SENDER_EMAIL</li>
     * <li>SENDER_NAME</li>
     * <li>RECEIVER_EMAIL</li>
     * <li>RECEIVER_NAME</li>
     * <li>EMAIL_SUBJECT</li>
     * <li>EMAIL_BODY</li>
     * </ul>
     *
     * Deprecated, since Criteria is deprecated in mail API.
     *
     * @param criteria A Criteria.
     * @exception MessagingException.
     */
    public SimpleEmail(Criteria criteria)
        throws MessagingException
    {
        super.init();
        this.initCriteria(criteria);
    }

    /**
     * Uses the criteria to set the fields.
     *
     * Deprecated, since the Criteria is deprecated.
     *
     * @param criteria A Criteria.
     * @exception MessagingException.
     */
    protected void initCriteria(Criteria criteria)
        throws MessagingException
    {
        super.initCriteria(criteria);

        if (criteria.containsKey(CONTENT_TYPE))
        {
            contentType = criteria.getString(CONTENT_TYPE);
        }

        if (criteria.containsKey(EMAIL_BODY))
        {
            setMsg(criteria.getString(EMAIL_BODY));
        }
        else
        {
            setMsg("NO MESSAGE");
        }
    }

    /**
     * Set the content of the mail
     *
     * @param msg A String.
     * @return An Email.
     * @exception MessagingException.
     */
    public Email setMsg(String msg) throws MessagingException
    {
        if (contentType == null)
        {
            contentType = TEXT_PLAIN;
        }
        message.setContent(msg, contentType);
        return this;
    }
}
