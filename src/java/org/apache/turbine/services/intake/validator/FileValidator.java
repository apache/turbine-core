package org.apache.turbine.services.intake.validator;

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

import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import org.apache.turbine.services.intake.IntakeException;

/**
 * A validator that will compare a FileItem testValue against the following
 * constraints in addition to those listed in DefaultValidator.
 *
 *
 *
 * This validator can serve as the base class for more specific validators
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:Colin.Chalmers@maxware.nl">Colin Chalmers</a>
 * @version $Id$
 */
public class FileValidator
        extends DefaultValidator
{

    /**
     *
     * Constructor
     *
     * @param paramMap a <code>Map</code> of <code>rule</code>'s
     * containing constraints on the input.
     * @exception InvalidMaskException an invalid mask was specified
     */
    public FileValidator(Map paramMap)
            throws IntakeException
    {
        init(paramMap);
    }

    /**
     * Default constructor
     */
    public FileValidator()
    {
    }

    /**
     * Determine whether a testValue meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param testValue a <code>FileItem</code> to be tested
     * @exception ValidationException containing an error message if the
     * testValue did not pass the validation tests.
     */
    public void assertValidity(FileItem testValue)
            throws ValidationException
    {
        super.assertValidity(testValue.getString());
    }
}
