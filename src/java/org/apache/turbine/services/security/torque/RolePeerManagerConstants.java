package org.apache.turbine.services.security.torque;

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

/**
 * Constants for configuring the various columns and bean properties
 * for the used peer.
 *
 * <pre>
 * Default is:
 *
 * security.torque.rolePeer.class = org.apache.turbine.services.security.torque.om.TurbineRolePeer
 * security.torque.rolePeer.column.name       = ROLE_NAME
 * security.torque.rolePeer.column.id         = ROLE_ID
 * 
 * security.torque.role.class = org.apache.turbine.services.security.torque.om.TurbineRole
 * security.torque.role.property.name       = Name
 * security.torque.role.property.id         = RoleId
 *
 * </pre>
 * If security.torque.role.class is unset, then the value of the constant CLASSNAME_DEFAULT
 * from the configured Peer is used. 
 * 
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public interface RolePeerManagerConstants
{
    /** The key within the security service properties for the role class implementation */
    public static final String ROLE_CLASS_KEY = 
        "torque.role.class";

    /** The key within the security service properties for the role peer class implementation */
    public static final String ROLE_PEER_CLASS_KEY = 
        "torque.rolePeer.class";

    /** Role peer default class */
    public static final String ROLE_PEER_CLASS_DEFAULT =
        "org.apache.turbine.services.security.torque.om.TurbineRolePeer";

    /** The column name for the login name field. */
    public static final String ROLE_NAME_COLUMN_KEY =
        "torque.rolePeer.column.name";

    /** The column name for the id field. */
    public static final String ROLE_ID_COLUMN_KEY =
        "torque.rolePeer.column.id";


    /** The default value for the column name constant for the login name field. */
    public static final String ROLE_NAME_COLUMN_DEFAULT =
        "ROLE_NAME";

    /** The default value for the column name constant for the id field. */
    public static final String ROLE_ID_COLUMN_DEFAULT =
        "ROLE_ID";


    /** The property name of the bean property for the login name field. */
    public static final String ROLE_NAME_PROPERTY_KEY = 
        "torque.role.property.name";
    
    /** The property name of the bean property for the id field. */
    public static final String ROLE_ID_PROPERTY_KEY =
        "torque.role.property.id";
    

    /** The default value of the bean property for the login name field. */
    public static final String ROLE_NAME_PROPERTY_DEFAULT =
        "Name";

    /** The default value of the bean property for the id field. */
    public static final String ROLE_ID_PROPERTY_DEFAULT =
        "RoleId";

};

    
