package org.apache.turbine.util.db.map;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Date;
import java.util.Hashtable;

import org.apache.torque.Torque;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.map.MapBuilder;
import org.apache.torque.map.TableMap;

/**
 * Default Builder for Database/Table/Column Maps within the Turbine
 * System.  If you decide to use your own table schema, then you
 * probably will want to implement this class on your own.  It is then
 * defined within the TurbineResources.properties file.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
public class TurbineMapBuilder implements MapBuilder
{
    /**
     * Get the User table.
     *
     * @return A String.
     */
    public String getTableUser()
    {
        return "TURBINE_USER";
    }

    /**
     * Get the UserRole table.
     *
     * @return A String.
     */
    public String getTableRole()
    {
        return "TURBINE_ROLE";
    }

    /**
     * Get the Permission table.
     *
     * @return A String.
     */
    public String getTablePermission()
    {
        return "TURBINE_PERMISSION";
    }

    /**
     * Get the UserGroupRole table.
     *
     * @return A String.
     */
    public String getTableUserGroupRole()
    {
        return "TURBINE_USER_GROUP_ROLE";
    }

    /**
     * Get the RolePermission table.
     *
     * @return A String.
     */
    public String getTableRolePermission()
    {
        return "TURBINE_ROLE_PERMISSION";
    }

    /**
     * Get the Group table.
     *
     * @return A String.
     */
    public String getTableGroup()
    {
        return "TURBINE_GROUP";
    }

    /**
     * Internal Unique key to the visitor table.  Override this if
     * using your custom table.
     *
     * @return A String.
     */
    public String getUserId()
    {
        return "USER_ID";
    }

    /**
     * Fully qualified Unique key to the visitor table.  Shouldn't
     * need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getUser_UserId()
    {
        return getTableUser() + '.' + getUserId();
    }

    /**
     * Column used to record the last login time for visitor.
     * Override this if using your custom table.
     *
     * @return A String.
     */
    public String getLastLogin()
    {
        return "LAST_LOGIN";
    }

    /**
     * Fully qualified column used to record the last login time for
     * visitor.  Shouldn't need to override this as it uses the above
     * methods.
     *
     * @return A String.
     */
    public String getUser_LastLogin()
    {
        return getTableUser() + '.' + getLastLogin();
    }

    /**
     * Column used to record the users username.  Override this if
     * using your custom table.
     *
     * @return A String.
     */
    public String getUsername()
    {
        return "LOGIN_NAME";
    }

    /**
     * Fully qualified column used to record the visitors username.
     * Shouldn't need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getUser_Username()
    {
        return getTableUser() + '.' + getUsername();
    }

    /**
     * Column used to record the users password.  Override this if
     * using your custom table.
     *
     * @return A String.
     */
    public String getPassword()
    {
        return "PASSWORD_VALUE";
    }

    /**
     * Fully qualified column used to record the visitors password.
     * Shouldn't need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getUser_Password()
    {
        return getTableUser() + '.' + getPassword();
    }

    /**
     * Column used to record general visitor data from a hashmap.
     * Override this if using your custom table.
     *
     * @return A String.
     */
    public String getObjectData()
    {
        return "OBJECTDATA";
    }

    /**
     * Fully qualified column used to record general visitor data from
     * a hashmap.  Shouldn't need to override this as it uses the
     * above methods.
     *
     * @return A String.
     */
    public String getUser_ObjectData()
    {
        return getTableUser() + '.' + getObjectData();
    }

    /**
     * Column used to store the user's first name.
     * Override this if using your custom table.
     *
     * @return A String.
     */
    public String getFirstName()
    {
        return "FIRST_NAME";
    }

    /**
     * Fully qualified column used to store the user's last name.
     * Shouldn't need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getUser_FirstName()
    {
        return getTableUser() + '.' + getFirstName();
    }

    /**
     * Column used to store the user's last name.
     * Override this if using your custom table.
     *
     * @return A String.
     */
    public String getLastName()
    {
        return "LAST_NAME";
    }

    /**
     * Fully qualified column used to store the user's last name.
     * Shouldn't need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getUser_LastName()
    {
        return getTableUser() + '.' + getLastName();
    }

    /**
     * Column used to store the user's data modification time.
     * Override this if using your custom table.
     *
     * @return A String.
     */
    public String getModified()
    {
        return "MODIFIED";
    }

    /**
     * Fully qualified column used to store the user's data modification time.
     * Shouldn't need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getUser_Modified()
    {
        return getTableUser() + '.' + getModified();
    }

    /**
     * Column used to store the user's record cration time.
     * Override this if using your custom table.
     *
     * @return A String.
     */
    public String getCreated()
    {
        return "CREATED";
    }

    /**
     * Fully qualified column used to store the user's record cration time.
     * Shouldn't need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getUser_Created()
    {
        return getTableUser() + '.' + getCreated();
    }

    /**
     * Column used to store the user's email.
     * Override this if using your custom table.
     *
     * @return A String.
     */
    public String getEmail()
    {
        return "EMAIL";
    }

    /**
     * Fully qualified column used to store the user's email.
     * Shouldn't need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getUser_Email()
    {
        return getTableUser() + '.' + getEmail();
    }

    /**
     * Column used to store the user's confirmation flag.
     * Override this if using your custom table.
     *
     * @return A String.
     */
    public String getConfirmValue()
    {
        return "CONFIRM_VALUE";
    }

    /**
     * Fully qualified column used to store the user's confirmation flag.
     * Shouldn't need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getUser_ConfirmValue()
    {
        return getTableUser() + '.' + getConfirmValue();
    }

    /**
     * Column used for the unique id to a Role.  Override this if
     * using your custom table
     *
     * @return A String.
     */
    public String getRoleId()
    {
        return "ROLE_ID";
    }

    /**
     * Fully qualified column name for Role unique key.  Shouldn't
     * need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getRole_RoleId()
    {
        return getTableRole() + '.' + getRoleId();
    }

    /**
     * Column used for the name of Role.  Override this if using
     * your custom table.
     *
     * @return A String.
     */
    public String getRoleName()
    {
        return "ROLE_NAME";
    }

    /**
     * Fully qualified column name for Role name.  Shouldn't need
     * to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getRole_Name()
    {
        return getTableRole() + '.' + getRoleName();
    }

    /**
     * Fully qualified column name for ObjectData column.  Shouldn't need
     * to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getRole_ObjectData()
    {
        return getTableRole() + '.' + getObjectData();
    }

    /**
     * Column used for the id of the Permission table.  Override this
     * if using your custom table.
     *
     * @return A String.
     */
    public String getPermissionId()
    {
        return "PERMISSION_ID";
    }

    /**
     * Fully qualified column name for Permission table unique key.
     * Shouldn't need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getPermission_PermissionId()
    {
        return getTablePermission() + '.' + getPermissionId();
    }

    /**
     * Column used for the name of a Permission.  Override this if
     * using your custom table.
     *
     * @return A String.
     */
    public String getPermissionName()
    {
        return "PERMISSION_NAME";
    }

    /**
     * Fully qualified column name for Permission table name of the
     * permission.  Shouldn't need to override this as it uses the
     * above methods.
     *
     * @return A String.
     */
    public String getPermission_Name()
    {
        return getTablePermission() + '.' + getPermissionName();
    }

    /**
     * Fully qualified column name for ObjectData column.  Shouldn't need
     * to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getPermission_ObjectData()
    {
        return getTablePermission() + '.' + getObjectData();
    }

    /**
     * Fully qualified column name for UserGroupRole visitor id.
     * Shouldn't need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getUserGroupRole_UserId()
    {
        return getTableUserGroupRole() + '.' + getUserId();
    }

    /**
     * Fully qualified column name for UserGroupRole group id.  Shouldn't
     * need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getUserGroupRole_GroupId()
    {
        return getTableUserGroupRole() + '.' + getGroupId();
    }

    /**
     * Fully qualified column name for UserGroupRole role id.  Shouldn't
     * need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getUserGroupRole_RoleId()
    {
        return getTableUserGroupRole() + '.' + getRoleId();
    }

    /**
     * Fully qualified column name for RolePermission permission id.
     * Shouldn't need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getRolePermission_PermissionId()
    {
        return getTableRolePermission() + '.' + getPermissionId();
    }

    /**
     * Fully qualified column name for RolePermission role id.
     * Shouldn't need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getRolePermission_RoleId()
    {
        return getTableRolePermission() + '.' + getRoleId();
    }

    /**
     * Column used for the id of the Group table.  Override this
     * if using your custom table.
     *
     * @return A String.
     */
    public String getGroupId()
    {
        return "GROUP_ID";
    }

    /**
     * Fully qualified column name for Group id.  Shouldn't
     * need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getGroup_GroupId()
    {
        return getTableGroup() + '.' + getGroupId();
    }

    /**
     * Column used for the name of a Group.  Override this if using
     * your custom table.
     *
     * @return A String.
     */
    public String getGroupName()
    {
        return "GROUP_NAME";
    }

    /**
     * Fully qualified column name for Group name.  Shouldn't
     * need to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getGroup_Name()
    {
        return getTableGroup() + '.' + getGroupName();
    }

    /**
     * Fully qualified column name for ObjectData column.  Shouldn't need
     * to override this as it uses the above methods.
     *
     * @return A String.
     */
    public String getGroup_ObjectData()
    {
        return getTableGroup() + '.' + getObjectData();
    }

    /**
     * GROUP_SEQUENCE.
     *
     * @return A String.
     */
    public String getSequenceGroup()
    {
        return "GROUP_SEQUENCE";
    }

    /**
     * PERMISSION_SEQUENCE.
     *
     * @return A String.
     */
    public String getSequencePermission()
    {
        return "PERMISSION_SEQUENCE";
    }

    /**
     * ROLE_SEQUENCE.
     *
     * @return A String.
     */
    public String getSequenceRole()
    {
        return "ROLE_SEQUENCE";
    }

    /**
     * USER_SEQUENCE.
     *
     * @return A String.
     */
    public String getSequenceUser()
    {
        return "USER_SEQUENCE";
    }

    /** The database map. */
    protected DatabaseMap dbMap = null;

    /**
     * Tells us if this DatabaseMapBuilder is built so that we don't
     * have to re-build it every time.
     *
     * @return True if DatabaseMapBuilder is built.
     */
    public boolean isBuilt()
    {
        return (dbMap != null);
    }

    /**
     * Gets the databasemap this map builder built.
     *
     * @return A DatabaseMap.
     */
    public DatabaseMap getDatabaseMap()
    {
        return this.dbMap;
    }

    /**
     * Build up the databasemapping.  It should probably be modified
     * to read a .xml file representation of the database to build
     * this.
     *
     * @exception Exception a generic exception.
     */
    public void doBuild()
            throws Exception
    {
        // Reusable TableMap
        TableMap tMap;

        // Make some objects.
        String string = new String("");
        Integer integer = new Integer(0);
        java.util.Date date = new Date();

        // Get default map.
        dbMap = Torque.getDatabaseMap();

        // Add tables.
        dbMap.addTable(getTableUser());
        dbMap.addTable(getTableGroup());
        dbMap.addTable(getTableRole());
        dbMap.addTable(getTablePermission());
        dbMap.addTable(getTableUserGroupRole());
        dbMap.addTable(getTableRolePermission());

        // Add User columns.
        tMap = dbMap.getTable(getTableUser());
        tMap.setPrimaryKeyMethod(TableMap.ID_BROKER);
        tMap.setPrimaryKeyMethodInfo(tMap.getName());
        tMap.addPrimaryKey(getUserId(), integer);
        tMap.addColumn(getUsername(), string);
        tMap.addColumn(getPassword(), string);
        tMap.addColumn(getFirstName(), string);
        tMap.addColumn(getLastName(), string);
        tMap.addColumn(getEmail(), string);
        tMap.addColumn(getConfirmValue(), string);
        tMap.addColumn(getCreated(), date);
        tMap.addColumn(getModified(), date);
        tMap.addColumn(getLastLogin(), date);
        tMap.addColumn(getObjectData(), new Hashtable(1));

        // Add Group columns.
        tMap = dbMap.getTable(getTableGroup());
        tMap.setPrimaryKeyMethod(TableMap.ID_BROKER);
        tMap.setPrimaryKeyMethodInfo(tMap.getName());
        tMap.addPrimaryKey(getGroupId(), integer);
        tMap.addColumn(getGroupName(), string);
        tMap.addColumn(getObjectData(), new Hashtable(1));

        // Add Role columns.
        tMap = dbMap.getTable(getTableRole());
        tMap.setPrimaryKeyMethod(TableMap.ID_BROKER);
        tMap.setPrimaryKeyMethodInfo(tMap.getName());
        tMap.addPrimaryKey(getRoleId(), integer);
        tMap.addColumn(getRoleName(), string);
        tMap.addColumn(getObjectData(), new Hashtable(1));

        // Add Permission columns.
        tMap = dbMap.getTable(getTablePermission());
        tMap.setPrimaryKeyMethod(TableMap.ID_BROKER);
        tMap.setPrimaryKeyMethodInfo(tMap.getName());
        tMap.addPrimaryKey(getPermissionId(), integer);
        tMap.addColumn(getPermissionName(), string);
        tMap.addColumn(getObjectData(), new Hashtable(1));

        // Add RolePermission columns.
        tMap = dbMap.getTable(getTableRolePermission());
        tMap.addForeignPrimaryKey(getPermissionId(),
                integer,
                getTablePermission(),
                getPermissionId());
        tMap.addForeignPrimaryKey(getRoleId(),
                integer,
                getTableRole(),
                getRoleId());

        // Add UserGroupRole columns.
        tMap = dbMap.getTable(getTableUserGroupRole());
        tMap.addForeignPrimaryKey(getUserId(),
                integer,
                getTableUser(),
                getUserId());
        tMap.addForeignPrimaryKey(getGroupId(),
                integer,
                getTableGroup(),
                getGroupId());
        tMap.addForeignPrimaryKey(getRoleId(),
                integer,
                getTableRole(),
                getRoleId());
    }
}
