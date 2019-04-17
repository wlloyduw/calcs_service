#!/bin/bash
# CCbench daemon - runs container continually as a task in Amazon ECS to keep ssh server running...
# Exits task and container when /stop file is created by external process
sleep=$1
echo "CCbench daemon up...  sleep_for=$1"
echo "Create or touch /stop file to terminate task"
for (( i=1 ; i <= $sleep; i++ ))
do
  sleep 1
  if [ -f "/stop" ]
  then
    exit
  fi
done
