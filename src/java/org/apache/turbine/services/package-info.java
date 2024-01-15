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

/**
 * Contains the Service framework for Turbine.
 * <p>
 * Services are singletons that have pluggable implementation
 * and can participate in Turbine startup and shutdown.
 * </p>
 * <table summary="Turbine service explained.">
 *     <tr><th>What is a service?</th></tr>
 *     <tr><td style="width: 100%">
 * <ul>
 * <li> Is a singleton -  there is only one instance of it in the system
 *      i.e. memory or connections are allocated once only, and the internal
 *      state is common to all requesting clients.
 * <li> Has pluggable implementation - you can use your own implementation
 *      if you need, just change  an entry in TurbineResources.properties,
 *      and there you go.
 * <li> Can access ServletConfig at system startup time to process
 *      relative paths and the like.
 * <li> Can access RunData on the first Turbine doGet execution to
 *      get URL we're running under and the like.
 * <li> Can initialize itself (allocate memory, make connections)
 *      just before the client requests it for the first time. Services that
 *      are never used by the application will not allocate resources.
 * <li> Can execute some action upon system shutdown e.g. close the
 *      opened connections.
 * </ul>
 * <strong>The life cycle of a Service</strong>
 * <p>
 * A Service (or any other Initable, if we had any) is not supposed
 * to do much in it's constructor. Especially it should not allocate
 * any costly resources like large memory structures, DB or network
 * connections and the like. It may well happen that the Service
 * is sitting in the config file, but the application does not
 * need it, so allocating all resources at system startup might
 * be a loss.
 * </p><p>
 * Early initialization is similar to the constructor. It is used
 * to pass some information that the Service will need in it's
 * future operation.
 * UniqueId Service uses the HttpRequest object from the first Turbine
 * invocation to determine URL this instance is running under, to
 * generate instance ID.
 * Early initialization method should process the configuration, store
 * some values, but NOT allocate resources. There is still a chance
 * that the Service will not be used.
 * If the Service is ready to work (i.e. does not need any more objects
 * being sent to it), and does not to allocate any resources during
 * late initialization, the internal state can be changed so that
 * getInit() returns true.
 * </p><p>
 * Late initialization happens when the Service is requested by the
 * application for the first time. It should allocate any resources
 * needed and change the state so that getInit() returns true.
 * If getInit() returns false after init() is executed, the Service
 * has malfunctioned.
 * </p><p>
 * After late initialization, the Service is ready to perform actions
 * on behalf of the application.
 * </p><p>
 * When the Service is no longer needed (this usually happens when
 * system is shutting down), the shutdown() method is called.
 * shutdown() should deallocate all resources. If any error conditions
 * occur they are ignored.
 * </p>
 * <h3>Initialization of services outside of the Turbine servlet</h3>
 * <p>
 * In the case where specific Turbine services are desired outside the
 * context of the <code>Turbine</code> servlet, a Turbine JAR file can be
 * used in conjunction with a <i>properly configured</i>
 * <code>TurbineResources.properties</code> file to initialize a specific
 * set of services to use in your application.  The following sample
 * code performs such initialization:
 * </p>
 * <code>
 * String webAppRoot = "/var/httpd/webapps";
 * String trProps = "/var/httpd/TurbineResources.properties";
 * try
 * {
 *     TurbineConfig cfg = new TurbineConfig(webAppRoot, trProps);
 *     cfg.init();
 * }
 * catch (Exception e)
 * {
 *     // If Turbine fails to initialize, no logging service will be available.
 *     String msg = "Failed to initialize Turbine: " + e.getMessage();
 *     // Write directly to stderr to preserve the full stack trace.
 *     System.err.println(msg);
 *     e.printStackTrace();
 *     throw new Error(msg);
 * }
 * </code>
 * </td></tr></table>
 * <br>
*/
package org.apache.turbine.services;
