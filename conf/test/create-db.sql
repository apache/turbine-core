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
alter table TURBINE_USER_GROUP_ROLE drop constraint FK576574BAF73AEE0F;
alter table TURBINE_USER_GROUP_ROLE drop constraint FK576574BA1E2E76DB;
alter table TURBINE_USER_GROUP_ROLE drop constraint FK576574BA52119584;
alter table TURBINE_ROLE_PERMISSION drop constraint FK78A122C852119584;
alter table TURBINE_ROLE_PERMISSION drop constraint FK78A122C8AAA7A58B;
drop table TURBINE_GROUP if exists;
drop table TURBINE_PERMISSION if exists;
drop table TURBINE_ROLE if exists;
drop table TURBINE_USER_GROUP_ROLE if exists;
drop table TURBINE_ROLE_PERMISSION if exists;
drop table TURBINE_USER if exists;
drop table ID_TABLE if exists;
create table TURBINE_GROUP (
   group_id BIGINT not null,
   group_name VARCHAR(64) not null,
   primary key (group_id)
);
create table TURBINE_PERMISSION (
   permission_id BIGINT not null,
   permission_name VARCHAR(64) not null,
   primary key (permission_id)
);
create table TURBINE_ROLE (
   role_id BIGINT not null,
   role_name VARCHAR(64) not null,
   primary key (role_id)
);
create table TURBINE_USER_GROUP_ROLE (
   role_id BIGINT not null,
   user_id BIGINT not null,
   group_id BIGINT not null,
   primary key (role_id, user_id, group_id)
);
create table TURBINE_ROLE_PERMISSION (
   role_id BIGINT not null,
   permission_id BIGINT not null,
   primary key (permission_id, role_id)
);
create table TURBINE_USER (
   user_id BIGINT not null,
   login_name VARCHAR(64) not null,
   password_value VARCHAR(64) not null,
   first_name VARCHAR(64) not null,
   last_name VARCHAR(64) not null,
   email VARCHAR(64),
   confirm_value VARCHAR(16),
   created TIMESTAMP,
   modified TIMESTAMP,
   last_login TIMESTAMP,
   objectdata VARBINARY(255),
   primary key (user_id)
);
alter table TURBINE_USER_GROUP_ROLE add constraint FK576574BAF73AEE0F foreign key (user_id) references turbine_user;
alter table TURBINE_USER_GROUP_ROLE add constraint FK576574BA1E2E76DB foreign key (group_id) references turbine_group;
alter table TURBINE_USER_GROUP_ROLE add constraint FK576574BA52119584 foreign key (role_id) references turbine_role;
alter table TURBINE_ROLE_PERMISSION add constraint FK78A122C852119584 foreign key (role_id) references turbine_role;
alter table TURBINE_ROLE_PERMISSION add constraint FK78A122C8AAA7A58B foreign key (permission_id) references turbine_permission;



create table ID_TABLE (
   id integer not null,
   table_name varchar (255) not null,
   next_id integer not null,
   quantity integer not null,
   primary key (id)
);

insert into ID_TABLE (id, table_name, next_id, quantity) VALUES (1000, 'TURBINE_USER', 10000, 100);
insert into ID_TABLE (id, table_name, next_id, quantity) VALUES (1001, 'TURBINE_GROUP', 10000, 100);
insert into ID_TABLE (id, table_name, next_id, quantity) VALUES (1002, 'TURBINE_ROLE', 10000, 100);
insert into ID_TABLE (id, table_name, next_id, quantity) VALUES (1003, 'TURBINE_PERMISSION', 10000, 100);
insert into ID_TABLE (id, table_name, next_id, quantity) VALUES (1004, 'TURBINE_USER_GROUP_ROLE', 10000, 100);
insert into ID_TABLE (id, table_name, next_id, quantity) VALUES (1005, 'TURBINE_ROLE_PERMISSION', 10000, 100);
insert into ID_TABLE (id, table_name, next_id, quantity) VALUES (1006, 'ID_TABLE', 10000, 100);

INSERT INTO TURBINE_USER (USER_ID,LOGIN_NAME,PASSWORD_VALUE,FIRST_NAME,LAST_NAME)
    VALUES (1,'admin','admin','Mister','Admin');


INSERT INTO TURBINE_USER (USER_ID,LOGIN_NAME,PASSWORD_VALUE,FIRST_NAME,LAST_NAME)
    VALUES (2,'user','user','Mister','User');


INSERT INTO TURBINE_PERMISSION (PERMISSION_ID,PERMISSION_NAME)
    VALUES (1,'Login');


INSERT INTO TURBINE_PERMISSION (PERMISSION_ID,PERMISSION_NAME)
    VALUES (2,'Application');


INSERT INTO TURBINE_PERMISSION (PERMISSION_ID,PERMISSION_NAME)
    VALUES (3,'Admin');


INSERT INTO TURBINE_ROLE (ROLE_ID,ROLE_NAME)
    VALUES (1,'User');


INSERT INTO TURBINE_ROLE (ROLE_ID,ROLE_NAME)
    VALUES (2,'Admin');


INSERT INTO TURBINE_GROUP (GROUP_ID,GROUP_NAME)
    VALUES (1,'global');


INSERT INTO TURBINE_GROUP (GROUP_ID,GROUP_NAME)
    VALUES (2,'Turbine');


INSERT INTO TURBINE_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID)
    VALUES (1,1);


INSERT INTO TURBINE_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID)
    VALUES (1,2);


INSERT INTO TURBINE_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID)
    VALUES (2,3);


INSERT INTO TURBINE_USER_GROUP_ROLE (USER_ID,GROUP_ID,ROLE_ID)
    VALUES (1,2,2);


INSERT INTO TURBINE_USER_GROUP_ROLE (USER_ID,GROUP_ID,ROLE_ID)
    VALUES (2,2,1);
