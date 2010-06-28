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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.lang.ArrayUtils;
import org.apache.turbine.services.pull.PullService;
import org.apache.turbine.services.pull.TurbinePull;
import org.apache.turbine.test.BaseTurbineTest;
import org.apache.velocity.context.Context;


public class UIToolTest
        extends BaseTurbineTest
{
    public UIToolTest(String name)
            throws Exception
    {
        super(name, "conf/test/TurbineResources.properties");
    }

    public static Test suite()
    {
        return new TestSuite(UIToolTest.class);
    }

    private UITool getTool()
    {
        PullService pullService = TurbinePull.getService();
        assertNotNull(pullService);

        Context globalContext = pullService.getGlobalContext();
        assertNotNull(globalContext);

        return (UITool) globalContext.get("ui");
    }

    public void testTool()
    {
        UITool ui = getTool();
        assertNotNull(ui);
    }

    public void testCssSlashes()
    {
        UITool ui = getTool();

        String cssUrl = ui.getStylecss();
        assertEquals("CSS URL does not match", "http:///conf/test/turbine-resources/turbine-skins/myskin/skins.css", cssUrl);
    }

    public void testImageSlashes()
    {
        UITool ui = getTool();

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

    public void testPathologicalCases()
    {
    	UITool ui = getTool();

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

    public void testGetSkinNames()
    {
        UITool ui = getTool();

        String[] skinNames = ui.getSkinNames();
        // Remove the ".svn" dir that may be present.
        skinNames = (String[]) ArrayUtils.removeElement(skinNames, ".svn");
        assertEquals(2, skinNames.length);

        assertTrue(ArrayUtils.contains(skinNames, "myotherskin"));
        assertTrue(ArrayUtils.contains(skinNames, "myskin"));
    }

    public void testSkinValues()
    {
        UITool ui = getTool();

        // Default skin
        //skin_property_1 = skin_property_1_my_skin
        assertEquals("skin_property_1_my_skin", ui.get("skin_property_1"));
        
        ui.setSkin("myotherskin");
        //skin_property_1 = skin_property_1_my_other_skin
        assertEquals("skin_property_1_my_other_skin", ui.get("skin_property_1"));
    }
}
