-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements.  See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership.  The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License.  You may obtain a copy of the License at
--
--   http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied.  See the License for the
-- specific language governing permissions and limitations
-- under the License.



-- ---------------------------------------------------------------------------
-- TURBINE_PERMISSION
-- ---------------------------------------------------------------------------
CREATE TABLE TURBINE_PERMISSION
(
    PERMISSION_ID integer,
    PERMISSION_NAME VARCHAR (99),
    OBJECTDATA BINARY,
    PRIMARY KEY(PERMISSION_ID),
    UNIQUE (PERMISSION_NAME)
);



-- ---------------------------------------------------------------------------
-- TURBINE_ROLE
-- ---------------------------------------------------------------------------
CREATE TABLE TURBINE_ROLE
(
    ROLE_ID integer,
    ROLE_NAME VARCHAR (99),
    OBJECTDATA BINARY,
    PRIMARY KEY(ROLE_ID),
    UNIQUE (ROLE_NAME)
);



-- ---------------------------------------------------------------------------
-- TURBINE_GROUP
-- ---------------------------------------------------------------------------
CREATE TABLE TURBINE_GROUP
(
    GROUP_ID integer,
    GROUP_NAME VARCHAR (99),
    OBJECTDATA BINARY,
    PRIMARY KEY(GROUP_ID),
    UNIQUE (GROUP_NAME)
);



-- ---------------------------------------------------------------------------
-- TURBINE_ROLE_PERMISSION
-- ---------------------------------------------------------------------------
CREATE TABLE TURBINE_ROLE_PERMISSION
(
    ROLE_ID integer,
    PERMISSION_ID integer,
    PRIMARY KEY(ROLE_ID,PERMISSION_ID)
);



-- ---------------------------------------------------------------------------
-- TURBINE_USER
-- ---------------------------------------------------------------------------
CREATE TABLE TURBINE_USER
(
    USER_ID integer,
    LOGIN_NAME VARCHAR (32),
    PASSWORD_VALUE VARCHAR (32),
    FIRST_NAME VARCHAR (99),
    LAST_NAME VARCHAR (99),
    EMAIL VARCHAR (99),
    CONFIRM_VALUE VARCHAR (99),
    MODIFIED timestamp,
    CREATED timestamp,
    LAST_LOGIN timestamp,
    OBJECTDATA BINARY,
    PRIMARY KEY(USER_ID),
    UNIQUE (LOGIN_NAME)
);



-- ---------------------------------------------------------------------------
-- TURBINE_USER_GROUP_ROLE
-- ---------------------------------------------------------------------------
CREATE TABLE TURBINE_USER_GROUP_ROLE
(
    USER_ID integer,
    GROUP_ID integer,
    ROLE_ID integer,
    PRIMARY KEY(USER_ID,GROUP_ID,ROLE_ID)
);



-- ---------------------------------------------------------------------------
-- TURBINE_SCHEDULED_JOB
-- ---------------------------------------------------------------------------
CREATE TABLE TURBINE_SCHEDULED_JOB
(
    JOB_ID integer,
    SECOND integer,
    MINUTE integer,
    HOUR integer,
    WEEK_DAY integer,
    DAY_OF_MONTH integer,
    TASK VARCHAR (99),
    EMAIL VARCHAR (99),
    PROPERTY BINARY,
    PRIMARY KEY(JOB_ID)
);



