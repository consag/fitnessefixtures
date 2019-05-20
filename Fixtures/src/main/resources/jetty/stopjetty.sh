JETTY_HOME=$(pwd)
pidFile=$JETTY_HOME/jetty.pid
if [ -f "${pidFile}" ] ; then
   echo "$(date) - >$pidFile< found. Killing process..."
   kill $(cat $pidFile)
   rc=$?
   echo "$(date) - Kill completed with rc=>$rc<." 
else
   echo "$(date) - Cannot find Jetty pid file >$pidFile<."
   echo "$(date) = Is Jetty running?"
   ps -ef | grep start.jar | grep -v grep
fi
rm -rf $pidFile


