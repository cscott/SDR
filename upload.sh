#!/bin/bash
# before release, change version here, as well as in build.xml and in
# src/net/cscott/sdr/Version.java
PACKAGE=sdr
VERSION=0.1
# build prerequisites
ant dist sign-jars
# make upload bundle
/bin/rm -rf ${PACKAGE}-${VERSION}
mkdir ${PACKAGE}-${VERSION}
# API docs
tar -c api | tar -C ${PACKAGE}-${VERSION} -x
mv ${PACKAGE}-${VERSION}/api ${PACKAGE}-${VERSION}/doc
# version information
touch ${PACKAGE}-${VERSION}/VERSION_${VERSION}
(date ; echo "Released $PACKAGE $VERSION" ; echo " " ; cat ChangeLog)\
                > ${PACKAGE}-${VERSION}/ChangeLog.txt
# sources & binaries
cp ${PACKAGE}-${VERSION}.tar.gz ${PACKAGE}-${VERSION}/
( cd ${PACKAGE}-${VERSION} && \
    ln -s ${PACKAGE}-${VERSION}.tar.gz ${PACKAGE}.tar.gz )
cp ${PACKAGE}.jar ${PACKAGE}-${VERSION}/
# Java web start stuff
cp sdr.jnlp ${PACKAGE}-${VERSION}/
mkdir -p ${PACKAGE}-${VERSION}/lib
cp sdr-libs.jar ${PACKAGE}-${VERSION}/lib
cp lib/jme/jnlp/*.jar ${PACKAGE}-${VERSION}/lib
# transfer to the distribution machine.
tar -c ${PACKAGE}-${VERSION}/ | \
    ssh k2.csail.mit.edu "mkdir -p public_html/Projects/SDR && cd public_html/Projects/SDR && /bin/rm -rf ${PACKAGE}-${VERSION} && tar -xv"
/bin/rm -rf ${PACKAGE}-${VERSION}
