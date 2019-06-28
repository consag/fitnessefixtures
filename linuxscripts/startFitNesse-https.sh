#--
#-- Start FitNesse
#--
#-- set INSTALLDIR to where fitnesse is installed
INSTALLDIR=/appl/testautomation/fitnesse
#-- set TESTPAGESDIR to where you want to store your test pages
TESTPAGEDIR=/appl/testautomation/testpages
#--
#nohup java -Dfile.encoding=UTF-8 -jar $INSTALLDIR/fitnesse-standalone.jar -p 9010 -e 14 -l $INSTALLDIR/log -o -f $INSTALLDIR/plugins-http.properties & fitpid=$!
## now we need to have the DataIntegration jar available due to SSL
nohup java -Dfile.encoding=UTF-8 -cp $INSTALLDIR/fitnesse-standalone.jar:lib/* fitnesseMain.FitNesseMain -p 9010 -e 14 -l $INSTALLDIR/log -o -f $INSTALLDIR/plugins-https.properties & fitpid=$!
echo "$fitpid" >$INSTALLDIR/fit.pid

