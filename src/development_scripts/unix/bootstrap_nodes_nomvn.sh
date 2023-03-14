#!/bin/bash

cd ../../..
ls

for i in {1..20}; do
    java -Xmx64m -jar target/CPEN431_2023_PROJECT_G6-1.0-SNAPSHOT-jar-with-dependencies.jar 127.0.0.1 $((10000+i)) 310 &
done

cd src/development_scripts/unix