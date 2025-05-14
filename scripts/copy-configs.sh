#!/bin/bash

ENV=$1

echo "Copying config files for environment: $ENV"

cp configs/nginx/$ENV.nginx.conf /etc/nginx/nginx.conf
cp configs/tomcat/$ENV.server.xml /opt/tomcat/conf/server.xml
cp configs/opensearch/$ENV.opensearch.yml /etc/opensearch/opensearch.yml

#More configuration based upon the environment needs to be introduced

echo "Configuration files copied successfully!"
