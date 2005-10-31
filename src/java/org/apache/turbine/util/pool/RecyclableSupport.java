package org.apache.turbine.util.pool;


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


import org.apache.avalon.framework.service.ServiceException;
import org.apache.fulcrum.pool.PoolService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.avaloncomponent.AvalonComponentService;

/**
 * A support class for recyclable objects implementing default methods.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @version $Id$
 */
public class RecyclableSupport implements org.apache.fulcrum.pool.Recyclable
{
    /**
     * The disposed flag.
     */
    private boolean disposed;
    
    /**
     * Reference to the Fulcrum Pool service
     */
    private PoolService pool;

    /**
     * Constructs a new recyclable support and calls the default recycle method.
     * @throws ServiceException 
     */
    public void Recyclable() throws ServiceException
    {
    		recycle();
    }

    /**
     * Recycles the object by removing its disposed flag.
     */
    public void recycle()
    {
        disposed = false;
    }

    /**
     * Disposes the object by setting its disposed flag.
     */
    public void dispose()
    {
        disposed = true;
    }

    /**
     * Checks whether the object is disposed.
     *
     * @return true, if the object is disposed.
     */
    public boolean isDisposed()
    {
        return disposed;
    }

    /**
     * A convenience method allowing a clever recyclable object
     * to put itself into a pool for recycling.
     *
     * @return true, if disposal was accepted by the pool.
     * @throws ServiceException 
     */
    protected boolean doDispose() throws ServiceException
    {
    		if (pool == null) {
    			AvalonComponentService acs = (AvalonComponentService) TurbineServices.getInstance().getService(AvalonComponentService.SERVICE_NAME);
    			pool = (PoolService)acs.lookup(PoolService.ROLE);
    		}
    		return pool.putInstance(this);
  
    }
}
