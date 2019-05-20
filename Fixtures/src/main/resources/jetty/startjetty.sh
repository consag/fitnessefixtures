JETTY_HOME=/appl/jetty/current
JETTY_BASE=$JETTY_HOME/dq-base
pidFile=$JETTY_HOME/jetty.pid
cd $JETTY_BASE
nohup java -jar $JETTY_HOME/start.jar & echo -n $$ >$pidFile
echo "$(date) Jetty started. Pid is >$(cat $pidFile)<"



