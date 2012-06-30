#!/bin/bash
MAVEN_OPTS="-Xmx4096m -XX:MaxPermSize=2048m";

SCRIPT_DIR=`dirname $BASH_SOURCE`;
SCRIPT_DIR=`readlink -f $SCRIPT_DIR`;

read -p 'Enter release version: ' RELEASE;
read -p 'Enter new development version: ' DEVELOPMENT;

CONF="--batch-mode -Dtag=${RELEASE} -DreleaseVersion=${RELEASE} -DdevelopmentVersion=${DEVELOPMENT} -DignoreSnapshots=false -Dbuild.all=true -DskipTests=true";

echo "Configuration: ${CONF}";
echo "MAVEN_OPTS: ${MAVEN_OPTS}";

read -p 'Press ENTER to clean...';
mvn release:clean clean ${CONF};
read -p 'Press ENTER to dry run...';
mvn release:prepare -DdryRun=true ${CONF} || exit 1;
read -p 'Press ENTER to clean...';
mvn release:clean ${CONF};
read -p 'Press ENTER to prepare...';
mvn clean release:prepare ${CONF} || exit 1;
read -p 'Press ENTER to perform...';
mvn release:perform ${CONF} || exit 1;
