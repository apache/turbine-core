package org.apache.turbine.util;

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

import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.services.security.TurbineSecurity;

/**
 * Utility for doing security checks in Screens and Actions.
 *
 * Sample usage:<br>
 *
 * <code>
 * SecurityCheck mycheck =
 *   new SecurityCheck(data, "Unauthorized to do this!", "WrongPermission");
 * if (!mycheck.hasPermission("add_user");
 *   return;
 *</code>
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
        return hasRole(TurbineSecurity.getRole(role));
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
        return hasPermission(TurbineSecurity.getPermission(permission));
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
