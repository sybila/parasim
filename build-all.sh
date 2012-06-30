#!/bin/bash
MAVEN_OPTS="-Xmx4096m -XX:MaxPermSize=2048m";
SCRIPT_DIR=`dirname $BASH_SOURCE`;
SCRIPT_DIR=`readlink -f $SCRIPT_DIR`;
mvn $@ -Plicense,parent,util,core,model,extensions,applications -f $SCRIPT_DIR/pom.xml;
