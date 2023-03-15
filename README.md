## Group ID: G6

## Verification Code: 9226502636

## Colour: Yellow

## Used Run Command:

- for single node:

  `java -Xmx64m -jar A7.jar 35.89.101.200 10001 310`

- for 20 nodes (on Mac):

  `for i in {1..20}; do
  java -Xmx64m -jar A7.jar 35.89.101.200 $((10000+i)) 310 &
done`

## Immediate crash honor:

- src/main/java/com/g6/CPEN431/A7/RequestHandlingLayer.java
- line 65

## Description:

- Network Layer: This layer listens for incoming client requests and forwards them to the appropriate node in the cluster based on the key.
- Request Handling Layer: This layer receives client requests from the network layer and performs the appropriate operations (e.g., get, put, delete) on the key-value store.
- Storage Layer: This layer stores the key-value pairs in memory and provides an interface for the request handling layer to perform CRUD operations on them.
  - Along with TransferRequest handles transferring added keys to other nodes on join
- Cache: This layer caches frequently accessed key-value pairs in memory to reduce the number of requests to the storage layer.
- Hash Ring: This layer uses a consistent hashing algorithm to partition the keys across the nodes in the cluster.
- Epidemic: This layer executes the epidemic protocol
