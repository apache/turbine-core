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

import org.apache.turbine.test.BaseTurbineTest;

/**
 * Start and Stop Turbine.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class TestTurbine
        extends BaseTurbineTest
{
    public TestTurbine(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(TestTurbine.class);
    }

    public void testTurbine()
    {
    }
}

