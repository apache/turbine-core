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

import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.ecs.GenericElement;
import org.apache.ecs.html.A;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.util.parser.BaseValueParser;

/**
 * This creates a Dynamic URI for use within the Turbine system
 *
 * <p>If you use this class to generate all of your href tags as well
 * as all of your URI's, then you will not need to worry about having
 * session data setup for you or using HttpServletRequest.encodeUrl()
 * since this class does everything for you.
 *
 * <code><pre>
 * DynamicURI dui = new DynamicURI (data, "UserScreen" );
 * dui.setName("Click Here").addPathInfo("user","jon");
 * dui.getA();
 * </pre></code>
 *
 * The above call to getA() would return the String:
 *
 * &lt;A HREF="http://www.server.com:80/servlets/Turbine/screen=UserScreen&amp;amp;user=jon"&gt;Click Here&lt;/A&gt;
 *
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 */
public class DynamicURI
{
    /** HTTP protocol. */
    public static final String HTTP = "http";

    /** HTTPS protocol. */
    public static final String HTTPS = "https";

    /** The ServerData object. */
    protected ServerData  sd = null;

    /** The RunData object. */
    protected RunData  data = null;

    /** ie: http or https. */
    protected String      serverScheme = null;

    /** ie: www.foo.com. */
    protected String      serverName = null;

    /** ie: 80 or 443. */
    protected int         serverPort = 80;

    /** /foo/Turbine. */
    protected String      scriptName = null;


    // Used with RunData constructors to provide a way around a JServ
    // 1.0 bug.

    /** Servlet response interface. */
    public HttpServletResponse res = null;

    /** A Vector that contains all the path info if any. */
    protected Vector pathInfo = null;

    /** A Vectory that contains all the query data if any. */
    protected Vector queryData = null;

    /**
     * Fast shortcut to determine if there is any data in the path
     * info.
     */
    protected boolean hasPathInfo = false;

    /**
     * Fast shortcut to determine if there is any data in the query
     * data.
     */
    protected boolean hasQueryData = false;

    /** Whether we want to redirect or not. */
    protected boolean redirect = false;

    /** P = 0 for path info. */
    protected static final int PATH_INFO = 0;

    /** Q = 1 for query data. */
    protected static final int QUERY_DATA = 1;

    /**
     * Constructor sets up some variables.
     *
     * @param data A Turbine RunData object.
     */
    public DynamicURI( RunData data )
    {
        init(data);
    }

    /**
     * Default constructor - one of the init methods must be called
     * before use.
     */
    public DynamicURI()
    {
    }
    
    /**
     * Constructor sets up some variables.
     *
     * @param data A Turbine RunData object.
     * @param screen A String with the name of a screen.
     */
    public DynamicURI( RunData data,
                       String screen )
    {
        this(data);
        setScreen(screen);
    }

    /**
     * Constructor sets up some variables.
     *
     * @param data A Turbine RunData object.
     * @param screen A String with the name of a screen.
     * @param action A String with the name of an action.
     */
    public DynamicURI( RunData data,
                       String screen,
                       String action )
    {
        this( data, screen );
        setAction(action);
    }

    /**
     * Constructor sets up some variables.
     *
     * @param data A Turbine RunData object.
     * @param screen A String with the name of a screen.
     * @param action A String with the name of an action.
     * @param redirect True if it should redirect.
     */
    public DynamicURI( RunData data,
                       String screen,
                       String action,
                       boolean redirect )
    {
        this( data, screen, action );
        this.redirect = redirect;
    }

    /**
     * Constructor sets up some variables.
     *
     * @param data A Turbine RunData object.
     * @param screen A String with the name of a screen.
     * @param redirect True if it should redirect.
     */
    public DynamicURI( RunData data,
                       String screen,
                       boolean redirect )
    {
        this( data, screen );
        this.redirect = redirect;
    }

    /**
     * Constructor sets up some variables.
     *
     * @param data A Turbine RunData object.
     * @param redirect True if it should redirect.
     */
    public DynamicURI( RunData data,
                       boolean redirect )
    {
        this( data );
        this.redirect = redirect;
    }

    /**
     * Main constructor for DynamicURI.  Uses ServerData.
     *
     * @param sd A ServerData.
     */
    public DynamicURI( ServerData sd )
    {
        init(sd);
    }

    /**
     * Main constructor for DynamicURI.  Uses ServerData.
     *
     * @param sd A ServerData.
     * @param screen A String with the name of a screen.
     */
    public DynamicURI( ServerData sd, String screen )
    {
        this(sd);
        setScreen(screen);
    }

    /**
     * Main constructor for DynamicURI.  Uses ServerData.
     *
     * @param sd A ServerData.
     * @param screen A String with the name of a screen.
     * @param action A String with the name of an action.
     */
    public DynamicURI( ServerData sd,
                       String screen,
                       String action )
    {
        this( sd, screen );
        setAction(action);
    }

    /**
     * Main constructor for DynamicURI.  Uses ServerData.
     *
     * @param sd A ServerData.
     * @param screen A String with the name of a screen.
     * @param action A String with the name of an action.
     * @param redirect True if it should redirect.
     */
    public DynamicURI( ServerData sd,
                       String screen,
                       String action,
                       boolean redirect )
    {
        this( sd, screen, action );
        this.redirect = redirect;
    }

    /**
     * Main constructor for DynamicURI.  Uses ServerData.
     *
     * @param sd A ServerData.
     * @param screen A String with the name of a screen.
     * @param redirect True if it should redirect.
     */
    public DynamicURI( ServerData sd,
                       String screen,
                       boolean redirect )
    {
        this( sd, screen );
        this.redirect = redirect;
    }

    /**
     * Main constructor for DynamicURI.  Uses ServerData.
     *
     * @param sd A ServerData.
     * @param redirect True if it should redirect.
     */
    public DynamicURI( ServerData sd,
                       boolean redirect )
    {
        this( sd );
        this.redirect = redirect;
    }
    
    /**
     * Initialize with a RunData object
     *
     * @param RunData
     */
    public void init( RunData data )
    {
        init(data.getServerData());
        this.data = data;
        this.res = data.getResponse();
    }

    /**
     * Initialize with a ServerData object.
     *
     * @param ServerData
     */
    public void init ( ServerData sd )
    {
        this.setServerData( sd );
        init();
    }
    
    /**
     * <p>If the type is P (0), then add name/value to the pathInfo
     * hashtable.
     *
     * <p>If the type is Q (1), then add name/value to the queryData
     * hashtable.
     *
     * @param type Type (P or Q) of insertion.
     * @param name A String with the name to add.
     * @param value A String with the value to add.
     */
    protected void add ( int type,
                       String name,
                       String value )
    {
        Object[] tmp = new Object[2];
        tmp[0] = (Object) BaseValueParser.convertAndTrim(name);
        tmp[1] = (Object) value;
        switch (type)
        {
        case PATH_INFO:
            this.pathInfo.addElement ( tmp );
            this.hasPathInfo = true;
            break;
        case QUERY_DATA:
            this.queryData.addElement ( tmp );
            this.hasQueryData = true;
            break;
        }
    }

    /**
     * Method for a quick way to add all the parameters in a
     * ParameterParser.
     *
     * <p>If the type is P (0), then add name/value to the pathInfo
     * hashtable.
     *
     * <p>If the type is Q (1), then add name/value to the queryData
     * hashtable.
     *
     * @param type Type (P or Q) of insertion.
     * @param pp A ParameterParser.
     */
    protected void add( int type,
                      ParameterParser pp )
    {
        Enumeration e = pp.keys();
        while ( e.hasMoreElements() )
        {
            String key = (String)e.nextElement();
            if ( !key.equalsIgnoreCase("action") &&
                 !key.equalsIgnoreCase("screen") &&
                 !key.equalsIgnoreCase("template") )
            {
                String[] values = pp.getStrings(key);
                for ( int i=0; i<values.length; i++ ) 
                {
                    add( type, key, values[i] );   
                }                
            }
        }
    }

    /**
     * Adds a name=value pair to the path_info string.
     *
     * @param name A String with the name to add.
     * @param value An Object with the value to add.
     */
    public DynamicURI addPathInfo ( String name, Object value )
    {
        add ( PATH_INFO, name, value.toString() );
        return this;
    }

    /**
     * Adds a name=value pair to the path_info string.
     *
     * @param name A String with the name to add.
     * @param value A String with the value to add.
     */
    public DynamicURI addPathInfo ( String name, String value )
    {
        add ( PATH_INFO, name, value );
        return this;
    }

    /**
     * Adds a name=value pair to the path_info string.
     *
     * @param name A String with the name to add.
     * @param value A double with the value to add.
     */
    public DynamicURI addPathInfo ( String name, double value )
    {
        add ( PATH_INFO, name, Double.toString(value) );
        return this;
    }

    /**
     * Adds a name=value pair to the path_info string.
     *
     * @param name A String with the name to add.
     * @param value An int with the value to add.
     */
    public DynamicURI addPathInfo ( String name, int value )
    {
        add ( PATH_INFO, name, new Integer(value).toString() );
        return this;
    }

    /**
     * Adds a name=value pair to the path_info string.
     *
     * @param name A String with the name to add.
     * @param value A long with the value to add.
     */
    public DynamicURI addPathInfo ( String name, long value )
    {
        add ( PATH_INFO, name, new Long(value).toString() );
        return this;
    }

    /**
     * Adds a name=value pair for every entry in a ParameterParser
     * object to the path_info string.
     *
     * @param pp A ParameterParser.
     */
    public DynamicURI addPathInfo ( ParameterParser pp )
    {
        add ( PATH_INFO, pp );
        return this;
    }

    /**
     * Adds a name=value pair to the query string.
     *
     * @param name A String with the name to add.
     * @param value An Object with the value to add.
     */
    public DynamicURI addQueryData ( String name, Object value )
    {
        add ( QUERY_DATA, name, value.toString() );
        return this;
    }

    /**
     * Adds a name=value pair to the query string.
     *
     * @param name A String with the name to add.
     * @param value A String with the value to add.
     */
    public DynamicURI addQueryData ( String name, String value )
    {
        add ( QUERY_DATA, name, value );
        return this;
    }

    /**
     * Adds a name=value pair to the query string.
     *
     * @param name A String with the name to add.
     * @param value A double with the value to add.
     */
    public DynamicURI addQueryData ( String name, double value )
    {
        add ( QUERY_DATA, name, Double.toString(value) );
        return this;
    }

    /**
     * Adds a name=value pair to the query string.
     *
     * @param name A String with the name to add.
     * @param value An int with the value to add.
     */
    public DynamicURI addQueryData ( String name, int value )
    {
        add ( QUERY_DATA, name, new Integer(value).toString() );
        return this;
    }

    /**
     * Adds a name=value pair to the query string.
     *
     * @param name A String with the name to add.
     * @param value A long with the value to add.
     */
    public DynamicURI addQueryData ( String name, long value )
    {
        add ( QUERY_DATA, name, new Long(value).toString() );
        return this;
    }

    /**
     * Adds a name=value pair for every entry in a ParameterParser
     * object to the query string.
     *
     * @param pp A ParameterParser.
     */
    public DynamicURI addQueryData ( ParameterParser pp )
    {
        add ( QUERY_DATA, pp );
        return this;
    }

    /**
     * Create an anchor object.  This call to getA():
     *
     * <code><pre>
     * DynamicURI dui = new DynamicURI (data, "UserScreen" );
     * dui.setName("Click Here").addPathInfo("user","jon");
     * dui.getA();
     * </pre></code>
     *
     * would return the String:
     *
     * <p>&lt;A HREF="http://www.server.com:80/servlets/Turbine/screen=UserScreen&amp;amp;user=jon"&gt;Click Here&lt;/A&gt;
     *
     * @param name A String with the name for the anchor.
     * @return The anchor as a &lt;A HREF=""&gt;name&lt;/A&gt;.
     */
    public String getA( String name )
    {
        return new A(this.toString(), name).toString();
    }

    /**
     * Gets the script name (/servlets/Turbine).
     *
     * @return A String with the script name.
     */
    public String getScriptName ()
    {
        if ( this.scriptName == null )
        {
            return "";
        }            
        return this.scriptName;
    }

    /**
     * Gets the server name.
     *
     * @return A String with the server name.
     */
    public String getServerName ()
    {
        if ( this.serverName == null )
        {
            return "";
        }            
        return(this.serverName);
    }

    /**
     * Gets the server port.
     *
     * @return A String with the server port.
     */
    public int getServerPort ()
    {
        if ( this.serverPort == 0 )
        {
            return 80;
        }            
        return this.serverPort;
    }

    /**
     * Gets the server scheme (HTTP or HTTPS).
     *
     * @return A String with the server scheme.
     */
    public String getServerScheme ()
    {
        if ( this.serverScheme == null )
        {
            return "";
        }            
        return (this.serverScheme);
    }

    /**
     * Initializes some common variables.
     */
    protected void init()
    {
        this.serverScheme = this.getServerData().getServerScheme();
        this.serverName = this.getServerData().getServerName();
        this.serverPort = this.getServerData().getServerPort();
        this.scriptName = this.getServerData().getScriptName();
        this.pathInfo = new Vector();
        this.queryData = new Vector();
    }

    /**
     * <p>If the type is P (0), then remove name/value from the
     * pathInfo hashtable.
     *
     * <p>If the type is Q (1), then remove name/value from the
     * queryData hashtable.
     *
     * @param type Type (P or Q) of removal.
     * @param name A String with the name to be removed.
     */
    protected void remove ( int type,
                          String name )
    {
        try
        {
            switch (type)
            {
            case PATH_INFO:
                for (Enumeration e = this.pathInfo.elements() ;
                     e.hasMoreElements() ;)
                {
                    Object[] tmp = (Object[]) e.nextElement();
                    if ( BaseValueParser.convertAndTrim(name)
                         .equals ( (String)tmp[0] ) )
                    {
                        this.pathInfo.removeElement ( tmp );
                    }
                }
                if ( hasPathInfo && this.pathInfo.size() == 0 )
                {
                    this.hasPathInfo = false;
                }                    
                break;
            case QUERY_DATA:
                for (Enumeration e = this.queryData.elements() ;
                     e.hasMoreElements() ;)
                {
                    Object[] tmp = (Object[]) e.nextElement();
                    if ( BaseValueParser.convertAndTrim(name)
                         .equals ( (String)tmp[0] ) )
                    {
                        this.queryData.removeElement ( tmp );
                    }
                }
                if ( hasQueryData && this.queryData.size() == 0 )
                {
                    this.hasQueryData = false;
                }                    
                break;
            }
        }
        catch ( Exception e )
        {
        }
    }

    /**
     * Removes all the path info elements.
     */
    public void removePathInfo ()
    {
        this.pathInfo.removeAllElements();
        this.hasPathInfo = false;
    }

    /**
     * Removes a name=value pair from the path info.
     *
     * @param name A String with the name to be removed.
     */
    public void removePathInfo ( String name )
    {
        remove ( PATH_INFO, name );
    }

    /**
     * Removes all the query string elements.
     */
    public void removeQueryData ()
    {
        this.queryData.removeAllElements();
        this.hasQueryData = false;
    }

    /**
     * Removes a name=value pair from the query string.
     *
     * @param name A String with the name to be removed.
     */
    public void removeQueryData ( String name )
    {
        remove ( QUERY_DATA, name );
    }

    /**
     * This method takes a Vector of key/value arrays and converts it
     * into a URL encoded querystring format.
     *
     * @param data A Vector of key/value arrays.
     * @return A String with the URL encoded data.
     */
    protected String renderPathInfo ( Vector data )
    {
        String key = null;
        String value = null;
        String tmp = null;
        StringBuffer out = new StringBuffer();
        Enumeration keys = data.elements();
        while(keys.hasMoreElements())
        {
            Object[] stuff = (Object[]) keys.nextElement();
            key = URLEncoder.encode((String)stuff[0]);
            tmp = (String) stuff[1];
            if (tmp == null || tmp.length() == 0)
            {
                value = "null";
            }
            else
            {
                value = URLEncoder.encode(tmp);
            }

            if (out.length() > 0) 
            {
                out.append ( "/" );
            }
            out.append ( key );
            out.append ( "/" );
            out.append ( value );
        }
        return out.toString();
    }

    /**
     * This method takes a Vector of key/value arrays and converts it
     * into a URL encoded querystring format.
     *
     * @param data A Vector of key/value arrays.
     * @return A String with the URL encoded data.
     */
    protected String renderQueryString ( Vector data )
    {
        String key = null;
        String value = null;
        String tmp = null;
        StringBuffer out = new StringBuffer();
        Enumeration keys = data.elements();
        while(keys.hasMoreElements())
        {
            Object[] stuff = (Object[]) keys.nextElement();
            key   = URLEncoder.encode((String) stuff[0]);
            tmp = (String) stuff[1];
            if (tmp == null || tmp.length() == 0)
            {
                value = "null";
            }
            else
            {
                value = URLEncoder.encode(tmp);
            }

            if ( out.length() > 0)
            {
                out.append ( "&" );
            }
            out.append ( key );
            out.append ( "=" );
            out.append ( value );
        }
        return out.toString();
    }

    /**
     * Sets the action= value for this URL.
     *
     * <p>By default it adds the information to the path_info instead
     * of the query data.
     *
     * @param action A String with the action value.
     * @return A DynamicURI (self).
     */
    public DynamicURI setAction ( String action )
    {
        add ( PATH_INFO, "action", action );
        return this;
    }

    /**
     * Sets the screen= value for this URL.
     *
     * <p>By default it adds the information to the path_info instead
     * of the query data.
     *
     * @param action A String with the screen value.
     * @return A DynamicURI (self).
     */
    public DynamicURI setScreen ( String screen )
    {
        add ( PATH_INFO, "screen", screen );
        return this;
    }

    /**
     * Sets the script name (/servlets/Turbine).
     *
     * @param name A String with the script name.
     * @return A DynamicURI (self).
     */
    public DynamicURI setScriptName ( String name )
    {
        this.scriptName = name;
        return this;
    }

    /**
     * Sets the server name.
     *
     * @param name A String with the server name.
     * @return A DynamicURI (self).
     */
    public DynamicURI setServerName ( String name )
    {
        this.serverName = name;
        return this;
    }

    /**
     * Sets the server port.
     *
     * @param port An int with the port.
     * @return A DynamicURI (self).
     */
    public DynamicURI setServerPort ( int port )
    {
        this.serverPort = port;
        return this;
    }

    /**
     * Method to specify that a URI should use SSL.  Whether or not it
     * does is determined from TurbineResources.properties.  Port
     * number is 443.
     *
     * @return A DynamicURI (self).
     */
    public DynamicURI setSecure()
    {
        return setSecure(443);
    }

    /**
     * Method to specify that a URI should use SSL.  Whether or not it
     * does is determined from TurbineResources.properties.
     *
     * @param port An int with the port number.
     * @return A DynamicURI (self).
     */
    public DynamicURI setSecure(int port)
    {
        boolean isSSL = TurbineResources.getBoolean("use.ssl", true);
        if (isSSL)
        {
            setServerScheme(DynamicURI.HTTPS);
            setServerPort(port);
        }
        return this;
    }

    /**
     * Sets the scheme (HTTP or HTTPS).
     *
     * @param scheme A String with the scheme.
     * @return A DynamicURI (self).
     */
    public DynamicURI setServerScheme ( String scheme )
    {
        this.serverScheme = scheme;
        return this;
    }

    /**
     * Builds the URL with all of the data URL-encoded as well as
     * encoded using HttpServletResponse.encodeUrl().
     *
     * <p>
     * <code><pre>
     * DynamicURI dui = new DynamicURI (data, "UserScreen" );
     * dui.addPathInfo("user","jon");
     * dui.toString();
     * </pre></code>
     *
     *  The above call to toString() would return the String:
     *
     * <p>
     * http://www.server.com/servlets/Turbine/screen/UserScreen/user/jon
     *
     * @return A String with the built URL.
     */
    public String toString()
    {
        StringBuffer output = new StringBuffer();
        output.append ( getServerScheme() );
        output.append ( "://" );
        output.append ( getServerName() );
        if ( (getServerScheme().equals(HTTP) && getServerPort() != 80)
            || (getServerScheme().equals(HTTPS) && getServerPort() != 443)
           )
        {
            output.append (":");
            output.append ( getServerPort() );
        }
        output.append ( getScriptName() );
        if ( this.hasPathInfo )
        {
            output.append ( "/" );
            output.append ( renderPathInfo(this.pathInfo) );
        }
        if ( this.hasQueryData )
        {
            output.append ( "?" );
            output.append ( renderQueryString(this.queryData) );
        }

        // There seems to be a bug in Apache JServ 1.0 where the
        // session id is not appended to the end of the url when a
        // cookie has not been set.
        if ( this.res != null )
        {
            if ( this.redirect )
                return res.encodeRedirectUrl (output.toString());
            else
                return res.encodeUrl (output.toString());
        }
        else
        {
            return output.toString();
        }
    }

    /**
     * Given a RunData object, get a URI for the request.  This is
     * necessary sometimes when you want the exact URL and don't want
     * DynamicURI to be too smart and remove actions, screens, etc.
     * This also returns the Query Data where DynamicURI normally
     * would not.
     *
     * @param data A Turbine RunData object.
     * @return A String with the URL representing the RunData.
     */
    public static String toString(RunData data)
    {
        StringBuffer output = new StringBuffer();
        HttpServletRequest request = data.getRequest();

        output.append ( data.getServerScheme() );
        output.append ( "://" );
        output.append ( data.getServerName() );

        if ( (data.getServerScheme().equals(HTTP) &&
              data.getServerPort() != 80) ||
             (data.getServerScheme().equals(HTTPS) &&
              data.getServerPort() != 443) )
        {
            output.append (":");
            output.append ( data.getServerPort() );
        }

        output.append ( data.getServerData().getScriptName() );

        if ( request.getPathInfo() != null )
        {
            output.append( request.getPathInfo() );
        }

        if ( request.getQueryString() != null )
        {
            output.append ( "?" );
            output.append ( request.getQueryString() );
        }
        return output.toString();
    }

    /**
     * Returns the ServerData used to initialize this DynamicURI.
     *
     * @return A ServerData used to initialize this DynamicURI.
     */
    public ServerData getServerData()
    {
        return this.sd;
    }

    /**
     * Sets the ServerData used to initialize this DynamicURI.
     *
     * @param sd A ServerData used to initialize this DynamicURI.
     */
    public void setServerData( ServerData sd )
    {
        this.sd = sd;
    }
}
