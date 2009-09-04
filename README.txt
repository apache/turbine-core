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
Once Maven has been installed, just type 'maven'.  The default behavior is to
compile, run the unit tests, and build the jar.  Some other useful goals:

maven site:generate   - generate the site documention

As of Turbine 2.3, you must also have the Torque plugin for Maven installed
to build Turbine.  Information on how to install the plugin is available
at http://db.apache.org/torque/maven-howto.html.

-The Turbine Team
