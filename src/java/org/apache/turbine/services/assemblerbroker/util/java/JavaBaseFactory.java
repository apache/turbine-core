package org.apache.turbine.services.assemblerbroker.util.java;

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

import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.Assembler;
import org.apache.turbine.modules.GenericLoader;
import org.apache.turbine.services.assemblerbroker.util.AssemblerFactory;
import org.apache.turbine.util.ObjectUtils;

/**
 * A screen factory that attempts to load a java class from
 * the module packages defined in the TurbineResource.properties.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public abstract class JavaBaseFactory
    implements AssemblerFactory
{
    /** A vector of packages. */
    private static Vector packages = 
        Turbine.getConfiguration().getVector(TurbineConstants.MODULE_PACKAGES);

    /** Logging */
    private static Log log = LogFactory.getLog(JavaBaseFactory.class);

    static
    {
        ObjectUtils.addOnce(packages, GenericLoader.getBasePackage());
        log.debug("Added the following packages: " + packages);
    }

    /**
     * Get an Assembler.
     *
     * @param packageName java package name
     * @param name name of the requested Assembler
     * @return an Assembler
     */
    public Assembler getAssembler(String packageName, String name)
    {
        Assembler assembler = null;
        
        log.debug("Class Fragment is " + name);

        if (StringUtils.isNotEmpty(name))
        {
            int dotIndex = name.lastIndexOf('.');

            if (dotIndex > 0)
            {
                //
                // Convert Foo.Bar.Baz ---> foo.bar.Baz
                StringBuffer nameBuffer = new StringBuffer();
                nameBuffer.append(name.substring(0, dotIndex).toLowerCase());
                nameBuffer.append('.');
                nameBuffer.append(name.substring(dotIndex + 1));
                name = nameBuffer.toString();
            }

            log.debug("Class Fragment now " + name);

            for (Iterator it = packages.iterator(); it.hasNext();)
            {
                StringBuffer className = new StringBuffer();
            
                className.append(it.next());
                className.append('.');
                className.append(packageName);
                className.append('.');
                className.append(name);
            
                log.debug("Trying " + className);

                try
                {
                    Class servClass = Class.forName(className.toString());
                    assembler = (Assembler) servClass.newInstance();
                    break; // for()
                }
                catch (ClassNotFoundException cnfe)
                {
                    // Do this so we loop through all the packages.
                    log.debug(className + ": Not found");
                }
                catch (NoClassDefFoundError ncdfe)
                {
                    // Do this so we loop through all the packages.
                    log.debug(className + ": No Class Definition found");
                }
                catch (ClassCastException cce)
                {
                    // This means trouble!
                    // Alternatively we can throw this exception so
                    // that it will appear on the client browser
                    log.error(cce);
                    break; // for()
                }
                catch (InstantiationException ine)
                {
                    // This means trouble!
                    // Alternatively we can throw this exception so
                    // that it will appear on the client browser
                    log.error(ine);
                    break; // for()
                }
                catch (IllegalAccessException ilae)
                {
                    // This means trouble!
                    // Alternatively we can throw this exception so
                    // that it will appear on the client browser
                    log.error(ilae);
                    break; // for()
                }
                // With ClassCastException, InstantiationException we hit big problems
            }
        }
        log.debug("Returning: " + assembler);

        return assembler;
    }
}
