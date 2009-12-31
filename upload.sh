#!/bin/bash
# Version number is kept in build.xml.  Change it there.
PACKAGE=sdr
VERSION=$(ant echo-version | fgrep "Current version is: " | sed -e 's/^.*Current version is: //')
# build prerequisites
ant dist src-jar sign-jars || exit $?
if [ sdr-libs.jar -nt sdr-libs.jar.pack.gz ]; then
  echo "Packing..." # this saves about 4M of download (~16%)
  /bin/rm -f sdr-libs.jar.pack.gz
  pack200 -E9 -mlatest -g -G sdr-libs.jar.pack sdr-libs.jar
  gzip --rsyncable sdr-libs.jar.pack
fi
# make upload bundle
echo "Making upload bundle..."
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
cp ${PACKAGE}-${VERSION}.tar.gz ${PACKAGE}-${VERSION}/ && \
gunzip ${PACKAGE}-${VERSION}/${PACKAGE}-${VERSION}.tar.gz && \
gzip --rsyncable ${PACKAGE}-${VERSION}/${PACKAGE}-${VERSION}.tar && \
ln -s ${PACKAGE}-${VERSION}.tar.gz ${PACKAGE}-${VERSION}/${PACKAGE}.tar.gz
cp ${PACKAGE}.jar ${PACKAGE}-src.jar ${PACKAGE}-${VERSION}/
# Java web start stuff
cp sdr.jnlp ${PACKAGE}-${VERSION}/
mkdir -p ${PACKAGE}-${VERSION}/lib
#cp sdr-libs.jar ${PACKAGE}-${VERSION}/lib
cp sdr-libs.jar.pack.gz ${PACKAGE}-${VERSION}/lib
cp lib/jme/jnlp/*.jar ${PACKAGE}-${VERSION}/lib
cp resources/net/cscott/sdr/anim/splash.png \
   resources/net/cscott/sdr/icon.gif \
   ${PACKAGE}-${VERSION}/
# Magic to allow pack200 to work, courtesy of:
# http://joust.kano.net/weblog/archive/2004/10/16/pack200-on-apache-web-server/
cat > ${PACKAGE}-${VERSION}/lib/.htaccess <<EOF
# Return the right mime type for JARs
AddType application/x-java-archive .jar
# Enable type maps
AddHandler application/x-type-map .var
Options +MultiViews
# Tweak MultiViews - this line is for
# APACHE 2.0 ONLY!
MultiViewsMatch Any

<Files *.pack.gz>
  # Enable the Content-Encoding header for .jar.pack.gz files
  AddEncoding pack200-gzip .jar
  # Stop mod_gzip from messing with the Content-Encoding
  # response for these files
  RemoveEncoding .gz
</Files>
EOF
cat > ${PACKAGE}-${VERSION}/lib/sdr-libs.jar.var <<EOF
URI: sdr-libs.jar.pack.gz
Content-Type: x-java-archive
Content-Encoding: pack200-gzip
EOF
# transfer to the distribution machine.
rsync -avyz --delete-after --copy-dest=sdr-0.6 ${PACKAGE}-${VERSION} cscott.net:public_html/Projects/SDR/ && \
/bin/rm -rf ${PACKAGE}-${VERSION} && \
echo "Upload of ${PACKAGE} ${VERSION} successful."
