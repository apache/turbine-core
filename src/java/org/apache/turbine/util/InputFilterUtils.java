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


import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Some filter methods that have been orphaned in the Screen class.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public abstract class InputFilterUtils
{
    /**
     * This function can/should be used in any screen that will output
     * User entered text.  This will help prevent users from entering
     * html (&lt;SCRIPT&gt;) tags that will get executed by the browser.
     *
     * @param s The string to prepare.
     * @return A string with the input already prepared.
     */
    public static String prepareText(String s)
    {
        return StringEscapeUtils.escapeHtml(s);
    }

    /**
     * This function can/should be used in any screen that will output
     * User entered text.  This will help prevent users from entering
     * html (&lt;SCRIPT&gt;) tags that will get executed by the browser.
     *
     * @param s The string to prepare.
     * @return A string with the input already prepared.
     */
    public static String prepareTextMinimum(String s)
    {
        /*
         * We would like to filter user entered text that might be
         * dynamically added, using javascript for example.  But we do not
         * want to filter all the above chars, so we will just disallow
         * <.
         */
        return StringUtils.replace(s, "<", "&lt;");
    }
}
