package org.apache.turbine.services.intake;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.lang.reflect.Method;
import org.apache.turbine.services.intake.model.Group;
import org.apache.turbine.util.TurbineException;

/**
 * This service provides access to input processing objects based
 * on an XML specification.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @version $Id$
 */
public interface IntakeService
{
    /**
     * The key under which this service is stored in TurbineServices.
     */
    public static final String SERVICE_NAME = "IntakeService";

    /**
     * The property specifying the location of the xml specification.
     */
    public static final String XML_PATH = "xml.path";

    /**
     * The default pool capacity.
     */
    public static final int DEFAULT_POOL_CAPACITY = 1024;

    /**
     * Gets an instance of a named group either from the pool
     * or by calling the Factory Service if the pool is empty.
     *
     * @param groupName the name of the group.
     * @return a Group instance.
     * @throws TurbineException if recycling fails.
     */
    public Group getGroup(String groupName)
        throws TurbineException;

    /**
     * Gets an instance of a named group either from the pool
     * or by calling the Factory Service if the pool is empty and
     * then initialize it using the ParameterParser looking for
     * a NEW id.
     *
     * @param groupName the name of the group.
     * @param pp the request parameters that may contain matching keys
     * @return a Group instance.
     * @throws TurbineException if recycling fails.
     * /
    public Group getGroup(String groupName, ParameterParser pp)
        throws Exception;

    /**
     * Gets an instance of a named group either from the pool
     * or by calling the Factory Service if the pool is empty and
     * then initialize it using the ParameterParser looking for id.
     *
     * @param groupName the name of the group.
     * @param pp the request parameters that may contain matching keys
     * @return a Group instance.
     * @throws TurbineException if recycling fails.
     * /
    public Group getGroup(String groupName, ParameterParser pp, String id)
        throws Exception;
    */

    /**
     * Puts a group back to the pool.
     * @param instance the object instance to recycle.
     * @return true if the instance was accepted.
     */
    public boolean releaseGroup(Group instance);

    /**
     * Gets the capacity of the pool for a named group.
     *
     * @param groupName the name of the group.
     */
    public int getCapacity(String groupName);

    /**
     * Sets the capacity of the pool for a named group.
     * Note that the pool will be cleared after the change.
     *
     * @param groupName the name of the group.
     * @param capacity the new capacity.
     */
    public void setCapacity(String groupName,
                            int capacity);

    /**
     * Gets the current size of the pool for a named group.
     *
     * @param groupName the name of the group.
     */
    public int getSize(String groupName);

    /**
     * Clears instances of a named group from the pool.
     *
     * @param groupName the name of the group.
     */
    public void clearPool(String groupName);

    /**
     * Clears all instances from the pool.
     */
    public void clearPool();

    /**
     * Names of all the defined groups.
     *
     * @return array of names.
     */
    public String[] getGroupNames();

    /**
     * Gets the key (usually a short identifier) for a group.
     *
     * @param groupName the name of the group.
     * @return the key.
     */
    public String getGroupKey(String groupName);

    /**
     * Gets the group name given its key.
     *
     * @param the the key.
     * @return groupName the name of the group.
     */
    public String getGroupName(String groupKey);

    /**
     * Gets the Method that can be used to set a property.
     *
     * @param className the name of the object.
     * @param propName the name of the property.
     * @return the setter.
     */
    public Method getFieldSetter(String className, String propName);

    /**
     * Gets the Method that can be used to get a property value.
     *
     * @param className the name of the object.
     * @param propName the name of the property.
     * @return the getter.
     */
    public Method getFieldGetter(String className, String propName);

}
