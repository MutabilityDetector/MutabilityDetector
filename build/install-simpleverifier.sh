#! /bin/sh
# run from ${basedir}

mvn install:install-file -Dfile=vendor/lib/main/asm-nonclassloadingsimpleverifier-1.0-SNAPSHOT.jar -DgroupId=org.mutabilitydetector -DartifactId=asm-nonclassloadingsimpleverifier -Dversion=1.0-SNAPSHOT -Dpackaging=jar