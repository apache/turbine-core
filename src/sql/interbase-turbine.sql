

                                        
/* --------------------------------------------------------------------------
TURBINE_PERMISSION
-------------------------------------------------------------------------- */
CREATE TABLE TURBINE_PERMISSION
(
    "PERMISSION_ID" INTEGER NOT NULL,
    "PERMISSION_NAME" VARCHAR (99) NOT NULL,
    "OBJECTDATA" BLOB,
    PRIMARY KEY(PERMISSION_ID),
    UNIQUE (PERMISSION_NAME)
);


                                                
/* --------------------------------------------------------------------------
TURBINE_ROLE
-------------------------------------------------------------------------- */
CREATE TABLE TURBINE_ROLE
(
    "ROLE_ID" INTEGER NOT NULL,
    "ROLE_NAME" VARCHAR (99) NOT NULL,
    "OBJECTDATA" BLOB,
    PRIMARY KEY(ROLE_ID),
    UNIQUE (ROLE_NAME)
);


                                                
/* --------------------------------------------------------------------------
TURBINE_GROUP
-------------------------------------------------------------------------- */
CREATE TABLE TURBINE_GROUP
(
    "GROUP_ID" INTEGER NOT NULL,
    "GROUP_NAME" VARCHAR (99) NOT NULL,
    "OBJECTDATA" BLOB,
    PRIMARY KEY(GROUP_ID),
    UNIQUE (GROUP_NAME)
);


                                                
/* --------------------------------------------------------------------------
TURBINE_ROLE_PERMISSION
-------------------------------------------------------------------------- */
CREATE TABLE TURBINE_ROLE_PERMISSION
(
    "ROLE_ID" INTEGER NOT NULL,
    "PERMISSION_ID" INTEGER NOT NULL,
    PRIMARY KEY(ROLE_ID,PERMISSION_ID)
);


                                                
/* --------------------------------------------------------------------------
TURBINE_USER
-------------------------------------------------------------------------- */
CREATE TABLE TURBINE_USER
(
    "USER_ID" INTEGER NOT NULL,
    "LOGIN_NAME" VARCHAR (32) NOT NULL,
    "PASSWORD_VALUE" VARCHAR (32) NOT NULL,
    "FIRST_NAME" VARCHAR (99) NOT NULL,
    "LAST_NAME" VARCHAR (99) NOT NULL,
    "EMAIL" VARCHAR (99),
    "CONFIRM_VALUE" VARCHAR (99),
    "MODIFIED" timestamp,
    "CREATED" timestamp,
    "LAST_LOGIN" timestamp,
    "OBJECTDATA" BLOB,
    PRIMARY KEY(USER_ID),
    UNIQUE (LOGIN_NAME)
);


                                                
/* --------------------------------------------------------------------------
TURBINE_USER_GROUP_ROLE
-------------------------------------------------------------------------- */
CREATE TABLE TURBINE_USER_GROUP_ROLE
(
    "USER_ID" INTEGER NOT NULL,
    "GROUP_ID" INTEGER NOT NULL,
    "ROLE_ID" INTEGER NOT NULL,
    PRIMARY KEY(USER_ID,GROUP_ID,ROLE_ID)
);


                                                
/* --------------------------------------------------------------------------
TURBINE_SCHEDULED_JOB
-------------------------------------------------------------------------- */
CREATE TABLE TURBINE_SCHEDULED_JOB
(
    "JOB_ID" INTEGER NOT NULL,
    "SECOND" INTEGER default -1 NOT NULL,
    "MINUTE" INTEGER default -1 NOT NULL,
    "HOUR" INTEGER default -1 NOT NULL,
    "WEEK_DAY" INTEGER default -1 NOT NULL,
    "DAY_OF_MONTH" INTEGER default -1 NOT NULL,
    "TASK" VARCHAR (99) NOT NULL,
    "EMAIL" VARCHAR (99),
    "PROPERTY" BLOB,
    PRIMARY KEY(JOB_ID)
);


                        