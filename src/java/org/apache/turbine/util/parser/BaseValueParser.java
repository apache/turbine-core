package org.apache.turbine.util.parser;

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

import java.beans.IndexedPropertyDescriptor;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.io.UnsupportedEncodingException;

import java.lang.reflect.Method;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import org.apache.torque.om.NumberKey;
import org.apache.torque.om.StringKey;

import org.apache.turbine.services.resources.TurbineResources;

import org.apache.turbine.util.DateSelector;
import org.apache.turbine.util.StringUtils;
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
 * @version $Id$
 */
public class BaseValueParser
    extends RecyclableSupport
    implements ValueParser,
               Recyclable
{
    /**
     * Random access storage for parameter data.
     */
    protected Hashtable parameters = new Hashtable();
    /**
     * The character encoding to use when converting to byte arrays
     */
    private String characterEncoding = "US-ASCII";

    /**
     * A static version of the convert method, which
     * trims the string data and applies the conversion specified in
     * the property given by URL_CASE_FOLDING.  It returns a new
     * string so that it does not destroy the value data.
     *
     * @param value A String to be processed.
     * @return A new String converted to lowercase and trimmed.
     */
    public static String convertAndTrim ( String value )
    {
        String tmp = value.trim();
        String fold =
            TurbineResources.getString(URL_CASE_FOLDING, "")
            .toLowerCase();
        if ((fold == null) ||
            (fold.equals("")) ||
            (fold.equals(URL_CASE_FOLDING_LOWER)))
            return (tmp.toLowerCase());
        else if (fold.equals(URL_CASE_FOLDING_UPPER))
            return (tmp.toUpperCase());

        return (tmp);
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
            super.recycle();
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
    public void setCharacterEncoding (String s)
    {
        characterEncoding = s;
    }

    /**
     * Get the character encoding that will be used by this ValueParser.
     */
    public String getCharacterEncoding ()
    {
        return characterEncoding;
    }

    /**
     * Add a name/value pair into this object.
     *
     * @param name A String with the name.
     * @param value A double with the value.
     */
    public void add ( String name,
                      double value )
    {
        add ( name, Double.toString(value));
    }

    /**
     * Add a name/value pair into this object.
     *
     * @param name A String with the name.
     * @param value An int with the value.
     */
    public void add ( String name,
                      int value )
    {
        add ( name, Integer.toString(value));
    }

    /**
     * Add a name/value pair into this object.
     *
     * @param name A String with the name.
     * @param value An Integer with the value.
     */
    public void add ( String name,
                      Integer value )
    {
        add ( name, value.toString());
    }

    /**
     * Add a name/value pair into this object.
     *
     * @param name A String with the name.
     * @param value A long with the value.
     */
    public void add ( String name,
                      long value )
    {
        add ( name, Long.toString(value));
    }

    /**
     * Add a name/value pair into this object.
     *
     * @param name A String with the name.
     * @param value A long with the value.
     */
    public void add ( String name,
                      String value )
    {
        append ( name, value);
    }

    /**
     * Add a String parameters.  If there are any Strings already
     * associated with the name, append to the array.  This is used
     * for handling parameters from mulitipart POST requests.
     *
     * @param name A String with the name.
     * @param value A String with the value.
     */
    public void append( String name,
                        String value )
    {
        String[] items = this.getStrings(name);
        if(items == null)
        {
            items = new String[1];
            items[0] = value;
            parameters.put( convert(name), items );
        }
        else
        {
            String[] newItems = new String[items.length+1];
            System.arraycopy(items, 0, newItems, 0, items.length);
            newItems[items.length] = value;
            parameters.put( convert(name), newItems );
        }
    }

    /**
     * Removes the named parameter from the contained hashtable. Wraps to the
     * contained <code>Hashtable.remove()</code>.
     *
     *
     * @return The value that was mapped to the key (a <code>String[]</code>)
     *         or <code>null</code> if the key was not mapped.
     */
    public Object remove(String name)
    {
        return parameters.remove( convert(name) );
    }

    /**
     * Trims the string data and applies the conversion specified in
     * the property given by URL_CASE_FOLDING.  It returns a new
     * string so that it does not destroy the value data.
     *
     * @param value A String to be processed.
     * @return A new String converted to lowercase and trimmed.
     */
    public String convert ( String value )
    {
        return convertAndTrim(value);
    }

    /**
     * Determine whether a given key has been inserted.  All keys are
     * stored in lowercase strings, so override method to account for
     * this.
     *
     * @param key An Object with the key to search for.
     * @return True if the object is found.
     */
    public boolean containsKey( Object key )
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


    /*
     * Get an enumerator for the parameter keys. Wraps to the
     * contained <code>Hashtable.keys()</code>.
     *
     * @return An <code>enumerator</code> of the keys.
     */
    public Enumeration keys()
    {
        return parameters.keys();
    }

    /*
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
    public boolean getBoolean(String name,
                              boolean defaultValue)
    {
        boolean value = defaultValue;
        Object object = parameters.get(convert(name));
        if (object != null)
        {
            String tmp = getString(name);
            if ( tmp.equalsIgnoreCase ("1") ||
                 tmp.equalsIgnoreCase ("true") ||
                 tmp.equalsIgnoreCase ("on") )
            {
                value = true;
            }
            if ( tmp.equalsIgnoreCase ("0") ||
                 tmp.equalsIgnoreCase ("false") )
            {
                value = false;
            }
        }
        return value;
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
     * Return a Boolean for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A Boolean.
     */
    public Boolean getBool(String name,
                           boolean defaultValue)
    {
        return new Boolean(getBoolean(name, defaultValue));
    }

    /**
     * Return a Boolean for the given name.  If the name does not
     * exist, return false.
     *
     * @param name A String with the name.
     * @return A Boolean.
     */
    public Boolean getBool(String name)
    {
        return new Boolean(getBoolean(name, false));
    }

    /**
     * Return a double for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A double.
     */
    public double getDouble(String name,
                            double defaultValue)
    {
        double value = defaultValue;
        try
        {
            Object object = parameters.get(convert(name));
            if (object != null)
                value = Double.valueOf(((String[])object)[0]).doubleValue();
        }
        catch (NumberFormatException exception)
        {
        }
        return value;
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
     * Return a float for the given name.  If the name does not
     * exist, return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A float.
     */
    public float getFloat(String name,
                            float defaultValue)
    {
        float value = defaultValue;
        try
        {
            Object object = parameters.get(convert(name));
            if (object != null)
                value = Float.valueOf(((String[])object)[0]).floatValue();
        }
        catch (NumberFormatException exception)
        {
        }
        return value;
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
     * Return a BigDecimal for the given name.  If the name does not
     * exist, return 0.0.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A BigDecimal.
     */
    public BigDecimal getBigDecimal(String name,
                                    BigDecimal defaultValue)
    {
        BigDecimal value = defaultValue;
        try
        {
            Object object = parameters.get(convert(name));
            if (object != null)
            {
                String temp = ((String[])object)[0];
                if (temp.length() > 0)
                {
                    value = new BigDecimal(((String[])object)[0]);
                }
            }
        }
        catch (NumberFormatException exception)
        {
        }
        return value;
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
        BigDecimal[] value = null;
        Object object = getStrings(convert(name));
        if (object != null)
        {
            String[] temp = (String[])object;
            value = new BigDecimal[temp.length];
            for (int i=0; i<temp.length; i++)
                value[i] = new BigDecimal( temp[i] );
        }
        return value;
    }

    /**
     * Return an int for the given name.  If the name does not exist,
     * return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return An int.
     */
    public int getInt(String name,
                      int defaultValue )
    {
        int value = defaultValue;
        try
        {
            Object object = parameters.get(convert(name));
            if (object != null)
                value = Integer.valueOf(((String[])object)[0]).intValue();
        }
        catch (NumberFormatException exception)
        {
        }
        return value;
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
    public Integer getInteger(String name,
                              int defaultValue)
    {
        return new Integer(getInt(name, defaultValue));
    }

    /**
     * Return an Integer for the given name.  If the name does not
     * exist, return defaultValue.  You cannot pass in a null here for
     * the default value.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return An Integer.
     */
    public Integer getInteger(String name,
                              Integer def)
    {
        return new Integer(getInt(name, def.intValue()));
    }

    /**
     * Return an Integer for the given name.  If the name does not
     * exist, return <code>null</code>.
     *
     * @param name A String with the name.
     * @return An Integer.
     */
    public Integer getInteger(String name)
    {
        if (containsKey(name))
        {
            return new Integer(getInt(name));
        }
        return null;
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
        int[] value = null;
        Object object = getStrings(convert(name));
        if (object != null)
        {
            String[] temp = (String[])object;
            value = new int[temp.length];
            for (int i=0; i<temp.length; i++)
                value[i] = Integer.parseInt( temp[i] );
        }
        return value;
    }

    /**
     * Return an array of Integers for the given name.  If the name
     * does not exist, return null.
     *
     * @param name A String with the name.
     * @return An Integer[].
     */
    public Integer[] getIntegers(String name)
    {
        Integer[] value = null;
        Object object = getStrings(convert(name));
        if (object != null)
        {
            String[] temp = (String[])object;
            value = new Integer[temp.length];
            for (int i=0; i<temp.length; i++)
                value[i] = Integer.valueOf( temp[i] );
        }
        return value;
    }

    /**
     * Return a long for the given name.  If the name does not exist,
     * return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A long.
     */
    public long getLong(String name,
                        long defaultValue )
    {
        long value = defaultValue;
        try
        {
            Object object = parameters.get(convert(name));
            if (object != null)
                value = Long.valueOf(((String[])object)[0]).longValue();
        }
        catch (NumberFormatException exception)
        {
        }
        return value;
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
        long[] value = null;
        Object object = getStrings(convert(name));
        if (object != null)
        {
            String[] temp = (String[])object;
            value = new long[temp.length];
            for (int i=0; i<temp.length; i++)
                value[i] = Long.parseLong( temp[i] );
        }
        return value;
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
        Long[] value = null;
        Object object = getStrings(convert(name));
        if (object != null)
        {
            String[] temp = (String[])object;
            value = new Long[temp.length];
            for (int i=0; i<temp.length; i++)
                value[i] = Long.valueOf( temp[i] );
        }
        return value;
    }

    /**
     * Return a byte for the given name.  If the name does not exist,
     * return defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A byte.
     */
    public byte getByte(String name,
                        byte defaultValue )
    {
        byte value = defaultValue;
        try
        {
            Object object = parameters.get(convert(name));
            if (object != null)
                value = Byte.valueOf(((String[])object)[0]).byteValue();
        }
        catch (NumberFormatException exception)
        {
        }
        return value;
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
     * @exception UnsupportedEncodingException.
     */
    public byte[] getBytes(String name)
        throws UnsupportedEncodingException
    {
        String tempStr = getString(name);
        if ( tempStr != null )
            return tempStr.getBytes(characterEncoding);
        return null;
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
        try
        {
            String value = null;
            Object object = parameters.get(convert(name));
            if (object != null)
                value = ((String[])object)[0];
            if (value == null || value.equals("null"))
                return null;
            return value;
        }
        catch ( ClassCastException e )
        {
            return null;
        }
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
    public String get (String name)
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
    public String getString(String name,
                            String defaultValue)
    {
        String value = getString(name);
        if (value == null ||
            value.length() == 0 ||
            value.equals("null"))
            return defaultValue;
        else
            return value;
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
        if(value != null)
        {
            parameters.put(convert(name), new String[] {value} );
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
        String[] value = null;
        Object object = parameters.get(convert(name));
        if (object != null)
            value = ((String[])object);
        return value;
    }

    /**
     * Return an array of Strings for the given name.  If the name
     * does not exist, return the defaultValue.
     *
     * @param name A String with the name.
     * @param defaultValue The default value.
     * @return A String[].
     */
    public String[] getStrings(String name,
                               String[] defaultValue)
    {
        String[] value = getStrings(name);
        if (value == null ||
            value.length == 0)
            return defaultValue;
        else
            return value;
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
        if(values != null)
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
        try
        {
            Object value = null;
            Object object = parameters.get(convert(name));
            if (object != null)
                value = ((Object[])object)[0];
            return value;
        }
        catch ( ClassCastException e )
        {
            return null;
        }
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
        try
        {
            return (Object[])parameters.get(convert(name));
        }
        catch ( ClassCastException e )
        {
            return null;
        }
    }

    /**
     * Returns a {@link java.util.Date} object.  String is parsed by supplied
     * DateFormat.  If the name does not exist, return the
     * defaultValue.
     *
     * @param name A String with the name.
     * @param df A DateFormat.
     * @param defaultValue The default value.
     * @return A Date.
     */
    public Date getDate(String name,
                        DateFormat df,
                        Date defaultValue)
    {
        Date date = null;

        if (containsKey(name))
        {
            try
            {
                // Reject invalid dates.
                df.setLenient(false);
                date = df.parse(getString(name));
            }
            catch (ParseException e)
            {
                // Thrown if couldn't parse date.
                date = defaultValue;
            }
        }
        else
            date = defaultValue;

        return date;
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
                Calendar cal =  new GregorianCalendar(
                        getInt(name + DateSelector.YEAR_SUFFIX),
                        getInt(name + DateSelector.MONTH_SUFFIX),
                        getInt(name + DateSelector.DAY_SUFFIX));

                // Reject invalid dates.
                cal.setLenient(false);
                date = cal.getTime();
            }
            catch (IllegalArgumentException e)
            {
                // Thrown if an invalid date.
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
                    if ( hour == 12 )
                    {
                        hour = (Integer.parseInt(ampm) == Calendar.PM) ? 12 : 0;
                    }
                    else if (Integer.parseInt(ampm) == Calendar.PM)
                    {
                        hour += 12;
                    }
                }
                Calendar cal =  new GregorianCalendar( 1, 1, 1,
                        hour,
                        getInt(name + TimeSelector.MINUTE_SUFFIX),
                        getInt(name + TimeSelector.SECOND_SUFFIX));

                // Reject invalid dates.
                cal.setLenient(false);
                date = cal.getTime();
            }
            catch (IllegalArgumentException e)
            {
                // Thrown if an invalid date.
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
    public Date getDate(String name,
                        DateFormat df)
    {
        return getDate(name, df, null);
    }

    /**
     * Return an NumberKey for the given name.  If the name does not
     * exist, return null.
     *
     * @param name A String with the name.
     * @return A NumberKey, or <code>null</code> if unparsable.
     */
    public NumberKey getNumberKey(String name)
    {
        try
        {
            String value = null;
            Object object = parameters.get(convert(name));
            if (object != null)
            {
                value = ((String[])object)[0];
            }
            return (StringUtils.isValid(value) ? new NumberKey(value) : null);
        }
        catch ( ClassCastException e )
        {
            return null;
        }
    }

    /**
     * Return an StringKey for the given name.  If the name does not
     * exist, return null.
     *
     * @param name A String with the name.
     * @return A StringKey, or <code>null</code> if unparsable.
     */
    public StringKey getStringKey(String name)
    {
        try
        {
            String value = null;
            Object object = parameters.get(convert(name));
            if (object != null)
            {
                value = ((String[])object)[0];
            }
            return (StringUtils.isValid(value) ? new StringKey(value) : null);
        }
        catch ( ClassCastException e )
        {
            return null;
        }
    }

    /**
     * Uses bean introspection to set writable properties of bean from
     * the parameters, where a (case-insensitive) name match between
     * the bean property and the parameter is looked for.
     *
     * @param bean An Object.
     * @exception Exception, a generic exception.
     */
    public void setProperties(Object bean)
        throws Exception
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
        for (Enumeration e = parameters.keys() ; e.hasMoreElements() ;)
        {
            String name = (String) e.nextElement();
            try
            {
                sb.append ('{');
                sb.append(name);
                sb.append('=');
                String[] params = this.getStrings(name);
                if ( params.length <= 1 )
                {
                    sb.append(params[0]);
                }
                else
                {
                    for ( int i=0; i<params.length; i++ )
                    {
                        if ( i != 0 )
                        {
                            sb.append(", ");
                        }
                        sb.append('[')
                          .append(params[i])
                          .append(']');
                    }
                }
                sb.append ("}\n");
            }
            catch ( Exception ee)
            {
                try
                {
                    sb.append ('{');
                    sb.append(name);
                    sb.append('=');
                    sb.append ("ERROR?");
                    sb.append ("}\n");
                }
                catch ( Exception eee )
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
     * @exception Exception, a generic exception.
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
        Object[] args = { null };

        if (propclass == String.class)
        {
            args[0] = getString(prop.getName());
        }
        else if (propclass == Integer.class || propclass == Integer.TYPE)
        {
            args[0] = getInteger(prop.getName());
        }
        else if (propclass == Long.class    || propclass == Long.TYPE)
        {
            args[0] = new Long(getLong(prop.getName()));
        }
        else if (propclass == Boolean.class || propclass == Boolean.TYPE)
        {
            args[0] = getBool(prop.getName());
        }
        else if (propclass == Double.class  || propclass == Double.TYPE)
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
}
