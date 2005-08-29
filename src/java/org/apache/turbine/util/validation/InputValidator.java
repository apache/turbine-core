package org.apache.turbine.util.validation;

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

/**
 * @author <a href="mailto:mikeh@ncsa.uiuc.edu">Mike Haberman</a>
 * @version $Id$
 * @deprecated Use Intake or commons-validator
 */
public abstract class InputValidator
{
    public static final boolean AllowNullInput = true;// allow null
    public static final int NoMaxSize = -1;  // no size restrictions
    public static final String EmptyArgv = "";  // no optional arguments

    // default error messages
    private static String NullInputError = "Null Input Not Allowed";
    private static String MaxSizeExceededError = "Maximum Size Exceeded";

    private boolean allowNullInput;
    private int maxSize;
    private String argv;

    /**
     * default Constructor,
     */
    public InputValidator()
    {
        this(AllowNullInput, NoMaxSize, EmptyArgv);
    }

    /**
     * Constructor,
     * @param boolean allowNullInput
     * @param int maxSize
     * @param String argv
     */
    public InputValidator(boolean allowNullInput,
                          int maxSize,
                          String argv)
    {
        this.allowNullInput = allowNullInput;
        this.maxSize = maxSize;
        this.argv = argv;
    }

    /**
     * @param boolean allowNullInput, set allowNullInput
     */
    public void setAllowNullInput(boolean allowNullInput)
    {
        this.allowNullInput = allowNullInput;
    }

    /**
     * @param int maxSize, set maxSize
     */
    public void setMaxSize(int maxSize)
    {
        this.maxSize = maxSize;
    }

    /**
     * @param String argv, set argv
     */
    public void setArgv(String argv)
    {
        this.argv = argv;
    }

    /**
     * @param String input, input to be checked
     * @return boolean, whether or not the input is valid
     */
    public boolean isValid(String input)
    {
        try
        {
            checkInput(input);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * @param String input, input to be checked
     * @return String, error message or null
     */
    public String getErrorMessage(String input)
    {
        try
        {
            checkInput(input);
        }
        catch (Exception e)
        {
            return e.toString();
        }

        // there is no error
        return null;
    }

    /**
     * @param String value
     * @exception Exception, a generic exception.
     */
    public void checkInput(String value)
            throws Exception
    {
        int size = 0;
        if (value != null)
        {
            value = value.trim();
            size = value.length();
        }

        if (!allowNullInput && value == null)
        {
            throw new Exception(NullInputError);
        }

        if (maxSize != NoMaxSize && size > maxSize)
        {
            throw new Exception(MaxSizeExceededError);
        }

        // allow the subclass to check specifics
        check(value);
    }

    /**
     * @return String, the expected format of the input
     */
    public abstract String getExpectedFormat();

    /**
     * @param String input, input to be checked
     * all subclasses must define this method
     */
    protected abstract void check(String input)
            throws Exception;

}
