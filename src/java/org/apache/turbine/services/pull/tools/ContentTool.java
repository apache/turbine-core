package org.apache.turbine.services.pull.tools;

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

import org.apache.turbine.services.pull.ApplicationTool;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.uri.DataURI;

/**
 * Terribly simple tool to translate URIs into Turbine Links.
 * Equivalent to URIUtils.getAbsoluteLink() in a pull tool.
 * 
 * <p>
 * If you're missing any routines from the 'old' $content tool concerning
 * path_info or query data, you did use the wrong tool then. You should've used
 * the TemplateLink tool which should be available as "$link" in your context.
 * <p>
 *
 * This is an application pull tool for the template system. You should <b>not</b>
 * use it in a normal application!
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class ContentTool
    implements ApplicationTool
{
    /** Caches a DataURI object which provides the translation routines */
    private DataURI dataURI = null;
    
    /**
     * C'tor
     */
    public ContentTool()
    {
    }

    /*
     * ========================================================================
     *
     * Application Tool Interface
     *
     * ========================================================================
     *
     */

    /**
     * This will initialise a ContentTool object that was
     * constructed with the default constructor (ApplicationTool
     * method).
     *
     * @param data assumed to be a RunData object
     */
    public void init(Object data)
    {
        // we just blithely cast to RunData as if another object
        // or null is passed in we'll throw an appropriate runtime
        // exception.
        dataURI = new DataURI((RunData) data);
    }

    /**
     * Refresh method - does nothing
     */
    public void refresh()
    {
        // empty
    }

    /**
     * Returns the Turbine URI of a given Path
     *
     * @param path The path to translate
     *
     * @return Turbine translated absolute path
     */
    public String getURI(String path)
    {
        dataURI.setScriptName(path);

        return dataURI.getAbsoluteLink();
    }
}
