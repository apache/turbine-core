package org.apache.turbine;

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

/**
 * This interface contains all the constants used throughout
 * the Turbine code base.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public interface TurbineConstants
{
    /**
     * <p>The prefix used to denote the namespace reserved for and
     * used by Turbine-specific configuration parameters (such as
     * those passed in via servlet container's config file
     * (<code>server.xml</code>), or the web app deployment descriptor
     * (<code>web.xml</code>).</p>
     *
     * <p>For example, a parameter in the Turbine namespace would be
     * <code>org.apache.turbine.loggingRoot</code>.</p>
     */
    String CONFIG_NAMESPACE = "org.apache.turbine";

    /** The key for the Log4J File */
    String LOG4J_CONFIG_FILE = "log4j.file";

    /** The default value for the Log4J File */
    String LOG4J_CONFIG_FILE_DEFAULT = "/WEB-INF/conf/Log4j.properties";

    /** This is the default log file to be used for logging */
    String DEFAULT_LOGGER = "turbine";

    /** The logging facility which captures output from the SchedulerService. */
    String SCHEDULER_LOG_FACILITY = "scheduler";

    /** The SMTP server Turbine uses to send mail. */
    String MAIL_SERVER_KEY = "mail.server";

    /** Property that controls whether Turbine modules are cached or not. */
    String MODULE_CACHE_KEY = "module.cache";

    /** Default value of the Turbine Module Caching */
    boolean MODULE_CACHE_DEFAULT = true;

    /** Property for the size of the action cache if caching is on */
    String ACTION_CACHE_SIZE_KEY = "action.cache.size";

    /** The default size for the action cache */
    int ACTION_CACHE_SIZE_DEFAULT = 20;

    /** Property for the size of the layout cache if caching is on */
    String LAYOUT_CACHE_SIZE_KEY = "layout.cache.size";

    /** The default size for the layout cache */
    int LAYOUT_CACHE_SIZE_DEFAULT = 10;

    /** Property for the size of the navigation cache if caching is on */
    String NAVIGATION_CACHE_SIZE_KEY = "navigation.cache.size";

    /** The default size for the navigation cache */
    int NAVIGATION_CACHE_SIZE_DEFAULT = 10;

    /** Property for the size of the page cache if caching is on */
    String PAGE_CACHE_SIZE_KEY = "page.cache.size";

    /** The default size for the page cache */
    int PAGE_CACHE_SIZE_DEFAULT = 5;

    /** Property for the size of the screen cache if caching is on */
    String SCREEN_CACHE_SIZE_KEY = "screen.cache.size";

    /** The default size for the screen cache */
    int SCREEN_CACHE_SIZE_DEFAULT = 50;

    /** The size of the schedulder job cache if module caching is on. */
    String SCHEDULED_JOB_CACHE_SIZE = "scheduledjob.cache.size";

    /** The packages where Turbine will look for modules. */
    String MODULE_PACKAGES = "module.packages";

    /** Home page template. */
    String TEMPLATE_HOMEPAGE = "template.homepage";

    /** Login template. */
    String TEMPLATE_LOGIN = "template.login";

    /** Template error template Property. */
    String TEMPLATE_ERROR_KEY = "template.error";

    /** Template error default for JSP */
    String TEMPLATE_ERROR_JSP = "error.jsp";

    /** Template error default for Velocity */
    String TEMPLATE_ERROR_VM = "error.vm";

    /** Home page screen. */
    String SCREEN_HOMEPAGE = "screen.homepage";

    /** Login screen. */
    String SCREEN_LOGIN = "screen.login";

    /** Login error screen. */
    String SCREEN_ERROR_KEY = "screen.error";

    /** Default value for Login Screen */
    String SCREEN_ERROR_DEFAULT = "VelocityErrorScreen";

    /** Report Screen for invalid state in the application*/
    String SCREEN_INVALID_STATE = "screen.invalidstate";

    /** Report Template for invalid state in the application */
    String TEMPLATE_INVALID_STATE = "template.invalidstate";

    /** Action to perform when a user logs in. */
    String ACTION_LOGIN_KEY = "action.login";

    /** Default Value for login Action */
    String ACTION_LOGIN_DEFAULT = "LoginUser";

    /** Action to perform when a user logs out. */
    String ACTION_LOGOUT_KEY = "action.logout";

    /** Default Value for ACTION_LOGOUT */
    String ACTION_LOGOUT_DEFAULT = "LogoutUser";

    /** Actions that performs session validation. */
    String ACTION_SESSION_VALIDATOR_KEY = "action.sessionvalidator";

    /** Default value for the session validator. (org.apache.modules.actions.sessionvalidator.TemplateSessionValidator) */
    String ACTION_SESSION_VALIDATOR_DEFAULT = "sessionvalidator.TemplateSessionValidator";

    /** Action that performs Access control */
    String ACTION_ACCESS_CONTROLLER_KEY = "action.accesscontroller";

    /** Default value for the access controller. (org.apache.modules.actions.AccessController) */
    String ACTION_ACCESS_CONTROLLER_DEFAULT = "AccessController";

    /** Default layout. */
    String LAYOUT_DEFAULT = "layout.default";

    /** Default page. */
    String PAGE_DEFAULT_KEY = "page.default";

    /** Default value for the Default Page */
    String PAGE_DEFAULT_DEFAULT = "DefaultPage";

    /** Default value for the Default Screen */
    String SCREEN_DEFAULT_DEFAULT = "DefaultScreen";

    /** Message to display upon successful login. */
    String LOGIN_MESSAGE = "login.message";

    /** Message to display when a user fails to login. */
    String LOGIN_ERROR = "login.error";

    /** Message to display when screens variable invalid. */
    String LOGIN_MESSAGE_NOSCREEN = "login.message.noscreen";

    /** Message to display when a user logs out. */
    String LOGOUT_MESSAGE = "logout.message";

    /** Session Timeout */
    String SESSION_TIMEOUT_KEY = "session.timeout";

    /** Session Timeout Default Value */
    int SESSION_TIMEOUT_DEFAULT = -1;

    /**
     * Indicate whether this Turbine application is using SSL.
     * Used for creating dynamic URIs.
     */
    String USE_SSL = "use.ssl";

    /**
     * Should the PP fold the case of everything. Possible values are
     * "upper", "lower" and "none".
     */
    String PP_URL_CASE_FOLDING = "url.case.folding";

    /** Default document type. */
    String DEFAULT_DOCUMENT_TYPE_KEY = "default.doctype";

    /** Html 4.0 Transitional */
    String DOCUMENT_TYPE_HTML40TRANSITIONAL = "Html40Transitional";
    /** Html 4.0 Strict */
    String DOCUMENT_TYPE_HTML40STRICT = "Html40Strict";
    /** Html 4.0 Frameset */
    String DOCUMENT_TYPE_HTML40FRAMESET = "Html40Frameset";

    /** Default Language property */
    String LOCALE_DEFAULT_LANGUAGE_KEY = "locale.default.language";

    /** Default value for Language property */
    String LOCALE_DEFAULT_LANGUAGE_DEFAULT = "en";

    /** Default Country property */
    String LOCALE_DEFAULT_COUNTRY_KEY = "locale.default.country";

    /** Default value for Country property */
    String LOCALE_DEFAULT_COUNTRY_DEFAULT = "US";

    /** Default Charset property */
    String LOCALE_DEFAULT_CHARSET_KEY = "locale.default.charset";

    /** Default value for Charset property */
    String LOCALE_DEFAULT_CHARSET_DEFAULT = "ISO-8859-1";

    /** If this value is set as applicationRoot, then the webContext is used
     * as application root
     */
    String WEB_CONTEXT = "webContext";

    /** Key for the Path to the TurbineResources.properties File */ 
    String APPLICATION_ROOT_KEY = "applicationRoot";

    /** Default Value for the Path to the TurbineResources.properties File */ 
    String APPLICATION_ROOT_DEFAULT = WEB_CONTEXT;
  
    /** This is the key used in the Turbine.properties to access resources 
     * relative to the Web Application root. It might differ from the 
     * Application root, but the normal case is, that the webapp root
     * and the application root point to the same path.
     */
    String WEBAPP_ROOT_KEY = "webappRoot";

    /** The Key in the deployment descriptor for the Logging Directory */
    String LOGGING_ROOT_KEY = "loggingRoot";
    
    /** Default Value for the Logging Directory, relative to the webroot */
    String LOGGING_ROOT_DEFAULT = "/logs";
}
