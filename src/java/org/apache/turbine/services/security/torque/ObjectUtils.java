package org.apache.turbine.services.security.torque;

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.IOException;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Tools pulled together to load and store the permanent storage
 * in the User class. Stolen from various parts of Turbine 2 and
 * Torque. :-)
 * 
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */


public class ObjectUtils
{
    /**
     * Nice method for adding data to a Hashtable in such a way
     * as to not get NPE's. The point being that if the
     * value is null, Hashtable.put() will throw an exception.
     * That blows in the case of this class cause you may want to
     * essentially treat put("Not Null", null) == put("Not Null", "")
     * We will still throw a NPE if the key is null cause that should
     * never happen.
     *
     * Maybe a hashtable isn't the best option here and we
     * should use a Map.
     *
     * @param hash The hashtable to use
     * @param key  The key to use
     * @param value The value to store in the hashtable
     *
     * @throws NullPointerException The supplied key was null
     *
     */
    public static final void safeAddToHashtable(Hashtable hash, Object key, Object value)
        throws NullPointerException
    {
        if (value == null)
        {
            hash.put (key, "");
        }
        else
        {
            hash.put (key, value);
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
            ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
            BufferedInputStream  bis  = new BufferedInputStream(bais);
            ObjectInputStream    in   = null;

            try                
            {
                in = new ObjectInputStream(bis);

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
                    if (bis != null)
                    {
                        bis.close();
                    }
                    if (bais != null) 
                    {
                        bais.close();
                    }
                } 
                catch (IOException e)
                {
                }
            }
        }
        return object;
    }

}

