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

/**
 * This interface marks a bean/class that can have its properties set
 * by values in a Map.
 *
 * @version $Id$
 */
public interface InitableByConstraintMap
{
    /**
     * Extract the relevant parameters from the constraints listed
     * in <input-param> tags within the intake.xml file.
     *
     * @param inputParameters a <code>Map</code> of <code>Constraint</code>'s
     * containing rules and error messages.
     * @exception InvalidMaskException one of the mask rules is invalid
     */
    void init(Map inputParameters)
            throws InvalidMaskException;
}
