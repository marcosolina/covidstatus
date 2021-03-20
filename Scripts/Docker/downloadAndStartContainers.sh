#!/bin/bash

# find ./myfolder -mindepth 1 ! -regex '^./myfolder/test2\(/.*\)?' -delete

# How it works
# find starts a find command.
# ./myfolder tells find to start with the directory ./myfolder and its contents.

# -mindepth 1 not to match ./myfolder itself, just the files and directories under it.

# ! -regex '^./myfolder/test2\(/.*\)?' tells find to exclude (!) any file or directory
# matching the regular expression ^./myfolder/test2\(/.*\)?. ^ matches the start of the
# path name. The expression (/.*\)? matches either (a) a slash followed by anything or (b) nothing at all.

# -delete tells find to delete the matching (that is, non-excluded) files.


git clone https://github.com/marcosolina/covidstatus.git

GIT_FOLDER="./covidstatus"
FOLDER_TO_KEEP="$GIT_FOLDER/Scripts"

# This does not work on Mac
#find $GIT_FOLDER -mindepth 1 ! -regex '^'$FOLDER_TO_KEEP'\(/.*\)?' -delete
#find $GIT_FOLDER -maxdepth 3 -type f -name "*.sh" -delete

rm -rf "$GIT_FOLDER/Misc"
rm -rf "$GIT_FOLDER/src"
rm -rf "$GIT_FOLDER/pom.xml"
rm -rf "$GIT_FOLDER/README.md"
rm -rf "$GIT_FOLDER/Scripts/startStop.sh"
rm -rf "$GIT_FOLDER/Scripts/Docker/downloadAndStartContainers.sh"

docker-compose -f covidstatus/Scripts/Docker/app-and-db-docker-compose.yml up
