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
 */
public interface TurbineConstants
{
    /**
     * The logging facility which captures output from Peers.
     */
    public static final String SQL_LOG_FACILITY = "sql";

    /**
     * The logging facility which captures output from the SchedulerService.
     */
    public static final String SCHEDULER_LOG_FACILITY = "scheduler";

    /**
     * SMTP server Turbine uses to send mail.
     */
    public static final String MAIL_SERVER_KEY = "mail.server";

    /**
     * Property that controls whether Turbine modules are
     * cached or not.
     */
    public static final String MODULE_CACHE = "module.cache";

    /**
     * The size of the actions cache if module caching is on.
     */
    public static final String ACTION_CACHE_SIZE = "action.cache.size";

    /**
     * The size of the layout cache if module caching is on.
     */
    public static final String LAYOUT_CACHE_SIZE = "layout.cache.size";

    /**
     * The size of the navigation cache if module caching is on.
     */
    public static final String NAVIGATION_CACHE_SIZE = "navigation.cache.size";

    /**
     * The size of the actions page if module caching is on.
     */
    public static final String PAGE_CACHE_SIZE = "page.cache.size";

    /**
     * The size of the actions cache if module caching is on.
     */
    public static final String SCREEN_CACHE_SIZE = "screen.cache.size";

    /**
     * The size of the actions cache if module caching is on.
     */
    public static final String SCHEDULED_JOB_CACHE_SIZE = "scheduledjob.cache.size";

    /**
     * The packages where Turbine will look for modules.
     * This is effectively Turbine's classpath.
     */
    public static final String MODULE_PACKAGES = "module.packages";

    /**
     * JDBC database driver.
     */
    public static final String DB_DRIVER = "database.default.driver";

    /**
     * JDBC database URL.
     */
    public static final String DB_URL = "database.default.url";

    /**
     * JDBC username.
     */
    public static final String DB_USERNAME = "database.default.username";

    /**
     * JDBC password.
     */
    public static final String DB_PASSWORD = "database.default.password";

    /**
     * Maximum number of connections to pool.
     */
    public static final String DB_MAXCONNECTIONS = "database.maxConnections";

    /**
     * Expiry time of database connections.
     */
    public static final String DB_EXPIRYTIME = "database.expiryTime";

    /**
     * How long a connection request will wait before giving up.
     */
    public static final String DB_CONNECTION_WAIT_TIMEOUT = "database.connectionWaitTimeout";

    /**
     * How often the PoolBrokerServer logs the status of the pool.
     */
    public static final String DB_CONNECTION_LOG_INTERVAL = "database.logInterval";

    /**
     * Database adaptor.
     */
    public static final String DB_ADAPTOR = "database.adaptor";

    /**
     * Indicates that the id broker will generate more ids
     * if the demand is high.
     */
    public static final String DB_IDBROKER_CLEVERQUANTITY = "database.idbroker.cleverquantity";

    /**
     * Home page template.
     */
    public static final String TEMPLATE_HOMEPAGE = "template.homepage";

    /**
     * Login template.
     */
    public static final String TEMPLATE_LOGIN = "template.login";

    /**
     * Login error template.
     */
    public static final String TEMPLATE_ERROR = "template.error";

    /**
     * Home page screen.
     */
    public static final String SCREEN_HOMEPAGE = "screen.homepage";

    /**
     * Login screen.
     */
    public static final String SCREEN_LOGIN = "screen.login";

    /**
     * Login error screen.
     */
    public static final String SCREEN_ERROR = "screen.error";
    public static final String SCREEN_INVALID_STATE = "screen.invalidstate";
    public static final String TEMPLATE_INVALID_STATE = "template.invalidstate";

    /**
     * Action to perform when a user logs in.
     */
    public static final String ACTION_LOGIN = "action.login";

    /**
     * Action to perform when a user logs out.
     */
    public static final String ACTION_LOGOUT = "action.logout";

    /**
     * Actions that performs session validation.
     */
    public static final String ACTION_SESSION_VALIDATOR = "action.sessionvalidator";

    /**
     * I don't think this is being used, is it?
     */
    public static final String ACTION_ACCESS_CONTROLLER = "action.accesscontroller";

    /**
     * Default layout.
     */
    public static final String LAYOUT_DEFAULT = "layout.default";

    /**
     * Default page.
     */
    public static final String PAGE_DEFAULT = "page.default";

    /**
     * Map building. This will probably be Torque generated at
     * some point.
     */
    public static final String MAPS_BUILDER = "database.maps.builder";

    /**
     * Message to display upon successful login.
     */
    public static final String LOGIN_MESSAGE = "login.message";

    /**
     * Message to display when a user fails to login.
     */
    public static final String LOGIN_ERROR = "login.error";

    /**
     * Message to display when screens variable invalid.
     */
    public static final String LOGIN_MESSAGE_NOSCREEN = "login.message.noscreen";

    /**
     * Message to display when a user logs out.
     */
    public static final String LOGOUT_MESSAGE = "logout.message";

    /**
     * Indicate whether this Turbine application is using SSL.
     * Used for creating dynamic URIs.
     */
    public static final String USE_SSL = "use.ssl";

    /**
     * Should the PP fold the case of everything. Possible values are
     * "upper", "lower" and "none".
     */
    public static final String PP_URL_CASE_FOLDING = "url.case.folding";

    /**
     * Default document type.
     */
    public static final String DEFAULT_DOCUMENT_TYPE = "default.doctype";

    public static final String APPLICATION_ROOT = "applicationRoot";
}
