#!/bin/sh
# Runs an analysis with the built MutabilityDetector.jar for a quick check that nothing is fundamentally broken.


MD_JAR=$1
if [ -z "$MD_JAR" ]; then
  MD_JAR=./target/MutabilityDetector-0.9-SNAPSHOT.jar
fi

JAR_TO_ANALYSE=$2
if [ -z "$JAR_TO_ANALYSE" ]; then
  JAR_TO_ANALYSE=$JAVA_HOME/jre/lib/rt.jar
fi

COMMAND="java -jar ${MD_JAR} --verbose -cp $JAR_TO_ANALYSE"

echo "Running command: ${COMMAND}"
time $COMMAND

exit 0
