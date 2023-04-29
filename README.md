# Distributed Key Value Store

## About
The project is a distributed system designed to manage a key-value store across a network of 40+ nodes. It uses consistent hashing, chain replication, epidemic protocol, and sequential consistency to achieve efficient and reliable data management.

## Project Structure
The system consists of several layers, each with a specific responsibility:

- Request Handling Layer: receives client requests from the Network Layer and performs appropriate operations on the key-value store.
- Storage Layer: stores the key-value pairs in memory and provides an interface for the Request Handling Layer to perform CRUD operations. It also features a TransferRequest subclass that handles transferring added keys to other nodes upon joining.
- Cache Layer: caches frequently accessed key-value pairs in memory to reduce requests to the Storage Layer.
- Hash Ring Layer: uses consistent hashing to partition keys across nodes in the cluster.
- Epidemic Layer: executes the epidemic protocol to ensure data consistency.
- HashRingUpdater: a runnable class that updates the HashRing periodically based on the state of the Epidemic protocol.
- NetworkListener: listens to client requests, forwards and replicates chains, and manages transfer requests when a node rejoins.
- Network Layer: instantiates the NetworkListener and HashRingUpdater.

Overall, this distributed system provides effective management of a large-scale key-value store while maintaining sequential consistency and data integrity.

## Deployment

Take a look at [deployment_scripts](https://github.com/hungnguyen3/Distributed-Key-Value-Store/tree/ee0d5c131c20739fe76e6cc33b53dd1deb1f44bd/src/deployment_scripts).

Make sure to:
- modify [servers.txt](https://github.com/hungnguyen3/Distributed-Key-Value-Store/blob/ee0d5c131c20739fe76e6cc33b53dd1deb1f44bd/servers.txt) with your own EC2 instance IP address
- modify the [script](https://github.com/hungnguyen3/Distributed-Key-Value-Store/blob/ee0d5c131c20739fe76e6cc33b53dd1deb1f44bd/src/deployment_scripts/bootstrap_nodes.sh#L5) with your EC2 IP address

```bash
      cd src/deployment_scripts
      
      chmod +x bootstrap_nodes.sh   # enable bootstrapping script to start up servers
      source bootstrap_nodes.sh     # start the distributed system
      
      source kill_nodes.sh          # kill the system
```
