package org.apache.turbine.services;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
     * @exception InitializationException if initialization of this
     * class was not successful.
     */
    void initClass(String className, Object data)
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
    void shutdownClass(String className);

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
     * @exception InstantiationException if there was a problem
     * during instantiation or initialization of the Initable.
     */
    Initable getInitable(String className) throws InstantiationException;
}
