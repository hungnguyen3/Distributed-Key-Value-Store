for i in {1..20}; do
   fuser -k $((10000+i))/udp
   fuser -k $((20000+i))/udp
   fuser -k $((30000+i))/udp
done