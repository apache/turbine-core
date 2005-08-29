package org.apache.turbine.om;

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

import java.util.HashMap;
import java.util.Map;

import org.apache.turbine.Turbine;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.pool.Recyclable;

/**
 * A Pull tool to make om objects available to a template
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class OMTool implements ApplicationTool, Recyclable
{
    // private RunData data;
    private HashMap omMap;

    // note the following could be a static attribute to reduce memory
    // footprint. Might require a service to front load the
    // PullHelpers to avoid MT issues. A multiple write is not so bad
    // though

    /** The cache of PullHelpers. **/
    private static Map pullMap = new HashMap();

    /**
     *  The Factory responsible for retrieving the
     *  objects from storage
     */
    private RetrieverFactory omFactory;

    public OMTool()throws Exception
    {
        omMap = new HashMap();
        String className = Turbine.getConfiguration()
                .getString("tool.om.factory");
        //        RetrieverFactory omFactory =
        //            (RetrieverFactory)Class.forName(className).newInstance();
    }

    /**
     * Prepares tool for a single request
     */
    public void init(Object runData)
    {
        // data = (RunData)runData;
    }

    /**
     * Implementation of ApplicationTool interface is not needed for this
     * method as the tool is request scoped
     */
    public void refresh()
    {
        // empty
    }

    /**
     * Inner class to present a nice interface to the template designer
     */
    private class PullHelper
    {
        String omName;

        private PullHelper(String omName)
        {
            this.omName = omName;
        }

        public Object setKey(String key)
            throws Exception
        {
            Object om = null;

            String inputKey = omName + key;
            if (omMap.containsKey(inputKey))
            {
                om = omMap.get(inputKey);
            }
            else
            {
                om = omFactory.getInstance(omName).retrieve(key);
                omMap.put(inputKey, om);
            }

            return om;
        }
    }

    public Object get(String omName) throws Exception
    {
        if (!pullMap.containsKey(omName))
        {
            // MT could overwrite a PullHelper, but that is not a problem
            // should still synchronize to avoid two threads adding at
            // same time
            synchronized (this.getClass())
            {
                pullMap.put(omName, new OMTool.PullHelper(omName));
            }
        }

        return pullMap.get(omName);
    }

    public Object get(String omName, String key) throws Exception
    {
        return ((OMTool.PullHelper) get(omName)).setKey(key);
    }


    public String getName()
    {
        return "om";
    }


    // ****************** Recyclable implementation ************************

    private boolean disposed;

    /**
     * Recycles the object for a new client. Recycle methods with
     * parameters must be added to implementing object and they will be
     * automatically called by pool implementations when the object is
     * taken from the pool for a new client. The parameters must
     * correspond to the parameters of the constructors of the object.
     * For new objects, constructors can call their corresponding recycle
     * methods whenever applicable.
     * The recycle methods must call their super.
     */
    public void recycle()
    {
        disposed = false;
    }

    /**
     * Disposes the object after use. The method is called
     * when the object is returned to its pool.
     * The dispose method must call its super.
     */
    public void dispose()
    {
        omMap.clear();
        // data = null;
        disposed = true;
    }

    /**
     * Checks whether the recyclable has been disposed.
     * @return true, if the recyclable is disposed.
     */
    public boolean isDisposed()
    {
        return disposed;
    }
}








