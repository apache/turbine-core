package org.apache.turbine.services.intake.validator;

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

import org.apache.turbine.services.intake.IntakeException;

/**
 * An Exception indidate an invalid field mask.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class InvalidMaskException
        extends IntakeException
{
    /** Serial Version UID */
    private static final long serialVersionUID = 2133836269854500843L;

    /**
     * Creates a new <code>InvalidMaskException</code> instance.
     *
     * @param message describing the reason validation failed.
     */
    public InvalidMaskException(String message)
    {
        super(message);
    }

    /**
     * Creates a new <code>InvalidMaskException</code> instance.
     *
     * @param cause Cause of the exception
     * @param message describing the reason validation failed.
     */
    public InvalidMaskException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
