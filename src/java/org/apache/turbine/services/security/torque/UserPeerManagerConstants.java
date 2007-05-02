package org.apache.turbine.services.security.torque;

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

import java.io.Serializable;

import org.apache.turbine.services.security.torque.om.TurbineUserPeer;

/**
 * Constants for configuring the various columns and bean properties
 * for the used peer.
 *
 * <pre>
 * Default is:
 *
 * security.torque.userPeer.class = org.apache.turbine.services.security.torque.om.TurbineUserPeer
 * security.torque.userPeer.column.name       = LOGIN_NAME
 * security.torque.userPeer.column.id         = USER_ID
 * security.torque.userPeer.column.password   = PASSWORD_VALUE
 * security.torque.userPeer.column.firstname  = FIRST_NAME
 * security.torque.userPeer.column.lastname   = LAST_NAME
 * security.torque.userPeer.column.email      = EMAIL
 * security.torque.userPeer.column.confirm    = CONFIRM_VALUE
 * security.torque.userPeer.column.createdate = CREATED
 * security.torque.userPeer.column.lastlogin  = LAST_LOGIN
 * security.torque.userPeer.column.objectdata = OBJECTDATA
 *
 * security.torque.user.class = org.apache.turbine.services.security.torque.om.TurbineUser
 * security.torque.user.property.name       = UserName
 * security.torque.user.property.id         = UserId
 * security.torque.user.property.password   = Password
 * security.torque.user.property.firstname  = FirstName
 * security.torque.user.property.lastname   = LastName
 * security.torque.user.property.email      = Email
 * security.torque.user.property.confirm    = Confirmed
 * security.torque.user.property.createdate = CreateDate
 * security.torque.user.property.lastlogin  = LastLogin
 * security.torque.user.property.objectdata = Objectdata
 *
 * </pre>
 * If security.torque.user.class is unset, then the value of the constant CLASSNAME_DEFAULT
 * from the configured Peer is used.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public interface UserPeerManagerConstants
    extends Serializable
{
    /** The key within the security service properties for the user class implementation */
    String USER_CLASS_KEY =
        "torque.user.class";

    /** The key within the security service properties for the user peer class implementation */
    String USER_PEER_CLASS_KEY =
        "torque.userPeer.class";

    /** User peer default class */
    String USER_PEER_CLASS_DEFAULT =
        TurbineUserPeer.class.getName();

    /** The column name for the login name field. */
    String USER_NAME_COLUMN_KEY =
        "torque.userPeer.column.name";

    /** The column name for the id field. */
    String USER_ID_COLUMN_KEY =
        "torque.userPeer.column.id";

    /** The column name for the password field. */
    String USER_PASSWORD_COLUMN_KEY =
        "torque.userPeer.column.password";

    /** The column name for the first name field. */
    String USER_FIRST_NAME_COLUMN_KEY =
        "torque.userPeer.column.firstname";

    /** The column name for the last name field. */
    String USER_LAST_NAME_COLUMN_KEY =
        "torque.userPeer.column.lastname";

    /** The column name for the email field. */
    String USER_EMAIL_COLUMN_KEY =
        "torque.userPeer.column.email";

    /** The column name for the confirm field. */
    String USER_CONFIRM_COLUMN_KEY =
        "torque.userPeer.column.confirm";

    /** The column name for the create date field. */
    String USER_CREATE_COLUMN_KEY =
        "torque.userPeer.column.createdate";

    /** The column name for the last login field. */
    String USER_LAST_LOGIN_COLUMN_KEY =
        "torque.userPeer.column.lastlogin";

    /** The column name for the objectdata field. */
    String USER_OBJECTDATA_COLUMN_KEY =
        "torque.userPeer.column.objectdata";


    /** The default value for the column name constant for the login name field. */
    String USER_NAME_COLUMN_DEFAULT =
        "LOGIN_NAME";

    /** The default value for the column name constant for the id field. */
    String USER_ID_COLUMN_DEFAULT =
        "USER_ID";

    /** The default value for the column name constant for the password field. */
    String USER_PASSWORD_COLUMN_DEFAULT =
        "PASSWORD_VALUE";

    /** The default value for the column name constant for the first name field. */
    String USER_FIRST_NAME_COLUMN_DEFAULT =
        "FIRST_NAME";

    /** The default value for the column name constant for the last name field. */
    String USER_LAST_NAME_COLUMN_DEFAULT =
        "LAST_NAME";

    /** The default value for the column name constant for the email field. */
    String USER_EMAIL_COLUMN_DEFAULT =
        "EMAIL";

    /** The default value for the column name constant for the confirm field. */
    String USER_CONFIRM_COLUMN_DEFAULT =
        "CONFIRM_VALUE";

    /** The default value for the column name constant for the create date field. */
    String USER_CREATE_COLUMN_DEFAULT =
        "CREATED";

    /** The default value for the column name constant for the last login field. */
    String USER_LAST_LOGIN_COLUMN_DEFAULT =
        "LAST_LOGIN";

    /** The default value for the column name constant for the objectdata field. */
    String USER_OBJECTDATA_COLUMN_DEFAULT =
        "OBJECTDATA";

    /** The property name of the bean property for the login name field. */
    String USER_NAME_PROPERTY_KEY =
        "torque.user.property.name";

    /** The property name of the bean property for the id field. */
    String USER_ID_PROPERTY_KEY =
        "torque.user.property.id";

    /** The property name of the bean property for the password field. */
    String USER_PASSWORD_PROPERTY_KEY =
        "torque.user.property.password";

    /** The property name of the bean property for the first name field. */
    String USER_FIRST_NAME_PROPERTY_KEY =
        "torque.user.property.firstname";

    /** The property name of the bean property for the last name field. */
    String USER_LAST_NAME_PROPERTY_KEY =
        "torque.user.property.lastname";

    /** The property name of the bean property for the email field. */
    String USER_EMAIL_PROPERTY_KEY =
        "torque.user.property.email";

    /** The property name of the bean property for the confirm field. */
    String USER_CONFIRM_PROPERTY_KEY =
        "torque.user.property.confirm";

    /** The property name of the bean property for the create date field. */
    String USER_CREATE_PROPERTY_KEY =
        "torque.user.property.createdate";

    /** The property name of the bean property for the last login field. */
    String USER_LAST_LOGIN_PROPERTY_KEY =
        "torque.user.property.lastlogin";

    /** The property name of the bean property for the last login field. */
    String USER_OBJECTDATA_PROPERTY_KEY =
        "torque.user.property.objectdata";

    /** The default value of the bean property for the login name field. */
    String USER_NAME_PROPERTY_DEFAULT =
        "UserName";

    /** The default value of the bean property for the id field. */
    String USER_ID_PROPERTY_DEFAULT =
        "UserId";

    /** The default value of the bean property for the password field. */
    String USER_PASSWORD_PROPERTY_DEFAULT =
        "Password";

    /** The default value of the bean property for the first name field. */
    String USER_FIRST_NAME_PROPERTY_DEFAULT =
        "FirstName";

    /** The default value of the bean property for the last name field. */
    String USER_LAST_NAME_PROPERTY_DEFAULT =
        "LastName";

    /** The default value of the bean property for the email field. */
    String USER_EMAIL_PROPERTY_DEFAULT =
        "Email";

    /** The default value of the bean property for the confirm field. */
    String USER_CONFIRM_PROPERTY_DEFAULT =
        "Confirmed";

    /** The default value of the bean property for the create date field. */
    String USER_CREATE_PROPERTY_DEFAULT =
        "CreateDate";

    /** The default value of the bean property for the last login field. */
    String USER_LAST_LOGIN_PROPERTY_DEFAULT =
        "LastLogin";

    /** The default value of the bean property for the objectdata field. */
    String USER_OBJECTDATA_PROPERTY_DEFAULT =
        "Objectdata";
};


