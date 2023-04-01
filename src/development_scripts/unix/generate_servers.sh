#!/bin/bash

# Set IP address and number of nodes
ip_address="34.212.38.229"
numberOfNodes=40

cd ../../..

# Clear the content of the servers.txt file
truncate -s 0 servers.txt

# Generate server IPs and ports
for (( i=0; i<$numberOfNodes; i++ )); do
    port=$((10001+i))
    echo "${ip_address}:${port}" >> servers.txt
done

cd src/development_scripts/unix
