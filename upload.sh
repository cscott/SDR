#!/bin/bash
# Version number is kept in build.xml.  Change it there.
PACKAGE=sdr
VERSION=$(ant echo-version | fgrep "Current version is: " | sed -e 's/^.*Current version is: //')
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
gunzip ${PACKAGE}-${VERSION}/${PACKAGE}-${VERSION}.tar.gz
gzip --rsyncable ${PACKAGE}-${VERSION}/${PACKAGE}-${VERSION}.tar
( cd ${PACKAGE}-${VERSION} && \
    ln -s ${PACKAGE}-${VERSION}.tar.gz ${PACKAGE}.tar.gz )
cp ${PACKAGE}.jar ${PACKAGE}-${VERSION}/
# Java web start stuff
cp sdr.jnlp ${PACKAGE}-${VERSION}/
mkdir -p ${PACKAGE}-${VERSION}/lib
cp sdr-libs.jar ${PACKAGE}-${VERSION}/lib
cp lib/jme/jnlp/*.jar ${PACKAGE}-${VERSION}/lib
cp resources/net/cscott/sdr/anim/splash.png \
   resources/net/cscott/sdr/icon.png \
   ${PACKAGE}-${VERSION}/
# transfer to the distribution machine.
ssh k2.csail.mit.edu "mkdir -p public_html/Projects/SDR/${PACKAGE}-${VERSION}"
rsync -avz --delete ${PACKAGE}-${VERSION} k2.csail.mit.edu:public_html/Projects/SDR

/bin/rm -rf ${PACKAGE}-${VERSION}
