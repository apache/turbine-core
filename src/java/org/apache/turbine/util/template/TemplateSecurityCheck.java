package org.apache.turbine.util.template;

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
import org.apache.turbine.om.security.User;

import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.services.template.TurbineTemplate;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.security.AccessControlList;

/**
 * Utility class to help check for proper authorization when using
 * template screens.  Sample usages:
 *
 * <p><code>
 * TemplateSecurityCheck secCheck = new TemplateSecurityCheck( data );
 * secCheck.setMessage( "Sorry, you do not have permission to " +
 *                      "access this area." );
 * secCheck.setFailTemplate("login.wm");
 * if ( !secCheck.hasRole("ADMIN") )
 *     return;
 * </code>
 *
 * @author <a href="mbryson@mont.mindspring.com">Dave Bryson</a>
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
    public TemplateSecurityCheck ( RunData data,
                                   String message )
    {
        this.data=data;
        this.message=message;
    }

    /**
     * Generic Constructor.
     *
     * @param data A Turbine RunData object.
     */
    public TemplateSecurityCheck( RunData data )
    {
        this.data=data;
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
        if ( !checkLogin() )
        {
            return false;
        }

        if ( data.getACL() == null ||
             !data.getACL().hasRole(role) )
        {
            data.setScreen( getFailScreen() );
            data.getTemplateInfo().setScreenTemplate( getFailTemplate() );
            data.setMessage(getMessage());
            return false;
        }
        else
        {
            return true;
        }
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
        if ( data.getACL() == null ||
             !data.getACL().hasPermission(permission) )
        {
            data.setScreen( getFailScreen() );
            data.getTemplateInfo().setScreenTemplate( getFailTemplate() );
            data.setMessage(getMessage());
            return false;
        }
        else
        {
            return true;
        }
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
        boolean value = false;

        if ( data.getUser() != null &&
             !data.getUser().hasLoggedIn() )
        {
            data.setMessage( TurbineResources.getString("login.message") );
            data.getTemplateInfo().setScreenTemplate( getFailTemplate() );
            value = false;
        }
        else
        {
            value = true;
        }
        return value;
    }

    /**
     * Set the message that should be displayed.  This is initialized
     * in the constructor.
     *
     * @param v A String with the message that should be displayed.
     */
    public void setMessage( String v )
    {
        this.message=v;
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
