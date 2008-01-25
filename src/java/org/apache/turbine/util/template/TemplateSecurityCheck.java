package org.apache.turbine.util.template;


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


import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.util.RunData;

/**
 * Utility class to help check for proper authorization when using
 * template screens.  Sample usages:
 *
 * <p><pre><code>
 * TemplateSecurityCheck secCheck = new TemplateSecurityCheck( data );
 * secCheck.setMessage( "Sorry, you do not have permission to " +
 *                      "access this area." );
 * secCheck.setFailTemplate("login.wm");
 * if ( !secCheck.hasRole("ADMIN") )
 *     return;
 * </pre></code>
 *
 * @author <a href="mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TemplateSecurityCheck
{
    private String message =
            "Sorry, you do not have permission to access this area.";
    private String failScreen = TurbineTemplate.getDefaultScreen();
    private String failTemplate;
    private RunData data = null;

    /**
     * Constructor.
     *
     * @param data A Turbine RunData object.
     * @param message A String with the message to display upon
     * failure.
     */
    public TemplateSecurityCheck(RunData data, String message)
    {
        this.data = data;
        this.message = message;
    }

    /**
     * Generic Constructor.
     *
     * @param data A Turbine RunData object.
     */
    public TemplateSecurityCheck(RunData data)
    {
        this.data = data;
    }

    /**
     * Does the User have this role?
     *
     * @param role The role to be checked.
     * @return Whether the user has the role.
     * @exception Exception Trouble validating.
     */
    public boolean hasRole(Role role)
        throws Exception
    {
        if (!checkLogin())
        {
            return false;
        }

        if (data.getACL() == null || !data.getACL().hasRole(role))
        {
            data.setScreen(getFailScreen());
            data.getTemplateInfo().setScreenTemplate(getFailTemplate());
            data.setMessage(getMessage());
            return false;
        }

        return true;
    }

    /**
     * Does the User have this permission?
     *
     * @param permission The permission to be checked.
     * @return Whether the user has the permission.
     * @exception Exception Trouble validating.
     */
    public boolean hasPermission(Permission permission)
        throws Exception
    {
        boolean value = true;
        if (data.getACL() == null || !data.getACL().hasPermission(permission))
        {
            data.setScreen(getFailScreen());
            data.getTemplateInfo().setScreenTemplate(getFailTemplate());
            data.setMessage(getMessage());
            value = false;
        }

        return value;
    }

    /**
     * Check that the user has logged in.
     *
     * @return True if user has logged in.
     * @exception Exception, a generic exception.
     */
    public boolean checkLogin()
        throws Exception
    {
        boolean value = true;

        // Do it like the AccessController
        if (!TurbineSecurity.isAnonymousUser(data.getUser())
            && !data.getUser().hasLoggedIn())
        {
            data.setMessage(Turbine.getConfiguration()
                .getString(TurbineConstants.LOGIN_MESSAGE));

            data.getTemplateInfo().setScreenTemplate(getFailTemplate());
            value = false;
        }

        return value;
    }

    /**
     * Set the message that should be displayed.  This is initialized
     * in the constructor.
     *
     * @param v A String with the message that should be displayed.
     */
    public void setMessage(String v)
    {
        this.message = v;
    }

    /**
     * Get the message that should be displayed.  This is initialized
     * in the constructor.
     *
     * @return A String with the message that should be displayed.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Get the value of failScreen.
     *
     * @return A String with the value of failScreen.
     */
    public String getFailScreen()
    {
        return failScreen;
    }

    /**
     * Set the value of failScreen.
     *
     * @param v A String with the value of failScreen.
     */
    public void setFailScreen(String v)
    {
        this.failScreen = v;
    }

    /**
     * Get the value of failTemplate.
     *
     * @return A String with the value of failTemplate.
     */
    public String getFailTemplate()
    {
        return failTemplate;
    }

    /**
     * Set the value of failTemplate.
     *
     * @param v A String with the value of failTemplate.
     */
    public void setFailTemplate(String v)
    {
        this.failTemplate = v;
    }
}
