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

import java.net.URL;

import java.util.Hashtable;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
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
 */
public class MultiPartEmail
    extends Email
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
    public MultiPartEmail( Criteria criteria )
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
        message.setContent( emailBody );

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
    protected void initCriteria( Criteria criteria )
        throws MessagingException
    {
        super.initCriteria(criteria);

        if( criteria.containsKey( EMAIL_BODY ) )
            setMsg( criteria.getString( EMAIL_BODY ) );
        else
            setMsg("NO MESSAGE");

        Vector attachments;

        if( criteria.containsKey(ATTACHMENTS ) )
            attachments = (Vector)criteria.get( ATTACHMENTS );
        else
            attachments = new Vector();

        if( criteria.containsKey(FILE_SERVER) )
            fileServer = criteria.getString(FILE_SERVER);

        for (int i=0; i<attachments.size(); i++)
        {
            EmailAttachment attachment =
                (EmailAttachment)attachments.elementAt(i);
            attach( attachment );
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
        if ( charset != null )
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
    public MultiPartEmail attach( EmailAttachment attachment )
        throws MessagingException
    {
        URL url = attachment.getURL();
        if( url == null )
        {
            try
            {
                String file = attachment.getPath();
                url = new URL( "file", fileServer, file );
            }
            catch( Exception e)
            {
                throw new MessagingException("Cannot find file", e);
            }
        }

        return attach(url,
                      attachment.getName(),
                      attachment.getDescription(),
                      attachment.getDisposition() );
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
    public MultiPartEmail attach( URL url,
                                  String name,
                                  String description )
        throws MessagingException
    {
        return attach( url, name, description, EmailAttachment.ATTACHMENT);
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
    public MultiPartEmail attach( URL url,
                                  String name,
                                  String description,
                                  String disposition)
        throws MessagingException
    {
        return attach( new URLDataSource(url), name, description, disposition );
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
    public MultiPartEmail attach( DataSource ds,
                                  String name,
                                  String description)
        throws MessagingException
    {
        return attach ( ds, name, description, EmailAttachment.ATTACHMENT);
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
    public MultiPartEmail attach( DataSource ds,
                                  String name,
                                  String description,
                                  String disposition)
        throws MessagingException
    {
        MimeBodyPart mbp = new MimeBodyPart();
        emailBody.addBodyPart( mbp );

        mbp.setDisposition( disposition );
        mbp.setFileName ( name );
        mbp.setDescription ( description );
        mbp.setDataHandler ( new DataHandler( ds ) );

        return this;
    }
}
