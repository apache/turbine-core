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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Used for adding and accessing messages that relate to a specific
 * form and field.  Allows to query for messages by form name and
 * field name.  Used together with FormMessage class.
 *
 * @version $Id$
 */
public class FormMessages
{
    private Hashtable forms_messages;
    private Hashtable fields_messages;
    private Hashtable messages_fields;
    private Hashtable forms_fields;

    /**
     * Constructor.
     */
    public FormMessages()
    {
        forms_messages = new Hashtable();
        fields_messages = new Hashtable();
        messages_fields = new Hashtable();
        forms_fields = new Hashtable();
    }

    /**
     * Sets a message for a field of a form.  The message is given as
     * a long representing a return code.
     *
     * @param formName A String with the form name.
     * @param fieldName A String with the field name.
     * @param returnCode A long with the return code.
     */
    public void setMessage(String formName,
                           String fieldName,
                           long returnCode)
    {
        setMessage(formName, fieldName, String.valueOf(returnCode));
    }

    /**
     * Sets a message for a field of a form.  The message is given as
     * a String.
     *
     * @param formName A String with the form name.
     * @param fieldName A String with the field name.
     * @param messageName A String with the message.
     */
    public void setMessage(String formName,
                           String fieldName,
                           String messageName)
    {
        fieldName = formName + "-" + fieldName;
        addValue(forms_messages, formName, messageName);
        addValue(fields_messages, fieldName, messageName);
        addValue(messages_fields, messageName, fieldName);
        addValue(forms_fields, formName, fieldName);
    }

    /**
     * Adds a pair key/value to a table, making sure not to add
     * duplicate keys.
     *
     * @param table A Hastable.
     * @param key A String with the key.
     * @param value A String with value.
     */
    private void addValue(Hashtable table,
                          String key,
                          String value)
    {
        Vector values;

        if (!table.containsKey(key))
        {
            values = new Vector();
            values.addElement(value);
            table.put(key, values);
        }
        else
        {
            values = ((Vector) table.get(key));
            if (!values.contains(value))
                values.addElement(value);
        }
    }

    /**
     * Gets a pair key/value from a table.
     *
     * @param table A Hastable.
     * @param key A String with the key.
     * @return A Vector with the pair key/value, or null.
     */
    private final Vector getValues(Hashtable table, String key)
    {
        return (Vector) table.get(key);
    }

    /**
     * Gets all form messages for a given form.
     *
     * @param formName A String with the form name.
     * @return A FormMessage[].
     */
    public FormMessage[] getFormMessages(String formName)
    {
        Vector messages, fields;
        String messageName, fieldName;
        messages = getValues(forms_messages, formName);
        if (messages != null)
        {
            FormMessage[] result = new FormMessage[messages.size()];
            for (int i = 0; i < messages.size(); i++)
            {
                result[i] = new FormMessage(formName);
                messageName = (String) messages.elementAt(i);
                result[i].setMessage(messageName);
                fields = getValues(messages_fields, messageName);
                for (int j = 0; j < fields.size(); j++)
                {
                    fieldName = (String) fields.elementAt(j);
                    if (formHasField(formName, fieldName))
                    {
                        result[i].setFieldName(fieldName);
                    }
                }
            }
            return result;
        }
        return null;
    }

    /**
     * Get form messages for a given form and field.
     *
     * @param formName A String with the form name.
     * @param fieldName A String with the field name.
     * @return A FormMessage[].
     */
    public FormMessage[] getFormMessages(String formName, String fieldName)
    {
        String key = formName + "-" + fieldName;

        Vector messages = getValues(fields_messages, key);
        String messageName;

        if (messages != null)
        {
            FormMessage[] result = new FormMessage[messages.size()];
            for (int i = 0; i < messages.size(); i++)
            {
                result[i] = new FormMessage(formName, fieldName);
                messageName = (String) messages.elementAt(i);
                result[i].setMessage(messageName);
            }
            return result;
        }
        return null;
    }

    /**
     * Check whether a form as a field.
     *
     * @param formName A String with the form name.
     * @param fieldName A String with the field name.
     * @return True if form has the field.
     */
    private boolean formHasField(String formName,
                                 String fieldName)
    {
        List fields = getValues(forms_fields, formName);
        for (Iterator iter = fields.iterator(); iter.hasNext();)
        {
            if (fieldName.equals(iter.next().toString()))
            {
                return true;
            }
        }
        return false;
    }
}
