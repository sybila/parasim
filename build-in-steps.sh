#!/bin/bash
MAVEN_OPTS="-Xmx512m -XX:MaxPermSize=128m";
SCRIPT_DIR=`dirname $BASH_SOURCE`;
SCRIPT_DIR=`readlink -f $SCRIPT_DIR`;
mvn $@ -Plicense -f $SCRIPT_DIR/pom.xml && \
mvn $@ -Pparent -f $SCRIPT_DIR/pom.xml && \
mvn $@ -Putil -f $SCRIPT_DIR/pom.xml && \
mvn $@ -Pcore -f $SCRIPT_DIR/pom.xml && \
mvn $@ -Pmodel -f $SCRIPT_DIR/pom.xml && \
mvn $@ -Pextensions -f $SCRIPT_DIR/pom.xml && \
mvn $@ -Papplications -f $SCRIPT_DIR/pom.xml;
