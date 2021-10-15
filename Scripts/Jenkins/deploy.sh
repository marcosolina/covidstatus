#!/bin/bash

APP_FOLDER=/opt/covid
START_STOP_SCR=$APP_FOLDER/startStop.sh
WORK_SPACE_FOLDER=$1

mkdir -p $APP_FOLDER
cp $WORK_SPACE_FOLDER/Scripts/startStop.sh $APP_FOLDER
chmod +x $START_STOP_SCR
$START_STOP_SCR stop

sleep 20

psql -w -U postgres -f $WORK_SPACE_FOLDER/Scripts/Docker/PostgreSQL/initCovidDb.sql

mv target/covidstatus*.jar $APP_FOLDER/Covidstatus.jar
echo "" > $APP_FOLDER/covid.log
$START_STOP_SCR stop