

                                        
-----------------------------------------------------------------------------
-- TURBINE_PERMISSION
-----------------------------------------------------------------------------
CREATE TABLE TURBINE_PERMISSION
(
    PERMISSION_ID integer,
    PERMISSION_NAME VARCHAR (99),
    OBJECTDATA BINARY,
    PRIMARY KEY(PERMISSION_ID),
    UNIQUE (PERMISSION_NAME)
);


                                                
-----------------------------------------------------------------------------
-- TURBINE_ROLE
-----------------------------------------------------------------------------
CREATE TABLE TURBINE_ROLE
(
    ROLE_ID integer,
    ROLE_NAME VARCHAR (99),
    OBJECTDATA BINARY,
    PRIMARY KEY(ROLE_ID),
    UNIQUE (ROLE_NAME)
);


                                                
-----------------------------------------------------------------------------
-- TURBINE_GROUP
-----------------------------------------------------------------------------
CREATE TABLE TURBINE_GROUP
(
    GROUP_ID integer,
    GROUP_NAME VARCHAR (99),
    OBJECTDATA BINARY,
    PRIMARY KEY(GROUP_ID),
    UNIQUE (GROUP_NAME)
);


                                                
-----------------------------------------------------------------------------
-- TURBINE_ROLE_PERMISSION
-----------------------------------------------------------------------------
CREATE TABLE TURBINE_ROLE_PERMISSION
(
    ROLE_ID integer,
    PERMISSION_ID integer,
    PRIMARY KEY(ROLE_ID,PERMISSION_ID)
);


                                                
-----------------------------------------------------------------------------
-- TURBINE_USER
-----------------------------------------------------------------------------
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


                                                
-----------------------------------------------------------------------------
-- TURBINE_USER_GROUP_ROLE
-----------------------------------------------------------------------------
CREATE TABLE TURBINE_USER_GROUP_ROLE
(
    USER_ID integer,
    GROUP_ID integer,
    ROLE_ID integer,
    PRIMARY KEY(USER_ID,GROUP_ID,ROLE_ID)
);


                                                
-----------------------------------------------------------------------------
-- TURBINE_SCHEDULED_JOB
-----------------------------------------------------------------------------
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


                        