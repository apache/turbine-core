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

import org.apache.ecs.Entities;

import org.apache.ecs.filter.CharacterFilter;

/**
 * Some filter methods that have been orphaned in the Screen class.
 *
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public abstract class InputFilterUtils
{
    /** A HtmlFilter Object for the normal input filter */
    private static final CharacterFilter filter = htmlFilter();

    /** A HtmlFilter Object for the minimal input filter */
    private static final CharacterFilter minFilter = htmlMinFilter();

    /**
     * This function can/should be used in any screen that will output
     * User entered text.  This will help prevent users from entering
     * html (<SCRIPT>) tags that will get executed by the browser.
     *
     * @param s The string to prepare.
     * @return A string with the input already prepared.
     */
    public static String prepareText(String s)
    {
        return filter.process(s);
    }

    /**
     * This function can/should be used in any screen that will output
     * User entered text.  This will help prevent users from entering
     * html (<SCRIPT>) tags that will get executed by the browser.
     *
     * @param s The string to prepare.
     * @return A string with the input already prepared.
     */
    public static String prepareTextMinimum(String s)
    {
        return minFilter.process(s);
    }

    /**
     * These attributes are supposed to be the default, but they are
     * not, at least in ECS 1.2.  Include them all just to be safe.
     *
     * @return A CharacterFilter to do HTML filtering.
     */
    private static CharacterFilter htmlFilter()
    {
        CharacterFilter filter = new CharacterFilter();
        filter.addAttribute("\"", Entities.QUOT);
        filter.addAttribute("'", Entities.LSQUO);
        filter.addAttribute("&", Entities.AMP);
        filter.addAttribute("<", Entities.LT);
        filter.addAttribute(">", Entities.GT);
        return filter;
    }

    /*
     * We would like to filter user entered text that might be
     * dynamically added, using javascript for example.  But we do not
     * want to filter all the above chars, so we will just disallow
     * <.
     *
     * @return A CharacterFilter to do minimal HTML filtering.
     */
    private static CharacterFilter htmlMinFilter()
    {
        CharacterFilter filter = new CharacterFilter();
        filter.removeAttribute(">");
        filter.removeAttribute("\"");
        filter.removeAttribute("'");
        filter.removeAttribute("&");
        filter.addAttribute("<", Entities.LT);
        return filter;
    }
}
