#!/bin/bash
version=20190402.0  #- initial
author="Jac. Beekers"
license="MIT"
#
##
curDir=$(dirname "$(readlink -f "$0")")
envDir=${curDir}/environment

. ${envDir}/infacmdenv.sh

#createConnection
#Usage:
#        <-DomainName|-dn> domain_name
#        <-UserName|-un> user_name
#        <-Password|-pd> password
#        [<-SecurityDomain|-sdn> security_domain]
#        [<-ResilienceTimeout|-re> timeout_period_in_seconds]
#        <-ConnectionName|-cn> connection_name
#        [<-ConnectionId|-cid> connection_id]
#        <-ConnectionType|-ct> connection_type
#        [<-ConnectionUserName|-cun> connection_user_name]
#        [<-ConnectionPassword|-cpd> connection_password]
#        [<-VendorId|-vid> vendor_id]
#        <-Options|-o> options, separated by space in the form of name=value.  Use single quote to escape any equal sign o r space in the value.
#
#Defines a connection and the connection options.

