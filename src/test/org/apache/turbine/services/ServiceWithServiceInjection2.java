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

import static org.junit.Assert.assertNotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.localization.LocalizationService;
import org.apache.turbine.annotation.TurbineService;
/**
 * This service is used for testing 2nd level injection of services and class level declaration of 
 * {@link TurbineService} (interface is optional). 
 *
 * @author <a href="mailto:gk@apache.org">Georg Kallidis</a>
 */
@TurbineService( ServiceWithServiceInjection2.SERVICE_NAME )
public class ServiceWithServiceInjection2 extends FieldAnnotatedTurbineBaseService 
{
    
    static final String SERVICE_NAME = "ServiceWithService2";
    
    private static Log log = LogFactory.getLog(ServiceWithServiceInjection2.class);
    
    // Test for implicit SERVICE_NAME
    @TurbineService
    private LocalizationService localizationService2;
    
    /**
     * Initializes the service.
     */
    @Override
    public void init() throws InitializationException
    {
        super.init();
        log.info("localizationService2 is: " + localizationService2);
//        setInit(true);
    }
    
    public void callService() 
    {
        assertNotNull("localizationService2 object was Null.", localizationService2);
    }
}
