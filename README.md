
# Apache Turbine

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

## Building

In order to get started with Turbine, you must build it first.
Turbine uses Maven for its build environment. You can find installation
information about Maven online at http://maven.apache.org/ .

Once Maven has been installed, just type 'mvn package'. The default behavior 
is to compile, run the unit tests, and build the jar. 

Some other useful goals:

    mvn site   - generate the site documention

* test site 

    mvn site scm-publish:publish-scm -Dscmpublish.dryRun=true -Papache-release 
    
Activating Maven profile apache-release is not required, und may require a signing process (you need a gpg key).

* deploy site

    mvn clean site scm-publish:publish-scm -Papache-release    
    
* Deploys Turbine release site to the Apache web site (cft. to https://github.com/apache/turbine-site how to deploy main Turbine web site).
CAVEAT: If you make a dry run or decide to rebuild a new site when publishing, delete the cache folder to avoid that no site might be deployt!
By default this folder is user.home/turbine-sites/turbine, configured in Turbine parent property turbine.site.cache. 

### More about Releases

Find more about release related command hints in 
* https://github.com/apache/turbine-fulcrum-build/
* IMPORTANT: BEFORE running release, i.e. if the major.minor versino numbers changed, you have to update turbine.site.path in pom.xml to the new production path 
(e.g. turbine/turbine-4.0 for version 4.0) removing the  "development/"-part of the path.
* if updating the site for the new SNAPSHOT version add the new development path (e.g. turbine/development/turbine-5.1 for version 5.1-SNAPSHOT) AFTER the release.
* Find more about the site structure here: https://gitbox.apache.org/repos/asf#turbine.

As of Turbine 2.3, you must also have the Torque plugin for Maven installed
to build Turbine.  Information on how to install the plugin is available
at http://db.apache.org/torque/torque-5.0/documentation/modules/maven-plugin/index.html .

-------------

-The Turbine Team
