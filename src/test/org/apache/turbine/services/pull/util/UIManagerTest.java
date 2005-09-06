package org.apache.turbine.services.pull.util;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.turbine.services.pull.PullService;
import org.apache.turbine.services.pull.TurbinePull;
import org.apache.turbine.test.BaseTurbineTest;
import org.apache.velocity.context.Context;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

public class UIManagerTest
        extends BaseTurbineTest
{
    public UIManagerTest(String name)
            throws Exception
    {
        super(name, "conf/test/TurbineResources.properties");
    }

    public static Test suite()
    {
        return new TestSuite(UIManagerTest.class);
    }

    private UIManager getTool()
    {
        PullService pullService = TurbinePull.getService();
        assertNotNull(pullService);

        Context globalContext = pullService.getGlobalContext();
        assertNotNull(globalContext);

        return (UIManager) globalContext.get("ui");
    }

    public void testTool()
    {
        UIManager ui = getTool();
        assertNotNull(ui);
    }

    public void testCssSlashes()
    {
        UIManager ui = getTool();

        String cssUrl = ui.getStylecss();
        assertEquals("CSS URL does not match", "http:///turbine-resources/turbine-skins/myskin/skins.css", cssUrl);
    }

    public void testImageSlashes()
    {
        UIManager ui = getTool();

        String img = "myimage.gif";

        String imgUrl = ui.image(img);
        assertEquals("CSS URL does not match", "http:///turbine-resources/turbine-skins/myskin/turbine-images/" + img, imgUrl);

        String img2 = "foo/myimage.gif";

        String imgUrl2 = ui.image(img2);
        assertEquals("CSS URL does not match", "http:///turbine-resources/turbine-skins/myskin/turbine-images/" + img2, imgUrl2);

        String img3 = "/foo/myimage.gif";

        String imgUrl3 = ui.image(img3);
        assertEquals("CSS URL does not match", "http:///turbine-resources/turbine-skins/myskin/turbine-images" + img3, imgUrl3);
    }

    public void testPathologicalCases()
    {
    	UIManager ui = getTool();

    	String img = "";
        String imgUrl = ui.image(img);
        assertEquals("Could not strip empty String", "http:///turbine-resources/turbine-skins/myskin/turbine-images/", imgUrl);

    	img = "/";
        imgUrl = ui.image(img);
        assertEquals("Could not strip single Slash", "http:///turbine-resources/turbine-skins/myskin/turbine-images/", imgUrl);

    	img = "//";
        imgUrl = ui.image(img);
        assertEquals("Could not strip double Slash", "http:///turbine-resources/turbine-skins/myskin/turbine-images/", imgUrl);
    }
}
