package org.apache.turbine.util;

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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.turbine.Turbine;

/**
 * A class used for initialization of Turbine without a servlet container.
 * <p>
 * If you need to use Turbine outside of a servlet container, you can
 * use this class for initialization of the Turbine servlet.
 * <p>
 * <blockquote><code><pre>
 * TurbineConfig config = new TurbineConfig(".", "conf/TurbineResources.properties");
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
    protected Map attributes;

    /** Turbine servlet initialization parameters. */
    protected Map initParams;

    /** The Turbine servlet instance used for initialization. */
    private Turbine turbine;

    /** Logging */
    private Log log = LogFactory.getLog(this.getClass());

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
    public TurbineConfig(String path, Map attributes, Map initParams)
    {
        root = new File(path);
        this.attributes = attributes;
        this.initParams = initParams;
    }

    /**
     * @see #TurbineConfig(String path, Map attributes, Map initParams)
     */
    public TurbineConfig(String path, Map initParams)
    {
        this(path, new HashMap(0), initParams);
    }

    /**
     * Constructs a TurbineConfig.
     *
     * This is a specialized constructor that allows to configure
     * Turbine easiliy in the common setups.
     *
     * @param path The web application root (i.e. the path for file lookup).
     * @param properties the relative path to TurbineResources.properties file
     */
    public TurbineConfig(String path, String properties)
    {
        this(path, new HashMap(1));
        initParams.put(PROPERTIES_PATH_KEY, properties);
    }

    /**
     * Causes this class to initialize itself which in turn initializes
     * all of the Turbine Services that need to be initialized.
     *
     * @see org.apache.stratum.lifecycle.Initializable
     */
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
    public void dispose()
    {
        if (turbine != null)
        {
            turbine.destroy();
        }
    }

    /**
     * Returns a reference to the object cast onto ServletContext type.
     *
     * @return a ServletContext reference
     */
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
    public String getRealPath(String path)
    {
        String result = null;
        File f = new File(root, path);

        if (log.isDebugEnabled())
        {
            StringBuffer sb = new StringBuffer();

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
    public String getInitParameter(String name)
    {
        return (String) initParams.get(name);
    }

    /**
     * Retrieves an Enumeration of initialization parameter names.
     *
     * @return an Enumeration of initialization parameter names.
     */
    public Enumeration getInitParameterNames()
    {
        return new Vector(initParams.keySet()).elements();
    }

    /**
     * Returns the servlet name.
     *
     * Fixed value "Turbine" is returned.
     *
     * @return the servlet name.
     */
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
    public String getServletContextName()
    {
        return "Turbine";
    }

    /**
     * Returns a URL to the resource that is mapped to a specified
     * path. The path must begin with a "/" and is interpreted
     * as relative to the current context root.
     *
     * @param s the path to the resource
     * @return a URL pointing to the resource
     * @exception MalformedURLException
     */
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
    public void log(Exception e, String m)
    {
        log.info(m, e);
    }

    /**
     * Logs a message.
     *
     * @param m a message.
     */
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
    public void log(String m, Throwable t)
    {
        log.info(m, t);
    }

    /**
     * Returns the servlet container attribute with the given name, or
     * null if there is no attribute by that name.
     */
    public Object getAttribute(String s)
    {
        return attributes.get(s);
    }

    /**
     * Returns an Enumeration containing the attribute names available
     * within this servlet context.
     */
    public Enumeration getAttributeNames()
    {
        return new Vector(attributes.keySet()).elements();
    }

    // Unimplemented methods follow

    /**
     * Not implemented.
     *
     * A method in ServletConfig or ServletContext interface that is not
     * implemented and will throw <code>UnsuportedOperationException</code>
     * upon invocation
     */
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
    public Set getResourcePaths(String s)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext (2.3) interface that is not implemented and
     * will throw <code>UnsuportedOperationException</code> upon invocation
     */
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
    public Enumeration getServletNames()
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
    public Enumeration getServlets()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletContext interface that is not implemented and will
     * throw <code>UnsuportedOperationException</code> upon invocation
     */
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
    public void setAttribute(String s, Object o)
    {
        throw new UnsupportedOperationException();
    }
}
