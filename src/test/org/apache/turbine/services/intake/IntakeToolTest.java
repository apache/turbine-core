package org.apache.turbine.services.intake;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import java.io.File;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletResponse;

import org.apache.fulcrum.intake.IntakeService;
import org.apache.fulcrum.intake.model.Group;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.test.EnhancedMockHttpServletRequest;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;

import com.mockobjects.servlet.MockHttpServletResponse;
import com.mockobjects.servlet.MockHttpSession;
import com.mockobjects.servlet.MockServletConfig;

/**
 * Unit test for Localization Tool.  Verifies that localization works the same using the
 * deprecated Turbine localization service as well as the new Fulcrum Localization
 * component.
 *
 * @author <a href="maiintakeToolo:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class IntakeToolTest extends BaseTestCase
{
    private static TurbineConfig tc = null;
    public IntakeToolTest(String name) throws Exception
    {
        super(name);
    }
    public void testGet() throws Exception
    {
        IntakeTool intakeTool = new IntakeTool();
        intakeTool.init(getRunData());
        File file = new File("./target/appData.ser");
        assertTrue(
            "Make sure serialized data file exists:" + file,
            file.exists());
        Group group = intakeTool.get("LoginGroup","loginGroupKey");
        assertNotNull(group);
        assertEquals("loginGroupKey", group.getGID());
        assertEquals("LoginGroup", group.getIntakeGroupName());
    }

    
    /**
     * Make sure refresh DOESN'T do anything
     * @throws Exception
     */
    public void testRefresh() throws Exception
    {
        IntakeTool intakeTool = new IntakeTool();
        intakeTool.init(getRunData());
        int numberOfGroups = intakeTool.getGroups().size();
        intakeTool.refresh();
        assertEquals(numberOfGroups,intakeTool.getGroups().size());
    }
    private RunData getRunData() throws Exception
    {
        RunDataService rds =
            (RunDataService) TurbineServices.getInstance().getService(
                RunDataService.SERVICE_NAME);
        EnhancedMockHttpServletRequest request =
            new EnhancedMockHttpServletRequest();
        request.setupServerName("bob");
        request.setupGetProtocol("http");
        request.setupScheme("scheme");
        request.setupPathInfo("damn");
        request.setupGetServletPath("damn2");
        request.setupGetContextPath("wow");
        request.setupGetContentType("html/text");
        request.setupAddHeader("Content-type", "html/text");
        request.setupAddHeader("Accept-Language", "en-US");
        Vector v = new Vector();
        request.setupGetParameterNames(v.elements());
        MockHttpSession session = new MockHttpSession();
        session.setupGetAttribute(User.SESSION_KEY, null);
        request.setSession(session);
        HttpServletResponse response = new MockHttpServletResponse();
        ServletConfig config = new MockServletConfig();
        RunData runData = rds.getRunData(request, response, config);
        return runData;
    }
    
    public void setUp() throws Exception
    {
        tc = new TurbineConfig(".", "/conf/test/TestFulcrumComponents.properties");
        tc.initialize();        
        TurbineServices.getInstance().getService(IntakeService.class.getName());
    }
    
    public void tearDown() throws Exception
    {
        if (tc != null)
        {
            tc.dispose();
        }
    }
}
