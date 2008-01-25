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
    public void setInitableBroker(InitableBroker broker)
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
    public void init(Object data) throws InitializationException
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
    protected void setInit(boolean value)
    {
        this.isInitialized = value;
    }
}
