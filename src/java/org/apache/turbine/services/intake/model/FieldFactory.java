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

import java.util.HashMap;
import java.util.Map;

import org.apache.turbine.services.intake.IntakeException;
import org.apache.turbine.services.intake.xmlmodel.XmlField;

/**
 * Creates Field objects.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public abstract class FieldFactory
{
    private static Map fieldCtors = initFieldCtors();

    private static Map initFieldCtors()
    {
        fieldCtors = new HashMap();

        fieldCtors.put("int", new FieldFactory.FieldCtor()
        {
            public Field getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new IntegerField(f, g);
            }
        }
        );
        fieldCtors.put("boolean", new FieldFactory.FieldCtor()
        {
            public Field getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new BooleanField(f, g);
            }
        }
        );
        fieldCtors.put("String", new FieldFactory.FieldCtor()
        {
            public Field getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new StringField(f, g);
            }
        }
        );
        fieldCtors.put("BigDecimal", new FieldFactory.FieldCtor()
        {
            public Field getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new BigDecimalField(f, g);
            }
        }
        );
        fieldCtors.put("NumberKey", new FieldFactory.FieldCtor()
        {
            public Field getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new NumberKeyField(f, g);
            }
        }
        );
        fieldCtors.put("ComboKey", new FieldFactory.FieldCtor()
        {
            public Field getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new ComboKeyField(f, g);
            }
        }
        );
        fieldCtors.put("StringKey", new FieldFactory.FieldCtor()
        {
            public Field getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new StringKeyField(f, g);
            }
        }
        );
        fieldCtors.put("FileItem", new FieldFactory.FieldCtor()
        {
            public Field getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new FileItemField(f, g);
            }
        }
        );
        fieldCtors.put("DateString", new FieldFactory.FieldCtor()
        {
            public Field getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new DateStringField(f, g);
            }
        }
        );
        fieldCtors.put("float", new FieldFactory.FieldCtor()
        {
            public Field getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new FloatField(f, g);
            }
        }
        );
        fieldCtors.put("double", new FieldFactory.FieldCtor()
        {
            public Field getInstance(XmlField f, Group g)
                    throws IntakeException
            {
                return new DoubleField(f, g);
            }
        }
        );
        return fieldCtors;
    }

    private static abstract class FieldCtor
    {
        public Field getInstance(XmlField f, Group g) throws IntakeException
        {
            return null;
        }
    }

    /**
     * Creates a Field object appropriate for the type specified
     * in the xml file.
     *
     * @param xmlField a <code>XmlField</code> value
     * @return a <code>Field</code> value
     * @throws IntakeException indicates that an unknown type was specified for a field.
     */
    public static final Field getInstance(XmlField xmlField, Group xmlGroup)
            throws IntakeException
    {
        FieldCtor fieldCtor = null;
        Field field = null;
        String type = xmlField.getType();

        fieldCtor = (FieldCtor) fieldCtors.get(type);
        if (fieldCtor == null)
        {
            throw new IntakeException("An Unsupported type has been specified for " +
                    xmlField.getName() + " in group " + xmlGroup.getIntakeGroupName() + " type = " + type);
        }
        else
        {
            field = fieldCtor.getInstance(xmlField, xmlGroup);
        }

        return field;
    }
}
