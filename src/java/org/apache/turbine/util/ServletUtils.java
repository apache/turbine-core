package org.apache.turbine.util;


import java.io.File;

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


import java.util.StringTokenizer;
import java.util.stream.Stream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.turbine.Turbine;

/**
 * This is where common Servlet manipulation routines should go.
 *
 * @author <a href="mailto:gonzalo.diethelm@sonda.com">Gonzalo Diethelm</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class ServletUtils
{
    /**
     * Expands a string that points to a relative path or path list,
     * leaving it as an absolute path based on the servlet context.
     * It will return null if the text is empty or the config object
     * is null.
     *
     * @param config The ServletConfig.
     * @param text The String containing a path or path list.
     * @return A String with the expanded path or path list.
     */
    public static String expandRelative(ServletConfig config,
                                        String text)
    {
        if (StringUtils.isEmpty(text))
        {
            return text;
        }

        if (config == null)
        {
            return null;
        }

        final String expandedText;

        // attempt to make it relative
        if (Stream.of("/", "./", "\\", ".\\").noneMatch(text::startsWith))
        {
            StringBuilder sb = new StringBuilder();
            sb.append("./");
            sb.append(text);
            expandedText = sb.toString();
        }
        else
        {
            expandedText = text;
        }

        ServletContext context = config.getServletContext();
        String base = StringUtils.defaultIfEmpty(context.getRealPath("/"),
                Turbine.getApplicationRoot());

        if (StringUtils.isEmpty(base))
        {
            return expandedText;
        }

        StringTokenizer tokenizer = new StringTokenizer(expandedText, File.pathSeparator);
        StringBuilder buffer = new StringBuilder();
        while (tokenizer.hasMoreTokens())
        {
            buffer.append(base).append(tokenizer.nextToken());
            if (tokenizer.hasMoreTokens())
            {
                buffer.append(File.pathSeparator);
            }
        }
        return buffer.toString();
    }
}
