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

import java.util.Date;
import java.util.Properties;
import java.util.Vector;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.torque.util.Criteria;
import org.apache.turbine.services.resources.TurbineResources;

/**
 * The base class for all email messages.  This class sets the
 * sender's email & name, receiver's email & name, subject, and the
 * sent date.  Subclasses are responsible for setting the message
 * body.
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @author <a href="mailto:unknown">Regis Koenig</a>
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

    public static final String MAIL_SERVER = "mail.server";
    public static final String MAIL_HOST = "mail.host";
    public static final String MAIL_SMTP_FROM = "mail.smtp.from";
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
        Properties properties = System.getProperties();
        properties.put(MAIL_TRANSPORT_PROTOCOL, SMTP);
        properties.put(MAIL_HOST, TurbineResources.getString(MAIL_SERVER));
        String mailSMTPFrom = TurbineResources.getString(MAIL_SMTP_FROM);
        if(mailSMTPFrom!=null && !mailSMTPFrom.equals(""))
        {
            properties.put(MAIL_SMTP_FROM, mailSMTPFrom);
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
        catch(Exception e)
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
        Transport.send( message );
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
