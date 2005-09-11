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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public abstract class DataStreamParser implements Iterator
{
    /**
     * The constant for empty fields
     */
    protected static final String EMPTYFIELDNAME = "UNKNOWNFIELD";

    /**
     * The list of column names.
     */
    private List columnNames = Collections.EMPTY_LIST;

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
        setColumnNames(columnNames);

        this.characterEncoding = characterEncoding;

        if (this.characterEncoding == null)
        {
            if (in instanceof InputStreamReader)
            {
                this.characterEncoding = ((InputStreamReader) in).getEncoding();
            }

            if (this.characterEncoding == null)
            {
                // try and get the characterEncoding from the reader
                this.characterEncoding = "US-ASCII";
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
        tokenizer.resetSyntax();

        // leave out the comma sign (,), we need it for empty fields
        tokenizer.wordChars(' ', Character.MAX_VALUE);

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
        if (columnNames != null)
        {
            this.columnNames = columnNames;
        }
    }

    /**
     * get the list of column names.
     *
     */
    public List getColumnNames()
    {
        return columnNames;
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
        List columnNames = new ArrayList();
        int fieldCounter = 0;

        if (hasNextRow())
        {
            String colName = null;
            boolean foundEol = false;

            while(!foundEol)
            {
                tokenizer.nextToken();

                if (tokenizer.ttype == '"'
                        || tokenizer.ttype == StreamTokenizer.TT_WORD)
                {
                    // tokenizer.ttype is either '"' or TT_WORD
                    colName = tokenizer.sval;
                }
                else
                {
                    // fieldSeparator, EOL or EOF
                    fieldCounter++;

                    if (colName == null)
                    {
                        colName = EMPTYFIELDNAME + fieldCounter;
                    }

                    columnNames.add(colName);
                    colName = null;
                }

                // EOL and EOF are checked independently from existing fields.
                if (tokenizer.ttype == StreamTokenizer.TT_EOL)
                {
                    foundEol = true;
                }
                else if (tokenizer.ttype == StreamTokenizer.TT_EOF)
                {
                    // Keep this token in the tokenizer for hasNext()
                    tokenizer.pushBack();
                    foundEol = true;
                }
            }

            setColumnNames(columnNames);
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

        String currVal = "";
        String colName = null;

        boolean foundEol = false;
        while (!foundEol || it.hasNext())
        {
            if (!foundEol)
            {
                tokenizer.nextToken();
            }

            if (colName == null && it.hasNext())
            {
                colName = String.valueOf(it.next());
            }

            if (tokenizer.ttype == '"'
                    || tokenizer.ttype == StreamTokenizer.TT_WORD)
            {
                // tokenizer.ttype is either '"' or TT_WORD
                currVal = tokenizer.sval;
            }
            else
            {
                // fieldSeparator, EOL or EOF
                lineValues.add(colName, currVal);
                colName = null;
                currVal = "";
            }

            // EOL and EOF are checked independently from existing fields.
            if (tokenizer.ttype == StreamTokenizer.TT_EOL)
            {
                foundEol = true;
            }
            else if (tokenizer.ttype == StreamTokenizer.TT_EOF)
            {
                // Keep this token in the tokenizer for hasNext()
                tokenizer.pushBack();
                foundEol = true;
            }
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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
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
