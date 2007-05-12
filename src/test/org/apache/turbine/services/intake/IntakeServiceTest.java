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

import junit.framework.TestSuite;

import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.intake.model.Field;
import org.apache.turbine.services.intake.model.Group;
import org.apache.turbine.services.intake.validator.BooleanValidator;
import org.apache.turbine.services.intake.validator.DateRangeValidator;
import org.apache.turbine.services.intake.validator.IntegerRangeValidator;
import org.apache.turbine.test.BaseTurbineTest;
import org.apache.turbine.util.parser.DefaultParameterParser;
import org.apache.turbine.util.parser.ParameterParser;


public class IntakeServiceTest extends BaseTurbineTest
{

    Group booleanTestGroup = null;
    Group rangeTestGroup = null;
    Group integerRangeTestGroup = null;

    public IntakeServiceTest(String name) throws Exception
    {
        super(name, "conf/test/TurbineResourcesWithIntake.properties");

        ServiceManager serviceManager = TurbineServices.getInstance();
        IntakeService intakeService = (IntakeService) serviceManager.getService(IntakeService.SERVICE_NAME);
        booleanTestGroup = intakeService.getGroup("BooleanTest");
        rangeTestGroup = intakeService.getGroup("DateRangeTest");
        integerRangeTestGroup = intakeService.getGroup("IntRangeTest");
    }


    public void testEmptyBooleanField() throws IntakeException
    {
        Field booleanField = booleanTestGroup.get("EmptyBooleanTestField");
        assertTrue("The Default Validator of an intake Field type boolean should be BooleanValidator", (booleanField.getValidator() instanceof BooleanValidator));
        assertFalse("An Empty intake Field type boolean should not be required", booleanField.isRequired());
    }

    public void testBooleanField() throws IntakeException
    {
        Field booleanField = booleanTestGroup.get("BooleanTestField");
        assertTrue("The Default Validator of an intake Field type boolean should be BooleanValidator", (booleanField.getValidator() instanceof BooleanValidator));
        assertFalse("An intake Field type boolean, which is not required, should not be required", booleanField.isRequired());
    }

    public void testRequiredBooleanField() throws IntakeException
    {
        Field booleanField = booleanTestGroup.get("RequiredBooleanTestField");
        assertTrue("The Default Validator of an intake Field type boolean should be BooleanValidator", (booleanField.getValidator() instanceof BooleanValidator));
        assertTrue("An intake Field type boolean, which is required, should be required", booleanField.isRequired());
    }

    public void testDateRangeValidator() throws IntakeException
    {
        ParameterParser pp = new DefaultParameterParser();
        pp.add("rt_0dmin", "05/11/2007");
        pp.add("rt_0dmax", "05/12/2007");
        pp.add("rt_0dmax2", "05/11/2007");
        rangeTestGroup.init(Group.NEW, pp);

        Field dmax = rangeTestGroup.get("DateMax");
        Field dmax2 = rangeTestGroup.get("DateMax2");

        assertTrue("The Validator of the field DateMax should be a DateRangeValidator", (dmax.getValidator() instanceof DateRangeValidator));
        assertTrue("The date range should be valid", dmax.isValid());
        assertFalse("The date range should not be valid", dmax2.isValid());
    }

    public void testIntegerRangeValidator() throws IntakeException
    {
        ParameterParser pp = new DefaultParameterParser();
        pp.add("irt_0imin", "1");
        pp.add("irt_0imax", "3");
        pp.add("irt_0imax2", "2");
        integerRangeTestGroup.init(Group.NEW, pp);

        Field imax = integerRangeTestGroup.get("IntMax");
        Field imax2 = integerRangeTestGroup.get("IntMax2");

        assertTrue("The Validator of the field IntMax should be an IntegerRangeValidator", (imax.getValidator() instanceof IntegerRangeValidator));
        assertTrue("The integer range should be valid", imax.isValid());
        assertFalse("The integer range should not be valid", imax2.isValid());
    }

    /**
     * Factory method for creating a TestSuite for this class.
     *
     * @return the test suite
     */
    public static TestSuite suite()
    {
        TestSuite suite = new TestSuite(IntakeServiceTest.class);
        return suite;
    }

}
