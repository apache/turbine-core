package org.apache.turbine.util;

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

import java.util.HashMap;
import java.util.Map;

/**
 * A class used for initialization of Turbine without a servlet container.
 * <p>
 * If you need to use Turbine outside of a servlet container, you can
 * use this class for initialization of the Turbine servlet.
 * <p>
 * <blockquote><code><pre>
 * TurbineXmlConfig config = new TurbineXmlConfig(".", "conf/TurbineResources.properties");
 * </pre></code></blockquote>
 * <p>
 * All paths referenced in TurbineResources.properties and the path to
 * the properties file itself (the second argument) will be resolved
 * relative to the directory given as the first argument of the constructor,
 * here - the directory where application was started. Don't worry about
 * discarding the references to objects created above. They are not needed,
 * once everything is initialized.
 * <p>
 * In order to initialize the Services Framework outside of the Turbine Servlet,
 * you need to call the <code>init()</code> method. By default, this will
 * initialize the Resource and Logging Services and any other services you
 * have defined in your TurbineResources.properties file.
 *
 * @todo Make this class enforce the lifecycle contracts
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class TurbineXmlConfig
        extends TurbineConfig
{
    /**
     * Constructs a new TurbineXmlConfig.
     *
     * This is the general form of the constructor. You can provide
     * a path to search for files, and a name-value map of init
     * parameters.
     *
     * <p> For the list of recognized init parameters, see
     * {@link org.apache.turbine.Turbine} class.
     *
     * @param path The web application root (i.e. the path for file lookup).
     * @param attributes Servlet container (or emulator) attributes.
     * @param initParams initialization parameters.
     */
    public TurbineXmlConfig(String path, Map attributes, Map initParams)
    {
        super(path, attributes, initParams);
    }

    /**
     * @see #TurbineXmlConfig(String path, Map attributes, Map initParams)
     */
    public TurbineXmlConfig(String path, Map initParams)
    {
        this(path, new HashMap(0), initParams);
    }

    /**
     * Constructs a TurbineXmlConfig.
     *
     * This is a specialized constructor that allows to configure
     * Turbine easiliy in the common setups.
     *
     * @param path The web application root (i.e. the path for file lookup).
     * @param configuration the relative path to TurbineResources.xml file
     */
    public TurbineXmlConfig(String path, String config)
    {
        this(path, new HashMap(1));
        initParams.put(CONFIGURATION_PATH_KEY, config);
    }
}
