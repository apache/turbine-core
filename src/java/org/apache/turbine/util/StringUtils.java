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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * This is where common String manipulation routines should go.
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:gcoladonato@yahoo.com">Greg Coladonato</a>
 * @version $Id$
 */
public class StringUtils
{
    /**
     * Deal with null strings converting them to "" instead.  It also
     * invokes String.trim() on the output.
     *
     * @param foo A String.
     * @return A String.
     */
    public static final String makeString(String foo)
    {
        return (foo == null ? "" : foo.trim());
    }

    /**
     * Validates that the supplied string is neither <code>null</code>
     * nor the empty string.
     *
     * @param foo The text to check.
     * @return Whether valid.
     */
    public static final boolean isValid(String foo)
    {
        return (foo != null && foo.length() > 0);
    }
    
    /**
     * Determine whether a (trimmed) string is empty
     *
     * @param foo The text to check.
     * @return Whether empty.
     */
    public static final boolean isEmpty(String foo)
    {
        return (foo == null || foo.trim().length() == 0);
    }

    /**
     * Returns the output of printStackTrace as a String.
     *
     * @param e A Throwable.
     * @return A String.
     */
    public static final String stackTrace(Throwable e)
    {
        String foo = null;
        try
        {
            // And show the Error Screen.
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            e.printStackTrace( new PrintWriter(buf, true) );
            foo = buf.toString();
        }
        catch (Exception f)
        {
            // Do nothing.
        }
        return foo;
    }
    
    /**
     * Returns the output of printStackTrace as a String.
     *
     * @param e A Throwable.
     * @param addPre a boolean to add HTML <pre> tags around the stacktrace
     * @return A String.
     */
    public static final String stackTrace(Throwable e, boolean addPre)
    {
        if (addPre)
        {
            return "<pre>" + stackTrace(e) + "</pre>";
        }
        else
        {
            return stackTrace(e);    
        }
    }

    /**
     * Compares two Strings, returns true if their values are the
     * same.
     *
     * @param s1 The first string.
     * @param s2 The second string.
     * @return True if the values of both strings are the same.
     */
    public static boolean equals( String s1,
                                  String s2 )
    {
        if (s1 == null)
        {
            return (s2 == null);
        }
        else if (s2 == null)
        {
            // s1 is not null
            return false;
        }
        else
        {
            return s1.equals(s2);
        }
    }

    public static final int PPKEY_CLASSNAME = 0;
    public static final int PPKEY_ID = 1;
    public static final int PPKEY_PROPERTY = 2;

    /**
     * Takes a String of the form substring[substring]subtring and
     * returns the 3 substrings
     *
     * @returns a three element String array
     */
    public static String[] parseObjectKey(String s)
    {
        String[] p  = new String[3];
        StringTokenizer st = new StringTokenizer(s, "[]");
        int count = st.countTokens();
        if ( count > 1)
        {
            p[0] = st.nextToken();
            p[1] = st.nextToken();
            if (count == 3)
            {
                p[2] = st.nextToken();
            }
        }
        return p;
    }


    /**
     * Remove Underscores from a string and replaces first
     * Letters with Capitals.  foo_bar becomes FooBar
     */
    public static String removeUnderScores (String data)
    {
        String temp = null;
        StringBuffer out = new StringBuffer();
        temp = data;

        StringTokenizer st = new StringTokenizer(temp, "_");
        while (st.hasMoreTokens())
        {
            String element = (String) st.nextElement();
            out.append ( firstLetterCaps(element));
        }
        return out.toString();
    }

    /**
     * Makes the first letter caps and leaves the rest as is.
     */
    public static String firstLetterCaps ( String data )
    {
        StringBuffer sbuf = new StringBuffer(data.length());
        sbuf.append(data.substring(0, 1).toUpperCase())
            .append(data.substring(1));
        return sbuf.toString();
    }

    /**
     * Splits the provided CSV text into a list.
     *
     * @param text      The CSV list of values to split apart.
     * @param separator The separator character.
     * @return          The list of values.
     */
    public static String[] split(String text, String separator)
    {
        StringTokenizer st = new StringTokenizer(text, separator);
        String[] values = new String[st.countTokens()];
        int pos = 0;
        while (st.hasMoreTokens())
        {
            values[pos++] = st.nextToken();
        }
        return values;
    }

    /**
     * Joins the elements of the provided array into a single string
     * containing a list of CSV elements.
     *
     * @param list      The list of values to join together.
     * @param separator The separator character.
     * @return          The CSV text.
     */
    public static String join(String[] list, String separator)
    {
        StringBuffer csv = new StringBuffer();
        for (int i = 0; i < list.length; i++)
        {
            if (i > 0)
            {
                csv.append(separator);
            }
            csv.append(list[i]);
        }
        return csv.toString();
    }

    /**
     * Takes a block of text which might have long lines in it and wraps
     * the long lines based on the supplied wrapColumn parameter. It was
     * initially implemented for use by VelocityEmail. If there are tabs
     * in inString, you are going to get results that are a bit strange,
     * since tabs are a single character but are displayed as 4 or 8
     * spaces. Remove the tabs.
     *
     * @param inString   Text which is in need of word-wrapping.
     * @param newline    The characters that define a newline.
     * @param wrapColumn The column to wrap the words at.
     * @return           The text with all the long lines word-wrapped.
     */

    public static String wrapText (String inString, String newline,
                                   int wrapColumn)
    {
        StringTokenizer lineTokenizer = new StringTokenizer (
                inString, newline, true);
        StringBuffer stringBuffer = new StringBuffer();

        while (lineTokenizer.hasMoreTokens ())
        {
            try
            {
                String nextLine = lineTokenizer.nextToken();

                if (nextLine.length() > wrapColumn)
                {
                    // This line is long enough to be wrapped.
                    nextLine = wrapLine (nextLine, newline, wrapColumn);
                }

                stringBuffer.append (nextLine);
            }
            catch (NoSuchElementException nsee)
            {
                // thrown by nextToken(), but I don't know why it would
                break;
            }
        }

        return (stringBuffer.toString());
    }

    /**
     * Wraps a single line of text. Called by wrapText(). I can't
     * think of any good reason for exposing this to the public, 
     * since wrapText should always be used AFAIK.
     *
     * @param line       A line which is in need of word-wrapping.
     * @param newline    The characters that define a newline.
     * @param wrapColumn The column to wrap the words at.
     * @return           A line with newlines inserted.
     */

    protected static String wrapLine (String line, String newline,
                                      int wrapColumn)
    {
        StringBuffer wrappedLine = new StringBuffer();

        while (line.length() > wrapColumn)
        {
            int spaceToWrapAt = line.lastIndexOf (' ', wrapColumn);

            if (spaceToWrapAt >= 0)
            {
                wrappedLine.append (line.substring (0, spaceToWrapAt));
                wrappedLine.append (newline);
                line = line.substring (spaceToWrapAt + 1);
            }

            // This must be a really long word or URL. Pass it
            // through unchanged even though it's longer than the
            // wrapColumn would allow. This behavior could be
            // dependent on a parameter for those situations when
            // someone wants long words broken at line length.
            else
            {
                spaceToWrapAt = line.indexOf (' ', wrapColumn);

                if (spaceToWrapAt >= 0)
                {
                    wrappedLine.append (line.substring (0, spaceToWrapAt));
                    wrappedLine.append (newline);
                    line = line.substring (spaceToWrapAt + 1);
                }
                else
                {
                    wrappedLine.append (line);
                    line = "";
                }
            }
        }

        // Whatever is left in line is short enough to just pass through,
        // just like a small small kidney stone
        wrappedLine.append (line);

        return (wrappedLine.toString());
    }
}
