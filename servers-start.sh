#!/bin/bash
SELF=`readlink -f $0`
SELF_DIR=`dirname $SELF`
CONFIG_FILE=$1
PARASIM_VERSION=`grep "<version>" "$SELF_DIR/pom.xml" | head -n 1 | tr -d ' ' | sed 's/<version>//g' | sed 's/<\/version>//g'`
TARGET_DIST=$SELF_DIR/application/target/parasim-${PARASIM_VERSION}-dist.jar

usage()
{
cat << EOF

---------------------------------------------------------------------------------
  usage: $0
      [parasim config XML file]
---------------------------------------------------------------------------------

EOF
}
##
if [ "$CONFIG_FILE" == "" ]; then
	usage;
	exit 1;
fi

for IP in `grep "<value>" parasim.xml | tr '<>' ' ' | awk '{ print $2 }'`; do
	nohup ssh $IP -t "java -Dparasim.remote.host=$IP -jar $TARGET_DIST -s" > "log-$IP" &
	echo "server $IP started";
done
