package org.apache.turbine.util.mail;

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
