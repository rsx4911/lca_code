#!/bin/bash
mvn clean compile
node_modules/gulp/bin/gulp.js build
mvn package