package org.apache.turbine.services.component;

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
 * This service loads components that can be loaded by the Stratum
 * component loader, e.g. the decoupled Torque.
 * 
 * @version $Id$
 * @deprecated torque is now loaded using the AvalonComponentService
 */
public interface ComponentService
        extends Service
{
    /** The publically visible name of the service */
    String SERVICE_NAME = "ComponentService";

}
