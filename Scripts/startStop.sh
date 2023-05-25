#!/bin/bash

APP_NAME=covidstatus
APP_FOLDER=/opt/${APP_NAME}/
APP_JAR=${APP_NAME}.jar
APP_NOHUP=${APP_FOLDER}${APP_NAME}.log
APP_PID_FILE=${APP_FOLDER}${APP_NAME}.pid

case "$1" in
start)
   cd $APP_FOLDER
   nohup java -jar $APP_JAR >> $APP_NOHUP 2>&1&
   echo $!>$APP_PID_FILE
   chmod 775 $APP_PID_FILE
   ;;
stop)
   kill `cat $APP_PID_FILE`
   rm $APP_PID_FILE
   ;;
restart)
   $0 stop
   $0 start
   ;;
status)
   if [ -e $APP_PID_FILE ]; then
      echo ${APP_NAME} is running, pid=cat $APP_PID_FILE
   else
      echo ${APP_NAME} is NOT running
      exit 1
   fi
   ;;
*)
   echo "Usage: $0 {start|stop|status|restart}"
esac

exit 0
