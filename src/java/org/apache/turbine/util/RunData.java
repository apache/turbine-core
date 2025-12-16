package org.apache.turbine.util;


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
import java.util.Locale;
import java.util.Map;

import javax.naming.Context;

import org.apache.fulcrum.parser.CookieParser;
import org.apache.fulcrum.parser.ParameterParser;
import org.apache.fulcrum.security.acl.AccessControlList;
import org.apache.turbine.Turbine;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.template.TemplateInfo;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * RunData is an interface to run-time information that is passed
 * within Turbine. This provides the threading mechanism for the
 * entire system because multiple requests can potentially come in
 * at the same time.  Thus, there is only one RunData instance
 * for each request that is being serviced.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:bhoeneis@ee.ethz.ch">Bernie Hoeneisen</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public interface RunData extends PipelineData
{
    /**
     * Gets the parameters.
     *
     * @return a parameter parser.
     */
    ParameterParser getParameters();

    /**
     * Gets the cookies.
     *
     * @return a cookie parser.
     */
    CookieParser getCookies();

    /**
     * Gets the servlet request.
     *
     * @return the request.
     */
    default HttpServletRequest getRequest()
    {
        return get(Turbine.class, HttpServletRequest.class);
    }

    /**
     * Gets the servlet response.
     *
     * @return the resposne.
     */
    default HttpServletResponse getResponse()
    {
        return get(Turbine.class, HttpServletResponse.class);
    }

    /**
     * Gets the servlet session information.
     *
     * @return the session.
     */
    default HttpSession getSession()
    {
        return getRequest().getSession();
    }

    /**
     * Gets the servlet configuration used during servlet init.
     *
     * @return the configuration.
     */
    default ServletConfig getServletConfig()
    {
        return get(Turbine.class, ServletConfig.class);
    }

    /**
     * Gets the servlet context used during servlet init.
     *
     * @return the context.
     */
    default ServletContext getServletContext()
    {
        return get(Turbine.class, ServletContext.class);
    }

    /**
     * Gets the access control list.
     *
     * @return the access control list.
     *
     * @param <A> a type extending {@link AccessControlList}
     */
    <A extends AccessControlList> A getACL();

    /**
     * Sets the access control list.
     *
     * @param <A> ACL type
     * @param acl an access control list.
     */
    <A extends AccessControlList> void setACL(A acl);

    /**
     * Whether or not an action has been defined.
     *
     * @return true if an action has been defined.
     */
    boolean hasAction();

    /**
     * Gets the action. It returns an empty string if null so
     * that it is easy to do conditionals on it based on the
     * equalsIgnoreCase() method.
     *
     * @return a string, "" if null.
     */
    String getAction();

    /**
     * Sets the action for the request.
     *
     * @param action a string.
     */
    void setAction(String action);

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
    String getLayout();

    /**
     * Set the layout for the request.
     *
     * @param layout a string.
     */
    void setLayout(String layout);

    /**
     * Convenience method for a template info that
     * returns the layout template being used.
     *
     * @return a string.
     */
    default String getLayoutTemplate()
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
    default void setLayoutTemplate(String layout)
    {
        getTemplateInfo().setLayoutTemplate(layout);
    }

    /**
     * Whether or not a screen has been defined.
     *
     * @return true if a screen has been defined.
     */
    boolean hasScreen();

    /**
     * Gets the screen to execute.
     *
     * @return a string.
     */
    String getScreen();

    /**
     * Sets the screen for the request.
     *
     * @param screen a string.
     */
    void setScreen(String screen);

    /**
     * Convenience method for a template info that
     * returns the name of the template being used.
     *
     * @return a string.
     */
    default String getScreenTemplate()
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
    default void setScreenTemplate(String screen)
    {
        getTemplateInfo().setScreenTemplate(screen);
    }

    /**
     * Gets the character encoding to use for reading template files.
     *
     * @return the template encoding or null if not specified.
     */
    Charset getTemplateCharset();

    /**
     * Sets the character encoding to use for reading template files.
     *
     * @param encoding the template encoding.
     */
    void setTemplateCharset(Charset encoding);

    /**
     * Gets the character encoding to use for reading template files.
     *
     * @return the template encoding or null if not specified.
     */
    @Deprecated
    default String getTemplateEncoding()
    {
        return getTemplateCharset().name();
    }

    /**
     * Sets the character encoding to use for reading template files.
     *
     * @param encoding the template encoding.
     */
    @Deprecated
    default void setTemplateEncoding(String encoding)
    {
        setTemplateCharset(Charset.forName(encoding));
    }

    /**
     * Gets the template info. Creates a new one if needed.
     *
     * @return a template info.
     */
    default TemplateInfo getTemplateInfo()
    {
        return computeIfAbsent(Turbine.class, TemplateInfo.class,
                k -> new TemplateInfo(this));
    }

    /**
     * Whether or not a message has been defined.
     *
     * @return true if a message has been defined.
     */
    boolean hasMessage();

    /**
     * Gets the results of an action or another message
     * to be displayed as a string.
     *
     * @return a string.
     */
    String getMessage();

    /**
     * Sets the message for the request as a string.
     *
     * @param msg a string.
     */
    void setMessage(String msg);

    /**
     * Adds the string to message. If message has prior messages from
     * other actions or screens, this method can be used to chain them.
     *
     * @param msg a string.
     */
    void addMessage(String msg);

    /**
     * Gets the results of an action or another message
     * to be displayed as a string.
     *
     * @return a string.
     */
    String getMessageAsHTML();

    /**
     * Unsets the message for the request.
     */
    void unsetMessage();

    /**
     * Gets a FormMessages object where all the messages to the
     * user should be stored.
     *
     * @return a FormMessages.
     */
    default FormMessages getMessages()
    {
        return computeIfAbsent(Turbine.class, FormMessages.class, k -> new FormMessages());
    }

    /**
     * Sets the FormMessages object for the request.
     *
     * @param msgs A FormMessages.
     */
    default void setMessages(FormMessages msgs)
    {
        get(Turbine.class).put(FormMessages.class, msgs);
    }

    /**
     * Gets the title of the page.
     *
     * @return a string.
     */
    String getTitle();

    /**
     * Sets the title of the page.
     *
     * @param title a string.
     */
    void setTitle(String title);

    /**
     * Checks if a user exists in this session.
     *
     * @return true if a user exists in this session.
     */
    boolean userExists();

    /**
     * Gets the user.
     *
     * @param <T> a type extending {@link User}
     *
     * @return a user.
     */
    @SuppressWarnings("unchecked")
    default <T extends User> T getUser()
    {
        return (T)get(Turbine.class, User.class);
    }

    /**
     * Sets the user.
     *
     * @param user a user.
     *
     * @param <T> a type extending {@link User}
     */
    default <T extends User> void setUser(T user)
    {
        get(Turbine.class).put(User.class, user);
    }

    /**
     * Attempts to get the user from the session. If it does
     * not exist, it returns null.
     *
     * @return a user.
     *
     * @param <T> a type extending {@link User}
     */
    <T extends User> T getUserFromSession();

    /**
     * Allows one to invalidate the user in the default session.
     *
     * @return true if user was invalidated.
     */
    boolean removeUserFromSession();

    /**
     * Checks to see if out is set.
     *
     * @return true if out is set.
     * @deprecated no replacement planned, response writer will not be cached
     */
    @Deprecated
    boolean isOutSet();

    /**
     * Gets the print writer. First time calling this
     * will set the print writer via the response.
     *
     * @return a print writer.
     * @throws IOException on failure getting the PrintWriter
     */
    PrintWriter getOut()
            throws IOException;

    /**
     * Declares that output will be direct to the response stream,
     * even though getOut() may never be called.  Useful for response
     * mechanisms that may call res.getWriter() themselves
     * (such as JSP.)
     */
    void declareDirectResponse();

    /**
     * Gets the locale. If it has not already been defined with
     * setLocale(), then  properties named "locale.default.lang"
     * and "locale.default.country" are checked from the Resource
     * Service and the corresponding locale is returned. If these
     * properties are undefined, JVM's default locale is returned.
     *
     * @return the locale.
     */
    Locale getLocale();

    /**
     * Sets the locale.
     *
     * @param locale the new locale.
     */
    void setLocale(Locale locale);

    /**
     * Gets the charset. If it has not already been defined with
     * setCharSet(), then a property named "locale.default.charset"
     * is checked from the Resource Service and returned. If this
     * property is undefined, the default charset of the locale
     * is returned. If the locale is undefined, null is returned.
     *
     * @return the name of the charset or null.
     */
    @Deprecated
    default String getCharSet()
    {
        return getCharset().name();
    }

    /**
     * Sets the charset.
     *
     * @param charset the name of the new charset.
     */
    @Deprecated
    default void setCharSet(String charset)
    {
        setCharset(Charset.forName(charset));
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
    Charset getCharset();

    /**
     * Sets the charset.
     *
     * @param charset the new charset.
     */
    void setCharset(Charset charset);

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
    String getContentType();

    /**
     * Sets the HTTP content type to return.
     *
     * @param ct the new content type.
     */
    void setContentType(String ct);

    /**
     * Gets the redirect URI. If this is set, also make sure to set
     * the status code to 302.
     *
     * @return a string, "" if null.
     */
    String getRedirectURI();

    /**
     * Sets the redirect uri. If this is set, also make sure to set
     * the status code to 302.
     *
     * @param ruri a string.
     */
    void setRedirectURI(String ruri);

    /**
     * Gets the HTTP status code to return.
     *
     * @return the status.
     */
    int getStatusCode();

    /**
     * Sets the HTTP status code to return.
     *
     * @param sc the status.
     */
    void setStatusCode(int sc);

    /**
     * Gets an array of system errors.
     *
     * @return a SystemError[].
     */
    SystemError[] getSystemErrors();

    /**
     * Adds a critical system error.
     *
     * @param err a system error.
     */
    void setSystemError(SystemError err);

    /**
     * Gets JNDI Contexts.
     *
     * @return a hashtable.
     */
    Map<String, Context> getJNDIContexts();

    /**
     * Sets JNDI Contexts.
     *
     * @param contexts a hashtable.
     */
    void setJNDIContexts(Map<String, Context> contexts);

    /**
     * Gets the cached server scheme.
     *
     * @return a string.
     */
    default String getServerScheme()
    {
        return getServerData().getServerScheme();
    }

    /**
     * Gets the cached server name.
     *
     * @return a string.
     */
    default String getServerName()
    {
        return getServerData().getServerName();
    }

    /**
     * Gets the cached server port.
     *
     * @return an int.
     */
    default int getServerPort()
    {
        return getServerData().getServerPort();
    }

    /**
     * Gets the cached context path.
     *
     * @return a string.
     */
    default String getContextPath()
    {
        return getServerData().getContextPath();
    }

    /**
     * Gets the cached script name.
     *
     * @return a string.
     */
    default String getScriptName()
    {
        return getServerData().getScriptName();
    }

    /**
     * Gets the server data used by the request.
     *
     * @return server data.
     */
    default ServerData getServerData()
    {
        return get(Turbine.class, ServerData.class);
    }

    /**
     * Gets the IP address of the client that sent the request.
     *
     * @return a string.
     */
    default String getRemoteAddr()
    {
        return getRequest().getRemoteAddr();
    }

    /**
     * Gets the qualified name of the client that sent the request.
     *
     * @return a string.
     */
    default String getRemoteHost()
    {
        return getRequest().getRemoteHost();
    }

    /**
     * Get the user agent for the request.
     *
     * @return a string.
     */
    default String getUserAgent()
    {
        return getRequest().getHeader("User-Agent");
    }

    /**
     * Pulls a user object from the session and increments the access
     * counter and sets the last access date for the object.
     */
    void populate();

    /**
     * Saves a user object into the session.
     */
    void save();

    /**
     * Gets the stack trace if set.
     *
     * @return the stack trace.
     */
    String getStackTrace();

    /**
     * Gets the stack trace exception if set.
     *
     * @return the stack exception.
     */
    Throwable getStackTraceException();

    /**
     * Sets the stack trace.
     *
     * @param trace the stack trace.
     * @param exp the exception.
     */
    void setStackTrace(String trace,
                       Throwable exp);

    /**
     * Sets a name/value pair in an internal Map that is accessible from the
     * Error screen.  This is a good way to get debugging information
     * when an exception is thrown.
     *
     * @param name name of the variable
     * @param value value of the variable.
     */
    void setDebugVariable(String name, Object value);

    /**
     * Gets a Map of debug variables.
     *
     * @return a Map of debug variables.
     */
    Map<String, Object> getDebugVariables();
}
