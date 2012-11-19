#!/bin/sh
JAVA_BIN=java
SELF=`readlink -f $0`
SELF_DIR=`dirname $SELF`
TARGET_DIST=$SELF_DIR/applications/parasim-gui/target/parasim-gui-1.0.0-SNAPSHOT-dist.jar
if [ -f $TARGET_DIST ]; then
	$JAVA_BIN -jar $TARGET_DIST "$@"
else
	echo "Parasim has not been built. Try \`build-all install'."
	exit 1
fi
