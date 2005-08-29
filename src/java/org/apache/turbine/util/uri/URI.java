package org.apache.turbine.util.uri;

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

/**
 * An interface class which describes the absolute minimum of methods that
 * a Turbine URI class must implement.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public interface URI
{
    /**
     * Gets the script name (/servlets/Turbine).
     *
     * @return A String with the script name.
     */
    String getScriptName();

    /**
     * Gets the context path.
     *
     * @return A String with the context path.
     */
    String getContextPath();

    /**
     * Gets the server name.
     *
     * @return A String with the server name.
     */
    String getServerName();

    /**
     * Gets the server port.
     *
     * @return A String with the server port.
     */
    int getServerPort();

    /**
     * Returns the current Server Scheme
     *
     * @return The current Server scheme
     *
     */
    String getServerScheme();

    /**
     * Returns the current reference anchor.
     *
     * @return A String containing the reference.
     */
    String getReference();
}
