## Group ID: G6

## Verification Code: 9226502636

## Colour: Yellow

## Used Run Command:

- for 80 nodes (on EC2 Ubuntu):

  `for i in {1..80}; do
  java -Xmx512m -jar A9.jar 44.233.222.183 $((10000+i)) 310 &
done`

## Description:

- Network Layer: This layer listens for incoming client requests and forwards them to the appropriate node in the cluster based on the key.
- Request Handling Layer: This layer receives client requests from the network layer and performs the appropriate operations (e.g., get, put, delete) on the key-value store.
- Storage Layer: This layer stores the key-value pairs in memory and provides an interface for the request handling layer to perform CRUD operations on them.
  - TransferRequest: Sub class of Storage Layer handles transferring added keys to other nodes on join
- Cache: This layer caches frequently accessed key-value pairs in memory to reduce the number of requests to the storage layer.
- Hash Ring: This layer uses a consistent hashing algorithm to partition the keys across the nodes in the cluster.
- Epidemic: This layer executes the epidemic protocol
