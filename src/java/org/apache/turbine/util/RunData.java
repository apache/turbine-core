package org.apache.turbine.util;

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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ecs.Document;
import org.apache.ecs.Element;
import org.apache.ecs.StringElement;

import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.parser.CookieParser;
import org.apache.turbine.util.parser.ParameterParser;
import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.util.template.TemplateInfo;

/**
 * RunData is an interface to run-time information that is passed
 * within Turbine. This provides the threading mechanism for the
 * entire system because multiple requests can potentially come in
 * at the same time.  Thus, there is only one RunData implementation
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
    HttpServletRequest getRequest();

    /**
     * Gets the servlet response.
     *
     * @return the resposne.
     */
    HttpServletResponse getResponse();

    /**
     * Gets the servlet session information.
     *
     * @return the session.
     */
    HttpSession getSession();

    /**
     * Gets the servlet configuration used during servlet init.
     *
     * @return the configuration.
     */
    ServletConfig getServletConfig();

    /**
     * Gets the servlet context used during servlet init.
     *
     * @return the context.
     */
    ServletContext getServletContext();

    /**
     * Gets the access control list.
     *
     * @return the access control list.
     */
    AccessControlList getACL();

    /**
     * Sets the access control list.
     *
     * @param acl an access control list.
     */
    void setACL(AccessControlList acl);

    /**
     * Checks to see if the page is set.
     *
     * @return true if the page is set.
     * @deprecated no replacement planned, ECS is no longer a requirement
     */
    boolean isPageSet();

    /**
     * Gets the page.
     *
     * @return a document.
     * @deprecated no replacement planned, ECS is no longer a requirement
     */
    Document getPage();

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
     * @param action a atring.
     */
    void setAction(String action);

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
    String getLayoutTemplate();

    /**
     * Modifies the layout template for the screen. This convenience
     * method allows for a layout to be modified from within a
     * template. For example;
     *
     *    $data.setLayoutTemplate("NewLayout.vm")
     *
     * @param layout a layout template.
     */
    void setLayoutTemplate(String layout);

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
    String getScreenTemplate();

    /**
     * Sets the screen template for the request. For
     * example;
     *
     *    $data.setScreenTemplate("NewScreen.vm")
     *
     * @param screen a screen template.
     */
    void setScreenTemplate(String screen);

    /**
     * Gets the character encoding to use for reading template files.
     *
     * @return the template encoding or null if not specified.
     */
    String getTemplateEncoding();

    /**
     * Sets the character encoding to use for reading template files.
     *
     * @param encoding the template encoding.
     */
    void setTemplateEncoding(String encoding);

    /**
     * Gets the template info. Creates a new one if needed.
     *
     * @return a template info.
     */
    TemplateInfo getTemplateInfo();

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
     * to be displayed as an ECS string element.
     *
     * @return a string element.
     */
    StringElement getMessageAsHTML();

    /**
     * Sets the message for the request as an ECS element.
     *
     * @param msg an element.
     */
    void setMessage(Element msg);

    /**
     * Adds the ECS element to message. If message has prior messages from
     * other actions or screens, this method can be used to chain them.
     *
     * @param msg an element.
     */
    void addMessage(Element msg);

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
    FormMessages getMessages();

    /**
     * Sets the FormMessages object for the request.
     *
     * @param msgs A FormMessages.
     */
    void setMessages(FormMessages msgs);

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
     * @return a user.
     */
    User getUser();

    /**
     * Sets the user.
     *
     * @param user a user.
     */
    void setUser(User user);

    /**
     * Attempts to get the user from the session. If it does
     * not exist, it returns null.
     *
     * @return a user.
     */
    User getUserFromSession();

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
    boolean isOutSet();

    /**
     * Gets the print writer. First time calling this
     * will set the print writer via the response.
     *
     * @return a print writer.
     * @throws IOException
     * @deprecated no replacement planned, response writer will not be cached
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
    String getCharSet();

    /**
     * Sets the charset.
     *
     * @param charset the name of the new charset.
     */
    void setCharSet(String charset);

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
    Map getJNDIContexts();

    /**
     * Sets JNDI Contexts.
     *
     * @param contexts a hashtable.
     */
    void setJNDIContexts(Map contexts);

    /**
     * Gets the cached server scheme.
     *
     * @return a string.
     */
    String getServerScheme();

    /**
     * Gets the cached server name.
     *
     * @return a string.
     */
    String getServerName();

    /**
     * Gets the cached server port.
     *
     * @return an int.
     */
    int getServerPort();

    /**
     * Gets the cached context path.
     *
     * @return a string.
     */
    String getContextPath();

    /**
     * Gets the cached script name.
     *
     * @return a string.
     */
    String getScriptName();

    /**
     * Gets the server data used by the request.
     *
     * @return server data.
     */
    ServerData getServerData();

    /**
     * Gets the IP address of the client that sent the request.
     *
     * @return a string.
     */
    String getRemoteAddr();

    /**
     * Gets the qualified name of the client that sent the request.
     *
     * @return a string.
     */
    String getRemoteHost();

    /**
     * Get the user agent for the request.
     *
     * @return a string.
     */
    String getUserAgent();

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
     * Gets a table of debug variables.
     *
     * @return a Map of debug variables.
     * @deprecated use {@link #getDebugVariables} instead
     */
    Map getVarDebug();

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
    Map getDebugVariables();
}
