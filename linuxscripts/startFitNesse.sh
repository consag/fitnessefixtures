#--
#-- Start FitNesse
#--
#-- set INSTALLDIR to where fitnesse is installed
INSTALLDIR=/appl/testautomation/fitnesse
#-- set TESTPAGESDIR to where you want to store your test pages
TESTPAGEDIR=/appl/testautomation/testpages
#--
nohup java -Dfile.encoding=UTF-8 -jar $INSTALLDIR/fitnesse-standalone.jar -p 9010 -e 14 -l $INSTALLDIR/log -o -f $INSTALLDIR/plugins.properties & fitpid=$!
echo "$fitpid" >$INSTALLDIR/fit.pid

