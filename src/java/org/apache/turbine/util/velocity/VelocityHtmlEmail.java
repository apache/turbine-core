package org.apache.turbine.util.velocity;


/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import java.net.URL;
import java.util.Hashtable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.velocity.VelocityService;
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
 * <p>You can turn on debugging for the JavaMail API by calling
 * setDebug(true).  The debugging messages will be written to System.out.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:A.Schild@aarboard.ch">Andre Schild</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class VelocityHtmlEmail extends HtmlEmail
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
    private Hashtable<String, String> embmap = null;

    /** Address of outgoing mail server */
    private String mailServer;

    /**
     * Constructor
     */
    public VelocityHtmlEmail()
    {
        super();
    }

    /**
     * Constructor, sets the context object.
     *
     * @param context A Velocity context object.
     */
    public VelocityHtmlEmail(Context context)
    {
        this();
        this.context = context;
        embmap = new Hashtable<String, String>();
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
     * @throws EmailException thrown if mail cannot be sent.
     */
    @Override
    public String send() throws EmailException
    {
        context.put("mail", this);

        try
        {
            VelocityService velocityService = (VelocityService)TurbineServices.getInstance().getService(VelocityService.SERVICE_NAME);

            if (htmlTemplate != null)
            {
                setHtmlMsg(velocityService.handleRequest(context, htmlTemplate));
            }
            if (textTemplate != null)
            {
                setTextMsg(velocityService.handleRequest(context, textTemplate));
            }
        }
        catch (Exception e)
        {
            throw new EmailException("Cannot parse velocity template", e);
        }
        setHostName(getMailServer());
        return super.send();
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
     * <pre>
     * &lt;html&gt;
     * &lt;!-- $mail.embed("http://server/border.gif","border.gif"); --&gt;
     * &lt;img src=$mail.getCid("border.gif")&gt;
     * &lt;p&gt;This is your content
     * &lt;img src=$mail.getCid("border.gif")&gt;
     * &lt;/html&gt;
     * </pre>
     *
     * @param surl A String.
     * @param name A String.
     * @return A String with the cid of the embedded file.
     *
     * @see HtmlEmail#embed(URL surl, String name) embed.
     */
    @Override
    public String embed(String surl, String name)
    {
        String cid = "";
        try
        {
            URL url = new URL(surl);
            cid = super.embed(url, name);
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
        String cid = embmap.get(filename);
        return "cid:" + cid;
    }

}
