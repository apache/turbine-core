package org.apache.turbine.modules;

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

// Java Core Classes
import java.util.Vector;

// Turbine Utility Clases
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.assemblerbroker.AssemblerBrokerService;
import org.apache.turbine.util.ObjectUtils;
import org.apache.turbine.util.RunData;


/**
 * The purpose of this class is to allow one to load and execute Page
 * modules.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 */
public class PageLoader extends GenericLoader
{
    /** The single instance of this class. */
    private static PageLoader instance = new PageLoader(
        TurbineResources.getInt(TurbineConstants.PAGE_CACHE_SIZE,5));

    /**
     * These ctor's are private to force clients to use getInstance()
     * to access this class.
     */
    private PageLoader()
    {
        super();
    }

    /**
     * These ctor's are private to force clients to use getInstance()
     * to access this class.
     */
    private PageLoader(int i)
    {
        super(i);
    }

    /**
     * Adds an instance of an object into the hashtable.
     *
     * @param name Name of object.
     * @param page Page to be associated with name.
     */
    private void addInstance( String name, Page page )
    {
        if ( cache() )
            this.put( name, (Page) page );
    }

    /**
     * Attempts to load and execute the external page.
     *
     * @param data Turbine information.
     * @param name Name of object that will execute the page.
     * @exception Exception a generic exception.
     */
    public void exec( RunData data, String name )
        throws Exception
    {
        // Execute page
        getInstance(name).build(data);
    }

    /**
     * Pulls out an instance of the object by name.  Name is just the
     * single name of the object.
     *
     * @param name Name of object instance.
     * @return A Page with the specified name, or null.
     * @exception Exception a generic exception.
     */
    public Page getInstance(String name)
        throws Exception
    {
        Page page = null;

        if ( cache() && this.containsKey( name ) )
        {
            page = (Page) this.get( name );
        }
        else
        {
            // We get the broker service
            AssemblerBrokerService ab =
                (AssemblerBrokerService)TurbineServices.getInstance()
                .getService (AssemblerBrokerService.SERVICE_NAME);

            try
            {
                // Attempt to load the screen
                page = (Page) ab.getAssembler(
                    AssemblerBrokerService.PAGE_TYPE,name);
            }
            catch (ClassCastException cce)
            {
                // This can alternatively let this exception be thrown
                // So that the ClassCastException is shown in the
                // browser window.  Like this it shows "Screen not Found"
                page = null;
            }

            if (page == null)
            {
                // If we did not find a screen we should try and give
                // the user a reason for that...
                // FIX ME: The AssemblerFactories should each add 
                //         it's own string here...
                Vector packages = TurbineResources.getVector(
                    TurbineConstants.MODULE_PACKAGES);
                ObjectUtils.addOnce( packages,
                                     GenericLoader.getBasePackage() );

                throw new ClassNotFoundException(
                    "\n\n\tRequested Page not found: " + name + "\n" +
                    "\tTurbine looked in the following modules.packages " +
                    "path: \n\t" + packages.toString() + "\n");
            }
            else if ( cache() )
            {
                // The new instance is added to the cache
                addInstance( name, page );
            }
        }
        return page;
    }

    /**
     * The method through which this class is accessed.
     *
     * @return The single instance of this class.
     */
    public static PageLoader getInstance()
    {
        return instance;
    }
}
