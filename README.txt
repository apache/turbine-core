--------------------------------------------------------------------------
$Id$
Turbine Top Level README
--------------------------------------------------------------------------

Welcome to Turbine.  For more information about Turbine, please look
at the HTML documentation in the docs/ directory.

Here is a description of what each of the top level directories
contains.  Please consult the documentation in each of the lower level
directories for information that is specific to their contents.

bin/        This is a temporary directory for building the project.
conf/       This is where the sample configurations live.
docs/       This is where the documentation and database schemas live.
            All of the files in this directory are mirrored onto
            the live website.
lib/        This is where the additional libraries and .jar
            files reside.
src/        This is where all of the source code to Turbine lives.

In order to get started with Turbine, you must build it first.
Turbine uses Maven for its build environment.  You can find installation
information about Maven online at http://maven.apache.org/ .
Once Maven has been installed, just type 'maven'.  The default behavior is to
compile, run the unit tests, and build the jar.  Some other useful goals:

maven site:generate   - generate the site documention

As of Turbine 2.3, you must also have the Torque plugin for Maven installed
to build Turbine.  Information on how to install the plugin is available
at http://db.apache.org/torque/maven-howto.html.

During the build process, you may run into a few unsatisfied dependencies.
These particular jar files are from Sun and can not be downloaded with Maven.
You will need to download these yourself from the URL's listed below and
manually copy the jar files into your local maven repository
($MAVEN_HOME/repository).

http://java.sun.com/products/javabeans/glasgow/jaf.html
http://java.sun.com/products/javamail/
http://java.sun.com/products/jdbc/
http://java.sun.com/products/jndi/

-The Turbine Team
