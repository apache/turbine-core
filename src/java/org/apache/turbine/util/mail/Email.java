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

import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.configuration.Configuration;

import org.apache.commons.lang.StringUtils;

import org.apache.torque.util.Criteria;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;

/**
 * The base class for all email messages.  This class sets the
 * sender's email & name, receiver's email & name, subject, and the
 * sent date.  Subclasses are responsible for setting the message
 * body.
 *
 * @version $Id$
 * @deprecated Use org.apache.commons.mail.Email instead.
 */
public abstract class Email
{
    /** Constants used to Email classes. */
    public static final String SENDER_EMAIL = "sender.email";
    public static final String SENDER_NAME = "sender.name";
    public static final String RECEIVER_EMAIL = "receiver.email";
    public static final String RECEIVER_NAME = "receiver.name";
    public static final String EMAIL_SUBJECT = "email.subject";
    public static final String EMAIL_BODY = "email.body";
    public static final String CONTENT_TYPE = "content.type";

    /** @deprecated Use TurbineConstants.MAIL_SERVER_KEY */
    public static final String MAIL_SERVER = TurbineConstants.MAIL_SERVER_KEY;

    /** @deprecated Use TurbineConstants.MAIL_SMTP_FROM */
    public static final String MAIL_SMTP_FROM = TurbineConstants.MAIL_SMTP_FROM;

    /** Mail Host, for javax.mail */
    public static final String MAIL_HOST = "mail.host";

    public static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    public static final String SMTP = "SMTP";
    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String ATTACHMENTS = "attachments";
    public static final String FILE_SERVER = "file.server";

    public static final String KOI8_R = "koi8-r";
    public static final String ISO_8859_1 = "iso-8859-1";
    public static final String US_ASCII = "us-ascii";

    /** The email message to send. */
    protected MimeMessage message;

    /** The charset to use for this message */
    protected String charset = null;

    /** Lists of related email adresses */
    private Vector toList;
    private Vector ccList;
    private Vector bccList;
    private Vector replyList;

    /**
     * Set the charset of the message.
     *
     * @param charset A String.
     */
    public void setCharset(String charset)
    {
        this.charset = charset;
    }

    /**
     * TODO: Document.
     *
     * @return A Session.
     */
    private Session getMailSession()
    {
        Configuration conf = Turbine.getConfiguration();
        Properties properties = System.getProperties();

        properties.put(MAIL_TRANSPORT_PROTOCOL, SMTP);
        properties.put(MAIL_HOST, conf.getString(TurbineConstants.MAIL_SERVER_KEY,
                                                 TurbineConstants.MAIL_SERVER_DEFAULT));


        String mailSMTPFrom = conf.getString(TurbineConstants.MAIL_SMTP_FROM);

        if (StringUtils.isNotEmpty(mailSMTPFrom))
        {
            properties.put(TurbineConstants.MAIL_SMTP_FROM, mailSMTPFrom);
        }
        return Session.getDefaultInstance(properties, null);
    }

    /**
     * Initializes the mail.
     *
     * Deprecated.
     *
     * @param criteria A Criteria.
     * @exception MessagingException.
     * @see #init() init.
     */
    protected void initialize(Criteria criteria) throws MessagingException
    {
        init();
        initCriteria(criteria);
    }

    /**
     * Initializes the mail.
     *
     * <p>This is the first method that should be called by a subclass
     * in its constructor.
     *
     * @exception MessagingException.
     */
    protected void init() throws MessagingException
    {

        // Create the message.
        message = new MimeMessage(getMailSession());

        toList = new Vector();
        ccList = new Vector();
        bccList = new Vector();
        replyList = new Vector();

        // Set the sent date.
        setSentDate(new Date());
    }

    /**
     * Initialize the mail according to the Criteria.
     *
     * <p>This method uses the criteria parameter to set the from, to
     * and subject fields of the email.
     *
     * Deprecated; one should use the setFrom, addTo, etc. methods.
     *
     * @param criteria A Criteria.
     * @exception MessagingException.
     */
    protected void initCriteria(Criteria criteria) throws MessagingException
    {
        // Set the FROM field.
        if (criteria.containsKey(SENDER_EMAIL)
                && criteria.containsKey(SENDER_NAME))
        {
            setFrom(criteria.getString(SENDER_EMAIL),
                    criteria.getString(SENDER_NAME));
        }

        // Set the TO field.
        if (criteria.containsKey(RECEIVER_EMAIL)
                && criteria.containsKey(RECEIVER_NAME))
        {
            addTo(criteria.getString(RECEIVER_EMAIL),
                    criteria.getString(RECEIVER_NAME));
        }

        // Set the SUBJECT field.
        if (criteria.containsKey(EMAIL_SUBJECT))
        {
            setSubject(criteria.getString(EMAIL_SUBJECT));
        }
        else
        {
            setSubject("no subject available");
        }
    }

    /**
     * Set the FROM field of the email.
     *
     * @param email A String.
     * @param name A String.
     * @return An Email.
     * @exception MessagingException.
     */
    public Email setFrom(String email, String name) throws MessagingException
    {
        try
        {
            if (name == null || name.trim().equals(""))
            {
                name = email;
            }
            message.setFrom(new InternetAddress(email, name));
        }
        catch (Exception e)
        {
            throw new MessagingException("cannot set from", e);
        }
        return this;
    }

    /**
     * Add a recipient TO to the email.
     *
     * @param email A String.
     * @param name A String.
     * @return An Email.
     * @exception MessagingException.
     */
    public Email addTo(String email, String name) throws MessagingException
    {
        try
        {
            if (name == null || name.trim().equals(""))
            {
                name = email;
            }
            toList.addElement(new InternetAddress(email, name));
        }
        catch (Exception e)
        {
            throw new MessagingException("cannot add to", e);
        }
        return this;
    }

    /**
     * Add a recipient CC to the email.
     *
     * @param email A String.
     * @param name A String.
     * @return An Email.
     * @exception MessagingException.
     */
    public Email addCc(String email, String name) throws MessagingException
    {

        try
        {
            if (name == null || name.trim().equals(""))
            {
                name = email;
            }
            ccList.addElement(new InternetAddress(email, name));
        }
        catch (Exception e)
        {
            throw new MessagingException("cannot add cc", e);
        }

        return this;
    }

    /**
     * Add a blind BCC recipient to the email.
     *
     * @param email A String.
     * @param name A String.
     * @return An Email.
     * @exception MessagingException.
     */
    public Email addBcc(String email, String name)
            throws MessagingException
    {
        try
        {
            if (name == null || name.trim().equals(""))
            {
                name = email;
            }
            bccList.addElement(new InternetAddress(email, name));
        }
        catch (Exception e)
        {
            throw new MessagingException("cannot add bcc", e);
        }

        return this;
    }

    /**
     * Add a reply to address to the email.
     *
     * @param email A String.
     * @param name A String.
     * @return An Email.
     * @exception MessagingException.
     */
    public Email addReplyTo(String email, String name)
            throws MessagingException
    {
        try
        {
            if (name == null || name.trim().equals(""))
            {
                name = email;
            }
            replyList.addElement(new InternetAddress(email, name));
        }
        catch (Exception e)
        {
            throw new MessagingException("cannot add replyTo", e);
        }
        return this;
    }

    /**
     * Set the email subject.
     *
     * @param subject A String.
     * @return An Email.
     * @exception MessagingException.
     */
    public Email setSubject(String subject)
            throws MessagingException
    {
        if (subject != null)
        {
            if (charset != null)
            {
                message.setSubject(subject, charset);
            }
            else
            {
                message.setSubject(subject);
            }
        }
        return this;
    }

    /**
     * Set the sent date field.
     *
     * @param date A Date.
     * @return An Email.
     * @exception MessagingException.
     */
    public Email setSentDate(Date date)
            throws MessagingException
    {
        if (date != null)
        {
            message.setSentDate(date);
        }
        return this;
    }

    /**
     * Define the content of the mail.  It should be overidden by the
     * subclasses.
     *
     * @param msg A String.
     * @return An Email.
     * @exception MessagingException.
     */
    public abstract Email setMsg(String msg)
            throws MessagingException;

    /**
     * Does the work of actually sending the email.
     *
     * @exception MessagingException, if there was an error.
     */
    public void send()
            throws MessagingException
    {
        InternetAddress[] foo = new InternetAddress[0];
        message.setRecipients(Message.RecipientType.TO,
                toInternetAddressArray(toList));
        message.setRecipients(Message.RecipientType.CC,
                toInternetAddressArray(ccList));
        message.setRecipients(Message.RecipientType.BCC,
                toInternetAddressArray(bccList));
        message.setReplyTo(toInternetAddressArray(replyList));
        Transport.send(message);
    }

    /**
     * Utility to copy Vector of known InternetAddress objects into an
     * array.
     *
     * @param v A Vector.
     * @return An InternetAddress[].
     */
    private InternetAddress[] toInternetAddressArray(Vector v)
    {
        int size = v.size();
        InternetAddress[] ia = new InternetAddress[size];
        for (int i = 0; i < size; i++)
        {
            ia[i] = (InternetAddress) v.elementAt(i);
        }
        return ia;
    }
}
