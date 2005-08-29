package org.apache.turbine.util.parser;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Id$
 */
public abstract class DataStreamParser implements Iterator
{
    /** Logging */
    private static Log log = LogFactory.getLog(DataStreamParser.class);

    /**
     * Conditional compilation flag.
     */
    private static final boolean DEBUG = false;

    /**
     * The constant for empty fields
     */
    protected static final String EMPTYFIELDNAME = "UNKNOWNFIELD";

    /**
     * The list of column names.
     */
    private List columnNames;

    /**
     * The stream tokenizer for reading values from the input reader.
     */
    private StreamTokenizer tokenizer;

    /**
     * The parameter parser holding the values of columns for the current line.
     */
    private ValueParser lineValues;

    /**
     * Indicates whether or not the tokenizer has read anything yet.
     */
    private boolean neverRead = true;

    /**
     * The character encoding of the input
     */
    private String characterEncoding;

    /**
     * The fieldseperator, which can be almost any char
     */
    private char fieldSeparator;

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
        this.columnNames = columnNames;
        this.characterEncoding = characterEncoding;

        if (this.characterEncoding == null)
        {
            // try and get the characterEncoding from the reader
            this.characterEncoding = "US-ASCII";
            try
            {
                this.characterEncoding = ((InputStreamReader) in).getEncoding();
            }
            catch (ClassCastException e)
            {
            }
        }

        tokenizer = new StreamTokenizer(new BufferedReader(in));
        initTokenizer(tokenizer);
    }

    /**
     * Initialize the StreamTokenizer instance used to read the lines
     * from the input reader. This must be implemented in subclasses to
     * set up other tokenizing properties.
     *
     * @param tokenizer the tokenizer to adjust
     */
    protected void initTokenizer(StreamTokenizer tokenizer)
    {
        // set all numeric characters as ordinary characters
        // (switches off number parsing)
        tokenizer.ordinaryChars('0', '9');
        tokenizer.ordinaryChars('-', '-');
        tokenizer.ordinaryChars('.', '.');

        // leave out the comma sign (,), we need it for empty fields

        tokenizer.wordChars(' ', Integer.MAX_VALUE);

        // and  set the quote mark as the quoting character
        tokenizer.quoteChar('"');

        // and finally say that end of line is significant
        tokenizer.eolIsSignificant(true);
    }

    /**
     * This method must be called to setup the field seperator
     * @param fieldSeparator the char which separates the fields
     */
    public void setFieldSeparator(char fieldSeparator)
    {
        this.fieldSeparator = fieldSeparator;
        // make this field also an ordinary char by default.
        tokenizer.ordinaryChar(fieldSeparator);
    }

    /**
     * Set the list of column names explicitly.
     *
     * @param columnNames A list of column names.
     */
    public void setColumnNames(List columnNames)
    {
        this.columnNames = columnNames;
    }

    /**
     * Read the list of column names from the input reader using the
     * tokenizer. If fieldNames are empty, we use the current fieldNumber
     * + the EMPTYFIELDNAME to make one up.
     *
     * @exception IOException an IOException occurred.
     */
    public void readColumnNames()
            throws IOException
    {
        columnNames = new ArrayList();
        int lastTtype = 0;
        int fieldCounter = 1;

        neverRead = false;
        tokenizer.nextToken();
        while (tokenizer.ttype == StreamTokenizer.TT_WORD || tokenizer.ttype == StreamTokenizer.TT_EOL
                || tokenizer.ttype == '"' || tokenizer.ttype == fieldSeparator)
        {
            if (tokenizer.ttype != fieldSeparator && tokenizer.ttype != StreamTokenizer.TT_EOL)
            {
                columnNames.add(tokenizer.sval);
                fieldCounter++;
            }
            else if (tokenizer.ttype == fieldSeparator && lastTtype == fieldSeparator)
            {
                // we have an empty field name
                columnNames.add(EMPTYFIELDNAME + fieldCounter);
                fieldCounter++;
            }
            else if (lastTtype == fieldSeparator && tokenizer.ttype == StreamTokenizer.TT_EOL)
            {
                columnNames.add(EMPTYFIELDNAME + fieldCounter);
                break;
            }
            else if (tokenizer.ttype == StreamTokenizer.TT_EOL)
            {
                break;
            }
            lastTtype = tokenizer.ttype;
            tokenizer.nextToken();
        }
    }

    /**
     * Determine whether a further row of values exists in the input.
     *
     * @return true if the input has more rows.
     * @exception IOException an IOException occurred.
     */
    public boolean hasNextRow()
            throws IOException
    {
        // check for end of line ensures that an empty last line doesn't
        // give a false positive for hasNextRow
        if (neverRead || tokenizer.ttype == StreamTokenizer.TT_EOL)
        {
            tokenizer.nextToken();
            tokenizer.pushBack();
            neverRead = false;
        }
        return tokenizer.ttype != StreamTokenizer.TT_EOF;
    }

    /**
     * Returns a ValueParser object containing the next row of values.
     *
     * @return a ValueParser object.
     * @exception IOException an IOException occurred.
     * @exception NoSuchElementException there are no more rows in the input.
     */
    public ValueParser nextRow()
            throws IOException, NoSuchElementException
    {
        if (!hasNextRow())
        {
            throw new NoSuchElementException();
        }

        if (lineValues == null)
        {
            lineValues = new BaseValueParser(characterEncoding);
        }
        else
        {
            lineValues.clear();
        }

        Iterator it = columnNames.iterator();
        tokenizer.nextToken();
        while (tokenizer.ttype == StreamTokenizer.TT_WORD
                || tokenizer.ttype == '"' || tokenizer.ttype == fieldSeparator)
        {
            int lastTtype = 0;
            // note this means that if there are more values than
            // column names, the extra values are discarded.
            if (it.hasNext())
            {
                String colname = it.next().toString();
                String colval = tokenizer.sval;
                if (tokenizer.ttype != fieldSeparator && lastTtype != fieldSeparator)
                {
                    if (DEBUG)
                    {
                        log.debug("DataStreamParser.nextRow(): " +
                                colname + "=" + colval);
                    }
                    lineValues.add(colname, colval);
                }
                else if (tokenizer.ttype == fieldSeparator && lastTtype != fieldSeparator)
                {
                    lastTtype = tokenizer.ttype;
                    tokenizer.nextToken();
                    if (tokenizer.ttype != fieldSeparator && tokenizer.sval != null)
                    {
                        lineValues.add(colname, tokenizer.sval);
                    }
                    else if (tokenizer.ttype == StreamTokenizer.TT_EOL)
                    {
                        tokenizer.pushBack();
                    }
                }
            }
            tokenizer.nextToken();
        }

        return lineValues;
    }

    /**
     * Determine whether a further row of values exists in the input.
     *
     * @return true if the input has more rows.
     */
    public boolean hasNext()
    {
        boolean hasNext = false;

        try
        {
            hasNext = hasNextRow();
        }
        catch (IOException e)
        {
            log.error("IOException in CSVParser.hasNext", e);
        }

        return hasNext;
    }

    /**
     * Returns a ValueParser object containing the next row of values.
     *
     * @return a ValueParser object as an Object.
     * @exception NoSuchElementException there are no more rows in the input
     *                                   or an IOException occurred.
     */
    public Object next()
            throws NoSuchElementException
    {
        Object nextRow = null;

        try
        {
            nextRow = nextRow();
        }
        catch (IOException e)
        {
            log.error("IOException in CSVParser.next", e);
            throw new NoSuchElementException();
        }

        return nextRow;
    }

    /**
     * The optional Iterator.remove method is not supported.
     *
     * @exception UnsupportedOperationException the operation is not supported.
     */
    public void remove()
            throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }
}
