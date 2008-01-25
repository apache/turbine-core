--------------------------------------------------------------------------
Copyright 2001-2004 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--------------------------------------------------------------------------

--------------------------------------------------------------------------
$Id$
Turbine Top Level README
--------------------------------------------------------------------------

Building Turbine from Source
============================

Turbine requires Apache Maven (http://maven.apache.org/) as build
tool. Please download and install maven from its download page at
http://maven.apache.org/start/download.html

As of September 2005, the official released maven version is
1.0.2. This is the version recommended to build Turbine. The current
Maven 1.1-beta2 version does build the jar, but not the site
(see http://jira.codehaus.org/browse/MAVEN-1690).

As of Turbine 2.3, you must also have the Torque plugin for Maven
installed to build Turbine. Install it using the following
plugin:download instruction:

maven -DartifactId=maven-torque-plugin -DgroupId=torque -Dversion=3.1.1 plugin:download

The turbine jar is built by running the "maven jar:jar" command in the
root directory of the Turbine source distribution.


Using JDK 1.4 or newer
----------------------

The following jars cannot be downloaded from the central maven
repository at www.ibiblio.org/maven/ due to license restrictions:

- activation 1.0.2 from http://java.sun.com/products/javabeans/glasgow/jaf.html
- javamail 1.3.3 from http://java.sun.com/products/javamail/

Please download these jars and install them into your local repository at

MAVEN_REPO_LOCAL/javax.activation/jars/activation-1.0.2.jar
MAVEN_REPO_LOCAL/javax.mail/jars/mail-1.3.3.jar


Using JDK 1.3
-------------

In addition to the jars mentioned above, you must also modify the
project.xml file shipped with Turbine which is in the root directory
of the source distribution. Open this file in an editor and search for
the "uncomment these dependencies if you are using a 1.3 JDK" line. In
between the two comment blocks, there are a number of dependencies
commented out which are required for JDK 1.3. Remove the two lines
that read "REMOVE THIS LINE".

You will also need two additional jars that cannot be downloaded from
the central maven repository at www.ibiblio.org/maven/ due to license
restrictions:

- jdbc 2.0 from http://java.sun.com/products/jdbc/
- jndi 1.2.1 from http://java.sun.com/products/jndi/

Please download these jars and install them into your local repository at

MAVEN_REPO_LOCAL/javax.sql/jars/jdbc-stdext-2.0.jar
MAVEN_REPO_LOCAL/javax.naming/jars/jndi-1.2.1.jar


Using a J2EE distribution
-------------------------

All jars mentioned above are part of J2EE distributions. Turbine
should build without any missing dependencies in a J2EE
environment. However, you might have to copy the mail.jar,
activation.jar, jndi.jar and jdbc.jar to the appropriate locations in
your local maven repository as described above.


Further documentation
=====================

The main Turbine documentation site is at the Apache Turbine Project
Homepage at http://turbine.apache.org/. You can also build the
most current documentation for this source tree by executing "maven
site" in the root directory of the source distribution after building
the jar.

