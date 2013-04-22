#!/bin/bash
SELF=`readlink -f $0`
SELF_DIR=`dirname $SELF`
BENCHMARK_LIST_FILE=$1

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

cat $BENCHMARK_LIST_FILE | while read LINE ;do
	IFS=":"
	set -- $LINE
	CONFIG=$1
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
	(time $BASH_EXEC $SELF_DIR/../parasim.sh -e $SELF_DIR/experiments/$EXPERIMENT/benchmark.experiment.properties -b -csv $SELF_DIR/results/${EXPERIMENT}__${CONFIG}/data.csv -c $SELF_DIR/configs/$CONFIG.xml > $SELF_DIR/results/${EXPERIMENT}__${CONFIG}/log.txt) 2> $SELF_DIR/results/${EXPERIMENT}__${CONFIG}/time.txt;
done
