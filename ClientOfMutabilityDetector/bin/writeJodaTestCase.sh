#!/bin/sh


immutable_slashed=`find . -name "*.java" -exec grep "is thread-safe and immutable" -H {} \; | awk '{print $1}' | sed -e 's/.java://' | sed -e 's/.\///' | uniq | sort`

for i in $immutable_slashed 
do
  underscored=`echo $i | sed -e 's/\//_/g'`
  dotted=`echo $i | sed -e 's/\//./g'`
  echo "@Test public void test$underscored() {"
  echo "    assertImmutable($dotted.class);" 
  echo "}"
done



