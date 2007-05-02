package org.apache.turbine.util.pool;

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

import org.apache.turbine.util.TurbineException;

/**
 * An interface for objects that can be pooled and recycled several times
 * by different clients.  Pooled objects that implement this interface
 * use no argument ctor and recycle methods.  Initialization is taken
 * care of using the init method.  This is a way to avoid
 * introspection/reflection when pooling an object.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @version $Id$
 */
public interface InitableRecyclable extends Recyclable
{
    /**
     * This method should be called after retrieving the object from
     * the pool.
     */
    void init(Object initObj) throws TurbineException;
}
