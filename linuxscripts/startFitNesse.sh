#--
#-- Start FitNesse
#--
#-- set INSTALLDIR to where fitnesse is installed
INSTALLDIR=/appl/testautomation/fitnesse
#-- set TESTPAGESDIR to where you want to store your test pages
TESTPAGEDIR=/appl/testautomation/testpages
#--
java -Dfile.encoding=UTF-8 -cp $INSTALLDIR/fitnesse-standalone.jar:$INSTALLDIR/f
ixtures/bin fitnesseMain.FitNesseMain -p 9010 -e 14 -l $INSTALLDIR/log -o -f $IN
STALLDIR/plugins.properties -d $TESTPAGEDIR -r TestPagesRoot &
fitpid=$!
echo "$fitpid" >$INSTALLDIR/fit.pid
