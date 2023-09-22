package org.apache.turbine.services.localization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

import static org.mockito.Mockito.mock;

import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.fulcrum.security.model.turbine.entity.TurbineUser;
import org.apache.fulcrum.testcontainer.BaseUnit5Test;
import org.apache.turbine.annotation.AnnotationProcessor;
import org.apache.turbine.annotation.TurbineTool;
import org.apache.turbine.om.security.DefaultUserImpl;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test for Localization Tool. Verifies that localization works the same using the
 * deprecated Turbine localization service as well as the new Fulcrum Localization
 * component.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class RundataLocalizationToolTest extends BaseUnit5Test
{
    private static TurbineConfig tc = null;
    
    @TurbineTool(LocalizationTool.class)
    private LocalizationTool lt;
    
    @BeforeAll
    public static void setUp() throws Exception
    {
        tc = new TurbineConfig(".", "/conf/test/rundataTemplateService.properties");
        tc.initialize();
        
    }

    @BeforeEach
    public void initTool() throws Exception
    {
        AnnotationProcessor.process(this);   
        lt.init(getRunData());
    }

    @Test
    public void testGet() throws Exception
    {
        assertEquals("wert1", lt.get("key1"));
        assertEquals("wert3", lt.get("key3"));
    }

    @Test
    public void testGetLocale() throws Exception
    {
        assertNotNull(lt.getLocale());
        assertEquals("DE", lt.getLocale().getCountry());
        assertEquals("de", lt.getLocale().getLanguage());
    }

    @Test
    public void testInit() throws Exception
    {
        assertNotNull(lt.getLocale());
    }

    private RunData getRunData() throws Exception
    {
        RunDataService rds = (RunDataService) TurbineServices.getInstance().getService(RunDataService.SERVICE_NAME);
        ServletConfig config = mock(ServletConfig.class);
        HttpServletRequest request = getMockRequest();
        HttpServletResponse response = mock(HttpServletResponse.class);
        RunData runData = rds.getRunData(request, response, config);
        
        User user = null;
        try {
            user = new DefaultUserImpl(mock(TurbineUser.class));
            user.setTemp("locale", new Locale("de","DE") );
            runData.setUser(user);            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return runData;
    }

    @AfterAll
    public static void destroy() {
        tc.dispose();
    }
}
