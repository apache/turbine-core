

                                        
/* ---------------------------------------------------------------------- */
/* TURBINE_PERMISSION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TURBINE_PERMISSION')
BEGIN
	DROP TABLE TURBINE_PERMISSION
END
;

CREATE TABLE TURBINE_PERMISSION
(
    PERMISSION_ID INT NOT NULL,
    PERMISSION_NAME VARCHAR (99) NOT NULL,
    OBJECTDATA IMAGE NULL,
    CONSTRAINT TURBINE_PERMISSION_PK PRIMARY KEY(PERMISSION_ID),
    UNIQUE (PERMISSION_NAME)
);



                                                
/* ---------------------------------------------------------------------- */
/* TURBINE_ROLE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TURBINE_ROLE')
BEGIN
	DROP TABLE TURBINE_ROLE
END
;

CREATE TABLE TURBINE_ROLE
(
    ROLE_ID INT NOT NULL,
    ROLE_NAME VARCHAR (99) NOT NULL,
    OBJECTDATA IMAGE NULL,
    CONSTRAINT TURBINE_ROLE_PK PRIMARY KEY(ROLE_ID),
    UNIQUE (ROLE_NAME)
);



                                                
/* ---------------------------------------------------------------------- */
/* TURBINE_GROUP                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TURBINE_GROUP')
BEGIN
	DROP TABLE TURBINE_GROUP
END
;

CREATE TABLE TURBINE_GROUP
(
    GROUP_ID INT NOT NULL,
    GROUP_NAME VARCHAR (99) NOT NULL,
    OBJECTDATA IMAGE NULL,
    CONSTRAINT TURBINE_GROUP_PK PRIMARY KEY(GROUP_ID),
    UNIQUE (GROUP_NAME)
);



                                                
/* ---------------------------------------------------------------------- */
/* TURBINE_ROLE_PERMISSION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TURBINE_ROLE_PERMISSION_FK_1')
    ALTER TABLE TURBINE_ROLE_PERMISSION DROP CONSTRAINT TURBINE_ROLE_PERMISSION_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TURBINE_ROLE_PERMISSION_FK_2')
    ALTER TABLE TURBINE_ROLE_PERMISSION DROP CONSTRAINT TURBINE_ROLE_PERMISSION_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TURBINE_ROLE_PERMISSION')
BEGIN
	DROP TABLE TURBINE_ROLE_PERMISSION
END
;

CREATE TABLE TURBINE_ROLE_PERMISSION
(
    ROLE_ID INT NOT NULL,
    PERMISSION_ID INT NOT NULL,
    CONSTRAINT TURBINE_ROLE_PERMISSION_PK PRIMARY KEY(ROLE_ID,PERMISSION_ID)
);

BEGIN
ALTER TABLE TURBINE_ROLE_PERMISSION
    ADD CONSTRAINT TURBINE_ROLE_PERMISSION_FK_1 FOREIGN KEY (ROLE_ID)
    REFERENCES TURBINE_ROLE (ROLE_ID)
END    
;

BEGIN
ALTER TABLE TURBINE_ROLE_PERMISSION
    ADD CONSTRAINT TURBINE_ROLE_PERMISSION_FK_2 FOREIGN KEY (PERMISSION_ID)
    REFERENCES TURBINE_PERMISSION (PERMISSION_ID)
END    
;



                                                
/* ---------------------------------------------------------------------- */
/* TURBINE_USER                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TURBINE_USER')
BEGIN
	DROP TABLE TURBINE_USER
END
;

CREATE TABLE TURBINE_USER
(
    USER_ID INT NOT NULL,
    LOGIN_NAME VARCHAR (32) NOT NULL,
    PASSWORD_VALUE VARCHAR (32) NOT NULL,
    FIRST_NAME VARCHAR (99) NOT NULL,
    LAST_NAME VARCHAR (99) NOT NULL,
    EMAIL VARCHAR (99) NULL,
    CONFIRM_VALUE VARCHAR (99) NULL,
    MODIFIED DATETIME NULL,
    CREATED DATETIME NULL,
    LAST_LOGIN DATETIME NULL,
    OBJECTDATA IMAGE NULL,
    CONSTRAINT TURBINE_USER_PK PRIMARY KEY(USER_ID),
    UNIQUE (LOGIN_NAME)
);



                                                
/* ---------------------------------------------------------------------- */
/* TURBINE_USER_GROUP_ROLE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TURBINE_USER_GROUP_ROLE_FK_1')
    ALTER TABLE TURBINE_USER_GROUP_ROLE DROP CONSTRAINT TURBINE_USER_GROUP_ROLE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TURBINE_USER_GROUP_ROLE_FK_2')
    ALTER TABLE TURBINE_USER_GROUP_ROLE DROP CONSTRAINT TURBINE_USER_GROUP_ROLE_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TURBINE_USER_GROUP_ROLE_FK_3')
    ALTER TABLE TURBINE_USER_GROUP_ROLE DROP CONSTRAINT TURBINE_USER_GROUP_ROLE_FK_3;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TURBINE_USER_GROUP_ROLE')
BEGIN
	DROP TABLE TURBINE_USER_GROUP_ROLE
END
;

CREATE TABLE TURBINE_USER_GROUP_ROLE
(
    USER_ID INT NOT NULL,
    GROUP_ID INT NOT NULL,
    ROLE_ID INT NOT NULL,
    CONSTRAINT TURBINE_USER_GROUP_ROLE_PK PRIMARY KEY(USER_ID,GROUP_ID,ROLE_ID)
);

BEGIN
ALTER TABLE TURBINE_USER_GROUP_ROLE
    ADD CONSTRAINT TURBINE_USER_GROUP_ROLE_FK_1 FOREIGN KEY (USER_ID)
    REFERENCES TURBINE_USER (USER_ID)
END    
;

BEGIN
ALTER TABLE TURBINE_USER_GROUP_ROLE
    ADD CONSTRAINT TURBINE_USER_GROUP_ROLE_FK_2 FOREIGN KEY (GROUP_ID)
    REFERENCES TURBINE_GROUP (GROUP_ID)
END    
;

BEGIN
ALTER TABLE TURBINE_USER_GROUP_ROLE
    ADD CONSTRAINT TURBINE_USER_GROUP_ROLE_FK_3 FOREIGN KEY (ROLE_ID)
    REFERENCES TURBINE_ROLE (ROLE_ID)
END    
;



                                                
/* ---------------------------------------------------------------------- */
/* TURBINE_SCHEDULED_JOB                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TURBINE_SCHEDULED_JOB')
BEGIN
	DROP TABLE TURBINE_SCHEDULED_JOB
END
;

CREATE TABLE TURBINE_SCHEDULED_JOB
(
    JOB_ID INT NOT NULL,
    SECOND INT default -1 NOT NULL,
    MINUTE INT default -1 NOT NULL,
    HOUR INT default -1 NOT NULL,
    WEEK_DAY INT default -1 NOT NULL,
    DAY_OF_MONTH INT default -1 NOT NULL,
    TASK VARCHAR (99) NOT NULL,
    EMAIL VARCHAR (99) NULL,
    PROPERTY IMAGE NULL,
    CONSTRAINT TURBINE_SCHEDULED_JOB_PK PRIMARY KEY(JOB_ID)
);



                        