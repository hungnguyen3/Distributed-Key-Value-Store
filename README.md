## Group ID: G6

## Verification Code: 9226502636

## Colour: Yellow

## Used Run Command:

- for 40 nodes (on EC2 Ubuntu):

  `for i in {1..40}; do
    java -Xmx512m -jar A12.jar 512 54.244.43.4 $((10000+i)) 310 &
done`

## Description:

- Request Handling Layer: This layer receives client requests from the network layer and performs the appropriate operations (e.g., get, put, delete) on the key-value store.
- Storage Layer: This layer stores the key-value pairs in memory and provides an interface for the request handling layer to perform CRUD operations on them.
  - TransferRequest: Sub class of Storage Layer handles transferring added keys to other nodes on join
- Cache: This layer caches frequently accessed key-value pairs in memory to reduce the number of requests to the storage layer.
- Hash Ring: This layer uses a consistent hashing algorithm to partition the keys across the nodes in the cluster.
- Epidemic: This layer executes the epidemic protocol
- HashRingUpdater: Runnable class runs on a separate thread to update the hashRing periodically based on the state of the epidemic protocol
- NetworkListener: Listen to client's requests and do forwarding/chain replicating
- TransferRequest: When a node rejoins, transfer the data from the previously in charge node into the newly rejoined node
- Network Layer: This layer instaniates NetworkListener and HashRingUpdater
