package org.apache.turbine.modules.layouts;

/*
 * Copyright (c) 1997-1999 The Java Apache Project.  All rights reserved.
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
 * 3. All advertising materials mentioning features or use of this
 *    software must display the following acknowledgment:
 *    "This product includes software developed by the Java Apache
 *    Project for use in the Apache JServ servlet engine project
 *    <http://java.apache.org/>."
 *
 * 4. The names "Apache JServ", "Apache JServ Servlet Engine", "Turbine",
 *    "Apache Turbine", "Turbine Project", "Apache Turbine Project" and
 *    "Java Apache Project" must not be used to endorse or promote products
 *    derived from this software without prior written permission.
 *
 * 5. Products derived from this software may not be called "Apache JServ"
 *    nor may "Apache" nor "Apache JServ" appear in their names without
 *    prior written permission of the Java Apache Project.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by the Java Apache
 *    Project for use in the Apache JServ servlet engine project
 *    <http://java.apache.org/>."
 *
 * THIS SOFTWARE IS PROVIDED BY THE JAVA APACHE PROJECT "AS IS" AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE JAVA APACHE PROJECT OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Java Apache Group. For more information
 * on the Java Apache Project and the Apache JServ Servlet Engine project,
 * please see <http://java.apache.org/>.
 *
 */

// JDK Imports
import java.io.StringReader;

// Turbine/Village/ECS Imports
import org.apache.ecs.ConcreteElement;
import org.apache.turbine.modules.Layout;
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.template.TemplateNavigation;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.services.xslt.TurbineXSLT;

// Velocity Stuff
import org.apache.velocity.context.Context;

/*
 * This Layout module allows Velocity XML templates to be used as layouts.
 * <br><br>
 * Once the (XML) screen and navigation templates have been inserted into
 * the layout template the result is transformed with a XSL stylesheet.
 * The stylesheet (with the same name than the screen template) is loaded
 * and executed by the XSLT service, so it is important that you correctly
 * set up your XSLT service.  If the named stylsheet does not exist the
 * default.xsl stylesheet is executed.  If default.xsl does not exist
 * the XML is merely echoed.
 * <br><br>
 * Since dynamic content is supposed to be primarily
 * located in screens and navigations there should be relatively few reasons
 * to subclass this Layout.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 */
public class VelocityXslLayout extends Layout
{
    /**
     * Method called by LayoutLoader.
     *
     * @param RunData
     * @return processed template in a String
     */
    public void doBuild( RunData data ) throws Exception
    {
        long start = System.currentTimeMillis();

        data.getResponse().setContentType ("text/html");
        Context context = TurbineVelocity.getContext( data );
        String returnValue = "";

        /*
         * First, generate the screen and put it in the context so
         * we can grab it the layout template.
         */

        ConcreteElement results = ScreenLoader.getInstance()
            .eval(data, data.getScreen());
        
        if (results != null)
        {
            returnValue = results.toString();
        }            

        /* 
         * variable for the screen in the layout template 
         */
        context.put("screen_placeholder", returnValue);

        /* 
         * variable to reference the navigation screen in 
         * the layout template 
         */
        context.put("navigation", new TemplateNavigation( data ));

        /* 
         * Grab the layout template set in the WebMacroSitePage.  
         * If null, then use the default layout template 
         * (done by the TemplateInfo object)  
         */
        String templateName = data.getTemplateInfo().getLayoutTemplate();

        /* 
         * Now, generate the layout template. 
         */
        String temp = TurbineVelocity.handleRequest(context,
            "layouts" + templateName);

        /* 
         * Finally we do a transformation and send the result
         * back to the browser 
         */
        TurbineXSLT.transform (data.getTemplateInfo().getScreenTemplate(),
            new StringReader(temp), data.getOut());
    }
}
