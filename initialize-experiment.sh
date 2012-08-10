#!/bin/bash

XML_HEADER='<?xml version="1.0" encoding="UTF-8"?>'

PROPERTIES_SUF=properties
PRECISION_SUF=precision.xml
SIMULATION_SUF=simulation_space.xml
SAMPLING_SUF=initial_sampling.xml
SPACE_SUF=initial_space.xml
RESULT_SUF=result.xml

print_help() {
cat <<EOF
This utility initializes experiment from an existing SBML file.
Given a \`experiment.sbml' or \`experiment.xml', it creates:
	experiment.$PROPERTIES_SUF
	experiment.$PRECISION_SUF
	experiment.$SIMULATION_SUF
	experiment.$SPACE_SUF
	experiment.$SAMPLING_SUF
Results are stored in \`experiment.$RESULT_SUF'.
These files are properly linked in \`experiment.properties'.
It may be, however, necessary to adjust their contents.

usage: initialize-experiment.sh [options] experiment-file

	-h
		Print this help.
	-b <name>
		Use \`name' instead of \`experiment-file' as a base for experiment files.
	-f
		force file overwrite
	-e <decimal number>
		Set maximal relative error.
	-s <decimal number>
		Set time step.
	-t <decimal number>
		Set simulation time.
	-m <decimal number>
		Set maximum species concentration value.
	-o <decimal number>
		Set simulation timeout.
	-i <natural number>
		Set simulation iteration limit.
EOF
}

BASE=
FORCE_OVERWRITE=false

REL_ERROR=0.1
TIME_STEP=0.1
TIME=60
MAX_CONC=10
TIMEOUT=480000
ITERATION_LIMIT=10

while getopts "hb:e:s:t:m:o:i:f" OPT; do
	case "$OPT" in
		h) print_help; exit 0;;
		b) BASE="$OPTARG";;
		e) REL_ERROR="$OPTARG";;
		s) TIME_STEP="$OPTARG";;
		t) TIME="$OPTARG";;
		m) MAX_CONC="$OPTARG";;
		o) TIMEOUT="$OPTARG";;
		i) ITERATION_LIMIT="$OPTARG";;
		f) FORCE_OVERWRITE=true;;
		?) echo; print_help;;
	esac
done
shift $(($OPTIND-1))

SBML_FILE="$1"

if [ -z $BASE ]; then
	BASENAME=`basename "$SBML_FILE"`
	BASE="${SBML_FILE%.*}"
fi

EXISTING=
for FILE in "$BASE.$PROPERTIES_SUF" "$BASE.$PRECISION_SUF" "$BASE.$SIMULATION_SUF" "$BASE.$SAMPLING_SUF" "$BASE.$SPACE_SUF"; do
	if [ -e "$FILE" ]; then
		if [ -f "$FILE" ]; then
			EXISTING="$EXISTING $FILE"
		else
			echo File \`$FILE\` already exists and is not a regular file. Aborting.
			exit 1
		fi
	fi
done

if [ $EXISTING ]; then
	if [ ! `$FORCE_OVERWRITE` ]; then
		echo -n "Files $EXISTING already exist. Do you wish to overwrite (Y/n)? "
		read AGREEMENT
		if [ "$AGREEMENT" != "Y" ]; then
			if [ "$AGREEMENT" != "y" ]; then
				exit 0;
			fi
		fi
	fi
fi

DIMS=`grep "<species " "$SBML_FILE" | wc -l`

#EXPERIMENT FILE
cat > "$BASE.$PROPERTIES_SUF" <<EOF
sbml.file=$SBML_FILE
stl.file=
space.initial.file=$BASE.$SPACE_SUF
space.simulation.file=$BASE.$SIMULATION_SUF
simulation.precision.file=$BASE.$PRECISION_SUF
density.sampling.file=$BASE.$SAMPLING_SUF
result.output.file=$BASE.$RESULT_SUF
timeout=$TIMEOUT
iteration.limit=$ITERATION_LIMIT
EOF

#SIMULATION SPACE TIME
cat > "$BASE.$SIMULATION_SUF" <<EOF
$XML_HEADER
<space xmlns="http://www.sybila.org/parasim/space">
	<time min="0" max="$TIME"/>
EOF
for I in `seq 1 $DIMS`; do
	echo >> "$BASE.$SIMULATION_SUF" "	<dimension min=\"0\" max=\"$MAX_CONC\"/>"
done
echo >> "$BASE.$SIMULATION_SUF" "</space>"

#PRECISION FILE
cat > "$BASE.$PRECISION_SUF" <<EOF
$XML_HEADER
<precision xmlns="http://www.sybila.org/parasim/precision" maxRelativeError="$REL_ERROR" timeStep="$TIME_STEP">
EOF
for I in `seq 1 $DIMS`; do
	echo >> "$BASE.$PRECISION_SUF" "	<dimension maxAbsoluteError=\"0\"/>"
done
echo >> "$BASE.$PRECISION_SUF" "</precision>"

#INITIAL SAMPLING FILE
cat > "$BASE.$SAMPLING_SUF" <<EOF
$XML_HEADER
<initial-sampling xmlns="http://www.sybilar.org/parasim/initial-sampling">
EOF
for I in `seq 1 $DIMS`; do
	echo >> "$BASE.$SAMPLING_SUF" "	<dimension sampling=\"1\"/>"
done
echo >> "$BASE.$SAMPLING_SUF" "</initial-sampling>"

#INITIAL SPACE FILE
cat > "$BASE.$SPACE_SUF" <<EOF
$XML_HEADER
<space xmlns="http://www.sybila.org/parasim/space">
EOF
sed -e '/<species /!d' -e 's/.*initialConcentration="\([^"]*\)".*/\t<dimension min="\1" max="\1"\/>/' $SBML_FILE >> "$BASE.$SPACE_SUF"
echo >> "$BASE.$SPACE_SUF" "</space>"
