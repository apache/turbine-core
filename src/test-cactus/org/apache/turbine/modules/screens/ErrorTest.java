package org.apache.turbine.modules.screens;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.turbine.Turbine;
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.RunDataFactory;
import org.apache.cactus.*;

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
	 *  Start the tests.
	 *
	 *@param  args  the arguments. Not used
	 */
	public static void main(String args[])
	{
		junit.awtui.TestRunner.main(new String[] { ErrorTest.class.getName()});
	}

	/**
	 *  Creates the test suite.
	 *
	 *@return    a test suite (<code>TestSuite</code>) that includes all methods
	 *      starting with "test"
	 */
	public static Test suite()
	{
		// All methods starting with "test" will be executed in the test suite.
		return new TestSuite(ErrorTest.class);
	}

	protected void setUp() throws Exception
	{
		super.setUp();

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

		data = RunDataFactory.getRunData(request, response, config);

		errorScreen =
			(org.apache.turbine.modules.screens.Error) ScreenLoader
				.getInstance()
				.getInstance("Error");
		data.getParameters ().setString ( "param", "param1Value" );
		errorScreen.doBuild(data);
		
		assertTrue("Make sure we have our error parameter.",data.getPage().toString().indexOf("param1Value")>-1);

	}

}
