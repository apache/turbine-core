-----------------------------------------------------------------------------
-- TURBINE_PERMISSION
-----------------------------------------------------------------------------
drop table TURBINE_PERMISSION;

CREATE TABLE TURBINE_PERMISSION
(
    PERMISSION_ID integer NOT NULL,
    PERMISSION_NAME varchar (99) NOT NULL,
    OBJECTDATA oid,
    PRIMARY KEY(PERMISSION_ID),
    UNIQUE (PERMISSION_NAME)
);


-----------------------------------------------------------------------------
-- TURBINE_ROLE
-----------------------------------------------------------------------------
drop table TURBINE_ROLE;

CREATE TABLE TURBINE_ROLE
(
    ROLE_ID integer NOT NULL,
    ROLE_NAME varchar (99) NOT NULL,
    OBJECTDATA oid,
    PRIMARY KEY(ROLE_ID),
    UNIQUE (ROLE_NAME)
);


-----------------------------------------------------------------------------
-- TURBINE_GROUP
-----------------------------------------------------------------------------
drop table TURBINE_GROUP;

CREATE TABLE TURBINE_GROUP
(
    GROUP_ID integer NOT NULL,
    GROUP_NAME varchar (99) NOT NULL,
    OBJECTDATA oid,
    PRIMARY KEY(GROUP_ID),
    UNIQUE (GROUP_NAME)
);


-----------------------------------------------------------------------------
-- TURBINE_ROLE_PERMISSION
-----------------------------------------------------------------------------
drop table TURBINE_ROLE_PERMISSION;

CREATE TABLE TURBINE_ROLE_PERMISSION
(
    ROLE_ID integer NOT NULL,
    PERMISSION_ID integer NOT NULL,
    PRIMARY KEY(ROLE_ID,PERMISSION_ID),
    FOREIGN KEY (ROLE_ID) REFERENCES TURBINE_ROLE (ROLE_ID),
    FOREIGN KEY (PERMISSION_ID) REFERENCES TURBINE_PERMISSION (PERMISSION_ID)
);


-----------------------------------------------------------------------------
-- TURBINE_USER
-----------------------------------------------------------------------------
drop table TURBINE_USER;

CREATE TABLE TURBINE_USER
(
    USER_ID integer NOT NULL,
    LOGIN_NAME varchar (32) NOT NULL,
    PASSWORD_VALUE varchar (32) NOT NULL,
    FIRST_NAME varchar (99) NOT NULL,
    LAST_NAME varchar (99) NOT NULL,
    EMAIL varchar (99),
    CONFIRM_VALUE varchar (99),
    MODIFIED timestamp,
    CREATED timestamp,
    LAST_LOGIN timestamp,
    OBJECTDATA oid,
    PRIMARY KEY(USER_ID),
    UNIQUE (LOGIN_NAME)
);


-----------------------------------------------------------------------------
-- TURBINE_USER_GROUP_ROLE
-----------------------------------------------------------------------------
drop table TURBINE_USER_GROUP_ROLE;

CREATE TABLE TURBINE_USER_GROUP_ROLE
(
    USER_ID integer NOT NULL,
    GROUP_ID integer NOT NULL,
    ROLE_ID integer NOT NULL,
    PRIMARY KEY(USER_ID,GROUP_ID,ROLE_ID),
    FOREIGN KEY (USER_ID) REFERENCES TURBINE_USER (USER_ID),
    FOREIGN KEY (GROUP_ID) REFERENCES TURBINE_GROUP (GROUP_ID),
    FOREIGN KEY (ROLE_ID) REFERENCES TURBINE_ROLE (ROLE_ID)
);


-----------------------------------------------------------------------------
-- TURBINE_SCHEDULED_JOB
-----------------------------------------------------------------------------
drop table TURBINE_SCHEDULED_JOB;

CREATE TABLE TURBINE_SCHEDULED_JOB
(
    JOB_ID integer NOT NULL,
    SECOND integer default -1 NOT NULL,
    MINUTE integer default -1 NOT NULL,
    HOUR integer default -1 NOT NULL,
    WEEK_DAY integer default -1 NOT NULL,
    DAY_OF_MONTH integer default -1 NOT NULL,
    TASK varchar (99) NOT NULL,
    EMAIL varchar (99),
    PROPERTY oid,
    PRIMARY KEY(JOB_ID)
);


