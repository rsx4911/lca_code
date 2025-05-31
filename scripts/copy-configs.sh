#!/bin/bash

ENV=$1

echo "Copying config files for environment: $ENV"

cp configs/nginx/$ENV.nginx.conf /etc/nginx/sites-available/lca_collab

echo "Configuration files copied successfully!"
