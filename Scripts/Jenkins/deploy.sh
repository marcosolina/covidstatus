#!/bin/bash


WORKSPACE_FOLDER=$1
APP_NAME=covidstatus
DEPLOY_APP_FOLDER=/opt/$APP_NAME
USR=marco
RASP_1=ixigoservices.lan

SSH_ADDRESS_1=$USR@$RASP_1

mvn clean package -f $WORKSPACE_FOLDER/pom.xml

ssh $SSH_ADDRESS_1 mkdir -p $DEPLOY_APP_FOLDER

scp $WORKSPACE_FOLDER/Scripts/startStop.sh $SSH_ADDRESS_1:$DEPLOY_APP_FOLDER
ssh $SSH_ADDRESS_1 chmod +x $DEPLOY_APP_FOLDER/startStop.sh
ssh $SSH_ADDRESS_1 $DEPLOY_APP_FOLDER/startStop.sh stop

sleep 5

#psql -w -U postgres -f $WORK_SPACE_FOLDER/Scripts/Docker/PostgreSQL/initCovidDb.sql

scp $WORKSPACE_FOLDER/target/$APP_NAME*.jar $SSH_ADDRESS_1:$DEPLOY_APP_FOLDER/$APP_NAME.jar

ssh -t -t $SSH_ADDRESS_1 << EOF
export BASH_ENV=/etc/bash.bashrc && echo "" > $DEPLOY_APP_FOLDER/$APP_NAME.log && $DEPLOY_APP_FOLDER/startStop.sh start && exit
EOF