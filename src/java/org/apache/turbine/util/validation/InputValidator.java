package org.apache.turbine.util.validation;

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

/**
 * @author <a href="mailto:mikeh@ncsa.uiuc.edu">Mike Haberman</a>
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
