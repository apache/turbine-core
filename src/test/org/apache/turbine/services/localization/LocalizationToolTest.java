package org.apache.turbine.services.localization;
/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
import java.util.Vector;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletResponse;
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
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class LocalizationToolTest extends BaseTestCase
{
    private static TurbineConfig tc = null;
    public LocalizationToolTest(String name) throws Exception
    {
        super(name);
    }
    public void testGet() throws Exception
    {
        LocalizationTool lt = new LocalizationTool();
        lt.init(getRunData());
        assertEquals("value1", lt.get("key1"));
        assertEquals("value3", lt.get("key3"));
    }
    public void testGetLocale() throws Exception
    {
        LocalizationTool lt = new LocalizationTool();
        lt.init(getRunData());
        assertNotNull(lt.getLocale());
        assertEquals("US", lt.getLocale().getCountry());
        assertEquals("en", lt.getLocale().getLanguage());
    }
    public void testInit() throws Exception
    {
        LocalizationTool lt = new LocalizationTool();
        lt.init(getRunData());
        assertNotNull(lt.getLocale());
    }
    public void testRefresh() throws Exception
    {
        LocalizationTool lt = new LocalizationTool();
        lt.init(getRunData());
        assertNotNull(lt.getLocale());
        lt.refresh();
        assertNull(lt.getLocale());
    }
    private RunData getRunData() throws Exception
    {
        RunDataService rds = (RunDataService) TurbineServices.getInstance().getService(RunDataService.SERVICE_NAME);
        EnhancedMockHttpServletRequest request = new EnhancedMockHttpServletRequest();
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
    }
    public void tearDown() throws Exception
    {
        if (tc != null)
        {
            tc.dispose();
        }
    }
}
