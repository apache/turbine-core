package org.apache.turbine.services.avaloncomponent;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.TestComponent;

import junit.framework.TestCase;

/**
 * Simple test to make sure that the AvalonComponentService can be initialized.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class TurbineAvalonComponentServiceTest extends TestCase
{
    private static final String PREFIX = "services." +
            AvalonComponentService.SERVICE_NAME + '.';

    /**
     * Initialize the unit test.  The AvalonComponentService will be configured
     * and initialized.
     *
     * @param name
     */
    public TurbineAvalonComponentServiceTest(String name)
    {
        super(name);
        ServiceManager serviceManager = TurbineServices.getInstance();
        serviceManager.setApplicationRoot(".");

        Configuration cfg = new BaseConfiguration();
        cfg.setProperty(PREFIX + "classname",
                TurbineAvalonComponentService.class.getName());

        // we want to configure the service to load test TEST configuration files
        cfg.setProperty(PREFIX + "componentConfiguration",
                "src/test/componentConfiguration.xml");
        cfg.setProperty(PREFIX + "componentRoles",
                "src/test/ComponentRoles.xml");
        serviceManager.setConfiguration(cfg);

        try
        {
            serviceManager.init();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Use the service to get an instance of the TestComponent.  The test() method will be called to
     * simply write a log message.  The component will then be released.
     */
    public void testGetAndUseTestComponent()
    {
        try
        {
            AvalonComponentService cs = (AvalonComponentService)
                    TurbineServices.getInstance().getService(AvalonComponentService.SERVICE_NAME);

            TestComponent tc = (TestComponent) cs.lookup(TestComponent.ROLE);
            tc.test();
            cs.release(tc);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
