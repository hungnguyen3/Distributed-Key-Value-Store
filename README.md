# Distributed Key-Value Store
### Overview
- Network Layer: This layer listens for incoming client requests and forwards them to the appropriate node in the cluster based on the key.
- Request Handling Layer: This layer receives client requests from the network layer and performs the appropriate operations (e.g., get, put, delete) on the key-value store.
- Storage Layer: This layer stores the key-value pairs in memory and provides an interface for the request handling layer to perform CRUD operations on them.
- Cache: This layer caches frequently accessed key-value pairs in memory to reduce the number of requests to the storage layer.
- Hash Ring: This layer uses a consistent hashing algorithm to partition the keys across the nodes in the cluster.

### Bootstrapping
The current implementation supports bootstrapping the cluster by reading a configuration file that contains the addresses and ports of the nodes in the cluster. The hash ring is constructed based on the number of nodes in the configuration file, and each node is assigned a range of hash values based on its position in the ring.

TODO: on bootstrapping, each node sends requests to all nodes and initialize a local List of isAlive nodes

### Forwarding: 
- Hash ring can hash the key (hash(key) = hashedValue)
- Use this hashedValue to find the node (node contains host and route)

TODO: forward the message to the right node, aka making the request. Handle logic to update isAlive list, forward to the next available node, etc.

### Epidemic:
TODO: fill this section

## Scripts to auto run 25 nodes locally:
These nodes will be run on 127.0.0.1 from port 10001 to 10025

### To run the scripts
- Navigate to src/development_scripts directory.
- Read the README.md file in this directory. Please note that I haven't tested the scripts on Windows, as I use a Mac.

add907