package org.apache.turbine.util.parser;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.List;

/**
 * CSVParser is used to parse a stream with comma-separated values and
 * generate ParameterParser objects which can be used to
 * extract the values in the desired type.
 *
 * <p>The class extends the abstract class DataStreamParser and implements
 * initTokenizer with suitable values for CSV files to provide this
 * functionality.
 *
 * <p>The class (indirectly through DataStreamParser) implements the
 * java.util.Iterator interface for convenience.
 * This allows simple use in a Velocity template for example:
 *
 * <pre>
 * #foreach ($row in $csvfile)
 *   Name: $row.Name
 *   Description: $row.Description
 * #end
 * </pre>
 *
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Id$
 */
public class CSVParser extends DataStreamParser
{
    /**
     * Create a new CSVParser instance. Requires a Reader to read the
     * comma-separated values from. The column headers must be set
     * independently either explicitly, or by reading the first line
     * of the CSV values.
     *
     * @param in the input reader.
     */
    public CSVParser(Reader in)
    {
        super(in, null, null);
    }

    /**
     * Create a new CSVParser instance. Requires a Reader to read the
     * comma-separated values from, and a list of column names.
     *
     * @param in the input reader.
     * @param columnNames a list of column names.
     */
    public CSVParser(Reader in, List columnNames)
    {
        super(in, columnNames, null);
    }

    /**
     * Create a new CSVParser instance. Requires a Reader to read the
     * comma-separated values from, a list of column names and a
     * character encoding.
     *
     * @param in the input reader.
     * @param columnNames a list of column names.
     * @param characterEncoding the character encoding of the input.
     */
    public CSVParser(Reader in, List columnNames, String characterEncoding)
    {
        super(in, columnNames, characterEncoding);
    }

    /**
     * Initialize the StreamTokenizer instance used to read the lines
     * from the input reader.
     * It is now only needed to set the fieldSeparator
     */
    protected void initTokenizer(StreamTokenizer tokenizer)
    {
        // everything is default so let's call super
        super.initTokenizer(tokenizer);
        // set the field separator to comma
        super.setFieldSeparator(',');

    }
}
