package org.apache.turbine.util;

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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Used for adding and accessing messages that relate to a specific
 * form and field.  Allows to query for messages by form name and
 * field name.  Used together with FormMessage class.
 *
 * @author <a href="mailto:neeme@one.lv">Neeme Praks</a>
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
        setMessage(formName, fieldName, String.valueOf(returnCode) );
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
        return (Vector)table.get(key);
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
                result[i] = new FormMessage( formName );
                messageName = (String) messages.elementAt(i);
                result[i].setMessage( messageName );
                fields = getValues(messages_fields, messageName);
                for (int j = 0; j < fields.size(); j++)
                {
                    fieldName = (String) fields.elementAt(j);
                    if (formHasField( formName, fieldName ))
                    {
                        result[i].setFieldName( fieldName );
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
                result[i] = new FormMessage( formName, fieldName );
                messageName = (String) messages.elementAt(i);
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
    private boolean formHasField(String formName,
                                 String fieldName)
    {
        List fields = getValues(forms_fields, formName);
        for (Iterator iter = fields.iterator(); iter.hasNext(); )
        {
            if (fieldName.equals(iter.next().toString()))
            {
                return true;
            }
        }
        return false;
    }
}
