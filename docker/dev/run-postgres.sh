#!/bin/bash

docker run -d \
	-p 5432:5432 \
        --restart=unless-stopped \
        --name=db \
        -e POSTGRES_PASSWORD=p@ssw0rd \
        -e POSTGRES_USERNAME=postgres \
        postgres:10.4