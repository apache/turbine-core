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

import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.torque.util.Criteria;

/**
 * A multipart email.
 *
 * <p>This class is used to send multi-part internet email like
 * messages with attachments.
 *
 * <p>To create a multi-part email, call the default constructor and
 * then you can call setMsg() to set the message and call the
 * different attach() methods.
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:unknown">Regis Koenig</a>
 * @version $Id$
 * @deprecated Use org.apache.commons.mail.MultiPartEmail instead.
 */
public class MultiPartEmail extends Email
{
    /** Body portion of the email. */
    protected MimeMultipart emailBody;

    /** The message container. */
    protected MimeBodyPart main;

    /** The file server if it exists. */
    private String fileServer = null;

    /**
     * Initialize the multipart email.
     *
     * @exception MessagingException.
     */
    public MultiPartEmail()
            throws MessagingException
    {
        this.init();
    }

    /**
     * Constructor used to initialize attributes.
     *
     * <p>This method uses the criteria object to set the different
     * fields of the e-mail. The expected fields of the Criteria are:
     *
     * <ul>
     * <li>SENDER_EMAIL</li>
     * <li>RECEIVER_EMAIL</li>
     * <li>EMAIL_SUBJECT</li>
     * <li>EMAIL_BODY</li>
     * <li>ATTACHMENTS - A Vector of EmailAttachment.</li>
     * <li>FILE_SERVER - Where the files are located.  If not given,
     * they are assumed to be local.</li>
     * </ul>
     *
     * Deprecated, since Criteria is deprecated in mail API.
     *
     * @param criteria A Criteria.
     * @exception MessagingException.
     */
    public MultiPartEmail(Criteria criteria)
            throws MessagingException
    {
        this.init();
        this.initCriteria(criteria);
    }

    /**
     * Initialize the multipart email.
     *
     * @exception MessagingException.
     */
    protected void init()
            throws MessagingException
    {
        super.init();

        fileServer = null;

        /* The body of the mail. */
        emailBody = new MimeMultipart();
        message.setContent(emailBody);

        /* The main message content. */
        main = new MimeBodyPart();
        emailBody.addBodyPart(main);
    }

    /**
     * Uses the criteria to set the fields.
     *
     * <p>This method uses the criteria object to set the different
     * fields of the e-mail.  The expected fields of the Criteria are:
     *
     * <ul>
     * <li>SENDER_EMAIL</li>
     * <li>RECEIVER_EMAIL</li>
     * <li>EMAIL_SUBJECT</li>
     * <li>EMAIL_BODY</li>
     * <li>ATTACHMENTS - A Vector of EmailAttachment.</li>
     * <li>FILE_SERVER - Where the files are located.  If not given,
     * they are assumed to be local.</li>
     * </ul>
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

        if (criteria.containsKey(EMAIL_BODY))
        {
            setMsg(criteria.getString(EMAIL_BODY));
        }
        else
        {
            setMsg("NO MESSAGE");
        }

        Vector attachments;

        if (criteria.containsKey(ATTACHMENTS))
        {
            attachments = (Vector) criteria.get(ATTACHMENTS);
        }
        else
        {
            attachments = new Vector();
        }

        if (criteria.containsKey(FILE_SERVER))
        {
            fileServer = criteria.getString(FILE_SERVER);
        }

        for (int i = 0; i < attachments.size(); i++)
        {
            EmailAttachment attachment =
                    (EmailAttachment) attachments.elementAt(i);
            attach(attachment);
        }
    }

    /**
     * Set the message of the email.
     *
     * @param msg A String.
     * @return An Email.
     * @exception MessagingException.
     */
    public Email setMsg(String msg)
            throws MessagingException
    {
        if (charset != null)
        {
            main.setText(msg, charset);
        }
        else
        {
            main.setText(msg);
        }
        return this;
    }

    /**
     * Attach an EmailAttachement.
     *
     * @param attachment An EmailAttachment.
     * @return A MultiPartEmail.
     * @exception MessagingException.
     */
    public MultiPartEmail attach(EmailAttachment attachment)
            throws MessagingException
    {
        URL url = attachment.getURL();
        if (url == null)
        {
            try
            {
                String file = attachment.getPath();
                url = new URL("file", fileServer, file);
            }
            catch (Exception e)
            {
                throw new MessagingException("Cannot find file", e);
            }
        }

        return attach(url, attachment.getName(),
                attachment.getDescription(),
                attachment.getDisposition());
    }

    /**
     * Attach a file located by its URL.  The disposition of the file
     * is set to mixed.
     *
     * @param url The URL of the file (may be any valid URL).
     * @param name The name field for the attachment.
     * @param description A description for the attachment.
     * @return A MultiPartEmail.
     * @exception MessagingException.
     */
    public MultiPartEmail attach(URL url, String name, String description)
            throws MessagingException
    {
        return attach(url, name, description, EmailAttachment.ATTACHMENT);
    }

    /**
     * Attach a file located by its URL.
     *
     * @param url The URL of the file (may be any valid URL).
     * @param name The name field for the attachment.
     * @param description A description for the attachment.
     * @param disposition Either mixed or inline.
     * @return A MultiPartEmail.
     * @exception MessagingException.
     */
    public MultiPartEmail attach(URL url,
                                 String name,
                                 String description,
                                 String disposition)
            throws MessagingException
    {
        return attach(new URLDataSource(url), name, description, disposition);
    }

    /**
     * Attach a file specified as a DataSource interface.
     *
     * @param ds A DataSource interface for the file.
     * @param name The name field for the attachment.
     * @param description A description for the attachment.
     * @return A MultiPartEmail.
     * @exception MessagingException.
     */
    public MultiPartEmail attach(DataSource ds,
                                 String name,
                                 String description)
            throws MessagingException
    {
        return attach(ds, name, description, EmailAttachment.ATTACHMENT);
    }

    /**
     * Attach a file specified as a DataSource interface.
     *
     * @param ds A DataSource interface for the file.
     * @param name The name field for the attachment.
     * @param description A description for the attachement.
     * @param disposition Either mixed or inline.
     * @return A MultiPartEmail.
     * @exception MessagingException.
     */
    public MultiPartEmail attach(DataSource ds,
                                 String name,
                                 String description,
                                 String disposition)
            throws MessagingException
    {
        MimeBodyPart mbp = new MimeBodyPart();
        emailBody.addBodyPart(mbp);

        mbp.setDisposition(disposition);
        mbp.setFileName(name);
        mbp.setDescription(description);
        mbp.setDataHandler(new DataHandler(ds));

        return this;
    }
}
