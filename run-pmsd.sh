#!/bin/bash
java -cp resources:sdr.jar:lib/dev/jline-0.9.94.jar:lib/dev/jdoctest/rhino-1.7r2-rc3.jar:lib/antlr-runtime-3.4.jar -ea:net.cscott... net.cscott.sdr.PMSD "$@" || echo "== FAILED =="
