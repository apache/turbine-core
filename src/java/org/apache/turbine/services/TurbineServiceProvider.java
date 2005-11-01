package org.apache.turbine.services;

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

/**
 * Interface for telling Turbine that the implementation class 
 * is an external service provider therefore can be used for looking
 * up services not found by the Turbine implementation. It is 
 * assumed that the referenced service container handles the 
 * complete lifecycle of its services.
 * 
 * @author <a href="mailto:siegfried.goeschl@it20one.at">Siegfried Goeschl</a>
 */
public interface TurbineServiceProvider
{
    /**
     * Returns an instance of the requested service. If the
     * given servise is not available/found we throw a RuntimeException
     * since this is less intrusive.
     *
     * @param roleName the name of the requested service
     * @return an instance of the service
     * @throws InstantiationException the service could not be instantiated
     */
    public Object get(String roleName) throws InstantiationException;

    /**
     * Releases the instance you got before. This is only really
     * required when not working with service singletons.
     *
     * @param component the component to release
     */
    public void release(Object component);

    /**
     * Is the service known to the service container? 
     */
    public boolean exists(String roleName);
}
