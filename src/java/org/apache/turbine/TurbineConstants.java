package org.apache.turbine;

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

import org.apache.turbine.pipeline.TurbinePipeline;




/**
 * This interface contains all the constants used throughout
 * the Turbine code base.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
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

	/** Default Value for he SMTP server that Turbine uses to send mail. */
	String MAIL_SERVER_DEFAULT = "localhost";

	/** The Smtp sender address property */
	String MAIL_SMTP_FROM = "mail.smtp.from";

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

	/** The key for the schedulder job cache size if module caching is on. */
	String SCHEDULED_JOB_CACHE_SIZE_KEY = "scheduledjob.cache.size";

	/** The default size of the schedulder job cache if module caching is on. */
	int SCHEDULED_JOB_CACHE_SIZE_DEFAULT = 10;

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

	/** Select whether an Action Event must have a non-zero value */
	String ACTION_EVENTSUBMIT_NEEDSVALUE_KEY = "action.eventsubmit.needsvalue";

	/** Default value for action.eventsubmit.needsvalue */
	boolean ACTION_EVENTSUBMIT_NEEDSVALUE_DEFAULT = false;

	/** Select whether an exception in an Action method is bubbled up to Turbine.handleException() */
	String ACTION_EVENT_BUBBLE_EXCEPTION_UP = "action.event.bubbleexception";

	/** Default value for action.event.bubbleexception */
	boolean ACTION_EVENT_BUBBLE_EXCEPTION_UP_DEFAULT = true;

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

	/** Indicate whether this Turbine application is using SSL. */
	String USE_SSL_KEY = "use.ssl";

	/** Default value for the SSL key */
	boolean USE_SSL_DEFAULT = true;

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

	/** Default doctype root element. */
	String DEFAULT_HTML_DOCTYPE_ROOT_ELEMENT_KEY
			= "default.html.doctype.root.element";

	/** Default value for the doctype root element */
	String DEFAULT_HTML_DOCTYPE_ROOT_ELEMENT_DEFAULT
			= "HTML";

	/** Default doctype dtd. */
	String DEFAULT_HTML_DOCTYPE_IDENTIFIER_KEY
			= "default.html.doctype.identifier";

	/** Default Doctype dtd value */
	String DEFAULT_HTML_DOCTYPE_IDENTIFIER_DEFAULT
			= "-//W3C//DTD HTML 4.01 Transitional//EN";

	/** Default doctype url. */
	String DEFAULT_HTML_DOCTYPE_URI_KEY
			= "default.html.doctype.url";

	/** Default doctype url value. */
	String DEFAULT_HTML_DOCTYPE_URI_DEFAULT
			= "http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd";

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

	/** Key for loading the UUID Generator with a constant value */
	String UUID_ADDRESS_KEY = "uuid.address";

	/** Context Key for the screen placeholder in the various velocity layouts */
	String SCREEN_PLACEHOLDER = "screen_placeholder";

	/** Context Key for the navigation object placeholder in the various velocity layouts */
	String NAVIGATION_PLACEHOLDER = "navigation";

	/** Context Key for the Processing Exception */
	String PROCESSING_EXCEPTION_PLACEHOLDER = "processingException";

	/** Context Key for the Stack Trace */
	String STACK_TRACE_PLACEHOLDER = "stackTrace";

	/** Prefix for layout related classes and templates */
	String LAYOUT_PREFIX = "layouts";

	/** Prefix for screen related classes and templates */
	String SCREEN_PREFIX = "screens";

	/** Prefix for navigation related classes and templates */
	String NAVIGATION_PREFIX = "navigations";

	/** Prefix for action related classes and templates */
	String ACTION_PREFIX = "actions";

	/** Prefix for page related classes and templates */
	String PAGE_PREFIX = "pages";

	/** Prefix for scheduler job related classes */
	String SCHEDULEDJOB_PREFIX = "scheduledjobs";

	/**
	 * The fully qualified class name of the default {@link
	 * org.apache.turbine.Pipeline} implementation to use in the
	 * {@link org.apache.turbine.Turbine} servlet.
	 */
	public static final String STANDARD_PIPELINE =
		TurbinePipeline.class.getName();
}
