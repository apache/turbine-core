package org.apache.turbine.modules.screens;

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

// Turbine/Village/ECS Imports
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.StringElement;
import org.apache.turbine.services.freemarker.FreeMarkerService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.util.RunData;

// FreeMarker Stuff
import freemarker.template.SimpleHash;


/**
 * Sample FreeMarker Screen.  Screens which use FreeMarker templates
 * can extend this screen.
 *
 * For templates which require no database content, this screen may be
 * adequate.  In other cases, screens can override the
 * doBuildFreeMarker method to add more data to the context.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @version $Id$
 * @deprecated you should use velocity
 */
public class BaseFreeMarkerScreen extends TemplateScreen
{
    /**
     * Screens extending this class should overide this method to
     * perform any particular business logic and add information to
     * the context.
     *
     * @param data, the Rundata object
     * @param context, the context into which the extra data is to be added.
     * @exception Exception, a generic exception.
     */
    protected void doBuildTemplate(RunData data, SimpleHash context)
        throws Exception
    {
    }


    /**
     * Needs to be implemented to make TemplateScreen like us.  The
     * actual method that you should override is the one with the
     * context in the parameter list.
     *
     * @param data, the Rundata object
     * @exception Exception, a generic exception.
     */
    protected void doBuildTemplate( RunData data )
        throws Exception
    {
        doBuildTemplate( data, getContext(data) );
    }

    /**
     * Return the model needed by FreeMarker.  This is where
     * information needed by the template should be placed.
     *
     * @param data, the Rundata object
     * @return SimpleHash
     */
    protected SimpleHash getContext(RunData data)
    {
        // Attempt to get it from the TemplateInfo first.  If it
        // doesn't exist, create it and then stuff it into the
        // TemplateInfo.
        SimpleHash context = (SimpleHash)data.getTemplateInfo()
            .getTemplateContext(FreeMarkerService.CONTEXT);
        if (context == null)
        {
            FreeMarkerService fm = (FreeMarkerService)TurbineServices
                .getInstance().getService(FreeMarkerService.SERVICE_NAME);
            context = fm.getContext();
            data.getTemplateInfo()
                .setTemplateContext(FreeMarkerService.CONTEXT, context);
        }
        return context;
    }

    /**
     * Build the template.
     *
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @exception Exception, a generic exception.
     */
    public ConcreteElement buildTemplate(RunData data)
        throws Exception
    {
        SimpleHash context = getContext(data);
        String templateName = TurbineTemplate.getScreenTemplateName(
            data.getTemplateInfo().getScreenTemplate() );

        // Template service adds the leading slash, but make it sure.
        if ((templateName.length() > 0) &&
            (templateName.charAt(0) != '/'))
        {
            templateName = '/' + templateName;
        }

        FreeMarkerService fm = (FreeMarkerService)
            TurbineServices.getInstance()
            .getService(FreeMarkerService.SERVICE_NAME);

        StringElement output = new StringElement();
        output.setFilterState(false);
        output.addElement(
            fm.handleRequest(context, "screens" + templateName, true));
        return output;
    }
}
