package org.apache.turbine.modules.screens.error;


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


import org.apache.ecs.ElementContainer;
import org.apache.ecs.html.A;
import org.apache.fulcrum.parser.ParameterParser;
import org.apache.turbine.modules.Screen;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
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
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class InvalidState
    extends Screen
{
    /**
     * Build the Screen.
     *
     * @param pipelineData Turbine information.
     * @exception Exception, a generic exception.
     */
    @Override
    public String doBuild(PipelineData pipelineData)
            throws Exception
    {
        RunData data = getRunData(pipelineData);
        ElementContainer body = new ElementContainer();
        ElementContainer message = new ElementContainer();

        StringBuilder sb = new StringBuilder();
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
        return body.toString();
    }
}
