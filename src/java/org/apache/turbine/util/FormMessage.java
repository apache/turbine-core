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

    /**
     * Write out the contents of the message in a friendly manner.
     *
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer("formName:" + getFormName() + ", fieldNames:");
        for (int i = 0; i< getFieldNames().length; i++){
            sb.append(getFieldNames()[i] + " ");
        }
        sb.append(", message:" + getMessage());

        return sb.toString();
    }
}
