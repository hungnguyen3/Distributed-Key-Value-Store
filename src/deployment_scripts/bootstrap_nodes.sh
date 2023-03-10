#!/bin/bash

# Deploy on Linux machine:
# source start_nodes.sh 127.0.0.1

# Get the IP address to use as a command line argument
ip=$1

cd ../..

mvn clean compile assembly:single

for i in {1..20}; do
    java -Xmx64m -jar target\CPEN431_2023_PROJECT_G6-1.0-SNAPSHOT-jar-with-dependencies.jar "$ip" $((10000+i)) 310 &
done