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

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Creates a very simple text/plain message and sends it.
 *
 * <p>MailMessage creates a very simple text/plain message and sends
 * it.  It can be used like this:<br>
 * <pre>
 * MailMessage sm = new MailMessage("mail.domain.net",
 *                                  "toYou@domain.net",
 *                                  "fromMe@domain",
 *                                  "this is the subject",
 *                                  "this is the body");
 * </pre>
 *
 * Another example is:<br>
 * <pre>
 * MailMessage sm = new MailMessage();
 * sm.setHost("mail.domain.net");
 * sm.setHeaders("X-Mailer: Sendmail class, X-Priority: 1(Highest)");
 * sm.setFrom("Net Freak1 user1@domain.com");
 * sm.setReplyTo("Net Freak8 user8@domain.com");
 * sm.setTo("Net Freak2 user2@domain.com, Net Freak3 user3@domain.com");
 * sm.setCc("Net Freak4 user4@domain.com, Net Freak5 user5@domain.com");
 * sm.setBcc("Net Freak6 user6@domain.com, Net Freak7 user7@domain.com");
 * sm.setSubject("New Sendmail Test");
 * sm.setBody("Test message from Sendmail class, more features to be added.
 *             Like multipart messages, html, binary files...");
 * sm.setDebug(true);
 * sm.send();
 * </pre>
 *
 * @author <a href="mailto:david@i2a.com">David Duddleston</a>
 * @version $Id$
 */
public class MailMessage
{
    /**
     * The host name of the mail server to use.
     */
    protected String host;

    /**
     * Used to specify the mail headers.  Example:
     *
     * X-Mailer: Sendmail, X-Priority: 1(highest)
     * or  2(high) 3(normal) 4(low) and 5(lowest)
     * Disposition-Notification-To: returnR user@domain.net
     */
    protected Hashtable headers;

    /**
     * The email address that the mail is being sent from.
     */
    protected InternetAddress from;

    /**
     * The email address used for replies to this message.
     */
    protected InternetAddress[] replyTo;

    /**
     * The email address or addresses that the email is being sent to.
     */
    protected InternetAddress[] to;

    /**
     * The email address or addresses that the email is being
     * carbon-copied to.
     */
    protected InternetAddress[] cc;

    /**
     * The email address or addresses that the email is being
     * blind-carbon-copied to.
     */
    protected InternetAddress[] bcc;

    /**
     * The subject of the email message.
     */
    protected String subject;

    /**
     * The body of the email message.
     */
    protected String body;

    /**
     * Displays debug information when true.
     */
    protected boolean debug;

    /**
     * Default constructor.  Must use the setHost, setTo, and other
     * set functions to properly send an email.  <b>host</b>,
     * <b>to</b>, <b>cc</b>, <b>bcc</b>, and <b>from</b> are set to
     * null.  <b>subject</b>, and <b>body</b> are set to empty
     * strings.  <b>debug</b> is set to false.
     */
    public MailMessage()
    {
        this(null,null,null,null,null,"","",false);
    }

    /**
     * Constructor used to specify <b>host</b>, <b>to</b>,
     * <b>from</b>, <b>subject</b>, and <b>body</b>.
     *
     * @param h A String with the host.
     * @param t A String with the TO.
     * @param f A String with the FROM.
     * @param s A String with the SUBJECT.
     * @param b A String with the BODY.
     */
    public MailMessage(String h,
                       String t,
                       String f,
                       String s,
                       String b)
    {
        this(h,t,null,null,f,s,b,false);
    }

    /**
     * Constructor used to specify <b>host</b>, <b>to</b>, <b>cc</b>,
     * <b>bcc</b>, <b>from</b>, <b>subject</b>, <b>body</b>, and
     * <b>debug</b>.
     *
     * @param h A String with the host.
     * @param t A String with the TO.
     * @param c A String with the CC.
     * @param bc A String with the BCC.
     * @param f A String with the FROM.
     * @param s A String with the SUBJECT.
     * @param b A String with the BODY.
     * @param d True if debugging is wanted.
     */
    public MailMessage(String h,
                       String t,
                       String c,
                       String bc,
                       String f,
                       String s,
                       String b,
                       boolean d)
    {
        host = h;
        to = (t == null?null:parseAddressField(t));
        cc = (cc == null?null:parseAddressField(c));
        bcc = (bc == null?null:parseAddressField(bc));
        from = (f == null?null:parseInternetAddress(f));
        subject = s;
        body = b;
        debug = d;
    }

    /**
     * Adds a header (name, value) to the headers Hashtable.
     *
     * @param name A String with the name.
     * @param value A String with the value.
     */
    public void addHeader(String name,
                          String value)
    {
        if (headers == null)
        {
            headers = new Hashtable();
        }
        headers.put(name,value);
    }

    /**
     * Parse an address field.
     *
     * @param str A String with the address.
     * @return An InternetAddress[].
     */
    public static InternetAddress[] parseAddressField(String str)
    {
        String[] addressList;
        if (str.indexOf(",") != -1)
        {
            Vector v = new Vector();
            StringTokenizer st = new StringTokenizer(str, ",", false);
            while (st.hasMoreTokens())
            {
                v.addElement(st.nextToken());
            }
            addressList = new String[v.size()];
            for (int i = 0; i < v.size(); i++)
            {
                addressList[i] = (String)v.elementAt(i);
            }
        }
        else
        {
            addressList = new String[1];
            addressList[0] = str;
        }
        return parseAddressList(addressList);
    }

    /**
     * Parse an address list.
     *
     * @param aList A String[] with the addresses.
     * @return An InternetAddress[].
     */
    public static InternetAddress[] parseAddressList(String[] aList)
    {
        InternetAddress[] ia = new InternetAddress[aList.length];

        for (int i = 0; i < aList.length; i++)
        {
            ia[i] = parseInternetAddress(aList[i]);
        }

        return ia;

    }

    /**
     * Parse a header.
     *
     * @param str A String with the header.
     * @param headers A Hashtable with the current headers.
     */
    public static void parseHeader(String str,
                                   Hashtable headers)
    {
        String name = null;
        String value = null;

        str = str.trim();
        int sp = str.lastIndexOf(":");
        name = str.substring(0, sp);
        value = (str.substring(sp+1)).trim();

        headers.put(name, value);
    }

    /**
     * Parse a header field.
     *
     * @param str A String with the header field.
     * @return A Hashtable with the parsed headers.
     */
    public static Hashtable parseHeaderField(String str)
    {
        String[] headerList;
        if (str.indexOf(",") != -1)
        {
            Vector v = new Vector();
            StringTokenizer st = new StringTokenizer(str, ",", false);
            while( st.hasMoreTokens() )
            {
                v.addElement(st.nextToken());
            }
            headerList = new String[v.size()];
            for (int i = 0; i < v.size(); i++)
            {
                headerList[i] = (String)v.elementAt(i);
            }
        }
        else
        {
            headerList = new String[1];
            headerList[0] = str;
        }
        return parseHeaderList(headerList);
    }

    /**
     * Parse a header list.
     *
     * @param hList A String[] with the headers.
     * @return A Hashtable with the parsed headers.
     */
    public static Hashtable parseHeaderList(String[] hList)
    {
        Hashtable headers = new Hashtable();

        for (int i = 0; i < hList.length; i++)
        {
            // headers.put("one", new Integer(1));
            parseHeader(hList[i], headers);
        }

        return headers;
    }

    /**
     * Parse an Internet address.
     *
     * @param str A String with the address.
     * @return An InternetAddress.
     */
    public static InternetAddress parseInternetAddress(String str)
    {
        String address = null;
        String personal = null;

        str = str.trim();
        if (str.indexOf(" ") == -1)
        {
            address = str;
        }
        else
        {
            int sp = str.lastIndexOf(" ");
            address = str.substring(sp+1);
            personal = str.substring(0, sp);
        }
        return parseInternetAddress(address, personal);
    }

    /**
     * Parse an Internet address.
     *
     * @param address A String with the address.
     * @param personal A String.
     * @return An InternetAddress.
     */
    public static InternetAddress parseInternetAddress(String address,
                                                       String personal)
    {
        InternetAddress ia = null;
        try
        {
            ia = new InternetAddress(address);

            if (personal != null)
            {
                ia.setPersonal(personal);
            }
        }
        catch (AddressException e)
        {
            e.printStackTrace();
            System.out.println();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            System.out.println();
        }

        return ia;
    }

    /**
     * Send the message.  The to, from, subject, host, and body should
     * be set prior to using this method.
     *
     * @return True is message was sent.
     */
    public boolean send()
    {
        // Create some properties and get the default Session.
        Properties props = new Properties();
        props.put("mail.smtp.host", host);

        Session session = Session.getInstance(props, null);
        session.setDebug(debug);

        try
        {
            // Create a message.
            Message msg = new MimeMessage(session);

            // Set the email address that the message is from.
            msg.setFrom(from);

            // Set the email addresses that the message is to.
            msg.setRecipients(Message.RecipientType.TO, to);

            // Set the email addresses that will be carbon-copied.
            if (cc != null)
            {
                msg.setRecipients(Message.RecipientType.CC, cc);
            }

            // Set the email addresses that will be
            // blind-carbon-copied.
            if (bcc != null)
            {
                msg.setRecipients(Message.RecipientType.BCC, bcc);
            }

            // Set the email addresses that reply-to messages are
            // sent.
            if (replyTo != null)
            {
                msg.setReplyTo(replyTo);
            }

            // Set the subject of the email message.
            msg.setSubject(subject);

            // Set the body of the message.  If the desired charset is
            // known, use setText(text, charset).
            msg.setText(body);

            // msg.addHeader("X-Mailer", "com.i2a.util.mail.Sendmail");

            if (headers != null)
            {
                Enumeration e = headers.keys();
                while (e.hasMoreElements())
                {
                    String name = (String)e.nextElement();
                    String value = (String)headers.get(name);
                    msg.addHeader(name, value);
                }
            }

            // Send the message.
            Transport.send(msg);
        }
        catch (MessagingException mex)
        {
            mex.printStackTrace();
            Exception ex = null;
            if ((ex = mex.getNextException()) != null)
            {
                ex.printStackTrace();
            }
            return false;
        }
        return true;
    }

    /**
     * Used to specify the email address that the mail is being
     * blind-carbon-copied to.
     *
     * @param bc An InternetAddress[].
     */
    public void setBcc(InternetAddress[] bc)
    {
        bcc = bc;
    }

    /**
     * Used to specify the email address that the mail is being
     * blind-carbon-copied to.
     *
     * @param bc A String.
     */
    public void setBcc(String bc)
    {
        bcc = parseAddressField(bc);
    }

    /**
     * Used to specify the body of the email message.
     *
     * @param b A String.
     */
    public void setBody(String b)
    {
        body = b;
    }

    /**
     * Used to specify the email address that the mail is being sent
     * to.
     *
     * @param c An InternetAddress[].
     */
    public void setCc(InternetAddress[] c)
    {
        cc = c;
    }

    /**
     * Used to specify the email address that the mail is being
     * carbon-copied to.
     *
     * @param c A String.
     */
    public void setCc(String c)
    {
        cc = parseAddressField(c);
    }

    /**
     * Setting to true will enable the display of debug information.
     *
     * @param str A String.
     */
    public void setDebug(String str)
    {
        if (str.equals("1"))
        {
            debug = true;
        }
        else if (str.equals("0"))
        {
            debug = false;
        }
        else
        {
            debug = new Boolean(str).booleanValue();
        }
    }

    /**
     * Setting to true will enable the display of debug information.
     *
     * @param d A boolean.
     */
    public void setDebug(boolean d)
    {
        debug = d;
    }

    /**
     * Used to specify the email address that the mail is being sent
     * from.
     *
     * @param f A String.
     */
    public void setFrom(String f)
    {
        from = parseInternetAddress(f);
    }

    /**
     * Used to specify the email address that the mail is being sent
     * from.
     *
     * @param f An InternetAddress.
     */
    public void setFrom(InternetAddress f)
    {
        from = f;
    }

    /**
     * Used to specify the mail headers.  Example:
     *
     * X-Mailer: Sendmail, X-Priority: 1(highest)
     * or  2(high) 3(normal) 4(low) and 5(lowest)
     * Disposition-Notification-To: returnR user@domain.net
     *
     * @param h A String.
     */
    public void setHeaders(String h)
    {
        headers = parseHeaderField(h);
    }

    /**
     * Used to specify the mail headers.  Example:
     *
     * X-Mailer: Sendmail, X-Priority: 1(highest)
     * or  2(high) 3(normal) 4(low) and 5(lowest)
     * Disposition-Notification-To: returnR user@domain.net
     *
     * @param h A Hashtable.
     */
    public void setHeaders(Hashtable h)
    {
        headers = h;
    }

    /**
     * Used to specify the mail server host.
     *
     * @param h A String.
     */
    public void setHost(String h)
    {
        host = h;
    }

    /**
     * Used to specify the email address that the mail is being sent
     * from.
     *
     * @param rt An InternetAddress[].
     */
    public void setReplyTo(InternetAddress[] rt)
    {
        replyTo = rt;
    }

    /**
     * Used to specify the email address that the mail is being sent
     * from.
     *
     * @param rp A String.
     */
    public void setReplyTo(String rp)
    {
        replyTo = parseAddressField(rp);
    }

    /**
     * Used to specify the subject of the email message.
     *
     * @param s A String.
     */
    public void setSubject(String s)
    {
        subject = s;
    }

    /**
     * Used to specify the email address that the mail is being sent
     * to.
     *
     * @param t An InternetAddress[].
     */
    public void setTo(InternetAddress[] t)
    {
        to = t;
    }

    /**
     * Used to specify the email address that the mail is being sent
     * to.
     *
     * @param t A String.
     */
    public void setTo(String t)
    {
        to = parseAddressField(t);
    }
}
