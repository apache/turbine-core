package org.apache.turbine.util;

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

import java.util.Vector;

/**
 * A message class for holding information about a message that
 * relates to a specific form and field.  Used together with
 * FormMessages class.
 *
 * @author <a href="mailto:neeme@one.lv">Neeme Praks</a>
 * @version $Id$
 */
public class FormMessage
{
    private String message;
    private String formName;
    private Vector fieldNames;

    /**
     * Constructor.
     */
    public FormMessage()
    {
        fieldNames = new Vector();
    }

    /**
     * Constructor.
     *
     * @param formName A String with the form name.
     */
    public FormMessage(String formName)
    {
        this();
        setFormName(formName);
    }

    /**
     * Constructor.
     *
     * @param formName A String with the form name.
     * @param fieldName A String with the field name.
     */
    public FormMessage(String formName,
                       String fieldName)
    {
        this(formName);
        setFieldName(fieldName);
    }

    /**
     * Constructor.
     *
     * @param formName A String with the form name.
     * @param fieldName A String with the field name.
     * @param message A String with the message.
     */
    public FormMessage(String formName,
                       String fieldName,
                       String message)
    {
        this(formName, fieldName);
        setMessage(message);
    }

    /**
     * Return the message.
     *
     * @return A String with the message.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Return the form name.
     *
     * @return A String with the form name.
     */
    public String getFormName()
    {
        return formName;
    }

    /**
     * Return the field names.
     *
     * @return A String[] with the field names.
     */
    public String[] getFieldNames()
    {
        String[] result = new String[fieldNames.size()];
        fieldNames.copyInto(result);
        return result;
    }

    /**
     * Set the message.
     *
     * @param message A String with the message.
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Set the form name.
     *
     * @param formName A String with the form name.
     */
    public void setFormName(String formName)
    {
        this.formName = formName;
    }

    /**
     * Adds one field name.
     *
     * @param fieldName A String with the field name.
     */
    public void setFieldName(String fieldName)
    {
        fieldNames.addElement(fieldName);
    }
}
