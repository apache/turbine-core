package org.apache.turbine.services.intake;

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

import java.util.Date;

public class RequiredFalseGroupTestObject
{
    private String stringRF;
    private Integer integerRF = null;
    private int intRF;
    private Date dateRF;

    /**
     * @return the dateRF
     */
    public Date getDateRF()
    {
        return dateRF;
    }

    /**
     * @param dateRF the dateRF to set
     */
    public void setDateRF(Date dateRF)
    {
        this.dateRF = dateRF;
    }

    /**
     * @return the integerRF
     */
    public Integer getIntegerRF()
    {
        return integerRF;
    }

    /**
     * @param integerRF the integerRF to set
     */
    public void setIntegerRF(Integer integerRF)
    {
        this.integerRF = integerRF;
    }

    /**
     * @return the intRF
     */
    public int getIntRF()
    {
        return intRF;
    }
    
    /**
     * @param intRF the intRF to set
     */
    public void setIntRF(int intRF)
    {
        this.intRF = intRF;
    }
    
    /**
     * @return the stringRF
     */
    public String getStringRF()
    {
        return stringRF;
    }

    /**
     * @param stringRF the stringRF to set
     */
    public void setStringRF(String stringRF)
    {
        this.stringRF = stringRF;
    }
}
