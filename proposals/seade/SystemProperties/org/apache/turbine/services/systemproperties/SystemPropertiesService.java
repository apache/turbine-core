package org.apache.turbine.services.systemproperties;


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


import org.apache.turbine.services.Service;

/**
 * The SystemPropertiesService provides a convenient way of getting properties 
 * defined in TurbineResources.properties into System.properties.
 *
 * <p>Properties defined as:<br/ >
 *     <code>services.SystemPropertiesService.name = value</code><br/ >
 * will be added to System.properties as<br/ >
 *     <code>name=value</code><br/ >
 * Suggested use is to configure mail.host for JavaMail thus:<br/ >
 *     <code>services.SystemPropertiesService.mail.host = localhost</code>
 *
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id$
 */
public interface SystemPropertiesService extends Service
{
    /**
     * The service identifier.
     */
    public String SERVICE_NAME = "SystemPropertiesService";

}
