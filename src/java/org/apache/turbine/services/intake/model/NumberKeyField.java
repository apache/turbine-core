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

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import org.apache.torque.om.NumberKey;

import org.apache.turbine.services.intake.IntakeException;
import org.apache.turbine.services.intake.validator.NumberKeyValidator;
import org.apache.turbine.services.intake.xmlmodel.XmlField;

/**
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class NumberKeyField
        extends BigDecimalField
{

    /**
     * Constructor.
     *
     * @param field xml field definition object
     * @param group xml group definition object
     * @throws IntakeException thrown by superclass
     */
    public NumberKeyField(XmlField field, Group group)
            throws IntakeException
    {
        super(field, group);
    }

    /**
     * Sets the default value for a NumberKey field
     *
     * @param prop Parameter for the default values
     */
    public void setDefaultValue(String prop)
    {
        if (prop == null)
        {
            return;
        }

        defaultValue = new NumberKey(prop);
    }

    /**
     * A suitable validator.
     *
     * @return A suitable validator
     */
    protected String getDefaultValidator()
    {
        return NumberKeyValidator.class.getName();
    }

    /**
     * Sets the value of the field from data in the parser.
     */
    protected void doSetValue()
    {
        if (isMultiValued)
        {
            String[] inputs = parser.getStrings(getKey());
            NumberKey[] values = new NumberKey[inputs.length];
            for (int i = 0; i < inputs.length; i++)
            {
                if (StringUtils.isNotEmpty(inputs[i]))
                {
                    values[i] = new NumberKey(
                            canonicalizeDecimalInput(inputs[i]));
                }
                else
                {
                    values[i] = null;
                }
            }
            setTestValue(values);
        }
        else
        {
            String val = parser.getString(getKey());
            if (StringUtils.isNotEmpty(val))
            {
                BigDecimal bd = canonicalizeDecimalInput(val);
                setTestValue(new NumberKey(bd));
            }
            else
            {
                setTestValue(null);
            }
        }
    }
}