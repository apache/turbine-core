package org.apache.turbine.modules;
import java.util.Vector;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletResponse;
import org.apache.turbine.modules.ActionLoader;
import org.apache.turbine.modules.PageLoader;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.test.EnhancedMockHttpServletRequest;
import org.apache.turbine.test.EnhancedMockHttpSession;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import com.mockobjects.servlet.MockHttpServletResponse;
import com.mockobjects.servlet.MockServletConfig;
/**
 * This test case is to verify whether Exceptions in Velocity actions are 
 * properly bubbled up.
 * 
 * @author     <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 */
public class ActionLoaderTest extends BaseTestCase
{
    private static TurbineConfig tc = null;
    private static TemplateService ts = null;
    private MockServletConfig config = null;
    private EnhancedMockHttpServletRequest request = null;
    private EnhancedMockHttpSession session = null;
    private HttpServletResponse response = null;
    private static ServletConfig sc = null;
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        config = new MockServletConfig();
        config.setupNoParameters();
        request = new EnhancedMockHttpServletRequest();
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
        session = new EnhancedMockHttpSession();
        response = new MockHttpServletResponse();
        session.setupGetAttribute(User.SESSION_KEY, null);
        request.setSession(session);
        sc = config;
        tc = new TurbineConfig(".", "/conf/test/CompleteTurbineResources.properties");
        tc.initialize();
    }
    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        if (tc != null)
        {
            tc.dispose();
        }
    }
    /**
     * Constructor for VelocityErrorScreenTest.
     * @param arg0
     */
    public ActionLoaderTest(String arg0) throws Exception
    {
        super(arg0);
    }
    /**
     * This unit test verifies that if your standard doPerform is called, 
     * and it throws an Exception, the exception is bubbled up out of the ActionLoader...
     * 
     * @throws Exception If something goes wrong with the unit test
     */
    public void testDoPerformBubblesException() throws Exception
    {
        RunData data = getRunData();
        data.setAction("VelocityActionThrowsException");
        try
        {
            String defaultPage = "VelocityPage";
            ActionLoader.getInstance().exec(data, data.getAction());
            fail("Should have thrown an exception");
        }
        catch (Exception e)
        {
            //good
        }
    }
    /**
       * This unit test verifies that if an Action Event doEventSubmit_ is called, and it throws an Exception, the
       * exception is bubbled up out of the ActionLoader...
       * 
       * @throws Exception If something goes wrong with the unit test
       */
    public void testActionEventBubblesException() throws Exception
    {
        // can't seem to figure out how to setup the Mock Request with the right parameters...
        request.setupAddParameter("eventSubmit_doCauseexception", "foo");
        RunData data = getRunData();
        data.getParameters().add("eventSubmit_doCauseexception", "foo");
        assertTrue(data.getParameters().containsKey("eventSubmit_doCauseexception"));
        data.setAction("VelocityActionThrowsException");
        try
        {
            String defaultPage = "VelocityPage";
            ActionLoader.getInstance().exec(data, data.getAction());
            fail("Should have bubbled out an exception thrown by the action.");
        }
        catch (Exception e)
        {
            //good
        }
    }
    public void testNonexistentActionCausesError() throws Exception
    {
        RunData data = getRunData();
        data.setAction("ImaginaryAction");
        try
        {
            PageLoader.getInstance().exec(data, "boo");
            fail("Should have thrown an exception");
        }
        catch (Exception e)
        {
            //good
        }
    }
    private RunData getRunData() throws Exception
    {
        RunDataService rds = (RunDataService) TurbineServices.getInstance().getService(RunDataService.SERVICE_NAME);
        RunData runData = rds.getRunData(request, response, config);
        return runData;
    }
}
