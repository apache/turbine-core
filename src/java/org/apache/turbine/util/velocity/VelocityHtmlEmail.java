package org.apache.turbine.util.velocity;

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

import javax.activation.DataSource;
import javax.activation.URLDataSource;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;

import org.apache.turbine.services.velocity.TurbineVelocity;

import org.apache.turbine.util.RunData;

import org.apache.velocity.context.Context;

/**
 * This is a simple class for sending html email from within Velocity.
 * Essentially, the bodies (text and html) of the email are a Velocity
 * Context objects.  The beauty of this is that you can send email
 * from within your Velocity template or from your business logic in
 * your Java code.  The body of the email is just a Velocity template
 * so you can use all the template functionality of Velocity within
 * your emails!
 *
 * <p>This class allows you to send HTML email with embedded content
 * and/or with attachments.  You can access the VelocityHtmlEmail
 * instance within your templates trough the <code>$mail</code>
 * Velocity variable.
 * <p><code>VelocityHtmlEmail   myEmail= new VelocityHtmlEmail(data);<br>
 *                              context.put("mail", myMail);</code>
 * <b>or</b>
 *    <code>VelocityHtmlEmail   myEmail= new VelocityHtmlEmail(context);<br>
 *                              context.put("mail", myMail);</code>
 *
 *
 * <p>The templates should be located under your Template turbine
 * directory.
 *
 * <p>This class wraps the HtmlEmail class from commons-email.  Thus, it uses
 * the JavaMail API and also depends on having the mail.server property
 * set in the TurbineResources.properties file.  If you want to use
 * this class outside of Turbine for general processing that is also
 * possible by making sure to set the path to the
 * TurbineResources.properties.  See the
 * TurbineResourceService.setPropertiesFileName() method for more
 * information.
 *
 * <p>This class is basically a conversion of the WebMacroHtmlEmail
 * written by Regis Koenig
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:A.Schild@aarboard.ch">Andre Schild</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class VelocityHtmlEmail
{
    /** Logging */
    private static Log log = LogFactory.getLog(VelocityHtmlEmail.class);

    /**
     * The html template to process, relative to VM's template
     * directory.
     */
    private String htmlTemplate = null;

    /**
     * The text template to process, relative to VM's template
     * directory.
     */
    private String textTemplate = null;

    /** The cached context object. */
    private Context context = null;

    /** The map of embedded files. */
    private Hashtable embmap = null;

    /** The HtmlEmail to send */
    private HtmlEmail htmlEmail = null;

    /** The to name field. */
    private String toName = null;

    /** The to email field. */
    private String toEmail = null;

    /** The from name field. */
    private String fromName = null;

    /** The from email field. */
    private String fromEmail = null;

    /** The subject of the message. */
    private String subject = null;

    /** Address of outgoing mail server */
    private String mailServer;

    /**
     * Constructor, sets the context object from
     * the passed RunData object
     *
     * @param data A Turbine RunData object.
     * @exception VelocityEmailException
     */
    public VelocityHtmlEmail(RunData data)
            throws VelocityEmailException
    {
        try
        {
            this.context = TurbineVelocity.getContext(data);
            embmap = new Hashtable();
            htmlEmail = new HtmlEmail();
        }
        catch (Exception e)
        {
            throw new VelocityEmailException(e);
        }
    }

    /**
     * Constructor, sets the context object.
     *
     * @param context A Velocity context object.
     * @exception VelocityEmailException
     */
    public VelocityHtmlEmail(Context context)
            throws VelocityEmailException
    {
        try
        {
            this.context = context;
            embmap = new Hashtable();
            htmlEmail = new HtmlEmail();
        }
        catch (Exception e)
        {
            throw new VelocityEmailException(e);
        }
    }

    /**
     * To: name, email
     *
     * @param to A String with the TO name.
     * @param email A String with the TO email.
     * @return A VelocityEmail (self).
     */
    public VelocityHtmlEmail setTo(String to, String email)
    {
        this.toName = to;
        this.toEmail = email;
        return (this);
    }

    /**
     * From: name, email.
     *
     * @param from A String with the FROM name.
     * @param email A String with the FROM email.
     * @return A VelocityEmail (self).
     */
    public VelocityHtmlEmail setFrom(String from, String email)
    {
        this.fromName = from;
        this.fromEmail = email;
        return (this);
    }

    /**
     * Subject.
     *
     * @param subject A String with the subject.
     * @return A VelocityEmail (self).
     */
    public VelocityHtmlEmail setSubject(String subject)
    {
        this.subject = subject;
        return (this);
    }

    /**
     * Set the HTML template for the mail.  This is the Velocity
     * template to execute for the HTML part.  Path is relative to the
     * VM templates directory.
     *
     * @param template A String.
     * @return A VelocityHtmlEmail (self).
     */
    public VelocityHtmlEmail setHtmlTemplate(String template)
    {
        this.htmlTemplate = template;
        return this;
    }

    /**
     * Set the text template for the mail.  This is the Velocity
     * template to execute for the text part.  Path is relative to the
     * VM templates directory
     *
     * @param template A String.
     * @return A VelocityHtmlEmail (self).
     */
    public VelocityHtmlEmail setTextTemplate(String template)
    {
        this.textTemplate = template;
        return this;
    }

    /**
     * Sets the address of the outgoing mail server.  This method
     * should be used when you need to override the value stored in
     * TR.props.
     *
     * @param serverAddress host name of your outgoing mail server
     */
    public void setMailServer(String serverAddress)
    {
        this.mailServer = serverAddress;
    }

    /**
     * Gets the host name of the outgoing mail server.  If the server
     * name has not been set by calling setMailServer(), the value
     * from TR.props for mail.server will be returned.  If TR.props
     * has no value for mail.server, localhost will be returned.
     *
     * @return host name of the mail server.
     */
    public String getMailServer()
    {
        return StringUtils.isNotEmpty(mailServer) ? mailServer
            : Turbine.getConfiguration().getString(
                TurbineConstants.MAIL_SERVER_KEY,
                TurbineConstants.MAIL_SERVER_DEFAULT);
    }

    /**
     * Actually send the mail.
     *
     * @exception VelocityEmailException thrown if mail cannot be sent.
     */
    public void send()
            throws VelocityEmailException
    {
        try
        {
            context.put("mail", this);

            String htmlbody = "";
            String textbody = "";

            // Process the templates.
            try
            {
                if (htmlTemplate != null)
                {
                    htmlbody = TurbineVelocity.handleRequest(context, htmlTemplate);
                }
                if (textTemplate != null)
                {
                    textbody = TurbineVelocity.handleRequest(context, textTemplate);
                }
            }
            catch (Exception e)
            {
                throw new VelocityEmailException("Cannot parse template", e);
            }

            htmlEmail.setFrom(fromEmail, fromName);
            htmlEmail.addTo(toEmail, toName);
            htmlEmail.setSubject(subject);
            htmlEmail.setHtmlMsg(htmlbody);
            htmlEmail.setTextMsg(textbody);
            htmlEmail.setHostName(getMailServer());
            htmlEmail.send();
        }
        catch (Exception e)
        {
            throw new VelocityEmailException(e);
        }
    }

    /**
     * Embed a file in the mail.  The file can be referenced through
     * its Content-ID.  This function also registers the CID in an
     * internal map, so the embedded file can be referenced more than
     * once by using the getCid() function.  This may be useful in a
     * template.
     *
     * <p>Example of template:
     *
     * <code><pre width="80">
     * &lt;html&gt;
     * &lt;!-- $mail.embed("http://server/border.gif","border.gif"); --&gt;
     * &lt;img src=$mail.getCid("border.gif")&gt;
     * &lt;p&gt;This is your content
     * &lt;img src=$mail.getCid("border.gif")&gt;
     * &lt;/html&gt;
     * </pre></code>
     *
     * @param surl A String.
     * @param name A String.
     * @return A String with the cid of the embedded file.
     * @exception VelocityEmailException
     * @see HtmlEmail#embed(URL surl, String name) embed.
     */
    public String embed(String surl, String name) throws VelocityEmailException
    {
        String cid = "";
        try
        {
            URL url = new URL(surl);
            cid = htmlEmail.embed(url, name);
            embmap.put(name, cid);
        }
        catch (Exception e)
        {
            log.error("cannot embed " + surl + ": ", e);
        }
        return cid;
    }

    /**
     * Get the cid of an embedded file.
     *
     * @param filename A String.
     * @return A String with the cid of the embedded file.
     * @see #embed(String surl, String name) embed.
     */
    public String getCid(String filename)
    {
        String cid = (String) embmap.get(filename);
        return "cid:" + cid;
    }

    /***
     * Add a recipient TO to the email.
     *
     * @param email A String.
     * @param name A String.
     * @return An Email.
     * @exception VelocityEmailException
     */
    public VelocityHtmlEmail addTo(String email, String name)
            throws VelocityEmailException
    {
        try
        {
            htmlEmail.addTo(email, name);
        }
        catch (Exception e)
        {
            throw new VelocityEmailException("cannot add to", e);
        }
        return this;
    }

    /***
     * Add a recipient CC to the email.
     *
     * @param email A String.
     * @param name A String.
     * @return An Email.
     * @exception VelocityEmailException
     */
    public VelocityHtmlEmail addCc(String email, String name)
            throws VelocityEmailException
    {

        try
        {
            htmlEmail.addCc(email, name);
        }
        catch (Exception e)
        {
            throw new VelocityEmailException("cannot add cc", e);
        }

        return this;
    }

    /***
     * Add a blind BCC recipient to the email.
     *
     * @param email A String.
     * @param name A String.
     * @return An Email.
     * @exception VelocityEmailException
     */
    public VelocityHtmlEmail addBcc(String email, String name)
            throws VelocityEmailException
    {
        try
        {
            htmlEmail.addBcc(email, name);
        }
        catch (Exception e)
        {
            throw new VelocityEmailException("cannot add bcc", e);
        }

        return this;
    }

    /***
     * Add a reply to address to the email.
     *
     * @param email A String.
     * @param name A String.
     * @return An Email.
     * @exception VelocityEmailException
     */
    public VelocityHtmlEmail addReplyTo(String email, String name)
            throws VelocityEmailException
    {
        try
        {
            htmlEmail.addReplyTo(email, name);
        }
        catch (Exception e)
        {
            throw new VelocityEmailException("cannot add replyTo", e);
        }
        return this;
    }

    /***
     * Attach an EmailAttachement.
     *
     * @param attachment An EmailAttachment.
     * @return A MultiPartEmail.
     * @exception VelocityEmailException
     */
    public VelocityHtmlEmail attach(EmailAttachment attachment)
            throws VelocityEmailException
    {
        try
        {
            htmlEmail.attach(attachment);
            return this;
        }
        catch (Exception e)
        {
            throw new VelocityEmailException("Could not attach file", e);
        }
    }

    /***
     * Attach a file located by its URL.  The disposition of the file
     * is set to mixed.
     *
     * @param url The URL of the file (may be any valid URL).
     * @param name The name field for the attachment.
     * @param description A description for the attachment.
     * @return A MultiPartEmail.
     * @exception VelocityEmailException
     */
    public VelocityHtmlEmail attach(URL url, String name, String description)
            throws VelocityEmailException
    {
        return attach(url, name, description, EmailAttachment.ATTACHMENT);
    }

    /***
     * Attach a file located by its URL.
     *
     * @param url The URL of the file (may be any valid URL).
     * @param name The name field for the attachment.
     * @param description A description for the attachment.
     * @param disposition Either mixed or inline.
     * @return A MultiPartEmail.
     * @exception VelocityEmailException
     */
    public VelocityHtmlEmail attach(URL url,
                                    String name,
                                    String description,
                                    String disposition)
            throws VelocityEmailException
    {
        return attach(new URLDataSource(url), name, description, disposition);
    }

    /***
     * Attach a file specified as a DataSource interface.
     *
     * @param ds A DataSource interface for the file.
     * @param name The name field for the attachment.
     * @param description A description for the attachment.
     * @return A MultiPartEmail.
     * @exception VelocityEmailException
     */
    public VelocityHtmlEmail attach(DataSource ds,
                                    String name,
                                    String description)
            throws VelocityEmailException
    {
        return attach(ds, name, description, EmailAttachment.ATTACHMENT);
    }

    /***
     * Attach a file specified as a DataSource interface.
     *
     * @param ds A DataSource interface for the file.
     * @param name The name field for the attachment.
     * @param description A description for the attachement.
     * @param disposition Either mixed or inline.
     * @return A MultiPartEmail.
     * @exception VelocityEmailException
     */
    public VelocityHtmlEmail attach(DataSource ds,
                                    String name,
                                    String description,
                                    String disposition)
            throws VelocityEmailException
    {
        try
        {
            htmlEmail.attach(ds, name, description, disposition);
        }
        catch (Exception e)
        {
            throw new VelocityEmailException("Could attach " + name, e);
        }

        return this;
    }

}
