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

import java.util.Hashtable;

import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.util.RunData;

/**
 * This is the base class for the loaders. It contains code that is
 * used across all of the loaders. It also specifies the interface
 * that is required to be called a Loader.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public abstract class GenericLoader extends Hashtable
{
    /** @serial This can be serialized */
    private boolean reload = false;
    /** @serial This can be serialized */
    private boolean isCaching = true;
    /** Base packages path for Turbine */
    private static final String TURBINE_PACKAGE = "org.apache.turbine.modules";

    /**
     * Basic constructor for creating a loader.
     */
    public GenericLoader()
    {
        super();
        isCaching = TurbineResources
                .getBoolean(TurbineConstants.MODULE_CACHE, true);
    }

    /**
     * Basic constructor for creating a loader.
     */
    public GenericLoader(int i)
    {
        super(i);
        isCaching = TurbineResources
                .getBoolean(TurbineConstants.MODULE_CACHE, true);
    }

    /**
     * If set to true, then cache the Loader objects.
     *
     * @return True if the Loader objects are being cached.
     */
    public boolean cache()
    {
        return this.isCaching;
    }

    /**
     * Attempts to load and execute the external action that has been
     * set.
     *
     * @exception Exception a generic exception.
     */
    public abstract void exec(RunData data, String name)
            throws Exception;

    /**
     * Commented out.
     * This method should return the complete classpath + name.
     *
     * @param name
     * @return
     *
     public abstract String getClassName(String name);
     */

    /**
     * Returns whether or not this external action is reload itself.
     * This is in cases where the Next button would be clicked, but
     * since we are checking for that, we would go into an endless
     * loop.
     *
     * @return True if the action is reload.
     */
    public boolean reload()
    {
        return this.reload;
    }

    /**
     * Sets whether or not this external action is reload itself.
     * This is in cases where the Next button would be clicked, but
     * since we are checking for that, we would go into an endless
     * loop.
     *
     * @param reload True if the action must be marked as reload.
     * @return Itself.
     */
    public GenericLoader setReload(boolean reload)
    {
        this.reload = reload;
        return this;
    }

    /**
     * Gets the base package where Turbine should find its default
     * modules.
     *
     * @return A String with the base package name.
     */
    public static String getBasePackage()
    {
        return TURBINE_PACKAGE;
    }
}
