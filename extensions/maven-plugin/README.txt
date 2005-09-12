=================================================
Copyright 2003-2005 The Apache Software Foundation.

Licensed under the Apache License, Version 2.0 (the "License")
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
=================================================

Maven Environment for Turbine Applications (META)

=================================================

General
*******

The Maven Environment for Turbine Application (META) is an easy way to
get started with applications based on the Turbine Webapplication
framework and also a collection of "best practices" and configuration
information to get you started with writing code using the Turbine
framework.

It consists of a plugin for the Apache Maven project
(maven.apache.org) and can be downloaded separately from the Turbine
web site or the maven central repository on ibiblio.


How it works
************

META builds an application skeleton from a set of predefined
configuration files which are part of the plugin and some user
supplied parameters. Most of the parameters have reasonable defaults,
only the application name must be supplied by the developer.

As META tries to integrate with the maven web application environment
as seamlessly as possible, it requires a few properties from other
plugins to be set correctly. These are listed below for the various
goals.

Parameters are supplied either on the command line (if you want to
rapid prototype an application, you can do so by running a simple
maven command) or with a special properties file that will be read by
the META task.

Building the plugin
*******************

If you fetched a binary release or simply want to use the plugin
available from ibiblio, you can skip this section. It is only
interesting for you if you downloaded the source code either from CVS
or bundled with the turbine distribution and want to build the plugin
yourself from source.

Doing so is simple: Go to the base directory of the source and run

maven plugin

This builds the plugin and puts it into your maven.repo.local repository.

Alternative goals are

maven plugin:deploy

for installing the unpacked plugin into your local maven installation. This
is useful if you're testing or debugging the plugin.

maven plugin:install

for packing the plugin and installing it into your local maven
installation. This jar will be expanded next time maven is run.


Plugin documentation
********************

The plugin documentation is in xdoc format and can be accessed through
the M.E.T.A. homepage at http://jakarta.apache.org/turbine/meta/ or by
building the site for the plugin by running

maven site

in the root directory. The documentation is then located in the 
target/docs subdirectory and can be accessed with a web browser.


