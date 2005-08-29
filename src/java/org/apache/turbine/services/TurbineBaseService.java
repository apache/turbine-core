package org.apache.turbine.services;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import javax.servlet.ServletConfig;

import org.apache.turbine.util.RunData;

/**
 * <p>This class provides a <code>Service</code> implementation that
 * Services used in Turbine are required to extend.  The
 * functionality provided in addition to <code>BaseService</code>
 * functionality is recognizing objects used in early initialization
 * of <code>Services</code> in Turbine, and passing them to
 * appropriate convenience methods.  These methods should be overriden
 * to provide desired initialization functionality.</p>
 *
 * <p><strong>Note!</strong><br>Remember to call
 * <code>setInit(true)</code> after successful initialization.</p>
 *
 * <p><strong>Note!</strong><br>If you need to use another
 * <code>Service</code> inside your early initialization, remember to
 * request initialization of that <code>Service</code> before using
 * it:</p>
 *
 * <pre><code>
 * getServiceBroker().initClass("OtherService",data);
 * OtherService service =
 *         (OtherService)getServiceBroker().getService("OtherService");
 * </code></pre>
 *
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:burton@apache.org">Kevin Burton</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class TurbineBaseService
        extends BaseService
{
    /**
     * Performs early initialization.  Overrides init() method in
     * BaseService to detect objects used in Turbine's Service
     * initialization and pass them to apropriate init() methods.
     *
     * @param data An Object to use for initialization activities.
     * @exception InitializationException if initialization of this
     * class was not successful.
     */
    public void init(Object data)
            throws InitializationException
    {
        if (data instanceof ServletConfig)
        {
            init((ServletConfig) data);
        }
        else if (data instanceof RunData)
        {
            init((RunData) data);
        }
    }

    /**
     * Performs early initialization.
     *
     * @param config A ServletConfing to use for initialization
     * activities.
     * @exception InitializationException if initialization of this
     * class was not successful.
     * @deprecated Use init() instead
     */
    public void init(ServletConfig config) throws InitializationException
    {
    }

    /**
     * Performs early initialization.
     *
     * @param data An RunData to use for initialization activities.
     * @exception InitializationException if initialization of this
     * class was not successful.
     */
    public void init(RunData data) throws InitializationException
    {
    }

    /**
     * Performs late initialization.
     *
     * If your class relies on early initialization, and the object it
     * expects was not received, you can use late initialization to
     * throw an exception and complain.
     *
     * @exception InitializationException, if initialization of this
     * class was not successful.
     */
    public void init() throws InitializationException
    {
        setInit(true);
    }

    /**
     * Returns to uninitialized state.
     *
     * You can use this method to release resources thet your Service
     * allocated when Turbine shuts down.
     */
    public void shutdown()
    {
        setInit(false);
    }
}
