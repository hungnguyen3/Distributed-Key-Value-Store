#!/bin/bash

# Set IP address and number of nodes
max_mem_usage=512
ip_address="54.244.43.4"
numberOfNodes=40

# mvn clean compile assembly:single
truncate -s 0 pid_list.txt

# Launch instances of the Java program with different port numbers
for (( i=0; i<$numberOfNodes; i++ )); do
    port=$((10001+i))
    java -Xmx${max_mem_usage}m -jar A12.jar ${max_mem_usage} ${ip_address} ${port} 310 &
    # Append the PID of the last backgrounded process to the pid list file
    echo $! >> pid_list.txt
done

# Get the PID of the Java program running on port 20000+i and add it to pid_list.txt
for (( i=0; i<=$numberOfNodes; i++ )); do
    port=$((20000+i))
    pid=$(lsof -i :$port -t)
    [[ ! -z "$pid" ]] && echo $pid >> pid_list.txt
done

# Get the PID of the Java program running on port 30000+i and add it to pid_list.txt
for (( i=0; i<=$numberOfNodes; i++ )); do
    port=$((30000+i))
    pid=$(lsof -i :$port -t)
    [[ ! -z "$pid" ]] && echo $pid >> pid_list.txt
done
