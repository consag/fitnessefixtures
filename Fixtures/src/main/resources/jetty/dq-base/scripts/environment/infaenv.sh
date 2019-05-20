#

INFA_VERSION=10.2.2
export INFA_VERSION
INFA_HOME=/appl/informatica/$INFA_VERSION
export INFA_HOME

ODBCINI=${envDir}/odbc.ini
export ODBCINI
ODBCINSTINI=${envDir}/odbcinst.ini
export ODBCINSTINI
LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${INFA_HOME}/server/bin:${INFA_HOME}/ODBC7.1/lib:${INFA_HOME}/services/shared/bin
export LD_LIBRARY_PATH
PATH=${INFA_HOME}/server/bin:${INFA_HOME}/tomcat/bin:$PATH
export PATH

export INFA_DEFAULT_DOMAIN_USER=<THE_USERID>
export INFA_DEFAULT_DOMAIN_PASSWORD=<PASSWORD_encrypted_with_pmpasswd>
export INFA_DEFAULT_SECURITY_DOMAIN=Native
export INFA_DOMAIN_NAME=DOM_Demo

