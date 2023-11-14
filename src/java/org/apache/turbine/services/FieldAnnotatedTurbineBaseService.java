package org.apache.turbine.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

import org.apache.turbine.annotation.AnnotationProcessor;
import org.apache.turbine.util.TurbineException;

/**
 * <p>This class provides a <code>Service</code> implementation that
 * Services used in Turbine are required to extend. 
 * This class provides the ability to process field annotation {@link TurbineServices} in a Turbine service.
 * You could enable scanning globally by annotating the class (service) with the annotation {@link TurbineServices}.
 * Field annotation could then be omitted, if the field class is {@link TurbineServices} annotated.
 * </p>
 *
 */
public abstract class FieldAnnotatedTurbineBaseService
        extends TurbineBaseService
{
    
    private static Logger log = LogManager.getLogger(FieldAnnotatedTurbineBaseService.class);
    /**
     * Performs late initialization.
     *
     * If your class relies on early initialization, and the object it
     * expects was not received, you can use late initialization to
     * throw an exception and complain.
     *
     * @throws InitializationException if initialization of this
     * class was not successful.
     */
    @Override
    public void init() throws InitializationException
    {
        try {
            log.debug("parsing annotations for {}", this.getClass());
            AnnotationProcessor.process(this, false);
        } catch (TurbineException e) {
            throw new InitializationException(e.getMessage(), e);
        }
        setInit(true);
    }
}
