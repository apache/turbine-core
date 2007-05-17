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

import java.util.Calendar;
import java.util.Date;

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
    Group requiredFalseTestGroup = null;
    Group requiredTrueTestGroup = null;

    public IntakeServiceTest(String name) throws Exception
    {
        super(name, "conf/test/TurbineResourcesWithIntake.properties");

        ServiceManager serviceManager = TurbineServices.getInstance();
        IntakeService intakeService = (IntakeService) serviceManager.getService(IntakeService.SERVICE_NAME);
        booleanTestGroup = intakeService.getGroup("BooleanTest");
        rangeTestGroup = intakeService.getGroup("DateRangeTest");
        integerRangeTestGroup = intakeService.getGroup("IntRangeTest");
        requiredFalseTestGroup = intakeService.getGroup("RequiredFalseTest");
        requiredTrueTestGroup = intakeService.getGroup("RequiredTrueTest");
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
     * This test verifies that an intake field returns true for isSet() even
     * when an empty field is submitted.
     *
     * @throws IntakeException
     */
    public void testRequiredFalse() throws IntakeException
    {
        ParameterParser pp = new DefaultParameterParser();
        pp.add("rft_0stringrf", "");
        pp.add("rft_0integerrf", "");
        pp.add("rft_0intrf", "");
        pp.add("rft_0daterf", "");
        requiredFalseTestGroup.init(Group.NEW, pp);

        Field stringRF = requiredFalseTestGroup.get("StringRF");
        Field integerRF = requiredFalseTestGroup.get("IntegerRF");
        Field intRF = requiredFalseTestGroup.get("IntRF");
        Field dateRF = requiredFalseTestGroup.get("DateRF");

        assertTrue("StringRF should be set", stringRF.isSet());
        assertTrue("StringRF should be valid", stringRF.isValid());
        assertNull(stringRF.getValue());
        assertTrue("IntegerRF should be set", integerRF.isSet());
        assertTrue("IntegerRF should be valid", integerRF.isValid());
        assertNull(integerRF.getValue());
        assertTrue("IntRF should be set", intRF.isSet());
        assertTrue("IntRF should be valid", intRF.isValid());
        assertNull(intRF.getValue()); // zero?
        assertTrue("DateRF should be set", dateRF.isSet());
        assertTrue("DateRF should be valid", dateRF.isValid());
        assertNull(dateRF.getValue());
    }

    /**
     * This test verify that an empty field can be used to clear existing
     * values.
     *
     * @throws IntakeException
     */
    public void testClearValues() throws IntakeException
    {
        RequiredFalseGroupTestObject rfgto = new RequiredFalseGroupTestObject();
        rfgto.setStringRF("originalString");
        rfgto.setIntegerRF(new Integer(5));
        rfgto.setIntRF(6);
        Date testDate = new Date();
        rfgto.setDateRF(testDate);

        ParameterParser pp = new DefaultParameterParser();
        pp.add("rft_0stringrf", "");
        pp.add("rft_0integerrf", "");
        pp.add("rft_0intrf", "");
        pp.add("rft_0daterf", "");
        requiredFalseTestGroup.init(Group.NEW, pp);

        requiredFalseTestGroup.setProperties(rfgto);
        assertNull("String value should have been cleared.", rfgto.getStringRF());
        assertNull("Date value should have been cleared.", rfgto.getDateRF());
        assertEquals("int value should have been cleared to zero.", 0, rfgto.getIntRF());

        // The following commented out test fails.
        // The trouble is that the use of reflection forces Intake to use Integer rather than int
        // when invoking the setter method on the object to which the group is being mapped.

        //assertNull("Integer value should have been cleared to null, but instead it is "
        //        + rfgto.getIntegerRF(), rfgto.getIntegerRF());

        // The net result is that Intake is currently not well suited to validating
        // Integer fields where a null value needs to be distinguished from a zero.
    }

    /**
     * This test attempts to verify that with that valid values coming from
     * intake map through to an object correctly.
     *
     * @throws IntakeException
     */
    public void testSetValues() throws IntakeException
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date testDate = cal.getTime();
        // This is in dd/mm/yyyy format, as defined in the intake.xml for this group.
        String testDateString = cal.get(Calendar.DAY_OF_MONTH) + "/"
                + (cal.get(Calendar.MONTH) + 1) + "/" + (cal.get(Calendar.YEAR));

        ParameterParser pp = new DefaultParameterParser();
        pp.add("rft_0stringrf", "ABC"); // rules require 3 characters.
        pp.add("rft_0integerrf", new Integer(10));
        pp.add("rft_0intrf", 11);
        pp.add("rft_0daterf", testDateString);
        requiredFalseTestGroup.init(Group.NEW, pp);

        Field stringRF = requiredFalseTestGroup.get("StringRF");
        Field integerRF = requiredFalseTestGroup.get("IntegerRF");
        Field intRF = requiredFalseTestGroup.get("IntRF");
        Field dateRF = requiredFalseTestGroup.get("DateRF");

        assertTrue("StringRF should be set", stringRF.isSet());
        assertTrue("StringRF should be valid", stringRF.isValid());
        assertEquals("ABC", stringRF.getValue());
        assertTrue("IntegerRF should be set", integerRF.isSet());
        assertTrue("IntegerRF should be valid", integerRF.isValid());
        assertEquals(new Integer(10), integerRF.getValue());
        assertTrue("IntRF should be set", intRF.isSet());
        assertTrue("IntRF should be valid", intRF.isValid());
        assertEquals(11, ((Integer) intRF.getValue()).intValue());
        assertTrue("DateRF should be set", dateRF.isSet());
        assertTrue("DateRF should be valid", dateRF.isValid());
        assertEquals(testDate, dateRF.getValue());

        RequiredFalseGroupTestObject rfgto = new RequiredFalseGroupTestObject();
        requiredFalseTestGroup.setProperties(rfgto);
        assertEquals("ABC", rfgto.getStringRF());
        assertEquals(new Integer(10), rfgto.getIntegerRF());
        assertEquals(11, rfgto.getIntRF());
        assertEquals(testDate, rfgto.getDateRF());
    }

    /**
     * Test that a required field with no value assigned is invalid.
     *
     * @throws IntakeException
     */
    public void testRequiredTrue() throws IntakeException
    {
        ParameterParser pp = new DefaultParameterParser();
        pp.add("rtt_0stringrt", "");
        requiredTrueTestGroup.init(Group.NEW, pp);

        Field stringRT = requiredTrueTestGroup.get("StringRT");

        assertTrue("StringRT should be set", stringRT.isSet());
        assertFalse("StringRT should not be valid", stringRT.isValid());
        assertEquals("", stringRT.getValue());
    }

    /**
     * This test verifies that a newly created group is not initiated in an
     * error state.
     *
     * @throws IntakeException
     */
    public void testInitialErrorState() throws IntakeException
    {
        ParameterParser pp = new DefaultParameterParser();
        requiredFalseTestGroup.init(Group.NEW, pp);

        Field stringRF = requiredFalseTestGroup.get("StringRF");
        Field integerRF = requiredFalseTestGroup.get("IntegerRF");
        Field intRF = requiredFalseTestGroup.get("IntRF");
        Field dateRF = requiredFalseTestGroup.get("DateRF");

        assertFalse("StringRF should not be set", stringRF.isSet());
        assertTrue("StringRF should be valid", stringRF.isValid());
        assertEquals("StringRF should have no messages.", "", stringRF.getMessage());
        assertNull(stringRF.getValue());
        assertFalse("IntegerRF should not be set", integerRF.isSet());
        assertTrue("IntegerRF should be valid", integerRF.isValid());
        assertEquals("IntegerRF should have no messages", "", integerRF.getMessage());
        assertNull(integerRF.getValue());
        assertFalse("IntRF should not be set", intRF.isSet());
        assertTrue("IntRF should be valid", intRF.isValid());
        assertEquals("IntRF should have no messages", "", intRF.getMessage());
        assertNull(intRF.getValue());
        assertFalse("DateRF should not be set", dateRF.isSet());
        assertTrue("DateRF should be valid", dateRF.isValid());
        assertEquals("DateRF should have no messages", "", dateRF.getMessage());
        assertNull(dateRF.getValue());

        requiredTrueTestGroup.init(Group.NEW, pp);
        Field stringRT = requiredTrueTestGroup.get("StringRT");

        assertFalse("StringRT should not be set", stringRT.isSet());
        assertTrue("StringRT should be valid", stringRT.isValid());
        assertEquals("StringRT should have no messages.", "", stringRT.getMessage());
        assertNull(stringRT.getValue());
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
