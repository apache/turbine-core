package org.apache.turbine.services.avaloncomponent;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.mimetype.MimeTypeService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;

import tutorial.HelloComponent;




/**
 * Simple test to make sure that the AvalonComponentService can be initialized.
 * 
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class MerlinComponentServiceTest extends BaseTestCase
{
	private static final Log log = LogFactory.getLog(MerlinComponentServiceTest.class);

	public MerlinComponentServiceTest(String name) throws Exception
	{
		super(name);
	}
	private MerlinComponentService getService()
	{
		return (MerlinComponentService) TurbineServices.getInstance().getService(
			MerlinComponentService.SERVICE_NAME);
	}

	/**
	 * Test that we successfully download and install the HelloComponent form the 
	 * Merlin tutorial.  Currently pulling from http://jakarta.apache.org/turbine/repo/merlin
	 * @throws Exception
	 */
	public void testMerlinWithMimetype() throws Exception
	{

		TurbineConfig tc =
			new TurbineConfig(".", "/conf/test/MerlinComponentServiceTest.properties");

		try
		{
			tc.initialize();

			MimeTypeService mimetypeservice = (MimeTypeService) getService().lookup("/fulcrum/mimetype"); // just to test
			assertNotNull(mimetypeservice);
            
			getService().release(mimetypeservice);

 
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			tc.dispose();
		}
	}
    
	/**
	 * Test that a locally compiled component can be loaded and used.  Currently not
	 * working because the org.apache.turbine.test.TestComponent is in this classloader,
	 * not in a downloaded jar.  
	 * @throws Exception
	 */
	public void OFF_testMerlinWithTestComponent() throws Exception
		{

			TurbineConfig tc =
				new TurbineConfig(".", "/conf/test/MerlinComponentServiceTest.properties");

			try
			{
				tc.initialize();

				Object appliance = getService().lookup("/local/testcomponent"); // just to test
				assertNotNull(appliance);
				assertEquals("appliance:/local/testcomponent", appliance.toString());
				log.info("Looked up appliance: " + appliance.toString());
				getService().release(appliance);

 
			}
			catch (Exception e)
			{
				throw e;
			}
			finally
			{
				tc.dispose();
			}
		}
}

