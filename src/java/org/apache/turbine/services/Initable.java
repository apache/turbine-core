package org.apache.turbine.services;


/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


/**
 * Classes that implement this interface need initialization before
 * they can work.
 *
 * @author <a href="mailto:burton@apache.org">Kevin Burton</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public interface Initable
{
    /**
     * Performs early initialization of an Initable
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
     * Default: do nothing
     * 
     * @param data An Object to use for initialization activities.
     * @throws InitializationException if initialization of this
     * class was not successful.
     */
    default void init(Object data) throws InitializationException {}

    /**
     * Performs late initialization of an Initable.
     * 
     * When your class is being requested from an InitableBroker, it
     * will call getInit(), and if it returns false, this method will
     * be invoked.
     *
     * Default: do nothing
     * 
     * @throws InitializationException if initialization of this
     * class was not successful.
     */
    default void init() throws InitializationException {}

    /**
     * Returns an <code>Initable</code> to an uninitialized state.
     *
     * <p>This method must release all resources allocated by the
     * <code>Initable</code> implementation, and resetting its internal state.
     * You may chose to implement this operation or not. If you support
     * this operation, getInit() should return false after successful
     * shutdown of the service.
     * 
     * Default: setInit(false)
     */
    default void shutdown() {
        setInit(false);
    }

    /**
     * Returns initialization status of an Initable.
     *
     * @return Initialization status of an Initable.
     */
    boolean getInit();

    /**
     * Sets initialization status.
     * 
     * @param value The new initialization status.
     */
    void setInit(boolean value);
}
