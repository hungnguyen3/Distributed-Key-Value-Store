for i in {1..5}; do
    java -Xmx64m -jar A7.jar 127.0.0.1 $((10000+i)) 310 &
done