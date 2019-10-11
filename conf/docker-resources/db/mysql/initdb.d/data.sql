-- already exists
-- CREATE DATABASE /*!32312 IF NOT EXISTS*/ `turbine`;

-- use default;

SET FOREIGN_KEY_CHECKS=0;

-- database should be creates, if MYSQL_DATABASE is provided

-- -----------------------------------------------------------------------
-- mysql SQL script for schema turbine
-- -----------------------------------------------------------------------

drop table if exists TURBINE_PERMISSION;
drop table if exists TURBINE_ROLE;
drop table if exists TURBINE_GROUP;
drop table if exists TURBINE_ROLE_PERMISSION;
drop table if exists TURBINE_USER;
drop table if exists TURBINE_USER_GROUP_ROLE;

CREATE TABLE TURBINE_PERMISSION
(
    PERMISSION_ID INTEGER NOT NULL AUTO_INCREMENT,
    PERMISSION_NAME VARCHAR(64) NOT NULL,
    PRIMARY KEY(PERMISSION_ID),
    UNIQUE TURBINE_PERMISSION_UQ_1 (PERMISSION_NAME)
);


-- -----------------------------------------------------------------------
-- TURBINE_ROLE
-- -----------------------------------------------------------------------
CREATE TABLE TURBINE_ROLE
(
    ROLE_ID INTEGER NOT NULL AUTO_INCREMENT,
    ROLE_NAME VARCHAR(64) NOT NULL,
    PRIMARY KEY(ROLE_ID),
    UNIQUE TURBINE_ROLE_UQ_1 (ROLE_NAME)
);


-- -----------------------------------------------------------------------
-- TURBINE_GROUP
-- -----------------------------------------------------------------------
CREATE TABLE TURBINE_GROUP
(
    GROUP_ID INTEGER NOT NULL AUTO_INCREMENT,
    GROUP_NAME VARCHAR(64) NOT NULL,
    PRIMARY KEY(GROUP_ID),
    UNIQUE TURBINE_GROUP_UQ_1 (GROUP_NAME)
);


-- -----------------------------------------------------------------------
-- TURBINE_ROLE_PERMISSION
-- -----------------------------------------------------------------------
CREATE TABLE TURBINE_ROLE_PERMISSION
(
    ROLE_ID INTEGER NOT NULL,
    PERMISSION_ID INTEGER NOT NULL,
    PRIMARY KEY(ROLE_ID, PERMISSION_ID)
);


-- -----------------------------------------------------------------------
-- TURBINE_USER
-- -----------------------------------------------------------------------
CREATE TABLE TURBINE_USER
(
    USER_ID INTEGER NOT NULL AUTO_INCREMENT,
    LOGIN_NAME VARCHAR(64) NOT NULL,
    PASSWORD_VALUE VARCHAR(16) NOT NULL,
    FIRST_NAME VARCHAR(64) NOT NULL,
    LAST_NAME VARCHAR(64) NOT NULL,
    EMAIL VARCHAR(64),
    CONFIRM_VALUE VARCHAR(16),
    MODIFIED_DATE DATETIME,
    CREATED DATETIME,
    LAST_LOGIN DATETIME,
    OBJECTDATA MEDIUMBLOB,
    PRIMARY KEY(USER_ID),
    UNIQUE TURBINE_USER_UQ_1 (LOGIN_NAME)
);


-- -----------------------------------------------------------------------
-- TURBINE_USER_GROUP_ROLE
-- -----------------------------------------------------------------------
CREATE TABLE TURBINE_USER_GROUP_ROLE
(
    USER_ID INTEGER NOT NULL,
    GROUP_ID INTEGER NOT NULL,
    ROLE_ID INTEGER NOT NULL,
    PRIMARY KEY(USER_ID, GROUP_ID, ROLE_ID)
);

/* ALTER TABLE TURBINE_ROLE_PERMISSION
    ADD CONSTRAINT TURBINE_ROLE_PERMISSION_FK_1
    FOREIGN KEY (ROLE_ID)
    REFERENCES TURBINE_ROLE (ROLE_ID);

ALTER TABLE TURBINE_ROLE_PERMISSION
    ADD CONSTRAINT TURBINE_ROLE_PERMISSION_FK_2
    FOREIGN KEY (PERMISSION_ID)
    REFERENCES TURBINE_PERMISSION (PERMISSION_ID);

ALTER TABLE TURBINE_USER_GROUP_ROLE
    ADD CONSTRAINT TURBINE_USER_GROUP_ROLE_FK_1
    FOREIGN KEY (USER_ID)
    REFERENCES TURBINE_USER (USER_ID);

ALTER TABLE TURBINE_USER_GROUP_ROLE
    ADD CONSTRAINT TURBINE_USER_GROUP_ROLE_FK_2
    FOREIGN KEY (GROUP_ID)
    REFERENCES TURBINE_GROUP (GROUP_ID);

ALTER TABLE TURBINE_USER_GROUP_ROLE
    ADD CONSTRAINT TURBINE_USER_GROUP_ROLE_FK_3
    FOREIGN KEY (ROLE_ID)
    REFERENCES TURBINE_ROLE (ROLE_ID);
*/

-- -----------------------------------------------------------------------
-- mysql SQL script for schema turbine
-- -----------------------------------------------------------------------


drop table if exists AUTHOR;
drop table if exists BOOK;



-- -----------------------------------------------------------------------
-- AUTHOR
-- -----------------------------------------------------------------------
CREATE TABLE AUTHOR
(
    AUTH_ID INTEGER NOT NULL,
    FIRST_NAME VARCHAR(64) NOT NULL,
    LAST_NAME VARCHAR(64) NOT NULL,
    PRIMARY KEY(AUTH_ID)
);


-- -----------------------------------------------------------------------
-- BOOK
-- -----------------------------------------------------------------------
CREATE TABLE BOOK
(
    BOOK_ID INTEGER NOT NULL,
    AUTH_ID INTEGER NOT NULL,
    TITLE VARCHAR(64) NOT NULL,
    SUBJECT VARCHAR(64) NOT NULL,
    PRIMARY KEY(BOOK_ID)
);


ALTER TABLE BOOK
    ADD CONSTRAINT BOOK_FK_1
    FOREIGN KEY (AUTH_ID)
    REFERENCES AUTHOR (AUTH_ID);


SET FOREIGN_KEY_CHECKS=0;

INSERT INTO TURBINE_USER (USER_ID,LOGIN_NAME,PASSWORD_VALUE,FIRST_NAME,LAST_NAME)
    VALUES (1,'admin','password','','Admin');

INSERT INTO TURBINE_USER (USER_ID,LOGIN_NAME,PASSWORD_VALUE,FIRST_NAME,LAST_NAME)
    VALUES (2,'user','password','','User');
    
INSERT INTO TURBINE_USER (USER_ID,LOGIN_NAME,PASSWORD_VALUE,FIRST_NAME,LAST_NAME)
    VALUES (3,'anon','nopw','','Anon');

INSERT INTO TURBINE_PERMISSION (`PERMISSION_ID`, `PERMISSION_NAME`) VALUES
(2, 'Turbine'),
(1, 'TurbineAdmin');

INSERT INTO TURBINE_ROLE (`ROLE_ID`, `ROLE_NAME`) VALUES
(1, 'turbineadmin'),
(2, 'turbineuser');

INSERT INTO TURBINE_GROUP (`GROUP_ID`, `GROUP_NAME`) VALUES
(1, 'global'),
(2, 'Turbine');

INSERT INTO TURBINE_ROLE_PERMISSION (`ROLE_ID`, `PERMISSION_ID`) VALUES
(1, 1),
(2, 2);

INSERT INTO TURBINE_USER_GROUP_ROLE (`USER_ID`, `GROUP_ID`, `ROLE_ID`) VALUES
(1, 1, 1),
(1, 2, 1),
(2, 2, 2),
(2, 1, 2);

-- 
ALTER TABLE TURBINE_USER MODIFY COLUMN USER_ID INT auto_increment;
ALTER TABLE TURBINE_PERMISSION MODIFY COLUMN PERMISSION_ID INT auto_increment;
ALTER TABLE TURBINE_ROLE MODIFY COLUMN ROLE_ID INT auto_increment;
ALTER TABLE TURBINE_GROUP MODIFY COLUMN GROUP_ID INT auto_increment;

-- mysql SQL script for schema default / turbine
-- -----------------------------------------------------------------------

SET foreign_key_checks=1;