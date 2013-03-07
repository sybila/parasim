#!/bin/sh
JAVA_BIN=java
SELF=`readlink -f $0`
SELF_DIR=`dirname $SELF`
PARASIM_VERSION=`grep "<version>" "$SELF_DIR/pom.xml" | head -n 1 | tr -d '</version> '`
TARGET_DIST=$SELF_DIR/application/target/parasim-${PARASIM_VERSION}-dist.jar
if [ -f $TARGET_DIST ]; then
	$JAVA_BIN -jar $TARGET_DIST "$@"
else
	echo "Parasim has not been built. Try \`build-all install'."
	exit 1
fi
