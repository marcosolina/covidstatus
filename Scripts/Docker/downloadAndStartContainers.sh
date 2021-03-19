#!/bin/bash

git clone https://github.com/marcosolina/covidstatus.git

GIT_FOLDER="./covidstatus"
FOLDER_TO_KEEP="$GIT_FOLDER/Scripts"

find $GIT_FOLDER -mindepth 1 ! -regex '^'$FOLDER_TO_KEEP'\(/.*\)?' -delete
find $GIT_FOLDER -maxdepth 3 -type f -name "*.sh" -delete

docker-compose -f covidstatus/Scripts/Docker/app-and-db-docker-compose.yml up
