#!/bin/bash
version=20190402.0  #- initial
author="Jac. Beekers"
license="MIT"
shName=$(basename $0)
#
##
curDir=$(dirname "$(readlink -f "$0")")
envDir=${curDir}/environment

##
# set the environment
. ${envDir}/infacmdenv.sh

##
# sub processes
. ${curDir}/subs/log
. ${curDir}/subs/encryptwithpmpasswd
. ${curDir}/subs/parsearguments

##
#
procName="MAIN"
##
#Usage:
#        <-DomainName|-dn> domain_name
#        <-UserName|-un> user_name
#        <-Password|-pd> password
#        [<-SecurityDomain|-sdn> security_domain]
#        [<-ResilienceTimeout|-re> timeout_period_in_seconds]
#        <-ConnectionName|-cn> connection_name
#        [<-ConnectionUserName|-cun> connection_user_name]
#        [<-ConnectionPassword|-cpd> connection_password]
#        <-Options|-o> options, separated by space in the form of name=value.  Use single quote to escape any equal sign or space in the value.

#Updates a connection. To list connection options, run infacmd isp ListConnectionOptions.

##
parsearguments $@

encryptWithPmpasswd $targetPassword
rc=$?
procName="MAIN"
if [ $rc -ne 0 ] ; then
   log ERROR "Password encryption failed with rc >$rc< and message >$result<"
   exit $rc
fi

export INFA_DEFAULT_CONNECTION_PASSWORD="${THE_ENCRYPTED_PASSWORD}"
log INFO "Invoking updateConnection..."
infacmd.sh isp updateConnection -DomainName $INFA_DOMAIN_NAME -ConnectionName $connectionName -Options EnableParallelMode=false
rc=$?
if [ $rc -eq 0 ] ; then
   log INFO "infacmd updateconnection completed successfully."
else
   log ERROR "infacmd updateconnection failed."
fi
infacmd.sh isp ListConnectionOptions -DomainName $INFA_DOMAIN_NAME -ConnectionName $connectionName

exit $rc

