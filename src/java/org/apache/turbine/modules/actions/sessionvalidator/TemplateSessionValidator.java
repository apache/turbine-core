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

import org.apache.commons.configuration.Configuration;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;

import org.apache.turbine.services.security.TurbineSecurity;

import org.apache.turbine.util.RunData;

/**
 * SessionValidator for use with the Template Service, the
 * TemplateSessionValidator is virtually identical to the
 * TemplateSecureValidator except that it does not transfer to the
 * login page when it detects a null user (or a user not logged in).
 *
 * <p>The Template Service requires a different Session Validator
 * because of the way it handles screens.
 *
 * <p>Note that you will need to set the template.login property to the
 * login template.
 *
 * @see TemplateSecureSessionValidator
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TemplateSessionValidator
    extends SessionValidator
{
    /** Logging */
    private static Log log = LogFactory.getLog(TemplateSessionValidator.class);

    /**
     * Execute the action.
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    public void doPerform(RunData data)
        throws Exception
    {
        Configuration conf = Turbine.getConfiguration();

        /*
         * Pull user from session.
         */
        data.populate();

        // The user may have not logged in, so create a "guest/anonymous" user.
        if (data.getUser() == null)
        {
            log.debug("Fixing up empty User Object!");
            data.setUser(TurbineSecurity.getAnonymousUser());
            data.save();
        }

        // make sure we have some way to return a response
        if (!data.hasScreen() &&
            StringUtils.isEmpty(data.getTemplateInfo().getScreenTemplate()))
        {
            String template = conf.getString(TurbineConstants.TEMPLATE_HOMEPAGE);

            if (StringUtils.isNotEmpty(template))
            {
                data.getTemplateInfo().setScreenTemplate(template);
            }
            else
            {
                data.setScreen(conf.getString(TurbineConstants.SCREEN_HOMEPAGE));
            }
        }
        // the session_access_counter can be placed as a hidden field in
        // forms.  This can be used to prevent a user from using the
        // browsers back button and submitting stale data.
        else if (data.getParameters().containsKey("_session_access_counter"))
        {
            // See comments in screens.error.InvalidState.
            if (data.getParameters().getInt("_session_access_counter") <
                    (((Integer) data.getUser().getTemp("_session_access_counter"))
                    .intValue() - 1))
            {
                if (data.getTemplateInfo().getScreenTemplate() != null)
                {
                    data.getUser().setTemp("prev_template",
                                           data.getTemplateInfo().getScreenTemplate()
                                           .replace('/', ','));
                    data.getTemplateInfo().setScreenTemplate(conf.getString(TurbineConstants.TEMPLATE_INVALID_STATE));
                }
                else
                {
                    data.getUser().setTemp("prev_screen",
                                           data.getScreen().replace('/', ','));
                    data.setScreen(conf.getString(TurbineConstants.SCREEN_INVALID_STATE));
                }
                data.getUser().setTemp("prev_parameters", data.getParameters());
                data.setAction("");
            }
        }

        // we do not want to allow both a screen and template parameter.
        // The template parameter is dominant.
        if (data.getTemplateInfo().getScreenTemplate() != null)
        {
            data.setScreen(null);
        }
    }
}
