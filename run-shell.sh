#!/bin/bash
D=$(dirname "$0")
if [ ! -f "$D/sdr.jar" ]; then
    echo "$0: $D/sdr.jar not found; build it with 'ant jar'" 1>&2
    exit 1
fi
exec java -cp "$D/resources:$D/sdr.jar:$D/lib/dev/jline-0.9.94.jar:$D/lib/dev/jdoctest/rhino-all-1.9.2.jar:$D/lib/antlr-runtime-3.5.3.jar" -ea:net.cscott... org.mozilla.javascript.tools.shell.Main -version 170 "$@"
