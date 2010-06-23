package org.apache.turbine.services.security.ldap;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * ActiveDirectoryUser extends LDAPUser and cares for the different handling
 * of DNs in Active Directory.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: LDAPUser.java 534527 2007-05-02 16:10:59Z tv $
 */
public class ActiveDirectoryUser extends LDAPUser
{

    /** Serial Version UID */
    private static final long serialVersionUID = 3953123276619326752L;

    /**
     * Gets the distinguished name (DN) of the User the AD-way.
     * 
     * @return The Distinguished Name of the user.
     */
    public String getDN()
    {
        String userBaseSearch = LDAPSecurityConstants.getBaseSearch();
            
        StringBuffer sb = new StringBuffer();

        sb.append("CN=");
        sb.append(getFirstName()).append(' ').append(getLastName());
        sb.append(',').append(userBaseSearch);
            
        return sb.toString();
    }
}
