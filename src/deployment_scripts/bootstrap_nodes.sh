#!/bin/bash

cd ../..

mvn clean compile assembly:single


for i in {1..20}; do
    java -Xmx64m -jar A7.jar $((10000+i)) 310 &
done