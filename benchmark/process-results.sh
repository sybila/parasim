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

# generates <experiment>-summary.csv file with the following attributes:
#	- environment.name
#	- environment.size
#	- time
#	- instances
#	- spawned.hit
#	- spawned.miss

for DIR in $SELF_DIR/results/*; do
	FILE=`basename "$DIR"`
	echo "processing summary for $FILE";
	EXPERIMENT=`echo $FILE | awk -F "__" '{print $1}'`
	if [ ! -f $SELF_DIR/results-processed/$EXPERIMENT-summary.csv ]; then
		echo "environment.name, environment.size, time, instances, cache.primary.hit, cache.primary.miss, cache.secondary.hit, cache.secondary.miss, result.duplicates, result.size" > $SELF_DIR/results-processed/$EXPERIMENT-summary.csv;
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
		PRIMARY_MISS=0;
		PRIMARY_HIT=0;
		SECONDARY_MISS=0;
		SECONDARY_HIT=0;
		for SERVER_LOG in $DIR/servers-logs/*; do
			PRIMARY_HIT_LOC=`cat $SERVER_LOG | grep "spawni" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk -F "," '{sum+=$1} END {print sum}'`;
			PRIMARY_MISS_LOC=`cat $SERVER_LOG | grep "spawni" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk -F "," '{sum+=$2} END {print sum}'`;
			SECONDARY_HIT_LOC=`cat $SERVER_LOG | grep "spawni" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk -F "," '{sum+=$3} END {print sum}'`;
			SECONDARY_MISS_LOC=`cat $SERVER_LOG | grep "spawni" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk -F "," '{sum+=$4} END {print sum}'`;
			PRIMARY_MISS=$((PRIMARY_MISS + PRIMARY_MISS_LOC))
			PRIMARY_HIT=$((PRIMARY_HIT + PRIMARY_HIT_LOC))
			SECONDARY_MISS=$((SECONDARY_MISS + SECONDARY_MISS_LOC))
			SECONDARY_HIT=$((SECONDARY_HIT + SECONDARY_HIT_LOC))
		done
	else
		PRIMARY_HIT=`cat $DIR/log.txt | grep "spawni" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk -F "," '{sum+=$1} END {print sum}'`;
		PRIMARY_MISS=`cat $DIR/log.txt | grep "spawni" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk -F "," '{sum+=$2} END {print sum}'`;
		SECONDARY_HIT=`cat $DIR/log.txt | grep "spawni" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk -F "," '{sum+=$3} END {print sum}'`;
		SECONDARY_MISS=`cat $DIR/log.txt | grep "spawni" | awk -F ":" '{print $4}' | tr -d [:alpha:] | tr -d '<> ' | awk -F "," '{sum+=$4} END {print sum}'`;
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

	echo "$ENVIRONMENT, $SIZE, $TIME, $INSTANCES, $PRIMARY_HIT, $PRIMARY_MISS, $SECONDARY_HIT, $SECONDARY_MISS, $DUPLICATES, $RESULT_SIZE" >> $SELF_DIR/results-processed/$EXPERIMENT-summary.csv;
done

# generates <experiment>-iterations.csv file with the following attributes:
#	- environment.name
#	- environment.size
# 	- iteration
#	- instances
#	- primary
#	- secondary

for DIR in $SELF_DIR/results/*; do
	FILE=`basename "$DIR"`
	echo "processing iterations for $FILE";
	EXPERIMENT=`echo $FILE | awk -F "__" '{print $1}'`
	if [ ! -f $SELF_DIR/results-processed/$EXPERIMENT-iterations.csv ]; then
		echo "environment.name, environment.size, iteration, instances, primary, secondary" > $SELF_DIR/results-processed/$EXPERIMENT-iterations.csv;
	fi
	CONFIG=`echo $FILE | awk -F "__" '{print $2}'`
	ENVIRONMENT=`echo $CONFIG | awk -F "-" '{print $2}'`
	SIZE=`echo $CONFIG | awk -F "-" '{print $3}'`

	I=0
	while true; do
		I=$((I+1))
		if [[ $CONFIG =~ .*dist.* ]]; then
			ITERATIONS=0;
			PRIMARY=0;
			SECONDARY=0;
			for SERVER_LOG in $DIR/servers-logs/*; do
				ITERATIONS_LOC=`cat $SERVER_LOG | grep "iteration <$I> started with" | wc -l`;
				PRIMARY_LOC=`cat $SERVER_LOG | grep "iteration <$I> started with" | awk -F "<" '{print $2}' | awk -F ">" '{sum+=$1} END {print sum}'`;
				SECONDARY_LOC=`cat $SERVER_LOG | grep "iteration <$I> started with" | awk -F "<" '{print $3}' | awk -F ">" '{sum+=$1} END {print sum}'`;
				ITERATIONS=$((ITERATIONS + ITERATIONS_LOC))
				PRIMARY=$((PRIMARY + PRIMARY_LOC))
				SECONDARY=$((SECONDARY + SECONDARY_LOC))
			done				
		else
			ITERATIONS=`cat $DIR/log.txt | grep "iteration <$I> started with" | wc -l`
			PRIMARY=`cat $DIR/log.txt | grep "iteration <$I> started with" | awk -F "<" '{print $2}' | awk -F ">" '{sum+=$1} END {print sum}'`;
			SECONDARY=`cat $DIR/log.txt | grep "iteration <$I> started with" | awk -F "<" '{print $3}' | awk -F ">" '{sum+=$1} END {print sum}'`;
		fi			
		if [ "$ITERATIONS" == "0" ]; then
			break;
		fi
		echo "$ENVIRONMENT, $SIZE, $I, $ITERATIONS, $PRIMARY, $SECONDARY" >> $SELF_DIR/results-processed/$EXPERIMENT-iterations.csv;
	done
done
