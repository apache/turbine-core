package org.apache.turbine.util;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * The base class of all exceptions thrown by Turbine.
 *
 * It is intended to ease the debugging by carrying on the information
 * about the exception which was caught and provoked throwing the
 * current exception. Catching and rethrowing may occur multiple
 * times, and provided that all exceptions except the first one
 * are descendands of <code>TurbineException</code>, when the
 * exception is finally printed out using any of the <code>
 * printStackTrace()</code> methods, the stacktrace will contain
 * the information about all exceptions thrown and caught on
 * the way.
 * <p> Running the following program
 * <p><blockquote><pre>
 *  1 import org.apache.turbine.util.TurbineException;
 *  2
 *  3 public class Test {
 *  4     public static void main( String[] args ) {
 *  5         try {
 *  6             a();
 *  7         } catch(Exception e) {
 *  8             e.printStackTrace();
 *  9         }
 * 10      }
 * 11
 * 12      public static void a() throws Exception {
 * 13          try {
 * 14              b();
 * 15          } catch(Exception e) {
 * 16              throw new TurbineException("foo", e);
 * 17          }
 * 18      }
 * 19
 * 20      public static void b() throws Exception {
 * 21          try {
 * 22              c();
 * 23          } catch(Exception e) {
 * 24              throw new TurbineException("bar", e);
 * 25          }
 * 26      }
 * 27
 * 28      public static void c() throws Exception {
 * 29          throw new Exception("baz");
 * 30      }
 * 31 }
 * </pre></blockquote>
 * <p>Yields the following stacktrace:
 * <p><blockquote><pre>
 * java.lang.Exception: baz: bar: foo
 *    at Test.c(Test.java:29)
 *    at Test.b(Test.java:22)
 * rethrown as TurbineException: bar
 *    at Test.b(Test.java:24)
 *    at Test.a(Test.java:14)
 * rethrown as TurbineException: foo
 *    at Test.a(Test.java:16)
 *    at Test.main(Test.java:6)
 * </pre></blockquote><br>
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 */
public class TurbineException extends Exception
{
    /**
     * Holds the reference to the exception or error that caused
     * this exception to be thrown.
     */
    private Throwable nested = null;

    /**
     * Constructs a new <code>TurbineException</code> without specified
     * detail message.
     */
    public TurbineException()
    {
        super();
    }

    /**
     * Constructs a new <code>TurbineException</code> with specified
     * detail message.
     *
     * @param msg The error message.
     */
    public TurbineException(String msg)
    {
        super(msg);
    }

    /**
     * Constructs a new <code>TurbineException</code> with specified
     * nested <code>Throwable</code>.
     *
     * @param nested The exception or error that caused this exception
     *               to be thrown.
     */
    public TurbineException(Throwable nested)
    {
        super();
        this.nested = nested;
    }

    /**
     * Constructs a new <code>TurbineException</code> with specified
     * detail message and nested <code>Throwable</code>.
     *
     * @param msg    The error message.
     * @param nested The exception or error that caused this exception
     *               to be thrown.
     */
    public TurbineException(String msg, Throwable nested)
    {
        super(msg);
        this.nested = nested;
    }

    /**
     * Returns the error message of this and any nested <code>Throwable</code>.
     *
     * @return The error message.
     */
    public String getMessage()
    {
        StringBuffer msg = new StringBuffer();
        String ourMsg = super.getMessage();
        if (ourMsg != null)
        {
            msg.append(ourMsg);
        }
        if (nested != null)
        {
            String nestedMsg = nested.getMessage();
            if (nestedMsg != null)
            {
                if (ourMsg != null)
                {
                    msg.append(": ");
                }
                msg.append(nestedMsg);
            }

        }
        return (msg.length() > 0 ? msg.toString() : null);
    }

    /**
     * Prints the stack trace of this exception the the standar error
     * stream.
     */
    public void printStackTrace()
    {
        synchronized (System.err)
        {
            printStackTrace(System.err);
        }
    }

    /**
     * Prints the stack trace of this exception to the specified print stream.
     *
     * @param out <code>PrintStream</code> to use for output.
     */
    public void printStackTrace(PrintStream out)
    {
        synchronized (out)
        {
            PrintWriter pw = new PrintWriter(out, false);
            printStackTrace(pw);
            // Flush the PrintWriter before it's GC'ed.
            pw.flush();
        }
    }

    /**
     * Prints the stack trace of this exception to the specified print writer.
     *
     * @param out <code>PrintWriter</code> to use for output.
     */
    public void printStackTrace(PrintWriter out)
    {
        synchronized (out)
        {
            printStackTrace(out, 0);
        }
    }

    /**
     * Prints the stack trace of this exception skiping a specified number
     * of stack frames.
     *
     * @param out  <code>PrintWriter</code> to use for output.
     * @param skip The numbere of stack frames to skip.
     */
    public void printStackTrace(PrintWriter out, int skip)
    {
        String[] st = captureStackTrace();
        if (nested != null)
        {
            if (nested instanceof TurbineException)
            {
                ((TurbineException)nested).printStackTrace(out, st.length - 2);
            }
            else if (nested instanceof TurbineRuntimeException)
            {
                ((TurbineRuntimeException)nested).printStackTrace(out, st.length - 2);
            }
            else
            {
                String[] nst = captureStackTrace(nested);
                for(int i = 0; i < nst.length - st.length + 2; i++)
                {
                    out.println(nst[i]);
                }
            }
            out.print("rethrown as ");
        }
        for (int i = 0; i < st.length - skip; i++)
        {
            out.println(st[i]);
        }
    }

    /**
     * Captures the stack trace associated with this exception.
     *
     * @return an array of Strings describing stack frames.
     */
    private String[] captureStackTrace()
    {
        StringWriter sw = new StringWriter();
        super.printStackTrace(new PrintWriter(sw, true));
        return splitStackTrace(sw.getBuffer().toString());
    }

    /**
     * Captures the stack trace associated with a <code>Throwable</code>
     * object.
     *
     * @param t The <code>Throwable</code>.
     * @return  An array of strings describing each stack frame.
     */
    private String[] captureStackTrace(Throwable t)
    {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return splitStackTrace(sw.getBuffer().toString());
    }

    /**
     * Splits the stack trace given as a newline separated string
     * into an array of stack frames.
     *
     * @param stackTrace The stack trace.
     * @return           An array of strings describing each stack frame.
     */
    private String[] splitStackTrace(String stackTrace)
    {
        String linebreak = System.getProperty("line.separator");
        StringTokenizer st = new StringTokenizer(stackTrace, linebreak);
        LinkedList list = new LinkedList();
        while (st.hasMoreTokens())
        {
            list.add(st.nextToken());
        }
        return (String []) list.toArray(new String[] {});
    }
}
