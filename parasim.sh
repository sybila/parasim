#!/bin/sh
JAVA_BIN=java
JAVA_OPT="-Xmx4096m -XX:MaxPermSize=2048m"
SELF=`readlink -f $0`
SELF_DIR=`dirname $SELF`
PARASIM_VERSION=`grep "<version>" "$SELF_DIR/pom.xml" | head -n 1 | tr -d ' ' | sed 's/<version>//g' | sed 's/<\/version>//g'`
TARGET_DIST=$SELF_DIR/application/target/parasim-${PARASIM_VERSION}-dist.jar
if [ -f $TARGET_DIST ]; then
	$JAVA_BIN $JAVA_OPT -jar $TARGET_DIST "$@"
else
	echo "Parasim has not been built. Try \`build-all install'."
	exit 1
fi
