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
}
