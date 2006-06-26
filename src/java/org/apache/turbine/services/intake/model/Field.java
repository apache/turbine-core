package org.apache.turbine.services.intake.model;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.om.Retrievable;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.intake.IntakeException;
import org.apache.turbine.services.intake.TurbineIntake;
import org.apache.turbine.services.intake.validator.DefaultValidator;
import org.apache.turbine.services.intake.validator.InitableByConstraintMap;
import org.apache.turbine.services.intake.validator.ValidationException;
import org.apache.turbine.services.intake.validator.Validator;
import org.apache.turbine.services.intake.xmlmodel.Rule;
import org.apache.turbine.services.intake.xmlmodel.XmlField;
import org.apache.turbine.services.localization.Localization;
import org.apache.turbine.services.localization.LocalizationService;
import org.apache.turbine.util.SystemError;
import org.apache.turbine.util.parser.ParameterParser;
import org.apache.turbine.util.parser.ValueParser;

/**
 * Base class for Intake generated input processing classes.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:dlr@finemaltcoding.com>Daniel Rall</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:jh@byteaction.de">J&uuml;rgen Hoffmann</a>
 * @version $Id$
 */
public abstract class Field
{
    /** Empty Value */
    private static final String EMPTY = "";

    /** CGI Key for "value if absent" */
    private static final String VALUE_IF_ABSENT_KEY = "_vifa_";

    /** Default Package */
    public static final String defaultFieldPackage = "org.apache.turbine.services.intake.validator.";

    // the following are set from the xml file and are permanent (final)

    /** Name of the field. */
    protected final String name;

    /** Key used to identify the field in the parser */
    protected final String key;

    /** Display name of the field to be used on data entry forms... */
    protected String displayName;

    /** Class name of the object to which the field is mapped */
    protected final String mapToObject;

    /** Used to validate the contents of the field */
    protected Validator validator;

    /** Getter method in the mapped object used to populate the field */
    protected final Method getter;

    /** Setter method in the mapped object used to store the value of field */
    protected final Method setter;

    /** Error message set on the field if required and not set by parser */
    protected String ifRequiredMessage;

    /** Does this field accept multiple values? */
    protected final boolean isMultiValued;

    /** Group to which the field belongs */
    protected final Group group;

    /** Is this field always required?  This is only set through the XML file */
    protected boolean alwaysRequired;

    /**
     * Value of the field if an error occurs while getting
     * the value from the mapped object
     */
    protected Object onError;

    /** Default value of the field */
    protected Object defaultValue;

    /** Value of the field to use if the mapped parameter is empty or non-existant */
    protected Object emptyValue;

    /** Display size of the field */
    private String displaySize;

    /** Max size of the field */
    private String maxSize;

    // these are reset when the Field is returned to the pool

    /** Has the field has been set from the parser? */
    protected boolean setFlag;

    /** Has the field passed the validation test? */
    protected boolean validFlag;

    /** Does the field require a value? */
    protected boolean required;

    /** Has the field has been set from the parser? */
    protected boolean initialized;

    /** Error message, is any, resulting from validation */
    protected String message;

    /** Mapped object used to set the initial field value */
    protected Retrievable retrievable;

    private Locale locale;
    /** String value of the field */
    private String stringValue;
    /** String valuess of the field if isMultiValued=true */
    private String[] stringValues;
    /** Stores the value of the field from the Retrievable object */
    private Object validValue;
    /** Stores the value of the field from the parser */
    private Object testValue;
    /** Used to pass testValue to the setter mathod through reflection */
    private Object[] valArray;
    /** The object containing the field data. */
    protected ValueParser parser;

    /** Logging */
    protected Log log = LogFactory.getLog(this.getClass());
    protected boolean isDebugEnabled = false;

    /**
     * Constructs a field based on data in the xml specification
     * and assigns it to a Group.
     *
     * @param field a <code>XmlField</code> value
     * @param group a <code>Group</code> value
     * @throws IntakeException indicates the validator was not valid or
     * could not be loaded.
     * @throws SystemError only occurs is the Validation object does not
     * extend InitableByConstraintMap
     */
    public Field(XmlField field, Group group) throws IntakeException
    {
        isDebugEnabled = log.isDebugEnabled();

        this.group = group;
        key = field.getKey();
        name = field.getName();
        displayName = field.getDisplayName();
        displaySize = field.getDisplaySize();
        isMultiValued = field.isMultiValued();

        try
        {
            setDefaultValue(field.getDefaultValue());
        }
        catch (RuntimeException e)
        {
            log.error("Could not set default value of " +
                    this.getDisplayName() + " to "
                    + field.getDefaultValue(), e);
        }

        try
        {
            setEmptyValue(field.getEmptyValue());
        }
        catch (RuntimeException e)
        {
            log.error("Could not set empty value of " +
                    this.getDisplayName() + " to "
                    + field.getEmptyValue(), e);
        }

        String validatorClassName = field.getValidator();
        if (validatorClassName == null)
        {
            validatorClassName = getDefaultValidator();
        }
        else if (validatorClassName != null
                && validatorClassName.indexOf('.') == -1)
        {
            validatorClassName = defaultFieldPackage + validatorClassName;
        }

        if (validatorClassName != null)
        {
            try
            {
                validator = (Validator)
                        Class.forName(validatorClassName).newInstance();
            }
            catch (InstantiationException e)
            {
                throw new IntakeException(
                        "Could not create new instance of Validator("
                        + validatorClassName + ")", e);
            }
            catch (IllegalAccessException e)
            {
                throw new IntakeException(
                        "Could not create new instance of Validator("
                        + validatorClassName + ")", e);
            }
            catch (ClassNotFoundException e)
            {
                throw new IntakeException(
                        "Could not load Validator class("
                        + validatorClassName + ")", e);
            }
            // this should always be true for now
            // (until bean property initialization is implemented)
            if (validator instanceof InitableByConstraintMap)
            {
                ((InitableByConstraintMap) validator).init(field.getRuleMap());
            }
            else
            {
                throw new SystemError(
                        "All Validation objects must be subclasses of "
                        + "InitableByConstraintMap");
            }
        }

        // field may have been declared as always required in the xml spec
        Rule reqRule = (Rule) field.getRuleMap().get("required");
        if (reqRule != null)
        {
            alwaysRequired = Boolean.valueOf(reqRule.getValue()).booleanValue();
            ifRequiredMessage = reqRule.getMessage();
        }

        Rule maxLengthRule = (Rule) field.getRuleMap().get("maxLength");
        if (maxLengthRule != null)
        {
            maxSize = maxLengthRule.getValue();
        }

        // map the getter and setter methods
        mapToObject = field.getMapToObject();
        String propName = field.getMapToProperty();
        Method tmpGetter = null;
        Method tmpSetter = null;
        if (StringUtils.isNotEmpty(mapToObject)
                && StringUtils.isNotEmpty(propName))
        {
            try
            {
                tmpGetter = TurbineIntake.getFieldGetter(mapToObject, propName);
            }
            catch (Exception e)
            {
                log.error("IntakeService could not map the getter for field "
                        + this.getDisplayName() + " in group "
                        + this.group.getIntakeGroupName()
                        + " to the property " + propName + " in object "
                        + mapToObject, e);
            }
            try
            {
                tmpSetter = TurbineIntake.getFieldSetter(mapToObject, propName);
            }
            catch (Exception e)
            {
                log.error("IntakeService could not map the setter for field "
                        + this.getDisplayName() + " in group "
                        + this.group.getIntakeGroupName()
                        + " to the property " + propName + " in object "
                        + mapToObject, e);
            }
        }
        getter = tmpGetter;
        setter = tmpSetter;

        valArray = new Object[1];
    }

    /**
     * Method called when this field (the group it belongs to) is
     * pulled from the pool.  The request data is searched to determine
     * if a value has been supplied for this field.  If so, the value
     * is validated.
     *
     * @param pp a <code>ValueParser</code> value
     * @return a <code>Field</code> value
     * @throws IntakeException this exception is only thrown by subclasses
     * overriding this implementation.
     */
    public Field init(ValueParser pp)
            throws IntakeException
    {
        this.parser = pp;
        validFlag = true;

        // If the parser is for a HTTP request, use the request it's
        // associated with to grok the locale.
        if (TurbineServices.getInstance()
                .isRegistered(LocalizationService.SERVICE_NAME))
        {
            if (pp instanceof ParameterParser)
            {
                this.locale = Localization.getLocale
                        (((ParameterParser) pp).getRequest());
            }
            else
            {
                this.locale = Localization.getLocale((String) null);
            }
        }

        if (pp.containsKey(getKey()))
        {
            if (isDebugEnabled)
            {
                log.debug(name + ": Found our Key in the request, setting Value");
            }
            if (pp.getString(getKey()) != null)
            {
                setFlag = true;
            }
            validate();
        }
        else if (pp.containsKey(getValueIfAbsent()) &&
                pp.getString(getValueIfAbsent()) != null)
        {
            pp.add(getKey(), pp.getString(getValueIfAbsent()));
            setFlag = true;
            validate();
        }

        initialized = true;
        return this;
    }

    /**
     * Method called when this field or the group it belongs to is
     * pulled from the pool.  The retrievable object can provide
     * a default value for the field, or using setProperty the field's
     * value can be transferred to the retrievable.
     *
     * @param obj a <code>Retrievable</code> value
     * @return a <code>Field</code> value
     */
    public Field init(Retrievable obj)
    {
        if (!initialized)
        {
            validFlag = true;
        }
        retrievable = obj;
        return this;
    }

    /**
     * Returns the <code>Locale</code> used when localizing data for
     * this field, or <code>null</code> if unknown.
     *
     * @return Where to localize for.
     */
    protected Locale getLocale()
    {
        return locale;
    }

    /**
     * Produces the fully qualified class name of the default validator.
     *
     * @return class name of the default validator
     */
    protected String getDefaultValidator()
    {
        return DefaultValidator.class.getName();
    }

    /**
     * Gets the Validator object for this field.
     * @return a <code>Validator</code> object
     */
    public Validator getValidator()
    {
        return validator;
    }

    /**
     * Flag to determine whether the field has been declared as required.
     *
     * @return value of required.
     */
    public boolean isRequired()
    {
        return alwaysRequired || required;
    }

    /**
     * Set whether this field is required to have a value.  If the field
     * is already required due to a setting in the XML file, this method
     * can not set it to false.
     *
     * @param v  Value to assign to required.
     */
    public void setRequired(boolean v)
    {
        setRequired(v, ifRequiredMessage);
    }

    /**
     * Set the value of required.
     *
     * @param v a <code>boolean</code> value
     * @param message override the value from intake.xml
     */
    public void setRequired(boolean v, String message)
    {
        this.required = v;
        if (v && (!setFlag || null == getTestValue()))
        {
            validFlag = false;
            this.message = message;
        }
    }

    /**
     * Removes references to this group and its fields from the
     * query parameters
     */
    public void removeFromRequest()
    {
        parser.remove(getKey());
        parser.remove(getKey()+ VALUE_IF_ABSENT_KEY);
    }

    /**
     * Disposes the object after use. The method is called
     * when the Group is returned to its pool.
     * if overridden, super.dispose() should be called.
     */
    public void dispose()
    {
        parser = null;
        initialized = false;
        setFlag = false;
        validFlag = false;
        required = false;
        message = null;
        retrievable = null;

        locale = null;
        stringValue = null;
        stringValues = null;
        validValue = null;
        testValue = null;
        valArray[0] = null;
    }

    /**
     * Get the key used to identify the field.
     *
     * @return the query data key.
     */
    public String getKey()
    {
        return (group == null) ? key : group.getObjectKey() + key;
    }

    /**
     * Use in a hidden field assign a default value in the event the
     * field is absent from the query parameters.  Used to track checkboxes,
     * since they only show up if checked.
     */
    public String getValueIfAbsent()
    {
        return getKey() + VALUE_IF_ABSENT_KEY;
    }

    /**
     * Flag set to true, if the test value met the constraints.
     * Is also true, in the case the test value was not set,
     * unless this field has been marked as required.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isValid()
    {
        return validFlag;
    }

    /**
     * Flag set to true, if the test value has been set to
     * anything other than an empty value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isSet()
    {
        return setFlag;
    }

    /**
     * Get the display name of the field. Useful for building
     * data entry forms. Returns name of field if no display
     * name has been assigned to the field by xml input file.
     *
     * @return a <code>String</code> value
     */
    public String getDisplayName()
    {
        return (displayName == null) ? name : displayName;
    }

    /**
     * Set the display name of the field. Display names are
     * used in building data entry forms and serve as a
     * user friendly description of the data contained in
     * the field.
     */
    public void setDisplayName(String newDisplayName)
    {
        displayName = newDisplayName;
    }

    /**
     * Get any error message resulting from invalid input.
     *
     * @return a <code>String</code> value
     */
    public String getMessage()
    {
        return (message == null) ? EMPTY : message;
    }

    /**
     * Sets an error message.  The field is also marked as invalid.
     */
    public void setMessage(String message)
    {
        this.message = message;
        validFlag = false;
    }

    /**
     * @deprecated Call validate() instead (with no parameters).
     */
    protected boolean validate(ValueParser pp)
    {
        return validate();
    }

    /**
     * Compares request data with constraints and sets the valid flag.
     */
    protected boolean validate()
    {
        log.debug(name + ": validate()");

        if (isMultiValued)
        {
            stringValues = parser.getStrings(getKey());

            if (isDebugEnabled)
            {
                log.debug(name + ": Multi-Valued");
                for (int i = 0; i < stringValues.length; i++)
                {
                    log.debug(name + ": " + i + ". Wert: " + stringValues[i]);
                }
            }


            if (validator != null)
            {
                // set the test value as a String[] which might be replaced by
                // the correct type if the input is valid.
                setTestValue(parser.getStrings(getKey()));
                for (int i = 0; i < stringValues.length; i++)
                {
                    try
                    {
                        validator.assertValidity(stringValues[i]);
                    }
                    catch (ValidationException ve)
                    {
                        setMessage(ve.getMessage());
                    }
                }
            }

            if (validFlag)
            {
                doSetValue();
            }
        }
        else
        {
            stringValue = parser.getString(getKey());

            if (isDebugEnabled)
            {
                log.debug(name + ": Single Valued, Value is " + stringValue);
            }

            if (validator != null)
            {
                // set the test value as a String which might be replaced by
                // the correct type if the input is valid.
                setTestValue(parser.getString(getKey()));

                try
                {
                    validator.assertValidity(stringValue);
                    log.debug(name + ": Value is ok");
                    doSetValue();
                }
                catch (ValidationException ve)
                {
                    log.debug(name + ": Value failed validation!");
                    setMessage(ve.getMessage());
                }
            }
            else
            {
                doSetValue();
            }
        }

        return validFlag;
    }

    /**
     * Set the default Value. This value is used if
     * Intake should map this field to a new object.
     *
     * @param prop The value to use if the field is mapped to a new object.
     */
    public abstract void setDefaultValue(String prop);

    /**
     * Set the empty Value. This value is used if Intake
     * maps a field to a parameter returned by the user and
     * the corresponding field is either empty (empty string)
     * or non-existant.
     *
     * @param prop The value to use if the field is empty.
     */
    public abstract void setEmptyValue(String prop);

    /**
     * @deprecated Use doSetValue() instead (with no parameters).
     */
    protected void doSetValue(ValueParser pp)
    {
        doSetValue();
    }

    /**
     * Sets the value of the field from data in the parser.
     */
    protected abstract void doSetValue();

    /**
     * Set the value used as a default, in the event the field
     * has not been set yet.
     *
     * @param obj an <code>Object</code> value
     */
    void setInitialValue(Object obj)
    {
        validValue = obj;
    }

    /**
     * Get the value used as a default.  If the initial value has
     * not been set and a <code>Retrievable</code> object has
     * been associated with this field, the objects property will
     * be used as the initial value.
     *
     * @return an <code>Object</code> value
     * @exception IntakeException indicates the value could not be
     * returned from the mapped object
     */
    public Object getInitialValue() throws IntakeException
    {
        if (validValue == null)
        {
            if (retrievable != null)
            {
                getProperty(retrievable);
            }
            else
            {
                getDefault();
            }
        }
        return validValue;
    }

    /**
     * Set the value input by a user that will be validated.
     *
     * @param obj an <code>Object</code> value
     */
    void setTestValue(Object obj)
    {
        testValue = obj;
    }

    /**
     * Get the value input by a user that will be validated.
     *
     * @return an <code>Object</code> value
     */
    public Object getTestValue()
    {
        return testValue;
    }

    /**
     * Get the value of the field.  if a test value has been set, it
     * will be returned as is, unless it is so badly formed that the
     * validation could not parse it.  In most cases the test value
     * is returned even though invalid, so that it can be returned to
     * the user to make modifications.  If the test value is not set
     * the initial value is returned.
     *
     * @return an <code>Object</code> value
     */
    public Object getValue()
    {
        Object val = null;
        try
        {
            val = getInitialValue();
        }
        catch (IntakeException e)
        {
            log.error("Could not get intial value of " + this.getDisplayName() +
                    " in group " + this.group.getIntakeGroupName(), e);
        }

        if (getTestValue() != null)
        {
            val = getTestValue();
        }

        if (val == null)
        {
            val = onError;
        }
        return val;
    }

    /**
     * Calls toString() on the object returned by getValue(),
     * unless null; and then it returns "", the empty String.
     *
     * @return a <code>String</code> value
     */
    public String toString()
    {
        String res = EMPTY;

        if (stringValue != null)
        {
            res = stringValue;
        }
        else if (getValue() != null)
        {
            res = getValue().toString();
        }
        return res;
    }

    /**
     * Calls toString() on the object returned by getValue(),
     * unless null; and then it returns "", the empty String.
     * Escapes &quot; characters to be able to display these
     * in HTML form fields.
     *
     * @return a <code>String</code> value
     */
    public String getHTMLString()
    {
        String res = toString();
        return StringUtils.replace(res, "\"", "&quot;");
    }

    /**
     * Loads the valid value from a bean
     *
     * @throws IntakeException indicates a problem during the execution of the
     * object's getter method
     */
    public void getProperty(Object obj)
            throws IntakeException
    {
        try
        {
            validValue = getter.invoke(obj, null);
        }
        catch (IllegalAccessException e)
        {
            throwSetGetException("getter", obj, this.getDisplayName(),
                    this.group.getIntakeGroupName(), e);
        }
        catch (IllegalArgumentException e)
        {
            throwSetGetException("getter", obj, this.getDisplayName(),
                    this.group.getIntakeGroupName(), e);
        }
        catch (InvocationTargetException e)
        {
            throwSetGetException("getter", obj, this.getDisplayName(),
                    this.group.getIntakeGroupName(), e);
        }
    }

    /**
     * Loads the default value from the object
     */

    public void getDefault()
    {
        validValue = getDefaultValue();
    }

    /**
     * Calls a setter method on obj, if this field has been set.
     *
     * @throws IntakeException indicates a problem during the execution of the
     * object's setter method
     */
    public void setProperty(Object obj) throws IntakeException
    {
        if (isDebugEnabled)
        {
            log.debug(name + ".setProperty(" + obj.getClass().getName() + ")");
        }

        if (!isValid())
        {
            throw new IntakeException(
                    "Attempted to assign an invalid input.");
        }
        if (isSet())
        {
            valArray[0] = getTestValue();
            if (isDebugEnabled)
            {
                log.debug(name + ": Property is set, value is " + valArray[0]);
            }
        }
        else
        {
            valArray[0] = getSafeEmptyValue();
            if (isDebugEnabled)
            {
                log.debug(name + ": Property is not set, using emptyValue " + valArray[0]);
            }
        }

        try
        {
            /*
             * In the case we map a Group to an Object using mapToObject, and we
             * want to add an additional Field which should not be mapped, and
             * we leave the mapToProperty empty, we will get a NPE here. So we
             * have to double check, if we really have a setter set.
             */
            if(setter != null)
            {
                setter.invoke(obj, valArray);
            }
            else if (isDebugEnabled)
            {
                log.debug(name + ": has a null setter for the mapToProperty"
                        + " Attribute, although all Fields should be mapped"
                        + " to " + mapToObject + ". If this is unwanted, You"
                        + " should doublecheck the mapToProperty Attribute, and"
                        + " consult the logs. The Turbine Intake Serice will"
                        + " have logged a detailed Message with the error.");
            }                
        }
        catch (IllegalAccessException e)
        {
            throwSetGetException("setter", obj, this.getDisplayName(),
                    this.group.getIntakeGroupName(), e);
        }
        catch (IllegalArgumentException e)
        {
            throwSetGetException("setter", obj, this.getDisplayName(),
                    this.group.getIntakeGroupName(), e);
        }
        catch (InvocationTargetException e)
        {
            throwSetGetException("setter", obj, this.getDisplayName(),
                    this.group.getIntakeGroupName(), e);
        }
    }

    /**
     * Used to throw an IntakeException when an error occurs execuing the
     * get/set method of the mapped persistent object.
     *
     * @param type Type of method. (setter/getter)
     * @param fieldName Name of the field
     * @param groupName Name of the group
     * @param e Exception that was thrown
     * @throws IntakeException New exception with formatted message
     */
    private void throwSetGetException(String type, Object obj,
                                      String fieldName, String groupName,
                                      Exception e)
            throws IntakeException
    {
        throw new IntakeException("Could not execute " + type
                + " method for " + fieldName + " in group " + groupName
                + " on " + obj.getClass().getName(), e);

    }

    /**
     * Get the default Value
     *
     * @return the default value
     */
    public Object getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * Get the Value to use if the field is empty
     *
     * @return the value to use if the field is empty.
     */
    public Object getEmptyValue()
    {
        return emptyValue;
    }

    /**
     * Provides access to emptyValue such that the value returned will be
     * acceptable as an argument parameter to Method.invoke.  Subclasses
     * that deal with primitive types should ensure that they return an
     * appropriate value wrapped in the object wrapper class for the
     * primitive type.
     *
     * @return the value to use when the field is empty or an Object that
     * wraps the empty value for primitive types.
     */
    protected Object getSafeEmptyValue()
    {
        return getEmptyValue();
    }

    /**
     * Gets the name of the field.
     *
     * @return name of the field as specified in the XML file.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the diplay size of the field.  This is useful when
     * building the HTML input tag.  If no displaySize was set,
     * an empty string is returned.
     */
    public String getDisplaySize()
    {
        return (StringUtils.isEmpty(displaySize) ? "" : displaySize);
    }

    /**
     * Gets the maximum size of the field.  This is useful when
     * building the HTML input tag.  The maxSize is set with the maxLength
     * rule.  If this rul was not set, an enmpty string is returned.
     */
    public String getMaxSize()
    {
        return (StringUtils.isEmpty(maxSize) ? "" : maxSize);
    }

    /**
     * Gets the String representation of the Value. This is basically a wrapper
     * method for the toString method which doesn't seem to show anything on
     * screen if accessed from Template. Name is also more in line with getValue
     * method which returns the actual Object.
     * This is useful for displaying correctly formatted data such as dates,
     * such as 18/11/1968 instead of the toString dump of a Date Object.
     *
     * @return the String Value
     */
    public String getStringValue()
    {
        return this.toString();
    }

}
