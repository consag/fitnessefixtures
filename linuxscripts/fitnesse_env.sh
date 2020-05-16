#--
#-- FitNesse environment settings
#--
#-- set INSTALLDIR to where fitnesse is installed
umask 022
INSTALLDIR=/data/testautomation/fitnesse

## hostname
#
thisHost=$(hostname -f)
##
# user file
fitUserFile=users_fitnesse.txt
##
# SSL
fitKeyStore=${INSTALLDIR}/myfitnesse.jks
certLocation=/data/security/certs
##
# Port to be used
FitNessePort=9010

cd /data/environment/scripts 
. ./informatica_env.sh

JAVA_HOME=/data/tooling/jdk/current
export JAVA_HOME
PATH=$JAVA_HOME/bin:$PATH
export PATH
cd - 

#which java
#java -version

