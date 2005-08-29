package org.apache.turbine.services.intake;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import java.beans.IntrospectionException;

import java.lang.reflect.Method;

import org.apache.turbine.services.intake.model.Group;

/**
 * This service provides access to input processing objects based
 * on an XML specification.
 *
 * <p>Localization of Intake's error messages can be accomplished
 * using Turbine's <code>LocalizationTool</code> from a Velocity template
 * as follows:
 * <blockquote><code></pre>
 * $l10n.get($intake.SomeGroup.SomeField.Message)
 * </pre></code></blockquote>
 * </p>
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public interface IntakeService
{
    /**
     * The key under which this service is stored in TurbineServices.
     */
    String SERVICE_NAME = "IntakeService";

    /**
     * The property specifying the location of the xml specification.
     */
    String XML_PATH = "xml.path";

    /**
     * The default location of the xml specification.
     */
    String XML_PATH_DEFAULT = "WEB-INF/conf/intake.xml";

    /**
     * The property specifying the location where a serialized version of
     * the xml specification can be written for faster restarts..
     */
    String SERIAL_XML = "serialize.path";

    /**
     * The default location where a serialized version of
     * the xml specification can be written for faster restarts..
     */
    String SERIAL_XML_DEFAULT = "WEB-INF/appData.ser";

    /**
     * The default pool capacity.
     */
    int DEFAULT_POOL_CAPACITY = 1024;

    /**
     * Gets an instance of a named group either from the pool
     * or by calling the Factory Service if the pool is empty.
     *
     * @param groupName the name of the group.
     * @return a Group instance.
     * @throws IntakeException if recycling fails.
     */
    Group getGroup(String groupName)
            throws IntakeException;

    /**
     * Puts a group back to the pool.
     * @param instance the object instance to recycle.
     *
     * @throws IntakeException The passed group name does not exist.
     */
    void releaseGroup(Group instance)
            throws IntakeException;

    /**
     * Gets the current size of the pool for a named group.
     *
     * @param groupName the name of the group.
     *
     * @throws IntakeException The passed group name does not exist.
     */
    int getSize(String groupName)
            throws IntakeException;

    /**
     * Names of all the defined groups.
     *
     * @return array of names.
     */
    String[] getGroupNames();

    /**
     * Gets the key (usually a short identifier) for a group.
     *
     * @param groupName the name of the group.
     * @return the key.
     */
    String getGroupKey(String groupName);

    /**
     * Gets the group name given its key.
     *
     * @param groupKey the key.
     * @return groupName the name of the group.
     */
    String getGroupName(String groupKey);

    /**
     * Gets the Method that can be used to set a property.
     *
     * @param className the name of the object.
     * @param propName the name of the property.
     * @return the setter.
     * @throws ClassNotFoundException
     * @throws IntrospectionException
     */
    Method getFieldSetter(String className, String propName)
            throws ClassNotFoundException, IntrospectionException;

    /**
     * Gets the Method that can be used to get a property value.
     *
     * @param className the name of the object.
     * @param propName the name of the property.
     * @return the getter.
     * @throws ClassNotFoundException
     * @throws IntrospectionException
     */
    Method getFieldGetter(String className, String propName)
            throws ClassNotFoundException, IntrospectionException;
}





