#!/bin/bash
SELF=`readlink -f $0`
SELF_DIR=`dirname $SELF`
CONFIG_FILE=$1
LOG_DIR=$2
PARASIM_VERSION=`grep "<version>" "$SELF_DIR/pom.xml" | head -n 1 | tr -d ' ' | sed 's/<version>//g' | sed 's/<\/version>//g'`
TARGET_DIST=$SELF_DIR/application/target/parasim-${PARASIM_VERSION}-dist.jar

usage()
{
cat << EOF

---------------------------------------------------------------------------------
  usage: $0
      parasim <config XML file> <log directory>
---------------------------------------------------------------------------------

EOF
}
##
if [ -z $CONFIG_FILE ]; then
	usage;
	exit 1;
fi

if [ -z $LOG_DIR ]; then
	usage;
	exit 1;
fi

for IP in `grep "<value>" $CONFIG_FILE | tr '<>' ' ' | awk '{ print $2 }'`; do
	nohup ssh $IP -t "java -Dparasim.remote.host=$IP -jar $TARGET_DIST -s -c $CONFIG_FILE" > "$LOG_DIR/log-$IP" &
	echo "server $IP started";
done
