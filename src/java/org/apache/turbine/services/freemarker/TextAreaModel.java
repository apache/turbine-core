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

import java.util.List;

// Turbine Utility Classes
import org.apache.turbine.util.*;
import org.apache.ecs.html.*;

// FreeMarker Classes
import freemarker.template.*;

/**
 * Creates a TEXTAREA html tag.  The rows, columns, and wrap attribute
 * can be passed in through a list. This object is intended to be used
 * by applications, so that the name and value attributes will not
 * accidently be altered by designers.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @version $Id$
 * @deprecated
 */
public class TextAreaModel
    extends TextArea
    implements TemplateMethodModel
{
    /**
     * Creates a textarea html tag.
     *
     * @param name The input name attribute.
     * @param value The text between the <textarea> and </textarea>
     * tags.
     */
    public TextAreaModel(String name,
                         String value)
    {
        super();
        setName(name);
        addElement(value);
    }

    /**
     * Method called by FreeMarker during template parsing.
     *
     * @param args A List of Strings passed from the template to set,
     * in order, the row and column attributes, and optionally the
     * wrap attribute.
     * @return <textarea name="name" rows="args[0]" columns="args[1]"
     * wrap="args[2]">value</textarea>
     * @exception TemplateModelException.
     */
    public TemplateModel exec(List args)
        throws TemplateModelException
    {
        if (args.size() > 1)
        {
            String rows = (String)args.get(0);
            String columns = (String)args.get(1);
            try
            {
                Integer.parseInt(rows);
                Integer.parseInt(columns);
                setRows(rows);
                setCols(columns);
            }
            catch (Exception e) {}
            if (args.size() > 2)
            {
                setWrap((String)args.get(2));
            }
        }
        return new SimpleScalar( toString() );
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
}
