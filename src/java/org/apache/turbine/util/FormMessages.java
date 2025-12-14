package org.apache.turbine.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Used for adding and accessing messages that relate to a specific form and field. Allows to query for messages by form
 * name and field name. Used together with FormMessage class.
 *
 * @author <a href="mailto:neeme@one.lv">Neeme Praks</a>
 * @version $Id$
 */
public class FormMessages
{
    private final Map<String, List<String>> forms_messages;

    private final Map<String, List<String>> fields_messages;

    private final Map<String, List<String>> messages_fields;

    private final Map<String, List<String>> forms_fields;

    /**
     * Constructor.
     */
    public FormMessages()
    {
        forms_messages = new HashMap<>();
        fields_messages = new HashMap<>();
        messages_fields = new HashMap<>();
        forms_fields = new HashMap<>();
    }

    /**
     * Sets a message for a field of a form. The message is given as a long representing a return code.
     *
     * @param formName A String with the form name.
     * @param fieldName A String with the field name.
     * @param returnCode A long with the return code.
     */
    public void setMessage( String formName, String fieldName, long returnCode )
    {
        setMessage( formName, fieldName, String.valueOf( returnCode ) );
    }

    /**
     * Sets a message for a field of a form. The message is given as a String.
     *
     * @param formName A String with the form name.
     * @param fieldName A String with the field name.
     * @param messageName A String with the message.
     */
    public void setMessage( String formName, String fieldName, String messageName )
    {
        String formFieldName = formName + "-" + fieldName;
        addValue( forms_messages, formName, messageName );
        addValue( fields_messages, formFieldName, messageName );
        addValue( messages_fields, messageName, formFieldName );
        addValue( forms_fields, formName, formFieldName );
    }

    /**
     * Adds a pair key/value to a table, making sure not to add duplicate keys.
     *
     * @param table A Map.
     * @param key A String with the key.
     * @param value A String with value.
     */
    private void addValue( Map<String, List<String>> table, String key, String value )
    {
        List<String> values = table.computeIfAbsent(key, k -> new ArrayList<>());
        if (!values.contains( value))
        {
            values.add(value);
        }
    }

    /**
     * Gets a pair key/value from a table.
     *
     * @param table A Map.
     * @param key A String with the key.
     * @return A List with the pair key/value, or null.
     */
    private final List<String> getValues( Map<String, List<String>> table, String key )
    {
        return table.get( key );
    }

    /**
     * Gets all form messages for a given form.
     *
     * @param formName A String with the form name.
     * @return A FormMessage[].
     */
    public FormMessage[] getFormMessages( String formName )
    {
        List<String> messages = getValues( forms_messages, formName );
        if ( messages != null )
        {
            return messages.stream()
                .map(message -> {
                    FormMessage fm = new FormMessage(formName);
                    fm.setMessage(message);
                    List<String> fields = getValues( messages_fields, message);
                    fields.forEach(field -> {
                        if (formHasField(formName, field))
                        {
                            fm.setFieldName(field);
                        }
                    });

                    return fm;
                })
                .collect(Collectors.toList())
                .toArray(new FormMessage[messages.size()]);
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
    public FormMessage[] getFormMessages( String formName, String fieldName )
    {
        String key = formName + "-" + fieldName;

        List<String> messages = getValues( fields_messages, key );
        String messageName;

        if ( messages != null )
        {
            FormMessage[] result = new FormMessage[messages.size()];
            for ( int i = 0; i < messages.size(); i++ )
            {
                result[i] = new FormMessage( formName, fieldName );
                messageName = messages.get( i );
                result[i].setMessage( messageName );
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
    private boolean formHasField( String formName, String fieldName )
    {
        List<String> fields = getValues( forms_fields, formName );
        for (String field : fields)
        {
            if ( fieldName.equals( field.toString() ) )
            {
                return true;
            }
        }
        return false;
    }
}
