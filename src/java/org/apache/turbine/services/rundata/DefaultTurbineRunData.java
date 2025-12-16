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
import org.apache.turbine.util.LocaleUtils;
import org.apache.turbine.util.SystemError;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
    private boolean disposed = false;

    /** Cached action name to execute for this request. */
    private String action;

    /** This is the layout that the page will use to render the screen. */
    private String layout;

    /** Cached screen name to execute for this request. */
    private String screen;

    /** The character encoding of template files. */
    private Charset templateEncoding;

    /** This is what will build the title of the document. */
    private String title;

    /** Determines if there is information in the outputstream or not. */
    private boolean outSet;

    /**
     * Cache the output stream because it can be used in many
     * different places.
     */
    private PrintWriter out;

    /** The HTTP content type to return. */
    private String contentType = TurbineConstants.DEFAULT_HTML_CONTENT_TYPE;

    /** If this is set, also set the status code to 302. */
    private String redirectURI;

    /** The HTTP status code to return. */
    private int statusCode = HttpServletResponse.SC_OK;

    /** This is a List to hold critical system errors. */
    private final List<SystemError> errors = new ArrayList<>();

    /** JNDI Contexts. */
    private Map<String, Context> jndiContexts;

    /** A holder for stack trace. */
    private String stackTrace;

    /** A holder for stack trace exception. */
    private Throwable stackTraceException;

    /**
     * Put things here and they will be shown on the default Error
     * screen.  This is great for debugging variable values when an
     * exception is thrown.
     */
    private final Map<String, Object> debugVariables = new HashMap<>();

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
    @SuppressWarnings("unchecked")
    public static <T extends User> T getUserFromSession(HttpSession session)
    {
        try
        {
            return (T) session.getAttribute(User.SESSION_KEY);
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
        templateEncoding = null; // FIXME This is never set
        title = null;
        outSet = false;
        out = null;
        contentType = TurbineConstants.DEFAULT_HTML_CONTENT_TYPE;
        redirectURI = null;
        statusCode = HttpServletResponse.SC_OK;
        errors.clear();
        jndiContexts = null;
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
     * Gets the access control list.
     *
     * @return the access control list.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <A extends AccessControlList> A getACL()
    {
        return (A)get(Turbine.class, TurbineAccessControlList.class);
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
     * Gets the character encoding to use for reading template files.
     *
     * @return the template encoding or null if not specified.
     */
    @Override
    public Charset getTemplateCharset()
    {
        return templateEncoding;
    }

    /**
     * Sets the character encoding to use for reading template files.
     *
     * @param encoding the template encoding.
     */
    @Override
    public void setTemplateCharset(Charset encoding)
    {
        templateEncoding = encoding;
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
        return computeIfAbsent(Turbine.class, Locale.class, k -> LocaleUtils.getDefaultLocale());
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
        ParameterParser parameters = getParameterParser();
        CookieParser cookies = getCookieParser();

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
     * @return the charset or null.
     */
    @Override
    public Charset getCharset()
    {
        return computeIfAbsent(Turbine.class, Charset.class, k -> LocaleUtils.getDefaultCharset());
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
        get(Turbine.class).put(Charset.class, charSet);
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
            if (contentType.startsWith("text/"))
            {
                Charset charSet = getCharset();
                return contentType + "; charset=" + charSet.name();
            }

            return contentType;
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
        return errors.toArray(new SystemError[0]);
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
            jndiContexts = new HashMap<>();
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

    // ********************
    // Miscellaneous setters
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
