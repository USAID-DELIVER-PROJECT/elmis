#!/bin/bash

docker run -d \
	--restart=unless-stopped \
	-P 8080:8080 \
	--name esigl \
	--link db:db \
	-v database-config.properties:/usr/local/tomcat/lib/database-config.properties \
	elogistics/esigl-ci:v18