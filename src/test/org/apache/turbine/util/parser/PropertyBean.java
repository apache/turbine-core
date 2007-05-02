package org.apache.turbine.util.parser;

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

public class PropertyBean
{
    private int intValue;

    private long longValue;

    private double doubleValue;

    private String stringValue;

    private Boolean booleanValue;

    private String doNotTouchValue;

    /**
     * Get the LongValue value.
     * @return the LongValue value.
     */
    public long getLongValue()
    {
        return longValue;
    }

    /**
     * Set the LongValue value.
     * @param newLongValue The new LongValue value.
     */
    public void setLongValue(long newLongValue)
    {
        this.longValue = newLongValue;
    }
    /**
     * Get the DoubleValue value.
     * @return the DoubleValue value.
     */
    public double getDoubleValue()
    {
        return doubleValue;
    }

    /**
     * Set the DoubleValue value.
     * @param newDoubleValue The new DoubleValue value.
     */
    public void setDoubleValue(double newDoubleValue)
    {
        this.doubleValue = newDoubleValue;
    }

    /**
     * Get the StringValue value.
     * @return the StringValue value.
     */
    public String getStringValue()
    {
        return stringValue;
    }

    /**
     * Set the StringValue value.
     * @param newStringValue The new StringValue value.
     */
    public void setStringValue(String newStringValue)
    {
        this.stringValue = newStringValue;
    }

    /**
     * Get the IntValue value.
     * @return the IntValue value.
     */
    public int getIntValue()
    {
        return intValue;
    }

    /**
     * Set the BooleanValue value.
     * @param newBooleanValue The new BooleanValue value.
     */
    public void setBooleanValue(Boolean newBooleanValue)
    {
        this.booleanValue = newBooleanValue;
    }

    /**
     * Get the BooleanValue value.
     * @return the BooleanValue value.
     */
    public Boolean getBooleanValue()
    {
        return booleanValue;
    }

    /**
     * Set the IntValue value.
     * @param newIntValue The new IntValue value.
     */
    public void setIntValue(int newIntValue)
    {
        this.intValue = newIntValue;
    }

    /**
     * Get the DoNotTouchValue value.
     * @return the DoNotTouchValue value.
     */
    public String getDoNotTouchValue()
    {
        return doNotTouchValue;
    }

    /**
     * Set the DoNotTouchValue value.
     * @param newDoNotTouchValue The new DoNotTouchValue value.
     */
    public void setDoNotTouchValue(String newDoNotTouchValue)
    {
        this.doNotTouchValue = newDoNotTouchValue;
    }

}
