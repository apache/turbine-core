package org.apache.turbine.util;


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
 * Used by quicksort.
 *
 * @author <a href="mailto:mbryson@mindspring.com">Dave Bryson</a>
 * @version $Id$
 * @deprecated This class will be removed after the 2.3 release. It is
 *             not part of the Web Framework scope. If you need it, please
 *             use a sorting library for quicksort.
 */
public interface Comparable
{
    /**
     * Return the result of comparing two objects.
     *
     * @param obj1 First object.
     * @param obj2 Second object.
     * @return An int.
     */
    int compare(Object obj1,
                Object obj2);
}
