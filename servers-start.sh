#!/bin/bash
SELF=`readlink -f $0`
SELF_DIR=`dirname $SELF`
CONFIG_FILE=$1

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
	nohup ssh $IP -t "java -Dparasim.remote.host=$IP -jar $SELF_DIR/application/target/parasim-2.0.0-SNAPSHOT-dist.jar -s" > "log-$IP" &
	echo "server $IP started";
done
