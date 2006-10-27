#!/bin/sh
JME_CVS=../../dist/jme
cp ${JME_CVS}/target/*.jar ${JME_CVS}/lib/*.{so,jar,dll,jnilib,dylib} \
   ${JME_CVS}/lib/mvn-lib-install .
/bin/rm lwjgl_test.jar jmetest.jar jmetest-data.jar
