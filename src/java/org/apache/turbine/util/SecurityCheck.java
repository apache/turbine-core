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


import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.services.security.TurbineSecurity;

/**
 * Utility for doing security checks in Screens and Actions.
 *
 * Sample usage:<br>
 *
 * <pre><code>
 * SecurityCheck mycheck =
 *   new SecurityCheck(data, "Unauthorized to do this!", "WrongPermission");
 * if (!mycheck.hasPermission("add_user");
 *   return;
 *</code></pre>
 *
 * @author <a href="mailto:mbryson@mindspring.com">Dave Bryson</a>
 * @version $Id$
 */
public class SecurityCheck
{
    private String message;
    private String failScreen;
    private RunData data = null;

    /**
     * Constructor.
     *
     * @param data A Turbine RunData object.
     * @param message The message to display upon failure.
     * @param failedScreen The screen to redirect to upon failure.
     */
    public SecurityCheck(RunData data,
                         String message,
                         String failedScreen)
    {
        this.data = data;
        this.message = message;
        this.failScreen = failedScreen;
    }

    /**
     * Does the user have this role?
     *
     * @param role A Role.
     * @return True if the user has this role.
     * @exception Exception, a generic exception.
     */
    public boolean hasRole(Role role)
            throws Exception
    {
        boolean value = false;
        if (data.getACL() == null ||
                !data.getACL().hasRole(role))
        {
            data.setScreen(failScreen);
            data.setMessage(message);
        }
        else
        {
            value = true;
        }
        return value;
    }

    /**
     * Does the user have this role?
     *
     * @param role A String.
     * @return True if the user has this role.
     * @exception Exception, a generic exception.
     */
    public boolean hasRole(String role)
            throws Exception
    {
        return hasRole(TurbineSecurity.getRoleByName(role));
    }

    /**
     * Does the user have this permission?
     *
     * @param permission A Permission.
     * @return True if the user has this permission.
     * @exception Exception, a generic exception.
     */
    public boolean hasPermission(Permission permission)
            throws Exception
    {
        boolean value = false;
        if (data.getACL() == null ||
                !data.getACL().hasPermission(permission))
        {
            data.setScreen(failScreen);
            data.setMessage(message);
        }
        else
        {
            value = true;
        }
        return value;
    }

    /**
     * Does the user have this permission?
     *
     * @param permission A String.
     * @return True if the user has this permission.
     * @exception Exception, a generic exception.
     */
    public boolean hasPermission(String permission)
            throws Exception
    {
        return hasPermission(TurbineSecurity.getPermissionByName(permission));
    }

    /**
     * Get the message that should be displayed.  This is initialized
     * in the constructor.
     *
     * @return A String.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Get the screen that should be displayed.  This is initialized
     * in the constructor.
     *
     * @return A String.
     */
    public String getFailScreen()
    {
        return failScreen;
    }
}
