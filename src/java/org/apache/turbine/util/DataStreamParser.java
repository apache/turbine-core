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
 * DataStreamParser is used to parse a stream with a fixed format and
 * generate ValueParser objects which can be used to extract the values
 * in the desired type.
 *
 * <p>The class itself is abstract - a concrete subclass which implements
 * the initTokenizer method such as CSVParser or TSVParser is required
 * to use the functionality.
 *
 * <p>The class implements the java.util.Iterator interface for convenience.
 * This allows simple use in a Velocity template for example:
 *
 * <pre>
 * #foreach ($row in $datastream)
 *   Name: $row.Name
 *   Description: $row.Description
 * #end
 * </pre>
 *
 * @version $Id$
 * @deprecated Use org.apache.turbine.util.parser.DataStreamParser instead.
 */
public abstract class DataStreamParser 
        extends org.apache.turbine.util.parser.DataStreamParser
{
    /**
     * Create a new DataStreamParser instance. Requires a Reader to read the
     * comma-separated values from, a list of column names and a
     * character encoding.
     *
     * @param in the input reader.
     * @param columnNames a list of column names.
     * @param characterEncoding the character encoding of the input.
     */
    public DataStreamParser(Reader in, List columnNames,
                            String characterEncoding)
    {
        super(in, columnNames, characterEncoding);
    }
}
