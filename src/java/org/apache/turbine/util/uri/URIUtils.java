package org.apache.turbine.util.uri;

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

import org.apache.turbine.Turbine;

/**
 * Bundles a few static routines concerning URIs, that you
 * will need all the time.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public abstract class URIUtils
{
    /**
     * Convert a given Path into a Turbine Data URI. The resulting
     * path contains no path_info or query data. If you have a current
     * runData object around, you should use DataURI and setScriptName()!.
     *
     * @param path A relative path
     *
     * @return the absolute path for the request.
     *
     */

    public static String getAbsoluteLink(String path)
    {
        DataURI du = new DataURI(Turbine.getDefaultServerData());
        du.setScriptName(path);
        return du.getAbsoluteLink();
    }
}
