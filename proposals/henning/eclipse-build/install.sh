#! /bin/sh
#
# Linux Installation
#

#
# Change this to the actual location of your
# Turbine Tree! The default should work if you've checked out
# the tree from CVS and enter the directory where this file
# is located.
#
TURBINE_PROJECT_HOME=../../..

[ -f ${TURBINE_PROJECT_HOME}/maven.xml ] || exit 1

cp eclipse_classpath ${TURBINE_PROJECT_HOME}/.classpath
cp eclipse_project   ${TURBINE_PROJECT_HOME}/.project

mkdir -p ${TURBINE_PROJECT_HOME}/.externalToolBuilders
cp eclipse_prepare_turbine "${TURBINE_PROJECT_HOME}/.externalToolBuilders/Prepare Turbine.launch"

mkdir -p ${TURBINE_PROJECT_HOME}/build/eclipse
cp eclipse_build ${TURBINE_PROJECT_HOME}/build/eclipse/build.xml

PWD=`( cd ${TURBINE_PROJECT_HOME} ; pwd )`

echo "Now import a project from ${PWD} into Eclipse!"
