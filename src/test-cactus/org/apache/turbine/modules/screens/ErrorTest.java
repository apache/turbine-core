package org.apache.turbine.modules.screens;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.cactus.ServletTestCase;
import org.apache.turbine.Turbine;
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.util.RunData;

/**
 *  ErrorTest
 *
 *@author     <a href="epugh@upstate.com">Eric Pugh</a>
 *@created    December 10, 2002
 *@version    $Id: ErrorTest.java,v 1.3 2002/11/08
 *      10:04:13 raphael Exp $
 */

public class ErrorTest extends ServletTestCase
{
	private RunData data = null;
	private org.apache.turbine.modules.screens.Error errorScreen = null;
	private Turbine turbine = null;
	
	/**
	 *  Defines the testcase name for JUnit.
	 *
	 *@param  name  the testcase's name.
	 */
	public ErrorTest(String name)
	{
		super(name);
	}

	/**
	* After each testXXX test runs, shut down the Turbine servlet.
	*/
	protected void tearDown() throws Exception
	{
		turbine.destroy();
		super.tearDown();
	}

	/**
	   * Tests if we can call the doBuild method
	   *
	   *@todo Move the turbine setup stuff into the setUp() method.
	   */
	public void testDobuild() throws Exception
	{

		config.setInitParameter(
			"properties",
			"/WEB-INF/conf/TurbineCacheTest.properties");
		turbine = new Turbine();
		turbine.init(config);

		data = ((RunDataService) TurbineServices.getInstance().getService(RunDataService.SERVICE_NAME)).getRunData(request, response, config);

		errorScreen =
			(org.apache.turbine.modules.screens.Error) ScreenLoader
				.getInstance()
				.getInstance("Error");
		data.getParameters ().setString ( "param", "param1Value" );
		errorScreen.doBuild(data);
		
		assertTrue("Make sure we have our error parameter.",data.getPage().toString().indexOf("param1Value")>-1);

	}

}
