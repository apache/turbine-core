package org.apache.turbine.util;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

/**
 * QuickSort - adapted from Doug Lea's Public Domain collection
 * library.
 *
 * @author <a href="mailto:mbryson@mindspring.com">Dave Bryson</a>
 * @version $Id$
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
