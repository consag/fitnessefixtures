#--
#-- Stop FitNesse
#--
#-- set INSTALLDIR to the directory in which FitNesse was installed
INSTALLDIR=/appl/testautomation/fitnesse
#--
pidfile=$INSTALLDIR/fit.pid
[[ -f $pidfile ]] && { kill `cat $pidfile` ; rm $pidfile ; }
