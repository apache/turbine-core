package org.apache.turbine.om;

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

import java.util.HashMap;
import java.util.Map;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.util.pool.Recyclable;

/**
 * A Pull tool to make om objects available to a template
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @version $Id$
 */
public class OMTool implements ApplicationTool, Recyclable
{
    // private RunData data;
    private HashMap omMap;

    // note the following could be a static attribute to reduce memory
    // footprint. Might require a service to front load the
    // PullHelpers to avoid MT issues. A multiple write is not so bad
    // though

    /** The cache of PullHelpers. **/
    private static Map pullMap = new HashMap();

    /**
     *  The Factory responsible for retrieving the
     *  objects from storage
     */
    private RetrieverFactory omFactory;

    public OMTool()throws Exception
    {
        omMap = new HashMap();
        String className = TurbineResources.getString("tool.om.factory");
        //        RetrieverFactory omFactory =
        //            (RetrieverFactory)Class.forName(className).newInstance();
    }

    /**
     * Prepares tool for a single request
     */
    public void init(Object runData)
    {
        // data = (RunData)runData;
    }

    /**
     * Implementation of ApplicationTool interface is not needed for this
     * method as the tool is request scoped
     */
    public void refresh()
    {
        // empty
    }

    /**
     * Inner class to present a nice interface to the template designer
     */
    private class PullHelper
    {
        String omName;

        private PullHelper(String omName)
        {
            this.omName = omName;
        }

        public Object setKey(String key)
            throws Exception
        {
            Object om = null;

            String inputKey = omName + key;
            if (omMap.containsKey(inputKey))
            {
                om = omMap.get(inputKey);
            }
            else
            {
                om = omFactory.getInstance(omName).retrieve(key);
                omMap.put(inputKey, om);
            }

            return om;
        }
    }

    public Object get(String omName) throws Exception
    {
        if (!pullMap.containsKey(omName))
        {
            // MT could overwrite a PullHelper, but that is not a problem
            // should still synchronize to avoid two threads adding at
            // same time
            synchronized (this.getClass())
            {
                pullMap.put(omName, new OMTool.PullHelper(omName));
            }
        }

        return pullMap.get(omName);
    }

    public Object get(String omName, String key) throws Exception
    {
        return ((OMTool.PullHelper) get(omName)).setKey(key);
    }


    public String getName()
    {
        return "om";
    }


    // ****************** Recyclable implementation ************************

    private boolean disposed;

    /**
     * Recycles the object for a new client. Recycle methods with
     * parameters must be added to implementing object and they will be
     * automatically called by pool implementations when the object is
     * taken from the pool for a new client. The parameters must
     * correspond to the parameters of the constructors of the object.
     * For new objects, constructors can call their corresponding recycle
     * methods whenever applicable.
     * The recycle methods must call their super.
     */
    public void recycle()
    {
        disposed = false;
    }

    /**
     * Disposes the object after use. The method is called
     * when the object is returned to its pool.
     * The dispose method must call its super.
     */
    public void dispose()
    {
        omMap.clear();
        // data = null;
        disposed = true;
    }

    /**
     * Checks whether the recyclable has been disposed.
     * @return true, if the recyclable is disposed.
     */
    public boolean isDisposed()
    {
        return disposed;
    }
}








