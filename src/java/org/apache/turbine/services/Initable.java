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
 * Classes that implement this interface need initialization before
 * they can work.
 *
 * These classes rely also on an <code>InitableBroker</code> that
 * ensures that there is only one instance of the class in the system,
 * and handles dependencies between <code>Initables</code>.
 *
 * @author <a href="mailto:burton@apache.org">Kevin Burton</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public interface Initable
{
    /**
     * Provides an Initable with a reference to the InitableBroker
     * that instantiated this object, so that it can access other
     * Initables.
     *
     * @param broker The InitableBroker that instantiated this object.
     */
    void setInitableBroker( InitableBroker broker );

    /**
     * Performs early initailization of an Initable
     *
     * During the startup of the system, different objects may be
     * passed to your class using this method.  It should ignore any
     * objects that it doesn't need or understand.
     *
     * After the class changes its internal state so that getInit()
     * returns true, this method will be called no more, and late
     * initialization will not be performed.
     *
     * If your class relies on early initialization, and the object it
     * expects was not received, you can use late initialization to
     * throw an exception and complain.
     *
     * @param data An Object to use for initialization activities.
     * @exception InitializationException, if initilaization of this
     * class was not successful.
     */
    void init( Object data )
        throws InitializationException;

    /**
     * Performs late initialization of an Initable.
     *
     * When your class is being requested from an InitableBroker, it
     * will call getInit(), and if it returns false, this method will
     * be invoked.
     *
     * @exception InitializationException, if initialization of this
     * class was not successful.
     */
    void init( ) throws InitializationException;

    /**
     * Returns an <code>Initable</code> to an uninitialized state.
     *
     * <p>This method must release all resources allocated by the 
     * <code>Initable</code> implementation, and resetting its internal state.
     * You may chose to implement this operation or not. If you support
     * this operation, getInit() should return false after successful
     * shutdown of the service.
     */
    void shutdown( );

    /**
     * Returns initialization status of an Initable.
     *
     * @return Initialization status of an Initable.
     */
    boolean getInit( );
}
