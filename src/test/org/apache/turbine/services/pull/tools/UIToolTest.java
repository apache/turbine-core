package org.apache.turbine.services.pull.tools;

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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.jsonrpc.JsonrpcServicelTest;
import org.apache.turbine.services.pull.PullService;
import org.apache.turbine.services.pull.TurbinePull;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
import org.apache.velocity.context.Context;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    JsonrpcServicelTest.class
    })
public class UIToolTest
        extends BaseTestCase
{
    
    private static TurbineConfig turbineConfig = null;
    private static PullService pullService = null;
    private static UITool ui = null;

    
    @BeforeClass
    public static void setUp() throws Exception {


        Map<String, String> initParams = new HashMap<String, String>();
        initParams.put(TurbineConfig.PROPERTIES_PATH_KEY, "/conf/test/CompleteTurbineResources.properties"); // "conf/test/TurbineResources.properties"
        initParams.put(TurbineConstants.LOGGING_ROOT_KEY, "target/test-logs");

        turbineConfig = new TurbineConfig(".", initParams);
        turbineConfig.initialize();
        
        //pullService = (PullService)TurbineServices.getInstance().getService(PullService.SERVICE_NAME);
        pullService = TurbinePull.getService();
        assertNotNull(pullService);
        Context globalContext = pullService.getGlobalContext();
        assertNotNull(globalContext);

        ui = (UITool) globalContext.get("ui");
    }
    
    @AfterClass
    public static void destroy() throws Exception {
        turbineConfig.dispose();
        ui=null;
    }

    @Test
    public void testTool()
    {
        assertNotNull(ui);
    }
    
    @Test
    public void testCssSlashes()
    {

        ui.setSkin("myskin");

        String cssUrl = ui.getStylecss();
        assertEquals("CSS URL does not match", "http:///conf/test/turbine-resources/turbine-skins/myskin/skins.css", cssUrl);
    }
    
    @Test
    public void testImageSlashes()
    {
        ui.setSkin("myskin");

        String img = "myimage.gif";

        String imgUrl = ui.image(img);
        assertEquals("CSS URL does not match", "http:///conf/test/turbine-resources/turbine-skins/myskin/turbine-images/" + img, imgUrl);

        String img2 = "foo/myimage.gif";

        String imgUrl2 = ui.image(img2);
        assertEquals("CSS URL does not match", "http:///conf/test/turbine-resources/turbine-skins/myskin/turbine-images/" + img2, imgUrl2);

        String img3 = "/foo/myimage.gif";

        String imgUrl3 = ui.image(img3);
        assertEquals("CSS URL does not match", "http:///conf/test/turbine-resources/turbine-skins/myskin/turbine-images" + img3, imgUrl3);
    }
    @Test
    public void testPathologicalCases()
    {
        ui.setSkin("myskin");

        String img = "";
        String imgUrl = ui.image(img);
        assertEquals("Could not strip empty String", "http:///conf/test/turbine-resources/turbine-skins/myskin/turbine-images/", imgUrl);

        img = "/";
        imgUrl = ui.image(img);
        assertEquals("Could not strip single Slash", "http:///conf/test/turbine-resources/turbine-skins/myskin/turbine-images/", imgUrl);

        img = "//";
        imgUrl = ui.image(img);
        assertEquals("Could not strip double Slash", "http:///conf/test/turbine-resources/turbine-skins/myskin/turbine-images/", imgUrl);
    }
    @Test
    public void testGetSkinNames()
    {

        String[] skinNames = ui.getSkinNames();
        // Remove the ".svn" dir that may be present.
        skinNames = (String[]) ArrayUtils.removeElement(skinNames, ".svn");
        assertEquals(2, skinNames.length);

        assertTrue(ArrayUtils.contains(skinNames, "myotherskin"));
        assertTrue(ArrayUtils.contains(skinNames, "myskin"));
    }
    @Test
    public void testSkinValues()
    {

        // Default skin
        //skin_property_1 = skin_property_1_my_skin
        assertEquals("skin_property_1_my_skin", ui.get("skin_property_1"));
        
        ui.setSkin("myotherskin");
        //skin_property_1 = skin_property_1_my_other_skin
        assertEquals("skin_property_1_my_other_skin", ui.get("skin_property_1"));
    }
}
