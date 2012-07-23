#!/bin/sh
JAVA_BIN=java
TARGET=$(dirname $(readlink -f $0))/applications/parasim-cli/target/parasim-cli-1.0.0-SNAPSHOT-dist.jar
if [ -f $TARGET ]; then
	$JAVA_BIN -jar $TARGET "$@"
else
	echo "Parasim has not been built. Try \`build-all install'."
	exit 1
fi
