package org.apache.turbine.services.urlmapper;

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

import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.services.pull.tools.TemplateLink;

/**
 * This is a pull to to be used in Templates to convert links in
 * Templates into the correct references.
 *
 * The pull service might insert this tool into the Context.
 * in templates.  Here's an example of its Velocity use:
 *
 * <p><code>
 * $link.setPage("index.vm").addPathInfo("hello","world")
 * This would return: http://foo.com/Turbine/template/index.vm/hello/world
 * </code>
 *
 * <p>
 *
 * This is an application pull tool for the template system. You should <b>not</b>
 * use it in a normal application!
 *
 * @author <a href="mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id: TemplateLink.java 1854688 2019-03-03 10:36:42Z tv $
 */

public class MappedTemplateLink extends TemplateLink
{
    /**
     * The URL Mapper service.
     */
    @TurbineService
    private URLMapperService urlMapperService;
    

    /**
     * Builds the URL with all of the data URL-encoded as well as
     * encoded using HttpServletResponse.encodeUrl(). The resulting
     * URL is absolute; it starts with http/https...
     *
     * <pre>
     * TemplateURI tui = new TemplateURI (data, "UserScreen");
     * tui.addPathInfo("user","jon");
     * tui.getAbsoluteLink();
     * </pre>
     *
     * The above call to absoluteLink() would return the String:
     * <p>
     * http://www.server.com/servlets/Turbine/screen/UserScreen/user/jon
     * <p>
     * After rendering the URI, it clears the
     * pathInfo and QueryString portions of the TemplateURI. So you can
     * use the $link reference multiple times on a page and start over
     * with a fresh object every time.
     *
     * @return A String with the built URL.
     */
    public String getAbsoluteLink()
    {
        urlMapperService.mapToURL(templateURI);
        return super.getAbsoluteLink();
    }


    /**
     * Builds the URL with all of the data URL-encoded as well as
     * encoded using HttpServletResponse.encodeUrl(). The resulting
     * URL is relative to the webserver root.
     *
     * <pre>
     * TemplateURI tui = new TemplateURI (data, "UserScreen");
     * tui.addPathInfo("user","jon");
     * tui.getRelativeLink();
     * </pre>
     *
     * The above call to relativeLink() would return the String:
     * <p>
     * /servlets/Turbine/screen/UserScreen/user/jon
     * <p>
     * After rendering the URI, it clears the
     * pathInfo and QueryString portions of the TemplateURI. So you can
     * use the $link reference multiple times on a page and start over
     * with a fresh object every time.
     *
     * @return A String with the built URL.
     */
    public String getRelativeLink()
    {
        urlMapperService.mapToURL(templateURI);
        return super.getRelativeLink();
    }
    
}
