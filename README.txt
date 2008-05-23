--------------------------------------------------------------------------
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
--------------------------------------------------------------------------

--------------------------------------------------------------------------
$Id$
Turbine Top Level README
--------------------------------------------------------------------------

Building Turbine from Source
============================

Turbine requires Apache Maven (http://maven.apache.org/) as build
tool. Please download and install maven from its download page at
http://maven.apache.org/maven-1.x/start/download.html

As of Turbine 2.3, you must also have the Torque plugin for Maven
installed to build Turbine. Install it using the following
plugin:download instruction:

maven -DartifactId=maven-torque-plugin -DgroupId=torque -Dversion=3.3 plugin:download

The turbine jar is built by running the "maven jar:jar" command in the
root directory of the Turbine source distribution.

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
IMPORTANT NOTICE FOR USERS OF JDK 1.3. IT IS NO LONGER SUPPORTED
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

Further documentation
=====================

The main Turbine documentation site is at the Apache Turbine Project
Homepage at http://turbine.apache.org/. You can also build the
most current documentation for this source tree by executing "maven
site" in the root directory of the source distribution after building
the jar.
