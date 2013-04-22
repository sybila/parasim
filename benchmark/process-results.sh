#!/bin/bash
SELF=`readlink -f $0`
SELF_DIR=`dirname $SELF`

if [ -z $BASH_EXEC ]; then
	BASH_EXEC=/bin/sh
fi

if [ ! -d $SELF_DIR/results ]; then
	echo "there is no results directory!";
	exit 1;
fi

if [ -d $SELF_DIR/results-processed ]; then
	rm -rf $SELF_DIR/results-processed;
fi

mkdir $SELF_DIR/results-processed;

# generates <experiment>.csv file with the following attributes:
#	- environment.name
#	- environment.size
#	- time
#	- instances
#	- spawned.hit
#	- spawned.miss

for DIR in $SELF_DIR/results/*; do
	FILE=`basename "$DIR"`
	echo "processing $FILE";
	EXPERIMENT=`echo $FILE | awk -F "__" '{print $1}'`
	if [ ! -f $SELF_DIR/results-processed/$EXPERIMENT.csv ]; then
		echo "environment.name, environment.size, time, instances, spawned.hit, spawned.miss, result.duplicates, result.size" > $SELF_DIR/results-processed/$EXPERIMENT.csv;
	fi
	CONFIG=`echo $FILE | awk -F "__" '{print $2}'`
	ENVIRONMENT=`echo $CONFIG | awk -F "-" '{print $2}'`
	SIZE=`echo $CONFIG | awk -F "-" '{print $3}'`

	TIME=`grep real $DIR/time.txt | sed 's/real//g' | tr -d ' s'`
	SECS=`echo $TIME | awk -F "m" '{print $2}'`
	MINS=`echo $TIME | awk -F "m" '{print $1}'`
	MINS_IN_SECS=$((60 * MINS))
	TIME=`echo $MINS_IN_SECS + $SECS | bc`;

	if [[ $CONFIG =~ .*dist.* ]]; then
		INSTANCES=0
		for SERVER_LOG in $DIR/servers-logs/*; do
			ITER=`cat $SERVER_LOG | grep "started with" | wc -l`
			INSTANCES=$((ITERATIONS + ITER))
		done
	else
		INSTANCES=`cat $DIR/log.txt | grep "started with" | wc -l`
	fi
	
	if [[ $CONFIG =~ .*dist.* ]]; then	
		SPAWNED_HIT=0
		SPAWNED_MISS=0
		for SERVER_LOG in $DIR/servers-logs/*; do
			PRIMARY_MISS=`cat $SERVER_LOG | grep "spawni" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk -F "," '{sum+=$1} END {print sum}'`;
			PRIMARY_HIT=`cat $SERVER_LOG | grep "spawni" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk -F "," '{sum+=$2} END {print sum}'`;
			SECONDARY_MISS=`cat $SERVER_LOG | grep "spawni" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk -F "," '{sum+=$3} END {print sum}'`;
			SECONDARY_HIT=`cat $SERVER_LOG | grep "spawni" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk -F "," '{sum+=$4} END {print sum}'`;
			SPAWNED_HIT=$((SPAWNED_HIT + PRIMARY_HIT + SECONDARY_HIT))
			SPAWNED_MISS=$((SPAWNED_MISS + PRIMARY_MISS + SECONDARY_MISS))
		done
	else
		PRIMARY_MISS=`cat $DIR/log.txt | grep "spawni" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk -F "," '{sum+=$1} END {print sum}'`;
		PRIMARY_HIT=`cat $DIR/log.txt | grep "spawni" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk -F "," '{sum+=$2} END {print sum}'`;
		SECONDARY_MISS=`cat $DIR/log.txt | grep "spawni" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk -F "," '{sum+=$3} END {print sum}'`;
		SECONDARY_HIT=`cat $DIR/log.txt | grep "spawni" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk -F "," '{sum+=$4} END {print sum}'`;
		SPAWNED_HIT=$((PRIMARY_HIT + SECONDARY_HIT))
		SPAWNED_MISS=$((PRIMARY_MISS + SECONDARY_MISS))
	fi
	
	if [[ $CONFIG =~ .*dist.* ]]; then	
		DUPLICATES=0;
		for SERVER_LOG in $DIR/servers-logs/*; do
			DUPLICATES_LOC=`cat $SERVER_LOG | grep "result merging" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk -F "," '{sum+=$1} END {print sum}'`;
			DUPLICATES=$((DUPLICATES + DUPLICATES_LOC));
		done	
	else
		DUPLICATES=`cat $DIR/log.txt | grep "merging" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk '{sum+=$1} END {print sum}'`;
	fi

	RESULT_SIZE=`cat $DIR/data.csv | wc -l`;

	echo "$ENVIRONMENT, $SIZE, $TIME, $INSTANCES, $SPAWNED_HIT, $SPAWNED_MISS, $DUPLICATES, $RESULT_SIZE" >> $SELF_DIR/results-processed/$EXPERIMENT.csv;
done
