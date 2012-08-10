#!/bin/sh
JAVA_BIN=java
SELF=`readlink -f $0`
SELF_DIR=`dirname $SELF`
TARGET_DIST=$SELF_DIR/applications/parasim-cli/target/parasim-cli-1.0.0-SNAPSHOT-dist.jar
TARGET_PERF=$SELF_DIR/applications/parasim-cli/target/parasim-cli-1.0.0-SNAPSHOT-performence.jar
if [ -f $TARGET_DIST ]; then
	$JAVA_BIN -jar $TARGET_DIST "$@"
else
	if [ -f $TARGET_PERF ]; then
		$JAVA_BIN -jar $TARGET_PERF "$@"
	else
		echo "Parasim has not been built. Try \`build-all install'."
		exit 1
	fi
fi
