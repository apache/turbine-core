package org.apache.turbine.util.hibernate;

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

import org.apache.commons.lang3.exception.NestableException;
/**
 * A general PersistenceException that can be thrown by 
 * Hibernate DAO classes.
 *
 */
public class PersistenceException extends NestableException
{
    //~ Constructors ===========================================================

    /**
     * Constructor for PersistenceException.
     */
    public PersistenceException()
    {
        super();
    }

    /**
     * Constructor for PersistenceException.
     *
     * @param message
     */
    public PersistenceException(String message)
    {
        super(message);
    }

    /**
     * Constructor for PersistenceException.
     *
     * @param message
     * @param cause
     */
    public PersistenceException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructor for PersistenceException.
     *
     * @param cause
     */
    public PersistenceException(Throwable cause)
    {
        super(cause);
    }

}
