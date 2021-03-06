#!/bin/bash
SELF=`readlink -f $0`
SELF_DIR=`dirname $SELF`
CONFIG_FILE=$1
set -x;

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

for IP in `grep "<value>" $CONFIG_FILE | tr '<>' ' ' | awk '{ print $2 }'`; do
	PROCESSES=`ssh $IP "ps -ux | grep parasim" | awk '{print $2}'`;
	for PROCESS in $PROCESSES; do
		ssh $IP "kill -9 $PROCESS";
	done
	echo "server $IP killed";
done
