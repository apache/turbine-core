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


/**
 * A constraint has a name and a value and an optional message.
 * The name/value pair will have meaning to a Validator and the
 * message will serve as an error message in the event the Validator
 * determines the constraint is violated.
 * example:
 * name="maxLength"
 * value="255"
 * message="Value cannot be longer than 255 characters."
 *
 * @deprecated Use the Fulcrum Intake component instead.
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @version $Id$
 */
public interface Constraint
{
    /**
     * Get the name of the constraint.
     */
    String getName();

    /**
     * Get the value of the constraint.
     */
    String getValue();

    /**
     * Get the error message.
     */
    String getMessage();
}
