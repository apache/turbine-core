package org.apache.turbine.util;

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
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:neeme@one.lv">Neeme Praks</a>
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
