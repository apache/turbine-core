package org.apache.turbine.services.intake;

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

import junit.framework.TestSuite;

import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.intake.model.Field;
import org.apache.turbine.services.intake.model.Group;
import org.apache.turbine.services.intake.validator.BooleanValidator;
import org.apache.turbine.test.BaseTurbineTest;


public class IntakeServiceTest extends BaseTurbineTest
{

    Group booleanTestGroup = null;
    
    public IntakeServiceTest(String name) throws Exception
    {
        super(name, "conf/test/TurbineResourcesWithIntake.properties");

        ServiceManager serviceManager = TurbineServices.getInstance();
        IntakeService intakeService = (IntakeService) serviceManager.getService(IntakeService.SERVICE_NAME);
        booleanTestGroup = intakeService.getGroup("BooleanTest");
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
