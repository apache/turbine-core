package org.apache.turbine.services;

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

/**
 * This class provides a generic implementation of
 * <code>Initable</code>.  This implementation, that other
 * <code>Initables</code> are welcome to extend, contains facilities
 * to maintain internal state.
 *
 * @author <a href="mailto:burton@apache.org">Kevin Burton</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public class BaseInitable
    implements Initable
{
    /** InitableBroker that instantiatd this class. */
    protected InitableBroker initableBroker;

    /** Initialization status of this class. */
    protected boolean isInitialized = false;

    /**
     * Default constructor of BaseInitable.
     *
     * This constructor does nothing.  Your own constructurs should be
     * modest in allocating memory and other resources, leaving this
     * to the <code>init()</code> method.
     */
    public BaseInitable()
    {
    }

    /**
     * Saves InitableBroker reference for later use.
     *
     * @param broker The InitableBroker that instantiated this object.
     */
    public void setInitableBroker( InitableBroker broker )
    {
        this.initableBroker = broker;
    }

    /**
     * Returns an InitableBroker reference.
     *
     * @return The InitableBroker that instantiated this object.
     */
    public InitableBroker getInitableBroker()
    {
        return initableBroker;
    }

    /**
     * Performs early initialization.  Used in a manner similar to a ctor.
     *
     * BaseInitable doesn't need early initialization, therefore it
     * ignores all objects passed to it and performs no initialization 
     * activities.
     *
     * @param data An Object to use for initialization activities.
     * @exception InitializationException Initialization of this
     * class was not successful.
     */
    public void init( Object data ) throws InitializationException
    {
    }

    /**
     * Performs late initializtion.  Called when the Service is requested 
     * for the first time (if not already completely initialized by the 
     * early initializer).
     *
     * Late intialization of a BaseInitable is alwas successful.
     *
     * @exception InitializationException Initialization of this
     * class was not successful.
     */
    public void init() throws InitializationException
    {
    }

    /**
     * Returns an Initable to uninitialized state.
     *
     * Calls setInit(false) to mark that we are no longer in initialized
     * state.
     */
    public void shutdown()
    {
        setInit(false);
    }

    
    /**
     * Returns initialization status.
     *
     * @return True if the initable is initialized.
     */
    public boolean getInit()
    {
        return isInitialized;
    }

    /**
     * Sets initailization status.
     *
     * @param value The new initialization status.
     */
    protected void setInit( boolean value )
    {
        this.isInitialized = value;
    }
}
