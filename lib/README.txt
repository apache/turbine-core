--------------------------------------------------------------------------
$Id$
--------------------------------------------------------------------------

The files in this directory are here for your convenience in building
and using Turbine.

--------------------------------------------------------------------------

CORE LIBRARIES
==============
These libraries are critical to the basic Turbine functionality and are 
required no matter what optional services you use.

* activation-*.jar

  JavaBeans Activation Framework. Required by JavaMail.  Part of Java
  2 Enterprise Edition.

  http://java.sun.com/products/javabeans/glasgow/jaf.html

* ecs-*.jar

  Element Construction Set, used to generate markup (HTML, XML) from
  Java code without using print statements.

  http://java.apache.org/ecs/

* servlet-*.jar

  This is the Servlet API 2.0 or greater. We include version 2.2 of the
  Servlet API with Turbine for building purposes. It is however recommended
  that you use the Servlet API that came with your Servlet Engine for deployment
  though.

  http://jakarta.apache.org/

* mail-*.jar

  Java Mail.

  http://java.sun.com/products/javamail/index.html

* village-*.jar

  A Java interface to databases via JDBC drivers.

  http://www.working-dogs.com/village/


TEMPLATE ENGINES
================
These are the template engines supported by Turbine. Obviously, if you plan to
use a particular engine, you must have the appropriate JAR.

* freemarker-1.5.3.jar

  A templating engine. You must download this .jar file from the website 
  below and place it into the turbine/lib directory before attempting to
  build Turbine with Freemarker support.

  http://sourceforge.net/projects/freemarker/

* velocity-*.jar

  A templating engine that will soon replace WebMacro.

  http://jakarta.apache.org/velocity/

* webmacro-*.jar

  A templating engine, soon to be replaced by Velocity.

  http://www.webmacro.org/


BUILD TOOLS
===========
These libraries are used when building Turbine and its documentation. These are
not necessary for the operation of Turbine itself.

* ant-*.jar (in ../build)

  Java build tool.

  http://jakarta.apache.org/ant/

* ant-*-optional.jar (in ../build)

  Optional task definitions for Ant, including JUnit.

  http://jakarta.apache.org/ant/

* junit-*.jar

  JUnit testing framework.

  http://www.junit.org/

* stylebook-*.jar

  A tool for generating pretty html documentation from XML sources
  based on Xalan. This is an XSLT processor that generates our
  documentation from XML. There is no "official" documentation for
  this; it is a simple application of XML/XSL. The link below points
  to an explanation for this.

  http://www.mail-archive.com/turbine@list.working-dogs.com/msg04415.html

* xalan_*.jar

  An XSL processor.

  http://xml.apache.org/xalan-j/

* xerces-*.jar

  An XML parser.

  http://xml.apache.org/xerces-j/


OPTIONAL SERVICES
=================
These JARs are used in optional services and are not not necessary for the 
default operation of Turbine itself.

* castor-*.jar

  Castor RDBMS/XML persistence framework. This is an object-relational
  mapping engine.

  http://castor.exolab.org/

* jdbc-*.jar

  JDBC 2.0 Optional Package API. Required by Castor. JDBC is Java Data
  Base Conectivity. and is part of Java 2 Standard edition.

  http://java.sun.com/products/jdbc/

* jndi-*.jar

  Java Naming and Directory Interface.

  http://java.sun.com/products/jndi/index.html

* jta*.jar

  Java Transaction API. Required by Castor.

  http://java.sun.com/products/jta/index.html

* log4j*.jar

  Logging API

  http://jakarta.apache.org/log4j/

* mysql-*.jar

  MM MySQL JDBC Driver
  
  http://www.worldserver.com/mm.mysql/

* xmlrpc.jar

  XML Remote Procedure Calls: handles remote procedure calls
  implemented through the passing of XML messages.

  http://www.xmlrpc.org/
