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


import java.io.Reader;

import java.util.List;

/**
 * TSVParser is used to parse a stream with tab-separated values and
 * generate ParameterParser objects which can be used to
 * extract the values in the desired type.
 *
 * <p>The class extends the abstract class DataStreamParser and implements
 * initTokenizer with suitable values for TSV files to provide this
 * functionality.
 *
 * <p>The class (indirectly through DataStreamParser) implements the
 * java.util.Iterator interface for convenience.
 * This allows simple use in a Velocity template for example:
 *
 * <pre>
 * #foreach ($row in $tsvfile)
 *   Name: $row.Name
 *   Description: $row.Description
 * #end
 * </pre>
 *
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Id$
 * @deprecated Use org.apache.turbine.util.parser.TSVParser instead.
 */
public class TSVParser 
        extends org.apache.turbine.util.parser.TSVParser
{
    /**
     * Create a new TSVParser instance. Requires a Reader to read the
     * tab-separated values from. The column headers must be set
     * independently either explicitly, or by reading the first line
     * of the TSV values.
     *
     * @param in the input reader.
     */
    public TSVParser(Reader in)
    {
        super(in, null, null);
    }

    /**
     * Create a new TSVParser instance. Requires a Reader to read the
     * tab-separated values from, and a list of column names.
     *
     * @param in the input reader.
     * @param columnNames a list of column names.
     */
    public TSVParser(Reader in, List columnNames)
    {
        super(in, columnNames, null);
    }

    /**
     * Create a new TSVParser instance. Requires a Reader to read the
     * tab-separated values from, a list of column names and a
     * character encoding.
     *
     * @param in the input reader.
     * @param columnNames a list of column names.
     * @param characterEncoding the character encoding of the input.
     */
    public TSVParser(Reader in, List columnNames, String characterEncoding)
    {
        super(in, columnNames, characterEncoding);
    }
}
