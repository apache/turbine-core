package org.apache.turbine.services.intake.model;

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

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.turbine.services.intake.IntakeException;
import org.apache.turbine.services.intake.xmlmodel.XmlField;

/**
 * Provides helper methods for localizing floating point numbers
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: BigDecimalField.java 223047 2004-07-01 11:30:52Z epugh $
 */
public abstract class AbstractNumberField
        extends Field
{
    /**
     * Constructor.
     *
     * @param field xml field definition object
     * @param group xml group definition object
     * @throws IntakeException thrown by superclass
     */
    public AbstractNumberField(XmlField field, Group group)
            throws IntakeException
    {
        super(field, group);
    }

    /**
     * Canonicalizes an user-inputted <code>BigDecimal</code> string
     * to the system's internal format.
     *
     * @param number Text conforming to a <code>Number</code>
     * description for a set of <code>DecimalFormatSymbols</code>.
     * @return The canonicalized representation.
     */
    protected final String canonicalizeDecimalInput(String number)
    {
        if (getLocale() != null)
        {
            DecimalFormatSymbols internal = new DecimalFormatSymbols(Locale.US);
            DecimalFormatSymbols user = new DecimalFormatSymbols(getLocale());

            if (!internal.equals(user))
            {
                number = number.replace(user.getDecimalSeparator(),
                        internal.getDecimalSeparator());
            }
        }
        return number;
    }
}
