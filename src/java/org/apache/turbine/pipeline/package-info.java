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
 * 
 * <p>
 *   The Turbine processing pipeline is modeled after the pipeline found
 *   in Tomcat 4.0.1 (Catalina), and after the module architecture found
 *   in Apache httpd.  It is extensible both laterally and
 *   vertically via Valve additions and default Valve implementations
 *   (respectively).  Even the semantics of the default Turbine
 *   Pipeline (ClassicPipeline) can be changed by replacing or extending
 *   the implementation with a custom one.
 * </p>
 * 
 * <p>
 *   As with Catalina, Turbine's Pipeline and Valve configuration is
 *   setup via XML:
 * 
 *   <code>
 * 	&lt;pipeline name="default"&gt;
 * 	  &lt;valves&gt;
 * 	    &lt;valve&gt;org.apache.turbine.pipeline.DetermineActionValve&lt;/valve&gt;
 * 	    &lt;valve&gt;org.apache.turbine.pipeline.DetermineTargetValve&lt;/valve&gt;
 * 	    &lt;valve&gt;org.apache.turbine.pipeline.DefaultSessionTimeoutValve&lt;/valve&gt;
 * 	    &lt;valve&gt;org.apache.turbine.pipeline.DefaultLoginValve&lt;/valve&gt;
 * 	    &lt;valve&gt;org.apache.turbine.pipeline.DefaultSessionValidationValve&lt;/valve&gt;
 * 	    &lt;valve&gt;org.apache.turbine.pipeline.DefaultACLCreationValve&lt;/valve&gt;
 * 	    &lt;valve&gt;org.apache.turbine.pipeline.ExecutePageValve&lt;/valve&gt;
 * 	    &lt;valve&gt;org.apache.turbine.pipeline.CleanUpValve&lt;/valve&gt;
 * 	    &lt;valve&gt;org.apache.turbine.pipeline.DetermineRedirectRequestedValve&lt;/valve&gt;
 * 	  &lt;/valves&gt;
 * 	&lt;/pipeline&gt;
 *   </code>
 * </p>
 * 
 * <p>
 *   Please direct all comments, fixes, and enhancements to the
 *   <a href="mailto:dev@turbine.apache.org">development list</a>.
 * </p>
 * 
*/
package org.apache.turbine.pipeline;
