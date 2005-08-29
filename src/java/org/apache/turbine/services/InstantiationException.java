package org.apache.turbine.services;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.apache.turbine.util.TurbineRuntimeException;

/**
 * Thrown by <code>InitableBroker</code> and
 * <code>ServiceBroker</code> classes to indicate problems with
 * instatiation of requested objects.
 *
 * Make sure you don't confuse this exception with the java.lang.InstantiationException.
 *
 * @author <a href="mailto:burton@apache.org">Kevin Burton</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 * @see org.apache.turbine.services.Initable
 */
public class InstantiationException extends TurbineRuntimeException
{
    /**
     * Construct an InstantiationException with specified detail
     * message.
     *
     * @param msg The detail message.
     */
    public InstantiationException(String msg)
    {
        super(msg);
    }

    /**
     * Construct an InstantiatioException with specified detail message
     * and nested Throwable.
     *
     * @param msg The detail message.
     * @param nested the exception or error that caused this exception
     *               to be thrown.
     */
    public InstantiationException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
