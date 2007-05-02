package org.apache.turbine.services.jsp;

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

import org.apache.turbine.services.Service;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;


/**
 * Implementations of the JspService interface.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @version $Id$
 */
public interface JspService
    extends Service
{
    /** The name used to specify this service in Turbine.properties */
    String SERVICE_NAME = "JspService";

    /** The key used to store an instance of RunData in the request */
    String RUNDATA = "rundata";

    /** The key used to store an instance of JspLink in the request */
    String LINK = "link";

    /** The default extension of JSPs */
    String JSP_EXTENSION = "jsp";

    /** Property key for Template Pathes */
    String TEMPLATE_PATH_KEY = "templates";

    /** Property for Jsp Page Buffer Size */
    String BUFFER_SIZE_KEY = "buffer.size";

    /** Default Value for Jsp Page Buffer Size */
    int BUFFER_SIZE_DEFAULT = 8192;

    /**
     * Adds some convenience objects to the request.  For example an instance
     * of JspLink which can be used to generate links to other templates.
     *
     * @param data the turbine rundata object
     */
    void addDefaultObjects(RunData data);

    /**
     * executes the JSP given by templateName.
     *
     * @param data A RunData Object
     * @param templateName The template to execute
     * @param isForward whether to perform a forward or include.
     *
     * @throws TurbineException If a problem occured while executing the JSP
     */
    void handleRequest(RunData data, String templateName, boolean isForward)
        throws TurbineException;

    /**
     * executes the JSP given by templateName.
     *
     * @param data A RunData Object
     * @param templateName The template to execute
     *
     * @throws TurbineException If a problem occured while executing the JSP
     */
    void handleRequest(RunData data, String templateName)
        throws TurbineException;

    /**
     * Returns the default buffer size of the JspService
     *
     * @return The default buffer size.
     */
    int getDefaultBufferSize();

    /**
     * Searchs for a template in the default.template path[s] and
     * returns the template name with a relative path which is required
     * by <a href="http://java.sun.com/products/servlet/2.3/javadoc/javax/servlet/ServletContext.html#getRequestDispatcher(java.lang.String)">javax.servlet.RequestDispatcher</a>
     *
     * @param template The name of the template to search for.
     *
     * @return the template with a relative path
     */
    String getRelativeTemplateName(String template);

}
