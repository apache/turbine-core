#!/bin/sh

# Little script to help me make zip and tarballs
# for the Turbine source, Torque, and the TDK.

VERSION=2.1
TARGET_DIR="/tmp/${VERSION}"

# Clean out the target directory for the dist build.

[ -d ${TARGET_DIR} ] && rm -rf ${TARGET_DIR}
mkdir -p ${TARGET_DIR}

# Do a clean build of turbine, and make sure the API
# docs are created or they won't show up in the TDK

ant clean
ant
ant javadocs
ant docs

# ------------------------------------------------------------------------
# T D K
# ------------------------------------------------------------------------

(
  cd ../../jakarta-turbine-tdk/build
  ant production-dist
  cd ../dist

  # Tar gzipped file of the TDK.
  FILE="${TARGET_DIR}/tdk-${VERSION}.tar.gz"
  [ -f ${FILE} ] && rm -f ${FILE}
  tar cvzf ${FILE} tdk

  # Zipped file of Torque.
  FILE="${TARGET_DIR}/tdk-${VERSION}.zip"
  [ -f ${FILE} ] && rm -f ${FILE}
  zip -r ${FILE} tdk
)  

# ------------------------------------------------------------------------
# T O R Q U E
# ------------------------------------------------------------------------

ant torque

(
  cd ../bin/torque
  
  # Tar gzipped file of Torque.
  FILE="${TARGET_DIR}/torque-${VERSION}.tar.gz"
  [ -f ${FILE} ] && rm -f ${FILE}
  tar cvzf ${FILE} torque
 
  # Zipped file of Torque.
  FILE="${TARGET_DIR}/torque-${VERSION}.zip"
  [ -f ${FILE} ] && rm -f ${FILE}
  zip -r ${FILE} torque
)  

# ------------------------------------------------------------------------
# T U R B I N E  S O U R C E
# ------------------------------------------------------------------------

# Get rid of all the backup files in
# the src directory.
find ../src -name '*~' -exec rm {} \;

# Get rid of any conflict files in
# the src directory.
find ../src -name '\.#*' -exec rm {} \;

(
  cd ..
  # Tar gzipped file of the source.
  FILE="${TARGET_DIR}/turbine-src-${VERSION}.tar.gz"
  [ -f ${FILE} ] && rm -f ${FILE}
  tar cvzf ${FILE} src

  # Zipped file of the source.
  FILE="${TARGET_DIR}/turbine-src-${VERSION}.zip"
  [ -f ${FILE} ] && rm -f ${FILE}
  zip -r ${FILE} src
)

# Create md5 checksums for everything
# for safety.

(
  cd ${TARGET_DIR}
  
  for i in `ls`
  do
    md5sum ${i} > ${i}.md5
  done
)
