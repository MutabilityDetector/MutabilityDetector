#!/bin/sh
# Runs an analysis with the built MutabilityDetector.jar for a quick check that nothing is fundamentally broken.


MD_JAR=$1
RT_JAR=$JAVA_HOME/jre/lib/rt.jar
COMMAND="java -jar ${MD_JAR} -cp $RT_JAR"

echo "Running command: ${COMMAND}"
date
$COMMAND
date
exit 0
