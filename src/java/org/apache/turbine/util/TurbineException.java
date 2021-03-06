package org.apache.turbine.util;


/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


/**
 * The base class of all exceptions thrown by Turbine.
 *
 * <p>
 * It is intended to ease the debugging by carrying on the information
 * about the exception which was caught and provoked throwing the
 * current exception. Catching and rethrowing may occur multiple
 * times, and provided that all exceptions except the first one
 * are descendands of <code>TurbineException</code>, when the
 * exception is finally printed out using any of the <code>
 * printStackTrace()</code> methods, the stacktrace will contain
 * the information about all exceptions thrown and caught on the way.
 * </p>
 *
 * <p> Running the following program </p>
 *
 * <pre>
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
 * 12      public static void a() throws TurbineException {
 * 13          try {
 * 14              b();
 * 15          } catch(Exception e) {
 * 16              throw new TurbineException("foo", e);
 * 17          }
 * 18      }
 * 19
 * 20      public static void b() throws TurbineException {
 * 21          try {
 * 22              c();
 * 23          } catch(Exception e) {
 * 24              throw new TurbineException("bar", e);
 * 25          }
 * 26      }
 * 27
 * 28      public static void c() throws TurbineException {
 * 29          throw new Exception("baz");
 * 30      }
 * 31 }
 * </pre>
 *
 * <p>Yields the following stacktrace: </p>
 *
 * <pre>
 * java.lang.Exception: baz: bar: foo
 *    at Test.c(Test.java:29)
 *    at Test.b(Test.java:22)
 * rethrown as TurbineException: bar
 *    at Test.b(Test.java:24)
 *    at Test.a(Test.java:14)
 * rethrown as TurbineException: foo
 *    at Test.a(Test.java:16)
 *    at Test.main(Test.java:6)
 * </pre>
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 */
public class TurbineException extends Exception
{
    /** Serial version */
    private static final long serialVersionUID = 6287570348053189763L;

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
        super(nested);
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
        super(msg, nested);
    }
}
