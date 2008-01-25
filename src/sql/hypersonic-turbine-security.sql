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
# Script to fill the tables with default roles and permissions
# Currently tested with MySQL, Oracle, Postgres and Hypersonic only.

# Create the global group
# this group is used to assign system-wide roles to users

INSERT INTO TURBINE_GROUP (GROUP_ID, GROUP_NAME) VALUES (1,'global');

# Create the root role

INSERT INTO TURBINE_ROLE (ROLE_ID, ROLE_NAME) VALUES (1, 'turbine_root');

# Create an account 'turbine' for system administartor
# Remeber to set a good password for this user in a production system!

INSERT INTO TURBINE_USER
    (USER_ID, LOGIN_NAME, PASSWORD_VALUE, FIRST_NAME, LAST_NAME)
    VALUES
    (0, 'turbine', 'turbine', 'turbine', 'turbine');

# Assign the user 'turbine' a system-wide role 'turbine_root'

INSERT INTO TURBINE_USER_GROUP_ROLE ( USER_ID, GROUP_ID, ROLE_ID )
SELECT TURBINE_USER.USER_ID, TURBINE_GROUP.GROUP_ID, TURBINE_ROLE.ROLE_ID from
TURBINE_USER, TURBINE_GROUP, TURBINE_ROLE
WHERE TURBINE_USER.LOGIN_NAME = 'turbine' AND
TURBINE_GROUP.GROUP_NAME = 'global' AND TURBINE_ROLE.ROLE_NAME = 'turbine_root';

# Add some default permissions

INSERT INTO TURBINE_PERMISSION
    (PERMISSION_ID, PERMISSION_NAME)
    VALUES
    (1, 'admin_users');

# Add some permissions for the root role

INSERT INTO TURBINE_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID)
SELECT TURBINE_ROLE.ROLE_ID, TURBINE_PERMISSION.PERMISSION_ID FROM
TURBINE_ROLE, TURBINE_PERMISSION
WHERE TURBINE_PERMISSION.PERMISSION_NAME = 'admin_users' AND
TURBINE_ROLE.ROLE_NAME = 'turbine_root';


