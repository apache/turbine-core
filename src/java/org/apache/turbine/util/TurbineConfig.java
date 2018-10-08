package org.apache.turbine.util;


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


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.annotation.TurbineConfiguration;

/**
 * A class used for initialization of Turbine without a servlet container.
 * <p>
 * If you need to use Turbine outside of a servlet container, you can
 * use this class for initialization of the Turbine servlet.
 * <p>
 * <pre>
 * TurbineConfig config = new TurbineConfig(".", "conf/TurbineResources.properties");
 * </pre>
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
 * TODO Make this class enforce the lifecycle contracts
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class TurbineConfig
        implements ServletConfig, ServletContext, Initializable, Disposable
{
	
    @TurbineConfiguration( TurbineConstants.SESSION_TIMEOUT_KEY )
    protected int timeout = TurbineConstants.SESSION_TIMEOUT_DEFAULT;

    /**
     * Servlet initialization parameter name for the path to
     * TurbineConfiguration.xml file used by Turbine
     */
    public static final String CONFIGURATION_PATH_KEY = "configuration";

    /**
     * Servlet initialization parameter name for the path to
     * Turbine.properties file used by Turbine
     */
    public static final String PROPERTIES_PATH_KEY = "properties";

    /**
     * Default value of TurbineResources.properties file path
     * (<code>/WEB-INF/conf/TurbineResources.properties</code>).
     */
    public static final String PROPERTIES_PATH_DEFAULT =
            "/WEB-INF/conf/TurbineResources.properties";

    /** Filenames are looked up in this directory. */
    protected File root;

    /** Servlet container (or emulator) attributes. */
    protected Map<String, Object> attributes;

    /** Turbine servlet initialization parameters. */
    protected Map<String, String> initParams;

    /** The Turbine servlet instance used for initialization. */
    private Turbine turbine;

    /** Logging */
    private final Log log = LogFactory.getLog(this.getClass());

    /**
     * Constructs a new TurbineConfig.
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
    public TurbineConfig(String path, Map<String, Object> attributes,
            Map<String, String> initParams)
    {
        root = new File(path);
        this.attributes = attributes;
        this.initParams = initParams;
    }

    /**
     * Constructs a new TurbineConfig.
     *
     * This is the general form of the constructor. You can provide
     * a path to search for files, and a name-value map of init
     * parameters.
     *
     * <p> For the list of recognized init parameters, see
     * {@link org.apache.turbine.Turbine} class.
     *
     * @param path The web application root (i.e. the path for file lookup).
     * @param initParams initialization parameters.
     */
    public TurbineConfig(String path, Map<String, String> initParams)
    {
        this(path, new HashMap<String, Object>(0), initParams);
    }

    /**
     * Constructs a TurbineConfig.
     *
     * This is a specialized constructor that allows to configure
     * Turbine easily in the common setups.
     *
     * @param path The web application root (i.e. the path for file lookup).
     * @param properties the relative path to TurbineResources.properties file
     */
    public TurbineConfig(String path, String properties)
    {
        this(path, new HashMap<String, String>(1));
        initParams.put(PROPERTIES_PATH_KEY, properties);
    }

    /**
     * Causes this class to initialize itself which in turn initializes
     * all of the Turbine Services that need to be initialized.
     */
    @Override
    public void initialize()
    {
        try
        {
            turbine = new Turbine();
            turbine.init(this);
        }
        catch (Exception e)
        {
            log.error("TurbineConfig: Initialization failed", e);
        }
    }

    /**
     * Initialization requiring a HTTP <code>GET</code> request.
     * @param data the Turbine request
     */
    public void init(RunData data)
    {
        if (turbine != null)
        {
            turbine.init(data);
        }
    }

    /**
     * Shutdown the Turbine System, lifecycle style
     *
     */
    @Override
    public void dispose()
    {
        if (turbine != null)
        {
            turbine.destroy();
        }
    }

    /**
     * Returns a reference to the Turbine servlet that was initialized.
     *
     * @return a ServletContext reference
     */
    public Turbine getTurbine()
    {
        return turbine;
    }

    /**
     * Returns a reference to the object cast onto ServletContext type.
     *
     * @return a ServletContext reference
     */
    @Override
    public ServletContext getServletContext()
    {
        return this;
    }

    /**
     * Translates a path relative to the web application root into an
     * absolute path.
     *
     * @param path A path relative to the web application root.
     * @return An absolute version of the supplied path, or <code>null</code>
     * if the translated path doesn't map to a file or directory.
     */
    @Override
    public String getRealPath(String path)
    {
        String result = null;
        File f = new File(root, path);

        if (log.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder();

            sb.append("TurbineConfig.getRealPath: path '");
            sb.append(path);
            sb.append("' translated to '");
            sb.append(f.getPath());
            sb.append("' ");
            sb.append(f.exists() ? "" : "not ");
            sb.append("found");
            log.debug(sb.toString());
        }

        if (f.exists())
        {
          result = f.getPath();
        }
        else
        {
            log.error("getRealPath(\"" + path + "\") is undefined, returning null");
        }

        return result;
    }

    /**
     * Retrieves an initialization parameter.
     *
     * @param name the name of the parameter.
     * @return the value of the parameter.
     */
    @Override
    public String getInitParameter(String name)
    {
        return initParams.get(name);
    }

    /**
     * Retrieves an Enumeration of initialization parameter names.
     *
     * @return an Enumeration of initialization parameter names.
     */
    @Override
    public Enumeration<String> getInitParameterNames()
    {
        return new Vector<String>(initParams.keySet()).elements();
    }

    /**
     * Returns the servlet name.
     *
     * Fixed value "Turbine" is returned.
     *
     * @return the servlet name.
     */
    @Override
    public String getServletName()
    {
        return "Turbine";
    }

    /**
     * Returns the context name.
     *
     * Fixed value "Turbine" is returned
     *
     * @return the context name
     */
    @Override
    public String getServletContextName()
    {
        return "Turbine";
    }

    /**
     * Returns the context path.
     *
     * Fixed value "/turbine" is returned
     *
     * @return the context path
     */
    @Override
    public String getContextPath()
    {
        return "/turbine";
	}

	/**
     * Returns a URL to the resource that is mapped to a specified
     * path. The path must begin with a "/" and is interpreted
     * as relative to the current context root.
     *
     * @param s the path to the resource
     * @return a URL pointing to the resource
     * @throws MalformedURLException
     */
    @Override
    public URL getResource(String s)
            throws MalformedURLException
    {
        return new URL("file://" + getRealPath(s));
    }

    /**
     * Returns the resource located at the named path as
     * an <code>InputStream</code> object.
     *
     * @param s the path to the resource
     * @return an InputStream object from which the resource can be read
     */
    @Override
    public InputStream getResourceAsStream(String s)
    {
        try
        {
            FileInputStream fis = new FileInputStream(getRealPath(s));
            return new BufferedInputStream(fis);
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
    }

    /**
     * Logs an error message.
     *
     * @param e an Exception.
     * @param m a message.
     * @deprecated use log(String,Throwable) instead
     */
    @Override
    @Deprecated
    public void log(Exception e, String m)
    {
        log.info(m, e);
    }

    /**
     * Logs a message.
     *
     * @param m a message.
     */
    @Override
    public void log(String m)
    {
        log.info(m);
    }

    /**
     * Logs an error message.
     *
     * @param t a Throwable object.
     * @param m a message.
     */
    @Override
    public void log(String m, Throwable t)
    {
        log.info(m, t);
    }

    /**
     * Returns the servlet container attribute with the given name, or
     * null if there is no attribute by that name.
     */
    @Override
    public Object getAttribute(String s)
    {
        return attributes.get(s);
    }

    /**
     * Returns an Enumeration containing the attribute names available
     * within this servlet context.
     */
    @Override
    public Enumeration<String> getAttributeNames()
    {
        return new Vector<String>(attributes.keySet()).elements();
    }

    // Unimplemented methods follow

    /**
     * Not implemented.
     *
     * A method in ServletConfig or ServletContext interface that is not
     * implemented and will throw <code>UnsuportedOperationException</code>
     * upon invocation
     */
    @Override
    public ServletContext getContext(String s)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletConfig or ServletContext interface that is not
     * implemented and will throw <code>UnsuportedOperationException</code>
     * upon invocation
     */
    @Override
    public int getMajorVersion()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletConfig or ServletContext interface that is not
     * implemented and will throw <code>UnsuportedOperationException</code>
     * upon invocation
     */
    @Override
    public String getMimeType(String s)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletConfig or ServletContext interface that is not
     * implemented and will throw <code>UnsuportedOperationException</code>
     * upon invocation
     */
    @Override
    public int getMinorVersion()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletConfig or ServletContext interface that is not
     * implemented and will throw <code>UnsuportedOperationException</code>
     * upon invocation
     */
    @Override
    public RequestDispatcher getNamedDispatcher(String s)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletConfig or ServletContext interface that is not
     * implemented and will throw <code>UnsuportedOperationException</code>
     * upon invocation
     */
    @Override
    public RequestDispatcher getRequestDispatcher(String s)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext (2.3) interface that is not implemented and
     * will throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public Set<String> getResourcePaths(String s)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext (2.3) interface that is not implemented and
     * will throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public String getServerInfo()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     * @deprecated As of Java Servlet API 2.1, with no direct replacement.
     */
    @Override
    @Deprecated
    public Servlet getServlet(String s)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     * @deprecated As of Java Servlet API 2.1, with no replacement.
     */
    @Override
    @Deprecated
    public Enumeration<String> getServletNames()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     * @deprecated As of Java Servlet API 2.0, with no replacement.
     */
    @Override
    @Deprecated
    public Enumeration<Servlet> getServlets()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public void removeAttribute(String s)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public void setAttribute(String s, Object o)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public int getEffectiveMajorVersion()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public int getEffectiveMinorVersion()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public boolean setInitParameter(String name, String value)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public Dynamic addServlet(String servletName, String className)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public Dynamic addServlet(String servletName, Servlet servlet)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public ServletRegistration getServletRegistration(String servletName)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public FilterRegistration getFilterRegistration(String filterName)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public SessionCookieConfig getSessionCookieConfig()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public void addListener(String className)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public <T extends EventListener> void addListener(T t)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public void addListener(Class<? extends EventListener> listenerClass)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public JspConfigDescriptor getJspConfigDescriptor()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public ClassLoader getClassLoader()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public void declareRoles(String... roleNames)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
    @Override
    public String getVirtualServerName()
    {
        throw new UnsupportedOperationException();
    }

	@Override
	public Dynamic addJspFile(String servletName, String jspFile) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getSessionTimeout() {
        // set the session timeout if specified in TR.properties
        if (timeout > 0)
        {
            return timeout;
        }

        return 0;
	}

	@Override
	public void setSessionTimeout(int sessionTimeout) {
		timeout = sessionTimeout;
	}

	@Override
	public String getRequestCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRequestCharacterEncoding(String encoding) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getResponseCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResponseCharacterEncoding(String encoding) {
		// TODO Auto-generated method stub
		
	}
}
