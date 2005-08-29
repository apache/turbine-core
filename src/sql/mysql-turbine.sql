-- Copyright 2001-2005 The Apache Software Foundation.
--
-- Licensed under the Apache License, Version 2.0 (the "License")
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
# -----------------------------------------------------------------------
# TURBINE_PERMISSION
# -----------------------------------------------------------------------
drop table if exists TURBINE_PERMISSION;

CREATE TABLE TURBINE_PERMISSION
(
    PERMISSION_ID INTEGER NOT NULL,
    PERMISSION_NAME VARCHAR (99) NOT NULL,
    OBJECTDATA MEDIUMBLOB,
    PRIMARY KEY(PERMISSION_ID),
    UNIQUE (PERMISSION_NAME)
);


# -----------------------------------------------------------------------
# TURBINE_ROLE
# -----------------------------------------------------------------------
drop table if exists TURBINE_ROLE;

CREATE TABLE TURBINE_ROLE
(
    ROLE_ID INTEGER NOT NULL,
    ROLE_NAME VARCHAR (99) NOT NULL,
    OBJECTDATA MEDIUMBLOB,
    PRIMARY KEY(ROLE_ID),
    UNIQUE (ROLE_NAME)
);


# -----------------------------------------------------------------------
# TURBINE_GROUP
# -----------------------------------------------------------------------
drop table if exists TURBINE_GROUP;

CREATE TABLE TURBINE_GROUP
(
    GROUP_ID INTEGER NOT NULL,
    GROUP_NAME VARCHAR (99) NOT NULL,
    OBJECTDATA MEDIUMBLOB,
    PRIMARY KEY(GROUP_ID),
    UNIQUE (GROUP_NAME)
);


# -----------------------------------------------------------------------
# TURBINE_ROLE_PERMISSION
# -----------------------------------------------------------------------
drop table if exists TURBINE_ROLE_PERMISSION;

CREATE TABLE TURBINE_ROLE_PERMISSION
(
    ROLE_ID INTEGER NOT NULL,
    PERMISSION_ID INTEGER NOT NULL,
    PRIMARY KEY(ROLE_ID,PERMISSION_ID),
    INDEX(PERMISSION_ID),
    FOREIGN KEY (ROLE_ID) REFERENCES TURBINE_ROLE (ROLE_ID),
    FOREIGN KEY (PERMISSION_ID) REFERENCES TURBINE_PERMISSION (PERMISSION_ID)
);


# -----------------------------------------------------------------------
# TURBINE_USER
# -----------------------------------------------------------------------
drop table if exists TURBINE_USER;

CREATE TABLE TURBINE_USER
(
    USER_ID INTEGER NOT NULL,
    LOGIN_NAME VARCHAR (32) NOT NULL,
    PASSWORD_VALUE VARCHAR (32) NOT NULL,
    FIRST_NAME VARCHAR (99) NOT NULL,
    LAST_NAME VARCHAR (99) NOT NULL,
    EMAIL VARCHAR (99),
    CONFIRM_VALUE VARCHAR (99),
    MODIFIED TIMESTAMP,
    CREATED TIMESTAMP,
    LAST_LOGIN TIMESTAMP,
    OBJECTDATA MEDIUMBLOB,
    PRIMARY KEY(USER_ID),
    UNIQUE (LOGIN_NAME)
);


# -----------------------------------------------------------------------
# TURBINE_USER_GROUP_ROLE
# -----------------------------------------------------------------------
drop table if exists TURBINE_USER_GROUP_ROLE;

CREATE TABLE TURBINE_USER_GROUP_ROLE
(
    USER_ID INTEGER NOT NULL,
    GROUP_ID INTEGER NOT NULL,
    ROLE_ID INTEGER NOT NULL,
    PRIMARY KEY(USER_ID,GROUP_ID,ROLE_ID),
    INDEX(GROUP_ID,ROLE_ID),
    INDEX(ROLE_ID),
    FOREIGN KEY (USER_ID) REFERENCES TURBINE_USER (USER_ID),
    FOREIGN KEY (GROUP_ID) REFERENCES TURBINE_GROUP (GROUP_ID),
    FOREIGN KEY (ROLE_ID) REFERENCES TURBINE_ROLE (ROLE_ID)
);


# -----------------------------------------------------------------------
# TURBINE_SCHEDULED_JOB
# -----------------------------------------------------------------------
drop table if exists TURBINE_SCHEDULED_JOB;

CREATE TABLE TURBINE_SCHEDULED_JOB
(
    JOB_ID INTEGER NOT NULL,
    SECOND INTEGER default -1 NOT NULL,
    MINUTE INTEGER default -1 NOT NULL,
    HOUR INTEGER default -1 NOT NULL,
    WEEK_DAY INTEGER default -1 NOT NULL,
    DAY_OF_MONTH INTEGER default -1 NOT NULL,
    TASK VARCHAR (99) NOT NULL,
    EMAIL VARCHAR (99),
    PROPERTY MEDIUMBLOB,
    PRIMARY KEY(JOB_ID)
);


