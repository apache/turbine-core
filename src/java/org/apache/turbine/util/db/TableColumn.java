package org.apache.turbine.util.db;

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

/**
 * Models a specific column in a specific table.
 *
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @version $Id$
 * @deprecated Use <a href="http://db.apache.org/torque/">Torque</a>.
 */
public class TableColumn
{
    /**
     * The name of the database table.
     */
    protected String tableName;

    /**
     * The name of the database column.
     */
    protected String columnName;

    /**
     * The concatenation of the table name and column name separated with a
     * dot.
     */
    private String tableColumn;

    public TableColumn(String tableName, String columnName)
    {
        this.tableName = tableName;
        this.columnName = columnName;
        this.tableColumn = (tableName + '.' + columnName);
    }

    /**
     * Compares this object with another <code>TableColumn</code>.
     *
     * @param obj The object to compare to.
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        else if (obj == null)
        {
            return false;
        }
        else if (obj instanceof TableColumn)
        {
            TableColumn tc = (TableColumn) obj;
            return (tableName.equals(tc.tableName) &&
                    columnName.equals(tc.columnName));
        }
        else
        {
            return false;
        }
    }

    /**
     * The concatenation of the table name and column name separated with a
     * dot.
     *
     * @return This object's string representation.
     */
    public String toString()
    {
        return tableColumn;
    }
}
