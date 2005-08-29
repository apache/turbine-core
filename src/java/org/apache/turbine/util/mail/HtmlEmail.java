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
