#!/bin/bash
SELF=`readlink -f $0`
SELF_DIR=`pwd` #`dirname $SELF`
BENCHMARK_LIST_FILE=$1
set -x

if [ -z $BASH_EXEC ]; then
	BASH_EXEC=/bin/sh
fi

usage()
{
cat << EOF

---------------------------------------------------------------------------------
  usage: $0
      bench <benchmark list lile>
---------------------------------------------------------------------------------

EOF
}

if [ -z $BENCHMARK_LIST_FILE ]; then
	usage;
	exit 1;
fi

for LINE in `cat $BENCHMARK_LIST_FILE`; do
	if [[ $LINE =~ .*#.* ]]; then
		continue
	fi
	IFS=":"
	set -- $LINE
	CONFIG=$1
	CONFIG_SIZE=`echo $CONFIG | awk -F '-' '{print $3}'`;
	EXPERIMENT=$2
	echo "## processing experiment <$EXPERIMENT> with configuration <$CONFIG>";
	if [ -d $SELF_DIR/results/${EXPERIMENT}__${CONFIG} ]; then
		rm -rf $SELF_DIR/results/${EXPERIMENT}__${CONFIG};
	fi
	mkdir -p $SELF_DIR/results/${EXPERIMENT}__${CONFIG};
	if [[ $CONFIG =~ .*dist.* ]]; then
		echo "## starting servers";
		mkdir $SELF_DIR/results/${EXPERIMENT}__${CONFIG}/servers-logs;
		$BASH_EXEC $SELF_DIR/../servers-start.sh $SELF_DIR/configs/$CONFIG.xml $SELF_DIR/results/${EXPERIMENT}__${CONFIG}/servers-logs;
	fi

	if [[ $CONFIG =~ .*dist.* ]]; then
		COMMAND="(time $BASH_EXEC $SELF_DIR/../application/build/install/parasim/bin/parasim -e $SELF_DIR/experiments/$EXPERIMENT/benchmark.experiment.properties -b -csv $SELF_DIR/results/${EXPERIMENT}__${CONFIG}/data.csv -c $SELF_DIR/configs/$CONFIG.xml > $SELF_DIR/results/${EXPERIMENT}__${CONFIG}/log.txt) 2> $SELF_DIR/results/${EXPERIMENT}__${CONFIG}/time.txt";
	else
		LAST_PROC=$((2*CONFIG_SIZE-1))
		COMMAND="(time taskset -c 0-$LAST_PROC $BASH_EXEC $SELF_DIR/../application/build/install/parasim/bin/parasim -e $SELF_DIR/experiments/$EXPERIMENT/benchmark.experiment.properties -b -csv $SELF_DIR/results/${EXPERIMENT}__${CONFIG}/data.csv -c $SELF_DIR/configs/$CONFIG.xml > $SELF_DIR/results/${EXPERIMENT}__${CONFIG}/log.txt) 2> $SELF_DIR/results/${EXPERIMENT}__${CONFIG}/time.txt";
	fi
	echo $COMMAND > $SELF_DIR/results/${EXPERIMENT}__${CONFIG}/command
	echo "## computing";
	eval $COMMAND

	if [[ $CONFIG =~ .*dist.* ]]; then
		echo "## stopping servers";
		COMMAND="$BASH_EXEC $SELF_DIR/../servers-shutdown.sh $SELF_DIR/configs/$CONFIG.xml";
		eval $COMMAND;
	fi
done
