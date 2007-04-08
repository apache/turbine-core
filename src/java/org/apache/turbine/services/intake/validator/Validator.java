package org.apache.turbine.services.intake.validator;

import org.apache.turbine.services.intake.model.Field;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
 * Validator api.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id$
 */
public interface Validator
{
    /** "flexible" Rule, used in DateFormat Validator */
    String FLEXIBLE_RULE_NAME = "flexible";

    /** "format" Rule, used in DateFormat Validator */
    String FORMAT_RULE_NAME = "format";

    /** "invalidNumber" Rule, used in the various Number Validators */
    String INVALID_NUMBER_RULE_NAME = "invalidNumber";

    /** "mask" Rule, used in StringValidator */
    String MASK_RULE_NAME = "mask";

    /** "maxLength" Rule, used in all validators */
    String MAX_LENGTH_RULE_NAME = "maxLength";

    /** "maxValue" Rule, used in the various Number Validators */
    String MAX_VALUE_RULE_NAME = "maxValue";

    /** "minLength" Rule, used in all validators */
    String MIN_LENGTH_RULE_NAME = "minLength";

    /** "minValue" Rule, used in the various Number Validators */
    String MIN_VALUE_RULE_NAME = "minValue";

    /** "required" Rule, used in all validators */
    String REQUIRED_RULE_NAME = "required";

    /**
     * Determine whether a field meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param field a <code>Field</code> to be tested
     * @return true if valid, false otherwise
     */
    boolean isValid(Field field);

    /**
     * Determine whether a field meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param field a <code>Field</code> to be tested
     * @exception ValidationException containing an error message if the
     * testValue did not pass the validation tests.
     */
    void assertValidity(Field field)
            throws ValidationException;

    /**
     * Determine whether a testValue meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param testValue a <code>String</code> to be tested
     * @return true if valid, false otherwise
     * 
     * @deprecated use isValid(Field) instead
     */
    boolean isValid(String testValue);

    /**
     * Determine whether a testValue meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param testValue a <code>String</code> to be tested
     * @exception ValidationException containing an error message if the
     * testValue did not pass the validation tests.
     */
    void assertValidity(String testValue)
            throws ValidationException;

    /**
     * Get the last error message resulting from invalid input.
     *
     * @return a <code>String</code> message, or the empty String "".
     */
    String getMessage();
}
