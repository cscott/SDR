# Square Dance Revolution

[![Build Status](https://travis-ci.org/cscott/SDR.png)](https://travis-ci.org/cscott/SDR)

Square Dance Revolution is a voice-controlled square dance calling
game, in the spirit of the other "______ Revolution" games.  It can
also be used as a square dance choreography tool, and contains a
GWT/GAE web interface as well as a text UI for this purpose.

This project can be build with either Eclipse or Ant.  A Java 1.5 compiler is
required.

Using `ant`, build a jar from the sources:

```
$ cp sample.build.properties build.properties
<edit build.properties to set jdk.home appropriately>
$ ant jar
```

And then use the text UI (example transcripts in
`resources/net/cscott/sdr/tests`):

```
$ ./run-pmsd.sh
```

To run the web UI (in development mode):

```
$ ant run.devmode
```

To run the game:

```
$ ant run
```

For more information, see: http://cscott.net/Projects/SDR

 -- C. Scott Ananian
