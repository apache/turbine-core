package org.apache.turbine.services.bsf;

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

import com.ibm.bsf.BSFManager;
import com.ibm.bsf.BSFException;
import com.ibm.bsf.util.CodeBuffer;

import org.apache.turbine.services.TurbineBaseService;

/**
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 */
public class TurbineBSFService extends TurbineBaseService
    implements BSFService
{
    /**
     * BSF manager that is responsible for executing scripts.
     * This may eventually be a pool of managers and 
     * utilize the pool service.
     */
    protected BSFManager manager = null;
    
    /**
     * Compile or interpret the scripts we are running.
     */
    protected boolean compile = false;
    
    /**
     * Initialize the TurbineBSF Service.
     */
    public void init()
    {
        initBSFManagers();
        setInit(true);
    }

    private void initBSFManagers()
    {
        manager = new BSFManager();
    }
    
    /**
     * Execute a script. The script can be in any of
     * the scripting languages supported by the BSF.
     *
     * @param String name of the script
     */
    public void execute(String script)
    {
        try
        {
            String language = manager.getLangFromFilename(script);
            
            if (compile)
            {
                /*
                 * Compiling in BSF is not exactly what I 
                 * thought it would be, these two lines below
                 * produce Java source code not a Java class
                 * file. There are scripting engines that will
                 * produce bytecode, like Jython, but I don't
                 * think BSF takes advantage of this yet.
                 */
                CodeBuffer cb = new CodeBuffer();
                manager.compileScript(language, script, 0, 0, "", cb);
            }
            else
            {
                manager.exec(language, script, 0, 0, "");
            }
        }
        catch (BSFException bsfe)
        {
            /*
             * Find out what went wrong with the processing
             * of the script.
             */
            int reason = bsfe.getReason();
        }
    }
}
