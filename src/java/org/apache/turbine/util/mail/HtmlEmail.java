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

import javax.activation.DataHandler;
import javax.activation.URLDataSource;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;

import org.apache.ecs.Document;
import org.apache.ecs.ElementContainer;
import org.apache.ecs.html.Body;
import org.apache.ecs.html.Html;
import org.apache.ecs.html.PRE;

/**
 * An HTML multipart email.
 *
 * <p>This class is used to send HTML formatted email.  A text message
 * can also be set for HTML unaware email clients, such as text-based
 * email clients.
 *
 * <p>This class also inherits from MultiPartEmail, so it is easy to
 * add attachents to the email.
 *
 * <p>To send an email in HTML, one should create a HtmlEmail, then
 * use the setFrom, addTo, etc. methods.  The HTML content can be set
 * with the setHtmlMsg method.  The alternate text content can be set
 * with setTextMsg.
 *
 * <p>Either the text or HTML can be omitted, in which case the "main"
 * part of the multipart becomes whichever is supplied rather than a
 * multipart/alternative.
 *
 * @author <a href="mailto:unknown">Regis Koenig</a>
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @version $Id$
 * @deprecated Use org.apache.commons.mail.HtmlEmail instead.
 */
public class HtmlEmail extends MultiPartEmail
{
    protected MimeMultipart htmlContent;

    protected String text;
    protected String html;

    /**
     * Basic constructor.
     *
     * @exception MessagingException.
     */
    public HtmlEmail()
            throws MessagingException
    {
        this.init();
    }

    /**
     * Instantiates a new MimeMultipart object if it isn't already
     * instantiated.
     *
     * @return A MimeMultipart object
     */
    public MimeMultipart getHtmlContent()
    {
        if (htmlContent == null)
        {
            htmlContent = new MimeMultipart();
        }
        return htmlContent;
    }

    /**
     * Set the text content.
     *
     * @param text A String.
     * @return An HtmlEmail.
     * @exception MessagingException.
     */
    public HtmlEmail setTextMsg(String text)
            throws MessagingException
    {
        this.text = text;
        return this;
    }

    /**
     * Set the HTML content.
     *
     * @param html A String.
     * @return An HtmlEmail.
     * @exception MessagingException.
     */
    public HtmlEmail setHtmlMsg(String html)
            throws MessagingException
    {
        this.html = html;
        return this;
    }

    /**
     * Set the HTML content based on an ECS document.
     *
     * @param doc A Document.
     * @return An HtmlEmail.
     * @exception MessagingException.
     */
    public HtmlEmail setHtmlMsg(Document doc)
            throws MessagingException
    {
        return setHtmlMsg(doc.toString());
    }

    /**
     * Set the message.
     *
     * <p>This method overrides the MultiPartEmail setMsg() method in
     * order to send an HTML message instead of a full text message in
     * the mail body. The message is formatted in HTML for the HTML
     * part of the message, it is let as is in the alternate text
     * part.
     *
     * @param msg A String.
     * @return An Email.
     * @exception MessagingException.
     */
    public Email setMsg(String msg)
            throws MessagingException
    {
        setTextMsg(msg);
        setHtmlMsg(new ElementContainer(new Html(new Body()
                .addElement(new PRE(msg)))).toString());
        return this;
    }

    /**
     * Embeds an URL in the HTML.
     *
     * <p>This method allows to embed a file located by an URL into
     * the mail body.  It allows, for instance, to add inline images
     * to the email.  Inline files may be referenced with a
     * <code>cid:xxxxxx</code> URL, where xxxxxx is the Content-ID
     * returned by the embed function.
     *
     * <p>Example of use:<br><code><pre>
     * HtmlEmail he = new HtmlEmail();
     * he.setHtmlMsg("&lt;html&gt;&lt;img src=cid:"+embed("file:/my/image.gif","image.gif")+"&gt;&lt;/html&gt;");
     * // code to set the others email fields (not shown)
     * </pre></code>
     *
     * @param url The URL of the file.
     * @param name The name that will be set in the filename header
     * field.
     * @return A String with the Content-ID of the file.
     * @exception MessagingException.
     */
    public String embed(URL url, String name)
            throws MessagingException
    {
        MimeBodyPart mbp = new MimeBodyPart();

        mbp.setDataHandler(new DataHandler(new URLDataSource(url)));
        mbp.setFileName(name);
        mbp.setDisposition("inline");
        String cid = org.apache.turbine.util.GenerateUniqueId.getIdentifier();
        mbp.addHeader("Content-ID", cid);

        getHtmlContent().addBodyPart(mbp);
        return mbp.getContentID();
    }

    /**
     * Does the work of actually sending the email.
     *
     * @exception MessagingException, if there was an error.
     */
    public void send()
            throws MessagingException
    {
        MimeBodyPart msgText = null;
        MimeBodyPart msgHtml = null;

        if (StringUtils.isNotEmpty(text) && StringUtils.isNotEmpty(html))
        {
            // The message in text and HTML form.
            MimeMultipart msg = getHtmlContent();
            msg.setSubType("alternative");
            main.setContent(msg);

            msgText = new MimeBodyPart();
            msgHtml = new MimeBodyPart();
            msg.addBodyPart(msgText);
            msg.addBodyPart(msgHtml);

        }
        else if (StringUtils.isNotEmpty(text))
        {
            // just text so the text part is the main part
            msgText = main;
        }
        else if (StringUtils.isNotEmpty(html))
        {
            // just HTML so the html part is the main part
            msgHtml = main;
        }
        else
        {
            msgText = main;
            text = "NO BODY";
        }

        if (msgText != null)
        {
            // add the text
            if (charset != null)
            {
                msgText.setText(text, charset);
            }
            else
            {
                msgText.setText(text);
            }
        }

        if (msgHtml != null)
        {
            // add the html
            if (charset != null)
            {
                msgHtml.setContent(html, TEXT_HTML + ";charset=" + charset);
            }
            else
            {
                msgHtml.setContent(html, TEXT_HTML);
            }
        }

        super.send();
    }
}
