package org.apache.turbine.torque.engine.sql;

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

import java.io.IOException;
import java.io.Reader;
import java.util.Vector;

/**
 * A simple Scanner implementation that scans an
 * sql file into usable tokens.  Used by SQLToAppData.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public class SQLScanner
{
    static private final String white = "\f\r\t\n ";
    static private final String alfa = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static private final String numer = "0123456789";
    static private final String alfanum = alfa+numer;
    static private final String special = ";(),'";
    static private final char commentPound = '#';
    static private final char commentSlash = '/';
    static private final char commentStar = '*';
    static private final char commentDash = '-';
    
    private Reader in;
    private int chr;
    private String token;
    private Vector tokens;

    private int line;
    private int col;

    /**
     * Creates a new scanner with no Reader
     */
    public SQLScanner()
    {
        this (null);
    }

    /**
     * Creates a new scanner with an Input Reader
     */
    public SQLScanner (Reader input)
    {
        setInput (input);
    }

    /**
     * Set the Input
     */
    public void setInput (Reader input)
    {
        in = input;
    }


    /**
     * Reads the next character and increments the line and
     * column counters.
     */
    private void readChar() throws IOException
    {
        chr = in.read();
        if ((char)chr == '\n' || (char)chr == '\r' || (char)chr == '\f')
        {
            col=0;
            line++;
        } else col++;
    }

    /**
     * Scans an identifier.
     */
    private void scanIdentifier () throws IOException
    {
        token = "";
        char c = (char)chr;
        while (chr != -1 && white.indexOf(c) == -1 && special.indexOf(c) == -1)
        {
            token = token+(char)chr;
            readChar();
            c = (char)chr;
        }
        tokens.addElement(new Token (token,line,col));
    }

    /**
     * Scan the input Reader and returns a list
     * of tokens.
     */
    public Vector scan () throws IOException
    {
        line = 1;
        col = 0;
        boolean inComment = false;
        boolean inCommentSlashStar = false;
        boolean inCommentDash = false;

        tokens = new Vector();
        readChar();
        while (chr != -1)
        {
            char c = (char)chr;

            if ((char)c == commentDash)
            {
                readChar();
                if ((char)chr == commentDash)
                {
                    inCommentDash = true;
                }
            }
            
            if (inCommentDash)
            {
                if ((char)c == '\n' || (char)c == '\r')
                {
                    inCommentDash = false;
                }
                readChar();
            }
            else if ((char)c == commentPound)
            {
                inComment = true;
                readChar();
            }
            else if ((char)c == commentSlash)
            {
                readChar();
                if ((char)chr == commentStar)
                {
                    inCommentSlashStar = true;
                }
            }
            else if (inComment || inCommentSlashStar)
            {
                if ((char)c == '*')
                {
                    readChar();
                    if ((char)chr == commentSlash)
                    {
                        inCommentSlashStar = false;
                    }
                }
                else if ((char)c == '\n' || (char)c == '\r')
                {
                    inComment = false;
                }
                readChar();
            }
            else if (alfanum.indexOf(c) >= 0)
            {
                scanIdentifier();
            }
            else if (special.indexOf(c) >= 0)
            {
                tokens.addElement(new Token (""+c,line,col));
                readChar();
            }
            else
            {
                readChar();
            }
        }
        return tokens;
    }
}
