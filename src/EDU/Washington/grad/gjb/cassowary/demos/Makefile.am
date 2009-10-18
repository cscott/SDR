## $Id: Makefile.am,v 1.1 2008/08/12 22:32:47 larrymelia Exp $
## Process this file with automake to create Makefile.in

JAVAFILES = DraggableBox.java QuadDemo.java

if HAVE_JAVA
java_JAVA = $(JAVAFILES)
else
classjava.stamp:
	echo timestamp > classjava.stamp

install-javaJAVA:
	echo "Not installing java .class files since Java build was disabled"
endif

EXTRA_DIST=$(JAVAFILES) quaddemo.htm

run:
	appletviewer quaddemo.htm
