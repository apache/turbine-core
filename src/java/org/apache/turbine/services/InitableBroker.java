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
 * Classes that implement this interface can act as a broker for
 * <code>Initable</code> classes.
 *
 * Functionality provided by the broker includes:
 *
 * <ul>
 *
 * <li>Maintaining a single instance of each <code>Initable</code> in
 * the system.</li>
 *
 * <li>Early initialization of <code>Initables</code> during system
 * startup.</li>
 *
 * <li>Late initialization of <code>Initables</code> before they are
 * used.</li>
 *
 * <li>Providing instances of <code>Initables</code> to requesting
 * parties.</li>
 *
 * <li>Maintainging dependencies between <code>Initables</code> during
 * early initalization phases, including circular dependencies
 * detection.</li>
 *
 * </ul>
 *
 * @author <a href="mailto:burton@apache.org">Kevin Burton</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public interface InitableBroker
{
    /**
     * Performs early initialization of an Initable class.
     *
     * If your class depends on another Initable being initialized to
     * perform early initialization, you should always ask your broker
     * to initialize the other class with the objects that are passed
     * to you, before you try to retrieve that Initable's instance with
     * getInitable().
     *
     * @param className The name of the class to be initailized.
     * @param data An object to be used for initialization activities.
     * @exception InitializationException, if initialization of this
     * class was not successful.
     */
    void initClass( String className,
                    Object data )
        throws InitializationException;

    /**
     * Shutdowns an Initable class.
     *
     * This method is used to release resources allocated by an 
     * Initable class, and return it to initial (uninitailized)
     * state.
     *
     * @param className The name of the class to be uninitialized.
     */
    void shutdownClass( String className );

    /**
     * Provides an instance of Initable class ready to work.
     *
     * If the requested class couldn't be instatiated or initialized,
     * InstantiationException will be thrown.  You needn't handle this
     * exception in your code, since it indicates fatal
     * misconfigurtion of the system.
     *
     * @param className The name of the Initable requested.
     * @return An instance of requested Initable.
     * @exception InstantiationException, if there was a problem
     * during instantiation or initialization of the Initable.
     */
    Initable getInitable( String className )
        throws InstantiationException;
}
