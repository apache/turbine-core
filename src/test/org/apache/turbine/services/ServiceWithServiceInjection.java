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

import org.apache.avalon.framework.activity.Initializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.localization.LocalizationService;
import org.apache.turbine.annotation.TurbineService;
/**
 * This service is used for testing injection of services in fields and methods and class level declaration of 
 * {@link TurbineService} without interface. 
 *
 * @author <a href="mailto:gk@apache.org">Georg Kallidis</a>
 */
@TurbineService( ServiceWithServiceInjection.SERVICE_NAME )
public class ServiceWithServiceInjection extends MethodAnnotatedTurbineBaseService implements Initializable /* ServiceWithService */
{

    String ROLE = ServiceWithServiceInjection.class.getName();
    
    static final String SERVICE_NAME = "ServiceWithService";
    
    private static Log log = LogFactory.getLog(ServiceWithServiceInjection.class);
    
    // Test for implicit SERVICE_NAME
    // we need the declaration as this is not by default a Turbine Service
    @TurbineService
    private LocalizationService localizationService;
    
    static private ServiceWithServiceInjection2 serviceWithServiceInjection2;
    
 
    // Test for method injected class level SERVICE_NAME
    // Annotation could be omitted as the class is annotated
    //  @TurbineService
    public void setServiceWithServiceInjection2(ServiceWithServiceInjection2 serviceWithServiceInjection) {
        serviceWithServiceInjection2 = serviceWithServiceInjection;
    }
    
    /**
     * Initializes the service.
     */
    @Override
    public void initialize() throws Exception 
    {
        log.debug("Calling initializable()");
        // do not call  AnnotationProcessor.process(this); here as it will result in an endless looping;
    }
    
    /**
     * Initializes the service.
     */
    @Override
    public void init() throws InitializationException
    {
        super.init();
        log.info("localizationService is: " + localizationService);
//        setInit(true);
    }
    
    public void callService() 
    {
        assertNotNull("field injected localizationService object was Null.", localizationService);
        assertNotNull("method injected service serviceWithServiceInjection2 object was Null.", serviceWithServiceInjection2);
        ServiceWithServiceInjection.serviceWithServiceInjection2.callService();
    }
}
