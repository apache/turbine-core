package org.apache.turbine.services.rundata;

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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ecs.Document;
import org.apache.ecs.Element;
import org.apache.ecs.StringElement;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.mimetype.TurbineMimeTypes;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.util.CookieParser;
import org.apache.turbine.util.FormMessages;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.ServerData;
import org.apache.turbine.util.SystemError;
import org.apache.turbine.util.pool.Recyclable;
import org.apache.turbine.util.pool.RecyclableSupport;
import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.util.template.TemplateInfo;

/**
 * DefaultTurbineRunData is the default implementation of the
 * TurbineRunData interface, which is distributed by the Turbine
 * RunData service, if another implementation is not defined in
 * the default or specified RunData configuration.
 * TurbineRunData is an extension to RunData, which
 * is an interface to run-rime information that is passed
 * within Turbine. This provides the threading mechanism for the
 * entire system because multiple requests can potentially come in
 * at the same time.  Thus, there is only one RunData implementation
 * for each request that is being serviced.
 *
 * <p>DefaultTurbineRunData implements the Recyclable interface making
 * it possible to pool its instances for recycling.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:bhoeneis@ee.ethz.ch">Bernie Hoeneisen</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
public class DefaultTurbineRunData
        extends RecyclableSupport
        implements TurbineRunData,
        Recyclable
{
    /**
     * The default locale.
     */
    private static Locale defaultLocale;

    /**
     * The default locale checked flag.
     */
    private static boolean defaultLocaleChecked;

    /**
     * The default charset.
     */
    private static String defaultCharSet;

    /**
     * The default charset checked flag.
     */
    private static boolean defaultCharSetChecked;

    /**
     * A reference to the GET/POST data parser.
     */
    private ParameterParser parameters;

    /**
     * A reference to a cookie parser.
     */
    public CookieParser cookies;

    /**
     * The servlet request interface.
     */
    private HttpServletRequest req;

    /**
     * The servlet response interface.
     */
    private HttpServletResponse res;

    /**
     * The servlet session information.
     */
    private HttpSession session;

    /**
     * The servlet configuration.
     */
    private ServletConfig config;

    /**
     * The servlet context information.
     * Note that this is from the
     * "Turbine" Servlet context.
     */
    private ServletContext servletContext;

    /**
     * The access control list.
     */
    private AccessControlList acl;

    /**
     *  Determines if there is information in the document or not.
     */
    private boolean pageSet;

    /**
     * This creates an ECS Document.
     */
    private Document page;

    /**
     * Cached action name to execute for this request.
     */
    private String action;

    /**
     * This is the layout that the page will use to render the screen.
     */
    private String layout;

    /**
     * Cached screen name to execute for this request.
     */
    private String screen;

    /**
     * The character encoding of template files.
     */
    private String templateEncoding;

    /**
     * Information used by a Template system (such as
     * Velocity/Freemarker).
     */
    private TemplateInfo templateInfo;

    /**
     * This is where output messages from actions should go.
     */
    private StringElement message;

    /**
     * This is a dedicated message class where output messages from
     * actions should go.
     */
    private FormMessages messages;

    /**
     * The user object.
     */
    private User user;

    /**
     * This is what will build the <title></title> of the document.
     */
    private String title;

    /**
     * Determines if there is information in the outputstream or
     * not.
     */
    private boolean outSet;

    /**
     * Cache the output stream because it can be used in many
     * different places.
     */
    private PrintWriter out;

    /**
     * The locale.
     */
    private Locale locale;

    /**
     * The HTTP charset.
     */
    private String charSet;

    /**
     * The HTTP content type to return.
     */
    private String contentType = "text/html";

    /**
     * If this is set, also set the status code to 302.
     */
    private String redirectURI;

    /**
     * The HTTP status code to return.
     */
    private int statusCode = HttpServletResponse.SC_OK;

    /**
     * This is a vector to hold critical system errors.
     */
    private Vector errors = new Vector();

    /**
     * JNDI Contexts.
     */
    private Hashtable jndiContexts;

    /**
     * Holds ServerData (basic properties) about this RunData object.
     */
    private ServerData serverData;

    /**
     * @see #getRemoteAddr()
     */
    private String remoteAddr;

    /**
     * @see #getRemoteHost()
     */
    private String remoteHost;

    /**
     * @see #getUserAgent()
     */
    private String userAgent;

    /**
     * A holder for stack trace.
     */
    private String stackTrace;

    /**
     * A holder ofr stack trace exception.
     */
    private Throwable stackTraceException;

    /**
     * Put things here and they will be shown on the default Error
     * screen.  This is great for debugging variable values when an
     * exception is thrown.
     */
    private Hashtable varDebug = new Hashtable();

    /**
     * Attempts to get the User object from the session.  If it does
     * not exist, it returns null.
     *
     * @param session An HttpSession.
     * @return A User.
     */
    public static User getUserFromSession(HttpSession session)
    {
        try
        {
            return (User) session.getValue(User.SESSION_KEY);
        }
        catch (ClassCastException e)
        {
            return null;
        }
    }

    /**
     * Allows one to invalidate the user in a session.
     *
     * @param session An HttpSession.
     * @return True if user was invalidated.
     */
    public static boolean removeUserFromSession(HttpSession session)
    {
        try
        {
            session.removeValue(User.SESSION_KEY);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    /**
     * Gets the default locale defined by properties named
     * "locale.default.lang" and "locale.default.country".
     *
     * @return the default locale or null.
     */
    protected static Locale getDefaultLocale()
    {
        if (!defaultLocaleChecked)
        {
            /* Get the default locale and cache it in a static variable. */
            String lang = TurbineResources.getString("locale.default.language");
            String country = TurbineResources.getString("locale.default.country");
            if (lang != null)
            {
                defaultLocale = country != null ?
                        new Locale(lang, country) : new Locale(lang, "");
            }
            else if (country != null)
            {
                defaultLocale = new Locale("", country);
            }
            else
            {
                defaultLocale = null;
            }
            defaultLocaleChecked = true;
        }
        return defaultLocale;
    }

    /**
     * Gets the default charset defined by a property named
     * "locale.default.charset" or by the specified locale.
     * If the specified locale is null, the default locale is applied.
     *
     * @return the name of the default charset or null.
     */
    protected String getDefaultCharSet()
    {
        if (!defaultCharSetChecked)
        {
            /* Get the default charset and cache it in a static variable. */
            defaultCharSet = TurbineResources.
                    getString("locale.default.charset");
            defaultCharSetChecked = true;
        }

        String charset = defaultCharSet;
        if (charset == null)
        {
            /* Default charset isn't specified, get the locale specific one. */
            Locale locale = this.locale;
            if (locale == null)
            {
                locale = getDefaultLocale();
            }
            if ((locale != null) &&
                    !locale.equals(Locale.US))
            {
                charset = TurbineMimeTypes.getCharSet(locale);
            }
        }
        return charset;
    }

    /**
     * Constructs a run data object.
     */
    public DefaultTurbineRunData()
    {
        super();
    }

    /**
     * Recycles a run data object.
     */
    public void recycle()
    {
        super.recycle();
    }

    /**
     * Disposes a run data object.
     */
    public void dispose()
    {
        parameters = null;
        cookies = null;
        req = null;
        res = null;
        session = null;
        config = null;
        servletContext = null;
        acl = null;
        pageSet = false;
        page = null;
        action = null;
        layout = null;
        screen = null;
        templateEncoding = null;
        templateInfo = null;
        message = null;
        messages = null;
        user = null;
        title = null;
        outSet = false;
        out = null;
        locale = null;
        charSet = null;
        contentType = "text/html";
        redirectURI = null;
        statusCode = HttpServletResponse.SC_OK;
        errors.clear();
        jndiContexts = null;
        serverData = null;
        remoteAddr = null;
        remoteHost = null;
        userAgent = null;
        stackTrace = null;
        stackTraceException = null;
        varDebug.clear();

        super.dispose();
    }

    // ***************************************
    // Implementation of the RunData interface
    // ***************************************

    /**
     * Gets the parameters.
     *
     * @return a parameter parser.
     */
    public ParameterParser getParameters()
    {
        // Parse the parameters first, if not yet done.
        if ((this.parameters != null) &&
                (this.parameters.getRequest() != this.req))
        {
            this.parameters.setRequest(this.req);
        }
        return this.parameters;
    }

    /**
     * Gets the cookies.
     *
     * @return a cookie parser.
     */
    public CookieParser getCookies()
    {
        // Parse the cookies first, if not yet done.
        if ((this.cookies != null) &&
                (this.cookies.getRunData() != this))
        {
            this.cookies.setRunData(this);
        }
        return this.cookies;
    }

    /**
     * Gets the servlet request.
     *
     * @return the request.
     */
    public HttpServletRequest getRequest()
    {
        return this.req;
    }

    /**
     * Gets the servlet response.
     *
     * @return the resposne.
     */
    public HttpServletResponse getResponse()
    {
        return this.res;
    }

    /**
     * Gets the servlet session information.
     *
     * @return the session.
     */
    public HttpSession getSession()
    {
        return session;
    }

    /**
     * Gets the servlet configuration used during servlet init.
     *
     * @return the configuration.
     */
    public ServletConfig getServletConfig()
    {
        return this.config;
    }

    /**
     * Gets the servlet context used during servlet init.
     *
     * @return the context.
     */
    public ServletContext getServletContext()
    {
        return this.servletContext;
    }

    /**
     * Gets the access control list.
     *
     * @return the access control list.
     */
    public AccessControlList getACL()
    {
        return acl;
    }

    /**
     * Sets the access control list.
     *
     * @param acl an access control list.
     */
    public void setACL(AccessControlList acl)
    {
        this.acl = acl;
    }

    /**
     * Checks to see if the page is set.
     *
     * @return true if the page is set.
     * @deprecated no replacement planned, ECS is no longer a requirement
     */
    public boolean isPageSet()
    {
        return pageSet;
    }

    /**
     * Gets the page.
     *
     * @return a document.
     * @deprecated no replacement planned, ECS is no longer a requirement
     */
    public Document getPage()
    {
        pageSet = true;
        if (this.page == null)
            this.page = new Document();
        return this.page;
    }

    /**
     * Whether or not an action has been defined.
     *
     * @return true if an action has been defined.
     */
    public boolean hasAction()
    {
        return (this.action != null &&
                this.action.length() > 0 &&
                !this.action.equalsIgnoreCase("null"));
    }

    /**
     * Gets the action. It returns an empty string if null so
     * that it is easy to do conditionals on it based on the
     * equalsIgnoreCase() method.
     *
     * @return a string, "" if null.
     */
    public String getAction()
    {
        return (hasAction() ? this.action : "");
    }

    /**
     * Sets the action for the request.
     *
     * @param action a atring.
     */
    public void setAction(String action)
    {
        this.action = action;
    }

    /**
     * If the Layout has not been defined by the screen then set the
     * layout to be "DefaultLayout".  The screen object can also
     * override this method to provide intelligent determination of
     * the Layout to execute.  You can also define that logic here as
     * well if you want it to apply on a global scale.  For example,
     * if you wanted to allow someone to define layout "preferences"
     * where they could dynamicially change the layout for the entire
     * site.
     *
     * @return a string.
     */

    public String getLayout()
    {
        if (this.layout == null)
        {
            /*
             * This will return something if the template
             * services are running. If we get nothing we
             * will fall back to the ECS layout.
             */
            layout = TurbineTemplate.getDefaultLayoutName(this);

            if (layout == null)
            {
                layout = "DefaultLayout";
            }
        }

        return this.layout;
    }

    /**
     * Set the layout for the request.
     *
     * @param layout a string.
     */
    public void setLayout(String layout)
    {
        this.layout = layout;
    }

    /**
     * Convenience method for a template info that
     * returns the layout template being used.
     *
     * @return a string.
     */
    public String getLayoutTemplate()
    {
        return getTemplateInfo().getLayoutTemplate();
    }

    /**
     * Modifies the layout template for the screen. This convenience
     * method allows for a layout to be modified from within a
     * template. For example;
     *
     *    $data.setLayoutTemplate("/NewLayout.vm")
     *
     * @param layout a layout template.
     */
    public void setLayoutTemplate(String layout)
    {
        getTemplateInfo().setLayoutTemplate(layout);
    }

    /**
     * Whether or not a screen has been defined.
     *
     * @return true if a screen has been defined.
     */
    public boolean hasScreen()
    {
        return (this.screen != null &&
                this.screen.length() > 0);
    }

    /**
     * Gets the screen to execute.
     *
     * @return a string.
     */
    public String getScreen()
    {
        return (hasScreen() ? this.screen : "");
    }

    /**
     * Sets the screen for the request.
     *
     * @param screen a string.
     */
    public void setScreen(String screen)
    {
        this.screen = screen;
    }

    /**
     * Convenience method for a template info that
     * returns the name of the template being used.
     *
     * @return a string.
     */
    public String getScreenTemplate()
    {
        return getTemplateInfo().getScreenTemplate();
    }

    /**
     * Sets the screen template for the request. For
     * example;
     *
     *    $data.setScreenTemplate("NewScreen.vm")
     *
     * @param screen a screen template.
     */
    public void setScreenTemplate(String screen)
    {
        getTemplateInfo().setScreenTemplate(screen);
    }

    /**
     * Gets the character encoding to use for reading template files.
     *
     * @return the template encoding or null if not specified.
     */
    public String getTemplateEncoding()
    {
        return templateEncoding;
    }

    /**
     * Sets the character encoding to use for reading template files.
     *
     * @param encoding the template encoding.
     */
    public void setTemplateEncoding(String encoding)
    {
        templateEncoding = encoding;
    }

    /**
     * Gets the template info. Creates a new one if needed.
     *
     * @return a template info.
     */
    public TemplateInfo getTemplateInfo()
    {
        if (templateInfo == null)
        {
            templateInfo = new TemplateInfo(this);
        }
        return templateInfo;
    }

    /**
     * Whether or not a message has been defined.
     *
     * @return true if a message has been defined.
     */
    public boolean hasMessage()
    {
        return (this.message != null &&
                this.message.toString().length() > 0);
    }

    /**
     * Gets the results of an action or another message
     * to be displayed as a string.
     *
     * @return a string.
     */
    public String getMessage()
    {
        return (this.message == null ? null : this.message.toString());
    }

    /**
     * Sets the message for the request as a string.
     *
     * @param msg a string.
     */
    public void setMessage(String msg)
    {
        this.message = new StringElement(msg);
    }

    /**
     * Adds the string to message. If message has prior messages from
     * other actions or screens, this method can be used to chain them.
     *
     * @param msg a string.
     */
    public void addMessage(String msg)
    {
        addMessage(new StringElement(msg));
    }

    /**
     * Gets the results of an action or another message
     * to be displayed as an ECS string element.
     *
     * @return a string element.
     */
    public StringElement getMessageAsHTML()
    {
        return this.message;
    }

    /**
     * Sets the message for the request as an ECS element.
     *
     * @param msg an element.
     */
    public void setMessage(Element msg)
    {
        this.message = new StringElement(msg);
    }

    /**
     * Adds the ECS element to message. If message has prior messages from
     * other actions or screens, this method can be used to chain them.
     *
     * @param msg an element.
     */
    public void addMessage(Element msg)
    {
        if (msg != null)
        {
            if (message != null)
            {
                message.addElement(msg);
            }
            else
            {
                message = new StringElement(msg);
            }
        }
    }

    /**
     * Unsets the message for the request.
     */
    public void unsetMessage()
    {
        this.message = null;
    }

    /**
     * Gets a FormMessages object where all the messages to the
     * user should be stored.
     *
     * @return a FormMessages.
     */
    public FormMessages getMessages()
    {
        if (this.messages == null)
        {
            this.messages = new FormMessages();
        }
        return this.messages;
    }

    /**
     * Sets the FormMessages object for the request.
     *
     * @param msgs A FormMessages.
     */
    public void setMessages(FormMessages msgs)
    {
        this.messages = msgs;
    }

    /**
     * Gets the title of the page.
     *
     * @return a string.
     */
    public String getTitle()
    {
        return (this.title == null ? "" : this.title);
    }

    /**
     * Sets the title of the page.
     *
     * @param title a string.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Checks if a user exists in this session.
     *
     * @return true if a user exists in this session.
     */
    public boolean userExists()
    {
        user = getUserFromSession();
        return (user != null);
    }

    /**
     * Gets the user.
     *
     * @return a user.
     */
    public User getUser()
    {
        return this.user;
    }

    /**
     * Sets the user.
     *
     * @param user a user.
     */
    public void setUser(User user)
    {
        this.user = user;
    }

    /**
     * Attempts to get the user from the session. If it does
     * not exist, it returns null.
     *
     * @return a user.
     */
    public User getUserFromSession()
    {
        return getUserFromSession(session);
    }

    /**
     * Allows one to invalidate the user in the default session.
     *
     * @return true if user was invalidated.
     */
    public boolean removeUserFromSession()
    {
        return removeUserFromSession(session);
    }

    /**
     * Checks to see if out is set.
     *
     * @return true if out is set.
     * @deprecated no replacement planned, response writer will not be cached
     */
    public boolean isOutSet()
    {
        return outSet;
    }

    /**
     * Gets the print writer. First time calling this
     * will set the print writer via the response.
     *
     * @return a print writer.
     * @throws IOException.
     */
    public PrintWriter getOut()
            throws IOException
    {
        // Check to see if null first.
        if (this.out == null)
            setOut(res.getWriter());
        pageSet = false;
        outSet = true;
        return this.out;
    }

    /**
     * Declares that output will be direct to the response stream,
     * even though getOut() may never be called.  Useful for response
     * mechanisms that may call res.getWriter() themselves
     * (such as JSP.)
     */
    public void declareDirectResponse()
    {
        outSet = true;
        pageSet = false;
    }

    /**
     * Gets the locale. If it has not already been defined with
     * setLocale(), then  properties named "locale.default.lang"
     * and "locale.default.country" are checked from the Resource
     * Service and the corresponding locale is returned. If these
     * properties are undefined, JVM's default locale is returned.
     *
     * @return the locale.
     */
    public Locale getLocale()
    {
        Locale locale = this.locale;
        if (locale == null)
        {
            locale = getDefaultLocale();
            if (locale == null)
            {
                locale = Locale.getDefault();
            }
        }
        return locale;
    }

    /**
     * Sets the locale.
     *
     * @param locale the new locale.
     */
    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    /**
     * Gets the charset. If it has not already been defined with
     * setCharSet(), then a property named "locale.default.charset"
     * is checked from the Resource Service and returned. If this
     * property is undefined, the default charset of the locale
     * is returned. If the locale is undefined, null is returned.
     *
     * @return the name of the charset or null.
     */
    public String getCharSet()
    {
        String charset = this.charSet;
        if (charset == null)
        {
            charset = getDefaultCharSet();
        }
        return charset;
    }

    /**
     * Sets the charset.
     *
     * @param charset the name of the new charset.
     */
    public void setCharSet(String charset)
    {
        this.charSet = charset;
    }

    /**
     * Gets the HTTP content type to return. If a charset
     * has been specified, it is included in the content type.
     * If the charset has not been specified and the main type
     * of the content type is "text", the default charset is
     * included. If the default charset is undefined, but the
     * default locale is defined and it is not the US locale,
     * a locale specific charset is included.
     *
     * @return the content type or an empty string.
     */
    public String getContentType()
    {
        String ct = this.contentType;
        if (ct != null)
        {
            String charset = this.charSet;
            if (charset == null)
            {
                if (ct.startsWith("text/"))
                {
                    charset = getDefaultCharSet();
                    if (charset != null)
                    {
                        ct += "; charset=" + charset;
                    }
                }
            }
            else
            {
                ct += "; charset=" + charset;
            }
        }
        else
        {
            ct = "";
        }
        return ct;
    }

    /**
     * Sets the HTTP content type to return.
     *
     * @param ct a string.
     */
    public void setContentType(String ct)
    {
        this.contentType = ct;
    }

    /**
     * Gets the redirect URI. If this is set, also make sure to set
     * the status code to 302.
     *
     * @return a string, "" if null.
     */
    public String getRedirectURI()
    {
        return (this.redirectURI == null ? "" : redirectURI);
    }

    /**
     * Sets the redirect uri. If this is set, also make sure to set
     * the status code to 302.
     *
     * @param ruri a string.
     */
    public void setRedirectURI(String ruri)
    {
        this.redirectURI = ruri;
    }

    /**
     * Gets the HTTP status code to return.
     *
     * @return the status.
     */
    public int getStatusCode()
    {
        return statusCode;
    }

    /**
     * Sets the HTTP status code to return.
     *
     * @param sc the status.
     */
    public void setStatusCode(int sc)
    {
        this.statusCode = sc;
    }

    /**
     * Gets an array of system errors.
     *
     * @return a SystemError[].
     */
    public SystemError[] getSystemErrors()
    {
        SystemError[] result = new SystemError[errors.size()];
        errors.copyInto(result);
        return result;
    }

    /**
     * Adds a critical system error.
     *
     * @param err a system error.
     */
    public void setSystemError(SystemError err)
    {
        this.errors.addElement(err);
    }

    /**
     * Gets JNDI Contexts.
     *
     * @return a hashtable.
     */
    public Hashtable getJNDIContexts()
    {
        if (jndiContexts == null)
            jndiContexts = new Hashtable();
        return jndiContexts;
    }

    /**
     * Sets JNDI Contexts.
     *
     * @param contexts a hashtable.
     */
    public void setJNDIContexts(Hashtable contexts)
    {
        this.jndiContexts = contexts;
    }

    /**
     * Gets the cached server scheme.
     *
     * @return a string.
     */
    public String getServerScheme()
    {
        return getServerData().getServerScheme();
    }

    /**
     * Gets the cached server name.
     *
     * @return a string.
     */
    public String getServerName()
    {
        return getServerData().getServerName();
    }

    /**
     * Gets the cached server port.
     *
     * @return an int.
     */
    public int getServerPort()
    {
        return getServerData().getServerPort();
    }

    /**
     * Gets the cached context path.
     *
     * @return a string.
     */
    public String getContextPath()
    {
        return getServerData().getContextPath();
    }

    /**
     * Gets the cached script name.
     *
     * @return a string.
     */
    public String getScriptName()
    {
        return getServerData().getScriptName();
    }

    /**
     * Gets the server data ofy the request.
     *
     * @return server data.
     */
    public ServerData getServerData()
    {
        return this.serverData;
    }

    /**
     * Gets the IP address of the client that sent the request.
     *
     * @return a string.
     */
    public String getRemoteAddr()
    {
        if (this.remoteAddr == null)
        {
            this.remoteAddr = this.getRequest().getRemoteAddr();
        }

        return this.remoteAddr;
    }

    /**
     * Gets the qualified name of the client that sent the request.
     *
     * @return a string.
     */
    public String getRemoteHost()
    {
        if (this.remoteHost == null)
        {
            this.remoteHost = this.getRequest().getRemoteHost();
        }

        return this.remoteHost;
    }

    /**
     * Get the user agent for the request.
     *
     * @return a string.
     */
    public String getUserAgent()
    {
        if (this.userAgent == null)
        {
            this.userAgent = this.getRequest().getHeader("User-Agent");
        }

        return this.userAgent;
    }

    /**
     * Pulls a user object from the session and increments the access
     * counter and sets the last access date for the object.
     */
    public void populate()
    {
        user = getUserFromSession();

        if (user != null)
        {
            user.setLastAccessDate();
            user.incrementAccessCounter();
            user.incrementAccessCounterForSession();
        }
    }

    /**
     * Saves a user object into the session.
     */
    public void save()
    {
        session.putValue(User.SESSION_KEY, (Object) user);
    }

    /**
     * Gets the stack trace if set.
     *
     * @return the stack trace.
     */
    public String getStackTrace()
    {
        return stackTrace;
    }

    /**
     * Gets the stack trace exception if set.
     *
     * @return the stack exception.
     */
    public Throwable getStackTraceException()
    {
        return stackTraceException;
    }

    /**
     * Sets the stack trace.
     *
     * @param trace the stack trace.
     * @param exp the exception.
     */
    public void setStackTrace(String trace,
                              Throwable exp)
    {
        stackTrace = trace;
        stackTraceException = exp;
    }

    /**
     * Gets a table of debug variables.
     *
     * @return a hashtable for debug variables.
     */
    public Hashtable getVarDebug()
    {
        return varDebug;
    }

    // **********************************************
    // Implementation of the TurbineRunData interface
    // **********************************************

    /**
     * Gets the parameter parser without parsing the parameters.
     *
     * @return the parameter parser.
     */
    public ParameterParser getParameterParser()
    {
        return parameters;
    }

    /**
     * Sets the parameter parser.
     *
     * @param parser a parameter parser.
     */
    public void setParameterParser(ParameterParser parser)
    {
        parameters = parser;
    }

    /**
     * Gets the cookie parser without parsing the cookies.
     *
     * @return the cookie parser.
     */
    public CookieParser getCookieParser()
    {
        return cookies;
    }

    /**
     * Sets the cookie parser.
     *
     * @param parser a cookie parser.
     */
    public void setCookieParser(CookieParser parser)
    {
        cookies = parser;
    }

    /**
     * Sets the servlet request.
     *
     * @param req a request.
     */
    public void setRequest(HttpServletRequest req)
    {
        this.req = req;
    }

    /**
     * Sets the servlet response.
     *
     * @param res a response.
     */
    public void setResponse(HttpServletResponse res)
    {
        this.res = res;
    }

    /**
     * Sets the servlet session inforamtion.
     *
     * @param sess a session.
     */
    public void setSession(HttpSession sess)
    {
        this.session = sess;
    }

    /**
     * Setsthe servlet configuration used during servlet init.
     *
     * @param config a configuration.
     */
    public void setServletConfig(ServletConfig config)
    {
        this.config = config;
        if (config == null)
        {
            this.servletContext = null;
        }
        else
        {
            this.servletContext = config.getServletContext();
        }
    }

    /**
     * Sets the server data of the request.
     *
     * @param serverData server data.
     */
    public void setServerData(ServerData serverData)
    {
        this.serverData = serverData;
    }

    // ********************
    // Miscellanous setters
    // ********************

    /**
     * Sets the print writer.
     *
     * @param out a print writer.
     * @deprecated no replacement planned, response writer will not be cached
     */
    protected void setOut(PrintWriter out)
    {
        this.out = out;
    }

    /**
     * Sets the cached server scheme that is stored in the server data.
     *
     * @param ss a string.
     */
    protected void setServerScheme(String ss)
    {
        getServerData().setServerScheme(ss);
    }

    /**
     * Sets the cached server same that is stored in the server data.
     *
     * @param sn a string.
     */
    protected void setServerName(String sn)
    {
        getServerData().setServerName(sn);
    }

    /**
     * Sets the cached server port that is stored in the server data.
     *
     * @param port an int.
     */
    protected void setServerPort(int port)
    {
        getServerData().setServerPort(port);
    }

    /**
     * Sets the cached context path that is stored in the server data.
     *
     * @param cp a string.
     */
    protected void setContextPath(String cp)
    {
        getServerData().setContextPath(cp);
    }

    /**
     * Sets the cached script name that is stored in the server data.
     *
     * @param sn a string.
     */
    protected void setScriptName(String sn)
    {
        getServerData().setScriptName(sn);
    }
}
