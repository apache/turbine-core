package org.apache.turbine.modules.screens.error;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.ecs.ConcreteElement;
import org.apache.ecs.ElementContainer;

import org.apache.ecs.html.A;

import org.apache.turbine.modules.Screen;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.parser.ParameterParser;
import org.apache.turbine.util.uri.TurbineURI;

/**
 * Users will get this screen if the screen on their browser is in an
 * invalid state.  For example, if they hit "Back" or "Reload" and
 * then try to submit old form data.
 *
 * If you want one of your screens to check for invalid state
 * then add a hidden form field called "_session_access_counter"
 * with the value currently stored in the session.  The
 * SessionValidator action will check to see if it is an old
 * value and redirect you to this screen.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class InvalidState
    extends Screen
{
    /**
     * Build the Screen.
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    public ConcreteElement doBuild(RunData data)
            throws Exception
    {
        ElementContainer body = new ElementContainer();
        ElementContainer message = new ElementContainer();

        StringBuffer sb = new StringBuffer();
        sb.append("<b>There has been an error.</b>")
                .append("<p>")
                .append("- If you used the browser \"Back\" or \"Reload\"")
                .append(" buttons please use the navigation buttons we provide")
                .append(" within the screen.")
                .append("<p>")
                .append("Please click ");

        message.addElement(sb.toString());
        ParameterParser pp;
        pp = (ParameterParser) data.getUser().getTemp("prev_parameters");
        pp.remove("_session_access_counter");

        TurbineURI back = new TurbineURI(data,(String) data.getUser().getTemp("prev_screen"));
        back.addPathInfo(pp);
        message.addElement(new A().setHref(back.getRelativeLink()).addElement("here"));

        message.addElement(" to return the the screen you were working on.");

        body.addElement(message);
        return body;
    }
}
