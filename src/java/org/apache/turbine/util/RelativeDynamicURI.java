package org.apache.turbine.util;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javax.servlet.http.HttpServletRequest;

/**
 * This creates a Dynamic URI for use within the Turbine system
 *
 * <p>If you use this class to generate all of your href tags as well
 * as all of your URI's, then you will not need to worry about having
 * session data setup for you or using HttpServletRequest.encodeUrl()
 * since this class does everything for you.
 * This class generates relative URI's which can be used in environments with
 * firewalls and gateways for outgoing connections.
 *
 * <code><pre>
 * RelativeDynamicURI dui = new RelativeDynamicURI (data, "UserScreen" );
 * dui.setName("Click Here").addPathInfo("user","jon");
 * dui.getA();
 * </pre></code>
 *
 * The above call to getA() would return the String:
 *
 * &lt;A HREF="/servlets/Turbine/screen=UserScreen&amp;amp;user=jon"&gt;ClickHere&lt;/A&gt;
 *
 * @author <a href="mailto:dfaller@raleigh.ibm.com">David S. Faller</a>
 * @version $Id$
 * @deprecated Use {@link org.apache.turbine.util.uri.TurbineURI} with {@link org.apache.turbine.util.uri.TurbineURI#getRelativeLink} instead.
 */
public class RelativeDynamicURI extends DynamicURI
{
    /**
     * Default constructor - one of the init methods must be called before use.
     */
    public RelativeDynamicURI()
    {
    }

    /**
     * Constructor sets up some variables.
     *
     * @param data A Turbine RunData object.
     */
    public RelativeDynamicURI(RunData data)
    {
        super(data);
    }

    /**
     * Constructor sets up some variables.
     *
     * @param data A Turbine RunData object.
     * @param screen A String with the name of a screen.
     */
    public RelativeDynamicURI(RunData data, String screen)
    {
        super(data, screen);
    }

    /**
     * Constructor sets up some variables.
     *
     * @param data A Turbine RunData object.
     * @param screen A String with the name of a screen.
     * @param action A String with the name of an action.
     */
    public RelativeDynamicURI(RunData data, String screen, String action)
    {
        super(data, screen, action);
    }

    /**
     * Constructor sets up some variables.
     *
     * @param data A Turbine RunData object.
     * @param screen A String with the name of a screen.
     * @param action A String with the name of an action.
     * @param redirect True if it should redirect.
     */
    public RelativeDynamicURI(RunData data,
                              String screen,
                              String action,
                              boolean redirect)
    {
        super(data, screen, action, redirect);
    }

    /**
     * Constructor sets up some variables.
     *
     * @param data A Turbine RunData object.
     * @param screen A String with the name of a screen.
     * @param redirect True if it should redirect.
     */
    public RelativeDynamicURI(RunData data,
                              String screen,
                              boolean redirect)
    {
        super(data, screen, redirect);
    }

    /**
     * Constructor sets up some variables.
     *
     * @param data A Turbine RunData object.
     * @param redirect True if it should redirect.
     */
    public RelativeDynamicURI(RunData data, boolean redirect)
    {
        super(data, redirect);
    }

    /**
     * Main constructor for RelativeDynamicURI.  Uses ServerData.
     *
     * @param sd A ServerData.
     */
    public RelativeDynamicURI(ServerData sd)
    {
        super(sd);
    }

    /**
     * Main constructor for RelativeDynamicURI.  Uses ServerData.
     *
     * @param sd A ServerData.
     * @param screen A String with the name of a screen.
     */
    public RelativeDynamicURI(ServerData sd, String screen)
    {
        super(sd, screen);
    }

    /**
     * Main constructor for RelativeDynamicURI.  Uses ServerData.
     *
     * @param sd A ServerData.
     * @param screen A String with the name of a screen.
     * @param action A String with the name of an action.
     */
    public RelativeDynamicURI(ServerData sd,
                              String screen,
                              String action)
    {
        super(sd, screen, action);
    }

    /**
     * Main constructor for RelativeDynamicURI.  Uses ServerData.
     *
     * @param sd A ServerData.
     * @param screen A String with the name of a screen.
     * @param action A String with the name of an action.
     * @param redirect True if it should redirect.
     */
    public RelativeDynamicURI(ServerData sd,
                              String screen,
                              String action,
                              boolean redirect)
    {
        super(sd, screen, action, redirect);
    }

    /**
     * Main constructor for RelativeDynamicURI.  Uses ServerData.
     *
     * @param sd A ServerData.
     * @param screen A String with the name of a screen.
     * @param redirect True if it should redirect.
     */
    public RelativeDynamicURI(ServerData sd,
                              String screen,
                              boolean redirect)
    {
        super(sd, screen, redirect);
    }

    /**
     * Main constructor for RelativeDynamicURI.  Uses ServerData.
     *
     * @param sd A ServerData.
     * @param redirect True if it should redirect.
     */
    public RelativeDynamicURI(ServerData sd, boolean redirect)
    {
        super(sd, redirect);
    }

    /**
     * Builds the relative URL with all of the data URL-encoded as well as
     * encoded using HttpServletResponse.encodeUrl().
     *
     * <p>
     * <code><pre>
     * RelativeDynamicURI dui = new RelativeDynamicURI (data, "UserScreen" );
     * dui.addPathInfo("user","jon");
     * dui.toString();
     * </pre></code>
     *
     *  The above call to toString() would return the String:
     *
     * <p>
     * /servlets/Turbine/screen/UserScreen/user/jon
     *
     * @return A String with the built relative URL.
     */
    public String toString()
    {
        assertInitialized();
        StringBuffer output = new StringBuffer();
        output.append(getContextPath());
        output.append(getScriptName());
        if (this.hasPathInfo)
        {
            output.append("/");
            output.append(renderPathInfo(this.pathInfo));
        }
        if (this.hasQueryData)
        {
            output.append("?");
            output.append(renderQueryString(this.queryData));
        }
        if (this.reference != null)
        {
            output.append("#");
            output.append(this.getReference());
        }

        // There seems to be a bug in Apache JServ 1.0 where the
        // session id is not appended to the end of the url when a
        // cookie has not been set.
        if (this.res != null)
        {
            if (this.redirect)
            {
                return res.encodeRedirectURL(output.toString());
            }
            else
            {
                return res.encodeURL(output.toString());
            }
        }
        else
        {
            return output.toString();
        }
    }

    /**
     * Given a RunData object, get a relative URI for the request.  This is
     * necessary sometimes when you want the relative URL and don't want
     * RelativeDynamicURI to be too smart and remove actions, screens, etc.
     * This also returns the Query Data where RelativeDynamicURI normally
     * would not.
     *
     * @param data A Turbine RunData object.
     * @return A String with the relative URL.
     */
    public static String toString(RunData data)
    {
        StringBuffer output = new StringBuffer();
        HttpServletRequest request = data.getRequest();

        output.append(data.getServerData().getContextPath());
        output.append(data.getServerData().getScriptName());

        if (request.getPathInfo() != null)
        {
            output.append(request.getPathInfo());
        }

        if (request.getQueryString() != null)
        {
            output.append("?");
            output.append(request.getQueryString());
        }
        return output.toString();
    }
}
