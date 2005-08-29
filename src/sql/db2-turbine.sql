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

-- ---------------------------------------------------------------------------
-- TURBINE_PERMISSION
-- ---------------------------------------------------------------------------
drop table TURBINE_PERMISSION;


CREATE TABLE TURBINE_PERMISSION
(
    PERMISSION_ID INT NOT NULL,
    PERMISSION_NAME VARCHAR (99) NOT NULL,
    OBJECTDATA BLOB (16777215) NOT NULL,
    UNIQUE (PERMISSION_NAME)
);

ALTER TABLE TURBINE_PERMISSION
    ADD CONSTRAINT TURBINE_PERMISSION_PK
PRIMARY KEY (PERMISSION_ID);





-- ---------------------------------------------------------------------------
-- TURBINE_ROLE
-- ---------------------------------------------------------------------------
drop table TURBINE_ROLE;


CREATE TABLE TURBINE_ROLE
(
    ROLE_ID INT NOT NULL,
    ROLE_NAME VARCHAR (99) NOT NULL,
    OBJECTDATA BLOB (16777215) NOT NULL,
    UNIQUE (ROLE_NAME)
);

ALTER TABLE TURBINE_ROLE
    ADD CONSTRAINT TURBINE_ROLE_PK
PRIMARY KEY (ROLE_ID);





-- ---------------------------------------------------------------------------
-- TURBINE_GROUP
-- ---------------------------------------------------------------------------
drop table TURBINE_GROUP;


CREATE TABLE TURBINE_GROUP
(
    GROUP_ID INT NOT NULL,
    GROUP_NAME VARCHAR (99) NOT NULL,
    OBJECTDATA BLOB (16777215) NOT NULL,
    UNIQUE (GROUP_NAME)
);

ALTER TABLE TURBINE_GROUP
    ADD CONSTRAINT TURBINE_GROUP_PK
PRIMARY KEY (GROUP_ID);





-- ---------------------------------------------------------------------------
-- TURBINE_ROLE_PERMISSION
-- ---------------------------------------------------------------------------
drop table TURBINE_ROLE_PERMISSION;


CREATE TABLE TURBINE_ROLE_PERMISSION
(
    ROLE_ID INT NOT NULL,
    PERMISSION_ID INT NOT NULL
);

ALTER TABLE TURBINE_ROLE_PERMISSION
    ADD CONSTRAINT TURBINE_ROLE_PERMISSION_PK
PRIMARY KEY (ROLE_ID,PERMISSION_ID);

ALTER TABLE TURBINE_ROLE_PERMISSION
    ADD CONSTRAINT TURBINE_ROLE_PERMISSION_FK_1 FOREIGN KEY (ROLE_ID)
    REFERENCES TURBINE_ROLE (ROLE_ID)
USING INDEX;

ALTER TABLE TURBINE_ROLE_PERMISSION
    ADD CONSTRAINT TURBINE_ROLE_PERMISSION_FK_2 FOREIGN KEY (PERMISSION_ID)
    REFERENCES TURBINE_PERMISSION (PERMISSION_ID)
USING INDEX;





-- ---------------------------------------------------------------------------
-- TURBINE_USER
-- ---------------------------------------------------------------------------
drop table TURBINE_USER;


CREATE TABLE TURBINE_USER
(
    USER_ID INT NOT NULL,
    LOGIN_NAME VARCHAR (32) NOT NULL,
    PASSWORD_VALUE VARCHAR (32) NOT NULL,
    FIRST_NAME VARCHAR (99) NOT NULL,
    LAST_NAME VARCHAR (99) NOT NULL,
    EMAIL VARCHAR (99) NOT NULL,
    CONFIRM_VALUE VARCHAR (99) NOT NULL,
    MODIFIED DATE NOT NULL,
    CREATED DATE NOT NULL,
    LAST_LOGIN DATE NOT NULL,
    OBJECTDATA BLOB (16777215) NOT NULL,
    UNIQUE (LOGIN_NAME)
);

ALTER TABLE TURBINE_USER
    ADD CONSTRAINT TURBINE_USER_PK
PRIMARY KEY (USER_ID);





-- ---------------------------------------------------------------------------
-- TURBINE_USER_GROUP_ROLE
-- ---------------------------------------------------------------------------
drop table TURBINE_USER_GROUP_ROLE;


CREATE TABLE TURBINE_USER_GROUP_ROLE
(
    USER_ID INT NOT NULL,
    GROUP_ID INT NOT NULL,
    ROLE_ID INT NOT NULL
);

ALTER TABLE TURBINE_USER_GROUP_ROLE
    ADD CONSTRAINT TURBINE_USER_GROUP_ROLE_PK
PRIMARY KEY (USER_ID,GROUP_ID,ROLE_ID);

ALTER TABLE TURBINE_USER_GROUP_ROLE
    ADD CONSTRAINT TURBINE_USER_GROUP_ROLE_FK_1 FOREIGN KEY (USER_ID)
    REFERENCES TURBINE_USER (USER_ID)
USING INDEX;

ALTER TABLE TURBINE_USER_GROUP_ROLE
    ADD CONSTRAINT TURBINE_USER_GROUP_ROLE_FK_2 FOREIGN KEY (GROUP_ID)
    REFERENCES TURBINE_GROUP (GROUP_ID)
USING INDEX;

ALTER TABLE TURBINE_USER_GROUP_ROLE
    ADD CONSTRAINT TURBINE_USER_GROUP_ROLE_FK_3 FOREIGN KEY (ROLE_ID)
    REFERENCES TURBINE_ROLE (ROLE_ID)
USING INDEX;





-- ---------------------------------------------------------------------------
-- TURBINE_SCHEDULED_JOB
-- ---------------------------------------------------------------------------
drop table TURBINE_SCHEDULED_JOB;


CREATE TABLE TURBINE_SCHEDULED_JOB
(
    JOB_ID INT NOT NULL,
    SECOND INT default -1 NOT NULL,
    MINUTE INT default -1 NOT NULL,
    HOUR INT default -1 NOT NULL,
    WEEK_DAY INT default -1 NOT NULL,
    DAY_OF_MONTH INT default -1 NOT NULL,
    TASK VARCHAR (99) NOT NULL,
    EMAIL VARCHAR (99) NOT NULL,
    PROPERTY BLOB (16777215) NOT NULL
);

ALTER TABLE TURBINE_SCHEDULED_JOB
    ADD CONSTRAINT TURBINE_SCHEDULED_JOB_PK
PRIMARY KEY (JOB_ID);





