#!/bin/bash
java -cp resources:sdr.jar:lib/jline-4.4.8.jar:lib/dev/jdoctest/rhino-all-1.9.2.jar:lib/antlr-runtime-3.5.3.jar -ea:net.cscott... net.cscott.sdr.PMSD "$@" || ( echo "== FAILED ==" 1>&2 ; exit 1 )
