package org.apache.turbine.util.webmacro;

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

import org.apache.turbine.services.webmacro.TurbineWebMacro;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.mail.Email;
import org.apache.turbine.util.mail.SimpleEmail;

import org.webmacro.Context;
import org.webmacro.servlet.WebContext;

/**
 * This is a simple class for sending email from within WebMacro.
 * Essentially, the body of the email is a WebMacro Context object.
 * The beauty of this is that you can send email from within your
 * WebMacro template or from your business logic in your Java code.
 * The body of the email is just a WebMacro template so you can use
 * all the template functionality of WebMacro within your emails!
 *
 * <p>Example Usage (This all needs to be on one line in your
 * template):
 *
 * <p>Setup your context:
 *
 * <p>context.put ("WebMacroEmail", new WebMacroEmail() );
 *
 * <p>Then, in your template:
 *
 * <pre>
 * $WebMacroEmail.setTo("Jon Stevens", "jon@clearink.com")
 *     .setFrom("Mom", "mom@mom.com").setSubject("Eat dinner")
 *     .setTemplate("email/momEmail.wm")
 *     .setContext($context)
 * </pre>
 *
 * The email/momEmail.wm template will then be parsed with the current
 * Context that is stored in the
 * RunData.getUser().getTemp(WebMacroService.WEBMACRO_CONTEXT)
 * location.  If the context does not already exist there, it will be
 * created and then that will be used.
 *
 * <p>If you want to use this class from within your Java code all you
 * have to do is something like this:
 *
 * <pre>
 * WebMacroEmail wme = new WebMacroEmail();
 * wme.setTo("Jon Stevens", "jon@clearink.com");
 * wme.setFrom("Mom", "mom@mom.com").setSubject("Eat dinner");
 * wme.setContext(context);
 * wme.setTemplate("email/momEmail.wm")
 * wme.send();
 * </pre>
 *
 * (Note that when used within a WebMacro template, the send method
 * will be called for you when WebMacro tries to convert the
 * WebMacroEmail to a string by calling toString).
 *
 * <p>This class is just a wrapper around the SimpleEmail class.
 * Thus, it uses the JavaMail API and also depends on having the
 * mail.server property set in the TurbineResources.properties file.
 * If you want to use this class outside of Turbine for general
 * processing that is also possible by making sure to set the path to
 * the TurbineResources.properties.  See the
 * TurbineResourceService.setPropertiesFileName() method for more
 * information.
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 * @deprecated you should use velocity
 */
public class WebMacroEmail
{
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

    /**
     * The template to process, relative to WM's template
     * directory.
     */
    private String template = null;

    /**
     * A WebContext
     */
    private WebContext context = null;

    /**
     * Constructor
     */
    public WebMacroEmail ()
    {
    }

    /**
     * Constructor
     */
    public WebMacroEmail (WebContext context)
    {
        this.context = context;
    }

    /**
     * To: name, email
     *
     * @param to A String with the TO name.
     * @param email A String with the TO email.
     * @return A WebMacroEmail (self).
     */
    public WebMacroEmail setTo(String to,
                               String email)
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
     * @return A WebMacroEmail (self).
     */
    public WebMacroEmail setFrom(String from,
                                 String email)
    {
        this.fromName = from;
        this.fromEmail = email;
        return (this);
    }

    /**
     * Subject.
     *
     * @param subject A String with the subject.
     * @return A WebMacroEmail (self).
     */
    public WebMacroEmail setSubject(String subject)
    {
        this.subject = subject;
        return (this);
    }

    /**
     * Webmacro template to execute. Path is relative to the WM
     * templates directory.
     *
     * @param template A String with the template.
     * @return A WebMacroEmail (self).
     */
    public WebMacroEmail setTemplate(String template)
    {
        this.template = template;
        return (this);
    }

    /**
     * Set the context object that will be merged with the
     * template.
     *
     * @param context A WebMacro context object.
     * @return A WebMacroEmail (self).
     */
    public WebMacroEmail setContext(WebContext context)
    {
        this.context = context;
        return (this);
    }

    /**
     * Get the context object that will be merged with the
     * template.
     *
     * @return A WebContext (self).
     */
    public WebContext getContext()
    {
        return this.context;
    }

    /**
     * This method sends the email.
     */
    public void send()
    {
        context.put("mail",this);
        try
        {
            // Process the template.
            String body = TurbineWebMacro.handleRequest(context,template);

            SimpleEmail se = new SimpleEmail();
            se.setFrom(fromEmail, fromName);
            se.addTo(toEmail, toName);
            se.setSubject(subject);
            se.setMsg(body);
            se.send();
        }
        catch (Exception e)
        {
            // Log the error.
            Log.error ("WebMacroEmail error: ", e);
        }
    }

    /**
     * The method toString() calls send() for ease of use within a
     * WebMacro template (see example usage above).
     *
     * @return An empty ("") String.
     */
    public String toString()
    {
        send();
        return "";
    }
}
