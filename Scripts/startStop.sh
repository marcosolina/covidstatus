#!/bin/bash


COVID_FOLDER=/opt/covid/
COVID_JAR=Covidstatus.jar
COVID_NOHUP=${COVID_FOLDER}covid.log
COVID_PID_FILE=${COVID_FOLDER}covid.pid

case "$1" in
start)
   cd $COVID_FOLDER
   nohup java -jar $COVID_JAR >> $COVID_NOHUP 2>&1&
   echo $!>$COVID_PID_FILE
   chmod 775 $COVID_PID_FILE
   ;;
stop)
   kill `cat $COVID_PID_FILE`
   rm $COVID_PID_FILE
   ;;
restart)
   $0 stop
   $0 start
   ;;
status)
   if [ -e $COVID_PID_FILE ]; then
      echo Covid is running, pid=cat $COVID_PID_FILE
   else
      echo Covid is NOT running
      exit 1
   fi
   ;;
*)
   echo "Usage: $0 {start|stop|status|restart}"
esac

exit 0
