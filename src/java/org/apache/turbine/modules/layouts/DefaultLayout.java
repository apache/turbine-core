package org.apache.turbine.modules.layouts;

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

// Turbine Classes
import org.apache.turbine.modules.Layout;
import org.apache.turbine.modules.NavigationLoader;
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.util.RunData;

// ECS Classes
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.HtmlColor;
import org.apache.ecs.html.Font;
import org.apache.ecs.html.P;


/**
 * This is an example Layout module that is executed by default.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 * @deprecated The use of ECS for the view is deprecated. Use a templating solution.
 */
public class DefaultLayout extends Layout
{
    /**
     * Build the layout.
     *
     * <p><em>NOTE: Unless otherwise specified, the page background
     * defaults to 'white'</em></p>
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    public void doBuild( RunData data ) throws Exception
    {
        // Execute the Top Navigation portion for this Layout.
        ConcreteElement topNav =
            NavigationLoader.getInstance().eval(data, "DefaultTopNavigation");

        if ( topNav != null)
        {
            data.getPage().getBody().addElement( topNav );
        }

        // If an Action has defined a message, attempt to display it
        // here.
        if ( data.getMessage() != null )
        {
            data.getPage().getBody().addElement(new P())
                .addElement(new Font().setColor(HtmlColor.red)
                    .addElement(data.getMessageAsHTML()));
        }

        // Now execute the Screen portion of the page.
        ConcreteElement screen = ScreenLoader.getInstance()
            .eval(data, data.getScreen());

        if (screen != null)
        {
            data.getPage().getBody().addElement( screen );
        }

        // The screen should have attempted to set a Title for itself,
        // otherwise, a default title is set.
        data.getPage().getTitle().addElement(data.getTitle());

        // The screen should have attempted to set a Body bgcolor for
        // itself, otherwise, a default body bgcolor is set.
        data.getPage().getBody().setBgColor(HtmlColor.white);

        // Execute the Bottom Navigation portion for this Layout.
        ConcreteElement bottomNav =
        NavigationLoader.getInstance().eval ( data, "DefaultBottomNavigation" );

        if ( bottomNav != null)
        {
            data.getPage().getBody().addElement( bottomNav );
        }
    }
}
