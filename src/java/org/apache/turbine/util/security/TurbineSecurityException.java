package org.apache.turbine.util.security;

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

import org.apache.turbine.util.TurbineException;

/**
 * Thrown by SecurityService methods to indicate various problems.
 *
 * @version $Id$
 */
public class TurbineSecurityException
        extends TurbineException
{
    /**
     * Construct an SecurityException with specified detail message.
     *
     * @param msg The detail message.
     */
    public TurbineSecurityException(String msg)
    {
        super(msg);
    }

    /**
     * Construct an SecurityException with specified detail message
     * and nested <code>Throwable</code>.
     *
     * @param msg The detail message.
     * @param nested the exception or error that caused this exception
     *               to be thrown.
     */
    public TurbineSecurityException(String msg, Throwable nested)
    {
        super(msg, nested);
    }
}
