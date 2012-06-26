#!/bin/bash
MAVEN_OPTS="-Xmx512m -XX:MaxPermSize=128m";
SCRIPT_DIR=`dirname $BASH_SOURCE`;
SCRIPT_DIR=`readlink -f $SCRIPT_DIR`;
mvn $@ -f $SCRIPT_DIR/parent/pom.xml && \
mvn $@ -Plicense,parent,util,core,model,extensions,applications -f $SCRIPT_DIR/pom.xml;
