package org.apache.turbine.util;

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

import org.apache.commons.lang.exception.NestableRuntimeException;

/**
 * This is a base class of runtime exeptions thrown by Turbine.
 *
 * This class represents a non-checked type exception (see
 * {@link java.lang.RuntimeException}). It has the nested stack trace
 * functionality found in the {@link TurbineException} class.
 *
 */
public class TurbineRuntimeException extends NestableRuntimeException
{
    /**
     * Constructs a new <code>TurbineRuntimeException</code> without specified
     * detail message.
     */
    public TurbineRuntimeException()
    {
    }

    /**
     * Constructs a new <code>TurbineRuntimeException</code> with specified
     * detail message.
     *
     * @param msg the error message.
     */
    public TurbineRuntimeException(String msg)
    {
        super(msg);
    }

    /**
     * Constructs a new <code>TurbineRuntimeException</code> with specified
     * nested <code>Throwable</code>.
     *
     * @param nested the exception or error that caused this exception
     *               to be thrown.
     */
    public TurbineRuntimeException(Throwable nested)
    {
        super(nested);
    }

    /**
     * Constructs a new <code>TurbineRuntimeException</code> with specified
     * detail message and nested <code>Throwable</code>.
     *
     * @param msg the error message.
     * @param nested the exception or error that caused this exception
     *               to be thrown.
     */
    public TurbineRuntimeException(String msg, Throwable nested)
    {
        super(msg, nested);
    }

}
