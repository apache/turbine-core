package org.apache.turbine.services.intake.model;

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

import org.apache.commons.fileupload.FileItem;

import org.apache.turbine.services.intake.IntakeException;
import org.apache.turbine.services.intake.validator.FileValidator;
import org.apache.turbine.services.intake.validator.ValidationException;
import org.apache.turbine.services.intake.xmlmodel.XmlField;
import org.apache.turbine.util.TurbineRuntimeException;
import org.apache.turbine.util.parser.ParameterParser;
import org.apache.turbine.util.parser.ValueParser;

/**
 * @deprecated Use the Fulcrum Intake component instead.
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class FileItemField
        extends Field
{

    /**
     * Constructor.
     *
     * @param field xml field definition object
     * @param group xml group definition object
     * @throws IntakeException thrown by superclass
     */
    public FileItemField(XmlField field, Group group)
            throws IntakeException
    {
        super(field, group);
    }

    /**
     * It is not possible to set the default value for this field type.
     * Calling this method with a non-null parameter will result in a
     * TurbineRuntimeException
     *
     * @param prop Parameter for the default values
     * @throws TurbineRuntimeException
     */
    public void setDefaultValue(String prop)
    {
        if (prop != null)
        {
            throw new TurbineRuntimeException(
                    "Default values are not valid for "
                    + this.getClass().getName());
        }

        defaultValue = null;
    }

    /**
     * It is not possible to set the empty value for this field type.
     * Calling this method with a non-null parameter will result in a
     * TurbineRuntimeException
     *
     * @param prop Parameter for the empty values
     * @throws TurbineRuntimeException
     */
    public void setEmptyValue(String prop)
    {
        if (prop != null)
        {
            throw new TurbineRuntimeException(
                    "Empty values are not valid for "
                    + this.getClass().getName());
        }

        emptyValue = null;
    }

    /**
     * A suitable validator.
     *
     * @return A suitable validator
     */
    protected String getDefaultValidator()
    {
        return FileValidator.class.getName();
    }

    /**
     * Method called when this field (the group it belongs to) is
     * pulled from the pool.  The request data is searched to determine
     * if a value has been supplied for this field.  if so, the value
     * is validated.
     *
     * @param vp a <code>ValueParser</code> value
     * @return a <code>Field</code> value
     * @exception IntakeException if an error occurs
     */
    public Field init(ValueParser vp)
            throws IntakeException
    {
        try
        {
            super.parser = (ParameterParser) vp;
        }
        catch (ClassCastException e)
        {
            throw new IntakeException(
                    "FileItemFields can only be used with ParameterParser");
        }

        validFlag = true;

        if (parser.containsKey(getKey()))
        {
            setFlag = true;
            validate();
        }

        initialized = true;
        return this;
    }

    /**
     * Compares request data with constraints and sets the valid flag.
     *
     * @return the valid flag
     */
    protected boolean validate()
    {
        ParameterParser pp = (ParameterParser) super.parser;
        if (isMultiValued)
        {
            FileItem[] ss = pp.getFileItems(getKey());
            // this definition of not set might need refined.  But
            // not sure the situation will arise.
            if (ss.length == 0)
            {
                setFlag = false;
            }

            if (validator != null)
            {
                for (int i = 0; i < ss.length; i++)
                {
                    try
                    {
                        ((FileValidator) validator).assertValidity(ss[i]);
                    }
                    catch (ValidationException ve)
                    {
                        setMessage(ve.getMessage());
                    }
                }
            }

            if (setFlag && validFlag)
            {
                doSetValue();
            }
        }
        else
        {
            FileItem s = pp.getFileItem(getKey());
            if (s == null || s.getSize() == 0)
            {
                setFlag = false;
            }

            if (validator != null)
            {
                try
                {
                    ((FileValidator) validator).assertValidity(s);

                    if (setFlag)
                    {
                        doSetValue();
                    }
                }
                catch (ValidationException ve)
                {
                    setMessage(ve.getMessage());
                }
            }
            else if (setFlag)
            {
                doSetValue();
            }
        }

        return validFlag;
    }

    /**
     * Sets the value of the field from data in the parser.
     */
    protected void doSetValue()
    {
        ParameterParser pp = (ParameterParser) super.parser;
        if (isMultiValued)
        {
            setTestValue(pp.getFileItems(getKey()));
        }
        else
        {
            setTestValue(pp.getFileItem(getKey()));
        }
    }
}
