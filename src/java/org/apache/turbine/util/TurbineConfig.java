package org.apache.turbine.util;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
import org.apache.turbine.Turbine;
import org.apache.turbine.services.TurbineServices;

/**
 * A class used for initalization of Turbine without a servlet container.
 *
 * If you need to use Turbine outside of a servlet container, you can
 * use this class for initalization of the Turbine servlet.<br>
 *
 * <blockquote><code><pre>
 * TurbineConfig config = new TurbineConfig(".", "/conf/TurbineResources.properties");
 * </pre></code></blockquote>
 *
 * All paths referenced in TurbineResources.properties and the path to
 * the properties file itself (the second argument) will be resolved
 * relative to the directory given as the first argument of the constructor,
 * here - the directory where application was started. Don't worry about
 * discarding the references to objects created above. They are not needed,
 * once everything is initialized.
 *
 * In order to initialize the Services Framework outside of the Turbine Servlet,
 * you need to call the <code>init()</code> method. By default, this will
 * initialize the Resource and Logging Services and any other services you
 * have defined in your TurbineResources.properties file.
 *
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @version $Id$
 */
public class TurbineConfig implements ServletConfig, ServletContext
{
    /** Enables output of debug messages (compile time option). */
    private final static boolean DEBUG = false;

    /** Filenames are looked up in this directory. */
    private File root;

    /** Servlet container (or emulator) attributes. */
    private Map attributes;

    /** Turbine servlet initialization parameters. */
    private Map initParams;

    /** The Turbine servlet instance used for initialization. */
    private Turbine turbine;

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
        initParams.put(TurbineServices.PROPERTIES_PATH_KEY, properties);
    }

    /**
     * Causes this class to initialize itself which in turn initializes
     * all of the Turbine Services that need to be initialized.
     */
    public void init()
    {
        try
        {
            turbine = new Turbine();
            turbine.init(this);
        }
        catch (Exception e)
        {
            Log.error("TurbineConfig: Initialization failed", e);
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
    public String getRealPath( String path )
    {
        File f = new File(root, path);
        if (DEBUG)
        {
            System.err.println("TurbineConfig.getRealPath: path '" + path +
                               "' translated to '" + f.getPath() + "' " +
                               (f.exists() ? "" : "not ") + "found");
        }
        return (f.exists() ? f.getPath() : null);
    }

    /**
     * Retrieves an initialization parameter.
     *
     * @param name the name of the parameter.
     * @return the value of the parameter.
     */
    public String getInitParameter(String name)
    {
        return (String)initParams.get(name);
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
    public URL getResource( String s )
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
    public InputStream getResourceAsStream( String s )
    {
        try
        {
            FileInputStream fis = new FileInputStream(getRealPath(s));
            return new BufferedInputStream(fis);
        }
        catch(FileNotFoundException e)
        {
            return null;
        }
    }

    /**
     * Logs an error message.
     *
     * @param e an Exception.
     * @param m a message.
     * @deprecated
     */
    public void log(Exception e, String m)
    {
        // cannot use Turbine logging yet.
        System.err.println(m);
        e.printStackTrace();
    }

    /**
     * Logs a message.
     *
     * @param m a message.
     */
    public void log(String m)
    {
        // cannot use Turbine logging yet.
        System.out.println(m);
    }

    /**
     * Logs an error message.
     *
     * @param t a Throwable object.
     * @param m a message.
     */
    public void log( String m, Throwable t )
    {
        // cannot use Turbine logging yet.
        System.err.println(m);
        t.printStackTrace();
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
    public RequestDispatcher getNamedDispatcher( String s)
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
    public RequestDispatcher getRequestDispatcher( String s )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletConfig or ServletContext interface that is not
     * implemented and will throw <code>UnsuportedOperationException</code>
     * upon invocation
     * @deprecated
     */
    public Set getResourcePaths()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletConfig or ServletContext interface that is not
     * implemented and will throw <code>UnsuportedOperationException</code>
     * upon invocation
     * @deprecated
     */
    public Set getResourcePaths(String s)
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
    public String getServerInfo()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletConfig or ServletContext interface that is not
     * implemented and will throw <code>UnsuportedOperationException</code>
     * upon invocation
     * @deprecated
     */
    public Servlet getServlet(String s)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletConfig or ServletContext interface that is not
     * implemented and will throw <code>UnsuportedOperationException</code>
     * upon invocation
     * @deprecated
     */
    public Enumeration getServletNames()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Not implemented.
     *
     * A method in ServletConfig or ServletContext interface that is not
     * implemented and will throw <code>UnsuportedOperationException</code>
     * upon invocation
     * @deprecated
     */
    public Enumeration getServlets()
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
    public void removeAttribute( String s )
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
    public void setAttribute( String s, Object o )
    {
        throw new UnsupportedOperationException();
    }
}
