package org.apache.turbine.services.mimetype.util;

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

import java.util.ArrayList;

/**
 * This class is used to represent parsed MIME types.
 * The representation is parsed from a string based
 * representation of the MIME type, as defined in the RFC1345.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @version $Id$
 */
public class MimeType
    implements Cloneable
{
    /**
     * A list of well known MIME types.
     */
    public static MimeType TEXT_HTML;
    public static MimeType TEXT_WML;
    public static MimeType TEXT_HDML;
    public static MimeType TEXT_CHTML;
    public static MimeType TEXT_PLAIN;
    public static MimeType MULTIPART;
    public static MimeType MULTIPART_FORM_DATA;
    public static MimeType APPLICATION_POSTSCRIPT;
    public static MimeType APPLICATION_OCTET_STREAM;
    public static MimeType APPLICATION_X_JAVA_AGENT;
    public static MimeType APPLICATION_X_WWW_FORM_URLENCODED;
    public static MimeType MESSAGE_HTTP;
    public static MimeType TEXT_CSS;
    public static MimeType TEXT;
    public static MimeType IMAGE_GIF;
    public static MimeType IMAGE_JPEG;
    public static MimeType IMAGE_WBMP;
    static
    {
        TEXT_HTML =
            new MimeType("text/html");
        TEXT_WML =
            new MimeType("text/vnd.wap.wml");
        TEXT_HDML =
            new MimeType("text/x-hdml");
        TEXT_CHTML =
            new MimeType("text/x-chtml");
        TEXT_PLAIN =
            new MimeType("text/plain");
        MULTIPART	=
            new MimeType("multipart/*");
        MULTIPART_FORM_DATA	=
            new MimeType("multipart/form-data");
        APPLICATION_POSTSCRIPT =
            new MimeType("application/postscript");
        APPLICATION_OCTET_STREAM =
            new MimeType("application/octet-stream");
        APPLICATION_X_JAVA_AGENT =
            new MimeType("application/x-java-agent");
        APPLICATION_X_WWW_FORM_URLENCODED =
            new MimeType("application/x-www-form-urlencoded");
        MESSAGE_HTTP =
            new MimeType("message/http");
        TEXT_CSS =
            new MimeType("text/css");
        TEXT =
            new MimeType("text/*");
        IMAGE_GIF =
            new MimeType("image/gif");
        IMAGE_JPEG =
            new MimeType("image/jpeg");
        IMAGE_WBMP =
            new MimeType("image/vnd.wap.wbmp");
    }

    /**
     * MIME type matching constants.
     */
    public static final int NO_MATCH = 0;
    public static final int MATCH_TYPE = 1;
    public static final int MATCH_SUBTYPE = 2;
    public static final int MATCH_SPECIFIC_SUBTYPE = 3;

    /**
     * A string representation of the main type.
     */
    private String mimeType;

    /**
     * A string representation of the subtype.
     */
    private String mimeSubtype;

    /**
     * Parameter names.
     */
    private String parameterNames[];

    /**
     * Parameter values.
     */
    private String parameterValues[];

    /**
     * A string representation of the MIME type.
     */
    private String mimeTypeString;

    /**
     * Constructs a new MIME type by parsing a specification string.
     *
     * @parameter spec a string representing a MIME type.
     * @throws IllegalArgument for parsing errors.
     */
    public MimeType(String spec)
    {
        this(spec,true);
    }

    /**
     * Constructs a new MIME type by parsing a specification string.
     *
     * @param spec a string representing a MIME type.
     * @param parsep a flag for parsing parameters also.
     * @throws IllegalArgumentException for parsing errors.
     */
    public MimeType(String spec,
                    boolean parsep)
    {
        int start = 0;
        char look = '\0';
        int length = spec.length();

        // Skip leading/trailing blanks.
        while ((start < length) &&
               Character.isWhitespace(spec.charAt(start)))
        {
            start++;
        }
        while ((length > start) &&
               Character.isWhitespace(spec.charAt(length - 1)))
        {
            length--;
        }

        // Get the type.
        StringBuffer sb = new StringBuffer();
        while ((start < length) &&
               ((look = spec.charAt(start)) != '/'))
        {
            sb.append((char) look);
            start++;
        }
        if (look != '/')
            throw new IllegalArgumentException(
                "Syntax error in MIME type " + spec);

        mimeType = sb.toString();

        // Get the subtype.
        start++;
        sb.setLength(0);
        while ((start < length) &&
               ((look = spec.charAt(start)) != ';') &&
               !Character.isWhitespace(look))
        {
            sb.append((char) look);
            start++;
        }
        mimeSubtype = sb.toString();

        if (parsep)
        {
            // Get parameters, if any.
            while ((start < length) &&
                   Character.isWhitespace(spec.charAt(start)))
            {
                start++;
            }
            if (start < length)
            {
                if (spec.charAt(start) != ';')
                    throw new IllegalArgumentException(
                        "Syntax error in MIME type parameters " + spec);

                start++;
                ArrayList na = new ArrayList(4);
                ArrayList va = new ArrayList(4);
                while (start < length)
                {
                    // Get the name.
                    while ((start < length) &&
                           Character.isWhitespace(spec.charAt(start)))
                    {
                        start++;
                    }
                    sb.setLength(0);
                    while ((start < length) &&
                           ((look=spec.charAt(start)) != '=') &&
                           !Character.isWhitespace(look))
                    {
                        sb.append(Character.toLowerCase((char) look));
                        start++ ;
                    }
                    String name = sb.toString();

                    // Get the value.
                    while ((start < length) &&
                           Character.isWhitespace(spec.charAt(start)))
                    {
                        start++;
                    }
                    if (spec.charAt(start) != '=')
                        throw new IllegalArgumentException(
                            "Syntax error in MIME type parameters " + spec);

                    start++ ;
                    while ((start < length) &&
                           Character.isWhitespace(spec.charAt(start)))
                    {
                        start++;
                    }
                    sb.setLength(0);
                    char delim = ';';
                    if (spec.charAt(start) == '"')
                    {
                        start++;
                        delim = '"';
                    }
                    while ((start < length) &&
                           ((look = spec.charAt(start)) != delim) &&
                           ((delim == '"') ||
                            !Character.isWhitespace(look)))
                    {
                        sb.append((char) look);
                        start++;
                    }
                    while ((start < length) &&
                           (spec.charAt(start) != ';'))
                    {
                        start++;
                    }
                    start++;
                    String value = sb.toString();

                    na.add(name);
                    va.add(value);
                }
                parameterNames = (String[]) na.toArray(new String[na.size()]);
                parameterValues = (String[]) va.toArray(new String[va.size()]);
            }
        }
    }

    /**
     * Contructs a new MIME type from specified types.
     *
     * @param type a type.
     * @param subtype a subtype.
     * @throws NullPointerException if type or subtype are nulls.
     */
    public MimeType(String type,
                    String subtype)
    {
        this(type,subtype,null,null);
    }

    /**
     * Contructs a new MIME type from specified parameters.
     *
     * @param type a type.
     * @param subtype a subtype.
     * @param names parameters names.
     * @param values parameter values.
     * @throws NullPointerException if type or subtype are nulls.
     */
    public MimeType(String type,
                    String subtype,
                    String names[],
                    String values[])
    {
        if ((type == null) ||
            (subtype == null))
            throw new NullPointerException("MIME type or subtype missing");

        mimeType = type.trim();
        mimeSubtype = subtype.trim();
        parameterNames  = names;
        parameterValues = values;
    }

    /**
     * Compares the specified MIME type to this one
     * and returns a matching level:
     * NO_MATCH=types do not match,
     * MATCH_TYPE=types match,
     * MATCH_SPECIFIC_TYPE=types match exactly,
     * MATCH_SUBTYPE=types match, subtypes match too,
     * MATCH_SPECIFIC_SUBTYPE=types match, subtypes match exactly.
     *
     * @param other the MimeType to compare.
     * @return the matching level.
     */
    public int match(MimeType other)
    {
        if (mimeType.equals("*") ||
            other.mimeType.equals("*"))
        {
            return MATCH_TYPE;
        }
        else if (!mimeType.equalsIgnoreCase(other.mimeType))
        {
            return NO_MATCH;
        }
        else if (mimeSubtype.equals("*") ||
                 other.mimeSubtype.equals("*"))
        {
            return MATCH_SUBTYPE;
        }
        else if (!mimeSubtype.equalsIgnoreCase(other.mimeSubtype))
        {
            return NO_MATCH;
        }
        else
        {
            return MATCH_SPECIFIC_SUBTYPE;
        }
    }

    /**
     * Gets the main type of the MIME type.
     *
     * @return the main type as a string.
     */
    public String getType()
    {
        return mimeType;
    }

    /**
     * Gets the subtype of the MIME type.
     *
     * @return the subtype as a string.
     */
    public String getSubtype()
    {
        return mimeSubtype;
    }

    /**
     * Gets the type and the subtype of the MIME type.
     *
     * @return the types as a string.
     */
    public String getTypes()
    {
        return mimeType + '/' + mimeSubtype;
    }

    /**
     * Checks whether the MIME type contains the specified parameter.
     *
     * @param param the name opf the parameter.
     * @return true if the parameter found, otherwise false.
     */
    public boolean hasParameter(String param)
    {
        String[] na = parameterNames;
        if (na != null)
        {
            for (int i = 0; i < na.length; i++)
            {
                if (na[i].equalsIgnoreCase(param))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the value of a MIME type parameter.
     * The first parameter with the specifed name will be returned.
     *
     * @param param the name of the parameter.
     * @return the value of the parameter, or null.
     */
    public String getParameter(String param)
    {
        String[] na = parameterNames;
        if (na != null)
        {
            String[] va = parameterValues;
            for (int i = 0; i < na.length; i++)
            {
                if (na[i].equalsIgnoreCase(param))
                {
                    return va[i];
                }
            }
        }
        return null ;
    }

    /**
     * Sets the value of a MIME type parameter replacing the old one.
     *
     * @param param the name of the parameter.
     * @param value the value of the parameter.
     */
    public synchronized void setParameter(String param,
                                          String value)
    {
      if (parameterNames != null)
      {
          for (int i = 0; i < parameterNames.length; i++)
          {
              if (parameterNames[i].equalsIgnoreCase(param))
              {
                  parameterValues[i] = value;
                  mimeTypeString = null;
                  return;
              }
          }
      }
      addParameter(param,value);
    }

    /**
     * Adds a parameter to the MIME type.
     *
     * @param param the name of the parameter.
     * @param value the value of the parameter.
     */
    public void addParameter(String param,
                             String value)
    {
        addParameters(new String[]{ param },new String[]{ value });
    }

    /**
     * Adds parameters to the MIME type.
     *
     * @param params an array of parameter names.
     * @param values an array of parameter values.
     * @throw IllegalArgumentException for incorrect parameters.
     */
    public synchronized void addParameters(String[] params,
                                           String[] values)
    {
        if ((params == null) ||
            (values == null) ||
            (params.length != values.length))
            throw new IllegalArgumentException("Incorrect MIME type parameters");

        if (parameterNames != null)
        {
            String[] na = new String[parameterNames.length + params.length];
            String[] va = new String[parameterValues.length + values.length];
            System.arraycopy(parameterNames,0,na,0,parameterNames.length);
            System.arraycopy(params,0,na,parameterNames.length,params.length);
            System.arraycopy(parameterValues,0,va,0,parameterValues.length);
            System.arraycopy(values,0,va,parameterValues.length,values.length);
            parameterNames = na;
            parameterValues = va;
        }
        else
        {
            parameterNames = params;
            parameterValues = values;
        }
        mimeTypeString = null;
    }

    /**
     * Converts the MIME type into a string.
     *
     * @return the string representation of the MIME type.
     */
    public String toString()
    {
        if (mimeTypeString == null)
        {
            StringBuffer sb = new StringBuffer(mimeType);
            sb.append('/');
            sb.append(mimeSubtype);
            String[] na = parameterNames;
            if (na != null)
            {
                String[] va = parameterValues;
                for (int i = 0; i < va.length; i++)
                {
                    sb.append(';');
                    sb.append(na[i]);
                    if (va[i] != null)
                    {
                        sb.append('=');
                        sb.append(va[i]);
                    }
                }
            }
            mimeTypeString = sb.toString();
        }
        return mimeTypeString;
    }
}
