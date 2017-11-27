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

Welcome to Turbine.  For more information about Turbine, please look
at the HTML documentation in the docs/ directory.

Here is a description of what each of the top level directories
contains.  Please consult the documentation in each of the lower level
directories for information that is specific to their contents.

conf/       This is where the sample configurations live.
xdocs/      This is where the documentation and database schemas live.
            All of the files in this directory are mirrored onto
            the live website.
src/        This is where all of the source code to Turbine lives.
target/     This is a temporary directory for building the project.

In order to get started with Turbine, you must build it first.
Turbine uses Maven for its build environment.  You can find installation
information about Maven online at http://maven.apache.org/ .
Once Maven has been installed, just type 'mvn package'.  The default behavior is to
compile, run the unit tests, and build the jar.  Some other useful goals:

mvn site   - generate the site documention

mvn clean site scm-publish:publish-scm    - deploy Turbine release site to the Apache web site (cft. to https://svn.apache.org/repos/asf/turbine/site how to deploy main Turbine web site)

Find more about release related command hints in 
- https://svn.apache.org/repos/asf/turbine/fulcrum/trunk/README.txt
- Update turbine.site.path in pom.xml to the new production path 
(e.g. turbine/turbine-4.0 for version 4.0) BEFORE release and the new SNAPSHOT version to the new 
development path (e.g. turbine/development/turbine-4.1 for version 4.1-SNAPSHOT) AFTER the release, 
cft. the site structure https://svn.apache.org/repos/infra/websites/production/turbine/content/turbine/.

As of Turbine 2.3, you must also have the Torque plugin for Maven installed
to build Turbine.  Information on how to install the plugin is available
at http://db.apache.org/torque/maven-howto.html.

-The Turbine Team
