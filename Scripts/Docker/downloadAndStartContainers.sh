#!/bin/bash

git clone https://github.com/marcosolina/covidstatus.git
docker-compose -f covidstatus/Scripts/Docker/app-and-db-docker-compose.yml up
