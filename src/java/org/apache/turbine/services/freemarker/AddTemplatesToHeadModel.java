package org.apache.turbine.services.freemarker;

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

// Java stuff.
import java.util.*;
import java.io.*;

// Turbine Utility Classes
import org.apache.turbine.modules.*;
import org.apache.turbine.util.*;
import org.apache.turbine.services.*;
import org.apache.turbine.services.resources.*;
import org.apache.ecs.html.*;

// FreeMarker Classes
import freemarker.template.*;

/**
 * Returns output of a Navigation module.  Extension of FreeMarker.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @version $Id$
 * @deprecated
 */
public class AddTemplatesToHeadModel
    implements TemplateMethodModel
{
    private RunData data;

    /**
     * Constructor.
     *
     * @param data Turbine information.
     */
    public AddTemplatesToHeadModel(RunData data)
    {
        this.data = data;
    }

    /**
     * Method called by FreeMarker Template.
     *
     * @param args A List of Strings passed from the template.
     * @return A TemplateModel with an empty SimpleScalar("").
     * @exception TemplateModelException.
     */
    public TemplateModel exec(List args)
        throws TemplateModelException
    {
        Head head = data.getPage().getHead();
        String returnValue = "";
        String serviceName = TurbineResources.getString("services.freemarker");
        FreeMarkerService fm = (FreeMarkerService)
            TurbineServices.getInstance().getService(serviceName);
        SimpleHash context = fm.getContext(data);

        for (int i=0; i<args.size(); i++)
        {
            String name = (String)args.get(i);
            try
            {
                head.addElement( fm.handleRequest(context, name, true) );
                head.addElement("\n");
            }
            // We cannot pass this exception, because this methods overrides
            // a method from interface defined by FreeMarker.
            catch(TurbineException te)
            {
                // make sure the stack trace makes it to the log
                Log.error(te);

                throw new TemplateModelException("An error occured while rendering template "+name+":\n"+te);
            }
        }
        return new SimpleScalar(returnValue);
    }

    /**
     * Required method in TemplateMethodModel, not implemented.
     *
     * @return Always false.
     */
    public boolean isEmpty()
    {
        return false;
    }


    /*
     * This unfinished method loads the file from filesytem instead of
     * cache.

    //
    // Required method in TemplateMethodModel, not implemented.
    //
    // @param args A List of Strings passed from the template.
    // @return A TemplateModel with an empty SimpleScalar("").
    // @exception TemplateModelException.
    //
    public TemplateModel exec(ArgumentList args)
        throws TemplateModelException
    {
        Head head = data.getPage().getHead();
        String returnValue = "";
        for (int i=0; i<args.size(); i++)
        {
            String serviceName = TurbineResources.getString("services.freemarker");
            FreeMarkerService fm = (FreeMarkerService)
                TurbineServices.getInstance().getService(serviceName);
            String path = fm.getBasePath();
            File file = null;
            FileReader reader = null;
            BufferedReader br = null;
            try
            {
                file = new File(path, (String)args.get(i));
                reader = new FileReader(file);
                br = new BufferedReader(reader);
                String line = null;
                do
                {
                    line = br.readline();
                    if (line != null)
                    {
                        head.addElement(line);
                    }
                }
                while (line != null);
                head.addElement("\n");
            }
            return new SimpleScalar(returnValue);
        }
    }
    */
}
