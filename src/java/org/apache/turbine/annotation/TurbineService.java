package org.apache.turbine.annotation;

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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to mark class and fields in modules that require a service to be injected
 * 
 * Explicit field annotation of {@link #SERVICE_NAME} will take precedence of class annotation. 
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( {ElementType.TYPE, ElementType.FIELD, ElementType.METHOD} )
public @interface TurbineService
{
    /**
     * Get the name of the service to inject
     *
     * @return the service name or role
     */
    String value() default "";

    /**
     * A constant defining the field name for the service name
     */
    String SERVICE_NAME = "SERVICE_NAME";

    /**
     * A constant defining the field name for the role
     */
    String ROLE = "ROLE";
}
