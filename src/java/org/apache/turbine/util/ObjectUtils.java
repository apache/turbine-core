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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * This is where common Object manipulation routines should go.
 *
 * @author <a href="mailto:nissim@nksystems.com">Nissim Karpenstein</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public abstract class ObjectUtils
{
    /**
     * Returns a default value if the object passed is null.
     *
     * @param o The object to test.
     * @param dflt The default value to return.
     * @return The object o if it is not null, dflt otherwise.
     * @deprecated Use org.apache.commons.lang.ObjectUtils.defaultIfNull()
     */
    public static Object isNull(Object o, Object dflt)
    {
		return org.apache.commons.lang.ObjectUtils.defaultIfNull(o,dflt);
    }

    /**
     * Adds an object to a vector, making sure the object is in the
     * vector only once.
     *
     * @param v The vector.
     * @param o The object.
     * @deprecated No replacement
     */
    public static void addOnce(List l, Object o)
    {
        if (!l.contains(o))
        {
            l.add(o);
        }
    }

    /**
     * Converts a hashtable to a byte array for storage/serialization.
     *
     * @param hash The Hashtable to convert.
     *
     * @return A byte[] with the converted Hashtable.
     *
     * @exception Exception A generic exception.
     */
    public static byte[] serializeHashtable(Hashtable hash)
        throws Exception
    {
        Hashtable saveData = new Hashtable(hash.size());
        String key = null;
        Object value = null;
        byte[] byteArray = null;

        Enumeration keys = hash.keys();

        while (keys.hasMoreElements())
        {
            key = (String) keys.nextElement();
            value = hash.get(key);
            if (value instanceof Serializable)
            {
                saveData.put (key, value);
            }
        }

        ByteArrayOutputStream baos = null;
        BufferedOutputStream bos = null;
        ObjectOutputStream out = null;
        try
        {
            // These objects are closed in the finally.
            baos = new ByteArrayOutputStream();
            bos  = new BufferedOutputStream(baos);
            out  = new ObjectOutputStream(bos);

            out.writeObject(saveData);
            out.flush();
            bos.flush();

            byteArray = baos.toByteArray();
        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
            if (bos != null)
            {
                bos.close();
            }
            if (baos != null)
            {
                baos.close();
            }
        }
        return byteArray;
    }

    /**
     * Deserializes a single object from an array of bytes.
     *
     * @param objectData The serialized object.
     *
     * @return The deserialized object, or <code>null</code> on failure.
     */
    public static Object deserialize(byte[] objectData)
    {
        Object object = null;

        if (objectData != null)
        {
            // These streams are closed in finally.
            ObjectInputStream in = null;
            ByteArrayInputStream bin = new ByteArrayInputStream(objectData);
            BufferedInputStream bufin = new BufferedInputStream(bin);

            try
            {
                in = new ObjectInputStream(bufin);

                // If objectData has not been initialized, an
                // exception will occur.
                object = in.readObject();
            }
            catch (Exception e)
            {
            }
            finally
            {
                try
                {
                    if (in != null)
                    {
                        in.close();
                    }

                    bufin.close();
                    bin.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        return object;
    }

    /**
     * Compares two Objects, returns true if their values are the
     * same.  It checks for null values prior to an o1.equals(o2)
     * check
     *
     * @param o1 The first object.
     * @param o2 The second object.
     * @return True if the values of both xstrings are the same.
     * @deprecated Use org.apache.commons.lang.ObjectUtils.equals()
     */
    public static boolean equals(Object o1, Object o2)
    {
		return org.apache.commons.lang.ObjectUtils.equals(o1,o2);
    }

    /**
     * Nice method for adding data to a Hashtable in such a way
     * as to not get NPE's. The point being that if the
     * value is null, Hashtable.put() will throw an exception.
     * That blows in the case of this class cause you may want to
     * essentially treat put("Not Null", null ) == put("Not Null", "")
     * We will still throw a NPE if the key is null cause that should
     * never happen.
     * @deprecated No replacement
     */
    public static final void safeAddToHashtable(Hashtable hash, Object key,
                                                Object value)
            throws NullPointerException
    {
        if (value == null)
        {
            hash.put(key, "");
        }
        else
        {
            hash.put(key, value);
        }
    }
}
