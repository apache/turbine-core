package org.apache.turbine.util.validation;

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
 * @version $Id$
 * @deprecated Use Intake or commons-validator
 */
public class EmailValidator extends InputValidator
{
    /**
     * default Constructor,
     */
    public EmailValidator()
    {
        super(false, NoMaxSize, EmptyArgv);
    }

    /**
     * @param String input, input to be checked
     */
    protected void check(String input)
            throws Exception
    {
        // the only two checks:
        // 1. @ must be present
        // 2. must end in '.extension'

        int idx = input.indexOf("@");
        if (idx == -1)
        {
            throw new Exception("Missing @");
        }

        idx = input.lastIndexOf(".");
        if (idx == -1)
        {
            throw new Exception("Missing .{top level domain} suffix");
        }

        String domain = input.substring(idx);
        if (domain.length() < 3)
        {
            throw new Exception("Invalid .{top level domain} suffix");
        }
    }

    /**
     * @return String, the expected format of the input
     */
    public String getExpectedFormat()
    {
        return "name@domain.topLevelDomain";
    }
}
