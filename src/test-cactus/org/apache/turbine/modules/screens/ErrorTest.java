package org.apache.turbine.modules.screens;

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
 *@version    $Id$
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
