#!/bin/bash

docker run -d \
	--restart=unless-stopped \
	-p 8080:8080 \
	--name esigl \
	--link db:db \
	-v ${PWD}/database-config.properties:/usr/local/tomcat/lib/database-config.properties \
	elogistics/esigl-ci:v18