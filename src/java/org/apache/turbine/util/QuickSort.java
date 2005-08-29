package org.apache.turbine.util;

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
 * QuickSort - adapted from Doug Lea's Public Domain collection
 * library.
 *
 * @author <a href="mailto:mbryson@mindspring.com">Dave Bryson</a>
 * @version $Id$
 * @deprecated This class will be removed after the 2.3 release. It is
 *             not part of the Web Framework scope. If you need it, please
 *             use a sorting library for quicksort.
 */
public class QuickSort
{
    /**
     * Sort array of Objects using the QuickSort algorithm.
     *
     * @param s An Object[].
     * @param lo The current lower bound.
     * @param hi The current upper bound.
     * @param cmp A Comparable to compare two elements.
     */
    public static void quickSort(Object s[],
                                 int lo,
                                 int hi,
                                 Comparable cmp)
    {
        if (lo >= hi)
            return;

        /*
         * Use median-of-three(lo, mid, hi) to pick a partition.  Also
         * swap them into relative order while we are at it.
         */
        int mid = (lo + hi) / 2;

        if (cmp.compare(s[lo], s[mid]) > 0)
        {
            // Swap.
            Object tmp = s[lo];
            s[lo] = s[mid];
            s[mid] = tmp;
        }

        if (cmp.compare(s[mid], s[hi]) > 0)
        {
            // Swap .
            Object tmp = s[mid];
            s[mid] = s[hi];
            s[hi] = tmp;

            if (cmp.compare(s[lo], s[mid]) > 0)
            {
                // Swap.
                Object tmp2 = s[lo];
                s[lo] = s[mid];
                s[mid] = tmp2;
            }
        }

        // Start one past lo since already handled lo.
        int left = lo + 1;

        // Similarly, end one before hi since already handled hi.
        int right = hi - 1;

        // If there are three or fewer elements, we are done.
        if (left >= right)
            return;

        Object partition = s[mid];

        for (; ;)
        {
            while (cmp.compare(s[right], partition) > 0)
                --right;

            while (left < right &&
                    cmp.compare(s[left], partition) <= 0)
                ++left;

            if (left < right)
            {
                // Swap.
                Object tmp = s[left];
                s[left] = s[right];
                s[right] = tmp;

                --right;
            }
            else
                break;
        }
        quickSort(s, lo, left, cmp);
        quickSort(s, left + 1, hi, cmp);
    }

    /**
     * Sorts and array of objects.
     *
     * @param data An Object[].
     * @param cmp A Comparable to compare two elements.
     */
    public void sort(Object[] data,
                     Comparable cmp)
    {
        QuickSort.quickSort(data,
                0,
                data.length - 1,
                cmp);
    }
}
