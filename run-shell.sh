#!/bin/bash
exec java -cp resources:sdr.jar:lib/dev/jline-0.9.94.jar:lib/dev/jdoctest/rhino-1.7r5pre.jar:lib/antlr-runtime-3.4.jar -ea:net.cscott... org.mozilla.javascript.tools.shell.Main -version 170 "$@"
