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
information about Maven online at http://jakarta.apache.org/turbine/maven.
Once Maven has been installed, just type 'ant'.  The default behavior is to compile,
run the unit tests, and build the jar.  Some other useful targets:

ant maven:iutest - run the cactus in-container tests
ant maven:docs   - generate the documention

-The Turbine Team
