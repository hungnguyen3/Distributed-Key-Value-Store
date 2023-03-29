package com.g6.CPEN431.A7;

import java.util.ArrayList;

public class ReplicationService {
  private int hashRingSize;
  private ArrayList<Node> nodes;
  private int nodeId;
  private Epidemic epidemic;

  public ReplicationService(int hashRingSize, ArrayList<Node> nodes, int nodeId, Epidemic epidemic) {
    this.hashRingSize = hashRingSize;
    this.nodes = nodes;
    this.nodeId = nodeId;
    this.epidemic = epidemic;
  }

  /**
   * Method to get the next replica for this key
   * TODO: review this method
   */
  public Node getNextReplica() {
    int currentNodeID = this.nodeId;
    // TODO: do the wrap around maybe
    for(int i = currentNodeID - 1; i >= currentNodeID - hashRingSize; i--) {
        int predecessorID = (i + hashRingSize) % hashRingSize;
        if (epidemic.isAlive(predecessorID)) {
            return nodes.get(predecessorID);
        }
    }
    return null;
  }
}
