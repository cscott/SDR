# Square Dance Revolution

[![Build Status](https://travis-ci.org/cscott/SDR.png)](https://travis-ci.org/cscott/SDR)

Square Dance Revolution is a voice-controlled square dance calling
game, in the spirit of the other "______ Revolution" games.  It can
also be used as a square dance choreography tool, and contains a
GWT web interface as well as a text UI for this purpose.

This project can be built with either Eclipse or Ant.  JDK 21 or later is
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

To build the web UI, download the [GWT SDK](https://www.gwtproject.org/)
(2.13 or later), set `gwt.sdk` in `build.properties` to its directory, and:

```
$ ant war
```

This makes `sdrweb.war`, for a servlet container which uses the
`jakarta.servlet` API, such as Tomcat 10.1.  Saved sequences are kept in
the browser's local storage (*File > Export all...* and *File > Import...*
copy them to and from a file); the server only runs the dance engine, and
stores nothing.  To serve it at `/sdr/` with Tomcat behind Apache (on
Debian or Ubuntu):

```
$ sudo apt-get install tomcat10
$ sudo cp sdrweb.war /var/lib/tomcat10/webapps/sdr.war
$ sudo a2enmod proxy_http
```

and add to the Apache site's configuration:

```
ProxyPass        /sdr/ http://localhost:8080/sdr/
ProxyPassReverse /sdr/ http://localhost:8080/sdr/
```

Tomcat also answers on port 8080 by itself; to keep it private, add
`address="127.0.0.1"` to the `Connector` in
`/etc/tomcat10/server.xml`.  To work on the web UI, `ant run.codeserver`
runs GWT's code server (Super Dev Mode) beside a servlet container.

To run the game:

```
$ ant run
```

For more information, see: http://cscott.net/Projects/SDR

 -- C. Scott Ananian
