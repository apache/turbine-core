package org.apache.turbine;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.turbine.Turbine;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;

/**
 * Can we call "destroy" unconditionally on our Turbine Servlet, even if
 * it hasn't configured?
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class DestroyTest
    extends BaseTestCase
{
    private static TurbineConfig tc = null;

    public DestroyTest(String name)
            throws Exception
    {
        super(name);
        tc = new TurbineConfig(".", "/conf/test/TemplateService.properties");
    }

    public static Test suite()
    {
        return new TestSuite(DestroyTest.class);
    }

    public void testDestroy()
        throws Exception
    {
        Turbine t = new Turbine();
        t.destroy();
    }

    public void testInitAndDestroy()
        throws Exception
    {
        tc.initialize();
        tc.dispose();
    }
}
