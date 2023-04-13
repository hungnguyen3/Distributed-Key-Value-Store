#!/bin/bash

pid_list='pid_list.txt'
if [ ! -f "$pid_list" ]; then
  echo "pid_list.txt file not found!"
  exit 1
fi

echo "Sending SIGCONT to nodes/pids based on the information provided in pid_list.txt"
while read pid || [ -n "$pid" ]; do
  [[ ! -z "$pid" ]] && kill -CONT $pid && echo "Sent SIGCONT to server process with pid $pid"
done < $pid_list

sleep 1

echo "Killing nodes/pids based on the information provided in pid_list.txt"
echo "Killing both the main java processes and the epidemic processes"
while read pid || [ -n "$pid" ]; do
  [[ ! -z "$pid" ]] && kill -9 $pid && echo "Shut down server process with pid $pid"
done < $pid_list

pkill -f 'java -Xmx512m -jar'