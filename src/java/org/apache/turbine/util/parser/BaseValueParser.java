package org.apache.turbine.util.parser;

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

import java.beans.IndexedPropertyDescriptor;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.io.UnsupportedEncodingException;

import java.lang.reflect.Method;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.torque.om.NumberKey;
import org.apache.torque.om.StringKey;

import org.apache.turbine.util.DateSelector;
import org.apache.turbine.util.TimeSelector;
import org.apache.turbine.util.ValueParser;
import org.apache.turbine.util.pool.Recyclable;
import org.apache.turbine.util.pool.RecyclableSupport;

/**
 * BaseValueParser is a base class for classes that need to parse
 * name/value Parameters, for example GET/POST data or Cookies
 * (DefaultParameterParser and DefaultCookieParser)
 *
 * <p>It can also be used standalone, for an example see DataStreamParser.
 *
 * <p>NOTE: The name= portion of a name=value pair may be converted
 * to lowercase or uppercase when the object is initialized and when
 * new data is added.  This behaviour is determined by the url.case.folding
 * property in TurbineResources.properties.  Adding a name/value pair may
 * overwrite existing name=value pairs if the names match:
 *
 * <pre>
 * ValueParser vp = new BaseValueParser();
 * vp.add("ERROR",1);
 * vp.add("eRrOr",2);
 * int result = vp.getInt("ERROR");
 * </pre>
 *
 * In the above example, result is 2.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class BaseValueParser
        extends RecyclableSupport
        implements ValueParser, Recyclable
{
    /** Logging */
    private static Log log = LogFactory.getLog(BaseValueParser.class);

    /**
     * Random access storage for parameter data.  The keys must always be
     * Strings.  The values will be arrays of Strings.
     */
    private Map parameters = new HashMap();

    /** The character encoding to use when converting to byte arrays */
    private String characterEncoding = "US-ASCII";

    /**
     * A static version of the convert method, which
     * trims the string data and applies the conversion specified in
     * the property given by URL_CASE_FOLDING.  It returns a new
     * string so that it does not destroy the value data.
     *
     * @param value A String to be processed.
     * @return A new String converted to lowercase and trimmed.
     * @deprecated Use ParserUtils.convertAndTrim(value).
     */
    public static String convertAndTrim(String value)
    {
        return ParserUtils.convertAndTrim(value);
    }

    /**
     * Default constructor
     */
    public BaseValueParser()
    {
        super();
    }

    /**
     * Constructor that takes a character encoding
     */
    public BaseValueParser(String characterEncoding)
    {
        super();
        recycle(characterEncoding);
    }

    /**
     * Recycles the parser.
     */
    public void recycle()
    {
        recycle("US-ASCII");
    }

    /**
     * Recycles the parser with a character encoding.
     *
     * @param characterEncoding the character encoding.
     */
    public void recycle(String characterEncoding)
    {
        setCharacterEncoding(characterEncoding);
        if (!isDisposed())
        {
            super.recycle();
        }
    }

    /**
     * Disposes the parser.
     */
    public void dispose()
    {
        clear();
        super.dispose();
    }

    /**
     * Clear all name/value pairs out of this object.
     */
    public void clear()
    {
        parameters.clear();
    }

    /**
     * Set the character encoding that will be used by this ValueParser.
     */
    public void setCharacterEncoding(String s)
    {
        characterEncoding = s;
    }

    /**
     * Get the character encoding that will be used by this ValueParser.
     */
    public String getCharacterEncoding()
    {
        return characterEncoding;
    }

    /**
     * Add a name/value pair into this object.
     *
     * @param name A String with the name.
     * @param value A double with the value.
     */
    public void add(String name, double value)
    {
        add(name, Double.toString(value));
    }

    /**
     * Add a name/value pair into this object.
     *
     * @param name A String with the name.
     * @param value An int with the value.
     */
    public void add(String name, int value)
    {
        add(name, Integer.toString(value));
    }

    /**
     * Add a name/value pair into this object.
     *
     * @param name A String with the name.
     * @param value An Integer with the value.
     */
    public void add(String name, Integer value)
    {
        add(name, value.toString());
    }

    /**
     * Add a name/value pair into this object.
     *
     * @param name A String with the name.
     * @param value A long with the value.
     */
    public void add(String name, long value)
    {
        add(name, Long.toString(value));
    }

    /**
     * Add a name/value pair into this object.
     *
     * @param name A String with the name.
     * @param value A long with the value.
     */
    public void add(String name, String value)
    {
        append(name, value);
    }

    /**
     * Add an array of Strings for a key. This
     * is simply adding all the elements in the
     * array one by one.
     *
     * @param name A String with the name.
     * @param value A String Array.
     */
    public void add(String name, String [] value)
    {
        for (int i = 0 ; i < value.length; i++)
        {
            add(name, value[i]);
        }
    }

    /**
     * Add a String parameters.  If there are any Strings already
     * associated with the name, append to the array.  This is used
     * for handling parameters from mulitipart POST requests.
     *
     * @param name A String with the name.
     * @param value A String with the value.
     */
    public void append(String name, String value)
    {
        String[] items = this.getStrings(name);
        if (items == null)
        {
            items = new String[1];
            items[0] = value;
            parameters.put(convert(name), items);
        }
        else
        {
            String[] newItems = new String[items.length + 1];
            System.arraycopy(items, 0, newItems, 0, items.length);
            newItems[items.length] = value;
            parameters.put(convert(name), newItems);
        }
    }

    /**
     * Removes the named parameter from the contained hashtable. Wraps to the
     * contained <code>Map.remove()</code>.
     *
     * @return The value that was mapped to the key (a <code>String[]</code>)
     *         or <code>null</code> if the key was not mapped.
     */
    public Object remove(String name)
    {
        return parameters.remove(convert(name));
    }

    /**
     * Trims the string data and applies the conversion specified in
     * the property given by URL_CASE_FOLDING.  It returns a new
     * string so that it does not destroy the value data.
     *
     * @param value A String to be processed.
     * @return A new String converted to lowercase and trimmed.
     */
    public String convert(String value)
    {
        return ParserUtils.convertAndTrim(value);
    }

    /**
     * Determine whether a given key has been inserted.  All keys are
     * stored in lowercase strings, so override method to account for
     * this.
     *
     * @param key An Object with the key to search for.
     * @return True if the object is found.
     */
    public boolean containsKey(Object key)
    {
        return parameters.containsKey(convert((String)key));
    }

    /**
     * Check for existence of key_day, key_month and key_year
     * parameters (as returned by DateSelector generated HTML).
     *
     * @param key A String with the selector name.
     * @return True if keys are found.
     */
    public boolean containsDateSelectorKeys(String key)
    {
        return (containsKey(key + DateSelector.DAY_SUFFIX) &&
                containsKey(key + DateSelector.MONTH_SUFFIX) &&
                containsKey(key + DateSelector.YEAR_SUFFIX));
    }

    /**
     * Check for existence of key_hour, key_minute and key_second
     * parameters (as returned by TimeSelector generated HTML).
     *
     * @param key A String with the selector name.
     * @return True if keys are found.
     */
    public boolean containsTimeSelectorKeys(String key)
    {
        return (containsKey(key + TimeSelector.HOUR_SUFFIX) &&
                containsKey(key + TimeSelector.MINUTE_SUFFIX) &&
                containsKey(key + TimeSelector.SECOND_SUFFIX));
    }

    /**
     * Get an enumerator for the parameter keys.
     *
     * @return An <code>enumerator</code> of the keys.
     * @deprecated use {@link #keySet} instead.
     */
    public Enumeration keys()
    {
        return Collections.enumeration(parameters.keySet());
    }

    /**
     * Gets the set of keys
     *
     * @return A <code>Set</code> of the keys.
     */
    public Set keySet()
    {
        return parameters.keySet();
    }

    /**
     * Returns all the available parameter names.
     *
     * @return A object array with the keys.
     */
    public Object[] getKeys()
    {
        return parameters.keySet().toArray();
    }

    /**
     * Return a boolean for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A boolean.
     */
    public boolean getBoolean(String name, boolean defaultValue)
    {
        Boolean result = getBooleanObject(name);
        return (result == null ? defaultValue : result.booleanValue());
    }

    /**
     * Return a boolean for the given name.  If the name does not
     * exist, return false.
     *
     * @param name A String with the name.
     * @return A boolean.
     */
    public boolean getBoolean(String name)
    {
        return getBoolean(name, false);
    }

    /**
     * Returns a Boolean object for the given name.  If the parameter
     * does not exist or can not be parsed as a boolean, null is returned.
     * <p>
     * Valid values for true: true, on, 1, yes<br>
     * Valid values for false: false, off, 0, no<br>
     * <p>
     * The string is compared without reguard to case.
     *
     * @param name A String with the name.
     * @return A Boolean.
     */
    public Boolean getBooleanObject(String name)
    {
        Boolean result = null;
        String value = getString(name);
        if (StringUtils.isNotEmpty(value))
        {
            if (value.equals("1") ||
                    value.equalsIgnoreCase("true") ||
                    value.equalsIgnoreCase("yes") ||
                    value.equalsIgnoreCase("on"))
            {
                result = Boolean.TRUE;
            }
            else if (value.equals("0") ||
                    value.equalsIgnoreCase("false") ||
                    value.equalsIgnoreCase("no") ||
                    value.equalsIgnoreCase("off"))
            {
                result = Boolean.FALSE;
            }
            else
            {
                logConvertionFailure(name, value, "Boolean");
            }
        }
        return result;
    }

    /**
     * Returns a Boolean object for the given name.  If the parameter
     * does not exist or can not be parsed as a boolean, null is returned.
     * <p>
     * Valid values for true: true, on, 1, yes<br>
     * Valid values for false: false, off, 0, no<br>
     * <p>
     * The string is compared without reguard to case.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A Boolean.
     */
    public Boolean getBooleanObject(String name, Boolean defaultValue)
    {
        Boolean result = getBooleanObject(name);
        return (result==null ? defaultValue : result);
    }

    /**
     * Return a Boolean for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A Boolean.
     * @deprecated use {@link #getBooleanObject} instead
     */
    public Boolean getBool(String name, boolean defaultValue)
    {
        return getBooleanObject(name, new Boolean(defaultValue));
    }

    /**
     * Return a Boolean for the given name.  If the name does not
     * exist, return false.
     *
     * @param name A String with the name.
     * @return A Boolean.
     * @deprecated use {@link #getBooleanObject(String)} instead
     */
    public Boolean getBool(String name)
    {
        return getBooleanObject(name, Boolean.FALSE);
    }

    /**
     * Return a double for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A double.
     */
    public double getDouble(String name, double defaultValue)
    {
        double result = defaultValue;
        String value = getString(name);
        if (StringUtils.isNotEmpty(value))
        {
            try
            {
                result = Double.valueOf(value).doubleValue();
            }
            catch (NumberFormatException e)
            {
                logConvertionFailure(name, value, "Double");
            }
        }
        return result;
    }

    /**
     * Return a double for the given name.  If the name does not
     * exist, return 0.0.
     *
     * @param name A String with the name.
     * @return A double.
     */
    public double getDouble(String name)
    {
        return getDouble(name, 0.0);
    }

    /**
     * Return an array of doubles for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return A double[].
     */
    public double[] getDoubles(String name)
    {
        double[] result = null;
        String value[] = getStrings(name);
        if (value != null)
        {
            result = new double[value.length];
            for (int i = 0; i < value.length; i++)
            {
                if (StringUtils.isNotEmpty(value[i]))
                {
                    try
                    {
                        result[i] = Double.parseDouble(value[i]);
                    }
                    catch (NumberFormatException e)
                    {
                        logConvertionFailure(name, value[i], "Double");
                    }
                }
            }
        }
        return result;
    }

    /**
     * Return a Double for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A double.
     */
    public Double getDoubleObject(String name, Double defaultValue)
    {
        Double result = getDoubleObject(name);
        return (result==null ? defaultValue : result);
    }

    /**
     * Return a Double for the given name.  If the name does not
     * exist, return null.
     *
     * @param name A String with the name.
     * @return A double.
     */
    public Double getDoubleObject(String name)
    {
        Double result = null;
        String value = getString(name);
        if (StringUtils.isNotEmpty(value))
        {
            try
            {
                result = new Double(value);
            }
            catch(NumberFormatException e)
            {
                logConvertionFailure(name, value, "Double");
            }
        }
        return result;
    }

    /**
     * Return an array of doubles for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return A double[].
     */
    public Double[] getDoubleObjects(String name)
    {
        Double[] result = null;
        String value[] = getStrings(convert(name));
        if (value != null)
        {
            result = new Double[value.length];
            for (int i = 0; i < value.length; i++)
            {
                if (StringUtils.isNotEmpty(value[i]))
                {
                    try
                    {
                        result[i] = Double.valueOf(value[i]);
                    }
                    catch (NumberFormatException e)
                    {
                        logConvertionFailure(name, value[i], "Double");
                    }
                }
            }
        }
        return result;
    }

    /**
     * Return a float for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A float.
     */
    public float getFloat(String name, float defaultValue)
    {
        float result = defaultValue;
        String value = getString(name);
        if (StringUtils.isNotEmpty(value))
        {
            try
            {
                result = Float.valueOf(value).floatValue();
            }
            catch (NumberFormatException e)
            {
                logConvertionFailure(name, value, "Float");
            }
        }
        return result;
    }

    /**
     * Return a float for the given name.  If the name does not
     * exist, return 0.0.
     *
     * @param name A String with the name.
     * @return A float.
     */
    public float getFloat(String name)
    {
        return getFloat(name, 0.0f);
    }

    /**
     * Return an array of floats for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return A float[].
     */
    public float[] getFloats(String name)
    {
        float[] result = null;
        String value[] = getStrings(name);
        if (value != null)
        {
            result = new float[value.length];
            for (int i = 0; i < value.length; i++)
            {
                if (StringUtils.isNotEmpty(value[i]))
                {
                    try
                    {
                        result[i] = Float.parseFloat(value[i]);
                    }
                    catch (NumberFormatException e)
                    {
                        logConvertionFailure(name, value[i], "Float");
                    }
                }
            }
        }
        return result;
    }

    /**
     * Return a Float for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A Float.
     */
    public Float getFloatObject(String name, Float defaultValue)
    {
        Float result = getFloatObject(name);
        return (result==null ? defaultValue : result);
    }

    /**
     * Return a float for the given name.  If the name does not
     * exist, return null.
     *
     * @param name A String with the name.
     * @return A Float.
     */
    public Float getFloatObject(String name)
    {
        Float result = null;
        String value = getString(name);
        if (StringUtils.isNotEmpty(value))
        {
            try
            {
                result = new Float(value);
            }
            catch(NumberFormatException e)
            {
                logConvertionFailure(name, value, "Float");
            }
        }
        return result;
    }

    /**
     * Return an array of floats for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return A float[].
     */
    public Float[] getFloatObjects(String name)
    {
        Float[] result = null;
        String value[] = getStrings(convert(name));
        if (value != null)
        {
            result = new Float[value.length];
            for (int i = 0; i < value.length; i++)
            {
                if (StringUtils.isNotEmpty(value[i]))
                {
                    try
                    {
                        result[i] = Float.valueOf(value[i]);
                    }
                    catch (NumberFormatException e)
                    {
                        logConvertionFailure(name, value[i], "Float");
                    }
                }
            }
        }
        return result;
    }

    /**
     * Return a BigDecimal for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A BigDecimal.
     */
    public BigDecimal getBigDecimal(String name, BigDecimal defaultValue)
    {
        BigDecimal result = defaultValue;
        String value = getString(name);
        if (StringUtils.isNotEmpty(value))
        {
            try
            {
                result = new BigDecimal(value);
            }
            catch (NumberFormatException e)
            {
                logConvertionFailure(name, value, "BigDecimal");
            }
        }
        return result;
    }

    /**
     * Return a BigDecimal for the given name.  If the name does not
     * exist, return 0.0.
     *
     * @param name A String with the name.
     * @return A BigDecimal.
     */
    public BigDecimal getBigDecimal(String name)
    {
        return getBigDecimal(name, new BigDecimal(0.0));
    }

    /**
     * Return an array of BigDecimals for the given name.  If the name
     * does not exist, return null.
     *
     * @param name A String with the name.
     * @return A BigDecimal[].
     */
    public BigDecimal[] getBigDecimals(String name)
    {
        BigDecimal[] result = null;
        String value[] = getStrings(name);
        if (value != null)
        {
            result = new BigDecimal[value.length];
            for (int i = 0; i < value.length; i++)
            {
                if(StringUtils.isNotEmpty(value[i]))
                {
                    try
                    {
                        result[i] = new BigDecimal(value[i]);
                    }
                    catch (NumberFormatException e)
                    {
                        logConvertionFailure(name, value[i], "BigDecimal");
                    }
                }
            }
        }
        return result;
    }

    /**
     * Return an int for the given name.  If the name does not exist,
     * return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return An int.
     */
    public int getInt(String name, int defaultValue)
    {
        int result = defaultValue;
        String value = getString(name);
        if (StringUtils.isNotEmpty(value))
        {
            try
            {
                result = Integer.valueOf(value).intValue();
            }
            catch (NumberFormatException e)
            {
                logConvertionFailure(name, value, "Integer");
            }
        }
        return result;
    }

    /**
     * Return an int for the given name.  If the name does not exist,
     * return 0.
     *
     * @param name A String with the name.
     * @return An int.
     */
    public int getInt(String name)
    {
        return getInt(name, 0);
    }

    /**
     * Return an Integer for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return An Integer.
     */
    public Integer getInteger(String name, int defaultValue)
    {
        return getIntObject(name, new Integer(defaultValue));
    }

    /**
     * Return an Integer for the given name.  If the name does not
     * exist, return defaultValue.  You cannot pass in a null here for
     * the default value.
     *
     * @param name A String with the name.
     * @param def The default value.
     * @return An Integer.
     * @deprecated use {@link #getIntObject} instead
     */
    public Integer getInteger(String name, Integer def)
    {
        return getIntObject(name, def);
    }

    /**
     * Return an Integer for the given name.  If the name does not
     * exist, return 0.
     *
     * @param name A String with the name.
     * @return An Integer.
     * @deprecated use {@link #getIntObject} instead
     */
    public Integer getInteger(String name)
    {
        return getIntObject(name, new Integer(0));
    }

    /**
     * Return an array of ints for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return An int[].
     */
    public int[] getInts(String name)
    {
        int[] result = null;
        String value[] = getStrings(name);
        if (value != null)
        {
            result = new int[value.length];
            for (int i = 0; i < value.length; i++)
            {
                if (StringUtils.isNotEmpty(value[i]))
                {
                    try
                    {
                        result[i] = Integer.parseInt(value[i]);
                    }
                    catch (NumberFormatException e)
                    {
                        logConvertionFailure(name, value[i], "Integer");
                    }
                }
            }
        }
        return result;
    }

    /**
     * Return an Integer for the given name.  If the name does not exist,
     * return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return An Integer.
     */
    public Integer getIntObject(String name, Integer defaultValue)
    {
        Integer result = getIntObject(name);
        return (result==null ? defaultValue : result);
    }

    /**
     * Return an Integer for the given name.  If the name does not exist,
     * return null.
     *
     * @param name A String with the name.
     * @return An Integer.
     */
    public Integer getIntObject(String name)
    {
        Integer result = null;
        String value = getString(name);
        if (StringUtils.isNotEmpty(value))
        {
            try
            {
                result = new Integer(value);
            }
            catch(NumberFormatException e)
            {
                logConvertionFailure(name, value, "Integer");
            }
        }
        return result;
    }

    /**
     * Return an array of Integers for the given name.  If the name
     * does not exist, return null.
     *
     * @param name A String with the name.
     * @return An Integer[].
     */
    public Integer[] getIntObjects(String name)
    {
        Integer[] result = null;
        String value[] = getStrings(convert(name));
        if (value != null)
        {
            result = new Integer[value.length];
            for (int i = 0; i < value.length; i++)
            {
                if (StringUtils.isNotEmpty(value[i]))
                {
                    try
                    {
                        result[i] = Integer.valueOf(value[i]);
                    }
                    catch (NumberFormatException e)
                    {
                        logConvertionFailure(name, value[i], "Integer");
                    }
                }
            }
        }
        return result;
    }

    /**
     * Return an array of Integers for the given name.  If the name
     * does not exist, return null.
     *
     * @param name A String with the name.
     * @return An Integer[].
     * @deprecated use {@link #getIntObjects} instead
     */
    public Integer[] getIntegers(String name)
    {
        return getIntObjects(name);
    }

    /**
     * Return a long for the given name.  If the name does not exist,
     * return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A long.
     */
    public long getLong(String name, long defaultValue)
    {
        long result = defaultValue;
        String value = getString(name);
        if (StringUtils.isNotEmpty(value))
        {
            try
            {
                result = Long.valueOf(value).longValue();
            }
            catch (NumberFormatException e)
            {
                logConvertionFailure(name, value, "Long");
            }
        }
        return result;
    }

    /**
     * Return a long for the given name.  If the name does not exist,
     * return 0.
     *
     * @param name A String with the name.
     * @return A long.
     */
    public long getLong(String name)
    {
        return getLong(name, 0);
    }

    /**
     * Return an array of longs for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return A long[].
     */
    public long[] getLongs(String name)
    {
        long[] result = null;
        String value[] = getStrings(name);
        if (value != null)
        {
            result = new long[value.length];
            for (int i = 0; i < value.length; i++)
            {
                if (StringUtils.isNotEmpty(value[i]))
                {
                    try
                    {
                        result[i] = Long.parseLong(value[i]);
                    }
                    catch (NumberFormatException e)
                    {
                        logConvertionFailure(name, value[i], "Long");
                    }
                }
            }
        }
        return result;
    }

    /**
     * Return an array of Longs for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return A Long[].
     */
    public Long[] getLongObjects(String name)
    {
        Long[] result = null;
        String value[] = getStrings(convert(name));
        if (value != null)
        {
            result = new Long[value.length];
            for (int i = 0; i < value.length; i++)
            {
                if (StringUtils.isNotEmpty(value[i]))
                {
                    try
                    {
                        result[i] = Long.valueOf(value[i]);
                    }
                    catch (NumberFormatException e)
                    {
                        logConvertionFailure(name, value[i], "Long");
                    }
                }
            }
        }
        return result;
    }

    /**
     * Return a Long for the given name.  If the name does
     * not exist, return null.
     *
     * @param name A String with the name.
     * @return A Long.
     */
    public Long getLongObject(String name)
    {
        Long result = null;
        String value = getString(name);
        if (StringUtils.isNotEmpty(value))
        {
            try
            {
                result = new Long(value);
            }
            catch(NumberFormatException e)
            {
                logConvertionFailure(name, value, "Long");
            }
        }
        return result;
    }

    /**
     * Return a Long for the given name.  If the name does
     * not exist, return the default value.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A Long.
     */
    public Long getLongObject(String name, Long defaultValue)
    {
        Long result = getLongObject(name);
        return (result==null ? defaultValue : result);
    }

    /**
     * Return a byte for the given name.  If the name does not exist,
     * return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A byte.
     */
    public byte getByte(String name, byte defaultValue)
    {
        byte result = defaultValue;
        String value = getString(name);
        if (StringUtils.isNotEmpty(value))
        {
            try
            {
                result = Byte.valueOf(value).byteValue();
            }
            catch (NumberFormatException e)
            {
                logConvertionFailure(name, value, "Byte");
            }
        }
        return result;
    }

    /**
     * Return a byte for the given name.  If the name does not exist,
     * return 0.
     *
     * @param name A String with the name.
     * @return A byte.
     */
    public byte getByte(String name)
    {
        return getByte(name, (byte) 0);
    }

    /**
     * Return an array of bytes for the given name.  If the name does
     * not exist, return null. The array is returned according to the
     * HttpRequest's character encoding.
     *
     * @param name A String with the name.
     * @return A byte[].
     * @exception UnsupportedEncodingException
     */
    public byte[] getBytes(String name)
            throws UnsupportedEncodingException
    {
        byte result[] = null;
        String value = getString(name);
        if (StringUtils.isNotEmpty(value))
        {
            result = value.getBytes(getCharacterEncoding());
        }
        return result;
    }

    /**
     * Return a byte for the given name.  If the name does not exist,
     * return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A byte.
     */
    public Byte getByteObject(String name, Byte defaultValue)
    {
        Byte result = getByteObject(name);
        return (result==null ? defaultValue : result);
    }

    /**
     * Return a byte for the given name.  If the name does not exist,
     * return 0.
     *
     * @param name A String with the name.
     * @return A byte.
     */
    public Byte getByteObject(String name)
    {
        Byte result = null;
        String value = getString(name);
        if (StringUtils.isNotEmpty(value))
        {
            try
            {
                result = new Byte(value);
            }
            catch(NumberFormatException e)
            {
                logConvertionFailure(name, value, "Byte");
            }
        }
        return result;
    }

    /**
     * Return a String for the given name.  If the name does not
     * exist, return null.
     *
     * @param name A String with the name.
     * @return A String.
     */
    public String getString(String name)
    {
        String result = null;
        try
        {
            Object value = parameters.get(convert(name));
            if (value != null)
                value = ((String[]) value)[0];
            if (value == null || value.equals("null"))
                return null;
            result = (String) value;
        }
        catch (ClassCastException e)
        {
            log.fatal("Parameter ("
                    + name + ") wasn not stored as a String", e);
        }

        return result;
    }

    /**
     * Return a String for the given name.  If the name does not
     * exist, return null. It is the same as the getString() method
     * however has been added for simplicity when working with
     * template tools such as Velocity which allow you to do
     * something like this:
     *
     * <code>$data.Parameters.form_variable_name</code>
     *
     * @param name A String with the name.
     * @return A String.
     */
    public String get(String name)
    {
        return getString(name);
    }

    /**
     * Return a String for the given name.  If the name does not
     * exist, return the defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A String.
     */
    public String getString(String name, String defaultValue)
    {
        String value = getString(name);

        return (StringUtils.isEmpty(value) ? defaultValue : value );
    }

    /**
     * Set a parameter to a specific value.
     *
     * This is useful if you want your action to override the values
     * of the parameters for the screen to use.
     * @param name The name of the parameter.
     * @param value The value to set.
     */
    public void setString(String name, String value)
    {
        if (value != null)
        {
            parameters.put(convert(name), new String[]{value});
        }
    }

    /**
     * Return an array of Strings for the given name.  If the name
     * does not exist, return null.
     *
     * @param name A String with the name.
     * @return A String[].
     */
    public String[] getStrings(String name)
    {
        return (String[]) parameters.get(convert(name));
    }

    /**
     * Return an array of Strings for the given name.  If the name
     * does not exist, return the defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A String[].
     */
    public String[] getStrings(String name, String[] defaultValue)
    {
        String[] value = getStrings(name);

        return (value == null || value.length == 0)
            ? defaultValue : value;
    }

    /**
     * Set a parameter to a specific value.
     *
     * This is useful if you want your action to override the values
     * of the parameters for the screen to use.
     * @param name The name of the parameter.
     * @param values The value to set.
     */
    public void setStrings(String name, String[] values)
    {
        if (values != null)
        {
            parameters.put(convert(name), values);
        }
    }

    /**
     * Return an Object for the given name.  If the name does not
     * exist, return null.
     *
     * @param name A String with the name.
     * @return An Object.
     */
    public Object getObject(String name)
    {
        return getString(name);
    }

    /**
     * Return an array of Objects for the given name.  If the name
     * does not exist, return null.
     *
     * @param name A String with the name.
     * @return An Object[].
     */
    public Object[] getObjects(String name)
    {
        return getStrings(name);
    }

    /**
     * Returns a {@link java.util.Date} object.  String is parsed by supplied
     * DateFormat.  If the name does not exist or the value could not be
     * parsed into a date return the defaultValue.
     *
     * @param name A String with the name.
     * @param df A DateFormat.
     * @param defaultValue The default value.
     * @return A Date.
     */
    public Date getDate(String name, DateFormat df, Date defaultValue)
    {
        Date result = defaultValue;
        String value = getString(name);

        if (StringUtils.isNotEmpty(value))
        {
            try
            {
                // Reject invalid dates.
                df.setLenient(false);
                result = df.parse(value);
            }
            catch (ParseException e)
            {
                logConvertionFailure(name, value, "Date");
            }
        }

        return result;
    }

    /**
     * Returns a {@link java.util.Date} object.  If there are DateSelector or
     * TimeSelector style parameters then these are used.  If not and there
     * is a parameter 'name' then this is parsed by DateFormat.  If the
     * name does not exist, return null.
     *
     * @param name A String with the name.
     * @return A Date.
     */
    public Date getDate(String name)
    {
        Date date = null;

        if (containsDateSelectorKeys(name))
        {
            try
            {
                Calendar cal = new GregorianCalendar(
                        getInt(name + DateSelector.YEAR_SUFFIX),
                        getInt(name + DateSelector.MONTH_SUFFIX),
                        getInt(name + DateSelector.DAY_SUFFIX));

                // Reject invalid dates.
                cal.setLenient(false);
                date = cal.getTime();
            }
            catch (IllegalArgumentException e)
            {
                logConvertionFailure(name, "n/a", "Date");
            }
        }
        else if (containsTimeSelectorKeys(name))
        {
            try
            {
                String ampm = getString(name + TimeSelector.AMPM_SUFFIX);
                int hour = getInt(name + TimeSelector.HOUR_SUFFIX);

                // Convert from 12 to 24hr format if appropriate
                if (ampm != null)
                {
                    if (hour == 12)
                    {
                        hour = (Integer.parseInt(ampm) == Calendar.PM) ? 12 : 0;
                    }
                    else if (Integer.parseInt(ampm) == Calendar.PM)
                    {
                        hour += 12;
                    }
                }
                Calendar cal = new GregorianCalendar(1, 1, 1,
                        hour,
                        getInt(name + TimeSelector.MINUTE_SUFFIX),
                        getInt(name + TimeSelector.SECOND_SUFFIX));

                // Reject invalid dates.
                cal.setLenient(false);
                date = cal.getTime();
            }
            catch (IllegalArgumentException e)
            {
                logConvertionFailure(name, "n/a", "Date");
            }
        }
        else
        {
            DateFormat df = DateFormat.getDateInstance();
            date = getDate(name, df, null);
        }

        return date;
    }

    /**
     * Returns a {@link java.util.Date} object.  String is parsed by supplied
     * DateFormat.  If the name does not exist, return null.
     *
     * @param name A String with the name.
     * @param df A DateFormat.
     * @return A Date.
     */
    public Date getDate(String name, DateFormat df)
    {
        return getDate(name, df, null);
    }

    /**
     * Return an NumberKey for the given name.  If the name does not
     * exist, return null.
     *
     * @param name A String with the name.
     * @return A NumberKey, or <code>null</code> if unparsable.
     * @deprecated no replacement
     */
    public NumberKey getNumberKey(String name)
    {
        NumberKey result = null;
        try
        {
            String value = getString(name);
            if (StringUtils.isNotEmpty(value))
            {
                result = new NumberKey(value);
            }
        }
        catch (ClassCastException e)
        {
            log.error("Parameter ("
                    + name + ") could not be converted to a NumberKey", e);
        }
        return result;
    }

    /**
     * Return an StringKey for the given name.  If the name does not
     * exist, return null.
     *
     * @param name A String with the name.
     * @return A StringKey, or <code>null</code> if unparsable.
     * @deprecated no replacement
     */
    public StringKey getStringKey(String name)
    {
        StringKey result = null;
        try
        {
            String value = getString(name);
            if (StringUtils.isNotEmpty(value))
            {
                result = new StringKey(value);
            }
        }
        catch (ClassCastException e)
        {
            log.error("Parameter ("
                    + name + ") could not be converted to a StringKey", e);
        }
        return result;
    }

    /**
     * Uses bean introspection to set writable properties of bean from
     * the parameters, where a (case-insensitive) name match between
     * the bean property and the parameter is looked for.
     *
     * @param bean An Object.
     * @exception Exception a generic exception.
     */
    public void setProperties(Object bean) throws Exception
    {
        Class beanClass = bean.getClass();
        PropertyDescriptor[] props
                = Introspector.getBeanInfo(beanClass).getPropertyDescriptors();

        for (int i = 0; i < props.length; i++)
        {
            String propname = props[i].getName();
            Method setter = props[i].getWriteMethod();
            if (setter != null &&
                    (containsKey(propname) ||
                    containsDateSelectorKeys(propname) ||
                    containsTimeSelectorKeys(propname)))
            {
                setProperty(bean, props[i]);
            }
        }
    }

    /**
     * Simple method that attempts to get a textual representation of
     * this object's name/value pairs.  String[] handling is currently
     * a bit rough.
     *
     * @return A textual representation of the parsed name/value pairs.
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        for (Iterator iter = keySet().iterator(); iter.hasNext();)
        {
            String name = (String) iter.next();
            try
            {
                sb.append('{');
                sb.append(name);
                sb.append('=');
                String[] params = this.getStrings(name);
                if (params.length <= 1)
                {
                    sb.append(params[0]);
                }
                else
                {
                    for (int i = 0; i < params.length; i++)
                    {
                        if (i != 0)
                        {
                            sb.append(", ");
                        }
                        sb.append('[')
                                .append(params[i])
                                .append(']');
                    }
                }
                sb.append("}\n");
            }
            catch (Exception ee)
            {
                try
                {
                    sb.append('{');
                    sb.append(name);
                    sb.append('=');
                    sb.append("ERROR?");
                    sb.append("}\n");
                }
                catch (Exception eee)
                {
                }
            }
        }
        return sb.toString();
    }

    /**
     * Set the property 'prop' in the bean to the value of the
     * corresponding parameters.  Supports all types supported by
     * getXXX methods plus a few more that come for free because
     * primitives have to be wrapped before being passed to invoke
     * anyway.
     *
     * @param bean An Object.
     * @param prop A PropertyDescriptor.
     * @exception Exception a generic exception.
     */
    protected void setProperty(Object bean,
                               PropertyDescriptor prop)
            throws Exception
    {
        if (prop instanceof IndexedPropertyDescriptor)
        {
            throw new Exception(prop.getName() +
                    " is an indexed property (not supported)");
        }

        Method setter = prop.getWriteMethod();
        if (setter == null)
        {
            throw new Exception(prop.getName() +
                    " is a read only property");
        }

        Class propclass = prop.getPropertyType();
        Object[] args = {null};

        if (propclass == String.class)
        {
            args[0] = getString(prop.getName());
        }
        else if (propclass == Integer.class || propclass == Integer.TYPE)
        {
            args[0] = getInteger(prop.getName());
        }
        else if (propclass == Long.class || propclass == Long.TYPE)
        {
            args[0] = new Long(getLong(prop.getName()));
        }
        else if (propclass == Boolean.class || propclass == Boolean.TYPE)
        {
            args[0] = getBool(prop.getName());
        }
        else if (propclass == Double.class || propclass == Double.TYPE)
        {
            args[0] = new Double(getDouble(prop.getName()));
        }
        else if (propclass == BigDecimal.class)
        {
            args[0] = getBigDecimal(prop.getName());
        }
        else if (propclass == String[].class)
        {
            args[0] = getStrings(prop.getName());
        }
        else if (propclass == Object.class)
        {
            args[0] = getObject(prop.getName());
        }
        else if (propclass == int[].class)
        {
            args[0] = getInts(prop.getName());
        }
        else if (propclass == Integer[].class)
        {
            args[0] = getIntegers(prop.getName());
        }
        else if (propclass == Date.class)
        {
            args[0] = getDate(prop.getName());
        }
        else if (propclass == NumberKey.class)
        {
            args[0] = getNumberKey(prop.getName());
        }
        else if (propclass == StringKey.class)
        {
            args[0] = getStringKey(prop.getName());
        }
        else
        {
            throw new Exception("property "
                    + prop.getName()
                    + " is of unsupported type "
                    + propclass.toString());
        }

        setter.invoke(bean, args);
    }

    /**
     * Writes a log message about a convertion failure.
     *
     * @param paramName name of the parameter which could not be converted
     * @param value value of the parameter
     * @param type target data type.
     */
    private void logConvertionFailure(String paramName,
                                      String value, String type)
    {
        log.warn("Parameter (" + paramName
                + ") with value of ("
                + value + ") could not be converted to a " + type);
    }
}
