package org.apache.turbine.modules.actions.sessionvalidator;

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

import org.apache.turbine.TurbineConstants;

import org.apache.turbine.om.security.User;

import org.apache.turbine.services.resources.TurbineResources;

import org.apache.turbine.util.RunData;

/**
 * The SessionValidator attempts to retrieve the User object from the
 * Servlet API session that is associated with the request.  If the
 * data cannot be retrieved, it is handled here.  If the user has not
 * been marked as being logged into the system, the user is rejected
 * and the screen is set to the screen.homepage value in
 * TurbineResources.properties.
 *
 * <p>
 *
 * Other systems generally have a database table which stores this
 * information, but we take advantage of the Servlet API here to save
 * a hit to the database for each and every connection that a user
 * makes.
 *
 * <p>
 *
 * This action is special in that it should only be executed by the
 * Turbine servlet.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 */
public class DefaultSessionValidator extends SessionValidator
{
    /**
     * Execute the action.  The default is to populate the RunData
     * object and, if the user is unknown, to force a login screen (as
     * set in the tr.props).
     *
     * @see org.apache.turbine.modules.screens.error.InvalidState
     * @param data Turbine RunData context information.     * @exception Exception, a generic exception.
     */
    public void doPerform( RunData data ) throws Exception
    {
        /*
         * Pull user from session.
         */
        data.populate();

        // Make sure the User object exists in the Session and that
        // the user has logged into the system.
        if ( (data.getUser() == null) || (! data.getUser().hasLoggedIn()) )
        {
            // only set the message if nothing else has already set it
            // (e.g. the LogoutUser action)
            if (data.getMessage() == null)
                data.setMessage(TurbineResources.getString(
                    TurbineConstants.LOGIN_MESSAGE));
            // set the screen to be the login page
            data.setScreen(TurbineResources.getString(
                TurbineConstants.SCREEN_LOGIN));
            // we're not doing any actions buddy! (except action.login which
            // will have been performed already)
            data.setAction(null);
        }
        else if ( ! data.hasScreen() )
        {
            data.setMessage(TurbineResources.getString(
                TurbineConstants.LOGIN_MESSAGE_NOSCREEN));
            data.setScreen(TurbineResources.getString(
                TurbineConstants.SCREEN_HOMEPAGE));
        }
        else if ( data.getParameters().containsKey("_session_access_counter") )
        {
            // See comments in screens.error.InvalidState.
            if ( data.getParameters().getInt("_session_access_counter") <
                 (((Integer)data.getUser().getTemp("_session_access_counter"))
                 .intValue()-1) )
            {
                data.getUser().setTemp("prev_screen", data.getScreen());
                data.getUser().setTemp("prev_parameters", data.getParameters());
                data.setScreen( TurbineResources.getString(
                    TurbineConstants.SCREEN_INVALID_STATE) );
                data.setAction( "" );
            }
        }
    }
}
