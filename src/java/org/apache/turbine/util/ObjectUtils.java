package org.apache.turbine.util;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.turbine.log.Log;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;

/**
 * This is where common Object manipulation routines should go.
 *
 * @author <a href="mailto:nissim@nksystems.com">Nissim Karpenstein</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public abstract class ObjectUtils
{
    private static final Log log = Log.getLog(ObjectUtils.class);

    /**
     * Converts a map to a byte array for storage/serialization.
     *
     * @param map The Map to convert.
     *
     * @return A byte[] with the converted Map.
     *
     * @throws Exception A generic exception.
     */
    public static byte[] serializeMap(Map<String, Object> map)
        throws Exception
    {
        byte[] byteArray = null;
        Map<String, Object> mapCopy = new HashMap<>(map);

        // Remove all entries that are not serializable
        for (Iterator<Map.Entry<String, Object>> i = mapCopy.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry<String, Object> entry = i.next();
            if (! (entry.getValue() instanceof Serializable))
            {
                i.remove();
                log.warn("Skipping serialization, value is not serializable: " + entry.getValue());
            }
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
             ObjectOutputStream out = new ObjectOutputStream(baos))
        {
            out.writeObject(mapCopy);
            out.flush();

            byteArray = baos.toByteArray();
        }

        return byteArray;
    }

    /**
     * Deserializes a single object from an array of bytes.
     *
     * @param <T> type of the object to return
     * @param objectData The serialized object.
     *
     * @return The deserialized object, or <code>null</code> on failure.
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] objectData)
    {
        T object = null;

        if (objectData != null)
        {
            final String filterPattern = Turbine.getConfiguration().getString(TurbineConstants.SESSION_OBJECTINPUTFILTER);

            try (ByteArrayInputStream bin = new ByteArrayInputStream(objectData);
                 ObjectInputStream in = new ObjectInputStream(bin))
            {
                // Set filter to limit what can be deserialized if so configured
                if (StringUtils.isNotEmpty(filterPattern))
                {
                    in.setObjectInputFilter(ObjectInputFilter.Config.createFilter(filterPattern));
                }

                // If objectData has not been initialized, an
                // exception will occur.
                object = (T)in.readObject();
            }
            catch (Exception e)
            {
                log.warn("Problem deserializing object.", e);
            }
        }

        return object;
    }
}
