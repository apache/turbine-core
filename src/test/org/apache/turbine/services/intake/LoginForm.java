
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

package org.apache.turbine.services.intake;

/**
 * Test form for Intake
 *
 * @author <a href="mailto:epugh@upstate.com">epugh@upstate.com</a>
 * @version $Id$
 */
public class LoginForm
{

    private String username;
    /**
     * @return
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

}
