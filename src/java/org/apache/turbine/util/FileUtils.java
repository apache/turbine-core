package org.apache.turbine.util;


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
 * Common {@link java.io.File} manipulation routines.
 *
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 * @deprecated This class will be removed after the 2.3 release. Please
 *             use FileUtils from <a href="http://jakarta.apache.org/commons/">commons-io</a>.
 */
public class FileUtils
{
    /**
     * The number of bytes in a kilobyte.
     */
    public static final int ONE_KB = 1024;

    /**
     * The number of bytes in a megabyte.
     */
    public static final int ONE_MB = ONE_KB * ONE_KB;

    /**
     * The number of bytes in a gigabyte.
     */
    public static final int ONE_GB = ONE_KB * ONE_MB;

    /**
     * Returns a human-readable version of the file size (original is in
     * bytes).
     *
     * @param size The number of bytes.
     * @return     A human-readable display value (includes units).
     */
    public static String byteCountToDisplaySize(int size)
    {
        String displaySize;

        if (size / ONE_GB > 0)
        {
            displaySize = String.valueOf(size / ONE_GB) + " GB";
        }
        else if (size / ONE_MB > 0)
        {
            displaySize = String.valueOf(size / ONE_MB) + " MB";
        }
        else if (size / ONE_KB > 0)
        {
            displaySize = String.valueOf(size / ONE_KB) + " kB";
        }
        else
        {
            displaySize = String.valueOf(size) + " bytes";
        }

        return displaySize;
    }
}
