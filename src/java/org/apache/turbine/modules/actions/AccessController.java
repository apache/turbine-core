package org.apache.turbine.modules.actions;

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

// Turbine Utility Classes
import org.apache.turbine.modules.Action;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.security.AccessControlList;


/**
 * This action doPerforms an Access Control List and places it into
 * the RunData object, so it is easily available to modules.  The ACL
 * is also placed into the session.  Modules can null out the ACL to
 * force it to be rebuilt based on more information.
 *
 * <p>
 *
 * Turbine uses a User-Role-Permission arrangement for access control.
 * Users are assigned Roles.  Roles are assigned Permissions.  Turbine
 * modules then check the Permission required for an action or
 * information with the set of Permissions currently associated with
 * the session (which are dependent on the user associated with the
 * session.)
 *
 * <p>
 *
 * The criteria for assigning Roles/Permissions is application
 * dependent, in some cases an application may change a User's Roles
 * during the session.  To achieve flexibility, the ACL takes an
 * Object parameter, which the application can use to doPerform the
 * ACL.
 *
 * <p>
 *
 * This action is special in that it should only be executed by the
 * Turbine servlet.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @version $Id$
 */
public class AccessController extends Action
{
    /**
     * If there is a user and the user is logged in, doPerform will
     * set the RunData ACL.  The list is first sought from the current
     * session, otherwise it is loaded through
     * <code>TurbineSecurity.getACL()</code> and added to the current
     * session.
     *
     * @see org.apache.turbine.services.security.TurbineSecurity
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    public void doPerform( RunData data )
        throws Exception
    {
        if ( data.getUser() != null && data.getUser().hasLoggedIn() )
        {
            AccessControlList acl = (AccessControlList)
                data.getSession().getValue(AccessControlList.SESSION_KEY);
            if ( acl == null )
            {
                acl = TurbineSecurity.getACL( data.getUser() );
                data.getSession().putValue( AccessControlList.SESSION_KEY,
                                            (Object)acl );
            }
            data.setACL(acl);
        }
    }
}
