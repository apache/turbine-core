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

import java.util.Random;

/**
 * This class generates a unique 10+ character id.  This is good for
 * authenticating users or tracking users around.
 *
 * <p>This code was borrowed from Apache
 * JServ.JServServletManager.java.  It is what Apache JServ uses to
 * generate session ids for users.  Unfortunately, it was not included
 * in Apache JServ as a class, so I had to create one here in order to
 * use it.
 *
 * @version $Id$
 */
public class GenerateUniqueId
{
    /*
     * Create a suitable string for session identification.  Use
     * synchronized count and time to ensure uniqueness.  Use random
     * string to ensure the timestamp cannot be guessed by programmed
     * attack.
     *
     * Format of id is <6 chars random><3 chars time><1+ char count>
     */
    static private int session_count = 0;
    static private long lastTimeVal = 0;
    static private Random randomSource = new java.util.Random();

    // MAX_RADIX is 36

    /*
     * We want to have a random string with a length of 6 characters.
     * Since we encode it BASE 36, we've to modulo it with the
     * following value:
     */
    public final static long maxRandomLen = 2176782336L; // 36 ** 6

    /*
     * The session identifier must be unique within the typical
     * lifespan of a Session; the value can roll over after that.  3
     * characters: (this means a roll over after over a day, which is
     * much larger than a typical lifespan)
     */
    public final static long maxSessionLifespanTics = 46656; // 36 ** 3

    /*
     * Millisecons between different tics.  So this means that the
     * 3-character time string has a new value every 2 seconds:
     */
    public final static long ticDifference = 2000;

    /**
     * Get the unique id.
     *
     * <p>NOTE: This must work together with
     * get_jserv_session_balance() in jserv_balance.c
     *
     * @return A String with the new unique id.
     */
    static synchronized public String getIdentifier()
    {
        StringBuffer sessionId = new StringBuffer();

        // Random value.
        long n = randomSource.nextLong();
        if (n < 0) n = -n;
        n %= maxRandomLen;

        // Add maxLen to pad the leading characters with '0'; remove
        // first digit with substring.
        n += maxRandomLen;
        sessionId.append(Long.toString(n, Character.MAX_RADIX)
                .substring(1));

        long timeVal = (System.currentTimeMillis() / ticDifference);

        // Cut.
        timeVal %= maxSessionLifespanTics;

        // Padding, see above.
        timeVal += maxSessionLifespanTics;

        sessionId.append(Long.toString(timeVal, Character.MAX_RADIX)
                .substring(1));

        /*
         * Make the string unique: append the session count since last
         * time flip.
         */

        // Count sessions only within tics.  So the 'real' session
        // count isn't exposed to the public.
        if (lastTimeVal != timeVal)
        {
            lastTimeVal = timeVal;
            session_count = 0;
        }
        sessionId.append(Long.toString(++session_count,
                Character.MAX_RADIX));

        return sessionId.toString();
    }

    /**
     * Get the unique id.
     *
     * @param jsIdent A String.
     * @return A String with the new unique id.
     */
    synchronized public String getIdentifier(String jsIdent)
    {
        if (jsIdent != null && jsIdent.length() > 0)
        {
            return getIdentifier() + "." + jsIdent;
        }
        return getIdentifier();
    }

    /**
     * Simple test of the functionality.
     *
     * @param args A String[] with the command line arguments.
     */
    public static void main(String[] args)
    {
        System.out.println(GenerateUniqueId.getIdentifier());
        System.out.println(GenerateUniqueId.getIdentifier());
        System.out.println(GenerateUniqueId.getIdentifier());
        System.out.println(GenerateUniqueId.getIdentifier());
    }
}
