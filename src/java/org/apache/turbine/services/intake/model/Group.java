package org.apache.turbine.services.intake.model;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.turbine.om.Retrievable;
import org.apache.turbine.services.intake.IntakeException;
import org.apache.turbine.services.intake.TurbineIntake;
import org.apache.turbine.services.intake.xmlmodel.AppData;
import org.apache.turbine.services.intake.xmlmodel.XmlField;
import org.apache.turbine.services.intake.xmlmodel.XmlGroup;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.ValueParser;

/**
 * Holds a group of Fields
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class Group
{
    public static final String EMPTY = "";

    /*
     * An id representing a new object.
     */
    public static final String NEW = "_0";

    private static final Log log;
    private static final boolean isDebugEnabled;

    static
    {
        log = LogFactory.getLog(Group.class);
        isDebugEnabled = log.isDebugEnabled();
    }

    /**
     * The key used to represent this group in a parameter.
     * This key is usually a prefix as part of a field key.
     */
    protected final String gid;

    /**
     * The name used in templates and java code to refer to this group.
     */
    protected final String name;

    /**
     * The number of Groups with the same name that will be pooled.
     */
    private final int poolCapacity;

    /**
     * A map of the fields in this group mapped by field name.
     */
    protected Map fields;

    /**
     * Map of the fields by mapToObject
     */
    protected Map mapToObjectFields;

    /**
     * An array of fields in this group.
     */
    protected Field[] fieldsArray;

    /**
     * The object id used to associate this group to a bean
     * for one request cycle
     */
    protected String oid;

    /**
     * The object containing the request data
     */
    protected ValueParser pp;

    /**
     * A flag to help prevent duplicate hidden fields declaring this group.
     */
    protected boolean isDeclared;

    /**
     * Constructs a new Group based on the xml specification.  Groups are
     * instantiated and pooled by the IntakeService and should not
     * be instantiated otherwise.
     *
     * @param group a <code>XmlGroup</code> value
     * @exception IntakeException if an error occurs in other classes
     */
    public Group(XmlGroup group) throws IntakeException
    {
        gid = group.getKey();
        name = group.getName();
        poolCapacity = Integer.parseInt(group.getPoolCapacity());

        List inputFields = group.getFields();
        int size = inputFields.size();
        fields = new HashMap((int) (1.25 * size + 1));
        mapToObjectFields = new HashMap((int) (1.25 * size + 1));
        fieldsArray = new Field[size];
        for (int i = size - 1; i >= 0; i--)
        {
            XmlField f = (XmlField) inputFields.get(i);
            Field field = FieldFactory.getInstance(f, this);
            fieldsArray[i] = field;
            fields.put(f.getName(), field);

            // map fields by their mapToObject
            List tmpFields = (List) mapToObjectFields.get(f.getMapToObject());
            if (tmpFields == null)
            {
                tmpFields = new ArrayList(size);
                mapToObjectFields.put(f.getMapToObject(), tmpFields);
            }
            tmpFields.add(field);
        }

        // Change the mapToObjectFields values to Field[]
        for (Iterator keys = mapToObjectFields.keySet().iterator(); keys.hasNext();)
        {
            Object key = keys.next();
            List tmpFields = (List) mapToObjectFields.get(key);
            mapToObjectFields.put(key,
                    tmpFields.toArray(new Field[tmpFields.size()]));
        }
    }

    /**
     * Initializes the default Group using parameters.
     *
     * @param pp a <code>ValueParser</code> value
     * @return this Group
     */
    public Group init(ValueParser pp) throws TurbineException
    {
        return init(NEW, pp);
    }

    /**
     * Initializes the Group with parameters from RunData
     * corresponding to key.
     *
     * @param pp a <code>ValueParser</code> value
     * @return this Group
     */
    public Group init(String key, ValueParser pp) throws IntakeException
    {
        this.oid = key;
        this.pp = pp;
        for (int i = fieldsArray.length - 1; i >= 0; i--)
        {
            fieldsArray[i].init(pp);
        }
        return this;
    }

    /**
     * Initializes the group with properties from an object.
     *
     * @param obj a <code>Persistent</code> value
     * @return a <code>Group</code> value
     */
    public Group init(Retrievable obj)
    {
        this.oid = obj.getQueryKey();

        Class cls = obj.getClass();
        while (cls != null)
        {
            Field[] flds = (Field[]) mapToObjectFields.get(cls.getName());
            if (flds != null)
            {
                for (int i = flds.length - 1; i >= 0; i--)
                {
                    flds[i].init(obj);
                }
            }

            cls = cls.getSuperclass();
        }

        return this;
    }

    /**
     * Gets a list of the names of the fields stored in this object.
     *
     * @return A String array containing the list of names.
     */
    public String[] getFieldNames()
    {
        String nameList[] = new String[fieldsArray.length];
        for (int i = 0; i < nameList.length; i++)
        {
            nameList[i] = fieldsArray[i].name;
        }
        return nameList;
    }

    /**
     * Return the name given to this group.  The long name is to
     * avoid conflicts with the get(String key) method.
     *
     * @return a <code>String</code> value
     */
    public String getIntakeGroupName()
    {
        return name;
    }

    /**
     * Get the number of Group objects that will be pooled.
     *
     * @return an <code>int</code> value
     */
    public int getPoolCapacity()
    {
        return poolCapacity;
    }

    /**
     * Get the part of the key used to specify the group.
     * This is specified in the key attribute in the xml file.
     *
     * @return a <code>String</code> value
     */
    public String getGID()
    {
        return gid;
    }

    /**
     * Get the part of the key that distinguishes a group
     * from others of the same name.
     *
     * @return a <code>String</code> value
     */
    public String getOID()
    {
        return oid;
    }

    /**
     * Concatenation of gid and oid.
     *
     * @return a <code>String</code> value
     */
    public String getObjectKey()
    {
        return gid + oid;
    }

    /**
     * Describe <code>getObjects</code> method here.
     *
     * @param pp a <code>ValueParser</code> value
     * @return an <code>ArrayList</code> value
     * @exception IntakeException if an error occurs
     */
    public ArrayList getObjects(ValueParser pp) throws IntakeException
    {
        ArrayList objs = null;
        String[] oids = pp.getStrings(gid);
        if (oids != null)
        {
            objs = new ArrayList(oids.length);
            for (int i = oids.length - 1; i >= 0; i--)
            {
                objs.add(TurbineIntake.getGroup(name).init(oids[i], pp));
            }
        }
        return objs;
    }

    /**
     * Get the Field .
     * @return Field.
     * @throws IntakeException indicates the field could not be found.
     */
    public Field get(String fieldName)
            throws IntakeException
    {
        if (fields.containsKey(fieldName))
        {
            return (Field) fields.get(fieldName);
        }
        else
        {
            throw new IntakeException("Intake Field name: " + fieldName +
                    " not found!");
        }
    }

    /**
     * Performs an AND between all the fields in this group.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isAllValid()
    {
        boolean valid = true;
        for (int i = fieldsArray.length - 1; i >= 0; i--)
        {
            valid &= fieldsArray[i].isValid();
            if (isDebugEnabled && !fieldsArray[i].isValid())
            {
                log.debug("Group(" + oid + "): " + name + "; Field: "
                        + fieldsArray[i].name + "; value=" +
                        fieldsArray[i].getValue() + " is invalid!");
            }
        }
        return valid;
    }

    /**
     * Calls a setter methods on obj, for fields which have been set.
     *
     * @param obj Object to be set with the values from the group.
     * @throws IntakeException indicates that a failure occurred while
     * executing the setter methods of the mapped object.
     */
    public void setProperties(Object obj) throws IntakeException
    {
        Class cls = obj.getClass();
        while (cls != null)
        {
            Field[] flds = (Field[]) mapToObjectFields.get(cls.getName());
            if (flds != null)
            {
                for (int i = flds.length - 1; i >= 0; i--)
                {
                    flds[i].setProperty(obj);
                }
            }

            cls = cls.getSuperclass();
        }
    }

    /**
     * Calls a setter methods on obj, for fields which pass validity tests.
     * In most cases one should call Intake.isAllValid() and then if that
     * test passes call setProperties.  Use this method when some data is
     * known to be invalid, but you still want to set the object properties
     * that are valid.
     */
    public void setValidProperties(Object obj)
    {
        Class cls = obj.getClass();
        while (cls != null)
        {
            Field[] flds = (Field[]) mapToObjectFields.get(cls.getName());
            if (flds != null)
            {
                for (int i = flds.length - 1; i >= 0; i--)
                {
                    try
                    {
                        flds[i].setProperty(obj);
                    }
                    catch (Exception e)
                    {
                        // just move on to next field
                    }
                }
            }

            cls = cls.getSuperclass();
        }
    }

    /**
     * Calls getter methods on objects that are known to Intake
     * so that field values in forms can be initialized from
     * the values contained in the intake tool.
     *
     * @param obj Object that will be used to as a source of data for
     * setting the values of the fields within the group.
     * @throws IntakeException indicates that a failure occurred while
     * executing the setter methods of the mapped object.
     */
    public void getProperties(Object obj) throws IntakeException
    {
        Class cls = obj.getClass();
        while (cls != null)
        {
            Field[] flds = (Field[]) mapToObjectFields.get(cls.getName());
            if (flds != null)
            {
                for (int i = flds.length - 1; i >= 0; i--)
                {
                    flds[i].getProperty(obj);
                }
            }

            cls = cls.getSuperclass();
        }
    }

    /**
     * Removes references to this group and its fields from the
     * query parameters
     */
    public void removeFromRequest()
    {
        if (pp != null)
        {
            String[] groups = pp.getStrings(gid);
            if (groups != null)
            {
                pp.remove(gid);
                for (int i = 0; i < groups.length; i++)
                {
                    if (groups[i] != null && !groups[i].equals(oid))
                    {
                        pp.add(gid, groups[i]);
                    }
                }
                for (int i = fieldsArray.length - 1; i >= 0; i--)
                {
                    fieldsArray[i].removeFromRequest();
                }
            }
        }
    }

    /**
     * To be used in the event this group is used within multiple
     * forms within the same template.
     */
    public void resetDeclared()
    {
        isDeclared = false;
    }

    /**
     * A xhtml valid hidden input field that notifies intake of the
     * group's presence.
     *
     * @return a <code>String</code> value
     */
    public String getHtmlFormInput()
    {
        StringBuffer sb = new StringBuffer(64);
        appendHtmlFormInput(sb);
        return sb.toString();
    }

    /**
     * A xhtml valid hidden input field that notifies intake of the
     * group's presence.
     */
    public void appendHtmlFormInput(StringBuffer sb)
    {
        if (!isDeclared)
        {
            isDeclared = true;
            sb.append("<input type=\"hidden\" name=\"")
                    .append(gid)
                    .append("\" value=\"")
                    .append(oid)
                    .append("\"/>\n");
        }
    }

    // ********** PoolableObjectFactory implementation ******************

    public static class GroupFactory
            extends BaseKeyedPoolableObjectFactory
    {
        private AppData appData;

        public GroupFactory(AppData appData)
        {
            this.appData = appData;
        }

        /**
         * Creates an instance that can be returned by the pool.
         * @return an instance that can be returned by the pool.
         * @throws IntakeException indicates that the group could not be retreived
         */
        public Object makeObject(Object key) throws IntakeException
        {
            return new Group(appData.getGroup((String) key));
        }

        /**
         * Uninitialize an instance to be returned to the pool.
         * @param obj the instance to be passivated
         */
        public void passivateObject(Object key, Object obj)
        {
            Group group = (Group) obj;
            group.oid = null;
            group.pp = null;
            for (int i = group.fieldsArray.length - 1; i >= 0; i--)
            {
                group.fieldsArray[i].dispose();
            }
            group.isDeclared = false;
        }
    }
}


