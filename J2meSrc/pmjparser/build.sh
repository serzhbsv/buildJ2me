#!/bin/sh -e
#
# This batch file builds and preverifies the code for the demos.
# it then packages them in a JAR file appropriately.
#
FontViewer=FontViewer
LIB_DIR=../../lib
CLDCAPI=${LIB_DIR}/cldcapi10.jar
MIDPAPI=${LIB_DIR}/midpapi20.jar
PREVERIFY=../../bin/preverify

PATHSEP=":"

JAVAC=javac
JAR=jar

if [ -n "${JAVA_HOME}" ] ; then
  JAVAC=${JAVA_HOME}/bin/javac
  JAR=${JAVA_HOME}/bin/jar
fi

#
# Make possible to run this script from any directory'`
#
cd `dirname $0`

echo "Creating directories..."
mkdir -p ./tmpclasses
mkdir -p ./classes
/usr/bin/unzip -o -q lib.jar -d ./classes
echo "Compiling source files..."

${JAVAC} \
    -Xlint:all \
    -bootclasspath ${CLDCAPI}${PATHSEP}${MIDPAPI}${PATHSEP}./lib.jar \
    -source 1.3 \
    -target 1.3 \
    -d ./tmpclasses \
    -classpath ./tmpclasses \
    `find  -name '*'.java`

echo "Preverifying class files..."

${PREVERIFY} \
    -classpath ${CLDCAPI}${PATHSEP}${MIDPAPI}${PATHSEP}./tmpclasses \
    -d ./classes \
    ./tmpclasses

echo "Jaring preverified class files..."
${JAR} cmf MANIFEST.MF ${FontViewer}.jar -C ./classes .

if [ -e font.pmj ] ; then
  ${JAR} uf ${FontViewer}.jar font.pmj .
fi

echo
echo "Don't forget to update the JAR file size in the JAD file!!!"
echo
