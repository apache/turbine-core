package org.apache.turbine.test;


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


import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;

/**
 * Implementation of the test component.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class TestComponentImpl
        extends AbstractLogEnabled
        implements Initializable, Disposable, TestComponent, Contextualizable
{
    private String appRoot;

    public void initialize() throws Exception
    {
    }

    public void dispose()
    {
    }

    public void test()
    {
        setupLogger(this, "TestComponent");
        getLogger().debug("test");
        getLogger().debug("componentAppRoot = "+appRoot);
    }

    public void contextualize(Context context) throws ContextException
    {
        appRoot = (String) context.get("componentAppRoot");
    }
}
