package org.apache.turbine.services.avaloncomponent;

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

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;

import org.apache.turbine.services.Service;

/**
 * This service allows access to avalon components.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public interface AvalonComponentService
        extends Service
{
    /** The publically visible name of the service */
    String SERVICE_NAME = "AvalonComponentService";

    /** Where we write the Avalon Logger messages */
    String AVALON_LOG_CATEGORY = "avalon";

    /** property specifing file name of the component config file */
    String COMPONENT_CONFIG_KEY = "componentConfiguration";

    /** property specifing file name of the component config file */
    String COMPONENT_CONFIG_VALUE = "/WEB-INF/conf/componentConfiguration.xml";

    /** property specifing file name of the component role file */
    String COMPONENT_ROLE_KEY = "componentRoles";

    /** property specifing file name of the component role file */
    String COMPONENT_ROLE_VALUE = "/WEB-INF/conf/roleConfiguration.xml";

    /** property for the Components to look up */
    String COMPONENT_LOOKUP_KEY = "lookup";

    /** Key used in the context for defining the application root */
    String COMPONENT_APP_ROOT = "componentAppRoot";

    /**
     * Returns an instance of the named component
     *
     * @param roleName Name of the role the component fills.
     * @return an instance of the named component
     * @throws ComponentException generic exception
     */
    Component lookup(String roleName)
            throws ComponentException;

    /**
     * Releases the component
     *
     * @param component the component to release
     */
    void release(Component component);

}
