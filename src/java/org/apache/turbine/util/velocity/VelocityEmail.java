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

import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.StringUtils;
import org.apache.turbine.util.mail.SimpleEmail;
import org.apache.velocity.context.Context;

/**
 * This is a simple class for sending email from within Velocity.
 * Essentially, the body of the email is processed with a
 * Velocity Context object.
 * The beauty of this is that you can send email from within your
 * Velocity template or from your business logic in your Java code.
 * The body of the email is just a Velocity template so you can use
 * all the template functionality of Velocity within your emails!
 *
 * <p>Example Usage (This all needs to be on one line in your
 * template):
 *
 * <p>Setup your context:
 *
 * <p>context.put ("VelocityEmail", new VelocityEmail() );
 *
 * <p>Then, in your template:
 *
 * <pre>
 * $VelocityEmail.setTo("Jon Stevens", "jon@latchkey.com")
 *     .setFrom("Mom", "mom@mom.com").setSubject("Eat dinner")
 *     .setTemplate("email/momEmail.vm")
 *     .setContext($context)
 * </pre>
 *
 * The email/momEmail.wm template will then be parsed with the
 * Context that was defined with setContext().
 *
 * <p>If you want to use this class from within your Java code all you
 * have to do is something like this:
 *
 * <pre>
 * VelocityEmail ve = new VelocityEmail();
 * ve.setTo("Jon Stevens", "jon@latchkey.com");
 * ve.setFrom("Mom", "mom@mom.com").setSubject("Eat dinner");
 * ve.setContext(context);
 * ve.setTemplate("email/momEmail.vm")
 * ve.send();
 * </pre>
 *
 * <p>(Note that when used within a Velocity template, the send method
 * will be called for you when Velocity tries to convert the
 * VelocityEmail to a string by calling toString()).</p>
 *
 * <p>If you need your email to be word-wrapped, you can add the
 * following call to those above:
 *
 * <pre>
 * ve.setWordWrap (60);
 * </pre>
 *
 * <p>This class is just a wrapper around the SimpleEmail class.
 * Thus, it uses the JavaMail API and also depends on having the
 * mail.server property set in the TurbineResources.properties file.
 * If you want to use this class outside of Turbine for general
 * processing that is also possible by making sure to set the path to
 * the TurbineResources.properties.  See the
 * TurbineConfig class for more information.</p>
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:gcoladonato@yahoo.com">Greg Coladonato</a>
 * @version $Id$
 */
public class VelocityEmail
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

    /** The column to word-wrap at.  <code>0</code> indicates no wrap. */
    private int wordWrap = 0;

    /**
     * The template to process, relative to WM's template
     * directory.
     */
    private String template = null;

    /**
     * A Context
     */
    private Context context = null;

    /**
     * Constructor
     */
    public VelocityEmail()
    {
    }

    /**
     * Constructor
     */
    public VelocityEmail(Context context)
    {
        this.context = context;
    }

    /**
     * To: name, email
     *
     * @param to A String with the TO name.
     * @param email A String with the TO email.
     * @return A VelocityEmail (self).
     */
    public VelocityEmail setTo(String to, String email)
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
    public VelocityEmail setFrom(String from, String email)
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
    public VelocityEmail setSubject(String subject)
    {
        this.subject = subject;
        return (this);
    }

    /**
     * Velocity template to execute. Path is relative to the Velocity
     * templates directory.
     *
     * @param template A String with the template.
     * @return A VelocityEmail (self).
     */
    public VelocityEmail setTemplate(String template)
    {
        this.template = template;
        return (this);
    }

    /**
     * Set the column at which long lines of text should be word-
     * wrapped. Setting to zero turns off word-wrap (default).
     *
     * NOTE: don't use tabs in your email template document,
     * or your word-wrapping will be off for the lines with tabs
     * in them.
     *
     * @param wordWrap The column at which to wrap long lines.
     * @return A VelocityEmail (self).
     */
    public VelocityEmail setWordWrap(int wordWrap)
    {
        this.wordWrap = wordWrap;
        return (this);
    }

    /**
     * Set the context object that will be merged with the
     * template.
     *
     * @param context A Velocity context object.
     * @return A VelocityEmail (self).
     */
    public VelocityEmail setContext(Context context)
    {
        this.context = context;
        return (this);
    }

    /**
     * Get the context object that will be merged with the
     * template.
     *
     * @return A Context (self).
     */
    public Context getContext()
    {
        return this.context;
    }

    /**
     * This method sends the email.
     */
    public void send()
        throws Exception
    {
        // Process the template.
        String body = TurbineVelocity.handleRequest(context,template);

        // If the caller desires word-wrapping, do it here
        if (wordWrap > 0)
        {
            body = StringUtils.wrapText(body,
                    System.getProperty("line.separator"), wordWrap);
        }

        SimpleEmail se = new SimpleEmail();
        se.setFrom(fromEmail, fromName);
        se.addTo(toEmail, toName);
        se.setSubject(subject);
        se.setMsg(body);
        se.send();
    }

    /**
     * The method toString() calls send() for ease of use within a
     * Velocity template (see example usage above).
     *
     * @return An empty string.
     */
    public String toString()
    {
        try
        {
            send();
        }
        catch (Exception e)
        {
            Log.error ("VelocityEmail error", e);
        }
        return "";
    }
}
