package org.apache.turbine.services.rundata;

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

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.naming.Context;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.fulcrum.parser.CookieParser;
import org.apache.fulcrum.parser.ParameterParser;
import org.apache.fulcrum.security.acl.AccessControlList;
import org.apache.fulcrum.security.model.turbine.TurbineAccessControlList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.DefaultPipelineData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.util.FormMessages;
import org.apache.turbine.util.LocaleUtils;
import org.apache.turbine.util.ServerData;
import org.apache.turbine.util.SystemError;
import org.apache.turbine.util.template.TemplateInfo;

/**
 * DefaultTurbineRunData is the default implementation of the
 * TurbineRunData interface, which is distributed by the Turbine
 * RunData service, if another implementation is not defined in
 * the default or specified RunData configuration.
 * TurbineRunData is an extension to RunData, which
 * is an interface to run-time information that is passed
 * within Turbine. This provides the threading mechanism for the
 * entire system because multiple requests can potentially come in
 * at the same time.  Thus, there is only one RunData instance
 * for each request that is being serviced.
 *
 * <p>DefaultTurbineRunData implements the Recyclable interface making
 * it possible to pool its instances for recycling.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:bhoeneis@ee.ethz.ch">Bernie Hoeneisen</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class DefaultTurbineRunData
        extends DefaultPipelineData
        implements TurbineRunData
{
    /**
     * The disposed flag.
     */
    private boolean disposed;

    /** Cached action name to execute for this request. */
    private String action;

    /** This is the layout that the page will use to render the screen. */
    private String layout;

    /** Cached screen name to execute for this request. */
    private String screen;

    /** The character encoding of template files. */
    private String templateEncoding;

    /** This is what will build the <title></title> of the document. */
    private String title;

    /** Determines if there is information in the outputstream or not. */
    private boolean outSet;

    /**
     * Cache the output stream because it can be used in many
     * different places.
     */
    private PrintWriter out;

    /** The HTTP charset. */
    private Charset charSet;

    /** The HTTP content type to return. */
    private String contentType = TurbineConstants.DEFAULT_HTML_CONTENT_TYPE;

    /** If this is set, also set the status code to 302. */
    private String redirectURI;

    /** The HTTP status code to return. */
    private int statusCode = HttpServletResponse.SC_OK;

    /** This is a List to hold critical system errors. */
    private final List<SystemError> errors = new ArrayList<SystemError>();

    /** JNDI Contexts. */
    private Map<String, Context> jndiContexts;

    /** @see #getRemoteAddr() */
    private String remoteAddr;

    /** @see #getRemoteHost() */
    private String remoteHost;

    /** @see #getUserAgent() */
    private String userAgent;

    /** A holder for stack trace. */
    private String stackTrace;

    /** A holder for stack trace exception. */
    private Throwable stackTraceException;

    /**
     * Put things here and they will be shown on the default Error
     * screen.  This is great for debugging variable values when an
     * exception is thrown.
     */
    private final Map<String, Object> debugVariables = new HashMap<String, Object>();

    /** Logging */
    private static final Logger log = LogManager.getLogger(DefaultTurbineRunData.class);

    /**
     * Attempts to get the User object from the session.  If it does
     * not exist, it returns null.
     *
     * @param session An HttpSession.
     *
     * @param <T> a type extending {@link User}
     *
     * @return A User.
     */
    public static <T extends User> T getUserFromSession(HttpSession session)
    {
        try
        {
            @SuppressWarnings("unchecked")
            T user = (T) session.getAttribute(User.SESSION_KEY);
            return user;
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
            session.removeAttribute(User.SESSION_KEY);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    /**
     * Constructs a run data object.
     */
    public DefaultTurbineRunData()
    {
        super();

        // a map to hold information to be added to pipelineData
        put(Turbine.class, new HashMap<Class<?>, Object>());
        recycle();
    }

    /**
     * Recycles the object by removing its disposed flag.
     */
    @Override
    public void recycle()
    {
        disposed = false;
    }

    /**
     * Disposes a run data object.
     */
    @Override
    public void dispose()
    {
        // empty pipelinedata map
        get(Turbine.class).clear();

        action = null;
        layout = null;
        screen = null;
        templateEncoding = null;
        title = null;
        outSet = false;
        out = null;
        charSet = null;
        contentType = TurbineConstants.DEFAULT_HTML_CONTENT_TYPE;
        redirectURI = null;
        statusCode = HttpServletResponse.SC_OK;
        errors.clear();
        jndiContexts = null;
        remoteAddr = null;
        remoteHost = null;
        userAgent = null;
        stackTrace = null;
        stackTraceException = null;
        debugVariables.clear();
    }

    // ***************************************
    // Implementation of the RunData interface
    // ***************************************

    /**
     * Gets the parameters.
     *
     * @return a parameter parser.
     */
    @Override
    public ParameterParser getParameters()
    {
        // Parse the parameters first, if not yet done.
        ParameterParser parameters = getParameterParser();
        HttpServletRequest request = getRequest();

        if (parameters != null && parameters.getRequest() != request)
        {
            parameters.setRequest(request);
        }

        return parameters;
    }

    /**
     * Gets the cookies.
     *
     * @return a cookie parser.
     */
    @Override
    public CookieParser getCookies()
    {
        // Parse the cookies first, if not yet done.
        CookieParser cookies = getCookieParser();
        HttpServletRequest request = getRequest();

        if (cookies != null && cookies.getRequest() != request)
        {
            cookies.setData(request, getResponse());
        }

        return cookies;
    }

    /**
     * Gets the servlet request.
     *
     * @return the request.
     */
    @Override
    public HttpServletRequest getRequest()
    {
        return get(Turbine.class, HttpServletRequest.class);
    }

    /**
     * Gets the servlet response.
     *
     * @return the response.
     */
    @Override
    public HttpServletResponse getResponse()
    {
        return get(Turbine.class, HttpServletResponse.class);
    }

    /**
     * Gets the servlet session information.
     *
     * @return the session.
     */
    @Override
    public HttpSession getSession()
    {
        return getRequest().getSession();
    }

    /**
     * Gets the servlet configuration used during servlet init.
     *
     * @return the configuration.
     */
    @Override
    public ServletConfig getServletConfig()
    {
        return get(Turbine.class, ServletConfig.class);
    }

    /**
     * Gets the servlet context used during servlet init.
     *
     * @return the context.
     */
    @Override
    public ServletContext getServletContext()
    {
        return get(Turbine.class, ServletContext.class);
    }

    /**
     * Gets the access control list.
     *
     * @return the access control list.
     */
    @Override
    public <A extends AccessControlList> A getACL()
    {
        @SuppressWarnings("unchecked")
        A acl = (A)get(Turbine.class, TurbineAccessControlList.class);
        return acl;
    }

    /**
     * Sets the access control list.
     *
     * To delete ACL from session use key {@link TurbineConstants#ACL_SESSION_KEY}.
     * Invalidate session, if session persist.
     *
     * @param acl an access control list.
     */
    @Override
    public void setACL(AccessControlList acl)
    {
        get(Turbine.class).put(TurbineAccessControlList.class, acl);
    }

    /**
     * Whether or not an action has been defined.
     *
     * @return true if an action has been defined.
     */
    @Override
    public boolean hasAction()
    {
        return StringUtils.isNotEmpty(this.action)
          && !this.action.equalsIgnoreCase("null");
    }

    /**
     * Gets the action. It returns an empty string if null so
     * that it is easy to do conditionals on it based on the
     * equalsIgnoreCase() method.
     *
     * @return a string, "" if null.
     */
    @Override
    public String getAction()
    {
        return hasAction() ? this.action : "";
    }

    /**
     * Sets the action for the request.
     *
     * @param action a string.
     */
    @Override
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
     * where they could dynamically change the layout for the entire
     * site.
     *
     * @return a string.
     */

    @Override
    public String getLayout()
    {
        if (this.layout == null)
        {
            /*
             * This will return something if the template
             * services are running. If we get nothing we
             * will fall back to the ECS layout.
             */
            TemplateService templateService = (TemplateService)TurbineServices.getInstance().getService(TemplateService.SERVICE_NAME);
            layout = templateService.getDefaultLayoutName(this);

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
    @Override
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
    @Override
    public String getLayoutTemplate()
    {
        return getTemplateInfo().getLayoutTemplate();
    }

    /**
     * Modifies the layout template for the screen. This convenience
     * method allows for a layout to be modified from within a
     * template. For example;
     *
     *    $data.setLayoutTemplate("NewLayout.vm")
     *
     * @param layout a layout template.
     */
    @Override
    public void setLayoutTemplate(String layout)
    {
        getTemplateInfo().setLayoutTemplate(layout);
    }

    /**
     * Whether or not a screen has been defined.
     *
     * @return true if a screen has been defined.
     */
    @Override
    public boolean hasScreen()
    {
        return StringUtils.isNotEmpty(this.screen);
    }

    /**
     * Gets the screen to execute.
     *
     * @return a string.
     */
    @Override
    public String getScreen()
    {
        return hasScreen() ? this.screen : "";
    }

    /**
     * Sets the screen for the request.
     *
     * @param screen a string.
     */
    @Override
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
    @Override
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
    @Override
    public void setScreenTemplate(String screen)
    {
        getTemplateInfo().setScreenTemplate(screen);
    }

    /**
     * Gets the character encoding to use for reading template files.
     *
     * @return the template encoding or null if not specified.
     */
    @Override
    public String getTemplateEncoding()
    {
        return templateEncoding;
    }

    /**
     * Sets the character encoding to use for reading template files.
     *
     * @param encoding the template encoding.
     */
    @Override
    public void setTemplateEncoding(String encoding)
    {
        templateEncoding = encoding;
    }

    /**
     * Gets the template info. Creates a new one if needed.
     *
     * @return a template info.
     */
    @Override
    public TemplateInfo getTemplateInfo()
    {
        TemplateInfo templateInfo = get(Turbine.class, TemplateInfo.class);

        if (templateInfo == null)
        {
            templateInfo = new TemplateInfo(this);
            get(Turbine.class).put(TemplateInfo.class, templateInfo);
        }

        return templateInfo;
    }

    /**
     * Whether or not a message has been defined.
     *
     * @return true if a message has been defined.
     */
    @Override
    public boolean hasMessage()
    {
        StringBuilder message = get(Turbine.class, StringBuilder.class);
        return message != null && message.length() > 0;
    }

    /**
     * Gets the results of an action or another message
     * to be displayed as a string.
     *
     * @return a string.
     */
    @Override
    public String getMessage()
    {
        StringBuilder message = get(Turbine.class, StringBuilder.class);
        return message == null ? null : message.toString();
    }

    /**
     * Sets the message for the request as a string.
     *
     * @param msg a string.
     */
    @Override
    public void setMessage(String msg)
    {
        get(Turbine.class).put(StringBuilder.class, new StringBuilder(msg));
    }

    /**
     * Adds the string to message. If message has prior messages from
     * other actions or screens, this method can be used to chain them.
     *
     * @param msg a string.
     */
    @Override
    public void addMessage(String msg)
    {
        StringBuilder message = get(Turbine.class, StringBuilder.class);
        if (message == null)
        {
            setMessage(msg);
        }
        else
        {
            message.append(msg);
        }
    }

    /**
     * Gets the results of an action or another message
     * to be displayed as a string (never null).
     *
     * @return a string element.
     */
    @Override
    public String getMessageAsHTML()
    {
        String message = getMessage();
        return message == null ? "" : message;
    }

    /**
     * Unsets the message for the request.
     */
    @Override
    public void unsetMessage()
    {
        get(Turbine.class).remove(StringBuilder.class);
    }

    /**
     * Gets a FormMessages object where all the messages to the
     * user should be stored.
     *
     * @return a FormMessages.
     */
    @Override
    public FormMessages getMessages()
    {
        FormMessages messages = get(Turbine.class, FormMessages.class);
        if (messages == null)
        {
            messages = new FormMessages();
            setMessages(messages);
        }

        return messages;
    }

    /**
     * Sets the FormMessages object for the request.
     *
     * @param msgs A FormMessages.
     */
    @Override
    public void setMessages(FormMessages msgs)
    {
        get(Turbine.class).put(FormMessages.class, msgs);
    }

    /**
     * Gets the title of the page.
     *
     * @return a string.
     */
    @Override
    public String getTitle()
    {
        return this.title == null ? "" : this.title;
    }

    /**
     * Sets the title of the page.
     *
     * @param title a string.
     */
    @Override
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Checks if a user exists in this session.
     *
     * @return true if a user exists in this session.
     */
    @Override
    public boolean userExists()
    {
        User user = getUserFromSession();

        // TODO: Check if this side effect is reasonable
        get(Turbine.class).put(User.class, user);

        return (user != null);
    }

    /**
     * Gets the user.
     *
     * @param <T> a type extending {@link User}
     *
     * @return a user.
     */
    @Override
    public <T extends User> T getUser()
    {
        @SuppressWarnings("unchecked")
        T user = (T)get(Turbine.class, User.class);
        return user;
    }

    /**
     * Sets the user.
     *
     * @param user a user.
     */
    @Override
    public void setUser(User user)
    {
        log.debug("user set: {}", user::getName);
        get(Turbine.class).put(User.class, user);
    }

    /**
     * Attempts to get the user from the session. If it does
     * not exist, it returns null.
     *
     * @return a user.
     */
    @Override
    public <T extends User> T getUserFromSession()
    {
        return getUserFromSession(getSession());
    }

    /**
     * Allows one to invalidate the user in the default session.
     *
     * @return true if user was invalidated.
     */
    @Override
    public boolean removeUserFromSession()
    {
        return removeUserFromSession(getSession());
    }

    /**
     * Checks to see if out is set.
     *
     * @return true if out is set.
     * @deprecated no replacement planned, response writer will not be cached
     */
    @Override
    @Deprecated
    public boolean isOutSet()
    {
        return outSet;
    }

    /**
     * Gets the print writer. First time calling this
     * will set the print writer via the response.
     *
     * @return a print writer.
     * @throws IOException on failure getting the PrintWriter
     */
    @Override
    public PrintWriter getOut()
            throws IOException
    {
        // Check to see if null first.
        if (this.out == null)
        {
            setOut(getResponse().getWriter());
        }
        outSet = true;
        return this.out;
    }

    /**
     * Declares that output will be direct to the response stream,
     * even though getOut() may never be called.  Useful for response
     * mechanisms that may call res.getWriter() themselves
     * (such as JSP.)
     */
    @Override
    public void declareDirectResponse()
    {
        outSet = true;
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
    @Override
    public Locale getLocale()
    {
        Locale locale = get(Turbine.class, Locale.class);
        if (locale == null)
        {
            locale = LocaleUtils.getDefaultLocale();
        }
        return locale;
    }

    /**
     * Sets the locale.
     *
     * @param locale the new locale.
     */
    @Override
    public void setLocale(Locale locale)
    {
        get(Turbine.class).put(Locale.class, locale);

        // propagate the locale to the parsers
        ParameterParser parameters = get(Turbine.class, ParameterParser.class);
        CookieParser cookies = get(Turbine.class, CookieParser.class);

        if (parameters != null)
        {
            parameters.setLocale(locale);
        }

        if (cookies != null)
        {
            cookies.setLocale(locale);
        }
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
    @Override
    public String getCharSet()
    {
        return getCharset().name();
    }

    /**
     * Sets the charset.
     *
     * @param charSet the name of the new charset.
     */
    @Override
    public void setCharSet(String charSet)
    {
        setCharset(Charset.forName(charSet));
    }

    /**
     * Gets the charset. If it has not already been defined with
     * setCharSet(), then a property named "locale.default.charset"
     * is checked from the Resource Service and returned. If this
     * property is undefined, the default charset of the locale
     * is returned. If the locale is undefined, null is returned.
     *
     * @return the charset or null.
     */
    @Override
    public Charset getCharset()
    {
        log.debug("getCharset()");

        if (charSet == null)
        {
            log.debug("Charset was null!");
            charSet =  LocaleUtils.getDefaultCharset();
        }

        return charSet;
    }

    /**
     * Sets the charset.
     *
     * @param charSet the new charset.
     */
    @Override
    public void setCharset(Charset charSet)
    {
        log.debug("setCharset({})", charSet);
        this.charSet = charSet;
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
    @Override
    public String getContentType()
    {
        if (StringUtils.isNotEmpty(contentType))
        {
            if (charSet == null)
            {
                if (contentType.startsWith("text/"))
                {
                    return contentType + "; charset=" + LocaleUtils.getDefaultCharset();
                }

                return contentType;
            }
            else
            {
                return contentType + "; charset=" + charSet.name();
            }
        }

        return "";
    }

    /**
     * Sets the HTTP content type to return.
     *
     * @param contentType a string.
     */
    @Override
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    /**
     * Gets the redirect URI. If this is set, also make sure to set
     * the status code to 302.
     *
     * @return a string, "" if null.
     */
    @Override
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
    @Override
    public void setRedirectURI(String ruri)
    {
        this.redirectURI = ruri;
    }

    /**
     * Gets the HTTP status code to return.
     *
     * @return the status.
     */
    @Override
    public int getStatusCode()
    {
        return statusCode;
    }

    /**
     * Sets the HTTP status code to return.
     *
     * @param statusCode the status.
     */
    @Override
    public void setStatusCode(int statusCode)
    {
        this.statusCode = statusCode;
    }

    /**
     * Gets an array of system errors.
     *
     * @return a SystemError[].
     */
    @Override
    public SystemError[] getSystemErrors()
    {
        SystemError[] result = new SystemError[errors.size()];
        errors.toArray(result);
        return result;
    }

    /**
     * Adds a critical system error.
     *
     * @param err a system error.
     */
    @Override
    public void setSystemError(SystemError err)
    {
        this.errors.add(err);
    }

    /**
     * Gets JNDI Contexts.
     *
     * @return a hashmap.
     */
    @Override
    public Map<String, Context> getJNDIContexts()
    {
        if (jndiContexts == null)
        {
            jndiContexts = new HashMap<String, Context>();
        }
        return jndiContexts;
    }

    /**
     * Sets JNDI Contexts.
     *
     * @param contexts a hashmap.
     */
    @Override
    public void setJNDIContexts(Map<String, Context> contexts)
    {
        this.jndiContexts = contexts;
    }

    /**
     * Gets the cached server scheme.
     *
     * @return a string.
     */
    @Override
    public String getServerScheme()
    {
        return getServerData().getServerScheme();
    }

    /**
     * Gets the cached server name.
     *
     * @return a string.
     */
    @Override
    public String getServerName()
    {
        return getServerData().getServerName();
    }

    /**
     * Gets the cached server port.
     *
     * @return an int.
     */
    @Override
    public int getServerPort()
    {
        return getServerData().getServerPort();
    }

    /**
     * Gets the cached context path.
     *
     * @return a string.
     */
    @Override
    public String getContextPath()
    {
        return getServerData().getContextPath();
    }

    /**
     * Gets the cached script name.
     *
     * @return a string.
     */
    @Override
    public String getScriptName()
    {
        return getServerData().getScriptName();
    }

    /**
     * Gets the server data ofy the request.
     *
     * @return server data.
     */
    @Override
    public ServerData getServerData()
    {
        return get(Turbine.class, ServerData.class);
    }

    /**
     * Gets the IP address of the client that sent the request.
     *
     * @return a string.
     */
    @Override
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
    @Override
    public String getRemoteHost()
    {
        if (this.remoteHost == null)
        {
            this.remoteHost = this.getRequest().getRemoteHost();
        }

        return this.remoteHost;
    }

    /**
     * Get the user agent for the request. The semantics here
     * are muddled because RunData caches the value after the
     * first invocation. This is different e.g. from getCharSet().
     *
     * @return a string.
     */
    @Override
    public String getUserAgent()
    {
        if (StringUtils.isEmpty(userAgent))
        {
            userAgent = this.getRequest().getHeader("User-Agent");
        }

        return userAgent;
    }

    /**
     * Pulls a user object from the session and increments the access
     * counter and sets the last access date for the object.
     */
    @Override
    public void populate()
    {
        User user = getUserFromSession();
        get(Turbine.class).put(User.class, user);

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
    @Override
    public void save()
    {
        getSession().setAttribute(User.SESSION_KEY, getUser());
    }

    /**
     * Gets the stack trace if set.
     *
     * @return the stack trace.
     */
    @Override
    public String getStackTrace()
    {
        return stackTrace;
    }

    /**
     * Gets the stack trace exception if set.
     *
     * @return the stack exception.
     */
    @Override
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
    @Override
    public void setStackTrace(String trace, Throwable exp)
    {
        stackTrace = trace;
        stackTraceException = exp;
    }

    /**
     * Sets a name/value pair in an internal Map that is accessible from the
     * Error screen.  This is a good way to get debugging information
     * when an exception is thrown.
     *
     * @param name name of the variable
     * @param value value of the variable.
     */
    @Override
    public void setDebugVariable(String name, Object value)
    {
        this.debugVariables.put(name, value);
    }

    /**
     * Gets a Map of debug variables.
     *
     * @return a Map of debug variables.
     */
    @Override
    public Map<String, Object> getDebugVariables()
    {
        return this.debugVariables;
    }

    // **********************************************
    // Implementation of the TurbineRunData interface
    // **********************************************

    /**
     * Gets the parameter parser without parsing the parameters.
     *
     * @return the parameter parser.
     * TODO Does this method make sense? Pulling the parameter out of
     *       the run data object before setting a request (which happens
     *       only in getParameters() leads to the Parameter parser having
     *       no object and thus the default or even an undefined encoding
     *       instead of the actual request character encoding).
     */
    @Override
    public ParameterParser getParameterParser()
    {
        return get(Turbine.class, ParameterParser.class);
    }

    /**
     * Gets the cookie parser without parsing the cookies.
     *
     * @return the cookie parser.
     */
    @Override
    public CookieParser getCookieParser()
    {
        return get(Turbine.class, CookieParser.class);
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
    @Deprecated
    protected void setOut(PrintWriter out)
    {
        this.out = out;
    }

    /**
     * Sets the cached server scheme that is stored in the server data.
     *
     * @param serverScheme a string.
     */
    protected void setServerScheme(String serverScheme)
    {
        getServerData().setServerScheme(serverScheme);
    }

    /**
     * Sets the cached server same that is stored in the server data.
     *
     * @param serverName a string.
     */
    protected void setServerName(String serverName)
    {
        getServerData().setServerName(serverName);
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
     * @param contextPath a string.
     */
    protected void setContextPath(String contextPath)
    {
        getServerData().setContextPath(contextPath);
    }

    /**
     * Sets the cached script name that is stored in the server data.
     *
     * @param scriptName a string.
     */
    protected void setScriptName(String scriptName)
    {
        getServerData().setScriptName(scriptName);
    }

    /**
     * Checks whether the object is disposed.
     *
     * @return true, if the object is disposed.
     */
    @Override
    public boolean isDisposed()
    {
        return disposed;
    }

}
