#!/bin/sh

#--------------------------------------------
# No need to edit anything past here
#--------------------------------------------
if test -z "${JAVA_HOME}" ; then
    echo "ERROR: JAVA_HOME not found in your environment."
    echo "Please, set the JAVA_HOME variable in your environment to match the"
    echo "location of the Java Virtual Machine you want to use."
    exit
fi

if test -f ${JAVA_HOME}/lib/tools.jar ; then
    CLASSPATH=${CLASSPATH}:${JAVA_HOME}/lib/tools.jar
fi

# convert the existing path to unix
if [ "$OSTYPE" = "cygwin32" ] || [ "$OSTYPE" = "cygwin" ] ; then
   CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

for i in ant*.jar
do
    ANT_JAR=${ANT_JAR}:$i
done

# OSX hack to make it work with jikes
OSXHACK="/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Classes"
if [ -d ${OSXHACK} ] ; then 
for i in ${OSXHACK}/*.jar
do
    CLASSPATH=${CLASSPATH}:$i
done
fi

CLASSPATH=${CLASSPATH}:${ANT_JAR}
CLASSPATH=${CLASSPATH}:../bin/classes
CLASSPATH=${CLASSPATH}:xmlrpc.jar

for i in ../lib/*.jar
do
    CLASSPATH=$CLASSPATH:$i
done    


# convert the unix path to windows
if [ "$OSTYPE" = "cygwin32" ] || [ "$OSTYPE" = "cygwin" ] ; then
   CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi

${JAVA_HOME}/bin/java -classpath ${CLASSPATH} org.apache.turbine.util.ImportStatements "$@"
