package org.apache.turbine.util.pool;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * A deserialization stream for a specific class loader context.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @version $Id$
 */
public class ObjectInputStreamForContext extends ObjectInputStream
{
    /**
     * The class loader of the context.
     */
    private ClassLoader classLoader;

    /**
     * Contructs a new object stream for a context.
     *
     * @param in the serialized input stream.
     * @param loader the class loader of the context.
     * @throws IOException on errors.
     */
    public ObjectInputStreamForContext(InputStream in,
                                       ClassLoader loader)
            throws IOException
    {
        super(in);
        classLoader = loader;
    }

    protected Class resolveClass(ObjectStreamClass v)
            throws IOException,
            ClassNotFoundException
    {
        return classLoader == null ?
                super.resolveClass(v) : classLoader.loadClass(v.getName());
    }
}
