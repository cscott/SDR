#!/bin/sh
JME_CVS=../../jme-dist/jme
cp ${JME_CVS}/target/*.jar ${JME_CVS}/lib/*.{so,jar,dll,jnilib,dylib} \
   ${JME_CVS}/lib/mvn-lib-install .
