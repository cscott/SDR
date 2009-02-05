#!/bin/bash
exec java -cp sdr.jar:lib/antlr-3.1.1.jar:lib/jline-0.9.94.jar:lib/jdoctest/rhino-1.7r2-rc3.jar -ea:net.cscott... org.mozilla.javascript.tools.shell.Main
