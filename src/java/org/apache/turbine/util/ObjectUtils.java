package org.apache.turbine.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

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
     * Converts a map to a byte array for storage/serialization.
     *
     * @param map The Map to convert.
     *
     * @return A byte[] with the converted Map.
     *
     * @exception Exception A generic exception.
     */
	public static byte[] serializeMap(Map<String, Object> map)
            throws Exception
    {
        byte[] byteArray = null;

        for (Object value : map.values())
        {
            if (! (value instanceof Serializable))
            {
                throw new Exception("Could not serialize, value is not serializable:" + value);
            }
        }

        ByteArrayOutputStream baos = null;
        ObjectOutputStream out = null;
        try
        {
            // These objects are closed in the finally.
            baos = new ByteArrayOutputStream(1024);
            out  = new ObjectOutputStream(baos);

            out.writeObject(map);
            out.flush();

            byteArray = baos.toByteArray();
        }
        finally
        {
            if (out != null)
            {
                out.close();
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
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] objectData)
    {
        T object = null;

        if (objectData != null)
        {
            // These streams are closed in finally.
            ObjectInputStream in = null;
            ByteArrayInputStream bin = new ByteArrayInputStream(objectData);

            try
            {
                in = new ObjectInputStream(bin);

                // If objectData has not been initialized, an
                // exception will occur.
                object = (T)in.readObject();
            }
            catch (Exception e)
            {
                // ignore
            }
            finally
            {
                try
                {
                    if (in != null)
                    {
                        in.close();
                    }
                    bin.close();
                }
                catch (IOException e)
                {
                    // ignore
                }
            }
        }
        return object;
    }
}
